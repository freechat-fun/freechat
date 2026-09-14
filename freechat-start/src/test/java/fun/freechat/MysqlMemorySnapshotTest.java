package fun.freechat;

import static fun.freechat.service.util.CacheUtils.LONG_PERIOD_CACHE_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.model.ChatHistory;
import fun.freechat.service.chat.SystemPromptSnapshotStore;
import fun.freechat.service.chat.impl.MysqlChatMemoryStoreImpl;
import fun.freechat.service.common.FileStore;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockMakers;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.slf4j.LoggerFactory;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

class MysqlMemorySnapshotTest {
    private final ChatHistoryMapper mapper =
            mock(ChatHistoryMapper.class, withSettings().mockMaker(MockMakers.SUBCLASS));
    private final FileStore snapshotFiles =
            mock(FileStore.class, withSettings().mockMaker(MockMakers.SUBCLASS).defaultAnswer(invocation -> {
                if (invocation.getMethod().isDefault()) {
                    return CALLS_REAL_METHODS.answer(invocation);
                }
                throw new AssertionError("Unexpected nonlocal FileStore operation: "
                        + invocation.getMethod().getName());
            }));
    private final Map<String, byte[]> objects = new HashMap<>();
    private final MysqlChatMemoryStoreImpl store = new MysqlChatMemoryStoreImpl();
    private SystemPromptSnapshotStore snapshots;

    @BeforeEach
    void setup() throws Exception {
        // The byte[] default overload must delegate normally; no operation can obtain a local Path.
        doNothing().when(snapshotFiles).createDirectories(any(String.class));
        doAnswer(invocation -> {
                    byte[] bytes = invocation.getArgument(1);
                    objects.put(invocation.getArgument(0), bytes.clone());
                    return (long) bytes.length;
                })
                .when(snapshotFiles)
                .write(any(String.class), any(byte[].class), any(Instant.class));
        doAnswer(invocation -> (long) object(invocation.getArgument(0)).length)
                .when(snapshotFiles)
                .size(any(String.class));
        doAnswer(invocation -> new ByteArrayInputStream(object(invocation.getArgument(0))))
                .when(snapshotFiles)
                .newInputStream(any(String.class));
        snapshots = new SystemPromptSnapshotStore(snapshotFiles);
        ReflectionTestUtils.setField(store, "snapshots", snapshots);
        ReflectionTestUtils.setField(store, "chatHistoryMapper", mapper);
        ReflectionTestUtils.setField(store, "maxSize", 1000);
        ReflectionTestUtils.setField(store, "cacheManager", new ConcurrentMapCacheManager(LONG_PERIOD_CACHE_NAME));
        ReflectionTestUtils.setField(store, "eventPublisher", mock(ApplicationEventPublisher.class));
        when(mapper.selectMany(any())).thenReturn(List.of());
        doAnswer(invocation -> {
                    invocation.<ChatHistory>getArgument(0).setId(1L);
                    return 1;
                })
                .when(mapper)
                .insertSelective(any(ChatHistory.class));
    }

