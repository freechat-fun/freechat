package fun.freechat.service.tools.network;

import com.fasterxml.jackson.databind.JsonNode;
import fun.freechat.service.tools.ToolArguments;
import fun.freechat.service.tools.ToolContext;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Timeout;

final class NetworkTestSupport {
    private NetworkTestSupport() {}

    static ToolContext context() {
        return context(Duration.ofSeconds(10));
    }

    static ToolContext context(Duration timeout) {
        return new ToolContext("chat-secret", "user-secret", "character-secret", "backend-secret", timeout);
    }

    static JsonNode args(String json) throws IOException {
        return ToolArguments.JSON.readTree(json);
    }

    static Response response(Request request, int status, String mediaType, String content) {
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(status)
                .message("fixture")
                .header("Content-Type", mediaType)
                .body(ResponseBody.create(content, MediaType.get(mediaType)))
                .build();
    }

    @FunctionalInterface
    interface Script {
        Response execute(Request request) throws IOException;
    }

    static final class Calls implements Call.Factory {
        final AtomicInteger count = new AtomicInteger();
        volatile Request request;
        volatile ScriptedCall lastCall;
        private final Script script;

        Calls(Script script) {
            this.script = script;
        }

        @Override
        public Call newCall(Request request) {
            this.request = request;
            lastCall = new ScriptedCall(request);
            return lastCall;
        }

        final class ScriptedCall implements Call {
            private final Request request;
            private volatile boolean cancelled;
            private volatile boolean executed;

            ScriptedCall(Request request) {
                this.request = request;
            }

            @Override
            public Request request() {
                return request;
            }

            @Override
            public Response execute() throws IOException {
                executed = true;
                count.incrementAndGet();
                return script.execute(request);
            }

            @Override
            public void enqueue(Callback callback) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void cancel() {
                cancelled = true;
            }

            @Override
            public boolean isExecuted() {
                return executed;
            }

            @Override
            public boolean isCanceled() {
                return cancelled;
            }

            @Override
            public Timeout timeout() {
                return new Timeout();
            }

            @Override
            public Call clone() {
                return new ScriptedCall(request);
            }
        }
    }
}
