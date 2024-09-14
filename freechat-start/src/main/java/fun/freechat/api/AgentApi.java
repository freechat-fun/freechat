package fun.freechat.api;

import fun.freechat.api.dto.*;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.model.AgentInfo;
import fun.freechat.service.agent.AgentService;
import fun.freechat.service.enums.InfoType;
import fun.freechat.service.enums.StatsType;
import fun.freechat.service.enums.Visibility;
import fun.freechat.service.stats.InteractiveStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Controller
@Tag(name = "Agent")
@RequestMapping("/api/v1/agent")
@ResponseBody
@Validated
@SuppressWarnings("unused")
public class AgentApi {
    @Autowired
    private AgentService agentService;
    @Autowired
    private InteractiveStatsService interactiveStatsService;

    private void resetAgentInfo(AgentInfo info, String parentUid) {
        if (StringUtils.isNotBlank(parentUid)) {
            info.setParentUid(parentUid);
            info.setVisibility(Visibility.PRIVATE.text());
        }
        info.setAgentId(null);
        info.setVersion(1);
        info.setUserId(AccountUtils.currentUser().getUserId());
    }

    private void resetAgentInfoTriple(
            Triple<AgentInfo, List<String>, List<String>> infoTriple, String parentUid) {
        resetAgentInfo(infoTriple.getLeft(), parentUid);
    }

    private void increaseReferCount(String agentUid) {
        interactiveStatsService.add(AccountUtils.currentUser().getUserId(),
                InfoType.AGENT, agentUid, StatsType.REFER_COUNT, 1L);
    }

