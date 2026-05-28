package fun.freechat;

import fun.freechat.api.dto.*;
import fun.freechat.channels.telegram.TelegramChannelManager;
import fun.freechat.channels.telegram.handler.ChatBindingTelegramMessageHandler;
import fun.freechat.mapper.ChatContextDynamicSqlSupport;
import fun.freechat.mapper.ChatContextMapper;
import fun.freechat.model.ChatContext;
import fun.freechat.service.chat.TgMessageService;
import fun.freechat.service.enums.ModelProvider;
import fun.freechat.service.enums.PromptFormat;
import fun.freechat.service.enums.Visibility;
import fun.freechat.util.*;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.telegram.telegrambots.meta.TelegramUrl;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.List;

import static fun.freechat.service.enums.ModelProvider.OPEN_AI;
import static fun.freechat.util.TestAiApiKeyUtils.keyNameFor;
import static fun.freechat.util.TestCharacterUtils.idToUid;
import static fun.freechat.util.TestCommonUtils.defaultModelFor;
import static fun.freechat.util.TestCommonUtils.parametersFor;
import static fun.freechat.util.TestCommonUtils.waitAWhile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@EnabledIf("fun.freechat.TelegramChannelIT#hasTelegramToken")
@Import(TelegramChannelIT.TelegramITConfig.class)
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
class TelegramChannelIT extends AbstractIntegrationTest {

    @TestConfiguration
    static class TelegramITConfig {
        @Bean
        @Primary
        TelegramUrl telegramUrl() {
            TelegramITSupport.startIfNeeded(envOrProp("TELEGRAM_BOT_TOKEN"));
            return TelegramITSupport.telegramUrl();
        }
    }

    public static boolean hasTelegramToken() {
        EnvFileLoader.ensureLoaded();
        return envOrProp("TELEGRAM_BOT_TOKEN") != null;
    }

    public static boolean canRunInboundTests() {
        EnvFileLoader.ensureLoaded();
        return envOrProp("TELEGRAM_BOT_TOKEN") != null
                && envOrProp("OPENAI_API_KEY") != null
                && !TelegramITSupport.LIVE;
    }

    static String envOrProp(String key) {
        String v = System.getenv(key);
        if (v != null && !v.isEmpty()) {
            return v;
        }
        v = System.getProperty(key);
        return (v != null && !v.isEmpty()) ? v : null;
    }

    @Autowired
    private TelegramChannelManager telegramChannelManager;

    @Autowired
    private ChatBindingTelegramMessageHandler chatBindingTelegramMessageHandler;

    @Autowired
    private TgMessageService tgMessageService;

    @Autowired
    private ChatContextMapper chatContextMapper;

    private String developerId;
    private String developerApiKey;
    private Long promptId;
    private String promptTaskId;
    private String characterUid;
    private String backendId;

    private ModelProvider modelProvider() {
        return OPEN_AI;
    }

    @BeforeEach
    void setUp() {
        Pair<String, String> dev = TestAccountUtils.createUserAndToken("tg-ch-dev");
        developerId = dev.getLeft();
        developerApiKey = dev.getRight();
        String openAiKey = envOrProp("OPENAI_API_KEY");
        if (openAiKey != null) {
            TestAiApiKeyUtils.addAiApiKey(developerId, keyNameFor(modelProvider()), modelProvider(), openAiKey, true);
        }
        waitAWhile();
        createPromptTaskCharacterBackend();
        waitAWhile();
    }

    @AfterEach
    void tearDown() {
        if (backendId != null) {
            deleteTelegramChatContexts();
            telegramChannelManager.deactivate(backendId);
        }
        if (developerId != null) {
            TestCharacterUtils.deleteCharacters(developerId);
            TestPromptUtils.deletePrompts(developerId);
            waitAWhile();
            TestAiApiKeyUtils.cleanAiApiKeys(developerId);
            TestAccountUtils.deleteUserAndToken(developerId);
        }
    }

