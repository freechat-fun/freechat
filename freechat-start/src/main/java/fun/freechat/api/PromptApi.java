package fun.freechat.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import fun.freechat.api.dto.*;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.model.PromptInfo;
import fun.freechat.service.ai.message.ChatPromptContent;
import fun.freechat.service.enums.*;
import fun.freechat.service.prompt.PromptService;
import fun.freechat.service.stats.InteractiveStatsService;
import fun.freechat.service.util.InfoUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Controller
@Tag(name = "Prompt")
@RequestMapping("/api/v1/prompt")
@ResponseBody
@Validated
@SuppressWarnings("unused")
public class PromptApi {
    @Autowired
    private PromptService promptService;

    @Autowired
    private InteractiveStatsService interactiveStatsService;

    private String newUniqueName(String desired) {
        int index = 0;
        String name =desired;
        while (promptService.existsName(name, AccountUtils.currentUser())) {
            index++;
            name = desired + "-" + index;
        }
        return name;
    }

    private void resetPromptInfo(PromptInfo info, String parentId) {
        if (StringUtils.isNotBlank(parentId)) {
            info.setParentId(parentId);
            info.setVisibility(Visibility.PRIVATE.text());
        }
        info.setName(newUniqueName(info.getName()));
        info.setPromptId(null);
        info.setVersion(1);
        info.setUserId(AccountUtils.currentUser().getUserId());
    }

    private void resetPromptInfoTriple(
            Triple<PromptInfo, List<String>, List<String>> infoTriple, String parentId) {
        resetPromptInfo(infoTriple.getLeft(), parentId);
    }

    private void increaseReferCount(String promptId) {
        interactiveStatsService.add(AccountUtils.currentUser().getUserId(),
                InfoType.PROMPT, promptId, StatsType.REFER_COUNT, 1L);
    }

