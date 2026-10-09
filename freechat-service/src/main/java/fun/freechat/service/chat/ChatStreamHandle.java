package fun.freechat.service.chat;

import dev.langchain4j.service.TokenStream;
import java.util.concurrent.CompletableFuture;

public interface ChatStreamHandle {
    CompletableFuture<TokenStream> ready();

    // Never await settlement inside this stream's own callbacks: cleanup waits for them to return.
    CompletableFuture<Void> settled();

    // Cancellation cannot settle noncancellable provider work before its natural terminal callback.
    void cancel();

    static ChatStreamHandle empty() {
        return new ChatStreamHandle() {
            public CompletableFuture<TokenStream> ready() {
                return CompletableFuture.completedFuture(null);
            }

            public CompletableFuture<Void> settled() {
                return CompletableFuture.completedFuture(null);
            }

            public void cancel() {}
        };
    }
}