    @Test
    void plainFileIsVerifiedBeforeSqlReceivesOnlyReferenceAndMessage() throws Exception {
        SystemMessage system = SystemMessage.from("Configured context and learned memory 中文");
        UserMessage user = UserMessage.from("Hello");
        doAnswer(invocation -> {
                    ChatHistory row = invocation.getArgument(0);
                    assertNotNull(row.getSystemMessageRef());
                    assertEquals(141, row.getSystemMessageRef().length());
                    assertTrue(row.getSystemMessageRef().matches("[0-9a-f]{64}/[A-Za-z0-9_-]{43}-[0-9a-f]{32}"));
                    assertArrayEquals(
                            ChatMessageSerializer.messageToJson(system).getBytes(StandardCharsets.UTF_8),
                            object(path(row.getSystemMessageRef())));
                    assertEquals(
                            ChatMessageSerializer.messageToJson(system),
                            snapshots.read("chat", row.getSystemMessageRef()));
                    row.setId(1L);
                    return 1;
                })
                .when(mapper)
                .insertSelective(any(ChatHistory.class));
        store.updateMessages("chat", List.of(system, user));
        ArgumentCaptor<ChatHistory> row = ArgumentCaptor.forClass(ChatHistory.class);
        verify(mapper).insertSelective(row.capture());
        var ordered = inOrder(snapshotFiles, mapper);
        ordered.verify(snapshotFiles).createDirectories(any(String.class));
        ordered.verify(snapshotFiles).write(any(String.class), any(byte[].class), any(Instant.class));
        ordered.verify(snapshotFiles).size(path(row.getValue().getSystemMessageRef()));
        ordered.verify(snapshotFiles).newInputStream(path(row.getValue().getSystemMessageRef()));
        ordered.verify(mapper).insertSelective(any(ChatHistory.class));
        assertEquals(ChatMessageSerializer.messageToJson(user), row.getValue().getMessage());
        when(mapper.selectOne(any(SelectStatementProvider.class))).thenReturn(Optional.of(row.getValue()));
        assertEquals(ChatMessageSerializer.messageToJson(system), store.loadSystemMessage(1L));
    }

    @ParameterizedTest
    @ValueSource(strings = {"exception", "ambiguous", "partial", "count", "readback", "read-exception"})
    void fileFailurePreventsSqlInsertAndLeavesPriorReferenceUntouched(String mode) throws Exception {
        SystemMessage system = SystemMessage.from("private body 中文");
        String prior = snapshots.save("chat", system);
        byte[] original = object(path(prior)).clone();
        doAnswer(invocation -> {
                    String path = invocation.getArgument(0);
                    byte[] bytes = invocation.getArgument(1);
                    assertNotEquals(path(prior), path);
                    if (mode.equals("exception")) {
                        throw new IOException("private backend failure");
                    }
                    byte[] stored = mode.equals("partial") ? Arrays.copyOf(bytes, bytes.length / 2) : bytes.clone();
                    if (mode.equals("readback")) {
                        stored[stored.length - 1] ^= 1;
                    }
                    objects.put(path, stored);
                    if (mode.equals("ambiguous")) {
                        throw new IOException("private lost write acknowledgement");
                    }
                    return (long) (mode.equals("count") ? bytes.length - 1 : bytes.length);
                })
                .when(snapshotFiles)
                .write(any(String.class), any(byte[].class), any(Instant.class));
        if (mode.equals("read-exception")) {
            doAnswer(invocation -> {
                        String path = invocation.getArgument(0);
                        if (!path.equals(path(prior))) {
                            throw new IOException("private readback failure");
                        }
                        return new ByteArrayInputStream(object(path));
                    })
                    .when(snapshotFiles)
                    .newInputStream(any(String.class));
        }
        var failure = assertThrows(
                IllegalStateException.class,
                () -> store.updateMessages("chat", List.of(system, UserMessage.from("Hello"))));
        assertEquals("System prompt snapshot persistence failed", failure.getMessage());
        assertNull(failure.getCause());
        verify(mapper, never()).insertSelective(any());
        assertArrayEquals(original, object(path(prior)));
        assertEquals(ChatMessageSerializer.messageToJson(system), snapshots.read("chat", prior));
        assertEquals(mode.equals("exception") ? 1 : 2, objects.size(), "Failed uploads are retained, not deleted");
    }

    @Test
    void directoryCreationFailurePreventsSqlInsert() throws Exception {
        doThrow(new IOException("private remote directory detail"))
                .when(snapshotFiles)
                .createDirectories(any(String.class));
        assertThrows(
                IllegalStateException.class,
                () -> store.updateMessages(
                        "chat", List.of(SystemMessage.from("private body"), UserMessage.from("Hello"))));
        assertTrue(objects.isEmpty());
        verify(mapper, never()).insertSelective(any());
    }

