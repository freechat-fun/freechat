package fun.freechat.service.chat;

public class ChatQueueRejectedException extends RuntimeException {
    public ChatQueueRejectedException() {
        super("Chat queue is draining or shut down");
    }
}
