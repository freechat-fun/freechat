package fun.freechat.util;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.telegram.telegrambots.meta.TelegramUrl;

/**
 * Switches the Telegram backing service for integration tests between the real
 * api.telegram.org (when reachable) and a local WireMock server seeded with the
 * minimum stubs the SDK calls. The decision is made once per JVM; tests query
 * {@link #LIVE} to know which mode they are in.
 */
public final class TelegramITSupport {

    public static final boolean LIVE = canReach("api.telegram.org", 443, 2_000);

    private static WireMockServer mock;
    private static TelegramUrl telegramUrl;

    private TelegramITSupport() {}

    public static synchronized void startIfNeeded(String botToken) {
        if (telegramUrl != null) {
            return;
        }
        if (LIVE) {
            telegramUrl = TelegramUrl.DEFAULT_URL;
            return;
        }
        mock = new WireMockServer(options().dynamicPort());
        mock.start();
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
        m.stubFor(post(urlPathMatching("/bot" + botToken + "/getMe"))
                .willReturn(okJson("{\"ok\":true,\"result\":"
                        + "{\"id\":12345,\"is_bot\":true,\"first_name\":\"FC Test\","
                        + "\"username\":\"fc_test_bot\",\"can_join_groups\":true,"
                        + "\"can_read_all_group_messages\":false,"
                        + "\"supports_inline_queries\":false}}")));
        m.stubFor(post(urlPathMatching("/bot" + botToken + "/sendMessage.*"))
                .willReturn(okJson("{\"ok\":true,\"result\":"
                        + "{\"message_id\":1,\"date\":0,"
                        + "\"chat\":{\"id\":123,\"type\":\"private\"},"
                        + "\"text\":\"\"}}")));
        m.stubFor(post(urlPathMatching("/bot" + botToken + "/getUpdates.*"))
                .willReturn(okJson("{\"ok\":true,\"result\":[]}")));
        m.stubFor(post(urlPathMatching("/bot" + botToken + "/close.*"))
                .willReturn(okJson("{\"ok\":true,\"result\":true}")));
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
