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
import jakarta.validation.constraints.Positive;
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

    private static final long DEFAULT_AVATAR_MAX_SIZE = 1024 * 1024;

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

    private String newUniqueName(String desired) {
        int index = 0;
        String name =desired;
        while (characterService.existsName(name, AccountUtils.currentUser())) {
            index++;
            name = desired + "-" + index;
        }
        return name;
    }

    private void resetCharacterInfo(CharacterInfo info, String parentUid) {
        if (StringUtils.isNotBlank(parentUid)) {
            info.setParentUid(parentUid);
            info.setVisibility(Visibility.PRIVATE.text());
        }
        info.setName(newUniqueName(info.getName()));
        info.setCharacterId(null);
        info.setVersion(1);
        info.setUserId(AccountUtils.currentUser().getUserId());
    }

    private void resetCharacterInfoPair(
            Pair<CharacterInfo, List<String>> infoPair, String parentUid) {
        resetCharacterInfo(infoPair.getLeft(), parentUid);
    }

    private void increaseReferCount(String characterUid) {
        interactiveStatsService.add(AccountUtils.currentUser().getUserId(),
                InfoType.CHARACTER, characterUid, StatsType.REFER_COUNT, 1L);
    }

    private String getCharacterUidFromPath(String path) {
        String[] parts = path.split("/");
        if (parts.length < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image key");
        }
        return parts[parts.length - 2];
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
    @PostMapping("")
    @PreAuthorize("hasPermission(#p0.visibility, 'characterCreateOp')")
    public Long create(
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
            @Parameter(description = "The characterId to be updated") @PathVariable("characterId") @Positive
            Long characterId,
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
    @PostMapping("/clone/{characterId}")
    public Long clone(
            @Parameter(description = "The referenced characterId") @PathVariable("characterId") @Positive
            Long characterId) {
        var characterDetails = characterService.details(characterId, AccountUtils.currentUser());
        if (Objects.isNull(characterDetails)) {
            return null;
        }

        var characterInfo = Pair.of(characterDetails.getLeft(), characterDetails.getMiddle());
        String characterUid = characterService.getUid(characterId);
        resetCharacterInfoPair(characterInfo, characterUid);
        if (!characterService.create(characterInfo)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create character.");
        }
        increaseReferCount(characterUid);
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
            @Parameter(description = "The characterId to be deleted") @PathVariable("characterId") @Positive
            Long characterId) {
        return characterService.delete(characterId, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "deleteCharacterByName",
            summary = "Delete Character by Name",
            description = "Delete character by name. return the list of successfully deleted characterIds."
    )
    @DeleteMapping("/name/{name}")
    public List<Long> deleteByName(
            @Parameter(description = "The character name to be deleted") @PathVariable("name") @NotBlank
            String name) {
        return characterService.deleteByName(name, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "getCharacterSummary",
            summary = "Get Character Summary",
            description = "Get character summary information."
    )
    @GetMapping("/summary/{characterId}")
    public CharacterSummaryDTO summary(
            @Parameter(description = "CharacterId to be obtained") @PathVariable("characterId") @Positive
            Long characterId) {
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
            @Parameter(description = "CharacterId to be obtained") @PathVariable("characterId") @Positive
            Long characterId) {
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
    @PostMapping("/latest/{name}")
    public Long getLatestIdByName(
            @Parameter(description = "Character name") @PathVariable("name") @NotBlank
            String name) {
        return characterService.getLatestIdByName(name, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "publishCharacter",
            summary = "Publish Character",
            description = "Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication."
    )
    @PostMapping(value = {"/publish/{characterId}/{visibility}", "/publish/{characterId}"})
    @PreAuthorize("hasPermission(#p0 + '|' + #p1, 'characterUpdateOp')")
    public Long publish(
            @Parameter(description = "The characterId to be published") @PathVariable("characterId") @Positive
            Long characterId,
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
            @Parameter(description = "The characterId to be added a backend") @PathVariable("characterId") @Positive
            Long characterId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The character backend to be added") @RequestBody @NotNull
            CharacterBackendDTO backend) {
        String characterUid = characterService.getUid(characterId);
        CharacterBackend characterBackend = backend.toCharacterBackend(characterUid);
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

        CharacterBackend updatedBackend = backend.toCharacterBackend(characterBackend.getCharacterUid());
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
        String characterUid = characterService.getBackendCharacterUid(characterBackendId);
        if (StringUtils.isBlank(characterUid)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid character backend");
        }
        return characterService.removeBackend(characterUid, characterBackendId);
    }

    @Operation(
            operationId = "getDefaultCharacterBackend",
            summary = "Get Default Character Backend",
            description = "Get the default backend configuration."
    )
    @GetMapping("/backend/default/{characterId}")
    @PreAuthorize("hasPermission(#p0, 'characterDefaultOp')")
    public CharacterBackendDetailsDTO getDefaultBackend(
            @Parameter(description = "The characterId to be queried") @PathVariable("characterId") @Positive
            Long characterId) {
        String characterUid = characterService.getUid(characterId);
        return CharacterBackendDetailsDTO.from(characterService.getDefaultBackend(characterUid));
    }

    @Operation(
            operationId = "listCharacterBackendIds",
            summary = "List Character Backend ids",
            description = "List character backend identifiers."
    )
    @GetMapping("/backend/ids/{characterId}")
    @PreAuthorize("hasPermission(#p0, 'characterDefaultOp')")
    public List<String> listBackendIds(
            @Parameter(description = "The characterId to be queried") @PathVariable("characterId") @Positive
            Long characterId) {
        String characterUid = characterService.getUid(characterId);
        return characterService.listBackendIds(characterUid);
    }

    @Operation(
            operationId = "listCharacterBackends",
            summary = "List Character Backends",
            description = "List character backends."
    )
    @GetMapping("/backends/{characterId}")
    @PreAuthorize("hasPermission(#p0, 'characterDefaultOp')")
    public List<CharacterBackendDetailsDTO> listBackends(
            @Parameter(description = "The characterId to be queried") @PathVariable("characterId") @Positive
            Long characterId) {
        String characterUid = characterService.getUid(characterId);
        return characterService.listBackends(characterUid)
                .stream()
                .map(CharacterBackendDetailsDTO::from)
                .toList();
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
        String characterUid = characterService.getBackendCharacterUid(characterBackendId);
        if (StringUtils.isBlank(characterUid)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid character backend");
        }
        return characterService.setDefaultBackend(characterUid, characterBackendId);
    }

    @Operation(
            operationId = "uploadCharacterPicture",
            summary = "Upload Character Picture",
            description = "Upload a picture of the character."
    )
    @PostMapping(value = "/picture/{characterId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasPermission(#p2, 'characterDefaultOp')")
    public String uploadPicture(
            HttpServletRequest request,
            @Parameter(description = "Character picture") @RequestParam("file") @NotNull
            MultipartFile file,
            @Parameter(description = "Character identifier") @PathVariable("characterId") @Positive
            Long characterId) {
        Properties properties = ConfigUtils.getProperties(configService, CONFIG_NAME);
        long maxSize = ConfigUtils.getLongOrDefault(properties, PICTURE_MAX_SIZE_KEY, DEFAULT_PICTURE_MAX_SIZE);
        int maxCount = ConfigUtils.getIntOrDefault(properties, PICTURE_MAX_COUNT_KEY, DEFAULT_PICTURE_MAX_COUNT);


        if (file.getSize() > maxSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size should be less than " + maxSize);
        }

        String characterUid = characterService.getUid(characterId);
        if (StringUtils.isBlank(characterUid)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to find character");
        }

        FileStore fileStore = StoreUtils.defaultFileStore();
        try {
            String dstDir = PUBLIC_DIR + AccountUtils.currentUser().getUserId() + "/character/picture/" + characterUid;
            String dstPath = FileUtils.transfer(file, fileStore, dstDir, maxCount);
            String shareUrl = fileStore.getShareUrl(dstPath, Integer.MAX_VALUE);
            if (StringUtils.isBlank(shareUrl)) {
                shareUrl = FileUtils.getDefaultPublicUrlForImage(request, dstPath);
            }
            return shareUrl;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
        }
    }

    @Operation(
            operationId = "deleteCharacterPicture",
            summary = "Delete Character Picture",
            description = "Delete a picture of the character by key."
    )
    @DeleteMapping("/picture/{key}")
    public Boolean deletePicture(
            @Parameter(description = "Image key") @PathVariable("key") @NotBlank
            String key) {
        FileStore fileStore = StoreUtils.defaultFileStore();
        try {
            String path = FileUtils.getDefaultPublicPathForImage(key);
            String characterUid = getCharacterUidFromPath(path);
            String userId = characterService.getOwnerByUid(characterUid);
            if (!AccountUtils.currentUser().getUserId().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Image doesn't belong to you");
            }
            fileStore.delete(path);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Operation(
            operationId = "listCharacterPictures",
            summary = "List Character Pictures",
            description = "List pictures of the character."
    )
    @GetMapping("/pictures/{characterId}")
    @PreAuthorize("hasPermission(#p1, 'characterDefaultOp')")
    public List<String> listPictures(
            HttpServletRequest request,
            @Parameter(description = "Character identifier") @PathVariable("characterId") @Positive
            Long characterId) {
        String characterUid = characterService.getUid(characterId);
        if (StringUtils.isBlank(characterUid)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to find character");
        }

        FileStore fileStore = StoreUtils.defaultFileStore();
        try {
            String dstDir = PUBLIC_DIR + AccountUtils.currentUser().getUserId() + "/character/picture/" + characterUid;
            return fileStore.list(dstDir, null, false)
                    .stream()
                    .map(path -> {
                        String shareUrl = fileStore.getShareUrl(path, Integer.MAX_VALUE);
                        if (StringUtils.isBlank(shareUrl)) {
                            shareUrl = FileUtils.getDefaultPublicUrlForImage(request, path);
                        }
                        return shareUrl;
                    })
                    .toList();
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    @Operation(
            operationId = "uploadCharacterAvatar",
            summary = "Upload Character Avatar",
            description = "Upload an avatar of the character."
    )
    @PostMapping(value = "/avatar/{characterId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String uploadAvatar(
            HttpServletRequest request,
            @Parameter(description = "Character avatar") @RequestParam("file") @NotNull
            MultipartFile file,
            @Parameter(description = "Character identifier") @PathVariable("characterId") @Positive
            Long characterId) {
        Properties properties = ConfigUtils.getProperties(configService, CONFIG_NAME);
        long maxSize = ConfigUtils.getLongOrDefault(properties, AVATAR_MAX_SIZE_KEY, DEFAULT_AVATAR_MAX_SIZE);
        int maxCount = ConfigUtils.getIntOrDefault(properties, AVATAR_MAX_COUNT_KEY, DEFAULT_AVATAR_MAX_COUNT);

        if (file.getSize() > maxSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size should be less than " + maxSize);
        }

        String characterUid = characterService.getUid(characterId);
        if (StringUtils.isBlank(characterUid)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to find character");
        }

        FileStore fileStore = StoreUtils.defaultFileStore();
        try {
            String dstDir = PUBLIC_DIR + AccountUtils.currentUser().getUserId() + "/character/avatar/" + characterUid;
            String dstPath = FileUtils.transfer(file, fileStore, dstDir, maxCount);
            String shareUrl = fileStore.getShareUrl(dstPath, Integer.MAX_VALUE);
            if (StringUtils.isBlank(shareUrl)) {
                shareUrl = FileUtils.getDefaultPublicUrlForImage(request, dstPath);
            }
            return shareUrl;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
        }
    }

    @Operation(
            operationId = "existsCharacterName",
            summary = "Check If Character Name Exists",
            description = "Check if the character name already exists."
    )
    @GetMapping("/exists/name/{name}")
    public Boolean existsName(
            @Parameter(description = "Name") @PathVariable("name") @NotBlank String name) {
        return characterService.existsName(name, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "newCharacterName",
            summary = "Create New Character Name",
            description = "Create a new character name starting with a desired name."
    )
    @GetMapping(value = "/create/name/{desired}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String createName(
            @Parameter(description = "Desired name") @PathVariable("desired") @NotBlank String desired) {
        return newUniqueName(desired);
    }
}
