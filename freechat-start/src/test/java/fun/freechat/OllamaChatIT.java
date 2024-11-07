package fun.freechat;

import fun.freechat.service.enums.ModelProvider;

import static fun.freechat.service.enums.ModelProvider.OLLAMA;

public class OllamaChatIT extends OpenAiChatIT {
    protected ModelProvider modelProvider() {
        return OLLAMA;
    }
}
