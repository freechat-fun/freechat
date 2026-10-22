package fun.freechat.service.tools;

import static org.junit.jupiter.api.Assertions.*;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import fun.freechat.service.tools.builtin.RoleplayTools;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Timeout(10)
class ToolFrameworkTest {
    private final List<BuiltinTool> tools = RoleplayTools.create();
    private final ToolRegistry registry =
            new ToolRegistry(tools.stream().map(BuiltinTool::definition).toList());

    @Test
    void registryHasImmutableDescriptorsAndAtomicNameUniqueness() {
        var descriptor = tools.getFirst().definition();
        assertSame(descriptor, registry.find(descriptor.name()).orElseThrow());
        assertThrows(IllegalArgumentException.class, () -> registry.register(descriptor));
        assertThrows(
                UnsupportedOperationException.class, () -> descriptor.tags().add("isolated"));
        assertThrows(UnsupportedOperationException.class, () -> registry.list().clear());
        assertEquals(
                List.of("roll_dice"),
                registry.list(Set.of("random")).stream()
                        .map(ToolDefinition::name)
                        .toList());
        assertTrue(registry.unregister(descriptor.name()));
        assertFalse(registry.unregister(descriptor.name()));
        CompletableFuture<?>[] registrations = java.util.stream.IntStream.range(0, 10)
                .mapToObj(index -> CompletableFuture.runAsync(() -> {
                    try {
                        registry.register(descriptor);
                    } catch (IllegalArgumentException ignored) {
                    }
                }))
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(registrations).join();
        assertEquals(3, registry.list().size());
    }

    @Test
    void configurationDistinguishesDefaultsEmptyAndExactSelection() {
        assertEquals(
                CharacterToolConfiguration.DEFAULTS,
                CharacterToolConfiguration.parse(null).enabled());
        for (String ext :
                List.of("{}", "{\"theme\":\"night\"}", "{\"tools\":null}", "{\"tools\":{\"enabled\":null}}")) {
            assertEquals(
                    CharacterToolConfiguration.DEFAULTS,
                    CharacterToolConfiguration.parse(ext).enabled());
            assertDoesNotThrow(() -> registry.validateConfiguration(ext));
        }
        assertEquals(
                Set.of(),
                CharacterToolConfiguration.parse("{\"tools\":{\"enabled\":[]}}").enabled());
        assertEquals(
                Set.of("calculate"),
                CharacterToolConfiguration.parse("{\"tools\":{\"enabled\":[\"calculate\"]}}")
                        .enabled());
        registry.validateConfiguration("{\"tools\":{\"enabled\":[\"calculate\"]}}");
        assertThrows(
                IllegalArgumentException.class,
                () -> registry.validateConfiguration("{\"tools\":{\"enabled\":[\"unknown\"]}}"));
        registry.unregister("calculate");
        assertDoesNotThrow(() -> registry.validateConfiguration("{}"));
        assertThrows(
                IllegalArgumentException.class,
                () -> registry.validateConfiguration("{\"tools\":{\"enabled\":[\"calculate\"]}}"));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "null",
                "[]",
                "true",
                "{",
                "{} {}",
                "{\"tools\":[]}",
                "{\"tools\":{\"enabled\":true}}",
                "{\"tools\":{\"enabled\":[1]}}",
                "{\"tools\":{\"enabled\":[\"calculate\",\"calculate\"]}}",
                "{\"tools\":{\"enabled\":[\"../shell\"]}}",
                "{\"tools\":{\"enable\":[]}}",
                "{\"tools\":null,\"tools\":{}}"
            })
    void malformedConfigurationNeverBecomesDefaults(String ext) {
        assertThrows(IllegalArgumentException.class, () -> CharacterToolConfiguration.parse(ext));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "null",
                "[]",
                "{} {}",
                "{\"a\":1,\"a\":2}",
                "{\"expression\":\"1\",\"userId\":\"other\"}",
                "{\"expression\":2}",
                "{\"expression\":\"1\",\"precision\":1.5}"
            })
    void brokerRejectsMalformedOrUnexpectedArguments(String json) {
        ToolRuntimeBroker broker = broker();
        var map = broker.bind(context(), Map.of());
        var entry = map.entrySet().stream()
                .filter(value -> value.getKey().name().equals("calculate"))
                .findFirst()
                .orElseThrow();
        Throwable failure =
                assertThrows(IllegalStateException.class, () -> entry.getValue().execute(call(json), "chat"));
        assertEquals("Chat tool execution failed", failure.getMessage());
        assertNull(failure.getCause());
    }

    @Test
    void runtimeRemovalAndGlobalRevocationInvalidateBoundCalls() {
        ToolRuntimeBroker broker = broker();
        var executor = broker.bind(context(), Map.of()).entrySet().stream()
                .filter(value -> value.getKey().name().equals("calculate"))
                .findFirst()
                .orElseThrow()
                .getValue();
        assertTrue(executor.execute(call("{\"expression\":\"1+2\"}"), "chat").contains("3"));
        broker.setAllowed(Set.of());
        assertThrows(IllegalStateException.class, () -> executor.execute(call("{\"expression\":\"1\"}"), "chat"));
        broker.setAllowed(Set.of("calculate"));
        assertTrue(broker.unregisterRuntime("in-process"));
        assertThrows(IllegalStateException.class, () -> executor.execute(call("{\"expression\":\"1\"}"), "chat"));
        assertTrue(broker.bind(context(), Map.of()).isEmpty());
    }

    @Test
    void policyFailureDoesNotAdvertiseTools() {
        ToolRuntimeBroker broker = new ToolRuntimeBroker(
                registry,
                ignored -> {
                    throw new IllegalStateException("private failure");
                },
                List.of(new InProcessToolRuntime(tools)),
                Set.of("calculate"));
        assertTrue(broker.bind(context(), Map.of()).isEmpty());
    }

    @Test
    void contextBudgetsAndInterruptsAreBounded() {
        ToolContext context = context();
        for (int i = 0; i < 64; i++) {
            context.admit();
        }
        assertThrows(IllegalStateException.class, context::admit);
        assertThrows(IllegalStateException.class, () -> context.recordResult("x".repeat(65_537)));
        assertThrows(IllegalStateException.class, () -> context.recordResult("字".repeat(25_000)));
        for (int i = 0; i < 4; i++) {
            context.recordResult("x".repeat(65_536));
        }
        assertThrows(IllegalStateException.class, () -> context.recordResult("x"));
        Thread.currentThread().interrupt();
        try {
            assertThrows(IllegalStateException.class, context::check);
        } finally {
            Thread.interrupted();
        }
        assertThrows(
                IllegalArgumentException.class,
                () -> new ToolContext("chat", "user", "char", "backend", Duration.ZERO));
    }

    private ToolRuntimeBroker broker() {
        return new ToolRuntimeBroker(
                registry,
                ignored -> new ToolPolicySource.Policy("user", "char", "backend", Set.of("calculate"), false, false),
                List.of(new InProcessToolRuntime(tools)),
                Set.of("calculate"));
    }

    private static ToolContext context() {
        return new ToolContext("chat", "user", "char", "backend", Duration.ofSeconds(10));
    }

    private static ToolExecutionRequest call(String json) {
        return ToolExecutionRequest.builder()
                .id("call")
                .name("calculate")
                .arguments(json)
                .build();
    }
}
