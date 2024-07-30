package fun.freechat.api;

import fun.freechat.api.dto.*;
import fun.freechat.api.dto.conf.CharacterBackendConfigurationDTO;
import fun.freechat.api.dto.conf.CharacterConfigurationDTO;
import fun.freechat.api.util.*;
import fun.freechat.model.*;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.common.ConfigService;
import fun.freechat.service.common.FileStore;
import fun.freechat.service.enums.InfoType;
import fun.freechat.service.enums.SourceType;
import fun.freechat.service.enums.StatsType;
import fun.freechat.service.enums.Visibility;
import fun.freechat.service.prompt.PromptService;
import fun.freechat.service.prompt.PromptTaskService;
import fun.freechat.service.rag.RagTaskService;
import fun.freechat.service.stats.InteractiveStatsService;
import fun.freechat.service.util.StoreUtils;
import fun.freechat.util.AppMetaUtils;
import fun.freechat.util.TarUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static fun.freechat.api.util.ConfigUtils.*;
import static fun.freechat.api.util.FileUtils.*;
import static org.yaml.snakeyaml.nodes.Tag.MAP;

@Controller
@Tag(name = "Character")
@RequestMapping("/api/v1/character")
@ResponseBody
@Validated
@Slf4j
@SuppressWarnings("unused")
public class CharacterApi {
    @Value("${chat.memory.minMessageWindowSize:10}")
    private Integer minMessageWindowSize;
    @Value("${chat.memory.maxMessageWindowSize:500}")
    private Integer maxMessageWindowSize;
    @Value("${chat.memory.defaultMessageWindowSize:50}")
    private Integer defaultMessageWindowSize;
    @Value("${chat.memory.minLongTermMemoryWindowSize:0}")
    private Integer minLongTermMemoryWindowSize;
    @Value("${chat.memory.maxLongTermMemoryWindowSize:30}")
    private Integer maxLongTermMemoryWindowSize;
    @Value("${chat.memory.defaultLongTermMemoryWindowSize:5}")
    private Integer defaultLongTermMemoryWindowSize;
    @Value("${chat.rag.defaultMaxSegmentSize}")
    private Integer defaultMaxSegmentSize;
    @Value("${chat.rag.defaultMaxOverlapSize}")
    private Integer defaultMaxOverlapSize;
    @Autowired
    private CharacterService characterService;
    @Autowired
    private InteractiveStatsService interactiveStatsService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private PromptService promptService;
    @Autowired
    private PromptTaskService promptTaskService;
    @Autowired
    private RagTaskService ragTaskService;

    private void increaseReferCount(String characterUid) {
        interactiveStatsService.add(AccountUtils.currentUser().getUserId(),
                InfoType.CHARACTER, characterUid, StatsType.REFER_COUNT, 1L);
    }