    @Operation(
            operationId = "searchAgentSummary",
            summary = "Search Agent Summary",
            description = """
                    Search agents:
                    - Specifiable query fields, and relationship:
                      - Scope: private, public_org or public. Private can only search this account.
                      - Username: exact match, only valid when searching public, public_org. If not specified, search all users.
                      - Format: exact match, currently supported: langflow
                      - Tags: exact match (support and, or logic).
                      - Model type: exact match (support and, or logic).
                      - Name: left match.
                      - General: name, description, example, fuzzy match, one hit is enough; public scope + all user's general search does not guarantee timeliness.
                    - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending.
                    - The search result is the agent summary content.
                    - Support pagination.
                    """
    )
    @PostMapping("/search")
    public List<AgentSummaryDTO> search(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Query conditions",
                    content = @Content(
                            examples = @ExampleObject("""
                                    {
                                      "where": {
                                        "format": "langflow",
                                        "visibility": "public",
                                        "username": "amin",
                                        "name": "Second Test",
                                        "text": "(new)",
                                        "tags": ["demo2"],
                                        "aiModels": ["123"]
                                      },
                                      "orderBy": ["version", "modifyTime asc"],
                                      "pageNum": 0,
                                      "pageSize": 1
                                    }
                                    """
                            )
                    )
            )
            @RequestBody
            @NotNull
            AgentQueryDTO query) {
        return agentService.search(query.toAgentInfoQuery(), AccountUtils.currentUser())
                .stream()
                .map(AgentSummaryDTO::from)
                .toList();
    }

    @Operation(
            operationId = "searchAgentDetails",
            summary = "Search Agent Details",
            description = "Same as /api/v1/agent/search, but returns detailed information of the agent."
    )
    @PostMapping("/details/search")
    public List<AgentDetailsDTO> detailsSearch(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Query conditions",
                    content = @Content(
                            examples = @ExampleObject("""
                                    {
                                      "where": {
                                        "format": "langflow",
                                        "visibility": "public",
                                        "username": "amin",
                                        "name": "Second Test",
                                        "text": "(new)",
                                        "tags": ["demo2"],
                                        "aiModels": ["123"]
                                      },
                                      "orderBy": ["version", "modifyTime asc"],
                                      "pageNum": 0,
                                      "pageSize": 1
                                    }
                                    """
                            )
                    )
            )
            @RequestBody
            @NotNull
            AgentQueryDTO query) {
        return agentService.searchDetails(query.toAgentInfoQuery(), AccountUtils.currentUser())
                .stream()
                .map(AgentDetailsDTO::from)
                .toList();
    }


    @Operation(
            operationId = "batchSearchAgentSummary",
            summary = "Batch Search Agent Summaries",
            description = "Batch call shortcut for /api/v1/agent/search."
    )
    @PostMapping("/batch/search")
    public List<List<AgentSummaryDTO>> batchSearch(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Query conditions",
                    content = @Content(
                            examples = @ExampleObject("""
                                    [{
                                      "where": {
                                        "visibility": "public",
                                        "username": "amin",
                                        "text": "robot"
                                      },
                                      "orderBy": ["version", "modifyTime asc"],
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
                                        "format": "langflow",
                                        "visibility": "public",
                                        "username": "amin",
                                        "name": "Second Test",
                                        "text": "robot",
                                        "tags": ["robot"],
                                        "aiModels": ["123"]
                                      },
                                      "orderBy": ["version", "modifyTime asc"],
                                      "pageNum": 0,
                                      "pageSize": 1
                                    }]
                                    """
                            )
                    )
            )
            @RequestBody
            @NotNull
            List<AgentQueryDTO> queries) {
        List<List<AgentSummaryDTO>> results = new ArrayList<>(queries.size());
        for (AgentQueryDTO query : queries) {
            results.add(search(query));
        }
        return results;
    }

    @Operation(
            operationId = "batchSearchAgentDetails",
            summary = "Batch Search Agent Details",
            description = "Batch call shortcut for /api/v1/agent/details/search."
    )
    @PostMapping("/batch/details/search")
    public List<List<AgentDetailsDTO>> batchDetailsSearch(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Query conditions",
                    content = @Content(
                            examples = @ExampleObject("""
                                    [{
                                      "where": {
                                        "visibility": "public",
                                        "username": "amin",
                                        "text": "robot"
                                      },
                                      "orderBy": ["version", "modifyTime asc"],
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
                                        "format": "langflow",
                                        "visibility": "public",
                                        "username": "amin",
                                        "name": "Second Test",
                                        "text": "robot",
                                        "tags": ["robot"],
                                        "aiModels": ["123"]
                                      },
                                      "orderBy": ["version", "modifyTime asc"],
                                      "pageNum": 0,
                                      "pageSize": 1
                                    }]
                                    """
                            )
                    )
            )
            @RequestBody
            @NotNull
            List<AgentQueryDTO> queries) {
        List<List<AgentDetailsDTO>> results = new ArrayList<>(queries.size());
        for (AgentQueryDTO query : queries) {
            results.add(detailsSearch(query));
        }
        return results;
    }


    @Operation(
            operationId = "createAgent",
            summary = "Create Agent",
            description = """
                    Create a agent, ignore required fields:
                    - Agent name
                    - Agent configuration
                   
                    Limitations:
                    - Description: 300 characters
                    - Configuration: 2000 characters
                    - Example: 2000 characters
                    - Tags: 5
                    - Parameters: 10
                    """
    )
    @PostMapping("")
    @PreAuthorize("hasPermission(#p0.visibility, 'agentCreateOp')")
    public Long create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Information of the agent to be created",
                    content = @Content(
                            examples = @ExampleObject("""
                                    {
                                      "name": "A Test Agent",
                                      "description": "A agent description",
                                      "format": "langflow",
                                      "config": "{}",
                                      "parameters": "{\\"name\\": null}",
                                      "tags": [
                                        "test", "demo"
                                      ],
                                      "aiModels": [
                                        "123"
                                      ]
                                    }
                                    """
                            )
                    )
            )
            @RequestBody
            @NotNull
            AgentCreateDTO agent) {
        var agentInfo = agent.toAgentInfo();
        if (!agentService.create(agentInfo)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create agent.");
        }
        return agentInfo.getLeft().getAgentId();
    }

    @Operation(
            operationId = "createAgents",
            summary = "Batch Create Agents",
            description = "Batch create multiple agents. Ensure transactionality, return the agentId list after success."
    )
    @PostMapping("/batch")
    @PreFilter("hasPermission(filterObject.visibility, 'agentCreateOp')")
    public List<Long> batchCreate(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "List of agent information to be created",
                    content = @Content(
                            examples = @ExampleObject("""
                                    [{
                                      "name": "First Test Agent",
                                      "description": "First agent description",
                                      "format": "langflow",
                                      "config": "{}",
                                      "parameters": "{\\"name\\": null}",
                                      "tags": [
                                        "test1", "demo1"
                                      ],
                                      "aiModels": [
                                        "123"
                                      ]
                                    },
                                    {
                                      "name": "Second Test Agent",
                                      "visibility": "public",
                                      "description": "Second agent description",
                                      "format": "langflow",
                                      "config": "{}",
                                      "parameters": "{\\"robot\\": null}",
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
            List<AgentCreateDTO> agents) {
        var agentInfoList = agents.stream()
                .map(AgentCreateDTO::toAgentInfo)
                .toList();
        return agentService.create(agentInfoList);
    }

    @Operation(
            operationId = "updateAgent",
            summary = "Update Agent",
            description = "Update agent, refer to /api/v1/agent/create, required field: agentId. Return success or failure."
    )
    @PutMapping("/{agentId}")
    @PreAuthorize("hasPermission(#p0 + '|' + #p1.visibility, 'agentUpdateOp')")
    public Boolean update(
            @Parameter(description = "AgentId to be updated") @PathVariable("agentId") @Positive
            Long agentId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Agent information to be updated",
                    content = @Content(
                            examples = @ExampleObject("""
                                    {
                                      "version": 2,
                                      "name": "Second Test Agent (New)",
                                      "visibility": "public",
                                      "description": "Second agent description (new)",
                                      "config": "{}",
                                      "inputs": "{\\"robot\\": null}",
                                      "tags": [
                                        "test2", "demo2", "robot"
                                      ],
                                      "aiModels": [
                                        "123", "456"
                                      ]
                                    }
                                    """
                            )
                    )
            )
            @RequestBody
            @NotNull
            AgentUpdateDTO agent) {
        return agentService.update(agent.toAgentInfo(agentId));
    }

    @Operation(
            operationId = "cloneAgent",
            summary = "Clone Agent",
            description = """
                    Enter the agentId, generate a new record, the content is basically the same as the original agent, but the following fields are different:
                    - Version number is 1
                    - Visibility is private
                    - The parent agent is the source agentId
                    - The creation time is the current moment.
                     - All statistical indicators are zeroed.

                    Return the new agentId.
                    """
    )
    @PostMapping("/clone/{agentId}")
    public Long clone(
            @Parameter(description = "The referenced agentId") @PathVariable("agentId") @Positive
            Long agentId) {
        var agentInfo = agentService.details(agentId, AccountUtils.currentUser());
        if (agentInfo == null) {
            return null;
        }
        String agentUid = agentService.getUid(agentId);
        resetAgentInfoTriple(agentInfo, agentUid);
        if (!agentService.create(agentInfo)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create agent.");
        }
        increaseReferCount(agentUid);
        return agentInfo.getLeft().getAgentId();
    }

    @Operation(
            operationId = "cloneAgents",
            summary = "Batch Clone Agents",
            description = "Batch clone multiple agents. Ensure transactionality, return the agentId list after success."
    )
    @PostMapping("/batch/clone")
    public List<Long> batchClone(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "List of agent information to be created")
            @RequestBody
            @NotEmpty
            List<Long> agentIds) {
        var agentInfoList = agentService.details(agentIds, AccountUtils.currentUser());
        if (CollectionUtils.isEmpty(agentInfoList)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to find agents " + agentIds);
        }
        for (var agentInfo : agentInfoList) {
            Long parentAgentId = agentInfo.getLeft().getAgentId();
            String parentUid = parentAgentId == null ? null : agentService.getUid(parentAgentId);
            resetAgentInfoTriple(agentInfo, parentUid);
        }
        List<Long> newAgentIds = agentService.create(agentInfoList);
        if (!newAgentIds.isEmpty()) {
            for (var agentInfo : agentInfoList) {
                if (newAgentIds.contains(agentInfo.getLeft().getAgentId())) {
                    String parentUid = agentInfo.getLeft().getParentUid();
                    increaseReferCount(parentUid);
                }
            }
        }
        return newAgentIds;
    }

    @Operation(
            operationId = "deleteAgent",
            summary = "Delete Agent",
            description = "Delete agent. Return success or failure."
    )
    @DeleteMapping("/{agentId}")
    @PreAuthorize("hasPermission(#p0, 'agentDeleteOp')")
    public Boolean delete(
            @Parameter(description = "AgentId to be deleted") @PathVariable("agentId") @Positive
            Long agentId) {
        return agentService.delete(agentId, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "deleteAgents",
            summary = "Batch Delete Agents",
            description = "Delete multiple agents. Ensure transactionality, return the list of successfully deleted agentId."
    )
    @DeleteMapping("/batch/delete")
    @PreFilter("hasPermission(filterObject, 'agentDeleteOp')")
    public List<Long> batchDelete(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "List of agentId to be deleted")
            @RequestBody
            @NotEmpty
            List<Long> agentIds) {
        return agentService.delete(agentIds, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "getAgentSummary",
            summary = "Get Agent Summary",
            description = "Get agent summary information."
    )
    @GetMapping("/summary/{agentId}")
    public AgentSummaryDTO summary(
            @Parameter(description = "agentId to be obtained") @PathVariable("agentId") @Positive
            Long agentId) {
        var agentInfo = agentService.summary(agentId, AccountUtils.currentUser());
        return AgentSummaryDTO.from(agentInfo);
    }

    @Operation(
            operationId = "getAgentDetails",
            summary = "Get Agent Details",
            description = "Get agent detailed information."
    )
    @GetMapping("/details/{agentId}")
    public AgentDetailsDTO details(
            @Parameter(description = "AgentId to be obtained") @PathVariable("agentId") @Positive
            Long agentId) {
        var agentInfo = agentService.details(agentId, AccountUtils.currentUser());
        return AgentDetailsDTO.from(agentInfo);
    }

    @Operation(
            operationId = "countAgents",
            summary = "Calculate Number of Agents",
            description = "Calculate the number of agents according to the specified query conditions."
    )
    @PostMapping("/count")
    public Long count(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Query conditions") @RequestBody @NotNull
            AgentQueryDTO query) {
        AgentService.Query infoQuery = query.toAgentInfoQuery();
        infoQuery.setOffset(null);
        infoQuery.setLimit(null);
        infoQuery.setOrderBy(null);
        return agentService.count(infoQuery, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "listAgentVersionsByName",
            summary = "List Versions by Agent Name",
            description = "List the versions and corresponding agentIds by agent name."
    )
    @PostMapping("/versions/{name}")
    public List<AgentItemForNameDTO> versions(
            @Parameter(description = "Agent name") @PathVariable("name") @NotBlank
            String name) {
        return agentService.listVersionsByName(name, AccountUtils.currentUser())
                .stream()
                .map(AgentItemForNameDTO::from)
                .toList();
    }

    @Operation(
            operationId = "publishAgent",
            summary = "Publish Agent",
            description = "Publish agent, draft content becomes formal content, version number increases by 1. After successful publication, a new agentId will be generated and returned. You need to specify the visibility for publication."
    )
    @PostMapping("/publish/{agentId}/{visibility}")
    @PreAuthorize("hasPermission(#p0 + '|' + #p1, 'agentUpdateOp')")
    public Long publish(
            @Parameter(description = "The agentId to be published") @PathVariable("agentId") @Positive
            Long agentId,
            @Parameter(description = "Visibility: public | private | ...") @PathVariable("visibility") @NotBlank
            String visibility){
        return agentService.publish(agentId, Visibility.of(visibility), AccountUtils.currentUser());
    }
}
