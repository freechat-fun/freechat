package fun.freechat.service.character;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.output.Response;
import fun.freechat.model.User;

public interface CharacterAiService {
    Response<AiMessage> send(String characterId,
                             User user,
                             String content);

    <T> void sendAsync(String characterId,
                       User user,
                       String content,
                       StreamingResponseHandler<T> handler);
}
