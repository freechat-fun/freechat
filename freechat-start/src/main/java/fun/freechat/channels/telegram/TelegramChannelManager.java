package fun.freechat.channels.telegram;

import static org.mybatis.dynamic.sql.SqlBuilder.isNotNull;
import static org.mybatis.dynamic.sql.SqlBuilder.select;

import fun.freechat.channels.spi.ChannelFailure;
import fun.freechat.channels.spi.ChannelInstance;
import fun.freechat.channels.spi.ChannelPolicy;
import fun.freechat.channels.spi.ChannelRuntimeContext;
import fun.freechat.channels.spi.ChannelText;
import fun.freechat.mapper.CharacterBackendDynamicSqlSupport;
import fun.freechat.mapper.CharacterBackendMapper;
import fun.freechat.model.CharacterBackend;
import fun.freechat.service.channel.ChannelPollingLease;
import fun.freechat.service.channel.ChannelTaskScheduler;
import fun.freechat.service.common.EncryptionService;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.TelegramUrl;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@Slf4j
public class TelegramChannelManager {
    private static final String LOCK_PREFIX = "freechat:channels:telegram:polling:";
    private final CharacterBackendMapper backends;
    private final EncryptionService encryption;
    private final RedissonClient redisson;
    private final TelegramPollingSession.Factory polling;
    private final Function<String, TelegramClient> clients;
    private final OkHttpClient http;
    private final Map<String, Bot> bots = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<Void>> retiring = new ConcurrentHashMap<>();
    private ChannelRuntimeContext<Update> runtime;
    private volatile boolean stopped;

    @Autowired
    public TelegramChannelManager(
            TelegramUrl url, CharacterBackendMapper backends, EncryptionService encryption, RedissonClient redisson) {
        this(url, backends, encryption, redisson, null, null);
    }

    public TelegramChannelManager(
            TelegramUrl url,
            CharacterBackendMapper backends,
            EncryptionService encryption,
            RedissonClient redisson,
            TelegramPollingSession.Factory polling,
            Function<String, TelegramClient> clients) {
        this.backends = backends;
        this.encryption = encryption;
        this.redisson = redisson;
        this.polling =
                polling == null ? (token, updates) -> TelegramPollingSession.start(url, token, updates) : polling;
        this.http = clients == null ? TelegramHttpClient.create(Duration.ofSeconds(30)) : null;
        this.clients = clients == null ? token -> new OkHttpTelegramClient(http, token, url) : clients;
    }

    public synchronized void start(ChannelRuntimeContext<Update> runtime) {
        this.runtime = runtime;
        stopped = false;
        activateExisting();
    }

    public synchronized void activateExisting() {
        if (stopped || runtime == null) {
            return;
        }
        var statement = select(CharacterBackendDynamicSqlSupport.backendId)
                .from(CharacterBackendDynamicSqlSupport.characterBackend)
                .where(CharacterBackendDynamicSqlSupport.tgBotToken, isNotNull())
                .build()
                .render(RenderingStrategies.MYBATIS3);
        List<String> desired = backends.selectMany(statement).stream()
                .map(CharacterBackend::getBackendId)
                .toList();
        for (String backendId : desired) {
            try {
                activate(backendId);
            } catch (Exception ignored) {
                log.warn("Telegram backend activation deferred for {}", backendId);
            }
        }
        var desiredIds = new HashSet<>(desired);
        List.copyOf(bots.keySet()).stream()
                .filter(id -> !desiredIds.contains(id))
                .forEach(this::deactivate);
    }

    public synchronized void activate(String backendId) {
        if (stopped || runtime == null) {
            return;
        }
        CharacterBackend backend = backends.selectByPrimaryKey(backendId).orElse(null);
        String token = backend == null ? null : encryption.decrypt(backend.getTgBotToken());
        if (StringUtils.isBlank(token)) {
            deactivate(backendId);
            return;
        }
        Bot existing = bots.get(backendId);
        if (existing != null && !token.equals(existing.token)) {
            deactivate(backendId);
            existing = null;
        }
        if (retiring.containsKey(backendId)) {
            return;
        }
        if (existing == null) {
            if (bots.size() + retiring.size() >= ChannelPolicy.defaults().maxInstances()) {
                log.warn("Telegram account capacity reached");
                return;
            }
            TelegramClient client = clients.apply(token);
            String username;
            try {
                username = client.execute(new GetMe()).getUserName();
            } catch (Exception ignored) {
                log.warn("Telegram account lookup deferred for {}", backendId);
                return;
            }
            existing = new Bot(token, client, username);
            bots.put(backendId, existing);
        }
        if (existing.lease == null
                || existing.lease.settled().toCompletableFuture().isDone()) {
            startPolling(backendId, existing);
        }
    }

