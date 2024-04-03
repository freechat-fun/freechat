package fun.freechat.service.rag;

import fun.freechat.model.RagTask;

import java.util.concurrent.CompletableFuture;

public interface RagTaskRunner {
    CompletableFuture<Void> start(RagTask task);
}
