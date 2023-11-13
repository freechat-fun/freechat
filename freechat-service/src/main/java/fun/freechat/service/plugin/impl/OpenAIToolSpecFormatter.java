package fun.freechat.service.plugin.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fun.freechat.model.PluginInfo;
import fun.freechat.service.ai.tool.ToolSpecification;
import fun.freechat.service.ai.tool.ToolParameters;
import fun.freechat.service.ai.tool.ToolProperties;
import fun.freechat.service.enums.ApiFormat;
import fun.freechat.service.enums.ToolSpecFormat;
import fun.freechat.service.plugin.PluginToolSpecFormatService;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.parser.core.models.ParseOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Service
@Slf4j
@SuppressWarnings("unused")
public class OpenAIToolSpecFormatter implements PluginToolSpecFormatService {
    @Override
    public Pair<ToolSpecFormat, String> convert(PluginInfo pluginInfo) {
        try {
            ApiFormat apiFormat = ApiFormat.of(pluginInfo.getApiFormat());
            if (apiFormat != ApiFormat.OPENAPI_V3) {
                throw new NotImplementedException("Only support OpenAPI v3 api docs!");
            }
            return Pair.of(ToolSpecFormat.OPEN_AI,
                    convertOpenApiV3(pluginInfo.getApiInfo(), pluginInfo.getManifestInfo()));
        } catch (Exception e) {
            log.warn("Failed to convert plugin {} (id:{})", pluginInfo.getName(), pluginInfo.getPluginId(), e);
            return Pair.of(ToolSpecFormat.UNKNOWN, "");
        }
    }

