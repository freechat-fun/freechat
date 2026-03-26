package fun.freechat;

import static fun.freechat.service.enums.ModelProvider.OLLAMA;

import fun.freechat.service.enums.ModelProvider;
import org.junit.jupiter.api.Disabled;

@Disabled
class OllamaPromptAiIT extends OpenAiPromptAiIT {
    @Override
    protected ModelProvider modelProvider() {
        return OLLAMA;
    }
}
