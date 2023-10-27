package fun.freechat.service.prompt;

import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.output.Response;
import fun.freechat.model.AiModelInfo;
import fun.freechat.model.User;
import fun.freechat.service.ai.message.ChatMessage;
import fun.freechat.service.enums.PromptType;

import java.util.Map;

public interface PromptAiService {
    Response<ChatMessage> send(String prompt,
                               PromptType promptType,
                               User user,
                               String apiKeyInfo,
                               AiModelInfo modelInfo,
                               Map<String, Object> parameters);

    <T> void streamSend(String prompt,
                        PromptType promptType,
                        User user,
                        String apiKeyInfo,
                        AiModelInfo modelInfo,
                        Map<String, Object> parameters,
                        StreamingResponseHandler<T> handler);
}
