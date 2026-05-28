package fun.freechat.util;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import org.telegram.telegrambots.meta.TelegramUrl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

/**
 * Switches the Telegram backing service for integration tests between the real
 * api.telegram.org (when reachable) and a local WireMock server seeded with the
 * minimum stubs the SDK calls. The decision is made once per JVM; tests query
 * {@link #LIVE} to know which mode they are in.
 */
public final class TelegramITSupport {

    public static final boolean LIVE = computeLive();

    private static boolean computeLive() {
        String forceMock = System.getenv("TELEGRAM_FORCE_MOCK");
        if (forceMock == null) {
            forceMock = System.getProperty("TELEGRAM_FORCE_MOCK");
        }
        if ("true".equalsIgnoreCase(forceMock)) {
            return false;
        }
        return canReach("api.telegram.org", 443, 2_000);
    }

    private static WireMockServer mock;
    private static TelegramUrl telegramUrl;

    private TelegramITSupport() {}

    public static synchronized void startIfNeeded(String botToken) {
        if (telegramUrl != null) {
            return;
        }
        if (LIVE) {
            telegramUrl = TelegramUrl.DEFAULT_URL;
            System.out.println("[TelegramITSupport] Using live Telegram URL: " + telegramUrl);
            return;
        }
        mock = new WireMockServer(options().dynamicPort().notifier(new ConsoleNotifier(true)));
        mock.start();
        System.out.println("[TelegramITSupport] WireMock started on port " + mock.port());
        seed(mock, botToken);
        telegramUrl = TelegramUrl.builder()
                .schema("http")
                .host("localhost")
                .port(mock.port())
                .testServer(false)
                .build();
    }

    public static synchronized void stop() {
        if (mock != null) {
            mock.stop();
            mock = null;
        }
        telegramUrl = null;
    }

    public static TelegramUrl telegramUrl() {
        if (telegramUrl == null) {
            throw new IllegalStateException("TelegramITSupport.startIfNeeded(token) not called");
        }
        return telegramUrl;
    }

    public static WireMockServer mockServer() {
        return mock;
    }

    private static void seed(WireMockServer m, String botToken) {
        // SDK sends method names lowercase in the URL path (e.g. /getme, /sendmessage).
        // Specific stubs use priority=1 (higher) so they win against the catch-all (priority=10).
        m.stubFor(post(urlPathMatching("(?i).*/getme"))
                .atPriority(1)
                .willReturn(okJson("{\"ok\":true,\"result\":"
                        + "{\"id\":12345,\"is_bot\":true,\"first_name\":\"FC Test\","
                        + "\"username\":\"fc_test_bot\",\"can_join_groups\":true,"
                        + "\"can_read_all_group_messages\":false,"
                        + "\"supports_inline_queries\":false}}")));
        m.stubFor(post(urlPathMatching("(?i).*/sendmessage"))
                .atPriority(1)
                .willReturn(okJson("{\"ok\":true,\"result\":"
                        + "{\"message_id\":1,\"date\":0,"
                        + "\"chat\":{\"id\":123,\"type\":\"private\"},"
                        + "\"text\":\"\"}}")));
        m.stubFor(post(urlPathMatching("(?i).*/getupdates"))
                .atPriority(1)
                .willReturn(okJson("{\"ok\":true,\"result\":[]}")));
        // Catch-all for deleteWebhook / setMyCommands / close / etc — Boolean true result
        m.stubFor(any(anyUrl()).atPriority(10).willReturn(okJson("{\"ok\":true,\"result\":true}")));
    }

    private static boolean canReach(String host, int port, int timeoutMs) {
        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress(host, port), timeoutMs);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // Suppress unused import warnings for static-import-only symbols.
    @SuppressWarnings("unused")
    private static void __keepImports() {
        equalTo("");
    }
}
