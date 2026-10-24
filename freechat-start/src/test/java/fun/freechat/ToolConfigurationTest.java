package fun.freechat;

import static fun.freechat.BuiltinToolChatTest.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.service.tool.ToolExecutor;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.ChatContextService;
import fun.freechat.service.chat.ChatSession;
import fun.freechat.service.chat.ChatSessionService;
import fun.freechat.service.chat.impl.AlbumTool;
import fun.freechat.service.common.ShortLinkService;
import fun.freechat.service.tools.*;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ToolConfigurationTest {
    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void networkToolsBindCamelCaseSettingsAndEnvironmentVariables(boolean environmentVariables) throws Exception {
        try (var context = new org.springframework.context.annotation.AnnotationConfigApplicationContext()) {
            var sources = context.getEnvironment().getPropertySources();
            sources.remove(org.springframework.core.env.StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME);
            sources.remove(org.springframework.core.env.StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME);
            String endpoint = "https://dashscope.aliyuncs.com/compatible-mode/v1";
            if (environmentVariables) {
                sources.addFirst(new org.springframework.core.env.SystemEnvironmentPropertySource(
                        "systemEnvironment",
                        Map.of(
                                "CHAT_TOOLS_WEBFETCH_ALLOWEDHOSTS", "example.com",
                                "CHAT_TOOLS_WEBSEARCH_ENDPOINT", endpoint,
                                "CHAT_TOOLS_WEBSEARCH_APIKEY", "test-only-key",
                                "CHAT_TOOLS_WEBSEARCH_MODEL", "test-model")));
            } else {
                sources.addFirst(new org.springframework.core.env.MapPropertySource(
                        "explicit-settings",
                        Map.of(
                                "chat.tools.webFetch.allowedHosts", "example.com",
                                "chat.tools.webSearch.endpoint", endpoint,
                                "chat.tools.webSearch.apiKey", "test-only-key",
                                "chat.tools.webSearch.model", "test-model")));
            }
            for (var defaults : new org.springframework.boot.env.YamlPropertySourceLoader()
                    .load(
                            "application-defaults",
                            new org.springframework.core.io.ClassPathResource("application.yml"))) {
                sources.addLast(defaults);
            }
            org.springframework.boot.context.properties.source.ConfigurationPropertySources.attach(
                    context.getEnvironment());
            context.refresh();
            var configuration = context.getAutowireCapableBeanFactory().createBean(ChatToolsConfiguration.class);
            var available =
                    (List<?>) org.springframework.test.util.ReflectionTestUtils.getField(configuration, "available");
            assertNotNull(available);
            assertTrue(available.stream()
                    .anyMatch(tool -> tool instanceof fun.freechat.service.tools.network.WebFetchTool fetch
                            && fetch.configured()));
            assertTrue(available.stream()
                    .anyMatch(tool -> tool instanceof fun.freechat.service.tools.network.WebSearchTool search
                            && search.configured()));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"en", "zh"})
    void imageToolUsesEnglishAnnotationForCatalogAndSessionRegistration(String language) {
        var settings = withSettings().mockMaker(org.mockito.MockMakers.SUBCLASS);
        AlbumTool album = AlbumTool.builder()
                .homeUrl("https://unused.invalid")
                .characterService(mock(CharacterService.class, settings))
                .chatContextService(mock(ChatContextService.class, settings))
                .shortLinkService(mock(ShortLinkService.class, settings))
                .chatSessionService(mock(ChatSessionService.class, settings))
                .lang(language)
                .build();
        ChatSession session =
                ChatSession.builder().objectsWithTools(List.of(album)).build();
        ToolSpecification registered = session.getToolSpecifications().getFirst();
        ToolSpecification catalog = new ChatToolsConfiguration("", "", "", "")
                .toolRegistry()
                .find("find_image")
                .orElseThrow()
                .specification();
        assertEquals(catalog, registered);
        assertEquals(Set.of("find_image"), session.getToolExecutors().keySet());
        assertEquals(Set.of("description"), registered.parameters().properties().keySet());
        assertTrue(registered.description().startsWith("Fetch real-time, context-specific images of a character."));
        assertTrue(registered.description().chars().allMatch(character -> character < 128));
        assertTrue(registered.description().contains("![img](url)"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "{}", "{\"tools\":null}", "{\"tools\":{}}", "{\"tools\":{\"enabled\":null}}"})
    void absentSelectionUsesDefaults(String ext) {
        assertEquals(
                Set.of("calculate", "roll_dice", "get_current_time", "search_memory", "read_memory", "find_image"),
                CharacterToolConfiguration.parse(ext).enabled());
    }

    @ParameterizedTest
    @ValueSource(strings = {"searchMemory", "readMemory", "findAnImage"})
    void previousToolNamesAreNotRegisteredOrAccepted(String name) {
        Fixture f = new Fixture();
        assertTrue(f.registry.find(name).isEmpty());
        String ext = ToolArguments.result(Map.of("tools", Map.of("enabled", List.of(name))));
        assertThrows(IllegalArgumentException.class, () -> f.registry.validateConfiguration(ext));
    }

    @Test
    void explicitSelectionIsExactAndNamesMustBeRegistered() {
        Fixture f = new Fixture();
        assertTrue(CharacterToolConfiguration.parse("{\"tools\":{\"enabled\":[]}}")
                .enabled()
                .isEmpty());
        String selection = "{\"tools\":{\"enabled\":[\"calculate\",\"read_memory\"]}}";
        assertEquals(
                Set.of("calculate", "read_memory"),
                CharacterToolConfiguration.parse(selection).enabled());
        assertDoesNotThrow(() -> f.registry.validateConfiguration(selection));
        assertThrows(
                IllegalArgumentException.class,
                () -> f.registry.validateConfiguration("{\"tools\":{\"enabled\":[\"unknown_tool\"]}}"));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "[]",
                "{\"tools\":[]}",
                "{\"tools\":{\"enabled\":\"calculate\"}}",
                "{\"tools\":{\"enabled\":[1]}}",
                "{\"tools\":{\"enabled\":[\"calculate\",\"calculate\"]}}",
                "{\"tools\":{\"enabled\":[\"../private-credential-sentinel\"]}}",
                "{\"tools\":{\"enabled\":[],\"override\":true}}",
                "{\"tools\":{},\"tools\":{}}"
            })
    void malformedConfigurationFailsClosed(String ext) {
        var error = assertThrows(IllegalArgumentException.class, () -> CharacterToolConfiguration.parse(ext));
        assertFalse(error.getMessage().contains(SECRET));
        assertNull(error.getCause());
    }

    @ParameterizedTest
    @CsvSource({"false,false", "true,false", "false,true", "true,true"})
    void memoryAndAlbumRequireBothCharacterGrantAndBackendPrerequisite(boolean memoryEnabled, boolean albumEnabled) {
        Fixture f = new Fixture();
        f.select(CHAT, "character", DEFAULTS, memoryEnabled, albumEnabled);
        var invocation =
                f.session(CHAT, "character").forInvocation(memory(), f.provider, List.of(new Recall()), 3, () -> {});
        Set<String> expected = new HashSet<>(ROLEPLAY);
        if (memoryEnabled) expected.addAll(Set.of("search_memory", "read_memory"));
        if (albumEnabled) expected.add("find_image");
        assertEquals(expected, names(invocation.getToolSpecifications()));
    }

    @Test
    void domainCandidatesWithoutRegistryDescriptorsAreNotAccepted() {
        Fixture f = new Fixture();
        f.select(CHAT, "character", DEFAULTS, true, true);
        f.registry.unregister("read_memory");
        f.registry.unregister("find_image");
        Recall recall = new Recall();
        var invocation = f.session(CHAT, "character").forInvocation(memory(), f.provider, List.of(recall), 3, () -> {});
        assertEquals(
                Set.of("calculate", "roll_dice", "get_current_time", "search_memory"),
                names(invocation.getToolSpecifications()));
        f.registry.unregister("search_memory");
        sanitized(() -> invocation
                .getToolExecutors()
                .get("search_memory")
                .execute(call("search_memory", "{\"query\":\"past\"}"), CHAT));
        assertEquals(0, recall.calls);
    }

    @Test
    void unavailableIsolationNeverFallsBackToHostOrUnregisteredCandidate() {
        ToolSpecification spec = ToolSpecification.builder()
                .name("isolated_probe")
                .description("Test-only isolated tool")
                .parameters(JsonObjectSchema.builder().build())
                .build();
        ToolRegistry registry = new ToolRegistry(
                List.of(new ToolDefinition(spec, Set.of("workspace"), ToolDefinition.Boundary.ISOLATED, true)));
        ToolRuntimeBroker broker = new ToolRuntimeBroker(
                registry,
                chat -> new ToolPolicySource.Policy("owner", "character", "backend", Set.of(spec.name()), false, false),
                List.of(),
                Set.of(spec.name()));
        ToolContext context = new ToolContext(CHAT, "owner", "character", "backend", Duration.ofMinutes(1));
        AtomicInteger bypasses = new AtomicInteger();
        Map<ToolSpecification, ToolExecutor> candidates = Map.of(spec, (request, id) -> {
            bypasses.incrementAndGet();
            return "must not run";
        });
        assertTrue(broker.bind(context, candidates).isEmpty());
        SpyRuntime host = new SpyRuntime(false);
        broker.registerRuntime(host);
        assertTrue(broker.bind(context, candidates).isEmpty());
        SpyRuntime isolated = new SpyRuntime(true);
        broker.registerRuntime(isolated);
        ToolExecutor bound = broker.bind(context, candidates).get(spec);
        assertNotNull(bound);
        assertEquals("isolated-result", bound.execute(call(spec.name(), "{}"), CHAT));
        assertTrue(broker.unregisterRuntime(isolated.id()));
        sanitized(() -> bound.execute(call(spec.name(), "{}"), CHAT));
        assertTrue(broker.bind(context, candidates).isEmpty());
        assertEquals(0, host.calls);
        assertEquals(1, isolated.calls);
        assertEquals(0, bypasses.get());
    }

    private static final class SpyRuntime implements ToolRuntime {
        final boolean isolated;
        int calls;

        SpyRuntime(boolean isolated) {
            this.isolated = isolated;
        }

        public String id() {
            return isolated ? "isolated" : "host";
        }

        public boolean isolated() {
            return isolated;
        }

        public boolean supports(ToolDefinition tool, ToolContext context) {
            return true;
        }

        public String execute(ToolDefinition tool, JsonNode arguments, ToolContext context) {
            calls++;
            return "isolated-result";
        }
    }
}
