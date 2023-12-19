package fun.freechat.api;

import fun.freechat.api.dto.*;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.FileUtils;
import fun.freechat.model.CharacterBackend;
import fun.freechat.model.CharacterInfo;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.common.ConfigService;
import fun.freechat.service.common.FileStore;
import fun.freechat.service.enums.InfoType;
import fun.freechat.service.enums.StatsType;
import fun.freechat.service.enums.Visibility;
import fun.freechat.service.stats.InteractiveStatsService;
import fun.freechat.service.util.ConfigUtils;
import fun.freechat.service.util.StoreUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

import static fun.freechat.api.util.FileUtils.PUBLIC_DIR;

@Controller
@Tag(name = "Character")
@RequestMapping("/api/v1/character")
@ResponseBody
@Validated
@SuppressWarnings("unused")
public class CharacterApi {
    private static final String CONFIG_NAME = "character";

    private static final String PICTURE_MAX_SIZE_KEY = "picture.maxSize";

    private static final String PICTURE_MAX_COUNT_KEY = "picture.maxCount";

    private static final String AVATAR_MAX_SIZE_KEY = "avatar.maxSize";

    private static final String AVATAR_MAX_COUNT_KEY = "avatar.maxCount";

    private static final long DEFAULT_PICTURE_MAX_SIZE = 2 * 1024 * 1024;

    private static final int DEFAULT_PICTURE_MAX_COUNT = 10;

    private static final long DEFAULT_AVATAR_MAX_SIZE = 64 * 1024;

    private static final int DEFAULT_AVATAR_MAX_COUNT = 10;

    @Value("${chat.memory.minWindowSize:50}")
    private Integer minWindowSize;

    @Value("${chat.memory.maxWindowSize:1000}")
    private Integer maxWindowSize;

    @Value("${chat.memory.defaultWindowSize:100}")
    private Integer defaultWindowSize;

    @Autowired
    private CharacterService characterService;

    @Autowired
    private InteractiveStatsService interactiveStatsService;

    @Autowired
    @Qualifier("mysqlConfigService")
    private ConfigService configService;

    private void resetCharacterInfo(CharacterInfo info, String parentId) {
        if (StringUtils.isNotBlank(parentId)) {
            info.setParentId(parentId);
            info.setVisibility(Visibility.PRIVATE.text());
        }
        info.setCharacterId(null);
        info.setVersion(1);
        info.setUserId(AccountUtils.currentUser().getUserId());
    }

    private void resetCharacterInfoPair(
            Pair<CharacterInfo, List<String>> infoPair, String parentId) {
        resetCharacterInfo(infoPair.getLeft(), parentId);
    }

    private void increaseReferCount(String characterId) {
        interactiveStatsService.add(AccountUtils.currentUser().getUserId(),
                InfoType.CHARACTER, characterId, StatsType.REFER_COUNT, 1L);
    }

