package fun.freechat.service.ai.message;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageSerializer;

import java.io.IOException;
import java.util.List;

public class ChatMessageListJacksonSerializer  extends JsonSerializer<List<ChatMessage>> {
    @Override
    public void serialize(List<ChatMessage> messages, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(ChatMessageSerializer.messagesToJson(messages));
    }
}