    private String getCharacterUidFromPath(String path) {
        String[] parts = path.split("/");
        if (parts.length < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid key");
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
        characterInfo.getLeft().setCharacterUid(characterService.getUid(characterId));
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
        if (characterDetails == null) {
            return null;
        }

        var characterInfo = Pair.of(characterDetails.getLeft(), characterDetails.getMiddle());
        String characterUid = characterService.getUid(characterId);
        CharacterUtils.resetCharacterInfoPair(characterInfo, characterUid);
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
        if (characterBackend == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid character backend");
        }
        Integer messageWindowSize = characterBackend.getMessageWindowSize();
        if (messageWindowSize == null) {
            characterBackend.setMessageWindowSize(defaultMessageWindowSize);
        } else if (messageWindowSize < minMessageWindowSize || messageWindowSize > maxMessageWindowSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Message window size should be between " + minMessageWindowSize + " and " + maxMessageWindowSize);
        }
        Integer longTermMemoryWindowSize = characterBackend.getLongTermMemoryWindowSize();
        if (longTermMemoryWindowSize == null) {
            characterBackend.setLongTermMemoryWindowSize(defaultLongTermMemoryWindowSize);
        } else if (longTermMemoryWindowSize < minLongTermMemoryWindowSize ||
                longTermMemoryWindowSize > maxLongTermMemoryWindowSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Long term memory window size should be between " + minLongTermMemoryWindowSize +
                            " and " + maxLongTermMemoryWindowSize);
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
        if (characterBackend == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to find character backend: " + characterBackendId);
        }

        CharacterBackend updatedBackend = backend.toCharacterBackend(characterBackend.getCharacterUid());
        updatedBackend.setBackendId(characterBackend.getBackendId());

        Integer messageWindowSize = updatedBackend.getMessageWindowSize();
        if (Objects.nonNull(messageWindowSize) &&
                (messageWindowSize < minMessageWindowSize || messageWindowSize > maxMessageWindowSize)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Message window size should be between " + minMessageWindowSize + " and " + maxMessageWindowSize);
        }

        Integer longTermMemoryWindowSize = updatedBackend.getLongTermMemoryWindowSize();
        if (Objects.nonNull(longTermMemoryWindowSize) &&
                (longTermMemoryWindowSize < minLongTermMemoryWindowSize ||
                        longTermMemoryWindowSize > maxLongTermMemoryWindowSize)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Long term memory window size should be between " + minLongTermMemoryWindowSize +
                            " and " + maxLongTermMemoryWindowSize);
        }

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
        Properties properties = configService.load();
        long maxSize = ConfigUtils.getOrDefault(properties, PICTURE_MAX_SIZE_KEY, DEFAULT_PICTURE_MAX_SIZE);
        int maxCount = ConfigUtils.getOrDefault(properties, PICTURE_MAX_COUNT_KEY, DEFAULT_PICTURE_MAX_COUNT);


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
            String path = FileUtils.getDefaultPublicPath(key);
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
        Properties properties = configService.load();
        long maxSize = ConfigUtils.getOrDefault(properties, AVATAR_MAX_SIZE_KEY, DEFAULT_AVATAR_MAX_SIZE);
        int maxCount = ConfigUtils.getOrDefault(properties, AVATAR_MAX_COUNT_KEY, DEFAULT_AVATAR_MAX_COUNT);

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
            operationId = "uploadCharacterDocument",
            summary = "Upload Character Document",
            description = "Upload a document of the character."
    )
    @PostMapping(value = "/document/{characterId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasPermission(#p2, 'characterDefaultOp')")
    public String uploadDocument(
            HttpServletRequest request,
            @Parameter(description = "Character document") @RequestParam("file") @NotNull
            MultipartFile file,
            @Parameter(description = "Character identifier") @PathVariable("characterId") @Positive
            Long characterId) {
        Properties properties = configService.load();
        long maxSize = ConfigUtils.getOrDefault(properties, DOCUMENT_MAX_SIZE_KEY, DEFAULT_DOCUMENT_MAX_SIZE);
        int maxCount = ConfigUtils.getOrDefault(properties, DOCUMENT_MAX_COUNT_KEY, DEFAULT_DOCUMENT_MAX_COUNT);


        if (file.getSize() > maxSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size should be less than " + maxSize);
        }

        String characterUid = characterService.getUid(characterId);
        if (StringUtils.isBlank(characterUid)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to find character");
        }

        FileStore fileStore = StoreUtils.defaultFileStore();
        String userId = AccountUtils.currentUser().getUserId();
        try {
            String dstDir = PRIVATE_DIR + userId + "/character/document/" + characterUid;
            String dstPath = FileUtils.transfer(file, fileStore, dstDir, maxCount);
            return FileUtils.getDefaultPrivateUrlForDocument(request, dstPath, userId);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
        }
    }