    @Test
    void ambiguousSqlFailureNeverDeletesPublishedFileAndRetryUsesAFreshReference() {
        SystemMessage system = SystemMessage.from("Snapshot still needed if SQL committed");
        ArgumentCaptor<ChatHistory> row = ArgumentCaptor.forClass(ChatHistory.class);
        doThrow(new IllegalStateException("commit outcome unknown"))
                .when(mapper)
                .insertSelective(any());
        assertThrows(
                IllegalStateException.class,
                () -> store.updateMessages("chat", List.of(system, UserMessage.from("Hello"))));
        verify(mapper).insertSelective(row.capture());
        String uncertainReference = row.getValue().getSystemMessageRef();
        assertEquals(ChatMessageSerializer.messageToJson(system), snapshots.read("chat", uncertainReference));
        doAnswer(invocation -> {
                    invocation.<ChatHistory>getArgument(0).setId(2L);
                    return 1;
                })
                .when(mapper)
                .insertSelective(any(ChatHistory.class));
        store.updateMessages("chat", List.of(system, UserMessage.from("Retry")));
        ArgumentCaptor<ChatHistory> attempts = ArgumentCaptor.forClass(ChatHistory.class);
        verify(mapper, times(2)).insertSelective(attempts.capture());
        String retryReference = attempts.getAllValues().getLast().getSystemMessageRef();
        assertNotEquals(uncertainReference, retryReference);
        assertEquals(ChatMessageSerializer.messageToJson(system), snapshots.read("chat", uncertainReference));
        assertEquals(ChatMessageSerializer.messageToJson(system), snapshots.read("chat", retryReference));
        assertEquals(2, objects.size());
    }

    @Test
    void nullSnapshotDoesNotContactFileStore() {
        store.updateMessages("chat", List.of(UserMessage.from("Hello")));
        ArgumentCaptor<ChatHistory> row = ArgumentCaptor.forClass(ChatHistory.class);
        verify(mapper).insertSelective(row.capture());
        assertNull(row.getValue().getSystemMessageRef());
        assertTrue(objects.isEmpty());
        verifyNoInteractions(snapshotFiles);
    }

    @Test
    void loadsPlainSnapshotSelectingOnlyMemoryIdAndReference() throws Exception {
        SystemMessage message = SystemMessage.from("File snapshot");
        String reference = snapshots.save("chat", message);
        clearInvocations(snapshotFiles);
        when(mapper.selectOne(any(SelectStatementProvider.class)))
                .thenReturn(Optional.of(new ChatHistory().withMemoryId("chat").withSystemMessageRef(reference)));
        assertEquals(ChatMessageSerializer.messageToJson(message), store.loadSystemMessage(1L));
        ArgumentCaptor<SelectStatementProvider> query = ArgumentCaptor.forClass(SelectStatementProvider.class);
        verify(mapper).selectOne(query.capture());
        String statement = query.getValue().getSelectStatement();
        assertEquals("select memory_id, system_message_ref", statement.substring(0, statement.indexOf(" from ")));
        assertEquals(Map.of("p1", 1L), query.getValue().getParameters());
        verify(snapshotFiles).size(path(reference));
        verify(snapshotFiles).newInputStream(path(reference));
        verifyNoMoreInteractions(snapshotFiles);
    }

