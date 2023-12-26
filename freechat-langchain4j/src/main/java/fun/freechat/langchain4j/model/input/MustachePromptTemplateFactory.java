package fun.freechat.langchain4j.model.input;

import com.samskivert.mustache.Mustache;
import dev.langchain4j.spi.prompt.PromptTemplateFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.StringWriter;
import java.util.Map;

@SuppressWarnings("unused")
public class MustachePromptTemplateFactory implements PromptTemplateFactory {
    @Override
    public Template create(PromptTemplateFactory.Input input) {
        if (StringUtils.isBlank(input.getTemplate())) {
            return null;
        }
        com.samskivert.mustache.Template template = Mustache.compiler()
                .defaultValue("")
                .emptyStringIsFalse(true)
                .compile(input.getTemplate());
        return new MustacheTemplate(template);
    }

    static class MustacheTemplate implements Template {
        private final com.samskivert.mustache.Template template;

        MustacheTemplate(com.samskivert.mustache.Template template) {
            this.template = template;
        }

        public String render(Map<String, Object> vars) {
            StringWriter writer = new StringWriter();
            template.execute(vars, writer);
            return writer.toString();
        }
    }
}