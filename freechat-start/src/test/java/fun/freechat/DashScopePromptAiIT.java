package fun.freechat;

import fun.freechat.service.enums.ModelProvider;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static fun.freechat.service.enums.ModelProvider.DASH_SCOPE;

@EnabledIfEnvironmentVariable(named = "DASHSCOPE_API_KEY", matches = ".+")
public class DashScopePromptAiIT extends OpenAiPromptAiIT {
    @Override
    protected ModelProvider modelProvider() {
        return DASH_SCOPE;
    }
}
