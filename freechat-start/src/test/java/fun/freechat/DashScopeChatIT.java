package fun.freechat;

import static fun.freechat.service.enums.ModelProvider.DASH_SCOPE;

import fun.freechat.service.enums.ModelProvider;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "DASHSCOPE_API_KEY", matches = ".+")
class DashScopeChatIT extends OpenAiChatIT {
    @Override
    protected ModelProvider modelProvider() {
        return DASH_SCOPE;
    }
}
