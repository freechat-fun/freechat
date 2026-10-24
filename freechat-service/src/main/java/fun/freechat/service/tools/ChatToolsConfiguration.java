package fun.freechat.service.tools;

import dev.langchain4j.agent.tool.ToolSpecifications;
import fun.freechat.service.chat.impl.AlbumTool;
import fun.freechat.service.chat.memory.MemoryRecallTools;
import fun.freechat.service.tools.builtin.RoleplayTools;
import fun.freechat.service.tools.network.WebFetchTool;
import fun.freechat.service.tools.network.WebSearchTool;
import fun.freechat.service.tools.workspace.WorkspaceTools;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatToolsConfiguration {
    private static final String DEFAULT_ALLOWED =
            "get_current_time,calculate,roll_dice,search_memory,read_memory,find_image,skill,read_skill_resource,"
                    + "read_file,write_file,edit,list_directory,glob,grep_search,run_shell_command,web_fetch,web_search";
    private final List<BuiltinTool> catalog;
    private final List<BuiltinTool> available;

    public ChatToolsConfiguration(
            @Value("${chat.tools.webSearch.baseUrl:}") String baseUrl,
            @Value("${chat.tools.webSearch.apiKey:}") String apiKey,
            @Value("${chat.tools.webSearch.modelId:}") String modelId) {
        List<BuiltinTool> roleplay = RoleplayTools.create();
        WebFetchTool fetch = new WebFetchTool();
        WebSearchTool search = new WebSearchTool(baseUrl, apiKey, modelId);
        List<BuiltinTool> all = new ArrayList<>(roleplay);
        all.add(fetch);
        all.add(search);
        catalog = List.copyOf(all);
        List<BuiltinTool> ready = new ArrayList<>(roleplay);
        ready.add(fetch);
        if (search.configured()) {
            ready.add(search);
        }
        available = List.copyOf(ready);
    }

    @Bean
    public ToolRegistry toolRegistry() {
        List<ToolDefinition> definitions =
                new ArrayList<>(catalog.stream().map(BuiltinTool::definition).toList());
        definitions.addAll(WorkspaceTools.definitions());
        definitions.addAll(fun.freechat.service.skills.SkillTools.definitions());
        ToolSpecifications.toolSpecificationsFrom(MemoryRecallTools.class)
                .forEach(specification -> definitions.add(new ToolDefinition(
                        specification,
                        Set.of("roleplay", "memory", "read"),
                        ToolDefinition.Boundary.IN_PROCESS,
                        false)));
        ToolSpecifications.toolSpecificationsFrom(AlbumTool.class)
                .forEach(specification -> definitions.add(new ToolDefinition(
                        specification,
                        Set.of("roleplay", "image", "network", "write"),
                        ToolDefinition.Boundary.NETWORK,
                        true)));
        return new ToolRegistry(definitions);
    }

    @Bean
    public InProcessToolRuntime inProcessToolRuntime() {
        return new InProcessToolRuntime(available);
    }

    @Bean
    public ToolRuntimeBroker toolRuntimeBroker(
            ToolRegistry registry,
            ToolPolicySource policies,
            List<ToolRuntime> runtimes,
            @Value("${chat.tools.allowed:" + DEFAULT_ALLOWED + "}") String allowed) {
        return new ToolRuntimeBroker(registry, policies, runtimes, names(allowed));
    }

    private static Set<String> names(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
    }
}
