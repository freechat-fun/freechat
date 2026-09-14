package fun.freechat.service.chat;

import static org.junit.jupiter.api.Assertions.*;

import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.data.message.SystemMessage;
import fun.freechat.service.common.FileStore;
import fun.freechat.service.common.impl.LocalFileStoreImpl;
import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@Timeout(30)
class SystemPromptSnapshotStoreTest {
    private static final int MAX_BYTES = 1024 * 1024;
    private static final String HOME = "private/messages/snapshots/";
    private static final String CHAT = "private-chat-sentinel";
    private static final SystemMessage MESSAGE = SystemMessage.from("Private policy: café, 中文, 日本語\n\"quoted\" \\ end");

    @TempDir
    Path directory;

    private NonlocalFileStore files;
    private SystemPromptSnapshotStore store;

    @BeforeEach
    void nonlocalStorage() {
        // No Mockito, spring-test, local paths, or actual files are needed by this backend.
        files = new NonlocalFileStore();
        store = new SystemPromptSnapshotStore(files);
    }

    @Test
    void roundTripsExactPlainUtf8JsonThroughNonlocalFileStore() throws Exception {
        String json = ChatMessageSerializer.messageToJson(MESSAGE);
        String reference = store.save(CHAT, MESSAGE);
        assertReference(CHAT, json, reference);
        assertArrayEquals(json.getBytes(StandardCharsets.UTF_8), files.objects.get(path(reference)));
        assertEquals(List.of(HOME + hex(CHAT.getBytes(StandardCharsets.UTF_8))), List.copyOf(files.directories));
        assertEquals(List.of(path(reference)), List.copyOf(files.writes));
        assertEquals(1, files.streamsOpened.get(), "Save must verify readback before returning the reference");
        assertEquals(json.getBytes(StandardCharsets.UTF_8).length, files.bytesRead.get());
        String restored = new SystemPromptSnapshotStore(files).read(CHAT, reference);
        assertEquals(json, restored);
        assertEquals(MESSAGE, ChatMessageDeserializer.messageFromJson(restored));
        assertEquals(0, files.openStreams.get());
    }

    @Test
    void realLocalBackendCreatesDirectoriesAndWritesPlainJsonWithoutDeduplication() throws Exception {
        LocalFileStoreImpl local = localFiles();
        SystemPromptSnapshotStore localStore = new SystemPromptSnapshotStore(local);
        String json = ChatMessageSerializer.messageToJson(MESSAGE);
        String first = localStore.save(CHAT, MESSAGE);
        String second = localStore.save(CHAT, MESSAGE);
        assertReference(CHAT, json, first);
        assertReference(CHAT, json, second);
        assertNotEquals(first, second);
        assertArrayEquals(json.getBytes(StandardCharsets.UTF_8), Files.readAllBytes(directory.resolve(path(first))));
        assertArrayEquals(json.getBytes(StandardCharsets.UTF_8), Files.readAllBytes(directory.resolve(path(second))));
        assertEquals(json, new SystemPromptSnapshotStore(local).read(CHAT, first));
        try (var paths = Files.walk(directory)) {
            assertEquals(2, paths.filter(Files::isRegularFile).count());
        }
    }

    @Test
    void nullSnapshotDoesNotContactStorage() {
        assertNull(store.save(CHAT, null));
        assertTrue(files.objects.isEmpty());
        assertTrue(files.directories.isEmpty());
        assertTrue(files.writes.isEmpty());
        assertEquals(0, files.sizeCalls.get());
        assertEquals(0, files.streamsOpened.get());
    }

    @Test
    void identicalBodiesAlwaysHaveUniqueNamesAndNeverOverwriteReferencedData() throws Exception {
        String json = ChatMessageSerializer.messageToJson(MESSAGE);
        String first = store.save(CHAT, MESSAGE);
        byte[] original = files.objects.get(path(first)).clone();
        Set<String> references = ConcurrentHashMap.newKeySet();
        references.add(first);
        for (int index = 0; index < 5; index++) {
            String next = new SystemPromptSnapshotStore(files).save(CHAT, MESSAGE);
            assertReference(CHAT, json, next);
            assertTrue(references.add(next));
            assertArrayEquals(original, files.objects.get(path(first)));
            assertEquals(json, store.read(CHAT, first));
        }
        assertEquals(6, files.objects.size());
        assertEquals(0, files.overwrites.get());
    }

