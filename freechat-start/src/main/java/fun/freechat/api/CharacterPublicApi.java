package fun.freechat.api;

import fun.freechat.api.dto.CharacterQueryDTO;
import fun.freechat.api.dto.CharacterSummaryDTO;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.enums.Visibility;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Tag(name = "Character")
@RequestMapping("/api/v2/public/character")
@Validated
@Slf4j
@SuppressWarnings("unused")
public class CharacterPublicApi {
    @Autowired
    private CharacterService characterService;

    @Operation(
            operationId = "searchPublicCharacterSummary",
            summary = "Search Public Character Summary",
            description = """
                    Search characters:
                    - Specifiable query fields, and relationship:
                      - Scope: public(fixed).
                      - Username: exact match. If not specified, search all users.
                      - Tags: exact match (support and, or logic).
                      - Name: left match.
                      - Language, exact match.
                      - General: name, description, profile, chat style, experience, fuzzy match, one hit is enough; public scope + all user's general search does not guarantee timeliness.
                    - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending.
                    - The search result is the character summary content.
                    - Support pagination.
                    """
    )
    @PostMapping("/search")
    public List<CharacterSummaryDTO> search(
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
                                        "tags": ["demo2"]
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
            CharacterQueryDTO query) {
        if (query.getWhere() == null) {
            query.setWhere(new CharacterQueryDTO.Where());
        }
        if (Visibility.of(query.getWhere().getVisibility()) != Visibility.PUBLIC) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The visibility should be 'public'");
        }
        // ensure 'visibility' is non-blank
        query.getWhere().setVisibility(Visibility.PUBLIC.text());
        return characterService.search(query.toCharacterInfoQuery(), AccountUtils.currentUserOrNull())
                .stream()
                .map(CharacterSummaryDTO::from)
                .toList();
    }

    @Operation(
            operationId = "countPublicCharacters",
            summary = "Calculate Number of Public Characters",
            description = "Calculate the number of characters according to the specified query conditions."
    )
    @PostMapping("/count")
    public Long count(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Query conditions")
            @RequestBody
            @NotNull
            CharacterQueryDTO query) {
        if (query.getWhere() == null) {
            query.setWhere(new CharacterQueryDTO.Where());
        }
        if (Visibility.of(query.getWhere().getVisibility()) != Visibility.PUBLIC) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The visibility should be 'public'");
        }
        CharacterService.Query infoQuery = query.toCharacterInfoQuery();
        infoQuery.setOffset(null);
        infoQuery.setLimit(null);
        infoQuery.setOrderBy(null);
        infoQuery.getWhere().setVisibility(Visibility.PUBLIC.text());
        return characterService.count(infoQuery, AccountUtils.currentUserOrNull());
    }
}
