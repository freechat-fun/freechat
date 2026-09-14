package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.read.ListAppender;
import com.alibaba.dashscope.common.DashScopeResult;
import com.alibaba.dashscope.common.ResultCallback;
import com.alibaba.dashscope.protocol.HalfDuplexRequest;
import com.alibaba.dashscope.protocol.okhttp.OkHttpHttpClient;
import com.alibaba.dashscope.protocol.okhttp.OkHttpWebSocketClient;
import com.azure.core.implementation.serializer.DefaultJsonSerializer;
import com.azure.core.util.serializer.TypeReference;
import dev.langchain4j.http.client.sse.ServerSentEventListenerUtils;
import dev.langchain4j.internal.RetryUtils;
import dev.langchain4j.service.TokenStream;
import fun.freechat.channels.telegram.TelegramChannel;
import fun.freechat.channels.telegram.TelegramChannelEventBridge;
import fun.freechat.channels.telegram.TelegramChannelEventListener;
import fun.freechat.channels.telegram.TelegramChannelManager;
import fun.freechat.channels.telegram.command.HelpCommand;
import fun.freechat.channels.telegram.command.ResetCommand;
import fun.freechat.channels.telegram.command.StartCommand;
import fun.freechat.channels.telegram.handler.ChatBindingTelegramMessageHandler;
import fun.freechat.channels.telegram.handler.TelegramStreamingReplyEmitter;
import fun.freechat.channels.telegram.handler.TelegramUpdateDispatcher;
import fun.freechat.mapper.CharacterBackendMapper;
import fun.freechat.mapper.ChatContextMapper;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.mapper.ChatMemoryCommitMapper;
import fun.freechat.mapper.ChatMemoryCoordinationMapper;
import fun.freechat.mapper.ChatMemoryStateMapper;
import fun.freechat.model.CharacterBackend;
import fun.freechat.service.character.CharacterBackendEvent;
import fun.freechat.service.chat.ChatService;
import fun.freechat.service.chat.ChatSession;
import fun.freechat.service.chat.ChatSessionService;
import fun.freechat.service.chat.TgChatBindingService;
import fun.freechat.service.chat.TgMessageService;
import fun.freechat.service.chat.memory.LongTermMemoryProperties;
import fun.freechat.service.chat.memory.MemoryLifecycleRepository;
import fun.freechat.service.chat.memory.MemoryPublicationRepository;
import fun.freechat.service.chat.memory.MemoryTurnRepository;
import fun.freechat.service.chat.memory.MemoryWorkRepository;
import fun.freechat.service.common.EncryptionService;
import fun.freechat.service.enums.ChatVar;
import fun.freechat.service.util.EncryptionUtils;
import fun.freechat.service.util.InfoUtils;
import io.reactivex.FlowableEmitter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSourceListener;
import org.apache.ibatis.exceptions.PersistenceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockMakers;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.spring.MyBatisExceptionTranslator;
import org.redisson.api.RLock;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLErrorCodes;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.TelegramUrl;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.ResponseParameters;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.w3c.dom.Element;

/** No network, database, real credentials, Spring application, or production file appenders. */
class MemoryPrivacyLoggingTest {
    private static final String PAYLOAD = "FABRICATED_PRIVATE_MEMORY_CREDENTIAL_SENTINEL";
    private static final String TELEGRAM_BACKEND_TOPIC = "freechat:channels:telegram:backend-changed";
    private final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    private final Map<Logger, SavedLogger> saved = new HashMap<>();
    private final ListAppender<ILoggingEvent> events = new ListAppender<>();

