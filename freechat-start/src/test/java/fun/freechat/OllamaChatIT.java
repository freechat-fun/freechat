package fun.freechat;

import fun.freechat.service.enums.ModelProvider;
import org.junit.jupiter.api.Disabled;

import static fun.freechat.service.enums.ModelProvider.OLLAMA;

@Disabled
class OllamaChatIT extends OpenAiChatIT {
    @Override
    protected ModelProvider modelProvider() {
        return OLLAMA;
    }
}
