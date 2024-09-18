package fun.freechat.api;

import fun.freechat.api.dto.CharacterQueryDTO;
import fun.freechat.api.dto.CharacterSummaryDTO;
import fun.freechat.service.enums.Visibility;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
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
@Tag(name = "Character")
@RequestMapping("/api/v1/public/character")
@ResponseBody
@Validated
@Slf4j
@SuppressWarnings("unused")
public class CharacterPublicApi {
    @Autowired
    private CharacterApi characterApi;

    @Operation(
            operationId = "searchCharacterSummary",
            summary = "Search Character Summary",
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
        if (Visibility.of(query.getWhere().getVisibility()) != Visibility.PUBLIC) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The visibility should be 'public'");
        }
        // ensure the 'visibility' has a value
        query.getWhere().setVisibility(Visibility.PUBLIC.text());
        return characterApi.search(query);
    }
}
