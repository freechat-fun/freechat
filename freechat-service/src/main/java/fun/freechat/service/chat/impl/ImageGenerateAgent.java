package fun.freechat.service.chat.impl;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ImageGenerateAgent {
    @Agent
    Result<AiMessage> generate(
            @V("prompt") @UserMessage String prompt,
            @V("description") String description);
}