    @Operation(
            operationId = "deleteCharacterDocument",
            summary = "Delete Character Document",
            description = "Delete a document of the character by key."
    )
    @DeleteMapping("/document/{key}")
    public Boolean deleteDocument(
            @Parameter(description = "Document key") @PathVariable("key") @NotBlank
            String key) {
        FileStore fileStore = StoreUtils.defaultFileStore();
        String userId = AccountUtils.currentUser().getUserId();
        try {
            String path = FileUtils.getDefaultPrivatePath(key, userId);
            String characterUid = getCharacterUidFromPath(path);
            String ownerId = characterService.getOwnerByUid(characterUid);
            if (!userId.equals(ownerId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Document doesn't belong to you");
            }
            fileStore.delete(path);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Operation(
            operationId = "listCharacterDocuments",
            summary = "List Character Documents",
            description = "List documents of the character."
    )
    @GetMapping("/documents/{characterId}")
    @PreAuthorize("hasPermission(#p1, 'characterDefaultOp')")
    public List<String> listDocuments(
            HttpServletRequest request,
            @Parameter(description = "Character identifier") @PathVariable("characterId") @Positive
            Long characterId) {
        String characterUid = characterService.getUid(characterId);
        if (StringUtils.isBlank(characterUid)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to find character");
        }

        FileStore fileStore = StoreUtils.defaultFileStore();
        String userId = AccountUtils.currentUser().getUserId();
        try {
            String dstDir = PRIVATE_DIR + userId + "/character/document/" + characterUid;
            return fileStore.list(dstDir, null, false)
                    .stream()
                    .map(path -> FileUtils.getDefaultPrivateUrlForDocument(request, path, userId))
                    .toList();
        } catch (IOException e) {
            return Collections.emptyList();
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
        return CharacterUtils.newUniqueName(desired);
    }

    @Operation(
            operationId = "exportCharacter",
            summary = "Export Character Configuration",
            description = "Export character configuration in tar.gz format, including settings, documents and pictures."
    )
    @GetMapping(value = "/export/{characterId}", produces = "application/gzip")
    public void exportCharacter(
            @Parameter(description = "Character identifier") @PathVariable("characterId") @Positive
            Long characterId,
            HttpServletResponse response) {
        User currentUser = AccountUtils.currentUser();
        String currentUserId = currentUser.getUserId();
        var characterTriple = characterService.details(characterId, currentUser);
        CharacterCreateDTO characterCreateDTO = CharacterCreateDTO.from(
                Pair.of(characterTriple.getLeft(), characterTriple.getMiddle()));

        List<CharacterBackend> backends = characterTriple.getRight();
        List<CharacterBackendConfigurationDTO> characterBackendConfList = Optional.ofNullable(characterTriple.getRight())
                .orElse(Collections.emptyList())
                .stream()
                .map(backend -> {
                    CharacterBackendDTO backendDTO = CharacterBackendDTO.from(backend);

                    PromptTask chatPromptTask = Objects.nonNull(backend.getChatPromptTaskId()) ?
                            promptTaskService.get(backend.getChatPromptTaskId()) : null;

                    PromptCreateDTO chatPromptCreateDTO = Optional.ofNullable(chatPromptTask)
                            .map(PromptTask::getPromptUid)
                            .map(promptUid -> promptService.getLatestIdByUid(promptUid, currentUser))
                            .map(promptId -> promptService.details(promptId, currentUser))
                            .map(PromptCreateDTO::from)
                            .orElse(null);

                    PromptTaskDTO chatPromptTaskDTO = Objects.nonNull(chatPromptTask) ?
                            PromptTaskDTO.from(chatPromptTask.withPromptUid(null)) : null;

                    PromptTask greetingPromptTask = Objects.nonNull(backend.getGreetingPromptTaskId()) ?
                            promptTaskService.get(backend.getGreetingPromptTaskId()) : null;

                    PromptCreateDTO greetingPromptCreateDTO = Optional.ofNullable(greetingPromptTask)
                            .map(PromptTask::getPromptUid)
                            .map(promptUid -> promptService.getLatestIdByUid(promptUid, currentUser))
                            .map(promptId -> promptService.details(promptId, currentUser))
                            .map(PromptCreateDTO::from)
                            .orElse(null);

                    PromptTaskDTO greetingPromptTaskDTO = Objects.nonNull(greetingPromptTask) ?
                            PromptTaskDTO.from(greetingPromptTask.withPromptUid(null)) : null;

                    return CharacterBackendConfigurationDTO.builder()
                            .backend(backendDTO)
                            .chatPrompt(chatPromptCreateDTO)
                            .chatPromptTask(chatPromptTaskDTO)
                            .greetingPrompt(greetingPromptCreateDTO)
                            .greetingPromptTask(greetingPromptTaskDTO)
                            .build();
                })
                .toList();

        CharacterConfigurationDTO characterConf = CharacterConfigurationDTO.builder()
                .character(characterCreateDTO)
                .backends(characterBackendConfList)
                .build();

        LinkedList<Triple<String, InputStream, Long>> entries = new LinkedList<>();

        // manifest.yml
        String manifestFilename = "manifest.yml";
        byte[] manifestContent = new ManifestInfo().toYaml().getBytes(StandardCharsets.UTF_8);
        entries.add(Triple.of(manifestFilename,
                new ByteArrayInputStream(manifestContent),
                (long) manifestContent.length));

        // character.json
        String characterFilename = "character.json";
        String characterJson = characterConf.toJson();
        byte[] characterContent = characterJson.getBytes(StandardCharsets.UTF_8);
        entries.add(Triple.of(characterFilename,
                new ByteArrayInputStream(characterContent),
                (long) characterContent.length));

        String characterUid = characterService.getUid(characterId);
        if (StringUtils.isNotBlank(characterUid)) {
            // documents
            entries.addAll(documents(currentUserId, characterUid));

            // pictures
            entries.addAll(pictures(currentUserId, characterUid));

            // avatars
            avatar(currentUserId, characterUid, characterTriple.getLeft().getAvatar())
                    .ifPresent(entries::add);
        }

        String encodedFilename = URLEncoder.encode(
                characterConf.getCharacter().getName() + ".tar.gz", StandardCharsets.UTF_8);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename);
        response.setContentType("application/gzip");

        try {
            TarUtils.compressGzip(entries, response.getOutputStream());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to compress character configuration", e);
        }
    }

    private static List<Triple<String, InputStream, Long>> documents(String userId, String characterUid) {
        String dir = PRIVATE_DIR + userId + "/character/document/" + characterUid;
        return listFileStreams("documents", dir);
    }

    private static List<Triple<String, InputStream, Long>> pictures(String userId, String characterUid) {
        String dir = PUBLIC_DIR + userId + "/character/picture/" + characterUid;
        return listFileStreams("pictures", dir);
    }

    private Optional<Triple<String, InputStream, Long>> avatar(String userId, String characterUid, String avatarUrl) {
        if (StringUtils.isBlank(avatarUrl)) {
            return Optional.empty();
        }

        FileStore fileStore = StoreUtils.defaultFileStore();
        String key = getKeyFromUrl(avatarUrl);
        String path = FileUtils.getDefaultPublicPath(key);
        String dir = PUBLIC_DIR + userId + "/character/avatar/" + characterUid;
        if (path.startsWith(dir) && fileStore.exists(path)) {
            try {
                String filePath = "avatar" + path.substring(dir.length());
                Long fileSize = fileStore.size(path);
                InputStream fileStream = fileStore.read(path);
                return Optional.of(Triple.of(filePath, fileStream, fileSize));
            } catch (IOException ignored) {
            }
        }
        return Optional.empty();
    }

    private static List<Triple<String, InputStream, Long>> listFileStreams(String prefix, String dir) {
        FileStore fileStore = StoreUtils.defaultFileStore();
        try {
            return fileStore.list(dir, null, false)
                    .stream()
                    .map(path -> {
                        try {
                            String filePath = prefix + path.substring(dir.length());
                            Long fileSize = fileStore.size(path);
                            InputStream fileStream = fileStore.read(path);
                            return Triple.of(filePath, fileStream, fileSize);
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    @Operation(
            operationId = "importCharacter",
            summary = "Import Character Configuration",
            description = "Import character configuration from a tar.gz file."
    )
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long importCharacter(
            HttpServletRequest request,
            @Parameter(description = "Character avatar") @RequestParam("file") @NotNull
            MultipartFile file) {
        FileStore fileStore = StoreUtils.defaultFileStore();
        String userId = AccountUtils.currentUser().getUserId();

        try {
            Path tempDir = Files.createTempDirectory(file.getOriginalFilename() + "-");
            TarUtils.extractGzip(file.getInputStream(), tempDir);

            handleManifestYaml(tempDir);
            CharacterInfo characterInfo = handleCharacterJson(tempDir);
            handleAvatar(request, userId, characterInfo, tempDir);
            handlePictures(userId, characterInfo, tempDir);
            handleDocuments(request, userId, characterInfo, tempDir);

            cleanUpTempDirectory(tempDir);
            return characterInfo.getCharacterId();
        } catch (IOException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to import character configuration", e);
        }
    }

    private void handleManifestYaml(Path confPath) throws IOException {
        Path manifestFile = confPath.resolve("manifest.yml");
        String manifestYaml = Files.readString(manifestFile, StandardCharsets.UTF_8);
        ManifestInfo manifestInfo = ManifestInfo.fromYaml(manifestYaml);

        if (!ManifestInfo.API_VERSION.equals(manifestInfo.getApiVersion())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incompatible character configuration");
        }
    }

    private CharacterInfo handleCharacterJson(Path confPath) throws IOException {
        Path characterFile = confPath.resolve("character.json");
        String characterJson = Files.readString(characterFile, StandardCharsets.UTF_8);
        CharacterConfigurationDTO characterConf = CharacterConfigurationDTO.fromJson(characterJson);

        // create character
        CharacterCreateDTO characterCreateDTO = characterConf.getCharacter();
        var characterPair = characterCreateDTO.toCharacterInfo();
        CharacterUtils.resetCharacterInfoPair(characterPair, null);
        if (!characterService.create(characterPair)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create character.");
        }

        CharacterInfo characterInfo = characterPair.getLeft();
        String characterUid =characterInfo.getCharacterUid();

        // create backends
        for (CharacterBackendConfigurationDTO backendConf : characterConf.getBackends()) {
            CharacterBackendDTO backendDTO = backendConf.getBackend();
            if (backendDTO == null) {
                continue;
            }

            // create prompt tasks
            String chatPromptTaskId = createPromptTask(
                    backendConf.getChatPrompt(), backendConf.getChatPromptTask());
            String greetingPromptTaskId = createPromptTask(
                    backendConf.getGreetingPrompt(), backendConf.getGreetingPromptTask());

            // add backend
            backendDTO.setChatPromptTaskId(chatPromptTaskId);
            backendDTO.setGreetingPromptTaskId(greetingPromptTaskId);
            CharacterBackend backend = backendDTO.toCharacterBackend(characterUid);

            characterService.addBackend(backend);
        }

        return characterInfo;
    }

    private void handleAvatar(HttpServletRequest request,
                              String userId,
                              CharacterInfo characterInfo,
                              Path confPath) throws IOException {
        Path srcPath = confPath.resolve("avatar");
        String dir = PUBLIC_DIR + userId + "/character/avatar/" + characterInfo.getCharacterUid();
        FileStore fileStore = StoreUtils.defaultFileStore();
        try (Stream<Path> stream = Files.list(srcPath)) {
            stream.map(avatarPath -> {
                        if (Files.isRegularFile(avatarPath)) {
                            try {
                                return FileUtils.move(avatarPath, fileStore, dir);
                            } catch (IOException e) {
                                log.warn("Failed to move file from {} to {}", avatarPath, dir, e);
                            }
                        }
                        return null;
                    })
                    .filter(StringUtils::isNotBlank)
                    .findAny()
                    .map(avatarFile -> {
                        String shareUrl = fileStore.getShareUrl(avatarFile, Integer.MAX_VALUE);
                        if (StringUtils.isBlank(shareUrl)) {
                            shareUrl = FileUtils.getDefaultPublicUrlForImage(request, avatarFile);
                        }
                        return shareUrl;
                    })
                    .ifPresent(avatarUrl -> {
                        CharacterInfo newCharacterInfo = new CharacterInfo()
                                .withCharacterId(characterInfo.getCharacterId())
                                .withAvatar(avatarUrl);
                        characterService.update(Pair.of(newCharacterInfo, null));
                    });
        }
    }

    private void handlePictures(String userId,
                                CharacterInfo characterInfo,
                                Path confPath) throws IOException {
        Path srcPath = confPath.resolve("pictures");
        String dir = PUBLIC_DIR + userId + "/character/picture/" + characterInfo.getCharacterUid();
        FileStore fileStore = StoreUtils.defaultFileStore();
        try (Stream<Path> stream = Files.list(srcPath)) {
            stream.forEach(picturePath -> {
                if (Files.isRegularFile(picturePath)) {
                    try {
                        FileUtils.move(picturePath, fileStore, dir);
                    } catch (IOException e) {
                        log.warn("Failed to move file from {} to {}", picturePath, dir, e);
                    }
                }
            });
        }
    }

    private void handleDocuments(HttpServletRequest request,
                                 String userId,
                                 CharacterInfo characterInfo,
                                 Path confPath) throws IOException {
        Path srcPath = confPath.resolve("documents");
        String dir = PRIVATE_DIR + userId + "/character/document/" + characterInfo.getCharacterUid();
        FileStore fileStore = StoreUtils.defaultFileStore();
        try (Stream<Path> stream = Files.list(srcPath)) {
            stream.map(documentPath -> {
                        if (Files.isRegularFile(documentPath)) {
                            try {
                                return FileUtils.move(documentPath, fileStore, dir);
                            } catch (IOException e) {
                                log.warn("Failed to move file from {} to {}", documentPath, dir, e);
                            }
                        }
                        return null;
                    })
                    .filter(StringUtils::isNotBlank)
                    .forEach(documentFile -> {
                        RagTask ragTask = new RagTask()
                                .withCharacterUid(characterInfo.getCharacterUid())
                                .withSourceType(SourceType.FILE.text())
                                .withSource(documentFile)
                                .withMaxSegmentSize(defaultMaxSegmentSize)
                                .withMaxOverlapSize(defaultMaxOverlapSize);
                        ragTaskService.create(ragTask);
                    });
        }
    }

    private String createPromptTask(PromptCreateDTO promptCreateDTO, PromptTaskDTO promptTaskDTO) {
        if (promptCreateDTO == null || promptTaskDTO == null) {
            return null;
        }

        var promptInfo = promptCreateDTO.toPromptInfo();
        if (Objects.nonNull(promptInfo)) {
            PromptUtils.resetPromptInfoTriple(promptInfo, null);
            if (!promptService.create(promptInfo)) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create prompt.");
            }

            PromptTask promptTask = promptTaskDTO.toPromptTask();
            promptTask.setPromptUid(promptInfo.getLeft().getPromptUid());

            if (!promptTaskService.create(promptTask)) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create prompt task.");
            }

            return promptTask.getTaskId();
        }
        return null;
    }

    private static void cleanUpTempDirectory(Path tempDir) throws IOException {
        try (Stream<Path> stream = Files.walk(tempDir)) {
            stream.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    log.warn("Failed to delete file {}", p, e);
                }
            });
        }
    }

    @Data
    static public class ManifestInfo {
        static final String API_VERSION = "v1";

        private String apiVersion;
        private String appVersion;
        private String appUrl;

        public ManifestInfo() {
            apiVersion = API_VERSION;
            appVersion = AppMetaUtils.getVersion();
            appUrl = AppMetaUtils.getUrl();
        }

        String toYaml() {
            return yaml().dump(this);
        }

        static ManifestInfo fromYaml(String yaml) {
            return yaml().loadAs(yaml, ManifestInfo.class);
        }

        static Yaml yaml() {
            DumperOptions options = new DumperOptions();
            options.setIndent(2);
            options.setPrettyFlow(true);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            Representer representer = new Representer(options);
            representer.getPropertyUtils().setSkipMissingProperties(true);
            representer.addClassTag(ManifestInfo.class, MAP);

            return new Yaml(representer);
        }
    }
}
