package fun.freechat.langchain4j.model.input;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;

@SuppressWarnings("unused")
public class FStringTemplateVariablesExtractor {
    public static Set<String> extractTemplateVariables(String templateContent) {
        LinkedHashSet<String> variables = new LinkedHashSet<>();
        Matcher m = FStringPromptTemplate.VAR_PATTERN.matcher(templateContent);
        while (m.find()) {
            variables.add(m.group(3).trim());
        }

        return variables;
    }
}
