package fun.freechat.api.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fun.freechat.api.dto.*;
import fun.freechat.api.dto.conf.CharacterConfigurationDTO;
import fun.freechat.model.PromptTask;
import fun.freechat.service.enums.ChatVar;
import fun.freechat.service.skills.CharacterSkillConfiguration;
import fun.freechat.service.util.InfoUtils;
import fun.freechat.util.HttpUtils;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class CharacterTransferUtils {
    public static final int MAX_BACKENDS = 32;

    private CharacterTransferUtils() {}

    public static ResponseStatusException invalid() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid character configuration");
    }

    public static Map<String, Object> customVariables(Map<String, Object> variables) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (variables != null) {
            variables.forEach((name, value) -> {
                if (ChatVar.of(name) == ChatVar.UNKNOWN) {
                    result.put(name, value);
                }
            });
        }
        return result;
    }

    public static Map<String, Object> object(String json) {
        if (StringUtils.isBlank(json)) return Map.of();
        try {
            return InfoUtils.defaultMapper().readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (IOException | IllegalArgumentException e) {
            throw invalid();
        }
    }

    private static String json(Object value) {
        try {
            return InfoUtils.defaultMapper().writeValueAsString(value);
        } catch (IOException e) {
            throw invalid();
        }
    }

    public static void sanitize(CharacterCreateDTO character) {
        character.setDraft(null);
        character.setParentUid(null);
        character.setVisibility("private");
        character.setExt(CharacterSkillConfiguration.withoutSkills(character.getExt()));
    }

    public static void sanitize(CharacterBackendDTO backend) {
        backend.setChatPromptTaskId(null);
        backend.setGreetingPromptTaskId(null);
        backend.setModerationApiKeyValue(null);
        backend.setImageApiKeyValue(null);
        backend.setModerationApiKeyName(null);
        backend.setImageApiKeyName(null);
        backend.setTgBotId(null);
        backend.setTgBotToken(null);
    }

    public static void sanitize(PromptCreateDTO prompt) {
        if (prompt == null) return;
        prompt.setDraft(null);
        prompt.setParentUid(null);
        prompt.setVisibility("private");
        if (StringUtils.isNotBlank(prompt.getInputs())) {
            prompt.setInputs(json(customVariables(object(prompt.getInputs()))));
        }
    }

    public static void sanitize(PromptTaskDTO task) {
        if (task == null) return;
        task.setApiKeyValue(null);
        task.setApiKeyName(null);
        task.setStatus(null);
        task.setCron(null);
        task.setPromptRef(PromptRefDTO.from(
                null,
                customVariables(
                        task.getPromptRef() == null ? null : task.getPromptRef().getVariables()),
                null));
    }

    public static PromptTaskDTO exportTask(PromptTask task) {
        if (task == null) return null;
        PromptTaskDTO result = PromptTaskDTO.builder()
                .modelId(task.getModelId())
                .params(object(task.getParams()))
                .promptRef(PromptRefDTO.from(null, customVariables(object(task.getVariables())), null))
                .build();
        return result;
    }

    public static PromptTask importTask(PromptTaskDTO task, String promptUid) {
        sanitize(task);
        return new PromptTask()
                .withPromptUid(promptUid)
                .withModelId(task.getModelId())
                .withParams(json(task.getParams()))
                .withVariables(json(task.getPromptRef().getVariables()))
                .withDraft((byte) 0);
    }

    public static Path resource(Path root, String name) {
        if (name == null
                || name.indexOf('\0') >= 0
                || name.contains("\\")
                || name.startsWith("/")
                || !name.matches("(?:avatar|pictures|videos|documents)/[^/]+|voices/(?:[0-9]+/)?[^/]+")) {
            throw invalid();
        }
        Path path = root.resolve(name).normalize();
        if (!path.startsWith(root) || !Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
            throw invalid();
        }
        return path;
    }

    public static void validate(CharacterConfigurationDTO conf, Path root) {
        CharacterCreateDTO character = conf.getCharacter();
        if (character == null
                || StringUtils.isBlank(character.getName())
                || character.getName().length() > 128
                || conf.getBackends() == null
                || conf.getBackends().size() > MAX_BACKENDS) {
            throw invalid();
        }
        sanitize(character);
        choice(character.getGender(), Set.of("male", "female", "other"));
        int defaults = 0;
        for (var backend : conf.getBackends()) {
            if (backend == null || backend.getBackend() == null) throw invalid();
            sanitize(backend.getBackend());
            choice(backend.getBackend().getQuotaType(), Set.of("messages", "tokens", "none"));
            choice(backend.getBackend().getTtsSpeakerType(), Set.of("idx", "wav"));
            CharacterBackendDTO dto = backend.getBackend();
            if (dto.getMessageWindowSize() != null && dto.getMessageWindowSize() <= 0
                    || dto.getLongTermMemoryWindowSize() != null && dto.getLongTermMemoryWindowSize() < 0
                    || dto.getProactiveChatWaitingTime() != null && dto.getProactiveChatWaitingTime() < 0
                    || dto.getInitQuota() != null && dto.getInitQuota() < 0) throw invalid();
            object(backend.getBackend().getModerationParams());
            object(backend.getBackend().getImageParams());
            if (Boolean.TRUE.equals(backend.getBackend().getIsDefault())) defaults++;
            validatePrompt(backend.getChatPrompt(), backend.getChatPromptTask());
            validatePrompt(backend.getGreetingPrompt(), backend.getGreetingPromptTask());
            if (backend.getVoicePath() != null) {
                if (!backend.getVoicePath().startsWith("voices/")) throw invalid();
                resource(root, backend.getVoicePath());
            }
        }
        if (defaults > 1) throw invalid();
        if (conf.getResourceUrls() != null) {
            if (conf.getResourceUrls().size() > 4096) throw invalid();
            Set<String> urls = new HashSet<>();
            conf.getResourceUrls().forEach((path, references) -> {
                resource(root, path);
                if (!path.matches("(?:avatar|pictures|videos)/[^/]+") || references == null || references.size() > 16) {
                    throw invalid();
                }
                for (String reference : references) {
                    if (StringUtils.isBlank(reference) || reference.length() > 2048 || !urls.add(reference))
                        throw invalid();
                    try {
                        URI uri = URI.create(reference);
                        if (!Set.of("http", "https").contains(uri.getScheme())
                                || uri.getHost() == null
                                || uri.getUserInfo() != null) {
                            throw invalid();
                        }
                    } catch (IllegalArgumentException e) {
                        throw invalid();
                    }
                }
            });
        }
        if (conf.getRagTasks() != null) {
            if (conf.getRagTasks().size() > 4096) throw invalid();
            for (RagTaskDTO task : conf.getRagTasks()) {
                if (task == null || StringUtils.isBlank(task.getSource())) throw invalid();
                if ("file".equals(task.getSourceType())) {
                    if (!task.getSource().startsWith("documents/")) throw invalid();
                    resource(root, task.getSource());
                } else if (!"url".equals(task.getSourceType()) || !HttpUtils.isSafeUrl(task.getSource())) {
                    throw invalid();
                }
                if (task.getMaxSegmentSize() != null && task.getMaxSegmentSize() <= 0
                        || task.getMaxOverlapSize() != null && task.getMaxOverlapSize() < 0
                        || task.getMaxSegmentSize() != null
                                && task.getMaxOverlapSize() != null
                                && task.getMaxOverlapSize() >= task.getMaxSegmentSize()) throw invalid();
            }
        }
    }

    private static void validatePrompt(PromptCreateDTO prompt, PromptTaskDTO task) {
        if (prompt == null && task == null) return;
        if (prompt == null
                || task == null
                || StringUtils.isBlank(prompt.getName())
                || prompt.getName().length() > 128
                || StringUtils.isBlank(task.getModelId())
                || StringUtils.isBlank(prompt.getTemplate()) && prompt.getChatTemplate() == null) throw invalid();
        sanitize(prompt);
        sanitize(task);
        choice(prompt.getFormat(), Set.of("mustache", "f_string"));
        object(prompt.getExt());
        try {
            if (prompt.getChatTemplate() != null) prompt.getChatTemplate().toChatPromptContent();
        } catch (IllegalArgumentException | NullPointerException e) {
            throw invalid();
        }
    }

    private static void choice(String value, Set<String> choices) {
        if (value != null && !choices.contains(value)) throw invalid();
    }

    public static CharacterConfigurationDTO remapUrls(CharacterConfigurationDTO conf, Map<String, String> mapping) {
        JsonNode root = InfoUtils.defaultMapper().valueToTree(conf);
        replace(root, mapping);
        return CharacterConfigurationDTO.fromJson(root.toString());
    }

    private static void replace(JsonNode node, Map<String, String> mapping) {
        if (node instanceof ObjectNode object) {
            var fields = new ArrayList<String>();
            object.fieldNames().forEachRemaining(fields::add);
            for (String field : fields) {
                JsonNode value = object.get(field);
                if (value.isTextual()) object.put(field, replace(value.textValue(), mapping));
                else replace(value, mapping);
            }
        } else if (node instanceof ArrayNode array) {
            for (int i = 0; i < array.size(); i++) {
                JsonNode value = array.get(i);
                if (value.isTextual()) array.set(i, array.textNode(replace(value.textValue(), mapping)));
                else replace(value, mapping);
            }
        }
    }

    private static String replace(String value, Map<String, String> mapping) {
        for (var entry : mapping.entrySet()) value = value.replace(entry.getKey(), entry.getValue());
        return value;
    }
}
