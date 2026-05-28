package fun.freechat.channels.telegram;

import static org.mybatis.dynamic.sql.SqlBuilder.isNotNull;
import static org.mybatis.dynamic.sql.SqlBuilder.select;

import fun.freechat.channels.telegram.handler.TelegramUpdateDispatcher;
import fun.freechat.mapper.CharacterBackendDynamicSqlSupport;
import fun.freechat.mapper.CharacterBackendMapper;
import fun.freechat.model.CharacterBackend;
import fun.freechat.service.character.CharacterService;
import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.util.DefaultGetUpdatesGenerator;
import org.telegram.telegrambots.meta.TelegramUrl;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@Slf4j
public class TelegramChannelManager {

    private static final String INVITE_LINK_TEMPLATE = "https://t.me/%s";

    private final TelegramUrl telegramUrl;
    private final CharacterService characterService;
    private final CharacterBackendMapper characterBackendMapper;
    private final TelegramUpdateDispatcher updateDispatcher;
    private final TelegramBotsLongPollingApplication tgApp = new TelegramBotsLongPollingApplication();
    private final Map<String, RegisteredBot> bots = new ConcurrentHashMap<>();

    public TelegramChannelManager(
            TelegramUrl telegramUrl,
            CharacterService characterService,
            CharacterBackendMapper characterBackendMapper,
            @Lazy TelegramUpdateDispatcher updateDispatcher) {
        this.telegramUrl = telegramUrl;
        this.characterService = characterService;
        this.characterBackendMapper = characterBackendMapper;
        this.updateDispatcher = updateDispatcher;
    }

    @EventListener(ApplicationReadyEvent.class)
    public synchronized void activateExisting() {
        var statement = select(CharacterBackendDynamicSqlSupport.backendId)
                .from(CharacterBackendDynamicSqlSupport.characterBackend)
                .where(CharacterBackendDynamicSqlSupport.tgBotToken, isNotNull())
                .build()
                .render(RenderingStrategies.MYBATIS3);
        List<String> backendIds = characterBackendMapper.selectMany(statement).stream()
                .map(CharacterBackend::getBackendId)
                .toList();
        log.info("Activating {} telegram-bound character backends on startup", backendIds.size());
        for (String bid : backendIds) {
            try {
                activate(bid);
            } catch (Exception e) {
                log.warn("Failed to activate telegram bot for backend {}: {}", bid, e.getMessage());
            }
        }
    }

    public synchronized void activate(String backendId) {
        CharacterBackend backend = characterService.getBackend(backendId);
        if (backend == null || StringUtils.isBlank(backend.getTgBotToken())) {
            deactivate(backendId);
            return;
        }
        String token = backend.getTgBotToken();
        RegisteredBot existing = bots.get(backendId);
        if (existing != null && token.equals(existing.token())) {
            return;
        }
        deactivate(backendId);

        OkHttpTelegramClient client = new OkHttpTelegramClient(token, telegramUrl);
        String username;
        try {
            username = client.execute(new GetMe()).getUserName();
        } catch (TelegramApiException e) {
            log.warn("getMe failed for backend {}: {}", backendId, e.getMessage());
            return;
        }

        LongPollingUpdateConsumer consumer = updates -> updates.forEach(u -> {
            try {
                updateDispatcher.dispatch(backendId, u);
            } catch (Exception e) {
                log.error("Dispatch failed for backend {}", backendId, e);
            }
        });

        BotSession session;
        try {
            session = tgApp.registerBot(token, () -> telegramUrl, new DefaultGetUpdatesGenerator(), consumer);
        } catch (TelegramApiException e) {
            log.warn("registerBot failed for backend {}: {}", backendId, e.getMessage());
            return;
        }

        bots.put(backendId, new RegisteredBot(token, client, username, session));
        log.info("Activated telegram bot @{} for backend {}", username, backendId);
    }

    public synchronized void deactivate(String backendId) {
        RegisteredBot bot = bots.remove(backendId);
        if (bot == null) {
            return;
        }
        try {
            tgApp.unregisterBot(bot.token());
            log.info("Deactivated telegram bot @{} for backend {}", bot.username(), backendId);
        } catch (TelegramApiException e) {
            log.warn("unregisterBot failed for backend {}: {}", backendId, e.getMessage());
        }
    }

    public TelegramClient getClient(String backendId) {
        RegisteredBot bot = bots.get(backendId);
        return bot == null ? null : bot.client();
    }

    public String getUsername(String backendId) {
        RegisteredBot bot = bots.get(backendId);
        return bot == null ? null : bot.username();
    }

    public String getInviteLink(String backendId) {
        String username = getUsername(backendId);
        return username == null ? null : INVITE_LINK_TEMPLATE.formatted(username);
    }

    @PreDestroy
    public synchronized void shutdown() {
        for (Map.Entry<String, RegisteredBot> entry : bots.entrySet()) {
            try {
                tgApp.unregisterBot(entry.getValue().token());
            } catch (Exception e) {
                log.warn("unregisterBot at shutdown failed for {}", entry.getKey(), e);
            }
        }
        bots.clear();
        try {
            tgApp.close();
        } catch (Exception e) {
            log.warn("Closing TelegramBotsLongPollingApplication failed", e);
        }
    }

    private record RegisteredBot(String token, TelegramClient client, String username, BotSession session) {}
}
