package fun.freechat.service.rag;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fun.freechat.service.util.InfoUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Slf4j
public record RagTaskExt(String message, Throwable ex) {
    @Override
    public String toString() {
        ObjectMapper mapper = InfoUtils.defaultMapper();
        ExtInfo info = new ExtInfo();

        if (StringUtils.isNotBlank(message)) {
            info.setMessage(message);
        }

        if (Objects.nonNull(ex)) {
            StringWriter exInfo = new StringWriter();
            try (PrintWriter writer = new PrintWriter(exInfo)) {
                ex.printStackTrace(writer);
                writer.flush();
                info.setEx(exInfo.toString());
            }
        }

        try {
            return mapper.writeValueAsString(info);
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse extra info to json string!", e);
            return "{}";
        }
    }

    @Data
    @JsonInclude(NON_NULL)
    private static class ExtInfo {
        private String message;
        private String ex;
    }
}
