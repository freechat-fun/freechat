package fun.freechat;

import fun.freechat.service.enums.ModelProvider;

import static fun.freechat.service.enums.ModelProvider.DASH_SCOPE;

public class DashScopeChatIT extends OpenAiChatIT {
    protected ModelProvider modelProvider() {
        return DASH_SCOPE;
    }
}