    @Test
    void changedContentAndDifferentChatHaveDistinctReferences() {
        SystemMessage changed = SystemMessage.from(MESSAGE.text() + " revised");
        String first = store.save(CHAT, MESSAGE);
        String second = store.save(CHAT, changed);
        String otherChat = store.save("another-chat", MESSAGE);
        assertNotEquals(first, second);
        assertNotEquals(first, otherChat);
        assertEquals(ChatMessageSerializer.messageToJson(MESSAGE), store.read(CHAT, first));
        assertEquals(ChatMessageSerializer.messageToJson(changed), store.read(CHAT, second));
        assertEquals(ChatMessageSerializer.messageToJson(MESSAGE), store.read("another-chat", otherChat));
        int reads = files.sizeCalls.get();
        unavailable(() -> store.read(CHAT, otherChat));
        unavailable(() -> store.read("another-chat", first));
        assertEquals(reads, files.sizeCalls.get(), "Reject cross-scope references before accessing storage");
        assertEquals(3, files.objects.size());
        assertEquals(0, files.overwrites.get());
    }

    @Test
    void concurrentSavesOnOverwriteCapableNonlocalStoragePublishOnlyVerifiedUniqueReferences() throws Exception {
        SystemMessage message = SystemMessage.from("并发 snapshot ".repeat(2000));
        String json = ChatMessageSerializer.messageToJson(message);
        SystemPromptSnapshotStore other = new SystemPromptSnapshotStore(files);
        ConcurrentLinkedQueue<String> published = new ConcurrentLinkedQueue<>();
        published.add(store.save(CHAT, message));
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch finished = new CountDownLatch(2);
        try (var executor = Executors.newFixedThreadPool(3)) {
            var first = executor.submit(() -> publishRepeatedly(store, message, published, start, finished));
            var second = executor.submit(() -> publishRepeatedly(other, message, published, start, finished));
            var reader = executor.submit(() -> {
                assertTrue(start.await(5, TimeUnit.SECONDS));
                long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(20);
                do {
                    for (String reference : published) {
                        assertEquals(json, other.read(CHAT, reference));
                    }
                } while (finished.getCount() > 0 && System.nanoTime() < deadline);
                assertEquals(0, finished.getCount(), "Both writers must finish");
                return null;
            });
            start.countDown();
            first.get(25, TimeUnit.SECONDS);
            second.get(25, TimeUnit.SECONDS);
            reader.get(25, TimeUnit.SECONDS);
        }
        assertEquals(41, published.size());
        assertEquals(41, Set.copyOf(published).size());
        assertEquals(41, files.objects.size());
        assertEquals(0, files.overwrites.get());
        assertEquals(0, files.openStreams.get());
        for (String reference : published) {
            assertReference(CHAT, json, reference);
            assertEquals(json, store.read(CHAT, reference));
        }
    }