    @Operation(
            operationId = "searchCharacterSummary",
            summary = "Search Character Summary",
            description = """
                    Search characters:
                    - Specifiable query fields, and relationship:
                      - Scope: private, public_org or public. Private can only search this account.
                      - Username: exact match, only valid when searching public, public_org. If not specified, search all users.
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
        return characterService.search(query.toCharacterInfoQuery(), AccountUtils.currentUser())
                .stream()
                .map(CharacterSummaryDTO::from)
                .toList();
    }

    @Operation(
            operationId = "searchCharacterDetails",
            summary = "Search Character Details",
            description = "Same as /api/v1/character/search, but returns detailed information of the character."
    )
    @PostMapping("/details/search")
    public List<CharacterDetailsDTO> detailsSearch(
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
        return characterService.searchDetails(query.toCharacterInfoQuery(), AccountUtils.currentUser())
                .stream()
                .map(CharacterDetailsDTO::from)
                .toList();
    }

    @Operation(
            operationId = "batchSearchCharacterSummary",
            summary = "Batch Search Character Summaries",
            description = "Batch call shortcut for /api/v1/character/search."
    )
    @PostMapping("/batch/search")
    public List<List<CharacterSummaryDTO>> batchSearch(
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
                                        "tags": ["robot"]
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
            List<CharacterQueryDTO> queries) {
        List<List<CharacterSummaryDTO>> results = new ArrayList<>(queries.size());
        for (CharacterQueryDTO query : queries) {
            results.add(search(query));
        }
        return results;
    }

    @Operation(
            operationId = "batchSearchCharacterDetails",
            summary = "Batch Search Character Details",
            description = "Batch call shortcut for /api/v1/character/details/search."
    )
    @PostMapping("/batch/details/search")
    public List<List<CharacterDetailsDTO>> batchDetailsSearch(
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
                                        "tags": ["robot"]
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
            List<CharacterQueryDTO> queries) {
        List<List<CharacterDetailsDTO>> results = new ArrayList<>(queries.size());
        for (CharacterQueryDTO query : queries) {
            results.add(detailsSearch(query));
        }
        return results;
    }

    @Operation(
            operationId = "createCharacter",
            summary = "Create Character",
            description = "Create a character."
    )
    @PostMapping(value = "", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasPermission(#p0.visibility, 'characterCreateOp')")
    public String create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Information of the character to be created",
                    content = @Content(
                            examples = @ExampleObject("""
                                    {
                                      "name": "A Test Character",
                                      "description": "A character description",
                                      "profile": "Hello world. I'm Jack",
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
            CharacterCreateDTO character) {
        var characterInfo = character.toCharacterInfo();
        if (!characterService.create(characterInfo)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create character.");
        }
        return characterInfo.getLeft().getCharacterId();
    }

    @Operation(
            operationId = "updateCharacter",
            summary = "Update Character",
            description = "Update character, refer to /api/v1/character/create, required field: characterId. Returns success or failure."
    )
    @PutMapping("/{characterId}")
    @PreAuthorize("hasPermission(#p0 + '|' + #p1.visibility, 'characterUpdateOp')")
    public Boolean update(
            @Parameter(description = "The characterId to be updated") @PathVariable("characterId") @NotBlank
            String characterId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The character information to be updated",
                    content = @Content(
                            examples = @ExampleObject("""
                                    {
                                      "version": 2,
                                      "name": "Second Test Character (New)",
                                      "visibility": "public",
                                      "description": "Second character description (new)",
                                      "profile": "I am Kelvin",
                                      "tags": [
                                        "test2", "demo2", "robot"
                                      ]
                                    }
                                    """
                            )
                    )
            )
            @RequestBody
            @NotNull
            CharacterUpdateDTO character) {
        var characterInfo = character.toCharacterInfo(characterId);
        return characterService.update(characterInfo);
    }

    @Operation(
            operationId = "cloneCharacter",
            summary = "Clone Character",
            description = """
                    Enter the characterId, generate a new record, the content is basically the same as the original character, but the following fields are different:
                    - Version number is 1
                    - Visibility is private
                    - The parent character is the source characterId
                    - The creation time is the current moment.
                    - All statistical indicators are zeroed.
                                 
                    Return the new characterId.
                    """
    )
    @PostMapping(value = "/clone/{characterId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String clone(
            @Parameter(description = "The referenced characterId") @PathVariable("characterId") @NotBlank
            String characterId) {
        var characterDetails = characterService.details(characterId, AccountUtils.currentUser());
        if (Objects.isNull(characterDetails)) {
            return null;
        }

        var characterInfo = Pair.of(characterDetails.getLeft(), characterDetails.getMiddle());
        resetCharacterInfoPair(characterInfo, characterId);
        if (!characterService.create(characterInfo)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create character.");
        }
        increaseReferCount(characterId);
        return characterInfo.getLeft().getCharacterId();
    }

