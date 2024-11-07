package fun.freechat;

import fun.freechat.service.enums.ModelProvider;

import static fun.freechat.service.enums.ModelProvider.OLLAMA;

public class OllamaPromptAiIT extends OpenAiPromptAiIT {
    @Override
    protected ModelProvider modelProvider() {
        return OLLAMA;
    }
}
