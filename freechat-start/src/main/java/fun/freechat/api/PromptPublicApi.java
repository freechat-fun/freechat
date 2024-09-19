package fun.freechat.api;

import fun.freechat.api.dto.PromptQueryDTO;
import fun.freechat.api.dto.PromptSummaryDTO;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.service.enums.Visibility;
import fun.freechat.service.prompt.PromptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@Tag(name = "Prompt")
@RequestMapping("/api/v1/public/prompt")
@ResponseBody
@Validated
@SuppressWarnings("unused")
public class PromptPublicApi {
    @Autowired
    private PromptService promptService;

    @Operation(
            operationId = "searchPublicPromptSummary",
            summary = "Search Public Prompt Summary",
            description = """
                    Search prompts:
                    - Specifiable query fields, and relationship:
                      - Scope: public(fixed).
                      - Username: exact match. If not specified, search all users.
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
        if (query.getWhere() == null) {
            query.setWhere(new PromptQueryDTO.Where());
        }
        if (Visibility.of(query.getWhere().getVisibility()) != Visibility.PUBLIC) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The visibility should be 'public'");
        }
        // ensure 'visibility' is non-blank
        query.getWhere().setVisibility(Visibility.PUBLIC.text());
        return promptService.search(query.toPromptInfoQuery(), AccountUtils.currentUserOrNull())
                .stream()
                .map(PromptSummaryDTO::from)
                .toList();
    }

    @Operation(
            operationId = "countPublicPrompts",
            summary = "Calculate Number of Public Prompts",
            description = "Calculate the number of prompts according to the specified query conditions."
    )
    @PostMapping("/count")
    public Long count(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Query conditions") @RequestBody @NotNull
            PromptQueryDTO query) {
        if (query.getWhere() == null) {
            query.setWhere(new PromptQueryDTO.Where());
        }
        if (Visibility.of(query.getWhere().getVisibility()) != Visibility.PUBLIC) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The visibility should be 'public'");
        }
        PromptService.Query infoQuery = query.toPromptInfoQuery();
        infoQuery.setOffset(null);
        infoQuery.setLimit(null);
        infoQuery.setOrderBy(null);
        infoQuery.getWhere().setVisibility(Visibility.PUBLIC.text());
        return promptService.count(infoQuery, AccountUtils.currentUser());
    }
}
