package fun.freechat.service.ai.message;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageSerializer;

import java.io.IOException;

public class ChatMessageJacksonSerializer extends JsonSerializer<ChatMessage> {
    @Override
    public void serialize(ChatMessage message, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(ChatMessageSerializer.messageToJson(message));
    }
}
