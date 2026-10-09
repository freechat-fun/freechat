package example.channels;

import fun.freechat.channels.spi.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public final class DemoChannelPlugin implements ChannelPlugin<String> {
    public record SentMessage(String conversationId, String text) {}

    private final ConcurrentLinkedQueue<SentMessage> sent = new ConcurrentLinkedQueue<>();
    private final AtomicLong messageIds = new AtomicLong();
    private final CompletableFuture<Void> ready = new CompletableFuture<>();
    private volatile ChannelInstance<String> instance;

    @Override
    public String id() {
        return "demo";
    }

    @Override
    public ChannelTransport transport() {
        return (address, text) -> {
            sent.add(new SentMessage(address.conversationId(), text.text()));
            return new ChannelReceipt(Long.toString(messageIds.incrementAndGet()));
        };
    }

    @Override
    public ChannelInboundHandler<String> inboundHandler() {
        return (envelope, turn) -> turn.outbound()
                .sendText(ChannelText.plain("You said: " + envelope.payload()))
                .thenApply(receipt -> null);
    }

    @Override
    public void start(ChannelRuntimeContext<String> runtime) {
        instance = runtime.openInstance("demo-account");
        ready.complete(null);
    }

    public CompletableFuture<Void> ready() {
        return ready.copy();
    }

    public CompletableFuture<Void> receive(String conversationId, String text) {
        if (text == null || text.length() > 4096) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid demo message"));
        }
        ChannelInstance<String> current = instance;
        if (current == null) {
            return CompletableFuture.failedFuture(new ChannelFailure(ChannelFailure.Kind.CANCELLED));
        }
        return current.receive(conversationId, null, text);
    }

    public List<SentMessage> sentMessages() {
        return List.copyOf(sent);
    }

    @Override
    public void stopReceiving() {
        ChannelInstance<String> current = instance;
        if (current != null) {
            current.close();
        }
    }

    @Override
    public void close() {
        stopReceiving();
    }
}
