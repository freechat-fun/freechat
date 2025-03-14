package fun.freechat.util;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class HttpUtils {
    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

    private static final int CALL_TIMEOUT_M = 3;
    private static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
    private static final String DEFAULT_CONTENT_TYPE = "application/json";
    private static final String DEFAULT_ACCEPT = "*/*";
    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofMinutes(3))
            .build();

    private static HttpRequest.BodyPublisher publisherFor(String body) {
        return StringUtils.isBlank(body) ?
                HttpRequest.BodyPublishers.noBody() :
                HttpRequest.BodyPublishers.ofString(body);
    }

    private static HttpRequest.Builder builderFor(String url, Map<String, String> headers) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", DEFAULT_USER_AGENT)
                .header("Content-Type", DEFAULT_CONTENT_TYPE)
                .header("Accept", DEFAULT_ACCEPT);
        if (MapUtils.isNotEmpty(headers)) {
            headers.forEach(builder::header);
        }
        return builder;
    }

    private static String getResponseAsString(HttpRequest request) {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return response.body();
            }
        } catch (IOException | InterruptedException e) {
            log.warn("Failed to send data to {}", request.uri(), e);
        }
        return null;
    }

    public static boolean ping(String url) {
        HttpRequest request = builderFor(url, null).HEAD().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() >= 200 && response.statusCode() < 300;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    public static String get(String url) {
        return get(url, null);
    }

    public static String get(String url, Map<String, String> headers) {
        return getResponseAsString(builderFor(url, headers).GET().build());
    }

    public static String post(String url, String data) {
        return post(url, null, data);
    }

    public static String post(String url, Map<String, String> headers, String data) {
        HttpRequest.Builder builder = builderFor(url, headers);
        builder.POST(publisherFor(data));
        return getResponseAsString(builder.build());
    }

    public static CompletableFuture<HttpResponse<InputStream>> asyncPost(
            String url, Map<String, String> headers, String data) {
        HttpRequest.Builder builder = builderFor(url, headers);
        HttpRequest request = builder.POST(publisherFor(data)).build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream());
    }

    public static String put(String url, String data) {
        return put(url, null, data);
    }

    public static String put(String url, Map<String, String> headers, String data) {
        HttpRequest.Builder builder = builderFor(url, headers);
        builder.PUT(publisherFor(data));
        return getResponseAsString(builder.build());
    }

    public static String delete(String url) {
        return delete(url, null);
    }

    public static String delete(String url, Map<String, String> headers) {
        return getResponseAsString(builderFor(url, headers).DELETE().build());
    }

    public static String upload(String url, Path file, String filename) {
        return upload(url, file, filename, null);
    }

    public static String upload(String url, Path file, String filename, Map<String, String> headers) {
        if (file == null || !Files.exists(file)) {
            return null;
        }

        if (filename == null) {
            filename = file.getFileName().toString();
        }

        String boundary = "Boundary-" + System.currentTimeMillis();
        String crlf = "\r\n";
        String headerBuilder = "--" + boundary + crlf +
                "Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"" + crlf +
                "Content-Type: application/octet-stream" + crlf +
                crlf;

        byte[] headerBytes = headerBuilder.getBytes();
        byte[] footerBytes = (crlf + "--" + boundary + "--" + crlf).getBytes();

        try {
            HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofByteArrays(List.of(headerBytes,
                    Files.readAllBytes(file), footerBytes));
            HttpRequest.Builder builder = builderFor(url, headers);
            builder.header("Content-Type", "multipart/form-data; boundary=" + boundary);
            builder.POST(bodyPublisher);
            return getResponseAsString(builder.build());
        } catch (IOException e) {
            log.warn("Failed to send data to {}", url, e);
            return null;
        }
    }

    public static String toCurl(String url, Map<String, String> headers, String body) {
        String method = body == null ? "GET" : "POST";
        return toCurl(url, headers, body, method);
    }

    public static String toCurl(String url, Map<String, String> headers, String body, String method) {
        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("User-Agent", DEFAULT_USER_AGENT);
        httpHeaders.put("Content-Type", DEFAULT_CONTENT_TYPE);
        httpHeaders.put("Accept", DEFAULT_ACCEPT);
        if (MapUtils.isNotEmpty(headers)) {
            httpHeaders.putAll(headers);
        }
        StringBuilder debugInfo = new StringBuilder("curl -X ").append(method).append(" \\\n");
        httpHeaders.forEach((k, v) ->
                debugInfo.append(" -H '").append(k).append(": ").append(v).append("' \\\n"));
        if (StringUtils.isNotBlank(body)) {
            debugInfo.append(" -d '").append(body).append("' \\\n");
        }
        debugInfo.append(" '").append(url).append("'");

        return debugInfo.toString();
    }

    // See: https://stackoverflow.com/questions/161738/what-is-the-best-regular-expression-to-check-if-a-string-is-a-valid-url
    private static final String URL_REGEX = "^[a-z](?:[-a-z0-9\\+\\.])*:(?:\\/\\/(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:])*@)?(?:\\[(?:(?:(?:[0-9a-f]{1,4}:){6}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|::(?:[0-9a-f]{1,4}:){5}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:[0-9a-f]{1,4})?::(?:[0-9a-f]{1,4}:){4}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:(?:[0-9a-f]{1,4}:){0,1}[0-9a-f]{1,4})?::(?:[0-9a-f]{1,4}:){3}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:(?:[0-9a-f]{1,4}:){0,2}[0-9a-f]{1,4})?::(?:[0-9a-f]{1,4}:){2}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:(?:[0-9a-f]{1,4}:){0,3}[0-9a-f]{1,4})?::[0-9a-f]{1,4}:(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:(?:[0-9a-f]{1,4}:){0,4}[0-9a-f]{1,4})?::(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:(?:[0-9a-f]{1,4}:){0,5}[0-9a-f]{1,4})?::[0-9a-f]{1,4}|(?:(?:[0-9a-f]{1,4}:){0,6}[0-9a-f]{1,4})?::)|v[0-9a-f]+\\.[-a-z0-9\\._~!\\$&'\\(\\)\\*\\+,;=:]+)\\]|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3}|(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=])*)(?::[0-9]*)?(?:\\/(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:@]))*)*|\\/(?:(?:(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:@]))+)(?:\\/(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:@]))*)*)?|(?:(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:@]))+)(?:\\/(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:@]))*)*|(?!(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:@])))(?:\\?(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:@])|[\\x{E000}-\\x{F8FF}\\x{F0000}-\\x{FFFFD}\\x{100000}-\\x{10FFFD}\\/\\?])*)?(?:\\#(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:@])|[\\/\\?])*)?$";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);
    private static final String SAFE_URL_REGEX = "^(https?://)?(([\\da-zA-Z.-]+)\\.([a-zA-Z]{2,6})|((\\d{1,3}\\.){3}\\d{1,3}))(:\\d{1,5})?(/[^\\s]*)?$";
    private static final Pattern SAFE_URL_PATTERN = Pattern.compile(SAFE_URL_REGEX);

    public static boolean isValidUrl(String url) {
        return StringUtils.isNotBlank(url) &&
                URL_PATTERN.matcher(url.toLowerCase()).matches();
    }

    public static boolean isSafeUrl(String url) {
        if (StringUtils.isBlank(url) || !SAFE_URL_PATTERN.matcher(url).matches()) {
            return false;
        }

        try {
            URI uri = new URI(url);
            InetAddress address = InetAddress.getByName(uri.getHost());
            return !isPrivateIP(address);
        } catch (URISyntaxException | UnknownHostException e) {
            return false;
        }
    }

    private static boolean isPrivateIP(InetAddress address) {
        byte[] ip = address.getAddress();

        // RFC 1918 & RFC 3330
        return (ip[0] == (byte) 127) ||
                (ip[0] == (byte) 10) ||
                (ip[0] == (byte) 172 && (ip[1] >= 16 && ip[1] <= 31)) ||
                (ip[0] == (byte) 192 && ip[1] == (byte) 168);
    }

    public static void main(String[] args) {
        List<String> urls = List.of(
                "https://freechat.fun",
                "https://freechat.fun/index",
                "https://freechat.fun/index.html",
                "https://freechat.fun/public/test/",
                "https://freechat.fun/public/Test/.well-known/你是谁.json",
                "https://freechat.fun/public/test/?aaa=你是谁",
                "https://freechat.fun/public/test/api-docs.json#?aaa=1",
                "http://127.0.0.1:8080/public/test/api-docs.json#?aaa=1",
                "http://192.168.0.1:8080/public/test/api-docs.json#?aaa=1",
                "http://172.16.0.1:8080/public/test/api-docs.json#?aaa=1",
                "http://172.0.0.1:8080/public/test/api-docs.json#?aaa=1",
                "ftp://freechat.fun/public/test/你是谁.json",
                "tcp://freechat.fun",
                "file:///home/user/你是谁.json",
                "SCOPE_xxx",
                "[https://freechat.fun]",
                "{}",
                "{\"a\": \"https://freechat.fun\"}",
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNby"
        );
        for (var url : urls) {
            System.out.println(url + ": " + isValidUrl(url) + ", " + isSafeUrl(url));
        }
    }
}
