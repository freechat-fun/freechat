package fun.freechat.service.ai.message;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;

import java.io.IOException;
import java.util.List;

public class ChatMessageListJacksonDeserializer extends JsonDeserializer<List<ChatMessage>> {
    @Override
    public List<ChatMessage> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return ChatMessageDeserializer.messagesFromJson(parser.getText());
    }
}
