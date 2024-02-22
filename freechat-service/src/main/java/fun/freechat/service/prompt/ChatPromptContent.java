package fun.freechat.service.prompt;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import fun.freechat.service.ai.message.ChatMessageJacksonDeserializer;
import fun.freechat.service.ai.message.ChatMessageJacksonSerializer;
import fun.freechat.service.ai.message.ChatMessageListJacksonDeserializer;
import fun.freechat.service.ai.message.ChatMessageListJacksonSerializer;
import lombok.Data;

import java.util.List;

@Data
public class ChatPromptContent {
    private String system;
    @JsonSerialize(using = ChatMessageJacksonSerializer.class)
    @JsonDeserialize(using = ChatMessageJacksonDeserializer.class)
    private UserMessage messageToSend;
    @JsonSerialize(using = ChatMessageListJacksonSerializer.class)
    @JsonDeserialize(using = ChatMessageListJacksonDeserializer.class)
    private List<ChatMessage> messages;
}