    private String mergeStrings(String... strings) {
        return Stream.of(strings)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining("|"));
    }

    @SuppressWarnings("rawtypes")
    private String getType(Schema schema) {
        String type = schema.getType();
        if (StringUtils.isBlank(type)) {
            Set<String> types = schema.getTypes();
            if (CollectionUtils.isNotEmpty(types)) {
                type = types.iterator().next();
            }
        }
        return StringUtils.isNotBlank(type) ? type : "string";
    }

    private ToolParameters getParametersFromBody(OpenAPI openApi, RequestBody body) {
        if (Objects.isNull(body)) {
            return null;
        }

        Content content = body.getContent();
        if (Objects.isNull(content)) {
            return null;
        }

        MediaType mediaType = content.get(MimeTypeUtils.APPLICATION_JSON_VALUE);
        if (Objects.isNull(mediaType)) {
            return null;
        }

        //noinspection rawtypes
        Schema schema = mediaType.getSchema();
        if (Objects.isNull(schema)) {
            return null;
        }

        String ref = schema.get$ref();

        ToolParameters parameters = new ToolParameters();
        parameters.setType("object");
        parameters.setDescription(body.getDescription());
        Object example = mediaType.getExample();
        if (Objects.nonNull(example)) {
            parameters.setExample(mediaType.getExample().toString());
        }
        if (StringUtils.isNotBlank(ref)) {
            Components components = openApi.getComponents();
            if (Objects.isNull(components)) {
                return null;
            }
            String[] componentPaths = ref.split("/");
            String component = componentPaths[componentPaths.length - 1];

            //noinspection rawtypes
            Schema componentSchema = components.getSchemas().get(component);
            if (Objects.isNull(componentSchema)) {
                return null;
            }
            Map<String, Schema<?>> propertiesMap = componentSchema.getProperties();
            if (MapUtils.isEmpty(propertiesMap)) {
                return null;
            }

            HashMap<String, ToolProperties> properties = new HashMap<>();
            boolean required = Objects.nonNull(body.getRequired()) ? body.getRequired() : false;
            for (var entry : propertiesMap.entrySet()) {
                String key = entry.getKey();
                Schema<?> info = entry.getValue();

                ToolProperties infoProperties = new ToolProperties();
                infoProperties.setType(getType(info));
                infoProperties.setDescription(info.getDescription());
                example = info.getExample();
                if (Objects.nonNull(example)) {
                    infoProperties.setExample(example.toString());
                }
                properties.put(key, infoProperties);
                if (required) {
                    parameters.getRequired().add(key);
                }
            }
            parameters.setProperties(properties);
        }
        return parameters;
    }

    private ToolParameters getParameters(OpenAPI openApi, List<Parameter> swaggerParameters) {
        if (CollectionUtils.isEmpty(swaggerParameters)) {
            return null;
        }
        ToolParameters parameters = new ToolParameters();
        parameters.setType("object");
        HashMap<String, ToolProperties> properties = new HashMap<>();
        for (var p : swaggerParameters) {
             ToolProperties infoProperties = new ToolProperties();
             String type = "string";
            //noinspection rawtypes
            Schema schema = p.getSchema();
             if (Objects.nonNull(schema)) {
                 type = getType(schema);
             }
             infoProperties.setType(type);
             infoProperties.setDescription(p.getDescription());
             Object example = p.getExample();
            if (Objects.nonNull(example)) {
                infoProperties.setExample(example.toString());
            }
            properties.put(p.getName(), infoProperties);
            boolean required = Objects.nonNull(p.getRequired()) ? p.getRequired() : false;
            if (required) {
                parameters.getRequired().add(p.getName());
            }
        }
        parameters.setProperties(properties);
        return parameters;
    }

    private ToolParameters mergeParameters(ToolParameters... parameters) {
        var parametersList = Arrays.stream(parameters)
                .filter(Objects::nonNull)
                .toList();
        if (CollectionUtils.isEmpty(parametersList)) {
            return null;
        } else if (parametersList.size() == 1) {
            return parametersList.get(0);
        }

        Map<String, ToolProperties> properties = new HashMap<>();
        List<String> required = new LinkedList<>();
        String description = null;
        String example = null;
        for (var p : parametersList) {
            properties.putAll(p.getProperties());
            required.addAll(p.getRequired());
            description = mergeStrings(description, p.getDescription());
            example = mergeStrings(example, p.getExample());
        }
        ToolParameters allParameters = new ToolParameters();
        allParameters.setType("object");
        allParameters.setProperties(properties);
        allParameters.setRequired(required);
        allParameters.setDescription(description);
        allParameters.setExample(example);
        return allParameters;
    }

    private String convertOpenApiV3(String apiInfo, String manifestInfo) throws JsonProcessingException, NullPointerException {
        ObjectMapper stringMapper = new ObjectMapper()
                .setSerializationInclusion(NON_NULL);
        String openApiDesc = null;
        if (StringUtils.isNotBlank(manifestInfo)) {
            //noinspection rawtypes
            Map manifestInfoMap = stringMapper.readValue(manifestInfo, HashMap.class);
            openApiDesc = (String) manifestInfoMap.get("description_for_model");
        }
        ParseOptions options = new ParseOptions();
        options.setResolve(true);
        OpenAPI openApi = new OpenAPIParser()
                .readContents(apiInfo, null, options)
                .getOpenAPI();
        if (Objects.isNull(openApi)) {
            throw new RuntimeException("Failed to parse api docs: " + apiInfo);
        }
        List<ToolSpecification> resultList = new ArrayList<>();
        Paths paths = openApi.getPaths();
        if (Objects.isNull(paths)) {
            return "[]";
        }
        for (Map.Entry<String, PathItem> entry : paths.entrySet()) {
            PathItem item = entry.getValue();
            String pathDesc = item.getDescription();

            for (Operation op : item.readOperations()) {
                if (Objects.nonNull(op)) {
                    String name = op.getOperationId();
                    if (StringUtils.isBlank(name)) {
                        String prefix = getPrefix(op, item);
                        name = prefix + entry.getKey();
                    }
                    String methodDesc = op.getDescription();

                    ToolSpecification pluginToolInfo = new ToolSpecification();
                    pluginToolInfo.setName(name);
                    pluginToolInfo.setDescription(mergeStrings(openApiDesc, pathDesc, methodDesc));
                    var bodyParameters = getParametersFromBody(openApi, op.getRequestBody());
                    var directParameters = getParameters(openApi, op.getParameters());
                    var allParameters = mergeParameters(bodyParameters, directParameters);
                    pluginToolInfo.setParameters(mergeParameters(bodyParameters, directParameters));
                    resultList.add(pluginToolInfo);
                }
            }
        }
        return stringMapper.writeValueAsString(resultList);
    }

    @NotNull
    private static String getPrefix(Operation op, PathItem item) {
        String prefix;
        if (op == item.getGet()) {
            prefix = "GET ";
        } else if (op == item.getPost()) {
            prefix = "POST ";
        } else if (op == item.getPut()) {
            prefix = "PUT ";
        } else if (op == item.getDelete()) {
            prefix = "DELETE ";
        } else if (op == item.getHead()) {
            prefix = "HEAD ";
        } else if (op == item.getPatch()) {
            prefix = "PATCH ";
        } else if (op == item.getOptions()) {
            prefix = "OPTIONS ";
        } else if (op == item.getTrace()) {
            prefix = "TRACE ";
        } else {
            prefix = "";
        }
        return prefix;
    }
}