    @Test
    void inFlightPartialWriteAndItsFailureCannotDamageAnAlreadyReferencedSnapshot() throws Exception {
        CountDownLatch partialWritten = new CountDownLatch(1);
        CountDownLatch failWrite = new CountDownLatch(1);
        AtomicBoolean blockNext = new AtomicBoolean();
        files = new NonlocalFileStore() {
            @Override
            protected void afterPartialWrite() throws IOException {
                if (blockNext.compareAndSet(true, false)) {
                    partialWritten.countDown();
                    try {
                        assertTrue(failWrite.await(5, TimeUnit.SECONDS));
                    } catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                        throw new IOException(interrupted);
                    }
                    throw new IOException("private partial remote upload detail");
                }
            }
        };
        store = new SystemPromptSnapshotStore(files);
        String json = ChatMessageSerializer.messageToJson(MESSAGE);
        String original = store.save(CHAT, MESSAGE);
        blockNext.set(true);
        try (var executor = Executors.newSingleThreadExecutor()) {
            var failed = executor.submit(() -> persistenceFailure(() -> store.save(CHAT, MESSAGE)));
            try {
                assertTrue(partialWritten.await(5, TimeUnit.SECONDS));
                assertFalse(failed.isDone(), "An incomplete write must not publish its reference");
                assertEquals(json, store.read(CHAT, original));
                String concurrent = new SystemPromptSnapshotStore(files).save(CHAT, MESSAGE);
                assertNotEquals(original, concurrent);
                assertEquals(json, store.read(CHAT, concurrent));
                assertEquals(json, store.read(CHAT, original));
            } finally {
                failWrite.countDown();
            }
            failed.get(10, TimeUnit.SECONDS);
        }
        assertEquals(json, store.read(CHAT, original));
        assertEquals(3, files.objects.size(), "Retain even an unreferenced partial upload; do not attempt cleanup");
        assertEquals(0, files.overwrites.get());
    }

    @ParameterizedTest
    @EnumSource(value = WriteFailure.class, names = "NONE", mode = EnumSource.Mode.EXCLUDE)
    void failedWritesCountsAndReadbackFailClosedWithoutDamagingPriorReferences(WriteFailure failure) {
        String original = store.save(CHAT, MESSAGE);
        byte[] bytes = files.objects.get(path(original)).clone();
        files.failure = failure;
        persistenceFailure(() -> store.save(CHAT, MESSAGE));
        assertEquals(2, files.writes.size());
        String attempted = List.copyOf(files.writes).getLast();
        assertNotEquals(path(original), attempted);
        assertEquals(failure == WriteFailure.WRITE_EXCEPTION ? 1 : 2, files.objects.size());
        assertEquals(0, files.openStreams.get());
        files.failure = WriteFailure.NONE;
        assertArrayEquals(bytes, files.objects.get(path(original)));
        assertEquals(ChatMessageSerializer.messageToJson(MESSAGE), store.read(CHAT, original));
        String retry = store.save(CHAT, MESSAGE);
        assertNotEquals(original, retry);
        assertNotEquals(attempted, path(retry));
        assertEquals(0, files.overwrites.get());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(
            strings = {
                "../private-policy",
                "/tmp/private-policy",
                "v1/../../private-policy",
                "v2/../../private-policy",
                "v2\\..\\private-policy",
                "v2/invalid/hash",
                "v1/%2e%2e/hash",
                "v2/./hash",
                " v2/scope/hash",
                "v2/scope/hash\u0000"
            })
    void malformedReferencesAreRejectedWithoutStorageAccessOrPathDisclosure(String reference) {
        unavailable(() -> store.read(CHAT, reference));
        assertEquals(0, files.sizeCalls.get());
        assertEquals(0, files.streamsOpened.get());
        assertTrue(files.objects.isEmpty());
    }

    @Test
    void rejectsMalformedHashVersionSuffixAndScopeBeforeStorageAccess() throws Exception {
        String reference = store.save(CHAT, MESSAGE);
        int digestStart = reference.indexOf('/') + 1;
        int suffixStart = reference.lastIndexOf('-');
        int reads = files.sizeCalls.get();
        int streams = files.streamsOpened.get();
        for (String invalid : List.of(
                reference + ".json",
                reference + "/..",
                "/" + reference,
                "v1/" + hex(CHAT.getBytes(StandardCharsets.UTF_8)) + "/" + hex(utf8(MESSAGE)),
                "v2/" + reference,
                reference.toUpperCase(),
                reference.substring(0, reference.length() - 1),
                reference.substring(0, digestStart) + "+" + reference.substring(digestStart + 1),
                reference.substring(0, suffixStart) + "=" + reference.substring(suffixStart))) {
            unavailable(() -> store.read(CHAT, invalid));
        }
        assertEquals(reads, files.sizeCalls.get());
        assertEquals(streams, files.streamsOpened.get());
        assertEquals(1, files.objects.size());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"})
    void invalidChatIdentifiersAreRejected(String chatId) throws Exception {
        persistenceFailure(() -> store.save(chatId, MESSAGE));
        unavailable(() -> store.read(chatId, referenceFor(CHAT, utf8(MESSAGE))));
        assertTrue(files.directories.isEmpty());
        assertTrue(files.objects.isEmpty());
        assertEquals(0, files.sizeCalls.get());
    }

    @ParameterizedTest
    @ValueSource(strings = {"missing", "corrupt", "truncated", "hash"})
    void damagedPlainSnapshotsFailHashValidationAndAreNeverOverwrittenByAnotherSave(String damage) {
        String reference = store.save(CHAT, MESSAGE);
        byte[] original = files.objects.get(path(reference));
        switch (damage) {
            case "missing" -> files.objects.remove(path(reference));
            case "corrupt" ->
                files.objects.put(
                        path(reference), "private corrupt snapshot sentinel".getBytes(StandardCharsets.UTF_8));
            case "truncated" -> files.objects.put(path(reference), Arrays.copyOf(original, original.length - 1));
            case "hash" -> files.objects.put(path(reference), utf8(SystemMessage.from("Other private policy")));
            default -> fail("Unknown corruption");
        }
        unavailable(() -> store.read(CHAT, reference));
        byte[] damaged = files.objects.get(path(reference));
        String fresh = store.save(CHAT, MESSAGE);
        assertNotEquals(reference, fresh);
        assertArrayEquals(damaged, files.objects.get(path(reference)), "No repair or overwrite of an old reference");
        assertEquals(ChatMessageSerializer.messageToJson(MESSAGE), store.read(CHAT, fresh));
        assertEquals(0, files.overwrites.get());
    }

    @Test
    void jsonLimitIncludesSerializationAndCountsUtf8BytesNotCharacters() {
        int overhead = utf8(SystemMessage.from("x")).length - 1;
        SystemMessage exact = SystemMessage.from("x".repeat(MAX_BYTES - overhead));
        String json = ChatMessageSerializer.messageToJson(exact);
        assertEquals(MAX_BYTES, utf8(exact).length);
        String reference = store.save(CHAT, exact);
        assertEquals(json, store.read(CHAT, reference));
        persistenceFailure(() -> store.save(CHAT, SystemMessage.from(exact.text() + "x")));
        SystemMessage unicode = SystemMessage.from("汉".repeat(MAX_BYTES / 3 + 1));
        assertTrue(unicode.text().length() < MAX_BYTES);
        persistenceFailure(() -> store.save(CHAT, unicode));
        assertEquals(1, files.objects.size());
        assertEquals(1, files.writes.size());
        assertEquals(1, files.directories.size());
    }

    @Test
    void oversizedPlainReadsAreBoundedEvenWhenBackendSizeUnderreports() throws Exception {
        byte[] body = utf8(SystemMessage.from("x".repeat(2 * MAX_BYTES)));
        String reference = referenceFor(CHAT, body);
        files.objects.put(path(reference), body);
        unavailable(() -> store.read(CHAT, reference));
        assertEquals(0, files.streamsOpened.get(), "Reject reported oversize before opening a stream");
        files.reportedSize = 0L;
        unavailable(() -> store.read(CHAT, reference));
        assertEquals(MAX_BYTES + 1, files.bytesRead.get(), "Read at most one byte beyond the limit");
        assertEquals(0, files.openStreams.get());
    }

    @Test
    void saveReadbackIsBoundedEvenWhenBackendSizeUnderreports() {
        files.failure = WriteFailure.OVERSIZED_READBACK;
        files.reportedSize = 0L;
        persistenceFailure(() -> store.save(CHAT, MESSAGE));
        assertEquals(MAX_BYTES + 1, files.bytesRead.get());
        assertEquals(0, files.openStreams.get());
        assertEquals(1, files.objects.size(), "Failed verification does not clean up an ambiguous write");
    }

    @Test
    void directoryAndLocalFilesystemFailuresExposeNeitherPrivateContentPathNorCause() throws Exception {
        SystemPromptSnapshotStore failing = new SystemPromptSnapshotStore(new NonlocalFileStore() {
            @Override
            public void createDirectories(String path) throws IOException {
                throw new IOException("private-policy " + CHAT + " " + MESSAGE.text());
            }
        });
        persistenceFailure(() -> failing.save(CHAT, MESSAGE));
        Files.writeString(directory.resolve("private"), "private path obstruction");
        SystemPromptSnapshotStore local = new SystemPromptSnapshotStore(localFiles());
        persistenceFailure(() -> local.save(CHAT, MESSAGE));
        unavailable(() -> local.read(CHAT, referenceFor(CHAT, utf8(MESSAGE))));
    }

    private static void publishRepeatedly(
            SystemPromptSnapshotStore writer,
            SystemMessage message,
            ConcurrentLinkedQueue<String> published,
            CountDownLatch start,
            CountDownLatch finished) {
        try {
            assertTrue(start.await(5, TimeUnit.SECONDS));
            for (int index = 0; index < 20; index++) {
                published.add(writer.save(CHAT, message));
            }
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new AssertionError(interrupted);
        } finally {
            finished.countDown();
        }
    }

    private LocalFileStoreImpl localFiles() {
        return new LocalFileStoreImpl() {
            @Override
            public Path toPath(String path) {
                return directory.resolve(path);
            }
        };
    }

    private static String path(String reference) {
        return HOME + reference + ".json";
    }

    private static byte[] utf8(SystemMessage message) {
        return ChatMessageSerializer.messageToJson(message).getBytes(StandardCharsets.UTF_8);
    }

    private static String hex(byte[] bytes) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
    }

    private static String referencePrefix(String chat, byte[] bytes) throws Exception {
        return hex(chat.getBytes(StandardCharsets.UTF_8)) + "/"
                + Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(MessageDigest.getInstance("SHA-256").digest(bytes))
                + "-";
    }

    private static String referenceFor(String chat, byte[] bytes) throws Exception {
        return referencePrefix(chat, bytes) + "a".repeat(32);
    }

    private static void assertReference(String chat, String json, String reference) throws Exception {
        assertNotNull(reference);
        assertEquals(141, reference.length());
        assertTrue(reference.matches("[0-9a-f]{64}/[A-Za-z0-9_-]{43}-[0-9a-f]{32}"));
        assertEquals(
                referencePrefix(chat, json.getBytes(StandardCharsets.UTF_8)),
                reference.substring(0, reference.lastIndexOf('-') + 1));
        assertFalse(reference.contains(chat));
    }

    private void unavailable(Executable action) {
        sanitized(action, "System prompt snapshot unavailable");
    }

    private void persistenceFailure(Executable action) {
        sanitized(action, "System prompt snapshot persistence failed");
    }

    private void sanitized(Executable action, String message) {
        IllegalStateException failure = assertThrows(IllegalStateException.class, action);
        assertEquals(message, failure.getMessage());
        assertNull(failure.getCause());
        assertEquals(0, failure.getSuppressed().length);
        StringWriter trace = new StringWriter();
        failure.printStackTrace(new PrintWriter(trace));
        for (String privateValue : List.of(directory.toString(), CHAT, MESSAGE.text(), "private-policy")) {
            assertFalse(trace.toString().contains(privateValue), "Exception must not disclose private details");
        }
    }

    private enum WriteFailure {
        NONE,
        WRITE_EXCEPTION,
        PARTIAL_EXCEPTION,
        AMBIGUOUS_EXCEPTION,
        PARTIAL_COUNT,
        PARTIAL_READBACK,
        SHORT_COUNT,
        EXCESS_COUNT,
        WRONG_READBACK,
        SIZE_EXCEPTION,
        READ_EXCEPTION,
        OVERSIZED_READBACK
    }

    /** An overwrite-capable object store with deliberately non-atomic uploads, never a filesystem adapter. */
    private static class NonlocalFileStore implements FileStore {
        final Map<String, byte[]> objects = new ConcurrentHashMap<>();
        final ConcurrentLinkedQueue<String> directories = new ConcurrentLinkedQueue<>();
        final ConcurrentLinkedQueue<String> writes = new ConcurrentLinkedQueue<>();
        final AtomicInteger overwrites = new AtomicInteger();
        final AtomicInteger sizeCalls = new AtomicInteger();
        final AtomicInteger streamsOpened = new AtomicInteger();
        final AtomicInteger openStreams = new AtomicInteger();
        final AtomicLong bytesRead = new AtomicLong();
        volatile WriteFailure failure = WriteFailure.NONE;
        volatile Long reportedSize;

        @Override
        public Path toPath(String path) {
            throw new AssertionError("Nonlocal FileStore has no filesystem path");
        }

        @Override
        public void createDirectories(String path) throws IOException {
            directories.add(path);
        }

        // Inherit FileStore.write(path, byte[]) to exercise its real default-method delegation.
        @Override
        public long write(String path, byte[] bytes, Instant lastModified) throws IOException {
            writes.add(path);
            if (failure == WriteFailure.WRITE_EXCEPTION) {
                throw new IOException("private-policy remote write failure");
            }
            if (objects.put(path, Arrays.copyOf(bytes, bytes.length / 2)) != null) {
                overwrites.incrementAndGet();
            }
            afterPartialWrite();
            if (failure == WriteFailure.PARTIAL_EXCEPTION) {
                throw new IOException("private-policy partial remote write");
            }
            if (failure == WriteFailure.PARTIAL_COUNT) {
                return bytes.length / 2;
            }
            if (failure == WriteFailure.PARTIAL_READBACK) {
                return bytes.length;
            }
            byte[] stored = bytes.clone();
            if (failure == WriteFailure.WRONG_READBACK) {
                stored[stored.length - 1] ^= 1;
            } else if (failure == WriteFailure.OVERSIZED_READBACK) {
                stored = Arrays.copyOf(bytes, 2 * MAX_BYTES);
            }
            objects.put(path, stored);
            if (failure == WriteFailure.AMBIGUOUS_EXCEPTION) {
                throw new IOException("private-policy lost write acknowledgement");
            }
            return bytes.length
                    + (failure == WriteFailure.SHORT_COUNT ? -1 : failure == WriteFailure.EXCESS_COUNT ? 1 : 0);
        }

        protected void afterPartialWrite() throws IOException {
            Thread.yield();
        }

        @Override
        public long size(String path) throws IOException {
            sizeCalls.incrementAndGet();
            if (failure == WriteFailure.SIZE_EXCEPTION) {
                throw new IOException("private-policy remote metadata failure");
            }
            return reportedSize == null ? object(path).length : reportedSize;
        }

        @Override
        public InputStream newInputStream(String path) throws IOException {
            if (failure == WriteFailure.READ_EXCEPTION) {
                throw new IOException("private-policy remote readback failure");
            }
            byte[] body = object(path);
            streamsOpened.incrementAndGet();
            openStreams.incrementAndGet();
            return new FilterInputStream(new ByteArrayInputStream(body)) {
                private boolean closed;

                @Override
                public int read() throws IOException {
                    int value = in.read();
                    if (value >= 0) {
                        bytesRead.incrementAndGet();
                    }
                    return value;
                }

                @Override
                public int read(byte[] bytes, int offset, int length) throws IOException {
                    int count = in.read(bytes, offset, length);
                    if (count > 0) {
                        bytesRead.addAndGet(count);
                    }
                    return count;
                }

                @Override
                public void close() throws IOException {
                    if (!closed) {
                        closed = true;
                        openStreams.decrementAndGet();
                    }
                    super.close();
                }
            };
        }

        private byte[] object(String path) throws IOException {
            byte[] body = objects.get(path);
            if (body == null) {
                throw new IOException("private-policy missing remote object " + path);
            }
            return body;
        }

        @Override
        public void close() {
            throw new AssertionError("Snapshot store must not close its shared backend");
        }

        @Override
        public List<String> list(String path, String regex, boolean recursive) {
            throw new AssertionError("Snapshot store must not list objects");
        }

        @Override
        public long write(String path, InputStream stream, Long contentLength, Instant lastModified) {
            throw new AssertionError("New snapshots must use the byte-array write overload");
        }

        @Override
        public OutputStream newOutputStream(String path) {
            throw new AssertionError("Snapshot store must not open an output stream");
        }

        @Override
        public byte[] readBytes(String path) {
            throw new AssertionError("Snapshot reads must use a bounded input stream");
        }

        @Override
        public String readString(String path) {
            throw new AssertionError("Snapshot reads must use a bounded input stream");
        }

        @Override
        public boolean exists(String path) {
            throw new AssertionError("Snapshot publication must not use check-then-write");
        }

        @Override
        public void delete(String path) {
            throw new AssertionError("Ambiguous writes must not be cleaned up");
        }

        @Override
        public long getLastModifiedTime(String path) {
            throw new AssertionError("Snapshot store must not inspect timestamps");
        }

        @Override
        public void setLastModifiedTime(String path, long lastModifiedTime) {
            throw new AssertionError("Snapshot store must not alter timestamps");
        }
    }
}
