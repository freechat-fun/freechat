package fun.freechat.langchain4j.model.input;

import fun.freechat.util.PojoUtils;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FStringPromptTemplate extends PromptTemplate {
    static final Pattern VAR_PATTERN = Pattern.compile("(^|[^\\{])(\\{)(\\{*[^\\{\\}]*\\}*)(\\})([^\\}]|$)");

    private final String template;

    private String getAsString(Object value) {
        if (value instanceof String) {
            return (String) value;
        } else if (Objects.isNull(value)) {
            return "";
        } else if (value instanceof Number || value instanceof Boolean || value instanceof Character) {
            return value.toString();
        } else {
            return PojoUtils.object2JsonString(value);
        }
    }

    public FStringPromptTemplate(String template) {
        super(null);
        this.template = template;
    }

    @Override
    public Prompt apply(Map<String, Object> variables) {
        StringBuilder buffer = new StringBuilder();
        Matcher m = VAR_PATTERN.matcher(template);
        while (m.find()) {
            String content = getAsString(variables.getOrDefault(m.group(3).trim(), ""));
            m.appendReplacement(buffer, m.group(1) + content + m.group(5));
        }
        m.appendTail(buffer);
        return Prompt.from(buffer.toString());
    }

    public static FStringPromptTemplate from(String template) {
        return new FStringPromptTemplate(template);
    }
}
