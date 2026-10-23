package fun.freechat.service.tools;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;

class ChatToolsConfigurationTest {
    @Test
    void springWiresCatalogButNeverInstallsAWorkspaceRuntime() {
        try (var application = application(Map.of())) {
            ToolRegistry registry = application.getBean(ToolRegistry.class);
            assertEquals(17, registry.list().size());
            assertEquals(
                    7,
                    registry.list().stream()
                            .filter(tool -> tool.boundary() == ToolDefinition.Boundary.ISOLATED)
                            .count());
            assertEquals(
                    Set.of("inProcessToolRuntime"),
                    application.getBeansOfType(ToolRuntime.class).keySet());
            var selected = application.getBean(ToolRuntimeBroker.class).bind(context(), Map.of());
            assertEquals(
                    Set.of("calculate", "get_current_time", "roll_dice"),
                    selected.keySet().stream()
                            .map(specification -> specification.name())
                            .collect(Collectors.toSet()));
            assertFalse(application
                    .getBean(InProcessToolRuntime.class)
                    .supports(registry.find("run_shell_command").orElseThrow(), context()));
        }
    }

    @Test
    void deploymentAllowlistRestrictsCharacterSelection() {
        try (var application = application(Map.of("chat.tools.allowed", "calculate"))) {
            var selected = application.getBean(ToolRuntimeBroker.class).bind(context(), Map.of());
            assertEquals(
                    Set.of("calculate"),
                    selected.keySet().stream()
                            .map(specification -> specification.name())
                            .collect(Collectors.toSet()));
        }
    }

    private static AnnotationConfigApplicationContext application(Map<String, Object> settings) {
        var application = new AnnotationConfigApplicationContext();
        application.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test-tools", settings));
        application.registerBean(
                ToolPolicySource.class,
                () -> ignored -> new ToolPolicySource.Policy(
                        "user",
                        "character",
                        "backend",
                        Set.of(
                                "calculate",
                                "get_current_time",
                                "roll_dice",
                                "read_file",
                                "run_shell_command",
                                "web_fetch",
                                "web_search"),
                        true,
                        true));
        application.register(ChatToolsConfiguration.class);
        application.refresh();
        return application;
    }

    private static ToolContext context() {
        return new ToolContext("chat", "user", "character", "backend", Duration.ofSeconds(10));
    }
}
