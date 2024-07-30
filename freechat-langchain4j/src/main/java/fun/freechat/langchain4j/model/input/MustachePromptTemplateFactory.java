package fun.freechat.langchain4j.model.input;

import com.samskivert.mustache.Mustache;
import dev.langchain4j.spi.prompt.PromptTemplateFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.StringWriter;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class MustachePromptTemplateFactory implements PromptTemplateFactory {
    @Override
    public Template create(PromptTemplateFactory.Input input) {
        if (StringUtils.isBlank(input.getTemplate())) {
            return null;
        }
        return new MustacheTemplate(() -> Mustache.compiler()
                .defaultValue("")
                .emptyStringIsFalse(true)
                .compile(input.getTemplate()));
    }

    static class MustacheTemplate implements Template {
        private final Supplier<com.samskivert.mustache.Template> templateProvider;
        private com.samskivert.mustache.Template template;

        MustacheTemplate(Supplier<com.samskivert.mustache.Template> templateProvider) {
            this.templateProvider = templateProvider;
        }

        private com.samskivert.mustache.Template template() {
            if (template == null) {
                synchronized (this) {
                    if (template == null) {
                        template = templateProvider.get();
                    }
                }
            }
            return template;
        }

        public String render(Map<String, Object> vars) {
            StringWriter writer = new StringWriter();
            template().execute(vars, writer);
            return writer.toString();
        }
    }
}