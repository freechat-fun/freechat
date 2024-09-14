package fun.freechat.service.chat;

import dev.langchain4j.internal.Utils;
import dev.langchain4j.model.output.TokenUsage;

public record MemoryUsage(Long messageUsage, TokenUsage tokenUsage) {
    public MemoryUsage add(MemoryUsage that) {
        if (that == null) {
            return this;
        }
        return add(that.messageUsage(), that.tokenUsage());
    }

    public MemoryUsage add(Long messageUsage, TokenUsage tokenUsage) {
        return new MemoryUsage(
                sum(messageUsage(), messageUsage),
                sum(tokenUsage(), tokenUsage));
    }

    private static Long sum(Long first, Long second) {
        return first == null && second == null ? null :
                Utils.getOrDefault(first, 0L) + Utils.getOrDefault(second, 0L);
    }

    private static TokenUsage sum(TokenUsage first, TokenUsage second) {
        return first == null && second == null ? null :
                Utils.getOrDefault(first, TokenUsage::new).add(Utils.getOrDefault(second, TokenUsage::new));
    }
}