    @ParameterizedTest
    @ValueSource(strings = {"missing", "corrupt", "cross-chat", "invalid", "v1", "v2"})
    void unavailableReferencesReturnNullWithSanitizedDiagnosticsAndNoWrites(String mode) throws Exception {
        String chatId = "private-chat-sentinel";
        SystemMessage message = SystemMessage.from("Private snapshot body sentinel");
        String reference = snapshots.save(mode.equals("cross-chat") ? "other-private-chat" : chatId, message);
        switch (mode) {
            case "missing" -> objects.remove(path(reference));
            case "corrupt" -> objects.put(path(reference), "private corrupt body".getBytes(StandardCharsets.UTF_8));
            case "invalid" -> reference = "../../private-path-sentinel.json";
            case "v1" -> reference = "v1/" + reference.substring(0, reference.indexOf('/')) + "/" + "b".repeat(64);
            case "v2" -> reference = "v2/" + reference;
            default -> {}
        }
        clearInvocations(snapshotFiles);
        when(mapper.selectOne(any(SelectStatementProvider.class)))
                .thenReturn(Optional.of(new ChatHistory().withMemoryId(chatId).withSystemMessageRef(reference)));
        Logger logger = (Logger) LoggerFactory.getLogger(MysqlChatMemoryStoreImpl.class);
        Level previousLevel = logger.getLevel();
        boolean previousAdditive = logger.isAdditive();
        ListAppender<ILoggingEvent> events = new ListAppender<>();
        events.setContext(logger.getLoggerContext());
        events.start();
        logger.addAppender(events);
        logger.setLevel(Level.WARN);
        logger.setAdditive(false);
        try {
            assertNull(store.loadSystemMessage(1L));
            assertEquals(1, events.list.size());
            ILoggingEvent event = events.list.getFirst();
            assertEquals(Level.WARN, event.getLevel());
            assertNull(event.getThrowableProxy());
            assertTrue(event.getFormattedMessage().contains("snapshot"));
            for (String privateValue : List.of(chatId, message.text(), reference, path(reference), "private")) {
                assertFalse(event.getFormattedMessage().contains(privateValue));
                assertFalse(event.getMessage().contains(privateValue));
                if (event.getArgumentArray() != null) {
                    for (Object argument : event.getArgumentArray()) {
                        assertFalse(argument instanceof Throwable);
                        assertFalse(String.valueOf(argument).contains(privateValue));
                    }
                }
            }
        } finally {
            logger.detachAppender(events);
            logger.setLevel(previousLevel);
            logger.setAdditive(previousAdditive);
            events.stop();
        }
        if (mode.equals("missing") || mode.equals("corrupt")) {
            verify(snapshotFiles).size(path(reference));
            if (mode.equals("corrupt")) {
                verify(snapshotFiles).newInputStream(path(reference));
            }
            verifyNoMoreInteractions(snapshotFiles);
        } else {
            verifyNoInteractions(snapshotFiles);
        }
    }

    @Test
    void missingHistoryNeverReadsOrCreatesFiles() {
        when(mapper.selectOne(any(SelectStatementProvider.class))).thenReturn(Optional.empty());
        assertNull(store.loadSystemMessage(999L));
        verifyNoInteractions(snapshotFiles);
        assertTrue(objects.isEmpty());
    }

    @Test
    void missingReferenceNeverReadsOrCreatesFiles() {
        when(mapper.selectOne(any(SelectStatementProvider.class)))
                .thenReturn(Optional.of(new ChatHistory().withMemoryId("chat")));
        assertNull(store.loadSystemMessage(1L));
        verifyNoInteractions(snapshotFiles);
        assertTrue(objects.isEmpty());
    }

    @Test
    void ordinaryHistoryReadsNeverSelectSnapshotReferences() {
        store.getMessages("chat");
        store.listAllChatMessages("chat");
        store.getLatestChatMessage("chat");
        store.get(1L);
        ArgumentCaptor<SelectStatementProvider> queries = ArgumentCaptor.forClass(SelectStatementProvider.class);
        verify(mapper, atLeast(4)).selectMany(queries.capture());
        for (var query : queries.getAllValues()) {
            assertFalse(query.getSelectStatement().contains("system_message_ref"));
            assertFalse(query.getSelectStatement().contains("source_message"));
            assertFalse(query.getSelectStatement().contains("*"));
        }
    }

    private static String path(String reference) {
        return "private/messages/snapshots/" + reference + ".json";
    }

    private byte[] object(String path) throws IOException {
        byte[] bytes = objects.get(path);
        if (bytes == null) {
            throw new IOException("private object missing: " + path);
        }
        return bytes;
    }
}