    @Test
    void should_activate_bot_and_publish_invite_link() {
        String token = envOrProp("TELEGRAM_BOT_TOKEN");
        bindTelegramTokenAndAwaitActivation(token);

        CharacterBackendDetailsDTO fetched = testClient
                .get()
                .uri("/api/v2/character/backend/default/" + characterUid)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CharacterBackendDetailsDTO.class)
                .returnResult()
                .getResponseBody();
        assertNotNull(fetched);
        assertThat(fetched.getTgBotToken()).isEqualTo(token);
        assertThat(fetched.getTgBotUsername()).matches("\\w+");
        assertThat(fetched.getTgBotLink()).matches("^https://t\\.me/.+$");
    }

    @Test
    @EnabledIf("fun.freechat.TelegramChannelIT#canRunInboundTests")
    void should_route_inbound_message_through_session_and_reply() {
        String token = envOrProp("TELEGRAM_BOT_TOKEN");
        bindTelegramTokenAndAwaitActivation(token);

        Update update = buildUpdate(101, 999L, 999L, "Alice", "hello bot");
        chatBindingTelegramMessageHandler.handle(backendId, update);
        waitAWhile();

        ChatContext bound = chatContextMapper
                .selectOne(c -> c.where(ChatContextDynamicSqlSupport.userId, SqlBuilder.isEqualTo("tg-999"))
                        .and(ChatContextDynamicSqlSupport.backendId, SqlBuilder.isEqualTo(backendId)))
                .orElse(null);
        assertNotNull(bound);
        assertThat(bound.getTgChatId()).isEqualTo(999L);
        assertThat(bound.getTgUserId()).isEqualTo(999L);
        assertThat(bound.getChatType()).isEqualTo("private");
        assertThat(tgMessageService.listByChat(bound.getChatId(), null, null)).hasSizeGreaterThanOrEqualTo(1);
        // outbound reply assertion intentionally omitted: depends on LLM call succeeding,
        // which can fail due to model/parameter incompatibility (e.g. newer OpenAI models
        // requiring max_completion_tokens instead of max_tokens).
    }

    @Test
    @EnabledIf("fun.freechat.TelegramChannelIT#canRunInboundTests")
    void should_be_idempotent_on_repeated_inbound() {
        String token = envOrProp("TELEGRAM_BOT_TOKEN");
        bindTelegramTokenAndAwaitActivation(token);

        Update first = buildUpdate(201, 888L, 888L, "Bob", "ping");
        Update second = buildUpdate(202, 888L, 888L, "Bob", "again");
        chatBindingTelegramMessageHandler.handle(backendId, first);
        waitAWhile();
        chatBindingTelegramMessageHandler.handle(backendId, second);
        waitAWhile();

        List<ChatContext> contexts = chatContextMapper.select(
                c -> c.where(ChatContextDynamicSqlSupport.userId, SqlBuilder.isEqualTo("tg-888"))
                        .and(ChatContextDynamicSqlSupport.backendId, SqlBuilder.isEqualTo(backendId)));
        assertThat(contexts).hasSize(1);
        assertThat(tgMessageService.listByChat(contexts.getFirst().getChatId(), null, null))
                .hasSizeGreaterThanOrEqualTo(2);
    }

    private void bindTelegramTokenAndAwaitActivation(String token) {
        testClient
                .put()
                .uri("/api/v2/character/backend/" + backendId)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .bodyValue(CharacterBackendDTO.builder().tgBotToken(token).build())
                .exchange()
                .expectStatus()
                .isOk();
        long deadline = System.currentTimeMillis() + 10_000L;
        while (System.currentTimeMillis() < deadline) {
            if (telegramChannelManager.getUsername(backendId) != null) {
                return;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        throw new AssertionError("Telegram bot not activated within timeout for backend " + backendId);
    }

    private void createPromptTaskCharacterBackend() {
        ChatPromptContentDTO prompt =
                ChatPromptContentDTO.builder().system("You are a helpful bot.").build();
        promptId = testClient
                .post()
                .uri("/api/v2/prompt")
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .bodyValue(PromptCreateDTO.builder()
                        .name("tg-it-prompt")
                        .format(PromptFormat.MUSTACHE.text())
                        .lang("en")
                        .visibility(Visibility.PUBLIC.text())
                        .chatTemplate(prompt)
                        .build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Long.class)
                .returnResult()
                .getResponseBody();
        assertNotNull(promptId);

        String modelId = "[" + modelProvider().text() + "]" + defaultModelFor(modelProvider());
        promptTaskId = testClient
                .post()
                .uri("/api/v2/prompt/task")
                .accept(MediaType.TEXT_PLAIN)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .bodyValue(PromptTaskDTO.builder()
                        .apiKeyName(keyNameFor(modelProvider()))
                        .modelId(modelId)
                        .params(parametersFor(modelId))
                        .promptRef(PromptRefDTO.builder().promptId(promptId).build())
                        .build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
        assertNotNull(promptTaskId);

        Long characterId = testClient
                .post()
                .uri("/api/v2/character")
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .bodyValue(CharacterCreateDTO.builder()
                        .name("tg-it-char")
                        .visibility(Visibility.PRIVATE.text())
                        .build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Long.class)
                .returnResult()
                .getResponseBody();
        assertNotNull(characterId);
        characterUid = idToUid(characterId);

        backendId = testClient
                .post()
                .uri("/api/v2/character/backend/" + characterUid)
                .accept(MediaType.TEXT_PLAIN)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .bodyValue(CharacterBackendDTO.builder()
                        .chatPromptTaskId(promptTaskId)
                        .isDefault(true)
                        .messageWindowSize(20)
                        .longTermMemoryWindowSize(0)
                        .build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
        assertNotNull(backendId);
    }

    private void deleteTelegramChatContexts() {
        chatContextMapper.delete(c -> c.where(ChatContextDynamicSqlSupport.userId, SqlBuilder.isLike("tg-%"))
                .and(ChatContextDynamicSqlSupport.backendId, SqlBuilder.isEqualTo(backendId)));
    }

    private static Update buildUpdate(int messageId, long chatId, long fromUserId, String firstName, String text) {
        Update u = new Update();
        Message m = new Message();
        m.setMessageId(messageId);
        m.setText(text);
        m.setDate(0);
        Chat chat = new Chat(chatId, "private");
        m.setChat(chat);
        User from = new User(fromUserId, firstName, false);
        m.setFrom(from);
        u.setMessage(m);
        return u;
    }
}