    private void startPolling(String backendId, Bot bot) {
        AtomicReference<ChannelInstance<Update>> generation = new AtomicReference<>();
        bot.lease = ChannelPollingLease.start(
                redisson,
                LOCK_PREFIX + backendId,
                () -> {
                    if (stopped || bots.get(backendId) != bot) {
                        throw new ChannelFailure(ChannelFailure.Kind.CANCELLED);
                    }
                    ChannelInstance<Update> instance = runtime.openInstance(backendId);
                    generation.set(instance);
                    bot.inbound = instance;
                    AutoCloseable session;
                    try {
                        if (stopped || bots.get(backendId) != bot || !instance.isActive()) {
                            throw new ChannelFailure(ChannelFailure.Kind.CANCELLED);
                        }
                        session = polling.start(bot.token, updates -> {
                            if (!stopped && bots.get(backendId) == bot) {
                                receive(instance, updates);
                            }
                        });
                    } catch (Exception ignored) {
                        instance.close();
                        throw new IllegalStateException("Telegram polling registration failed");
                    }
                    return () -> {
                        try {
                            instance.close();
                        } finally {
                            session.close();
                        }
                    };
                },
                () -> {
                    ChannelInstance<Update> instance = generation.get();
                    if (instance != null) {
                        instance.close();
                    }
                },
                Duration.ofSeconds(1));
        bot.lease.settled().whenComplete((ignored, error) -> {
            if (error != null) {
                log.warn("Telegram polling ownership ended for {}", backendId);
            }
        });
    }

    private void receive(ChannelInstance<Update> instance, List<Update> updates) {
        for (Update update : updates) {
            if (!instance.isActive()) {
                return;
            }
            if (update == null
                    || !update.hasMessage()
                    || update.getMessage().getChat() == null
                    || update.getMessage().getChat().getId() == null) {
                continue;
            }
            String conversation = update.getMessage().getChatId().toString();
            try {
                instance.receive(
                                conversation,
                                update.getUpdateId() == null
                                        ? null
                                        : update.getUpdateId().toString(),
                                update)
                        .whenComplete((ignored, error) -> {
                            if (error != null
                                    && instance.isActive()
                                    && ChannelTaskScheduler.failure(error).kind() == ChannelFailure.Kind.REJECTED) {
                                instance.outbound(conversation)
                                        .sendText(ChannelText.plain(
                                                "I'm handling too many messages right now. Please try again shortly."));
                            }
                        });
            } catch (Exception ignored) {
                log.warn("Telegram update admission failed for {}", instance.id());
            }
        }
    }

    public synchronized void deactivate(String backendId) {
        Bot bot = bots.remove(backendId);
        if (bot == null) {
            return;
        }
        if (bot.inbound != null) {
            bot.inbound.close();
        }
        if (bot.lease != null) {
            CompletableFuture<Void> settlement = bot.lease.settled().toCompletableFuture();
            retiring.put(backendId, settlement);
            bot.lease.close();
            settlement.whenComplete((ignored, error) -> retiring.remove(backendId, settlement));
        }
    }

    public void reconcile() {
        activateExisting();
    }

    public TelegramClient getClient(String backendId) {
        Bot bot = bots.get(backendId);
        return bot == null ? null : bot.client;
    }

    public String getUsername(String backendId) {
        Bot bot = bots.get(backendId);
        return bot == null ? null : bot.username;
    }

    public String getInviteLink(String backendId) {
        String username = getUsername(backendId);
        return username == null ? null : "https://t.me/" + username;
    }

    public synchronized void stopReceiving() {
        stopped = true;
        for (Bot bot : bots.values()) {
            if (bot.inbound != null) {
                bot.inbound.close();
            }
            if (bot.lease != null) {
                bot.lease.close();
            }
        }
    }

    public synchronized void shutdown() {
        stopReceiving();
        List.copyOf(bots.keySet()).forEach(this::deactivate);
        try {
            CompletableFuture.allOf(retiring.values().toArray(new CompletableFuture[0]))
                    .get(10, TimeUnit.SECONDS);
        } catch (Exception ignored) {
            log.warn("Telegram polling cleanup did not settle before its deadline");
        }
        if (http != null) {
            TelegramHttpClient.closeOwned(http);
        }
    }

    private static final class Bot {
        final String token;
        final TelegramClient client;
        final String username;
        volatile ChannelPollingLease lease;
        volatile ChannelInstance<Update> inbound;

        Bot(String token, TelegramClient client, String username) {
            this.token = token;
            this.client = client;
            this.username = username;
        }
    }
}
