package fun.freechat.channels.telegram;

import static org.mybatis.dynamic.sql.SqlBuilder.isNotNull;
import static org.mybatis.dynamic.sql.SqlBuilder.select;

import fun.freechat.channels.telegram.handler.TelegramUpdateDispatcher;
import fun.freechat.mapper.CharacterBackendDynamicSqlSupport;
import fun.freechat.mapper.CharacterBackendMapper;
import fun.freechat.model.CharacterBackend;
import fun.freechat.service.common.EncryptionService;
import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
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

/**
 * Multi-replica safe Telegram bot registry.
 *
 * <p>Outbound state (TelegramClient + cached username) is replicated across every replica so any
 * instance can send messages or render invite links. Inbound long polling is owned by exactly one
 * replica per bot, elected via a Redisson distributed lock per {@code backendId}; non-leaders skip
 * {@link TelegramBotsLongPollingApplication#registerBot} but otherwise behave identically.
 *
 * <p>Leadership transitions: the leader's lock is auto-renewed by Redisson's watchdog while the JVM
 * lives. If the leader dies, the lock auto-expires after the default lease (~30s) and the periodic
 * {@link #reconcile()} task lets any other replica pick polling up.
 */
@Component
@Slf4j
public class TelegramChannelManager {

    private static final String INVITE_LINK_TEMPLATE = "https://t.me/%s";
    private static final String LOCK_PREFIX = "freechat:channels:telegram:polling:";
    private static final long RECONCILE_FIXED_DELAY_MS = 15_000L;

    private final TelegramUrl telegramUrl;
    private final CharacterBackendMapper characterBackendMapper;
    private final EncryptionService encryptionService;
    private final RedissonClient redisson;
    private final TelegramUpdateDispatcher updateDispatcher;
    private final TelegramBotsLongPollingApplication tgApp = new TelegramBotsLongPollingApplication();
    private final Map<String, RegisteredBot> bots = new ConcurrentHashMap<>();

    public TelegramChannelManager(
            TelegramUrl telegramUrl,
            CharacterBackendMapper characterBackendMapper,
            EncryptionService encryptionService,
            RedissonClient redisson,
            @Lazy TelegramUpdateDispatcher updateDispatcher) {
        this.telegramUrl = telegramUrl;
        this.characterBackendMapper = characterBackendMapper;
        this.encryptionService = encryptionService;
        this.redisson = redisson;
        this.updateDispatcher = updateDispatcher;
    }

    private CharacterBackend loadBackendUncached(String backendId) {
        return characterBackendMapper
                .selectByPrimaryKey(backendId)
                .map(b -> b.withTgBotToken(encryptionService.decrypt(b.getTgBotToken())))
                .orElse(null);
    }

    /**
     * Runs asynchronously so it doesn't block {@code ApplicationReadyEvent} dispatch — Spring
     * Boot's {@code ApplicationAvailability} flips the readiness probe to ACCEPTING_TRAFFIC
     * during that same event chain, and a synchronous network-bound scan would keep
     * {@code /actuator/health/readiness} on 503 until every {@code getMe} / {@code registerBot}
     * completed. The {@link #reconcile()} task is the safety net: any activation skipped here
     * (token rotation race, transient getMe failure, etc.) is picked up on the next cycle.
     */
    @EventListener(ApplicationReadyEvent.class)
    @Async
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
        CharacterBackend backend = loadBackendUncached(backendId);
        if (backend == null || StringUtils.isBlank(backend.getTgBotToken())) {
            deactivate(backendId);
            return;
        }
        String token = backend.getTgBotToken();
        RegisteredBot existing = bots.get(backendId);
        boolean tokenChanged = existing != null && !token.equals(existing.token());
        if (tokenChanged) {
            // Token rotated — drop the old binding entirely (release lock + unregister polling).
            deactivate(backendId);
            existing = null;
        }

        // 1. Outbound state — every replica caches this.
        TelegramClient client;
        String username;
        if (existing != null) {
            client = existing.client();
            username = existing.username();
        } else {
            client = new OkHttpTelegramClient(token, telegramUrl);
            try {
                username = ((OkHttpTelegramClient) client).execute(new GetMe()).getUserName();
            } catch (TelegramApiException e) {
                log.warn("getMe failed for backend {}", backendId, e);
                return;
            }
        }

