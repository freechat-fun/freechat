package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;

import fun.freechat.api.dto.CharacterBackendDTO;
import fun.freechat.api.dto.CharacterCreateDTO;
import fun.freechat.api.dto.ChatContentDTO;
import fun.freechat.api.dto.ChatMessageDTO;
import fun.freechat.api.dto.ChatPromptContentDTO;
import fun.freechat.api.dto.PromptCreateDTO;
import fun.freechat.api.dto.PromptRefDTO;
import fun.freechat.api.dto.PromptTaskDTO;
import fun.freechat.api.dto.RagTaskDTO;
import fun.freechat.api.dto.conf.CharacterBackendConfigurationDTO;
import fun.freechat.api.dto.conf.CharacterConfigurationDTO;
import fun.freechat.api.util.CharacterTransferUtils;
import fun.freechat.model.PromptTask;
import fun.freechat.service.enums.ChatVar;
import fun.freechat.service.tools.ToolArguments;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class CharacterTransferUtilsTest {
    private static final String MODEL = "[openai]gpt-4o-mini";
    private static final String PLACEHOLDERS =
            "{{CHARACTER_PROFILE}} {{USER_PROFILE}} {{input}} {{custom}} {USER_NICKNAME} {input} {custom}";
    private static final String SOURCE_IMAGE = "https://source.example/media/portrait.png";
    private static final String TARGET_IMAGE = "https://target.example/media/imported-portrait.png";
    private static final String SOURCE_VIDEO = "https://source.example/media/clip.mp4";
    private static final String TARGET_VIDEO = "https://target.example/media/imported-clip.mp4";

    @TempDir
    Path root;

    @Test
    void customVariablesFiltersEverySystemVariableCaseInsensitivelyWithoutMutatingInput() {
        Map<String, Object> variables = transferredVariables();
        Map<String, Object> before = new LinkedHashMap<>(variables);

        Map<String, Object> result = CharacterTransferUtils.customVariables(variables);

        assertEquals(customVariables(), result);
        assertEquals(before, variables);
        assertNotSame(variables, result);
    }

    @Test
    void customVariablesAcceptsAbsentVariables() {
        assertEquals(Map.of(), CharacterTransferUtils.customVariables(null));
        assertEquals(Map.of(), CharacterTransferUtils.customVariables(Map.of()));
    }

    @Test
    void exportTaskPreservesSourceAndExportsOnlyCustomVariablesModelAndParams() {
        Map<String, Object> params = Map.of("temperature", 0.25, "max_tokens", 64);
        PromptTask source = new PromptTask()
                .withTaskId("foreign-task")
                .withPromptUid("foreign-prompt")
                .withDraft((byte) 1)
                .withModelId(MODEL)
                .withParams(ToolArguments.result(params))
                .withVariables(ToolArguments.result(transferredVariables()))
                .withApiKeyName("foreign-key-name")
                .withApiKeyValue("foreign-key-value")
                .withCron("0 0 * * * ?")
                .withStatus("running")
                .withExt("foreign-extension");
        String before = ToolArguments.result(source);

        PromptTaskDTO exported = CharacterTransferUtils.exportTask(source);

        assertEquals(
                PromptTaskDTO.builder()
                        .modelId(MODEL)
                        .params(params)
                        .promptRef(PromptRefDTO.from(null, customVariables(), null))
                        .build(),
                exported);
        assertEquals(before, ToolArguments.result(source));
    }

    @Test
    void exportTaskHandlesAbsentTaskAndEmptyJsonFields() {
        assertNull(CharacterTransferUtils.exportTask(null));
        PromptTaskDTO exported = CharacterTransferUtils.exportTask(new PromptTask().withModelId(MODEL));
        assertEquals(Map.of(), exported.getParams());
        assertEquals(PromptRefDTO.from(null, Map.of(), null), exported.getPromptRef());
    }

    @Test
    void importTaskIgnoresForeignPromptIdAndDropsSystemValuesCredentialsAndScheduling() {
        Map<String, Object> params = Map.of("temperature", 0.25, "max_tokens", 64);
        PromptTaskDTO transferred = PromptTaskDTO.builder()
                .promptRef(PromptRefDTO.from(Long.MAX_VALUE, transferredVariables(), true))
                .modelId(MODEL)
                .params(params)
                .apiKeyName("foreign-key-name")
                .apiKeyValue("foreign-key-value")
                .cron("0 0 * * * ?")
                .status("running")
                .build();

        PromptTask imported = CharacterTransferUtils.importTask(transferred, "imported-prompt");

        assertEquals("imported-prompt", imported.getPromptUid());
        assertEquals(Byte.valueOf((byte) 0), imported.getDraft());
        assertEquals(MODEL, imported.getModelId());
        assertEquals(ToolArguments.parse(ToolArguments.result(params)), ToolArguments.parse(imported.getParams()));
        assertEquals(
                ToolArguments.parse(ToolArguments.result(customVariables())),
                ToolArguments.parse(imported.getVariables()));
        assertNull(imported.getTaskId());
        assertNull(imported.getApiKeyName());
        assertNull(imported.getApiKeyValue());
        assertNull(imported.getCron());
        assertNull(imported.getStatus());
        assertNull(imported.getExt());
        assertEquals(PromptRefDTO.from(null, customVariables(), null), transferred.getPromptRef());
        assertNull(transferred.getApiKeyName());
        assertNull(transferred.getApiKeyValue());
        assertNull(transferred.getCron());
        assertNull(transferred.getStatus());
    }

    @Test
    void importTaskAcceptsAbsentPromptReferenceWithoutResolvingForeignState() {
        PromptTask imported = CharacterTransferUtils.importTask(portableTask(), "imported-prompt");

        assertEquals("imported-prompt", imported.getPromptUid());
        assertEquals(Byte.valueOf((byte) 0), imported.getDraft());
        assertEquals(ToolArguments.parse("{}"), ToolArguments.parse(imported.getVariables()));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(
            strings = {
                "",
                "{not-json",
                "[]",
                "{\"ext\":{\"skills\":{\"enabled\":[\"foreign\"]}}}",
                "{\"inputs\":{\"INPUT\":\"foreign\"},\"parentUid\":\"foreign\",\"visibility\":\"public\"}"
            })
    void sanitizeAlwaysDropsCharacterAndPromptDraftsWithoutParsing(String draft) {
        CharacterCreateDTO character = CharacterCreateDTO.builder()
                .name("Persona")
                .draft(draft)
                .parentUid("foreign-character")
                .visibility("public")
                .profile(PLACEHOLDERS)
                .ext(ToolArguments.result(Map.of("skills", Map.of("enabled", List.of("a".repeat(32))), "custom", 42)))
                .build();
        PromptCreateDTO prompt = validPrompt();
        prompt.setDraft(draft);
        prompt.setParentUid("foreign-prompt");
        prompt.setVisibility("public_org");

        assertDoesNotThrow(() -> CharacterTransferUtils.sanitize(character));
        assertDoesNotThrow(() -> CharacterTransferUtils.sanitize(prompt));

        assertNull(character.getDraft());
        assertNull(character.getParentUid());
        assertEquals("private", character.getVisibility());
        assertEquals(PLACEHOLDERS, character.getProfile());
        assertEquals(ToolArguments.parse("{\"custom\":42}"), ToolArguments.parse(character.getExt()));
        assertNull(prompt.getDraft());
        assertNull(prompt.getParentUid());
        assertEquals("private", prompt.getVisibility());
        assertEquals(PLACEHOLDERS, prompt.getTemplate());
    }

    @ParameterizedTest
    @ValueSource(strings = {"mustache", "f_string"})
    void sanitizePromptRemovesSystemInputsWithoutRenderingEitherTemplate(String format) {
        PromptCreateDTO prompt = validPrompt();
        prompt.setFormat(format);
        prompt.setInputs(ToolArguments.result(transferredVariables()));
        prompt.setChatTemplate(chatTemplate(SOURCE_IMAGE));
        String originalChatTemplate = ToolArguments.result(prompt.getChatTemplate());

        CharacterTransferUtils.sanitize(prompt);

        assertEquals(
                ToolArguments.parse(ToolArguments.result(customVariables())), ToolArguments.parse(prompt.getInputs()));
        assertEquals(PLACEHOLDERS, prompt.getTemplate());
        assertEquals(originalChatTemplate, ToolArguments.result(prompt.getChatTemplate()));
        assertEquals(format, prompt.getFormat());
    }

    @Test
    void sanitizeBackendClearsCredentialsTokensAndForeignIdentifiersButPreservesModelSettings() {
        CharacterBackendDTO backend = CharacterBackendDTO.builder()
                .isDefault(true)
                .chatPromptTaskId("foreign-chat-task")
                .greetingPromptTaskId("foreign-greeting-task")
                .moderationModelId("moderation-model")
                .moderationApiKeyName("moderation-key-name")
                .moderationApiKeyValue("moderation-key-value")
                .moderationParams("{\"threshold\":0.5}")
                .imageModelId("image-model")
                .imageApiKeyName("image-key-name")
                .imageApiKeyValue("image-key-value")
                .imageParams("{\"size\":\"1024x1024\"}")
                .tgBotId(123456L)
                .tgBotToken("123456:foreign-token")
                .messageWindowSize(20)
                .build();

        CharacterTransferUtils.sanitize(backend);

        assertEquals(
                CharacterBackendDTO.builder()
                        .isDefault(true)
                        .moderationModelId("moderation-model")
                        .moderationParams("{\"threshold\":0.5}")
                        .imageModelId("image-model")
                        .imageParams("{\"size\":\"1024x1024\"}")
                        .messageWindowSize(20)
                        .build(),
                backend);
    }

    @Test
    void validateRejectsMissingBackendsWithBadRequest() {
        CharacterConfigurationDTO configuration = validConfiguration();
        configuration.setBackends(null);

        assertBadRequest(() -> CharacterTransferUtils.validate(configuration, root));
    }

    @Test
    void validateRejectsNullBackendEntriesAndMissingBackendDefinitionsWithBadRequest() {
        CharacterConfigurationDTO configuration = validConfiguration();
        configuration.setBackends(Collections.singletonList(null));
        assertBadRequest(() -> CharacterTransferUtils.validate(configuration, root));

        configuration.setBackends(List.of(new CharacterBackendConfigurationDTO()));
        assertBadRequest(() -> CharacterTransferUtils.validate(configuration, root));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("malformedPromptPairs")
    void validateRejectsMalformedChatAndGreetingPromptPairsWithBadRequest(
            String description, PromptCreateDTO prompt, PromptTaskDTO task) {
        CharacterConfigurationDTO configuration = validConfiguration();
        CharacterBackendConfigurationDTO chat = backendConfiguration();
        chat.setChatPrompt(prompt);
        chat.setChatPromptTask(task);
        configuration.setBackends(List.of(chat));
        assertBadRequest(() -> CharacterTransferUtils.validate(configuration, root));

        CharacterBackendConfigurationDTO greeting = backendConfiguration();
        greeting.setGreetingPrompt(prompt);
        greeting.setGreetingPromptTask(task);
        configuration.setBackends(List.of(greeting));
        assertBadRequest(() -> CharacterTransferUtils.validate(configuration, root));
    }

    @Test
    void validateRejectsDuplicateDefaultBackendsWithBadRequest() {
        CharacterConfigurationDTO configuration = validConfiguration();
        CharacterBackendConfigurationDTO first = backendConfiguration();
        CharacterBackendConfigurationDTO second = backendConfiguration();
        first.getBackend().setIsDefault(true);
        second.getBackend().setIsDefault(true);
        configuration.setBackends(List.of(first, second));

        assertBadRequest(() -> CharacterTransferUtils.validate(configuration, root));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "../image.png",
                "pictures/../../image.png",
                "pictures/../avatar/image.png",
                "pictures/./image.png",
                "pictures\\image.png",
                "/pictures/image.png",
                "voices/0/../../image.png",
                "documents/../image.png"
            })
    void validateRejectsMediaTraversalWithBadRequest(String path) throws IOException {
        writeResource("image.png");
        writeResource("avatar/image.png");
        writeResource("pictures/image.png");
        Files.createDirectories(root.resolve("voices/0"));
        Files.createDirectories(root.resolve("documents"));
        CharacterConfigurationDTO configuration = configurationWithMedia(path);

        assertBadRequest(() -> CharacterTransferUtils.validate(configuration, root));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "avatar/missing.png",
                "pictures/missing.png",
                "videos/missing.mp4",
                "voices/0/missing.wav",
                "documents/missing.txt"
            })
    void validateRejectsMissingMediaFilesWithBadRequest(String path) {
        CharacterConfigurationDTO configuration = configurationWithMedia(path);

        assertBadRequest(() -> CharacterTransferUtils.validate(configuration, root));
    }

    @Test
    void validateRejectsNulInMediaPathWithBadRequest() {
        CharacterConfigurationDTO configuration = configurationWithMedia("pictures/\0image.png");

        assertBadRequest(() -> CharacterTransferUtils.validate(configuration, root));
    }

    @ParameterizedTest
    @CsvSource(
            value = {"0, 0", "-1, 0", "16, -1", "16, 16", "16, 17", "null, -1", "0, null", "-1, null"},
            nullValues = "null")
    void validateRejectsInvalidRagParametersWithBadRequest(Integer segmentSize, Integer overlapSize)
            throws IOException {
        writeResource("documents/source.txt");
        CharacterConfigurationDTO configuration = configurationWithMedia("documents/source.txt");
        RagTaskDTO task = configuration.getRagTasks().getFirst();
        task.setMaxSegmentSize(segmentSize);
        task.setMaxOverlapSize(overlapSize);

        assertBadRequest(() -> CharacterTransferUtils.validate(configuration, root));
    }

    @ParameterizedTest
    @CsvSource(
            value = {"16, 0", "16, 15", "16, null", "null, 0", "null, null"},
            nullValues = "null")
    void validateAcceptsLocalMediaAndValidRagParameters(Integer segmentSize, Integer overlapSize) throws IOException {
        writeResource("avatar/image.png");
        writeResource("pictures/image.png");
        writeResource("videos/clip.mp4");
        writeResource("voices/0/sample.wav");
        writeResource("documents/source.txt");
        CharacterConfigurationDTO configuration = configurationWithMedia("voices/0/sample.wav");
        configuration.setResourceUrls(Map.of(
                "avatar/image.png", List.of("https://source.example/avatar.png"),
                "pictures/image.png", List.of(SOURCE_IMAGE),
                "videos/clip.mp4", List.of(SOURCE_VIDEO)));
        configuration.setRagTasks(List.of(RagTaskDTO.builder()
                .sourceType("file")
                .source("documents/source.txt")
                .maxSegmentSize(segmentSize)
                .maxOverlapSize(overlapSize)
                .build()));

        assertDoesNotThrow(() -> CharacterTransferUtils.validate(configuration, root));
    }

    @Test
    void remapUrlsUpdatesCharacterAndPromptResourcesWithoutMutatingSourceOrRenderingPlaceholders() {
        CharacterConfigurationDTO configuration = validConfiguration();
        configuration.getCharacter().setAvatar(SOURCE_IMAGE);
        configuration.getCharacter().setPicture(SOURCE_IMAGE);
        configuration.getCharacter().setVideo(SOURCE_VIDEO);
        configuration.getCharacter().setProfile(PLACEHOLDERS + " ![portrait](" + SOURCE_IMAGE + ")");
        configuration.getCharacter().setExt(ToolArguments.result(resourceVariables(SOURCE_IMAGE, SOURCE_VIDEO)));
        CharacterBackendConfigurationDTO backend = backendConfiguration();
        PromptCreateDTO prompt = validPrompt();
        prompt.setTemplate(PLACEHOLDERS + " " + SOURCE_IMAGE);
        prompt.setChatTemplate(chatTemplate(SOURCE_IMAGE));
        prompt.setInputs(ToolArguments.result(resourceVariables(SOURCE_IMAGE, SOURCE_VIDEO)));
        backend.setChatPrompt(prompt);
        backend.setChatPromptTask(PromptTaskDTO.builder()
                .modelId(MODEL)
                .params(Map.of("reference_image", SOURCE_IMAGE))
                .promptRef(PromptRefDTO.from(null, resourceVariables(SOURCE_IMAGE, SOURCE_VIDEO), null))
                .build());
        PromptCreateDTO greeting = validPrompt();
        greeting.setTemplate(PLACEHOLDERS + " " + SOURCE_VIDEO);
        backend.setGreetingPrompt(greeting);
        backend.setGreetingPromptTask(PromptTaskDTO.builder()
                .modelId(MODEL)
                .promptRef(PromptRefDTO.from(null, Map.of("clip", SOURCE_VIDEO, "template", PLACEHOLDERS), null))
                .build());
        configuration.setBackends(List.of(backend));
        configuration.setResourceUrls(Map.of("pictures/image.png", List.of(SOURCE_IMAGE)));
        String before = configuration.toJson();

        CharacterConfigurationDTO remapped = CharacterTransferUtils.remapUrls(
                configuration, Map.of(SOURCE_IMAGE, TARGET_IMAGE, SOURCE_VIDEO, TARGET_VIDEO));

        assertNotSame(configuration, remapped);
        assertEquals(before, configuration.toJson());
        assertEquals(TARGET_IMAGE, remapped.getCharacter().getAvatar());
        assertEquals(TARGET_IMAGE, remapped.getCharacter().getPicture());
        assertEquals(TARGET_VIDEO, remapped.getCharacter().getVideo());
        assertEquals(
                PLACEHOLDERS + " ![portrait](" + TARGET_IMAGE + ")",
                remapped.getCharacter().getProfile());
        assertEquals(
                ToolArguments.parse(ToolArguments.result(resourceVariables(TARGET_IMAGE, TARGET_VIDEO))),
                ToolArguments.parse(remapped.getCharacter().getExt()));
        CharacterBackendConfigurationDTO remappedBackend =
                remapped.getBackends().getFirst();
        assertEquals(
                PLACEHOLDERS + " " + TARGET_IMAGE,
                remappedBackend.getChatPrompt().getTemplate());
        assertEquals(chatTemplate(TARGET_IMAGE), remappedBackend.getChatPrompt().getChatTemplate());
        assertEquals(
                ToolArguments.parse(ToolArguments.result(resourceVariables(TARGET_IMAGE, TARGET_VIDEO))),
                ToolArguments.parse(remappedBackend.getChatPrompt().getInputs()));
        assertEquals(
                resourceVariables(TARGET_IMAGE, TARGET_VIDEO),
                remappedBackend.getChatPromptTask().getPromptRef().getVariables());
        assertEquals(
                Map.of("reference_image", TARGET_IMAGE),
                remappedBackend.getChatPromptTask().getParams());
        assertEquals(
                PLACEHOLDERS + " " + TARGET_VIDEO,
                remappedBackend.getGreetingPrompt().getTemplate());
        assertEquals(
                Map.of("clip", TARGET_VIDEO, "template", PLACEHOLDERS),
                remappedBackend.getGreetingPromptTask().getPromptRef().getVariables());
        assertEquals(Map.of("pictures/image.png", List.of(TARGET_IMAGE)), remapped.getResourceUrls());
    }

    private static Map<String, Object> customVariables() {
        return Map.of(
                "custom",
                "custom value",
                "input_suffix",
                "ordinary custom variable",
                "template",
                PLACEHOLDERS,
                "nested",
                Map.of("texts", List.of(PLACEHOLDERS), "count", 7));
    }

    private static Map<String, Object> transferredVariables() {
        Map<String, Object> variables = new LinkedHashMap<>(customVariables());
        for (ChatVar variable : ChatVar.values()) {
            if (variable != ChatVar.UNKNOWN) {
                variables.put(variable.name(), "foreign-system-value");
                variables.put(variable.name().toLowerCase(Locale.ROOT), "foreign-system-value");
            }
        }
        variables.put("iNpUt", "foreign-system-value");
        return variables;
    }

    private static PromptCreateDTO validPrompt() {
        return PromptCreateDTO.builder().name("Prompt").template(PLACEHOLDERS).build();
    }

    private static PromptTaskDTO portableTask() {
        return PromptTaskDTO.builder().modelId(MODEL).params(Map.of()).build();
    }

    private static CharacterConfigurationDTO validConfiguration() {
        return CharacterConfigurationDTO.builder()
                .character(CharacterCreateDTO.builder().name("Persona").build())
                .backends(List.of())
                .resourceUrls(Map.of())
                .ragTasks(List.of())
                .build();
    }

    private static CharacterBackendConfigurationDTO backendConfiguration() {
        return CharacterBackendConfigurationDTO.builder()
                .backend(CharacterBackendDTO.builder().isDefault(false).build())
                .build();
    }

    private static Stream<Arguments> malformedPromptPairs() {
        return Stream.of(
                Arguments.of("missing prompt", null, portableTask()),
                Arguments.of("missing task", validPrompt(), null),
                Arguments.of(
                        "missing name",
                        PromptCreateDTO.builder().template(PLACEHOLDERS).build(),
                        portableTask()),
                Arguments.of(
                        "oversized name",
                        PromptCreateDTO.builder()
                                .name("p".repeat(129))
                                .template(PLACEHOLDERS)
                                .build(),
                        portableTask()),
                Arguments.of(
                        "missing templates",
                        PromptCreateDTO.builder().name("Prompt").build(),
                        portableTask()),
                Arguments.of("missing model", validPrompt(), new PromptTaskDTO()),
                Arguments.of(
                        "blank model",
                        validPrompt(),
                        PromptTaskDTO.builder().modelId(" ").build()),
                Arguments.of(
                        "invalid inputs",
                        PromptCreateDTO.builder()
                                .name("Prompt")
                                .template(PLACEHOLDERS)
                                .inputs("{not-json")
                                .build(),
                        portableTask()));
    }

    private static CharacterConfigurationDTO configurationWithMedia(String path) {
        CharacterConfigurationDTO configuration = validConfiguration();
        if (path.startsWith("voices/")) {
            CharacterBackendConfigurationDTO backend = backendConfiguration();
            backend.setVoicePath(path);
            configuration.setBackends(List.of(backend));
        } else if (path.startsWith("documents/")) {
            configuration.setRagTasks(
                    List.of(RagTaskDTO.builder().sourceType("file").source(path).build()));
        } else {
            configuration.setResourceUrls(Map.of(path, List.of(SOURCE_IMAGE)));
        }
        return configuration;
    }

    private void writeResource(String path) throws IOException {
        Path file = root.resolve(path);
        Files.createDirectories(file.getParent());
        Files.writeString(file, "archive fixture");
    }

    private static Map<String, Object> resourceVariables(String image, String video) {
        return Map.of(
                "portrait", image,
                "template", PLACEHOLDERS,
                "nested", Map.of("media", List.of(image, video), "template", PLACEHOLDERS));
    }

    private static ChatPromptContentDTO chatTemplate(String image) {
        ChatMessageDTO message = ChatMessageDTO.builder()
                .role("user")
                .contents(List.of(
                        ChatContentDTO.builder()
                                .type("text")
                                .content(PLACEHOLDERS)
                                .build(),
                        ChatContentDTO.builder().type("image").content(image).build()))
                .build();
        return ChatPromptContentDTO.builder()
                .system(PLACEHOLDERS + " " + image)
                .messageToSend(message)
                .messages(List.of(message))
                .build();
    }

    private static void assertBadRequest(Executable action) {
        ResponseStatusException error = assertThrows(ResponseStatusException.class, action);
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    }
}
