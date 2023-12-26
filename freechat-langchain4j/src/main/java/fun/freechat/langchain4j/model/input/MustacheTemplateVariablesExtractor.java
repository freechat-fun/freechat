package fun.freechat.langchain4j.model.input;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.jetbrains.annotations.NotNull;

import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class MustacheTemplateVariablesExtractor {
    public static Set<String> extractTemplateVariables(String templateContent) {
        LinkedHashSet<String> variables = new LinkedHashSet<>();

        Object context = new Object() {
            public Object get(String name) {
                variables.add(name);
                return "";
            }
        };

        Template template = Mustache.compiler().compile(templateContent);
        template.execute(context, new NullWriter());

        return variables;
    }

    private static class NullWriter extends Writer {
        @Override public void write(@NotNull char[] buf, int off, int len) {}
        @Override public void flush() {}
        @Override public void close() {}
    }
}
