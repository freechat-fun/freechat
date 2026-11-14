package fun.freechat.util;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static org.apache.commons.compress.archivers.tar.TarArchiveOutputStream.LONGFILE_GNU;
import static org.apache.commons.compress.archivers.tar.TarConstants.LF_DIR;
import static org.apache.commons.compress.archivers.tar.TarConstants.LF_GNUTYPE_LONGNAME;
import static org.apache.commons.compress.archivers.tar.TarConstants.LF_NORMAL;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class TarUtilsTest {
    @TempDir
    Path sandbox;

    @Test
    void roundTripOpensInputsLazilyAndClosesThem() throws IOException {
        byte[] firstData = "角色配置".getBytes(UTF_8);
        byte[] secondData = new byte[10_003];
        new Random(1).nextBytes(secondData);
        String longName = "nested/" + "长名称".repeat(20) + "/content.bin";
        TrackedInput first = new TrackedInput(firstData);
        TrackedInput second = new TrackedInput(secondData);
        TrackedInput empty = new TrackedInput(new byte[0]);
        AtomicBoolean opened = new AtomicBoolean();
        List<TarUtils.Entry> entries = List.of(
                new TarUtils.Entry(
                        "character.json",
                        () -> {
                            assertFalse(opened.getAndSet(true));
                            return first;
                        },
                        firstData.length),
                new TarUtils.Entry(
                        longName,
                        () -> {
                            assertTrue(first.closed);
                            return second;
                        },
                        secondData.length),
                new TarUtils.Entry(
                        "empty",
                        () -> {
                            assertTrue(second.closed);
                            return empty;
                        },
                        0));
        assertFalse(opened.get());
        TrackedOutput out = new TrackedOutput();
        TarUtils.compressGzip(entries, out);
        assertTrue(opened.get());
        assertTrue(empty.closed);
        assertTrue(out.closed);

        TrackedInput archive = new TrackedInput(out.toByteArray());
        Path destination = sandbox.resolve("roundtrip");
        TarUtils.extractGzip(archive, destination);
        assertTrue(archive.closed);
        assertArrayEquals(firstData, Files.readAllBytes(destination.resolve("character.json")));
        assertArrayEquals(secondData, Files.readAllBytes(destination.resolve(longName)));
        assertEquals(0, Files.size(destination.resolve("empty")));
    }

    @Test
    void acceptsEmptyArchiveAndDirectoriesWithinExactBudgets() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        TarUtils.compressGzip(List.of(), out);
        Path empty = sandbox.resolve("empty");
        TarUtils.extractGzip(new ByteArrayInputStream(out.toByteArray()), empty, new TarUtils.Limits(0, 0, 0));
        try (var children = Files.list(empty)) {
            assertEquals(0, children.count());
        }

        Path destination = sandbox.resolve("directories");
        byte[] archive = archive(directory("nested/"), directory("nested/empty/"), file("nested/zero", new byte[0]));
        TarUtils.extractGzip(new ByteArrayInputStream(archive), destination, new TarUtils.Limits(0, 0, 3));
        assertTrue(Files.isDirectory(destination.resolve("nested/empty")));
        assertEquals(0, Files.size(destination.resolve("nested/zero")));
    }

    @Test
    void validatesRecordsAndDefaultLimits() {
        assertThrows(IllegalArgumentException.class, () -> new TarUtils.Entry("a", InputStream::nullInputStream, -1));
        assertThrows(NullPointerException.class, () -> new TarUtils.Entry("a", null, 0));
        assertThrows(IllegalArgumentException.class, () -> new TarUtils.Limits(-1, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new TarUtils.Limits(0, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> new TarUtils.Limits(0, 0, -1));
        assertEquals(new TarUtils.Limits(256L * 1024 * 1024, 1024L * 1024 * 1024, 4096), TarUtils.DEFAULT_LIMITS);
    }

    @ParameterizedTest
    @CsvSource({"0,1", "1,0", "4096,4095", "4096,4097", "9000,8999", "9000,9001"})
    void compressionRejectsLengthMismatchAndClosesInput(int actual, int declared) {
        TrackedInput input = new TrackedInput(new byte[actual]);
        TrackedOutput out = new TrackedOutput();
        AtomicBoolean laterOpened = new AtomicBoolean();
        assertThrows(
                IOException.class,
                () -> TarUtils.compressGzip(
                        List.of(new TarUtils.Entry("mismatch", () -> input, declared), unopenedEntry(laterOpened)),
                        out));
        assertTrue(input.closed);
        assertTrue(out.closed);
        assertFalse(laterOpened.get());
    }

    @Test
    void compressionRejectsNormalizedDuplicatesBeforeOpeningSecondInput() {
        TrackedInput first = new TrackedInput(new byte[0]);
        AtomicBoolean secondOpened = new AtomicBoolean();
        TrackedOutput out = new TrackedOutput();
        assertThrows(
                IOException.class,
                () -> TarUtils.compressGzip(
                        List.of(
                                new TarUtils.Entry("same", () -> first, 0),
                                new TarUtils.Entry(
                                        "./same",
                                        () -> {
                                            secondOpened.set(true);
                                            return InputStream.nullInputStream();
                                        },
                                        0)),
                        out));
        assertTrue(first.closed);
        assertFalse(secondOpened.get());
        assertTrue(out.closed);
    }

    @Test
    void compressionRejectsUnsafeNamesWithoutOpeningInput() {
        for (String name :
                List.of("../outside", "folder/../../outside", "C:/sandbox/file", "folder\\file", "folder/", ".", "")) {
            AtomicBoolean opened = new AtomicBoolean();
            TrackedOutput out = new TrackedOutput();
            assertThrows(
                    IOException.class,
                    () -> TarUtils.compressGzip(
                            List.of(new TarUtils.Entry(
                                    name,
                                    () -> {
                                        opened.set(true);
                                        return InputStream.nullInputStream();
                                    },
                                    0)),
                            out),
                    name);
            assertFalse(opened.get(), name);
            assertTrue(out.closed, name);
        }
    }

    @Test
    void closesStreamsWhenSupplierOrInputFails() {
        TrackedInput first = new TrackedInput(new byte[0]);
        AtomicBoolean laterOpened = new AtomicBoolean();
        TrackedOutput supplierOut = new TrackedOutput();
        assertThrows(
                IOException.class,
                () -> TarUtils.compressGzip(
                        List.of(
                                new TarUtils.Entry("first", () -> first, 0),
                                new TarUtils.Entry(
                                        "failed",
                                        () -> {
                                            throw new IOException("Supplier failure");
                                        },
                                        1),
                                unopenedEntry(laterOpened)),
                        supplierOut));
        assertTrue(first.closed);
        assertTrue(supplierOut.closed);
        assertFalse(laterOpened.get());

        TrackedInput failed = new TrackedInput(new byte[1]) {
            @Override
            public int read(byte[] data, int offset, int length) throws IOException {
                throw new IOException("Input failure");
            }
        };
        TrackedOutput inputOut = new TrackedOutput();
        assertThrows(
                IOException.class,
                () -> TarUtils.compressGzip(
                        List.of(new TarUtils.Entry("failed", () -> failed, 1), unopenedEntry(laterOpened)), inputOut));
        assertTrue(failed.closed);
        assertTrue(inputOut.closed);
        assertFalse(laterOpened.get());
    }

    @Test
    void rejectsNullSupplierResultAndClosesOutput() {
        TrackedOutput out = new TrackedOutput();
        assertThrows(
                IOException.class,
                () -> TarUtils.compressGzip(List.of(new TarUtils.Entry("null", () -> null, 0)), out));
        assertTrue(out.closed);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 128})
    void closesStreamsOnOutputFailureWithoutOpeningLaterInputs(int failAfter) {
        byte[] data = new byte[256 * 1024];
        new Random(2).nextBytes(data);
        TrackedInput input = new TrackedInput(data);
        AtomicBoolean opened = new AtomicBoolean();
        AtomicBoolean laterOpened = new AtomicBoolean();
        FailingOutput out = new FailingOutput(failAfter);
        assertThrows(
                IOException.class,
                () -> TarUtils.compressGzip(
                        List.of(
                                new TarUtils.Entry(
                                        "first",
                                        () -> {
                                            opened.set(true);
                                            return input;
                                        },
                                        data.length),
                                unopenedEntry(laterOpened)),
                        out));
        assertEquals(failAfter != 0, opened.get());
        assertEquals(opened.get(), input.closed);
        assertFalse(laterOpened.get());
        assertTrue(out.closed);
    }

    @Test
    void rejectsEscapingNamesEntirelyInsideSandbox() throws IOException {
        Path outside = Files.createDirectory(sandbox.resolve("outside"));
        Path marker = Files.writeString(outside.resolve("marker"), "untouched");
        String absolute = marker.toAbsolutePath().toString().replace('\\', '/');
        String windows = "C:" + absolute;
        List<String> names = List.of(
                "../outside/marker",
                "nested/../../outside/marker",
                absolute,
                windows,
                windows.replace('/', '\\'),
                absolute.replace('/', '\\'),
                "\\" + absolute.replace('/', '\\'),
                "../outside/new-file");
        int index = 0;
        for (String name : names) {
            Path destination = sandbox.resolve("extract-" + index++);
            assertThrows(
                    IOException.class,
                    () -> TarUtils.extractGzip(
                            new ByteArrayInputStream(archive(file(name, new byte[] {1}))), destination),
                    name);
            assertEquals("untouched", Files.readString(marker));
            assertFalse(Files.exists(outside.resolve("new-file")));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3", "4", "6", "7", "S", "x", "g", "K", "Z"})
    void rejectsLinksAndSpecialEntries(String flag) throws IOException {
        Path outside = Files.writeString(sandbox.resolve("outside"), "untouched");
        TarArchiveEntry entry = new TarArchiveEntry("special", (byte) flag.charAt(0), true);
        entry.setLinkName(outside.toString());
        byte[] header = new byte[512];
        entry.writeEntryHeader(header);
        Path destination = sandbox.resolve("specials");
        assertThrows(
                IOException.class, () -> TarUtils.extractGzip(new ByteArrayInputStream(gzip(header)), destination));
        assertFalse(Files.exists(destination.resolve("special"), NOFOLLOW_LINKS));
        assertEquals("untouched", Files.readString(outside));
    }

    @Test
    void rejectsNonEmptyDirectoryEntry() throws IOException {
        TarArchiveEntry entry = new TarArchiveEntry("directory/", LF_DIR);
        entry.setSize(1);
        byte[] header = new byte[512];
        entry.writeEntryHeader(header);
        Path destination = sandbox.resolve("bad-directory");
        assertThrows(
                IOException.class, () -> TarUtils.extractGzip(new ByteArrayInputStream(gzip(header)), destination));
        assertFalse(Files.exists(destination.resolve("directory")));
    }

    @Test
    void rejectsDuplicateFilesAndDirectoriesWithoutOverwriting() throws IOException {
        Path files = sandbox.resolve("duplicates");
        byte[] archive = archive(file("same", new byte[] {1}), file("./same", new byte[] {2}));
        assertThrows(IOException.class, () -> TarUtils.extractGzip(new ByteArrayInputStream(archive), files));
        assertArrayEquals(new byte[] {1}, Files.readAllBytes(files.resolve("same")));
        byte[] directories = archive(directory("folder/"), directory("./folder/"));
        assertThrows(
                IOException.class,
                () -> TarUtils.extractGzip(new ByteArrayInputStream(directories), sandbox.resolve("duplicate-dirs")));
    }

    @Test
    void rejectsExistingFilesAndFileDirectoryConflicts() throws IOException {
        Path destination = Files.createDirectory(sandbox.resolve("existing"));
        Path existing = Files.writeString(destination.resolve("same"), "untouched");
        byte[] archive = archive(file("same", new byte[] {1}));
        assertThrows(IOException.class, () -> TarUtils.extractGzip(new ByteArrayInputStream(archive), destination));
        assertEquals("untouched", Files.readString(existing));
        byte[] parentFile = archive(file("same/child", new byte[0]));
        assertThrows(IOException.class, () -> TarUtils.extractGzip(new ByteArrayInputStream(parentFile), destination));
        byte[] directoryFile = archive(directory("folder/"), file("folder", new byte[0]));
        assertThrows(
                IOException.class, () -> TarUtils.extractGzip(new ByteArrayInputStream(directoryFile), destination));
        assertTrue(Files.isDirectory(destination.resolve("folder")));
    }

    @Test
    void rejectsExistingSymbolicLinksIncludingDestinationAndDanglingLinks() throws IOException {
        Path outside = Files.createDirectory(sandbox.resolve("outside"));
        Path marker = Files.writeString(outside.resolve("marker"), "untouched");
        Path destination = Files.createDirectory(sandbox.resolve("links"));
        Path link = Files.createSymbolicLink(destination.resolve("link"), outside);
        byte[] parentLink = archive(file("link/new-file", new byte[0]));
        assertThrows(IOException.class, () -> TarUtils.extractGzip(new ByteArrayInputStream(parentLink), destination));
        assertTrue(Files.isSymbolicLink(link));
        assertFalse(Files.exists(outside.resolve("new-file")));
        Files.createSymbolicLink(destination.resolve("file"), marker);
        byte[] fileLink = archive(file("file", new byte[] {1}));
        assertThrows(IOException.class, () -> TarUtils.extractGzip(new ByteArrayInputStream(fileLink), destination));
        assertTrue(Files.isSymbolicLink(destination.resolve("file")));
        assertEquals("untouched", Files.readString(marker));
        Files.createSymbolicLink(destination.resolve("dangling"), outside.resolve("missing"));
        byte[] dangling = archive(file("dangling", new byte[0]));
        assertThrows(IOException.class, () -> TarUtils.extractGzip(new ByteArrayInputStream(dangling), destination));
        assertTrue(Files.isSymbolicLink(destination.resolve("dangling")));
        assertFalse(Files.exists(outside.resolve("missing")));
        Path rootLink = Files.createSymbolicLink(sandbox.resolve("root-link"), outside);
        assertThrows(IOException.class, () -> TarUtils.extractGzip(new ByteArrayInputStream(archive()), rootLink));
    }

    @Test
    void rejectsExistingHardLinkWithoutChangingTarget() throws IOException {
        Path outside = Files.writeString(sandbox.resolve("outside"), "untouched");
        Path destination = Files.createDirectory(sandbox.resolve("hardlinks"));
        Files.createLink(destination.resolve("file"), outside);
        byte[] archive = archive(file("file", new byte[] {1}));
        assertThrows(IOException.class, () -> TarUtils.extractGzip(new ByteArrayInputStream(archive), destination));
        assertEquals("untouched", Files.readString(outside));
        assertTrue(Files.isSameFile(outside, destination.resolve("file")));
    }

    @Test
    void enforcesFileAndTotalByteLimitsBeforeCreatingOversizedFiles() throws IOException {
        Path fileDestination = sandbox.resolve("file-limit");
        byte[] oversized = archive(file("large", new byte[5]));
        assertThrows(
                IOException.class,
                () -> TarUtils.extractGzip(
                        new ByteArrayInputStream(oversized), fileDestination, new TarUtils.Limits(4, 10, 10)));
        assertFalse(Files.exists(fileDestination.resolve("large")));

        Path totalDestination = sandbox.resolve("total-limit");
        byte[] archive = archive(file("first", new byte[3]), file("second", new byte[3]));
        assertThrows(
                IOException.class,
                () -> TarUtils.extractGzip(
                        new ByteArrayInputStream(archive), totalDestination, new TarUtils.Limits(3, 5, 10)));
        assertEquals(3, Files.size(totalDestination.resolve("first")));
        assertFalse(Files.exists(totalDestination.resolve("second")));

        Path exact = sandbox.resolve("exact");
        TarUtils.extractGzip(new ByteArrayInputStream(archive), exact, new TarUtils.Limits(3, 6, 2));
        assertEquals(3, Files.size(exact.resolve("second")));
    }

    @Test
    void rejectsOversizedDefaultDeclarationWithoutReadingPayload() throws IOException {
        TarArchiveEntry entry = new TarArchiveEntry("large");
        entry.setSize(TarUtils.DEFAULT_LIMITS.maxFileBytes() + 1);
        byte[] header = new byte[512];
        entry.writeEntryHeader(header);
        Path destination = sandbox.resolve("default-limit");
        IOException failure = assertThrows(
                IOException.class, () -> TarUtils.extractGzip(new ByteArrayInputStream(gzip(header)), destination));
        assertTrue(failure.getMessage().contains("size limit"));
        assertFalse(Files.exists(destination.resolve("large")));
    }

    @Test
    void countsDirectoriesAndLongNameMetadataTowardsEntryLimit() throws IOException {
        Path destination = sandbox.resolve("count-limit");
        byte[] archive = archive(directory("first/"), file("file", new byte[0]), directory("excess/"));
        assertThrows(
                IOException.class,
                () -> TarUtils.extractGzip(
                        new ByteArrayInputStream(archive), destination, new TarUtils.Limits(0, 0, 2)));
        assertTrue(Files.isDirectory(destination.resolve("first")));
        assertTrue(Files.isRegularFile(destination.resolve("file")));
        assertFalse(Files.exists(destination.resolve("excess")));

        String longName = "a".repeat(101);
        byte[] longNames = archive(file(longName, new byte[0]));
        assertThrows(
                IOException.class,
                () -> TarUtils.extractGzip(
                        new ByteArrayInputStream(longNames),
                        sandbox.resolve("metadata-count"),
                        new TarUtils.Limits(0, 0, 1)));
        TarUtils.extractGzip(
                new ByteArrayInputStream(longNames), sandbox.resolve("metadata-exact"), new TarUtils.Limits(0, 0, 2));
    }

    @Test
    void boundsLongNameMetadataBeforeAllocation() throws IOException {
        TarArchiveEntry entry = new TarArchiveEntry("././@LongLink", LF_GNUTYPE_LONGNAME);
        entry.setSize(64 * 1024 + 1);
        byte[] header = new byte[512];
        entry.writeEntryHeader(header);
        IOException failure = assertThrows(
                IOException.class,
                () -> TarUtils.extractGzip(new ByteArrayInputStream(gzip(header)), sandbox.resolve("metadata")));
        assertTrue(failure.getMessage().contains("metadata limit"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 100, 1511, 1512, 1536, 2047, 2048})
    void rejectsTruncatedTarEvenWithValidGzip(int length) throws IOException {
        byte[] raw = rawArchive(file("data", new byte[1000]));
        TrackedInput input = new TrackedInput(gzip(Arrays.copyOf(raw, length)));
        Path destination = sandbox.resolve("truncated");
        assertThrows(IOException.class, () -> TarUtils.extractGzip(input, destination));
        assertTrue(input.closed);
        if (length == 1511) {
            assertFalse(Files.exists(destination.resolve("data")));
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 8, 12})
    void rejectsTruncatedGzipAndClosesInput(int removed) throws IOException {
        byte[] archive = archive(file("data", new byte[1000]));
        TrackedInput input = new TrackedInput(Arrays.copyOf(archive, archive.length - removed));
        assertThrows(IOException.class, () -> TarUtils.extractGzip(input, sandbox.resolve("gzip-truncated")));
        assertTrue(input.closed);
    }

    @Test
    void rejectsInvalidGzipHeaderAndCorruptChecksums() throws IOException {
        TrackedInput invalid = new TrackedInput(new byte[] {1, 2, 3});
        assertThrows(IOException.class, () -> TarUtils.extractGzip(invalid, sandbox.resolve("invalid")));
        assertTrue(invalid.closed);

        byte[] tar = rawArchive(file("data", new byte[0]));
        tar[0] ^= 1;
        assertThrows(
                IOException.class,
                () -> TarUtils.extractGzip(new ByteArrayInputStream(gzip(tar)), sandbox.resolve("bad-tar-checksum")));
        byte[] archive = archive(file("data", new byte[0]));
        archive[archive.length - 8] ^= 1;
        assertThrows(
                IOException.class,
                () -> TarUtils.extractGzip(new ByteArrayInputStream(archive), sandbox.resolve("bad-gzip-checksum")));
    }

    @Test
    void acceptsBoundedZeroPaddingButRejectsTrailingData() throws IOException {
        byte[] raw = rawArchive();
        byte[] padded = Arrays.copyOf(raw, 10_240);
        TarUtils.extractGzip(new ByteArrayInputStream(gzip(padded)), sandbox.resolve("padded"));
        padded[padded.length - 1] = 1;
        assertThrows(
                IOException.class,
                () -> TarUtils.extractGzip(new ByteArrayInputStream(gzip(padded)), sandbox.resolve("trailing-data")));
        byte[] excessive = Arrays.copyOf(raw, raw.length + 10_241);
        assertThrows(
                IOException.class,
                () -> TarUtils.extractGzip(
                        new ByteArrayInputStream(gzip(excessive)), sandbox.resolve("excessive-padding")));
    }

    private static TarUtils.Entry unopenedEntry(AtomicBoolean opened) {
        return new TarUtils.Entry(
                "later",
                () -> {
                    opened.set(true);
                    return InputStream.nullInputStream();
                },
                0);
    }

    private record Item(TarArchiveEntry entry, byte[] content) {}

    private static Item file(String name, byte[] content) {
        TarArchiveEntry entry = new TarArchiveEntry(name, LF_NORMAL, true);
        entry.setSize(content.length);
        return new Item(entry, content);
    }

    private static Item directory(String name) {
        return new Item(new TarArchiveEntry(name, LF_DIR, true), new byte[0]);
    }

    private static byte[] rawArchive(Item... items) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (TarArchiveOutputStream tar = new TarArchiveOutputStream(out, "UTF-8")) {
            tar.setLongFileMode(LONGFILE_GNU);
            for (Item item : items) {
                tar.putArchiveEntry(item.entry());
                tar.write(item.content());
                tar.closeArchiveEntry();
            }
        }
        return out.toByteArray();
    }

    private static byte[] archive(Item... items) throws IOException {
        return gzip(rawArchive(items));
    }

    private static byte[] gzip(byte[] content) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GzipCompressorOutputStream gzip = new GzipCompressorOutputStream(out)) {
            gzip.write(content);
        }
        return out.toByteArray();
    }

    private static class TrackedInput extends FilterInputStream {
        boolean closed;

        TrackedInput(byte[] content) {
            super(new ByteArrayInputStream(content));
        }

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        }
    }

    private static class TrackedOutput extends ByteArrayOutputStream {
        boolean closed;

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        }
    }

    private static class FailingOutput extends OutputStream {
        private final int failAfter;
        private int written;
        boolean closed;

        FailingOutput(int failAfter) {
            this.failAfter = failAfter;
        }

        @Override
        public void write(int value) throws IOException {
            if (written++ >= failAfter) {
                throw new IOException("Disconnected output");
            }
        }

        @Override
        public void close() {
            closed = true;
        }
    }
}
