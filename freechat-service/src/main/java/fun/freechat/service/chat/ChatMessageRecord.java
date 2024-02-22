package fun.freechat.service.chat;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.langchain4j.data.message.ChatMessage;
import fun.freechat.service.ai.message.ChatMessageJacksonDeserializer;
import fun.freechat.service.ai.message.ChatMessageJacksonSerializer;
import lombok.Data;

import java.util.Date;

@Data
public class ChatMessageRecord {
    @JsonSerialize(using = ChatMessageJacksonSerializer.class)
    @JsonDeserialize(using = ChatMessageJacksonDeserializer.class)
    private ChatMessage message;
    private Long id;
    private Date gmtCreate;
    private String ext;
    private String chatOwnerId;
    private String characterOwnerId;
}
