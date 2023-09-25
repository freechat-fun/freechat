package fun.freechat.service.character.impl;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.output.Response;
import fun.freechat.model.User;
import fun.freechat.service.character.CharacterAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@SuppressWarnings("unused")
public class CharacterAiServiceImpl implements CharacterAiService {
    @Override
    public Response<AiMessage> send(String characterId, User user, String content) {
        return null;
    }

    @Override
    public <T> void sendAsync(String characterId, User user, String content, StreamingResponseHandler<T> handler) {

    }
}