    @Operation(
            operationId = "deleteCharacter",
            summary = "Delete Character",
            description = "Delete character. Returns success or failure."
    )
    @DeleteMapping("/{characterId}")
    @PreAuthorize("hasPermission(#p0, 'characterDeleteOp')")
    public Boolean delete(
            @Parameter(description = "The characterId to be deleted") @PathVariable("characterId") @NotBlank
            String characterId) {
        return characterService.delete(characterId, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "getCharacterSummary",
            summary = "Get Character Summary",
            description = "Get character summary information."
    )
    @GetMapping("/summary/{characterId}")
    public CharacterSummaryDTO summary(
            @Parameter(description = "CharacterId to be obtained") @PathVariable("characterId") @NotBlank
            String characterId) {
        var characterInfo = characterService.summary(characterId, AccountUtils.currentUser());
        return CharacterSummaryDTO.from(characterInfo);
    }

    @Operation(
            operationId = "getCharacterDetails",
            summary = "Get Character Details",
            description = "Get character detailed information."
    )
    @GetMapping("/details/{characterId}")
    public CharacterDetailsDTO details(
            @Parameter(description = "CharacterId to be obtained") @PathVariable("characterId") @NotBlank
            String characterId) {
        var characterInfo = characterService.details(characterId, AccountUtils.currentUser());
        return CharacterDetailsDTO.from(characterInfo);
    }

    @Operation(
            operationId = "countCharacters",
            summary = "Calculate Number of Characters",
            description = "Calculate the number of characters according to the specified query conditions."
    )
    @PostMapping("/count")
    public Long count(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Query conditions")
            @RequestBody
            @NotNull
            CharacterQueryDTO query) {
        CharacterService.Query infoQuery = query.toCharacterInfoQuery();
        infoQuery.setOffset(null);
        infoQuery.setLimit(null);
        infoQuery.setOrderBy(null);
        return characterService.count(infoQuery, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "listCharacterVersionsByName",
            summary = "List Versions by Character Name",
            description = "List the versions and corresponding characterIds by character name."
    )
    @PostMapping("/versions/{name}")
    public List<CharacterItemForNameDTO> versions(
            @Parameter(description = "Character name") @PathVariable("name") @NotBlank
            String name) {
        return characterService.listVersionsByName(name, AccountUtils.currentUser())
                .stream()
                .map(CharacterItemForNameDTO::from)
                .toList();
    }

    @Operation(
            operationId = "getCharacterLatestIdByName",
            summary = "Get Latest Character Id by Name",
            description = "Get latest characterId by character name."
    )
    @PostMapping(value = "/latest/{name}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getLatestIdByName(
            @Parameter(description = "Character name") @PathVariable("name") @NotBlank
            String name) {
        return characterService.getLatestIdByName(name, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "publishCharacter",
            summary = "Publish Character",
            description = "Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication."
    )
    @PostMapping(
            value = {"/publish/{characterId}/{visibility}", "/publish/{characterId}"},
            produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasPermission(#p0 + '|' + #p1, 'characterUpdateOp')")
    public String publish(
            @Parameter(description = "The characterId to be published") @PathVariable("characterId") @NotBlank
            String characterId,
            @Parameter(description = "Visibility: public | private | ...") @PathVariable("visibility")
            Optional<String> visibility){
        Visibility publishVisibility = visibility.map(Visibility::of).orElse(Visibility.PUBLIC);
        return characterService.publish(characterId, publishVisibility, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "addCharacterBackend",
            summary = "Add Character Backend",
            description = "Add a backend configuration for a character."
    )
    @PostMapping(value = "/backend/{characterId}", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasPermission(#p0 + '|' + #p1.chatPromptTaskId, 'characterBackendCreateOp')")
    public String addBackend(
            @Parameter(description = "The characterId to be added a backend") @PathVariable("characterId") @NotBlank
            String characterId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The character backend to be added") @RequestBody @NotNull
            CharacterBackendDTO backend) {
        CharacterBackend characterBackend = backend.toCharacterBackend(characterId);
        if (Objects.isNull(characterBackend)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid character backend");
        }
        Integer messageWindowSize = characterBackend.getMessageWindowSize();
        if (Objects.isNull(messageWindowSize)) {
            messageWindowSize = defaultWindowSize;
        }
        if (messageWindowSize < minWindowSize || messageWindowSize > maxWindowSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Message window size should be between " + minWindowSize + " and " + maxWindowSize);
        }
        return characterService.addBackend(characterBackend);
    }

    @Operation(
            operationId = "updateCharacterBackend",
            summary = "Update Character Backend",
            description = "Update a backend configuration."
    )
    @PutMapping("/backend/{characterBackendId}")
    @PreAuthorize("hasPermission(#p0 + '|' + #p1.chatPromptTaskId, 'characterBackendUpdateOp')")
    public Boolean updateBackend(
            @Parameter(description = "The characterBackendId to be updated") @PathVariable("characterBackendId") @NotBlank
            String characterBackendId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The character backend configuration to be updated") @RequestBody @NotNull
            CharacterBackendDTO backend) {
        CharacterBackend characterBackend = characterService.getBackend(characterBackendId);
        if (Objects.isNull(characterBackend)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to find character backend: " + characterBackendId);
        }

        CharacterBackend updatedBackend = backend.toCharacterBackend(characterBackend.getCharacterId());
        updatedBackend.setBackendId(characterBackend.getBackendId());
        return characterService.updateBackend(updatedBackend);
    }

    @Operation(
            operationId = "removeCharacterBackend",
            summary = "Remove Character Backend",
            description = "Remove a backend configuration."
    )
    @DeleteMapping("/backend/{characterBackendId}")
    @PreAuthorize("hasPermission(#p0, 'characterBackendDefaultOp')")
    public Boolean removeBackend(
            @Parameter(description = "The characterBackendId to be removed") @PathVariable("characterBackendId") @NotBlank
            String characterBackendId) {
        String characterId = characterService.getBackendCharacterId(characterBackendId);
        if (StringUtils.isBlank(characterId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid character backend");
        }
        return characterService.removeBackend(characterId, characterBackendId);
    }

    @Operation(
            operationId = "getDefaultCharacterBackend",
            summary = "Get Default Character Backend",
            description = "Get the default backend configuration."
    )
    @GetMapping("/backend/default/{characterId}")
    @PreAuthorize("hasPermission(#p0, 'characterDefaultOp')")
    public CharacterBackendDetailsDTO getDefaultBackend(
            @Parameter(description = "The characterId to be queried") @PathVariable("characterId") @NotBlank
            String characterId) {
        return CharacterBackendDetailsDTO.from(characterService.getDefaultBackend(characterId));
    }

    @Operation(
            operationId = "listCharacterBackendIds",
            summary = "List Character Backend ids",
            description = "List Character Backend identifiers."
    )
    @GetMapping("/backends/{characterId}")
    @PreAuthorize("hasPermission(#p0, 'characterDefaultOp')")
    public List<String> listBackendIds(
            @Parameter(description = "The characterId to be queried") @PathVariable("characterId") @NotBlank
            String characterId) {
        return characterService.listBackendIds(characterId);
    }

    @Operation(
            operationId = "setDefaultCharacterBackend",
            summary = "Set Default Character Backend",
            description = "Set the default backend configuration."
    )
    @PutMapping("/backend/default/{characterBackendId}")
    @PreAuthorize("hasPermission(#p0, 'characterBackendDefaultOp')")
    public Boolean setDefaultBackend(
            @Parameter(description = "The characterBackendId to be set to default") @PathVariable("characterBackendId") @NotBlank
            String characterBackendId) {
        String characterId = characterService.getBackendCharacterId(characterBackendId);
        if (StringUtils.isBlank(characterId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid character backend");
        }
        return characterService.setDefaultBackend(characterId, characterBackendId);
    }

    @Operation(
            operationId = "uploadCharacterPicture",
            summary = "Upload Character Picture",
            description = "Upload a picture of the character."
    )
    @PostMapping(value = "/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String uploadPicture(
            HttpServletRequest request,
            @Parameter(description = "Character picture") @RequestParam("file") @NotNull
            MultipartFile file) {
        Properties properties = ConfigUtils.getProperties(configService, CONFIG_NAME);
        long maxSize = ConfigUtils.getLongOrDefault(properties, PICTURE_MAX_SIZE_KEY, DEFAULT_PICTURE_MAX_SIZE);
        int maxCount = ConfigUtils.getIntOrDefault(properties, PICTURE_MAX_COUNT_KEY, DEFAULT_PICTURE_MAX_COUNT);

        if (file.getSize() > maxSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size should be less than " + maxSize);
        }

        FileStore fileStore = StoreUtils.defaultFileStore();
        try {
            String dstDir = PUBLIC_DIR + AccountUtils.currentUser().getUserId() + "/character/picture";
            String dstPath = FileUtils.transfer(file, fileStore, dstDir, maxCount);
            String shareUrl = fileStore.getShareUrl(dstPath, Integer.MAX_VALUE);
            if (StringUtils.isBlank(shareUrl)) {
                shareUrl = FileUtils.getDefaultShareUrlForImage(request, dstPath);
            }
            return shareUrl;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
        }
    }

    @Operation(
            operationId = "uploadCharacterAvatar",
            summary = "Upload Character Avatar",
            description = "Upload an avatar of the character."
    )
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String uploadAvatar(
            HttpServletRequest request,
            @Parameter(description = "Character avatar") @RequestParam("file") @NotNull
            MultipartFile file) {
        Properties properties = ConfigUtils.getProperties(configService, CONFIG_NAME);
        long maxSize = ConfigUtils.getLongOrDefault(properties, AVATAR_MAX_SIZE_KEY, DEFAULT_AVATAR_MAX_SIZE);
        int maxCount = ConfigUtils.getIntOrDefault(properties, AVATAR_MAX_COUNT_KEY, DEFAULT_AVATAR_MAX_COUNT);

        if (file.getSize() > maxSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size should be less than " + maxSize);
        }

        FileStore fileStore = StoreUtils.defaultFileStore();
        try {
            String dstDir = PUBLIC_DIR + AccountUtils.currentUser().getUserId() + "/character/avatar";
            String dstPath = FileUtils.transfer(file, fileStore, dstDir, maxCount);
            String shareUrl = fileStore.getShareUrl(dstPath, Integer.MAX_VALUE);
            if (StringUtils.isBlank(shareUrl)) {
                shareUrl = FileUtils.getDefaultShareUrlForImage(request, dstPath);
            }
            return shareUrl;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
        }
    }
}