    @BeforeEach
    void captureOnlyInMemoryAtRootDebug() {
        for (Logger logger : context.getLoggerList()) {
            List<Appender<ILoggingEvent>> appenders = new ArrayList<>();
            logger.iteratorForAppenders().forEachRemaining(appenders::add);
            saved.put(logger, new SavedLogger(logger.getLevel(), logger.isAdditive(), appenders));
            appenders.forEach(logger::detachAppender);
            logger.setLevel(Logger.ROOT_LOGGER_NAME.equals(logger.getName()) ? Level.DEBUG : null);
            logger.setAdditive(true);
        }
        context.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.DEBUG);
        events.setContext(context);
        events.start();
        context.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(events);
    }

    @AfterEach
    void restoreLoggingWithoutPrintingCapturedPayloads() {
        for (Logger logger : context.getLoggerList()) {
            List<Appender<ILoggingEvent>> appenders = new ArrayList<>();
            logger.iteratorForAppenders().forEachRemaining(appenders::add);
            appenders.forEach(logger::detachAppender);
            SavedLogger old = saved.get(logger);
            logger.setLevel(old == null ? null : old.level());
            logger.setAdditive(old == null || old.additive());
            if (old != null) {
                old.appenders().forEach(logger::addAppender);
            }
        }
        events.list.clear();
        events.stop();
    }

    enum Repository {
        TURN("inTransaction", "Memory transaction failed", "Rejected", "Memory turn or scope is no longer valid"),
        PUBLICATION(
                "transaction",
                "Memory publication transaction failed",
                "Conflict",
                "Memory publication state changed or lease expired"),
        WORK(
                "transaction",
                "Memory work transaction failed",
                "Rejected",
                "Memory work claim or scope is no longer valid"),
        LIFECYCLE("transaction", "Memory lifecycle transaction failed", null, "Memory lifecycle transaction failed");

        final String method;
        final String failureMessage;
        final String rejectionClass;
        final String rejectionMessage;

        Repository(String method, String failureMessage, String rejectionClass, String rejectionMessage) {
            this.method = method;
            this.failureMessage = failureMessage;
            this.rejectionClass = rejectionClass;
            this.rejectionMessage = rejectionMessage;
        }

        Object create(PlatformTransactionManager manager) {
            var coordination = mock(ChatMemoryCoordinationMapper.class);
            var states = mock(ChatMemoryStateMapper.class);
            var histories = mock(ChatHistoryMapper.class);
            var properties = new LongTermMemoryProperties();
            return switch (this) {
                case TURN ->
                    new MemoryTurnRepository(
                            coordination,
                            states,
                            histories,
                            manager,
                            properties,
                            mock(fun.freechat.service.chat.SystemPromptSnapshotStore.class));
                case PUBLICATION ->
                    new MemoryPublicationRepository(
                            coordination, states, mock(ChatMemoryCommitMapper.class), histories, manager, properties);
                case WORK -> new MemoryWorkRepository(coordination, states, manager, properties);
                case LIFECYCLE ->
                    new MemoryLifecycleRepository(
                            coordination, states, histories, mock(ChatContextMapper.class), manager, properties);
            };
        }

        Object invoke(Object repository, Supplier<?> callback) throws Throwable {
            var helper = repository.getClass().getDeclaredMethod(method, Supplier.class);
            helper.setAccessible(true);
            try {
                return helper.invoke(repository, callback);
            } catch (InvocationTargetException failure) {
                throw failure.getCause();
            }
        }
    }

    enum FailureKind {
        RUNTIME,
        ERROR,
        CHECKED;

        Throwable create() {
            Throwable failure =
                    switch (this) {
                        case RUNTIME -> new IllegalStateException(PAYLOAD, new IOException(PAYLOAD));
                        case ERROR -> new LinkageError(PAYLOAD, new IOException(PAYLOAD));
                        case CHECKED -> new IOException(PAYLOAD, new IOException(PAYLOAD));
                    };
            failure.addSuppressed(new IOException(PAYLOAD));
            return failure;
        }
    }

    static Stream<Arguments> callbackFailures() {
        return Stream.of(Repository.values())
                .flatMap(repository -> Stream.of(FailureKind.values())
                        .flatMap(kind ->
                                Stream.of(false, true).map(rollback -> Arguments.of(repository, kind, rollback))));
    }

    @ParameterizedTest
    @MethodSource("callbackFailures")
    void callbackIsSanitizedBeforeSpringLogsDebugAndRollbackErrors(
            Repository repository, FailureKind kind, boolean rollbackFails) {
        var manager = new TestTransactions();
        if (rollbackFails) {
            manager.rollbackFailure = FailureKind.RUNTIME.create();
        }
        Object target = repository.create(manager);
        Throwable original = kind.create();
        Throwable failure = caught(() -> repository.invoke(target, () -> sneaky(original)));
        assertSanitized(failure, repository.failureMessage, kind == FailureKind.ERROR);
        assertEquals(1, manager.rollbacks);
        assertEquals(0, manager.commits);
        assertTrue(events.list.stream()
                .anyMatch(event -> event.getLevel() == Level.DEBUG
                        && event.getLoggerName().equals(TransactionTemplate.class.getName())));
        if (rollbackFails) {
            assertTrue(events.list.stream()
                    .anyMatch(event -> event.getLevel() == Level.ERROR
                            && event.getLoggerName().equals(TransactionTemplate.class.getName())));
        }
        assertNoPayload();
    }

    static Stream<Arguments> lifecycleFailures() {
        return Stream.of(Repository.values())
                .flatMap(repository -> Stream.of(FailureKind.values())
                        .flatMap(kind -> Stream.of("begin", "commit", "rollback")
                                .map(phase -> Arguments.of(repository, kind, phase))));
    }

    @ParameterizedTest
    @MethodSource("lifecycleFailures")
    void transactionBoundaryDoesNotExposeBeginCommitOrRollbackFailures(
            Repository repository, FailureKind kind, String phase) {
        var manager = new TestTransactions();
        switch (phase) {
            case "begin" -> manager.beginFailure = kind.create();
            case "commit" -> manager.commitFailure = kind.create();
            case "rollback" -> manager.rollbackFailure = kind.create();
            default -> fail("Unexpected transaction phase");
        }
        Object target = repository.create(manager);
        Throwable failure = caught(() -> repository.invoke(target, () -> {
            if (phase.equals("rollback")) {
                return sneaky(FailureKind.RUNTIME.create());
            }
            return "safe";
        }));
        assertSanitized(failure, repository.failureMessage, kind == FailureKind.ERROR);
        assertNoPayload();
    }

    @ParameterizedTest
    @EnumSource(Repository.class)
    void successfulTransactionsKeepRequiresNewAndReadCommitted(Repository repository) throws Throwable {
        var manager = new TestTransactions();
        assertEquals("safe", repository.invoke(repository.create(manager), () -> "safe"));
        assertEquals(TransactionDefinition.PROPAGATION_REQUIRES_NEW, manager.propagation);
        assertEquals(TransactionDefinition.ISOLATION_READ_COMMITTED, manager.isolation);
        assertEquals(1, manager.commits);
        assertEquals(0, manager.rollbacks);
    }

    @ParameterizedTest
    @EnumSource(Repository.class)
    void privateRejectionsKeepOnlyTheirFixedText(Repository repository) throws Exception {
        var manager = new TestTransactions();
        Object target = repository.create(manager);
        Throwable rejection;
        if (repository == Repository.LIFECYCLE) {
            // Lifecycle deliberately uses the exact public type, not a private Rejected subclass.
            rejection = new IllegalStateException(repository.rejectionMessage);
        } else {
            var constructor = Class.forName(target.getClass().getName() + "$" + repository.rejectionClass)
                    .getDeclaredConstructor();
            constructor.setAccessible(true);
            rejection = (Throwable) constructor.newInstance();
        }
        // Even an accidentally attached cause/suppressed payload must not survive the boundary.
        rejection.initCause(new IOException(PAYLOAD));
        rejection.addSuppressed(new IOException(PAYLOAD));
        Throwable failure = caught(() -> repository.invoke(target, () -> sneaky(rejection)));
        assertSanitized(failure, repository.rejectionMessage, false);
        assertTrue(failure.getClass() == rejection.getClass());
        assertEquals(1, manager.rollbacks);
        assertNoPayload();
    }

    enum Sink {
        SQL_TRANSLATION,
        DATASOURCE_CLEANUP,
        JDBC_CLEANUP,
        CONNECTION_RESET,
        CONNECTION_RELEASE,
        RETRY,
        SSE_DECODER,
        AZURE_DECODER,
        DASHSCOPE_SSE,
        DASHSCOPE_CALLBACK,
        DASHSCOPE_WEBSOCKET
    }

    @ParameterizedTest
    @EnumSource(Sink.class)
    void productionLoggerRulesSuppressRealDependencyCallbacks(Sink sink) throws Exception {
        exercise(sink);
        assertTrue(
                events.list.stream().anyMatch(MemoryPrivacyLoggingTest::containsPayload),
                "Control callback must reach a payload-bearing dependency logger");
        events.list.clear();
        applyProductionLoggerRules();
        exercise(sink);
        assertNoPayload();
        context.getLogger("fun.freechat.privacy.UnrelatedOperation").debug("Safe diagnostic remains enabled");
        assertTrue(
                events.list.stream().anyMatch(event -> "Safe diagnostic remains enabled".equals(event.getMessage())));
    }

    @Test
    void exactSuppressionLevelsIncludeExistingMemoryProtections() throws Exception {
        applyProductionLoggerRules();
        for (String name : List.of(
                "org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator",
                "org.springframework.jdbc.datasource.DataSourceTransactionManager",
                "org.springframework.jdbc.support.JdbcTransactionManager",
                "org.springframework.jdbc.datasource.DataSourceUtils",
                "dev.langchain4j.internal.RetryUtils",
                "dev.langchain4j.http.client.sse.ServerSentEventListenerUtils",
                "com.alibaba.dashscope.protocol.okhttp.OkHttpHttpClient",
                "com.alibaba.dashscope.protocol.okhttp.OkHttpWebSocketClient",
                "com.azure.core.implementation.serializer.DefaultJsonSerializer",
                "io.grpc.netty.shaded.io.grpc.netty.NettyClientHandler",
                "fun.freechat.mapper.ChatHistoryMapper",
                "fun.freechat.mapper.ChatMemoryCoordinationMapper",
                "fun.freechat.mapper.ChatContextMapper",
                "fun.freechat.mapper.TgMessageMapper")) {
            assertEquals(Level.OFF, context.getLogger(name).getEffectiveLevel(), name);
        }
        assertEquals(Level.DEBUG, context.getLogger(TransactionTemplate.class).getEffectiveLevel());
        assertEquals(Level.DEBUG, context.getLogger(EncryptionUtils.class).getEffectiveLevel());
    }

    @ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(strings = {"selectHistoryPage", "conversationUsage"})
    void productionRulesSuppressCoordinationMapperResultRows(String statement) throws Exception {
        context.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.TRACE);
        var configuration = new org.apache.ibatis.session.Configuration();
        configuration.setLogImpl(org.apache.ibatis.logging.slf4j.Slf4jImpl.class);
        configuration.addMapper(ChatMemoryStateMapper.class);
        configuration.addMapper(ChatHistoryMapper.class);
        configuration.addMapper(ChatMemoryCoordinationMapper.class);
        var log = configuration
                .getMappedStatement(ChatMemoryCoordinationMapper.class.getName() + "." + statement)
                .getStatementLog();
        var metadata =
                mock(java.sql.ResultSetMetaData.class, withSettings().mockMaker(org.mockito.MockMakers.SUBCLASS));
        when(metadata.getColumnCount()).thenReturn(1);
        when(metadata.getColumnType(1)).thenReturn(java.sql.Types.VARCHAR);
        when(metadata.getColumnLabel(1)).thenReturn("result");
        var rows = mock(java.sql.ResultSet.class, withSettings().mockMaker(org.mockito.MockMakers.SUBCLASS));
        when(rows.getMetaData()).thenReturn(metadata);
        when(rows.getString(1)).thenReturn(PAYLOAD);
        when(rows.next()).thenReturn(true);
        var logged = org.apache.ibatis.logging.jdbc.ResultSetLogger.newInstance(rows, log, 1);
        assertTrue(logged.next());
        assertTrue(
                events.list.stream().anyMatch(MemoryPrivacyLoggingTest::containsPayload),
                "Control must exercise MyBatis result-row logging");
        events.list.clear();
        applyProductionLoggerRules();
        assertTrue(logged.next());
        assertNoPayload();
    }

    @Test
    void malformedLegacyUsageNeverLogsMetadataOrParserExcerpts() {
        assertNull(InfoUtils.deserialize("[\"" + PAYLOAD + "\"]"));
        assertTelegramLog(InfoUtils.class, Level.WARN, "Failed to deserialize token usage");
        assertNoPayload();
    }

    @Test
    void encryptionFailuresLogFixedMessagesAndPreserveLegacyFallbacks() throws Exception {
        var encryption = new EncryptionUtils("0123456789abcdef");
        String invalidCiphertext = Base64.getEncoder().encodeToString(PAYLOAD.getBytes(StandardCharsets.UTF_8));
        assertTrue(invalidCiphertext.equals(encryption.decrypt(invalidCiphertext)));
        Cipher noPadding = Cipher.getInstance("AES/ECB/NoPadding");
        noPadding.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(new byte[16], "AES"));
        var field = EncryptionUtils.class.getDeclaredField("encryptCipher");
        field.setAccessible(true);
        field.set(encryption, noPadding);
        assertTrue(PAYLOAD.equals(encryption.encrypt(PAYLOAD)));
        List<ILoggingEvent> failures = events.list.stream()
                .filter(event -> event.getLoggerName().equals(EncryptionUtils.class.getName()))
                .toList();
        assertEquals(2, failures.size());
        assertTrue(failures.stream()
                .allMatch(event -> event.getThrowableProxy() == null
                        && (event.getArgumentArray() == null || event.getArgumentArray().length == 0)
                        && List.of("Encryption failed", "Decryption failed").contains(event.getFormattedMessage())));
        assertNoPayload();
        assertTrue(
                events.list.stream()
                        .noneMatch(event -> event.getFormattedMessage().contains(invalidCiphertext)),
                "Ciphertext must not appear in logs");
    }

    @Test
    void shippedRulesKeepSpringMvcFromLoggingMemoryDerivedResponseBodies() throws Exception {
        var api = new fun.freechat.api.ChatApi();
        ChatService chats = mock(ChatService.class, withSettings().mockMaker(MockMakers.SUBCLASS));
        var contexts = mock(
                fun.freechat.service.chat.ChatContextService.class,
                withSettings().mockMaker(MockMakers.SUBCLASS));
        org.springframework.test.util.ReflectionTestUtils.setField(api, "chatService", chats);
        org.springframework.test.util.ReflectionTestUtils.setField(api, "chatContextService", contexts);
        when(contexts.get("chat")).thenReturn(new fun.freechat.model.ChatContext().withApiKeyName("synthetic"));
        when(chats.send(eq("chat"), any(), isNull()))
                .thenReturn(org.apache.commons.lang3.tuple.Pair.of(
                        dev.langchain4j.model.chat.response.ChatResponse.builder()
                                .aiMessage(dev.langchain4j.data.message.AiMessage.from(PAYLOAD))
                                .build(),
                        9L));
        var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(api)
                .build();
        var request = org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v2/chat/send/chat")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("{\"role\":\"user\",\"contents\":[{\"type\":\"text\",\"content\":\"hello\"}]}");
        mvc.perform(request)
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status()
                        .isOk());
        assertEquals(
                List.of("org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor"),
                events.list.stream()
                        .filter(MemoryPrivacyLoggingTest::containsPayload)
                        .map(ILoggingEvent::getLoggerName)
                        .distinct()
                        .toList());
        events.list.clear();
        applyProductionLoggerRules();
        var response = mvc.perform(request)
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status()
                        .isOk())
                .andReturn();
        assertTrue(response.getResponse().getContentAsString().contains(PAYLOAD));
        assertNoPayload();
    }

    enum TelegramFailure {
        PARTIAL,
        FINAL,
        EDIT,
        SPLIT_PLACEHOLDER,
        PHOTO,
        TYPING
    }

    @ParameterizedTest
    @EnumSource(TelegramFailure.class)
    void telegramDeliveryFailuresNeverLogReplyBodiesOrCredentials(TelegramFailure phase) throws Exception {
        TelegramChannel channel = mock(TelegramChannel.class, withSettings().mockMaker(MockMakers.SUBCLASS));
        Message placeholder = new Message();
        placeholder.setMessageId(42);
        when(channel.sendText(anyString(), anyLong(), anyString())).thenReturn(placeholder);
        var checked = new TelegramApiException(PAYLOAD, new IOException(PAYLOAD));
        switch (phase) {
            case PARTIAL, FINAL ->
                doThrow(new IllegalStateException(PAYLOAD))
                        .when(channel)
                        .editText(anyString(), anyLong(), anyLong(), anyString(), nullable(String.class));
            case EDIT ->
                doThrow(checked)
                        .when(channel)
                        .editText(anyString(), anyLong(), anyLong(), anyString(), nullable(String.class));
            case SPLIT_PLACEHOLDER ->
                when(channel.sendText(anyString(), anyLong(), anyString()))
                        .thenReturn(placeholder)
                        .thenThrow(checked);
            case PHOTO ->
                when(channel.sendPhoto(anyString(), anyLong(), any(), isNull())).thenThrow(checked);
            case TYPING -> doThrow(checked).when(channel).sendChatAction(anyString(), anyLong(), any());
        }
        var emitter = new TelegramStreamingReplyEmitter(channel, "backend", 7L);
        var deadline = TelegramStreamingReplyEmitter.class.getDeclaredField("nextFlushAt");
        deadline.setAccessible(true);
        var heartbeat = TelegramStreamingReplyEmitter.class.getDeclaredField("typingHeartbeat");
        heartbeat.setAccessible(true);
        emitter.start();
        ScheduledFuture<?> scheduled = (ScheduledFuture<?>) heartbeat.get(emitter);
        try {
            deadline.setLong(emitter, phase == TelegramFailure.FINAL ? Long.MAX_VALUE : 0L);
            emitter.append(
                    switch (phase) {
                        case SPLIT_PLACEHOLDER -> "x".repeat(4001);
                        case PHOTO -> "reply ![img](https://unused.invalid/" + PAYLOAD + ")";
                        default -> PAYLOAD;
                    });
        } finally {
            emitter.complete();
        }
        assertTrue(scheduled.isCancelled());
        assertNull(heartbeat.get(emitter));
        assertTrue(events.list.stream()
                .anyMatch(event -> event.getLoggerName().equals(TelegramStreamingReplyEmitter.class.getName())));
        assertNoPayload();
        if (phase == TelegramFailure.PHOTO) {
            verify(channel).sendPhoto(eq("backend"), eq(7L), any(), isNull());
            assertTrue(emitter.sentImages().isEmpty());
        }
    }

    @Test
    void telegramThrottlingKeepsRetryDelayWithoutLoggingProviderError() throws Exception {
        TelegramChannel channel = mock(TelegramChannel.class, withSettings().mockMaker(MockMakers.SUBCLASS));
        Message placeholder = new Message();
        placeholder.setMessageId(42);
        when(channel.sendText(anyString(), anyLong(), anyString())).thenReturn(placeholder);
        TelegramApiRequestException failure =
                mock(TelegramApiRequestException.class, withSettings().mockMaker(MockMakers.SUBCLASS));
        var parameters = new ResponseParameters();
        parameters.setRetryAfter(60);
        when(failure.getParameters()).thenReturn(parameters);
        when(failure.getMessage()).thenReturn(PAYLOAD);
        doThrow(failure).when(channel).editText(anyString(), anyLong(), anyLong(), anyString(), nullable(String.class));
        var emitter = new TelegramStreamingReplyEmitter(channel, "backend", 7L);
        var deadline = TelegramStreamingReplyEmitter.class.getDeclaredField("nextFlushAt");
        deadline.setAccessible(true);
        emitter.start();
        try {
            deadline.setLong(emitter, 0L);
            long before = System.currentTimeMillis();
            emitter.append(PAYLOAD);
            assertTrue(deadline.getLong(emitter) >= before + 60_000L);
            emitter.append(" more");
            verify(channel, times(1)).editText(anyString(), anyLong(), anyLong(), anyString(), nullable(String.class));
        } finally {
            emitter.complete();
        }
        assertTrue(events.list.stream()
                .anyMatch(event ->
                        event.getLevel() == Level.INFO && event.getMessage().startsWith("Telegram throttled")));
        assertNoPayload();
    }

    enum TelegramSetupFailure {
        PLACEHOLDER,
        ACQUIRE,
        REGISTER,
        START,
        ASYNC_ERROR,
        CLOSE,
        SUCCESS
    }

    @ParameterizedTest
    @EnumSource(TelegramSetupFailure.class)
    void telegramHandlerSanitizesErrorsAndClosesAdmittedStreams(TelegramSetupFailure phase) throws Exception {
        TelegramChannel channel = mock(TelegramChannel.class, withSettings().mockMaker(MockMakers.SUBCLASS));
        ChatService chats = mock(ChatService.class, withSettings().mockMaker(MockMakers.SUBCLASS));
        TgChatBindingService bindings =
                mock(TgChatBindingService.class, withSettings().mockMaker(MockMakers.SUBCLASS));
        TgMessageService messages = mock(TgMessageService.class, withSettings().mockMaker(MockMakers.SUBCLASS));
        when(bindings.getOrCreate(
                        eq("backend"), eq(7L), eq("private"), isNull(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn("chat");
        Message placeholder = new Message();
        placeholder.setMessageId(42);
        when(channel.sendText(anyString(), anyLong(), anyString())).thenReturn(placeholder);
        TokenStream stream = mock(
                TokenStream.class,
                withSettings()
                        .mockMaker(MockMakers.SUBCLASS)
                        .extraInterfaces(AutoCloseable.class)
                        .defaultAnswer(RETURNS_SELF));
        when(chats.streamSend(eq("chat"), any(), isNull())).thenReturn(stream);
        AtomicReference<Consumer<Throwable>> onError = new AtomicReference<>();
        AtomicReference<Consumer<String>> onPartial = new AtomicReference<>();
        AtomicReference<Consumer<dev.langchain4j.model.chat.response.ChatResponse>> onComplete =
                new AtomicReference<>();
        doAnswer(call -> {
                    onError.set(call.getArgument(0));
                    return stream;
                })
                .when(stream)
                .onError(any());
        doAnswer(call -> {
                    onPartial.set(call.getArgument(0));
                    return stream;
                })
                .when(stream)
                .onPartialResponse(any());
        doAnswer(call -> {
                    onComplete.set(call.getArgument(0));
                    return stream;
                })
                .when(stream)
                .onCompleteResponse(any());
        switch (phase) {
            case PLACEHOLDER ->
                when(channel.sendText(anyString(), anyLong(), anyString()))
                        .thenThrow(new TelegramApiException(PAYLOAD));
            case ACQUIRE ->
                when(chats.streamSend(eq("chat"), any(), isNull())).thenThrow(new IllegalStateException(PAYLOAD));
            case REGISTER ->
                doThrow(new IllegalStateException(PAYLOAD)).when(stream).onPartialResponse(any());
            case START, CLOSE ->
                doThrow(new IllegalStateException(PAYLOAD)).when(stream).start();
            case ASYNC_ERROR, SUCCESS -> {}
        }
        if (phase == TelegramSetupFailure.CLOSE) {
            doThrow(new IOException(PAYLOAD)).when((AutoCloseable) stream).close();
        }
        Chat chat = InfoUtils.defaultMapper().readValue("{\"id\":7,\"type\":\"private\"}", Chat.class);
        Message incoming = new Message();
        incoming.setChat(chat);
        incoming.setMessageId(1);
        incoming.setText(PAYLOAD);
        Update update = new Update();
        update.setMessage(incoming);
        var handler = new ChatBindingTelegramMessageHandler(bindings, messages, chats, channel);
        assertDoesNotThrow(() -> handler.handle("backend", update));
        if (phase == TelegramSetupFailure.ASYNC_ERROR) {
            assertNotNull(onError.get());
            assertDoesNotThrow(() -> onError.get().accept(new IllegalStateException(PAYLOAD)));
        } else if (phase == TelegramSetupFailure.SUCCESS) {
            Message photo = new Message();
            photo.setMessageId(43);
            String url = "https://unused.invalid/" + PAYLOAD;
            when(channel.sendPhoto(eq("backend"), eq(7L), any(), isNull())).thenReturn(photo);
            onPartial.get().accept(PAYLOAD + "![img](" + url + ")");
            onComplete
                    .get()
                    .accept(dev.langchain4j.model.chat.response.ChatResponse.builder()
                            .aiMessage(dev.langchain4j.data.message.AiMessage.from(PAYLOAD))
                            .build());
            verify(messages).record("chat", 42L, null, "out", "text", PAYLOAD, null);
            verify(messages).record("chat", 43L, null, "out", "photo", url, null);
            verify(stream).start();
        }
        if (phase == TelegramSetupFailure.REGISTER
                || phase == TelegramSetupFailure.START
                || phase == TelegramSetupFailure.CLOSE) {
            verify((AutoCloseable) stream).close();
        } else {
            verify((AutoCloseable) stream, never()).close();
        }
        if (phase == TelegramSetupFailure.PLACEHOLDER) {
            verify(chats, never()).streamSend(anyString(), any(), any());
        } else {
            verify(channel).sendChatAction("backend", 7L, ActionType.TYPING);
        }
        if (phase != TelegramSetupFailure.SUCCESS) {
            assertTrue(events.list.stream()
                    .anyMatch(
                            event -> event.getLoggerName().equals(ChatBindingTelegramMessageHandler.class.getName())));
        }
        assertNoPayload();
    }

    enum TelegramCommandFailure {
        START_GREETING("start", StartCommand.class, "Failed to resolve character greeting for chat {}"),
        START_SEND("start", StartCommand.class, "/start reply failed for chat {}"),
        RESET_CLEAR("reset", ResetCommand.class, "/reset clearMemory failed for chat {}"),
        RESET_SEND("reset", ResetCommand.class, "/reset reply failed for chat {}"),
        HELP_SEND("help", HelpCommand.class, "/help reply failed for chat {}");

        final String command;
        final Class<?> logger;
        final String logMessage;

        TelegramCommandFailure(String command, Class<?> logger, String logMessage) {
            this.command = command;
            this.logger = logger;
            this.logMessage = logMessage;
        }
    }

    @ParameterizedTest
    @EnumSource(TelegramCommandFailure.class)
    void telegramCommandsKeepFallbacksWithoutLoggingPrivateFailures(TelegramCommandFailure phase) throws Exception {
        var fixture = new TelegramDispatchFixture();
        switch (phase) {
            case START_GREETING -> when(fixture.sessions.get("chat")).thenThrow(FailureKind.RUNTIME.create());
            case START_SEND -> {
                ChatSession session = subclassMock(ChatSession.class);
                when(session.getVariables()).thenReturn(Map.of(ChatVar.CHARACTER_GREETING.text(), PAYLOAD));
                when(fixture.sessions.get("chat")).thenReturn(session);
            }
            case RESET_CLEAR ->
                doThrow(FailureKind.RUNTIME.create()).when(fixture.chats).clearMemory("chat");
            case RESET_SEND, HELP_SEND -> {}
        }
        if (phase == TelegramCommandFailure.START_SEND
                || phase == TelegramCommandFailure.RESET_SEND
                || phase == TelegramCommandFailure.HELP_SEND) {
            when(fixture.channel.sendText(eq("backend"), eq(7L), anyString())).thenThrow(telegramApiFailure());
        }
        Update update = telegramUpdate(1, "/" + phase.command + " " + PAYLOAD);
        assertDoesNotThrow(() -> fixture.dispatcher.dispatch("backend", update));
        ArgumentCaptor<String> reply = ArgumentCaptor.forClass(String.class);
        verify(fixture.channel).sendText(eq("backend"), eq(7L), reply.capture());
        switch (phase) {
            case START_GREETING -> {
                verify(fixture.sessions).get("chat");
                assertTrue("Hi! I'm ready when you are — just send a message.".equals(reply.getValue()));
            }
            case START_SEND -> {
                verify(fixture.sessions).get("chat");
                assertTrue(PAYLOAD.equals(reply.getValue()), "Private greeting must still be delivered, not logged");
            }
            case RESET_CLEAR -> {
                verify(fixture.bindings).findChatId("backend", 7L);
                verify(fixture.chats).clearMemory("chat");
                assertTrue(
                        "Sorry — couldn't reset the conversation just now. Please try again.".equals(reply.getValue()));
            }
            case RESET_SEND -> {
                verify(fixture.bindings).findChatId("backend", 7L);
                verify(fixture.chats).clearMemory("chat");
                assertTrue("Memory cleared. Say something to start fresh.".equals(reply.getValue()));
            }
            case HELP_SEND -> assertTrue(reply.getValue().startsWith("Available commands:\n"));
        }
        verifyNoInteractions(fixture.messages);
        assertTelegramLog(phase.logger, Level.WARN, phase.logMessage);
        assertNoPayload();
    }

    enum TelegramEventFailure {
        TOPIC_LOOKUP,
        PUBLICATION,
        ACTIVATION
    }

    @ParameterizedTest
    @EnumSource(TelegramEventFailure.class)
    void telegramEventCallbacksNeverLogPrivateFailures(TelegramEventFailure phase) {
        RedissonClient redisson = subclassMock(RedissonClient.class);
        RTopic topic = subclassMock(RTopic.class);
        when(redisson.getTopic(TELEGRAM_BACKEND_TOPIC)).thenReturn(topic);
        if (phase == TelegramEventFailure.ACTIVATION) {
            TelegramChannelManager manager = subclassMock(TelegramChannelManager.class);
            AtomicReference<MessageListener<String>> callback = new AtomicReference<>();
            doAnswer(call -> {
                        callback.set(call.getArgument(1));
                        return 1;
                    })
                    .when(topic)
                    .addListener(eq(String.class), any());
            doThrow(FailureKind.RUNTIME.create()).doNothing().when(manager).activate("backend");
            var listener = new TelegramChannelEventListener(manager, redisson);
            ReflectionTestUtils.invokeMethod(listener, "subscribe");
            verify(topic).addListener(eq(String.class), any());
            assertNotNull(callback.get());
            assertDoesNotThrow(() -> callback.get().onMessage(TELEGRAM_BACKEND_TOPIC, null));
            assertDoesNotThrow(() -> callback.get().onMessage(TELEGRAM_BACKEND_TOPIC, "backend"));
            assertDoesNotThrow(() -> callback.get().onMessage(TELEGRAM_BACKEND_TOPIC, "backend"));
            verify(manager, times(2)).activate("backend");
            verifyNoMoreInteractions(manager);
            assertTelegramLog(
                    TelegramChannelEventListener.class, Level.WARN, "Telegram (re)activation failed for backend {}");
        } else {
            if (phase == TelegramEventFailure.TOPIC_LOOKUP) {
                when(redisson.getTopic(TELEGRAM_BACKEND_TOPIC)).thenThrow(FailureKind.RUNTIME.create());
            } else {
                when(topic.publishAsync("backend")).thenThrow(FailureKind.RUNTIME.create());
            }
            var bridge = new TelegramChannelEventBridge(redisson);
            assertDoesNotThrow(() -> bridge.onBackendChanged(new CharacterBackendEvent("user", "backend")));
            verify(redisson).getTopic(TELEGRAM_BACKEND_TOPIC);
            if (phase == TelegramEventFailure.PUBLICATION) {
                verify(topic).publishAsync("backend");
            } else {
                verifyNoInteractions(topic);
            }
            assertTelegramLog(
                    TelegramChannelEventBridge.class,
                    Level.WARN,
                    "Failed to broadcast CharacterBackendEvent for backend {}");
        }
        assertNoPayload();
    }

    enum TelegramPollingEntry {
        ACTIVATE,
        RECONCILE;

        void invoke(TelegramChannelManager manager) {
            if (this == ACTIVATE) {
                manager.activate("backend");
            } else {
                manager.reconcile();
            }
        }
    }

    enum TelegramDispatchFailure {
        BINDING,
        INCOMING_PERSISTENCE,
        START_LOOKUP,
        RESET_LOOKUP
    }

    static Stream<Arguments> telegramDispatchFailures() {
        return Stream.of(TelegramPollingEntry.values())
                .flatMap(entry -> Stream.of(TelegramDispatchFailure.values()).map(phase -> Arguments.of(entry, phase)));
    }

    @ParameterizedTest
    @MethodSource("telegramDispatchFailures")
    void telegramPollingConsumersSanitizeEscapingFailuresAndContinueTheBatch(
            TelegramPollingEntry entry, TelegramDispatchFailure phase) throws Exception {
        var dispatch = new TelegramDispatchFixture();
        switch (phase) {
            case BINDING, START_LOOKUP ->
                when(dispatch.bindings.getOrCreate(
                                eq("backend"), eq(7L), eq("private"), isNull(), isNull(), isNull(), isNull(), isNull()))
                        .thenThrow(FailureKind.RUNTIME.create())
                        .thenReturn("chat");
            case INCOMING_PERSISTENCE ->
                when(dispatch.messages.record("chat", 1L, null, "in", "text", PAYLOAD, null))
                        .thenThrow(FailureKind.RUNTIME.create());
            case RESET_LOOKUP ->
                when(dispatch.bindings.findChatId("backend", 7L)).thenThrow(FailureKind.RUNTIME.create());
        }
        String text =
                switch (phase) {
                    case START_LOOKUP -> "/start " + PAYLOAD;
                    case RESET_LOOKUP -> "/reset " + PAYLOAD;
                    default -> PAYLOAD;
                };
        Update failing = telegramUpdate(1, text);
        // A non-text follow-up reaches real incoming persistence without starting a streaming heartbeat.
        Update following = telegramUpdate(2, null);
        try (var polling = new TelegramManagerFixture(dispatch.dispatcher)) {
            assertDoesNotThrow(() -> entry.invoke(polling.manager));
            ArgumentCaptor<LongPollingUpdateConsumer> consumer =
                    ArgumentCaptor.forClass(LongPollingUpdateConsumer.class);
            verify(polling.application).registerBot(eq(TelegramManagerFixture.TOKEN), any(), any(), consumer.capture());
            assertNotNull(consumer.getValue());
            assertDoesNotThrow(() -> consumer.getValue().consume(List.of(failing, following)));
            verify(dispatch.messages).record("chat", 2L, null, "in", "unsupported", null, null);
            verify(dispatch.bindings, times(phase == TelegramDispatchFailure.RESET_LOOKUP ? 1 : 2))
                    .getOrCreate(
                            eq("backend"), eq(7L), eq("private"), isNull(), isNull(), isNull(), isNull(), isNull());
            if (phase == TelegramDispatchFailure.INCOMING_PERSISTENCE) {
                verify(dispatch.messages).record("chat", 1L, null, "in", "text", PAYLOAD, null);
            } else {
                verify(dispatch.messages, never()).record(eq("chat"), eq(1L), any(), any(), any(), any(), any());
            }
            if (phase == TelegramDispatchFailure.RESET_LOOKUP) {
                verify(dispatch.bindings).findChatId("backend", 7L);
            }
            verifyNoInteractions(dispatch.channel, dispatch.chats, dispatch.sessions, polling.client);
            assertTelegramLog(TelegramChannelManager.class, Level.ERROR, "Dispatch failed for backend {}");
        }
        assertNoPayload();
    }

    enum TelegramManagerFailure {
        STARTUP_LOOKUP("Failed to activate telegram bot for backend {}"),
        ACTIVATE_LOCK("Failed to attempt polling lock for backend {}"),
        RECONCILE_LOCK("Polling lock probe failed for backend {}"),
        ACTIVATE_REGISTER("registerBot failed for backend {}"),
        RECONCILE_REGISTER("Reconcile registerBot failed for backend {}"),
        DEACTIVATE("unregisterBot failed for backend {}"),
        SHUTDOWN_UNREGISTER("unregisterBot at shutdown failed for {}"),
        SHUTDOWN_CLOSE("Closing TelegramBotsLongPollingApplication failed");

        final String logMessage;

        TelegramManagerFailure(String logMessage) {
            this.logMessage = logMessage;
        }
    }

    @ParameterizedTest
    @EnumSource(TelegramManagerFailure.class)
    void telegramLifecycleFailuresKeepSafeDiagnosticsAndCleanup(TelegramManagerFailure phase) throws Exception {
        var dispatch = new TelegramDispatchFixture();
        try (var fixture = new TelegramManagerFixture(dispatch.dispatcher)) {
            switch (phase) {
                case STARTUP_LOOKUP -> {
                    when(fixture.backends.selectMany(any(SelectStatementProvider.class)))
                            .thenReturn(List.of(
                                    new CharacterBackend().withBackendId("backend"),
                                    new CharacterBackend().withBackendId("next-backend")));
                    when(fixture.backends.selectByPrimaryKey("backend")).thenThrow(FailureKind.RUNTIME.create());
                    when(fixture.backends.selectByPrimaryKey("next-backend")).thenReturn(Optional.empty());
                    assertDoesNotThrow(fixture.manager::activateExisting);
                    verify(fixture.backends).selectByPrimaryKey("backend");
                    verify(fixture.backends).selectByPrimaryKey("next-backend");
                    verifyNoInteractions(fixture.application, fixture.lock);
                }
                case ACTIVATE_LOCK, RECONCILE_LOCK -> {
                    when(fixture.lock.tryLock()).thenThrow(FailureKind.RUNTIME.create());
                    TelegramPollingEntry entry = phase == TelegramManagerFailure.ACTIVATE_LOCK
                            ? TelegramPollingEntry.ACTIVATE
                            : TelegramPollingEntry.RECONCILE;
                    assertDoesNotThrow(() -> entry.invoke(fixture.manager));
                    verify(fixture.lock).tryLock();
                    verify(fixture.lock, never()).forceUnlock();
                    verifyNoInteractions(fixture.application);
                    assertSame(fixture.client, fixture.manager.getClient("backend"));
                }
                case ACTIVATE_REGISTER, RECONCILE_REGISTER -> {
                    when(fixture.application.registerBot(eq(TelegramManagerFixture.TOKEN), any(), any(), any()))
                            .thenThrow(telegramApiFailure());
                    TelegramPollingEntry entry = phase == TelegramManagerFailure.ACTIVATE_REGISTER
                            ? TelegramPollingEntry.ACTIVATE
                            : TelegramPollingEntry.RECONCILE;
                    assertDoesNotThrow(() -> entry.invoke(fixture.manager));
                    verify(fixture.application).registerBot(eq(TelegramManagerFixture.TOKEN), any(), any(), any());
                    verify(fixture.lock).tryLock();
                    verify(fixture.lock).forceUnlock();
                    assertSame(fixture.client, fixture.manager.getClient("backend"));
                }
                case DEACTIVATE, SHUTDOWN_UNREGISTER, SHUTDOWN_CLOSE -> {
                    fixture.cacheBot(true);
                    if (phase == TelegramManagerFailure.SHUTDOWN_CLOSE) {
                        doThrow(FailureKind.RUNTIME.create())
                                .when(fixture.application)
                                .close();
                    } else {
                        doThrow(telegramApiFailure())
                                .when(fixture.application)
                                .unregisterBot(TelegramManagerFixture.TOKEN);
                    }
                    if (phase == TelegramManagerFailure.DEACTIVATE) {
                        assertDoesNotThrow(() -> fixture.manager.deactivate("backend"));
                        verify(fixture.application, never()).close();
                    } else {
                        assertDoesNotThrow(fixture.manager::shutdown);
                        verify(fixture.application).close();
                    }
                    verify(fixture.application).unregisterBot(TelegramManagerFixture.TOKEN);
                    verify(fixture.lock).forceUnlock();
                    assertNull(fixture.manager.getClient("backend"));
                    assertTrue(fixture.bots.isEmpty());
                }
            }
            verifyNoInteractions(fixture.client);
            assertTelegramLog(
                    TelegramChannelManager.class,
                    phase == TelegramManagerFailure.RECONCILE_LOCK ? Level.DEBUG : Level.WARN,
                    phase.logMessage);
        }
        assertNoPayload();
    }

    private static <T> T subclassMock(Class<T> type) {
        return mock(type, withSettings().mockMaker(MockMakers.SUBCLASS));
    }

    private static TelegramApiException telegramApiFailure() {
        var failure = new TelegramApiException(PAYLOAD, FailureKind.RUNTIME.create());
        failure.addSuppressed(new IOException(PAYLOAD));
        return failure;
    }

    private static Update telegramUpdate(int messageId, String text) throws IOException {
        Message message = new Message();
        message.setChat(InfoUtils.defaultMapper().readValue("{\"id\":7,\"type\":\"private\"}", Chat.class));
        message.setMessageId(messageId);
        message.setText(text);
        if (text != null && text.startsWith("/")) {
            MessageEntity command = new MessageEntity("bot_command", 0, text.split("\\s+", 2)[0].length());
            message.setEntities(List.of(command));
            assertTrue(message.isCommand(), "Fixture must enter the real command dispatcher");
        }
        Update update = new Update();
        update.setMessage(message);
        return update;
    }

    private void assertTelegramLog(Class<?> logger, Level level, String message) {
        List<ILoggingEvent> matching = events.list.stream()
                .filter(event -> logger.getName().equals(event.getLoggerName())
                        && level.equals(event.getLevel())
                        && message.equals(event.getMessage()))
                .toList();
        assertEquals(1, matching.size(), "The intended catch boundary must log exactly once");
        assertTrue(matching.getFirst().getThrowableProxy() == null, "Exceptions must not be attached to diagnostics");
        Object[] arguments = matching.getFirst().getArgumentArray();
        if (arguments != null) {
            for (Object argument : arguments) {
                assertFalse(argument instanceof Throwable, "Exception arguments must not reach diagnostics");
            }
        }
    }

    private static final class TelegramDispatchFixture {
        final TelegramChannel channel = subclassMock(TelegramChannel.class);
        final TgChatBindingService bindings = subclassMock(TgChatBindingService.class);
        final TgMessageService messages = subclassMock(TgMessageService.class);
        final ChatService chats = subclassMock(ChatService.class);
        final ChatSessionService sessions = subclassMock(ChatSessionService.class);
        final TelegramUpdateDispatcher dispatcher;

        TelegramDispatchFixture() throws TelegramApiException {
            when(bindings.getOrCreate(
                            eq("backend"), eq(7L), eq("private"), isNull(), isNull(), isNull(), isNull(), isNull()))
                    .thenReturn("chat");
            when(bindings.findChatId("backend", 7L)).thenReturn("chat");
            Message placeholder = new Message();
            placeholder.setMessageId(42);
            when(channel.sendText(eq("backend"), eq(7L), anyString())).thenReturn(placeholder);
            dispatcher = new TelegramUpdateDispatcher(
                    List.of(new StartCommand(bindings, sessions), new ResetCommand(bindings, chats), new HelpCommand()),
                    new ChatBindingTelegramMessageHandler(bindings, messages, chats, channel),
                    channel);
        }
    }

    private static final class TelegramManagerFixture implements AutoCloseable {
        static final String TOKEN = "123456:" + PAYLOAD;
        final CharacterBackendMapper backends = subclassMock(CharacterBackendMapper.class);
        final EncryptionService encryption = subclassMock(EncryptionService.class);
        final RedissonClient redisson = subclassMock(RedissonClient.class);
        final RLock lock = subclassMock(RLock.class);
        final TelegramBotsLongPollingApplication application = subclassMock(TelegramBotsLongPollingApplication.class);
        final TelegramClient client = subclassMock(TelegramClient.class);
        final BotSession session = subclassMock(BotSession.class);
        final TelegramChannelManager manager;
        final Map<String, Object> bots;

        @SuppressWarnings("unchecked")
        TelegramManagerFixture(TelegramUpdateDispatcher dispatcher) throws Exception {
            manager = new TelegramChannelManager(TelegramUrl.DEFAULT_URL, backends, encryption, redisson, dispatcher);
            var unused = (TelegramBotsLongPollingApplication) ReflectionTestUtils.getField(manager, "tgApp");
            assertNotNull(unused);
            // The constructor's empty application has no registrations; close it before replacing it.
            unused.close();
            ReflectionTestUtils.setField(manager, "tgApp", application);
            bots = (Map<String, Object>) ReflectionTestUtils.getField(manager, "bots");
            assertNotNull(bots);
            when(backends.selectByPrimaryKey("backend"))
                    .thenReturn(Optional.of(
                            new CharacterBackend().withBackendId("backend").withTgBotToken(TOKEN)));
            when(encryption.decrypt(TOKEN)).thenReturn(TOKEN);
            when(redisson.getLock("freechat:channels:telegram:polling:backend")).thenReturn(lock);
            when(lock.tryLock()).thenReturn(true);
            when(application.registerBot(eq(TOKEN), any(), any(), any())).thenReturn(session);
            cacheBot(false);
        }

        void cacheBot(boolean polling) throws ReflectiveOperationException {
            // Same-token cached outbound state avoids new OkHttpTelegramClient and getMe entirely.
            var constructor = Class.forName(TelegramChannelManager.class.getName() + "$RegisteredBot")
                    .getDeclaredConstructor(String.class, TelegramClient.class, String.class, BotSession.class);
            constructor.setAccessible(true);
            bots.put("backend", constructor.newInstance(TOKEN, client, "safe_bot", polling ? session : null));
        }

        @Override
        public void close() {
            manager.shutdown();
        }
    }

    @SuppressWarnings("unchecked")
    private void exercise(Sink sink) throws Exception {
        switch (sink) {
            case SQL_TRANSLATION -> {
                var codes = new SQLErrorCodes();
                codes.setBadSqlGrammarCodes("1064");
                var translator =
                        new MyBatisExceptionTranslator(() -> new SQLErrorCodeSQLExceptionTranslator(codes), false);
                assertTrue(translator.translateExceptionIfPossible(
                                new PersistenceException(PAYLOAD, new SQLException(PAYLOAD, "42000", 1064)))
                        != null);
            }
            case DATASOURCE_CLEANUP, JDBC_CLEANUP -> {
                Connection connection = mock(Connection.class, withSettings().mockMaker(MockMakers.SUBCLASS));
                DataSource source = mock(DataSource.class, withSettings().mockMaker(MockMakers.SUBCLASS));
                when(source.getConnection()).thenReturn(connection);
                when(connection.getAutoCommit()).thenReturn(true);
                doThrow(new SQLException(PAYLOAD)).when(connection).setAutoCommit(true);
                PlatformTransactionManager manager = sink == Sink.JDBC_CLEANUP
                        ? new JdbcTransactionManager(source)
                        : new DataSourceTransactionManager(source);
                assertEquals("safe", new TransactionTemplate(manager).execute(status -> "safe"));
                verify(connection).commit();
                verify(connection).setAutoCommit(true);
                verify(connection).close();
            }
            case CONNECTION_RESET -> {
                Connection connection = mock(Connection.class, withSettings().mockMaker(MockMakers.SUBCLASS));
                doThrow(new SQLException(PAYLOAD))
                        .when(connection)
                        .setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                DataSourceUtils.resetConnectionAfterTransaction(
                        connection, Connection.TRANSACTION_READ_COMMITTED, false);
                verify(connection).setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            }
            case CONNECTION_RELEASE -> {
                Connection connection = mock(Connection.class, withSettings().mockMaker(MockMakers.SUBCLASS));
                doThrow(new SQLException(PAYLOAD)).when(connection).close();
                DataSourceUtils.releaseConnection(connection, null);
                verify(connection).close();
            }
            case RETRY -> {
                var attempts = new AtomicInteger();
                String result = RetryUtils.retryPolicyBuilder()
                        .maxRetries(2)
                        .delayMillis(0)
                        .jitterScale(0)
                        .build()
                        .withRetry(() -> {
                            if (attempts.incrementAndGet() == 1) {
                                throw new IOException(PAYLOAD);
                            }
                            return "safe";
                        });
                assertEquals("safe", result);
                assertEquals(2, attempts.get());
            }
            case SSE_DECODER ->
                ServerSentEventListenerUtils.ignoringExceptions(() -> {
                    try {
                        InfoUtils.defaultMapper().readTree("{\"content\": " + PAYLOAD + "}");
                    } catch (IOException failure) {
                        sneaky(failure);
                    }
                });
            case AZURE_DECODER -> {
                InputStream brokenBody = new InputStream() {
                    @Override
                    public int read() throws IOException {
                        throw new IOException(PAYLOAD);
                    }
                };
                Throwable failure = caught(() ->
                        new DefaultJsonSerializer().deserialize(brokenBody, TypeReference.createInstance(Map.class)));
                assertTrue(failure instanceof java.io.UncheckedIOException, "Decoder must still report failure");
            }
            case DASHSCOPE_SSE -> {
                var client = new OkHttpHttpClient(new OkHttpClient());
                FlowableEmitter<DashScopeResult> emitter =
                        mock(FlowableEmitter.class, withSettings().mockMaker(MockMakers.SUBCLASS));
                var method = OkHttpHttpClient.class.getDeclaredMethod(
                        "handleSSEEvent",
                        FlowableEmitter.class,
                        String.class,
                        String.class,
                        String.class,
                        boolean.class,
                        Response.class,
                        HalfDuplexRequest.class);
                method.setAccessible(true);
                method.invoke(client, emitter, "safe-id", "error", errorEvent(), false, null, null);
                verify(emitter).onError(any(Throwable.class));
            }
            case DASHSCOPE_CALLBACK -> {
                var client = new OkHttpHttpClient(new OkHttpClient());
                ResultCallback<DashScopeResult> callback =
                        mock(ResultCallback.class, withSettings().mockMaker(MockMakers.SUBCLASS));
                var constructor = Class.forName(OkHttpHttpClient.class.getName() + "$3")
                        .getDeclaredConstructor(OkHttpHttpClient.class, ResultCallback.class, HalfDuplexRequest.class);
                constructor.setAccessible(true);
                var listener = (EventSourceListener) constructor.newInstance(client, callback, null);
                listener.onEvent(null, "safe-id", "error", errorEvent());
                verify(callback).onError(any(Exception.class));
            }
            case DASHSCOPE_WEBSOCKET -> {
                var client = new OkHttpWebSocketClient(new OkHttpClient(), false);
                client.onMessage(
                        null, "{\"header\":{\"event\":\"task-started\"},\"payload\":{},\"extra\":\"" + PAYLOAD + "\"}");
                try (Response response = new Response.Builder()
                        .request(new Request.Builder()
                                .url("https://unused.invalid")
                                .build())
                        .protocol(okhttp3.Protocol.HTTP_1_1)
                        .code(500)
                        .message("synthetic failure")
                        .body(ResponseBody.create(MediaType.parse("application/json"), errorEvent()))
                        .build()) {
                    client.onFailure(null, new IOException(PAYLOAD), response);
                }
            }
        }
    }

    private static String errorEvent() {
        return "{\"code\":\"SyntheticError\",\"message\":\"" + PAYLOAD + "\",\"request_id\":\"safe-id\"}";
    }

    private void applyProductionLoggerRules() throws Exception {
        var builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        var rules = builder.newDocument();
        Element configuration = rules.createElement("configuration");
        rules.appendChild(configuration);
        // Load the shipped logger elements through Logback, not hand-coded levels. Never load its file appenders.
        try (InputStream stream = getClass().getResourceAsStream("/logback-spring.xml")) {
            assertNotNull(stream);
            var production = builder.parse(stream);
            var loggers = production.getElementsByTagName("logger");
            for (int i = 0; i < loggers.getLength(); ++i) {
                configuration.appendChild(rules.importNode(loggers.item(i), false));
            }
        }
        var bytes = new ByteArrayOutputStream();
        TransformerFactory.newInstance().newTransformer().transform(new DOMSource(rules), new StreamResult(bytes));
        var configurator = new JoranConfigurator();
        configurator.setContext(context);
        configurator.doConfigure(new ByteArrayInputStream(bytes.toByteArray()));
    }

    private void assertNoPayload() {
        assertFalse(
                events.list.stream().anyMatch(MemoryPrivacyLoggingTest::containsPayload),
                "Payload found in a log event; deliberately omitted from diagnostics");
    }

    private static boolean containsPayload(ILoggingEvent event) {
        if (contains(event.getMessage())
                || contains(event.getFormattedMessage())
                || containsPayload(event.getThrowableProxy())) {
            return true;
        }
        if (event.getArgumentArray() != null) {
            for (Object argument : event.getArgumentArray()) {
                if (contains(String.valueOf(argument))
                        || argument instanceof Throwable failure && throwableContainsPayload(failure)) {
                    return true;
                }
            }
        }
        return event.getMDCPropertyMap().values().stream().anyMatch(MemoryPrivacyLoggingTest::contains);
    }

    private static boolean containsPayload(IThrowableProxy failure) {
        if (failure == null) {
            return false;
        }
        if (contains(failure.getMessage()) || containsPayload(failure.getCause())) {
            return true;
        }
        if (failure.getSuppressed() != null) {
            for (IThrowableProxy suppressed : failure.getSuppressed()) {
                if (containsPayload(suppressed)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean throwableContainsPayload(Throwable failure) {
        if (contains(failure.getMessage())
                || failure.getCause() != null && throwableContainsPayload(failure.getCause())) {
            return true;
        }
        for (Throwable suppressed : failure.getSuppressed()) {
            if (throwableContainsPayload(suppressed)) {
                return true;
            }
        }
        return false;
    }

    private static boolean contains(String value) {
        return value != null && value.contains(PAYLOAD);
    }

    private static void assertSanitized(Throwable failure, String message, boolean fatal) {
        assertNotNull(failure, "Failure must propagate");
        assertTrue(message.equals(failure.getMessage()), "Failure text must be fixed, not payload-bearing");
        assertTrue(failure.getCause() == null, "Original cause must not escape");
        assertEquals(0, failure.getSuppressed().length);
        assertEquals(fatal, failure instanceof Error);
        assertTrue(fatal || failure instanceof IllegalStateException);
    }

    private static Throwable caught(ThrowingAction action) {
        try {
            action.run();
            return null;
        } catch (Throwable failure) {
            return failure;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T, E extends Throwable> T sneaky(Throwable failure) throws E {
        throw (E) failure;
    }

    @FunctionalInterface
    private interface ThrowingAction {
        void run() throws Throwable;
    }

    private record SavedLogger(Level level, boolean additive, List<Appender<ILoggingEvent>> appenders) {}

    private static final class TestTransactions implements PlatformTransactionManager {
        Throwable beginFailure;
        Throwable commitFailure;
        Throwable rollbackFailure;
        int commits;
        int rollbacks;
        int propagation;
        int isolation;

        @Override
        public TransactionStatus getTransaction(TransactionDefinition definition) {
            propagation = definition.getPropagationBehavior();
            isolation = definition.getIsolationLevel();
            if (beginFailure != null) {
                return sneaky(beginFailure);
            }
            return new SimpleTransactionStatus();
        }

        @Override
        public void commit(TransactionStatus status) {
            ++commits;
            if (commitFailure != null) {
                sneaky(commitFailure);
            }
        }

        @Override
        public void rollback(TransactionStatus status) {
            ++rollbacks;
            if (rollbackFailure != null) {
                sneaky(rollbackFailure);
            }
        }
    }
}
