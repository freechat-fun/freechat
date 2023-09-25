package fun.freechat.api;

import fun.freechat.api.dto.*;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.model.FlowInfo;
import fun.freechat.service.enums.InfoType;
import fun.freechat.service.enums.StatsType;
import fun.freechat.service.enums.Visibility;
import fun.freechat.service.flow.FlowService;
import fun.freechat.service.stats.InteractiveStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@Tag(name = "Flow")
@RequestMapping("/api/v1/flow")
@ResponseBody
@Validated
@SuppressWarnings("unused")
public class FlowApi {
    @Autowired
    private FlowService flowService;

    @Autowired
    private InteractiveStatsService interactiveStatsService;

    private void resetFlowInfo(FlowInfo info, String parentId) {
        if (StringUtils.isNotBlank(parentId)) {
            info.setParentId(parentId);
            info.setVisibility(Visibility.PRIVATE.text());
        }
        info.setFlowId(null);
        info.setVersion(1);
        info.setUserId(AccountUtils.currentUser().getUserId());
    }

    private void resetFlowInfoTriple(
            Triple<FlowInfo, List<String>, List<String>> infoTriple, String parentId) {
        resetFlowInfo(infoTriple.getLeft(), parentId);
    }

    private void increaseReferCount(String flowId) {
        interactiveStatsService.add(AccountUtils.currentUser().getUserId(),
                InfoType.FLOW, flowId, StatsType.REFER_COUNT, 1L);
    }

    @Operation(
            operationId = "searchFlowSummary",
            summary = "Search Flow Summary",
            description = """
                    Search flows:
                    - Specifiable query fields, and relationship:
                      - Scope: private, public_org or public. Private can only search this account.
                      - Username: exact match, only valid when searching public, public_org. If not specified, search all users.
                      - Format: exact match, currently supported: langflow
                      - Tags: exact match (support and, or logic).
                      - Model type: exact match (support and, or logic).
                      - Name: left match.
                      - General: name, description, example, fuzzy match, one hit is enough; public scope + all user's general search does not guarantee timeliness.
                    - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending.
                    - The search result is the flow summary content.
                    - Support pagination.
                    """
    )
    @PostMapping("/search")
    public List<FlowSummaryDTO> search(
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
            FlowQueryDTO query) {
        return flowService.search(query.toFlowInfoQuery(), AccountUtils.currentUser())
                .stream()
                .map(FlowSummaryDTO::fromFlowInfo)
                .toList();
    }

    @Operation(
            operationId = "searchFlowDetails",
            summary = "Search Flow Details",
            description = "Same as /api/v1/flow/search, but returns detailed information of the flow."
    )
    @PostMapping("/details/search")
    public List<FlowDetailsDTO> detailsSearch(
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
            FlowQueryDTO query) {
        return flowService.searchDetails(query.toFlowInfoQuery(), AccountUtils.currentUser())
                .stream()
                .map(FlowDetailsDTO::fromFlowInfo)
                .toList();
    }


    @Operation(
            operationId = "batchSearchFlowSummary",
            summary = "Batch Search Flow Summaries",
            description = "Batch call shortcut for /api/v1/flow/search."
    )
    @PostMapping("/batch/search")
    public List<List<FlowSummaryDTO>> batchSearch(
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
            List<FlowQueryDTO> queries) {
        List<List<FlowSummaryDTO>> results = new ArrayList<>(queries.size());
        for (FlowQueryDTO query : queries) {
            results.add(search(query));
        }
        return results;
    }

    @Operation(
            operationId = "batchSearchFlowDetails",
            summary = "Batch Search Flow Details",
            description = "Batch call shortcut for /api/v1/flow/details/search."
    )
    @PostMapping("/batch/details/search")
    public List<List<FlowDetailsDTO>> batchDetailsSearch(
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
            List<FlowQueryDTO> queries) {
        List<List<FlowDetailsDTO>> results = new ArrayList<>(queries.size());
        for (FlowQueryDTO query : queries) {
            results.add(detailsSearch(query));
        }
        return results;
    }