    @Operation(
            operationId = "searchPromptSummary",
            summary = "Search Prompt Summary",
            description = """
                    Search prompts:
                    - Specifiable query fields, and relationship:
                      - Scope: private, public_org or public. Private can only search this account.
                      - Username: exact match, only valid when searching public, public_org. If not specified, search all users.
                      - Tags: exact match (support and, or logic).
                      - Model type: exact match (support and, or logic).
                      - Name: left match.
                      - Type, exact match: string (default) | chat.
                      - Language, exact match.
                      - General: name, description, template, example, fuzzy match, one hit is enough; public scope + all user's general search does not guarantee timeliness.
                    - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending.
                    - The search result is the prompt summary content.
                    - Support pagination.
                    """
    )
    @PostMapping("/search")
    public List<PromptSummaryDTO> search(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Query conditions",
                    content = @Content(
                            examples = @ExampleObject("""
                                    {
                                      "where": {
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
            PromptQueryDTO query) {
        return promptService.search(query.toPromptInfoQuery(), AccountUtils.currentUser())
                .stream()
                .map(PromptSummaryDTO::from)
                .toList();
    }

    @Operation(
            operationId = "searchPromptDetails",
            summary = "Search Prompt Details",
            description = "Same as /api/v1/prompt/search, but returns detailed information of the prompt."
    )
    @PostMapping("/details/search")
    public List<PromptDetailsDTO> detailsSearch(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Query conditions",
                    content = @Content(
                            examples = @ExampleObject("""
                                    {
                                      "where": {
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
            PromptQueryDTO query) {
        return promptService.searchDetails(query.toPromptInfoQuery(), AccountUtils.currentUser())
                .stream()
                .map(PromptDetailsDTO::from)
                .toList();
    }

    @Operation(
            operationId = "batchSearchPromptSummary",
            summary = "Batch Search Prompt Summaries",
            description = "Batch call shortcut for /api/v1/prompt/search."
    )
    @PostMapping("/batch/search")
    public List<List<PromptSummaryDTO>> batchSearch(
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
            List<PromptQueryDTO> queries) {
        List<List<PromptSummaryDTO>> results = new ArrayList<>(queries.size());
        for (PromptQueryDTO query : queries) {
            results.add(search(query));
        }
        return results;
    }

    @Operation(
            operationId = "batchSearchPromptDetails",
            summary = "Batch Search Prompt Details",
            description = "Batch call shortcut for /api/v1/prompt/details/search."
    )
    @PostMapping("/batch/details/search")
    public List<List<PromptDetailsDTO>> batchDetailsSearch(
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
            List<PromptQueryDTO> queries) {
        List<List<PromptDetailsDTO>> results = new ArrayList<>(queries.size());
        for (PromptQueryDTO query : queries) {
            results.add(detailsSearch(query));
        }
        return results;
    }

    @Operation(
            operationId = "createPrompt",
            summary = "Create Prompt",
            description = """
                    Create a prompt, required fields:
                    - Prompt name
                    - Prompt content
                    - Applicable model
                                                 
                    Limitations:
                    - Description: 300 characters
                    - Template: 1000 characters
                    - Example: 2000 characters
                    - Tags: 5
                    - Parameters: 10
                    """
    )
    @PostMapping(value = "", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasPermission(#p0.visibility, 'promptCreateOp')")
    public String create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Information of the prompt to be created",
                    content = @Content(
                            examples = @ExampleObject("""
                                    {
                                      "name": "A Test Prompt",
                                      "description": "A prompt description",
                                      "template": "Hello world. I'm {name}",
                                      "inputs": "{\\"name\\": null}",
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
            PromptCreateDTO prompt) {
        var promptInfo = prompt.toPromptInfo();
        if (!promptService.create(promptInfo)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create prompt.");
        }
        return promptInfo.getLeft().getPromptId();
    }

    @Operation(
            operationId = "createPrompts",
            summary = "Batch Create Prompts",
            description = "Batch create multiple prompts. Ensure transactionality, return the promptId list after success."
    )
    @PostMapping("/batch")
    @PreFilter("hasPermission(filterObject.visibility, 'promptCreateOp')")
    public List<String> batchCreate(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "List of prompt information to be created",
                    content = @Content(
                            examples = @ExampleObject("""
                                    [{
                                      "name": "First Test Prompt",
                                      "description": "First prompt description",
                                      "template": "Hello world. I'm {name}",
                                      "inputs": "{\\"name\\": null}",
                                      "tags": [
                                        "test1", "demo1"
                                      ],
                                      "aiModels": [
                                        "123"
                                      ]
                                    },
                                    {
                                      "name": "Second Test Prompt",
                                      "visibility": "public",
                                      "description": "Second prompt description",
                                      "template": "I wanna call you {robot}",
                                      "inputs": "{\\"robot\\": null}",
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
            List<PromptCreateDTO> prompts) {
        var promptInfoList = prompts.stream()
                .map(PromptCreateDTO::toPromptInfo)
                .toList();
        return promptService.create(promptInfoList);
    }

    @Operation(
            operationId = "updatePrompt",
            summary = "Update Prompt",
            description = "Update prompt, refer to /api/v1/prompt/create, required field: promptId. Returns success or failure."
    )
    @PutMapping("/{promptId}")
    @PreAuthorize("hasPermission(#p0 + '|' + #p1.visibility, 'promptUpdateOp')")
    public Boolean update(
            @Parameter(description = "The promptId to be updated") @PathVariable("promptId") @NotBlank
            String promptId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The prompt information to be updated",
                    content = @Content(
                            examples = @ExampleObject("""
                                    {
                                      "version": 2,
                                      "name": "Second Test Prompt (New)",
                                      "visibility": "public",
                                      "description": "Second prompt description (new)",
                                      "template": "I wanna call you {robot}",
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
            PromptUpdateDTO prompt) {
        var promptInfo = prompt.toPromptInfo(promptId);
        return promptService.update(promptInfo);
    }

    @Operation(
            operationId = "clonePrompt",
            summary = "Clone Prompt",
            description = """
                    Enter the promptId, generate a new record, the content is basically the same as the original prompt, but the following fields are different:
                    - Version number is 1
                    - Visibility is private
                    - The parent prompt is the source promptId
                    - The creation time is the current moment.
                    - All statistical indicators are zeroed.
                                 
                    Return the new promptId.
                    """
    )
    @PostMapping(value = "/clone/{promptId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String clone(
            @Parameter(description = "The referenced promptId") @PathVariable("promptId") @NotBlank
            String promptId) {
        var promptInfo = promptService.details(promptId, AccountUtils.currentUser());
        if (Objects.isNull(promptInfo)) {
            return null;
        }
        resetPromptInfoTriple(promptInfo, promptId);
        if (!promptService.create(promptInfo)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create prompt.");
        }
        increaseReferCount(promptId);
        return promptInfo.getLeft().getPromptId();
    }

    @Operation(
            operationId = "clonePrompts",
            summary = "Batch Clone Prompts",
            description = "Batch clone multiple prompts. Ensure transactionality, return the promptId list after success."
    )
    @PostMapping("/batch/clone")
    public List<String> batchClone(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "List of prompt information to be created")
            @RequestBody
            @NotEmpty
            List<String> promptIds) {
        var promptInfoList = promptService.details(promptIds, AccountUtils.currentUser());
        if (CollectionUtils.isEmpty(promptInfoList)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to find prompts " + promptIds);
        }
        HashMap<String, Integer> referCountMap = new HashMap<>(promptInfoList.size());
        for (var promptInfo : promptInfoList) {
            String parentPromptId = promptInfo.getLeft().getPromptId();
            resetPromptInfoTriple(promptInfo, parentPromptId);
        }
        List<String> newPromptIds = promptService.create(promptInfoList);
        if (!newPromptIds.isEmpty()) {
            for (var promptInfo : promptInfoList) {
                if (newPromptIds.contains(promptInfo.getLeft().getPromptId())) {
                    String parentPromptId = promptInfo.getLeft().getParentId();
                    increaseReferCount(parentPromptId);
                }
            }
        }
        return newPromptIds;
    }

    @Operation(
            operationId = "deletePrompt",
            summary = "Delete Prompt",
            description = "Delete prompt. Returns success or failure."
    )
    @DeleteMapping("/{promptId}")
    @PreAuthorize("hasPermission(#p0, 'promptDeleteOp')")
    public Boolean delete(
            @Parameter(description = "The promptId to be deleted") @PathVariable("promptId") @NotBlank
            String promptId) {
        return promptService.delete(promptId, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "deletePromptByName",
            summary = "Delete Prompt by Name",
            description = "Delete prompt by name. return the list of successfully deleted promptIds."
    )
    @DeleteMapping("/name/{name}")
    public List<String> deleteByName(
            @Parameter(description = "The prompt name to be deleted") @PathVariable("name") @NotBlank
            String name) {
        return promptService.deleteByName(name, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "deletePrompts",
            summary = "Batch Delete Prompts",
            description = "Delete multiple prompts. Ensure transactionality, return the list of successfully deleted promptIds."
    )
    @DeleteMapping("/batch")
    @PreFilter("hasPermission(filterObject, 'promptDeleteOp')")
    public List<String> batchDelete(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "List of promptIds to be deleted")
            @RequestBody
            @NotEmpty
            List<String> promptIds) {
        return promptService.delete(promptIds, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "getPromptSummary",
            summary = "Get Prompt Summary",
            description = "Get prompt summary information."
    )
    @GetMapping("/summary/{promptId}")
    public PromptSummaryDTO summary(
            @Parameter(description = "PromptId to be obtained") @PathVariable("promptId") @NotBlank
            String promptId) {
        var promptInfo = promptService.summary(promptId, AccountUtils.currentUser());
        return PromptSummaryDTO.from(promptInfo);
    }

    @Operation(
            operationId = "getPromptDetails",
            summary = "Get Prompt Details",
            description = "Get prompt detailed information."
    )
    @GetMapping("/details/{promptId}")
    public PromptDetailsDTO details(
            @Parameter(description = "PromptId to be obtained") @PathVariable("promptId") @NotBlank
            String promptId) {
        var promptInfo = promptService.details(promptId, AccountUtils.currentUser());
        return PromptDetailsDTO.from(promptInfo);
    }

    @Operation(
            operationId = "countPrompts",
            summary = "Calculate Number of Prompts",
            description = "Calculate the number of prompts according to the specified query conditions."
    )
    @PostMapping("/count")
    public Long count(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Query conditions")
            @RequestBody
            @NotNull
            PromptQueryDTO query) {
        PromptService.Query infoQuery = query.toPromptInfoQuery();
        infoQuery.setOffset(null);
        infoQuery.setLimit(null);
        infoQuery.setOrderBy(null);
        return promptService.count(infoQuery, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "listPromptVersionsByName",
            summary = "List Versions by Prompt Name",
            description = "List the versions and corresponding promptIds by prompt name."
    )
    @PostMapping("/versions/{name}")
    public List<PromptItemForNameDTO> versions(
            @Parameter(description = "Prompt name") @PathVariable("name") @NotBlank
            String name) {
        return promptService.listVersionsByName(name, AccountUtils.currentUser())
                .stream()
                .map(PromptItemForNameDTO::from)
                .toList();
    }

    @Operation(
            operationId = "publishPrompt",
            summary = "Publish Prompt",
            description = "Publish prompt, draft content becomes formal content, version number increases by 1. After successful publication, a new promptId will be generated and returned. You need to specify the visibility for publication."
    )
    @PostMapping(value = "/publish/{promptId}/{visibility}", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasPermission(#p0 + '|' + #p1, 'promptUpdateOp')")
    public String publish(
            @Parameter(description = "The promptId to be published") @PathVariable("promptId") @NotBlank
            String promptId,
            @Parameter(description = "Visibility: public | private | ...") @PathVariable("visibility") @NotBlank
            String visibility){
        return promptService.publish(promptId, Visibility.of(visibility), AccountUtils.currentUser());
    }

    @Operation(
            operationId = "applyPromptTemplate",
            summary = "Apply Parameters to Prompt Template",
            description = "Apply parameters to prompt template."
    )
    @PostMapping(value = "/apply/template", produces = MediaType.TEXT_PLAIN_VALUE)
    public String apply(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "String type prompt template")
            @RequestBody
            @NotNull
            PromptTemplateDTO promptTemplate) {
        String template = promptTemplate.getTemplate();
        if (StringUtils.isNotBlank(template)) {
            return promptService.apply(template, promptTemplate.getVariables(),
                    PromptFormat.of(promptTemplate.getFormat()));
        }
        if (Objects.nonNull(promptTemplate.getChatTemplate())) {
            try {
                ChatPromptContentDTO chatTemplate = promptTemplate.getChatTemplate();
                Map<String, Object> variables = promptTemplate.getVariables();
                PromptFormat format = PromptFormat.of(promptTemplate.getFormat());
                ChatPromptContent applied = promptService.apply(
                        chatTemplate.toChatPromptContent(),variables, format);
                return InfoUtils.defaultMapper().writeValueAsString(ChatPromptContentDTO.from(applied));
            } catch (JsonProcessingException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "template or chatTemplate must be defined.");
        }
    }

    @Operation(
            operationId = "applyPromptRef",
            summary = "Apply Parameters to Prompt Record",
            description = "Apply parameters to prompt record."
    )
    @PostMapping(value = "/apply/ref", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasPermission(#p0.promptId, 'promptDefaultOp')")
    public String apply(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Prompt record")
            @RequestBody
            @NotNull
            PromptRefDTO promptRef) {
        Pair<String, PromptType> applied = promptService.apply(
                promptRef.getPromptId(), promptRef.getVariables(), promptRef.getDraft());
        return Objects.nonNull(applied) ? applied.getLeft() : null;
    }

    @Operation(
            operationId = "existsPromptName",
            summary = "Check If Prompt Name Exists",
            description = "Check if the prompt name already exists."
    )
    @GetMapping("/exists/name/{name}")
    public Boolean existsName(
            @Parameter(description = "Name") @PathVariable("name") @NotBlank String name) {
        return promptService.existsName(name, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "newPromptName",
            summary = "Create New Prompt Name",
            description = "Create a new prompt name starting with a desired name."
    )
    @GetMapping(value = "/create/name/{desired}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String createName(
            @Parameter(description = "Desired name") @PathVariable("desired") @NotBlank String desired) {
        return newUniqueName(desired);
    }
}
