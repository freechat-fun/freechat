package fun.freechat.web;

import fun.freechat.service.cache.LongPeriodCache;
import fun.freechat.util.HttpUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@Slf4j
@SuppressWarnings("unused")
public class DocController {
    private static final String DEFAULT_API_DOCS = "/public/openapi/v3/api-docs/g-all";
    private static final List<String> RENDERERS = List.of("explorer", "redoc", "swagger-ui");
    private static final ObjectMapper API_DOCS_MAPPER = new ObjectMapper();
    private static final Pattern JSON_INDEX_PATTERN = Pattern.compile("\\[(\\d+)\\]");

    @GetMapping("/public/docs/api/switch")
    public String switchRenderer(HttpServletRequest request) {
        String renderer = RENDERERS.getFirst();
        String ref = request.getHeader("Referer");
        if (StringUtils.isNotBlank(ref)) {
            String[] urlComponents = ref.split("[?#]")[0].split("/");
            String oldRenderer = urlComponents[urlComponents.length - 1];
            if (StringUtils.isNotBlank(oldRenderer)) {
                int index = RENDERERS.indexOf(oldRenderer);
                renderer = RENDERERS.get((index + 1) % RENDERERS.size());
            }
        }
        return "redirect:/public/docs/api/" + renderer + "?urls.primaryName=All";
    }

    @GetMapping("/public/docs/api")
    public String apiDocs(Model model) {
        return explorerApi(null, model);
    }

    @GetMapping("/public/docs/api/explorer")
    public String explorerApi(@RequestParam("doc") @Nullable String doc, Model model) {
        if (StringUtils.isBlank(doc)) {
            doc = DEFAULT_API_DOCS;
        }
        return apiDocs(doc, "explorer", model);
    }

    @GetMapping("/public/docs/api/redoc")
    public String reDocApi(@RequestParam("doc") @Nullable String doc, Model model) {
        if (StringUtils.isBlank(doc)) {
            doc = DEFAULT_API_DOCS;
        }
        return apiDocs(doc, "redoc", model);
    }

    private String apiDocs(String docPath, String renderer, Model model) {
        model.addAttribute("docPath", docPath);
        model.addAttribute("renderer", renderer);
        return "api";
    }


    @GetMapping(value = "/public/openapi/v3/api-docs/{lang}/{group}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @LongPeriodCache(keyBy = "'DocController::localizedApiDocs_' + #p1 + '_' + #p2")
    public String localizedApiDocs(
            HttpServletRequest request,
            @PathVariable("lang") @NotBlank String lang,
            @PathVariable("group") @NotBlank String group) {
        String localizedDocPath = "i18n/openapi." + lang + ".properties";
        ClassPathResource resource = new ClassPathResource(localizedDocPath);
        String defaultApiDocsUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath("/public/openapi/v3/api-docs/" + group)
                .build()
                .toString();
        String apiDocs = HttpUtils.get(defaultApiDocsUrl);
        try (InputStream in = resource.getInputStream()) {
            Properties properties = new Properties();
            properties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            JsonNode rootNode = API_DOCS_MAPPER.readTree(apiDocs);
            for (var property : properties.entrySet()) {
                var matchedNodes = getNode(rootNode, (String) property.getKey());
                if (Objects.nonNull(matchedNodes)) {
                    JsonNode node = matchedNodes.getLeft();
                    Object key = matchedNodes.getMiddle();
                    JsonNode value = matchedNodes.getRight();
                    JsonNode replacedValue = replaceValue(value, property);
                    if (Objects.nonNull(replacedValue)) {
                        setNode(node, key, replacedValue);
                    }
                }
            }
            apiDocs = rootNode.toString();

        } catch (IOException e) {
            log.warn("Failed to load {}", localizedDocPath, e);
        }
        return apiDocs;
    }

    private JsonNode replaceValue(JsonNode value, Map.Entry<Object, Object> property) {
        Object propertyValue = property.getValue();
        // type-safe mapping
        return switch(value.getNodeType()) {
            case STRING -> TextNode.valueOf(propertyValue.toString());
            case BOOLEAN -> {
                if (propertyValue instanceof Boolean booleanValue) {
                    yield BooleanNode.valueOf(booleanValue);
                } else {
                    yield null;
                }
            }
            case NUMBER -> {
                switch (propertyValue) {
                    case Integer intValue -> {
                        yield IntNode.valueOf(intValue);
                    }
                    case Float floatValue -> {
                        yield FloatNode.valueOf(floatValue);
                    }
                    case Double doubleValue -> {
                        yield DoubleNode.valueOf(doubleValue);
                    }
                    case Long longValue -> {
                        yield LongNode.valueOf(longValue);
                    }
                    case Short shortValue -> {
                        yield ShortNode.valueOf(shortValue);
                    }
                    case BigInteger bigIntegerValue -> {
                        yield BigIntegerNode.valueOf(bigIntegerValue);
                    }
                    case BigDecimal bigDecimalValue -> {
                        yield DecimalNode.valueOf(bigDecimalValue);
                    }
                    case null, default -> {
                        yield null;
                    }
                }
            }
            default -> null;
        };
    }

    private Triple<JsonNode, Object, JsonNode> getNode(JsonNode node, String path) {
        if (node == null || StringUtils.isBlank(path)) {
            return null;
        }

        String[] parts = path.split("\\.", 2);
        String key = parts[0];
        String subPath = parts.length > 1 ? parts[1] : null;
        if (node.isObject()) {
            JsonNode child = node.get(key);
            if (child == null) {
                return null;
            } else if (StringUtils.isBlank(subPath)) {
                return Triple.of(node, key, child);
            } else {
                return getNode(child, subPath);
            }
        } else if (node.isArray()) {
            Matcher m = JSON_INDEX_PATTERN.matcher(key);
            if (!m.matches()) {
                return null;
            }
            int index = Integer.parseInt(m.group(1));
            JsonNode child = node.get(index);
            if (child == null) {
                return null;
            } else if (StringUtils.isBlank(subPath)) {
                return Triple.of(node, index, child);
            } else {
                return getNode(child, subPath);
            }
        }
        return null;
    }

    private void setNode(JsonNode node, Object key, JsonNode value) {
        if (node.isObject()) {
            ((ObjectNode) node).replace((String) key, value);
        } else if (node.isArray()) {
            ((ArrayNode) node).set((Integer) key, value);
        }
    }
}
