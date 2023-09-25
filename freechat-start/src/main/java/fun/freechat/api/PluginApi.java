package fun.freechat.api;

import fun.freechat.api.dto.*;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.service.plugin.PluginFetchService;
import fun.freechat.service.plugin.PluginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Controller
@Tag(name = "Plugin")
@RequestMapping("/api/v1/plugin")
@ResponseBody
@Validated
@SuppressWarnings("unused")
public class PluginApi {
    @Autowired
    private PluginService pluginService;

    @Autowired
    private PluginFetchService pluginFetchService;

    @Operation(
            operationId = "searchPluginSummary",
            summary = "Search Plugin Summary",
            description = """
                    Search plugins:
                    - Specifiable query fields, and relationship:
                      - Scope: private, public_org or public. Private can only search this account.
                      - Username: exact match, only valid when searching public, public_org. If not specified, search all users.
                      - Plugin information format: currently supported: dash_scope, open_ai.
                      - Interface information format: currently supported: openapi_v3.
                      - Tags: exact match (support and, or logic).
                      - Model type: exact match (support and, or logic).
                      - Name: left match.
                      - Provider: left match.
                      - General: name, provider information, manifest (real-time pull mode is not currently supported), fuzzy match, one hit is enough; public scope + all user's general search does not guarantee timeliness.
                    - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending.
                    - The search result is the plugin summary content.
                    - Support pagination.
                    """
    )
    @PostMapping("/search")
    public List<PluginSummaryDTO> search(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Query conditions",
                    content = @Content(
                            examples = @ExampleObject("""
                                    {
                                      "where": {
                                        "visibility": "public",
                                        "username": "amin",
                                        "name": "Second Test",
                                        "provider": "freechat.fun",
                                        "text": "demo",
                                        "tags": ["test2"]
                                      },
                                      "orderBy": ["modifyTime asc"],
                                      "pageNum": 0,
                                      "pageSize": 1
                                    }
                                    """
                            )
                    )
            )
            @RequestBody
            @NotNull
            PluginQueryDTO query) {
        return pluginService.search(query.toPluginInfoQuery(), AccountUtils.currentUser())
                .stream()
                .map(PluginSummaryDTO::fromPluginInfo)
                .toList();
    }

    @Operation(
            operationId = "searchPluginDetails",
            summary = "Search Plugin Details",
            description = "Same as /api/v1/plugin/search, but returns detailed information of the plugin."
    )
    @PostMapping("/details/search")
    public List<PluginDetailsDTO> detailsSearch(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Query conditions",
                    content = @Content(
                            examples = @ExampleObject("""
                                    {
                                      "where": {
                                        "visibility": "public",
                                        "username": "amin",
                                        "name": "Second Test",
                                        "provider": "freechat.fun",
                                        "tags": ["test2"]
                                      },
                                      "orderBy": ["modifyTime asc"],
                                      "pageNum": 0,
                                      "pageSize": 1
                                    }
                                    """
                            )
                    )
            )
            @RequestBody
            @NotNull
            PluginQueryDTO query) {
        return pluginService.searchDetails(query.toPluginInfoQuery(), AccountUtils.currentUser())
                .stream()
                .map(PluginDetailsDTO::fromPluginInfo)
                .toList();
    }

    @Operation(
            operationId = "batchSearchPluginSummary",
            summary = "Batch Search Plugin Summaries",
            description = "Batch call shortcut for /api/v1/plugin/search."
    )
    @PostMapping("/batch/search")
    public List<List<PluginSummaryDTO>> batchSearch(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Query conditions",
                    content = @Content(
                            examples = @ExampleObject("""
                                    [{
                                      "where": {
                                        "visibility": "public",
                                        "username": "amin",
                                        "text": "demo"
                                      },
                                      "orderBy": ["modifyTime asc"],
                                      "pageNum": 1,
                                      "pageSize": 1
                                    },
                                    {
                                      "where": {
                                        "visibility": "private",
                                        "name": "A Test"
                                      }
                                    },
                                    {
                                      "where": {
                                        "visibility": "private",
                                        "tags": ["test1"]
                                      }
                                    },
                                    {
                                      "where": {
                                        "visibility": "public",
                                        "username": "amin",
                                        "name": "Test",
                                        "provider": "freechat.fun",
                                        "text": "demo",
                                        "manifestFormat": "dash_scope",
                                        "apiFormat": "openapi_v3",
                                        "tags": ["test"],
                                        "aiModels": ["123"]
                                      },
                                      "orderBy": ["modifyTime asc"],
                                      "pageNum": 0,
                                      "pageSize": 1
                                    }]
                                    """
                            )
                    )
            )
            @RequestBody
            @NotNull
            List<PluginQueryDTO> queries) {
        List<List<PluginSummaryDTO>> results = new ArrayList<>(queries.size());
        for (PluginQueryDTO query : queries) {
            results.add(search(query));
        }
        return results;
    }

    @Operation(
            operationId = "batchSearchPluginDetails",
            summary = "Batch Search Plugin Details",
            description = "Batch call shortcut for /api/v1/plugin/details/search."
    )
    @PostMapping("/batch/details/search")
    public List<List<PluginDetailsDTO>> batchDetailsSearch(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Query conditions",
                    content = @Content(
                            examples = @ExampleObject("""
                                    [{
                                      "where": {
                                        "visibility": "public",
                                        "username": "amin",
                                        "text": "demo"
                                      },
                                      "orderBy": ["modifyTime asc"],
                                      "pageNum": 1,
                                      "pageSize": 1
                                    },
                                    {
                                      "where": {
                                        "visibility": "private",
                                        "name": "Test"
                                      }
                                    },
                                    {
                                      "where": {
                                        "visibility": "private",
                                        "tags": ["test1"]
                                      }
                                    },
                                    {
                                      "where": {
                                        "visibility": "public",
                                        "username": "amin",
                                        "name": "Test",
                                        "provider": "freechat.fun",
                                        "text": "demo",
                                        "manifestFormat": "dash_scope",
                                        "apiFormat": "openapi_v3",
                                        "tags": ["test"],
                                        "aiModels": ["123"]
                                      },
                                      "orderBy": ["modifyTime asc"],
                                      "pageNum": 0,
                                      "pageSize": 1
                                    }]
                                    """
                            )
                    )
            )
            @RequestBody
            @NotNull
            List<PluginQueryDTO> queries) {
        List<List<PluginDetailsDTO>> results = new ArrayList<>(queries.size());
        for (PluginQueryDTO query : queries) {
            results.add(detailsSearch(query));
        }
        return results;
    }

    @Operation(
            operationId = "createPlugin",
            summary = "Create Plugin",
            description = """
                    Create a plugin, required fields:
                    - Plugin name
                    - Plugin manifestInfo (URL or JSON)
                    - Plugin apiInfo (URL or JSON)
                   
                    Limitations:
                    - Name: 100 characters
                    - Example: 2000 characters
                    - Tags: 5
                    """
    )
    @PostMapping(value = "", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasPermission(#p0.visibility, 'pluginCreateOp')")
    public String create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Information of the plugin to be created",
                    content = @Content(
                            examples = @ExampleObject("""
                                    {
                                      "name": "Test plugin",
                                      "provider": "freechat.fun NLP Lab",
                                      "manifestInfo": "http://127.0.0.1:8080/public/test/plugin/demo/.well-known/ai-plugin.json",
                                      "apiInfo": "http://127.0.0.1:8080/public/test/plugin/demo/.well-known/api-docs.json",
                                      "tags": [
                                        "test", "demo"
                                      ]
                                    }
                                    """
                            )
                    )
            )
            @RequestBody
            @NotNull
            PluginCreateDTO plugin) {
        var pluginInfo = plugin.toPluginInfo();
        if (!pluginService.create(pluginInfo)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create plugin.");
        }
        return pluginInfo.getLeft().getPluginId();
    }

    @Operation(
            operationId = "createPlugins",
            summary = "Batch Create Plugins",
            description = "Batch create multiple plugins. Ensure transactionality, return the pluginId list after success."
    )
    @PostMapping("/batch")
    @PreFilter("hasPermission(filterObject.visibility, 'pluginCreateOp')")
    public List<String> batchCreate(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "List of plugin information to be created",
                    content = @Content(
                            examples = @ExampleObject("""
                                    [{
                                      "name": "First Test Plugin",
                                      "provider": "freechat.fun NLP Lab",
                                      "manifestInfo": "https://freechat.fun/public/test/plugin/demo/.well-known/ai-plugin.json",
                                      "apiInfo": "https://freechat.fun/public/test/plugin/demo/.well-known/api-docs.json",
                                      "tags": [
                                        "test1", "demo1"
                                      ]
                                    },
                                    {
                                      "name": "Second Test Plugin",
                                      "visibility": "private",
                                      "manifestInfo": "https://freechat.fun/public/test/plugin/demo/.well-known/ai-plugin.json",
                                      "apiInfo": "https://freechat.fun/public/test/plugin/demo/.well-known/api-docs.json",
                                      "tags": [
                                        "test2", "demo2"
                                      ],
                                      "aiModels": [
                                        "123", "456"
                                      ]
                                    }]
                                    """
                            )
                    )
            )
            @RequestBody
            @NotEmpty
            List<PluginCreateDTO> plugins) {
        var pluginInfoList = plugins.stream()
                .map(PluginCreateDTO::toPluginInfo)
                .toList();
        return pluginService.create(pluginInfoList);
    }

    @Operation(
            operationId = "updatePlugin",
            summary = "Update Plugin",
            description = "Update plugin, refer to /api/v1/plugin/create, required field: pluginId. Returns success or failure."
    )
    @PutMapping("/{pluginId}")
    @PreAuthorize("hasPermission(#p0 + '|' + #p1.visibility, 'pluginUpdateOp')")
    public Boolean update(
            @Parameter(description = "The pluginId to be updated") @PathVariable("pluginId") @NotBlank
            String pluginId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The plugin information to be updated",
                    content = @Content(
                            examples = @ExampleObject("""
                                    {
                                      "name": "Second Test Plugin (New)",
                                      "visibility": "public",
                                      "tags": [
                                        "test2", "demo2", "business"
                                      ]
                                    }
                                    """
                            )
                    )
            )
            @RequestBody
            @NotNull
            PluginUpdateDTO plugin) {
        return pluginService.update(plugin.toPluginInfo(pluginId));
    }

    @Operation(
            operationId = "deletePlugin",
            summary = "Delete Plugin",
            description = "Delete plugin. Returns success or failure."
    )
    @DeleteMapping("/{pluginId}")
    @PreAuthorize("hasPermission(#p0, 'pluginDeleteOp')")
    public Boolean delete(
            @Parameter(description = "The pluginId to be deleted") @PathVariable("pluginId") @NotBlank
            String pluginId) {
        return pluginService.delete(pluginId, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "deletePlugins",
            summary = "Batch Delete Plugins",
            description = "Delete multiple plugins. Ensure transactionality, return the list of successfully deleted pluginIds."
    )
    @DeleteMapping("/batch")
    @PreFilter("hasPermission(filterObject, 'pluginDeleteOp')")
    public List<String> batchDelete(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "List of pluginIds to be deleted")
            @RequestBody
            @NotEmpty
            List<String> pluginIds) {
        return pluginService.delete(pluginIds, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "getPluginSummary",
            summary = "Get Plugin Summary",
            description = "Get plugin summary information."
    )
    @GetMapping("/summary/{pluginId}")
    public PluginSummaryDTO summary(
            @Parameter(description = "PluginId to be obtained") @PathVariable("pluginId") @NotBlank
            String pluginId) {
        var pluginInfo = pluginService.summary(pluginId, AccountUtils.currentUser());
        return PluginSummaryDTO.fromPluginInfo(pluginInfo);
    }

    @Operation(
            operationId = "getPluginDetails",
            summary = "Get Plugin Details",
            description = "Get plugin detailed information."
    )
    @GetMapping("/details/{pluginId}")
    public PluginDetailsDTO details(
            @Parameter(description = "PluginId to be obtained") @PathVariable("pluginId") @NotBlank
            String pluginId) {
        var pluginInfo = pluginService.details(pluginId, AccountUtils.currentUser());
        return PluginDetailsDTO.fromPluginInfo(pluginInfo);
    }

    @Operation(
            operationId = "countPlugins",
            summary = "Calculate Number of Plugins",
            description = "Calculate the number of plugins according to the specified query conditions."
    )
    @PostMapping("/count")
    public Long count(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Query conditions")
            @RequestBody
            @NotNull
            PluginQueryDTO query) {
        PluginService.Query infoQuery = query.toPluginInfoQuery();
        infoQuery.setOffset(null);
        infoQuery.setLimit(null);
        infoQuery.setOrderBy(null);
        return pluginService.count(infoQuery, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "refreshPluginInfo",
            summary = "Refresh Plugin Information",
            description = "For online manifest, api-docs information provided at the time of entry, this interface can immediately refresh the information in the system cache (default cache time is 1 hour). Generally, there is no need to call, unless you know that the corresponding plugin platform has just updated the interface, and the business side wants to get the latest information immediately, then call this interface to delete the system cache."
    )
    @PutMapping("/refresh/{pluginId}")
    public void refresh(
            @Parameter(description = "The pluginId to be fetched") @PathVariable("pluginId") @NotBlank
            String pluginId) {
        var pluginInfo = pluginService.summary(pluginId, AccountUtils.currentUser());
        pluginFetchService.clearCaches(pluginInfo.getLeft());
    }
}
