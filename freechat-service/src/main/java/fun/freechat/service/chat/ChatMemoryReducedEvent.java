package fun.freechat.service.chat;

import java.util.List;

public record ChatMemoryReducedEvent(Object memoryId, List<ChatMessageRecord> records) { }
