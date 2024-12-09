package fun.freechat;

import fun.freechat.service.enums.ModelProvider;
import org.junit.jupiter.api.Disabled;

import static fun.freechat.service.enums.ModelProvider.OLLAMA;

@Disabled
public class OllamaChatIT extends OpenAiChatIT {
    protected ModelProvider modelProvider() {
        return OLLAMA;
    }
}
