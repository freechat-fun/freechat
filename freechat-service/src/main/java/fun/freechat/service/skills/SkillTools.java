package fun.freechat.service.skills;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import fun.freechat.service.tools.ToolDefinition;
import java.util.List;
import java.util.Set;

public final class SkillTools {
    public static final Set<String> NAMES = Set.of("skill", "read_skill_resource");
    public static final Set<String> SAFE_TOOLS = Set.of(
            "skill",
            "read_skill_resource",
            "get_current_time",
            "calculate",
            "roll_dice",
            "search_memory",
            "read_memory");
    public static final ToolDefinition LOAD = new ToolDefinition(
            ToolSpecification.builder()
                    .name("skill")
                    .description(
                            "Load an enabled prompt-only skill for this invocation. Does not run scripts or grant tool permissions.")
                    .parameters(JsonObjectSchema.builder()
                            .addStringProperty("skillId", "Identifier from the available skill catalog")
                            .required("skillId")
                            .additionalProperties(false)
                            .build())
                    .build(),
            Set.of("skill", "read", "prompt-only"),
            ToolDefinition.Boundary.IN_PROCESS,
            false);
    public static final ToolDefinition READ = new ToolDefinition(
            ToolSpecification.builder()
                    .name("read_skill_resource")
                    .description(
                            "Load a text reference from an already activated skill. Only that skill's validated document names are accepted.")
                    .parameters(JsonObjectSchema.builder()
                            .addStringProperty("skillId", "Activated skill identifier")
                            .addStringProperty("file", "Exact logical document name listed by the skill catalog")
                            .required("skillId", "file")
                            .additionalProperties(false)
                            .build())
                    .build(),
            Set.of("skill", "read", "prompt-only"),
            ToolDefinition.Boundary.IN_PROCESS,
            false);

    private SkillTools() {}

    public static List<ToolDefinition> definitions() {
        return List.of(LOAD, READ);
    }
}