        // 2. Polling leadership — at most one replica registers and polls.
        BotSession session = existing == null ? null : existing.session();
        if (session == null) {
            RLock lock = redisson.getLock(LOCK_PREFIX + backendId);
            boolean acquired;
            try {
                acquired = lock.tryLock();
            } catch (Exception e) {
                log.warn("Failed to attempt polling lock for backend {}: {}", backendId, e.getMessage());
                acquired = false;
            }
            if (acquired) {
                try {
                    LongPollingUpdateConsumer consumer = updates -> updates.forEach(u -> {
                        try {
                            updateDispatcher.dispatch(backendId, u);
                        } catch (Exception ex) {
                            log.error("Dispatch failed for backend {}", backendId, ex);
                        }
                    });
                    session = tgApp.registerBot(token, () -> telegramUrl, new DefaultGetUpdatesGenerator(), consumer);
                } catch (TelegramApiException e) {
                    log.warn("registerBot failed for backend {}: {}", backendId, e.getMessage());
                    safeForceUnlock(lock);
                    session = null;
                }
            }
        }

        bots.put(backendId, new RegisteredBot(token, client, username, session));
        if (existing == null && session != null) {
            log.info("Activated telegram bot @{} for backend {} (POLLING LEADER)", username, backendId);
        } else if (existing == null) {
            log.info(
                    "Activated telegram bot @{} for backend {} (FOLLOWER, polling held by another replica)",
                    username,
                    backendId);
        } else if (session != null && existing.session() == null) {
            log.info("Took over polling for telegram bot @{} on backend {}", username, backendId);
        }
    }

    public synchronized void deactivate(String backendId) {
        RegisteredBot bot = bots.remove(backendId);
        if (bot == null) {
            return;
        }
        if (bot.session() != null) {
            try {
                tgApp.unregisterBot(bot.token());
            } catch (TelegramApiException e) {
                log.warn("unregisterBot failed for backend {}: {}", backendId, e.getMessage());
            }
            safeForceUnlock(redisson.getLock(LOCK_PREFIX + backendId));
            log.info("Deactivated telegram bot @{} for backend {} (released polling lock)", bot.username(), backendId);
        } else {
            log.info("Deactivated telegram bot @{} for backend {} (was follower)", bot.username(), backendId);
        }
    }

    /**
     * Periodic reconciliation: for any bot we hold outbound state for but don't currently lead
     * polling, try to acquire the polling lock. This lets a replica take over polling within ~one
     * cycle after the previous leader's lock auto-expires (Redisson default lease ~30s).
     */
    @Scheduled(fixedDelay = RECONCILE_FIXED_DELAY_MS, initialDelay = RECONCILE_FIXED_DELAY_MS)
    public synchronized void reconcile() {
        for (Map.Entry<String, RegisteredBot> entry : bots.entrySet()) {
            String backendId = entry.getKey();
            RegisteredBot bot = entry.getValue();
            if (bot.session() != null) {
                continue;
            }
            RLock lock = redisson.getLock(LOCK_PREFIX + backendId);
            boolean acquired;
            try {
                acquired = lock.tryLock();
            } catch (Exception e) {
                log.debug("Polling lock probe failed for backend {}: {}", backendId, e.getMessage());
                continue;
            }
            if (!acquired) {
                continue;
            }
            try {
                LongPollingUpdateConsumer consumer = updates -> updates.forEach(u -> {
                    try {
                        updateDispatcher.dispatch(backendId, u);
                    } catch (Exception ex) {
                        log.error("Dispatch failed for backend {}", backendId, ex);
                    }
                });
                BotSession session =
                        tgApp.registerBot(bot.token(), () -> telegramUrl, new DefaultGetUpdatesGenerator(), consumer);
                bots.put(backendId, new RegisteredBot(bot.token(), bot.client(), bot.username(), session));
                log.info("Took over polling for telegram bot @{} on backend {}", bot.username(), backendId);
            } catch (TelegramApiException e) {
                log.warn("Reconcile registerBot failed for backend {}: {}", backendId, e.getMessage());
                safeForceUnlock(lock);
            }
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
            RegisteredBot bot = entry.getValue();
            if (bot.session() == null) {
                continue;
            }
            try {
                tgApp.unregisterBot(bot.token());
            } catch (Exception e) {
                log.warn("unregisterBot at shutdown failed for {}", entry.getKey(), e);
            }
            safeForceUnlock(redisson.getLock(LOCK_PREFIX + entry.getKey()));
        }
        bots.clear();
        try {
            tgApp.close();
        } catch (Exception e) {
            log.warn("Closing TelegramBotsLongPollingApplication failed", e);
        }
    }

    private static void safeForceUnlock(RLock lock) {
        try {
            lock.forceUnlock();
        } catch (Exception e) {
            // best-effort — lock may have already auto-expired
        }
    }

    private record RegisteredBot(String token, TelegramClient client, String username, BotSession session) {}
}
