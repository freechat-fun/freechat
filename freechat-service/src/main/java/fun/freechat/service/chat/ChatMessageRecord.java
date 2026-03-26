package fun.freechat.service.chat;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.langchain4j.data.message.ChatMessage;
import fun.freechat.service.ai.message.ChatMessageJacksonDeserializer;
import fun.freechat.service.ai.message.ChatMessageJacksonSerializer;
import fun.freechat.service.enums.TtsSpeakerType;
import java.util.Date;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(NON_NULL)
public class ChatMessageRecord {
    @JsonSerialize(using = ChatMessageJacksonSerializer.class)
    @JsonDeserialize(using = ChatMessageJacksonDeserializer.class)
    private ChatMessage message;

    private Long id;
    private Date gmtCreate;
    private String ext;
    private String chatId;
    private String chatOwnerId;
    private String characterOwnerId;
    private String speaker;
    private TtsSpeakerType speakerType;
}
