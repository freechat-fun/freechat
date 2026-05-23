package fun.freechat.service.chat.impl;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ImageEditAgent {
    @Agent
    Result<AiMessage> edit(
            @V("prompt") @UserMessage String prompt,
            @V("description") String description,
            @V("originImage") @UserMessage ImageContent image);
}
