package fun.freechat.service.prompt;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import fun.freechat.model.AiModelInfo;
import fun.freechat.model.User;
import fun.freechat.service.enums.PromptType;

import java.util.Map;

public interface PromptAiService {
    ChatResponse send(String prompt,
                      PromptType promptType,
                      User user,
                      String apiKeyInfo,
                      AiModelInfo modelInfo,
                      Map<String, Object> parameters);
    void streamSend(String prompt,
                    PromptType promptType,
                    User user,
                    String apiKeyInfo,
                    AiModelInfo modelInfo,
                    Map<String, Object> parameters,
                    StreamingChatResponseHandler handler);
}