    @Operation(
            operationId = "createFlow",
            summary = "Create Flow",
            description = """
                    Create a flow, ignore required fields:
                    - Flow name
                    - Flow configuration
                   
                    Limitations:
                    - Description: 300 characters
                    - Configuration: 2000 characters
                    - Example: 2000 characters
                    - Tags: 5
                    - Parameters: 10
                    """
    )
    @PostMapping(value = "", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasPermission(#p0.visibility, 'flowCreateOp')")
    public String create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Information of the flow to be created",
                    content = @Content(
                            examples = @ExampleObject("""
                                    {
                                      "name": "A Test Flow",
                                      "description": "A flow description",
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
            FlowCreateDTO flow) {
        var flowInfo = flow.toFlowInfo();
        if (!flowService.create(flowInfo)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create flow.");
        }
        return flowInfo.getLeft().getFlowId();
    }

    @Operation(
            operationId = "createFlows",
            summary = "Batch Create Flows",
            description = "Batch create multiple flows. Ensure transactionality, return the flowId list after success."
    )
    @PostMapping("/batch")
    @PreFilter("hasPermission(filterObject.visibility, 'flowCreateOp')")
    public List<String> batchCreate(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "List of flow information to be created",
                    content = @Content(
                            examples = @ExampleObject("""
                                    [{
                                      "name": "First Test Flow",
                                      "description": "First flow description",
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
                                      "name": "Second Test Flow",
                                      "visibility": "public",
                                      "description": "Second flow description",
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
            List<FlowCreateDTO> flows) {
        var flowInfoList = flows.stream()
                .map(FlowCreateDTO::toFlowInfo)
                .toList();
        return flowService.create(flowInfoList);
    }

    @Operation(
            operationId = "updateFlow",
            summary = "Update Flow",
            description = "Update flow, refer to /api/v1/flow/create, required field: flowId. Return success or failure."
    )
    @PutMapping("/{flowId}")
    @PreAuthorize("hasPermission(#p0 + '|' + #p1.visibility, 'flowUpdateOp')")
    public Boolean update(
            @Parameter(description = "FlowId to be updated") @PathVariable("flowId") @NotBlank
            String flowId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Flow information to be updated",
                    content = @Content(
                            examples = @ExampleObject("""
                                    {
                                      "version": 2,
                                      "name": "Second Test Flow (New)",
                                      "visibility": "public",
                                      "description": "Second flow description (new)",
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
            FlowUpdateDTO flow) {
        return flowService.update(flow.toFlowInfo(flowId));
    }

    @Operation(
            operationId = "cloneFlow",
            summary = "Clone Flow",
            description = """
                    Enter the flowId, generate a new record, the content is basically the same as the original flow, but the following fields are different:
                    - Version number is 1
                    - Visibility is private
                    - The parent flow is the source flowId
                    - The creation time is the current moment.
                     - All statistical indicators are zeroed.

                    Return the new flowId.
                    """
    )
    @PostMapping(value = "/clone/{flowId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String clone(
            @Parameter(description = "The referenced flowId") @PathVariable("flowId") @NotBlank
            String flowId) {
        var flowInfo = flowService.details(flowId, AccountUtils.currentUser());
        if (Objects.isNull(flowInfo)) {
            return null;
        }
        resetFlowInfoTriple(flowInfo, flowId);
        if (!flowService.create(flowInfo)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create flow.");
        }
        increaseReferCount(flowId);
        return flowInfo.getLeft().getFlowId();
    }

    @Operation(
            operationId = "cloneFlows",
            summary = "Batch Clone Flows",
            description = "Batch clone multiple flows. Ensure transactionality, return the flowId list after success."
    )
    @PostMapping("/batch/clone")
    public List<String> batchClone(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "List of flow information to be created")
            @RequestBody
            @NotEmpty
            List<String> flowIds) {
        var flowInfoList = flowService.details(flowIds, AccountUtils.currentUser());
        if (CollectionUtils.isEmpty(flowInfoList)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to find flows " + flowIds);
        }
        for (var flowInfo : flowInfoList) {
            String parentFlowId = flowInfo.getLeft().getFlowId();
            resetFlowInfoTriple(flowInfo, parentFlowId);
        }
        List<String> newFlowIds = flowService.create(flowInfoList);
        if (!newFlowIds.isEmpty()) {
            for (var flowInfo : flowInfoList) {
                if (newFlowIds.contains(flowInfo.getLeft().getFlowId())) {
                    String parentFlowId = flowInfo.getLeft().getParentId();
                    increaseReferCount(parentFlowId);
                }
            }
        }
        return newFlowIds;
    }

    @Operation(
            operationId = "deleteFlow",
            summary = "Delete Flow",
            description = "Delete flow. Return success or failure."
    )
    @DeleteMapping("/{flowId}")
    @PreAuthorize("hasPermission(#p0, 'flowDeleteOp')")
    public Boolean delete(
            @Parameter(description = "FlowId to be deleted") @PathVariable("flowId") @NotBlank
            String flowId) {
        return flowService.delete(flowId, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "deleteFlows",
            summary = "Batch Delete Flows",
            description = "Delete multiple flows. Ensure transactionality, return the list of successfully deleted flowId."
    )
    @DeleteMapping("/batch/delete")
    @PreFilter("hasPermission(filterObject, 'flowDeleteOp')")
    public List<String> batchDelete(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "List of flowId to be deleted")
            @RequestBody
            @NotEmpty
            List<String> flowIds) {
        return flowService.delete(flowIds, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "getFlowSummary",
            summary = "Get Flow Summary",
            description = "Get flow summary information."
    )
    @GetMapping("/summary/{flowId}")
    public FlowSummaryDTO summary(
            @Parameter(description = "flowId to be obtained") @PathVariable("flowId") @NotBlank
            String flowId) {
        var flowInfo = flowService.summary(flowId, AccountUtils.currentUser());
        return FlowSummaryDTO.fromFlowInfo(flowInfo);
    }

    @Operation(
            operationId = "getFlowDetails",
            summary = "Get Flow Details",
            description = "Get flow detailed information."
    )
    @GetMapping("/details/{flowId}")
    public FlowDetailsDTO details(
            @Parameter(description = "FlowId to be obtained") @PathVariable("flowId") @NotBlank
            String flowId) {
        var flowInfo = flowService.details(flowId, AccountUtils.currentUser());
        return FlowDetailsDTO.fromFlowInfo(flowInfo);
    }

    @Operation(
            operationId = "countFlows",
            summary = "Calculate Number of Flows",
            description = "Calculate the number of flows according to the specified query conditions."
    )
    @PostMapping("/count")
    public Long count(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Query conditions")
            @RequestBody
            @NotNull
            FlowQueryDTO query) {
        FlowService.Query infoQuery = query.toFlowInfoQuery();
        infoQuery.setOffset(null);
        infoQuery.setLimit(null);
        infoQuery.setOrderBy(null);
        return flowService.count(infoQuery, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "listFlowVersionsByName",
            summary = "List Versions by Flow Name",
            description = "List the versions and corresponding flowIds by flow name."
    )
    @PostMapping("/versions/{name}")
    public List<FlowItemForNameDTO> versions(
            @Parameter(description = "Flow name") @PathVariable("name") @NotBlank
            String name) {
        return flowService.listVersionsByName(name, AccountUtils.currentUser())
                .stream()
                .map(FlowItemForNameDTO::fromInfoItem)
                .toList();
    }

    @Operation(
            operationId = "publishFlow",
            summary = "Publish Flow",
            description = "Publish flow, draft content becomes formal content, version number increases by 1. After successful publication, a new flowId will be generated and returned. You need to specify the visibility for publication."
    )
    @PostMapping(value = "/publish/{flowId}/{visibility}", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasPermission(#p0 + '|' + #p1, 'flowUpdateOp')")
    public String publish(
            @Parameter(description = "The flowId to be published") @PathVariable("flowId") @NotBlank
            String flowId,
            @Parameter(description = "Visibility: public | private | ...") @PathVariable("visibility") @NotBlank
            String visibility){
        return flowService.publish(flowId, Visibility.of(visibility), AccountUtils.currentUser());
    }
}
