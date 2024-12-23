package fun.freechat;

import fun.freechat.service.enums.ModelProvider;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static fun.freechat.service.enums.ModelProvider.AZURE_OPEN_AI;

@EnabledIfEnvironmentVariable(named = "AZURE_OPENAI_KEY", matches = ".+")
@EnabledIfEnvironmentVariable(named = "AZURE_OPENAI_ENDPOINT", matches = ".+")
@Disabled
public class AzureOpenAiChatIT extends OpenAiChatIT {
    @Override
    protected ModelProvider modelProvider() {
        return AZURE_OPEN_AI;
    }
}
