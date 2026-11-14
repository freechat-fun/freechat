package fun.freechat.api;

import static fun.freechat.api.util.ConfigUtils.*;
import static fun.freechat.api.util.FileUtils.getKeyFromUrl;
import static fun.freechat.service.util.StoreUtils.PRIVATE_DIR;
import static fun.freechat.service.util.StoreUtils.PUBLIC_DIR;
import static org.yaml.snakeyaml.nodes.Tag.MAP;

import fun.freechat.api.dto.*;
import fun.freechat.api.dto.conf.CharacterBackendConfigurationDTO;
import fun.freechat.api.dto.conf.CharacterConfigurationDTO;
import fun.freechat.api.dto.conf.ConnectorConfigurationDTO;
import fun.freechat.api.util.*;
import fun.freechat.channels.telegram.TelegramChannelManager;
import fun.freechat.model.*;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.TtsService;
import fun.freechat.service.common.FileStore;
import fun.freechat.service.config.RuntimeConfig;
import fun.freechat.service.connectors.CharacterConnectorConfiguration;
import fun.freechat.service.connectors.ConnectorCharacterPolicy;
import fun.freechat.service.connectors.ConnectorConfiguration;
import fun.freechat.service.connectors.ConnectorException;
import fun.freechat.service.connectors.ConnectorService;
import fun.freechat.service.enums.InfoType;
import fun.freechat.service.enums.SourceType;
import fun.freechat.service.enums.StatsType;
import fun.freechat.service.enums.Visibility;
import fun.freechat.service.prompt.PromptService;
import fun.freechat.service.prompt.PromptTaskService;
import fun.freechat.service.rag.RagTaskService;
import fun.freechat.service.stats.InteractiveStatsService;
import fun.freechat.service.tools.ToolRegistry;
import fun.freechat.service.util.StoreUtils;
import fun.freechat.util.AppMetaUtils;
import fun.freechat.util.IdUtils;
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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

@RestController
@Tag(name = "Character")
@RequestMapping("/api/v2/character")
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
    private TelegramChannelManager telegramChannelManager;

    @Autowired
    private InteractiveStatsService interactiveStatsService;

    @Autowired
    private RuntimeConfig runtimeConfig;

    @Autowired
    private PromptService promptService;

    @Autowired
    private PromptTaskService promptTaskService;

    @Autowired
    private RagTaskService ragTaskService;

    @Autowired
    private TtsService ttsService;

    @Autowired
    private ToolRegistry toolRegistry;

    @Autowired
    private fun.freechat.service.skills.SkillCharacterPolicy skillPolicy;

    @Autowired
    private ConnectorCharacterPolicy connectorPolicy;

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private void validateCharacterTools(String ext) {
        try {
            toolRegistry.validateConfiguration(ext);
        } catch (IllegalArgumentException ignored) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid character tool configuration");
        }
    }

    private void increaseReferCount(String characterUid) {
        interactiveStatsService.add(
                AccountUtils.currentUser().getUserId(), InfoType.CHARACTER, characterUid, StatsType.REFER_COUNT, 1L);
    }

    private String getCharacterUidFromPath(String path) {
        String[] parts = path.split("/");
        if (parts.length < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid key");
        }
        return parts[parts.length - 2];
    }

    @Operation(operationId = "searchCharacterSummary", summary = "Search Character Summary", description = """
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
                    """)
    @PostMapping("/search")
    public List<CharacterSummaryDTO> search(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "Query conditions",
                            content = @Content(examples = @ExampleObject("""
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
                                    """)))
                    @RequestBody
                    @NotNull
                    CharacterQueryDTO query) {
        return characterService.search(query.toCharacterInfoQuery(), AccountUtils.currentUser()).stream()
                .map(CharacterSummaryDTO::from)
                .toList();
    }

    @Operation(
            operationId = "searchCharacterDetails",
            summary = "Search Character Details",
            description = "Same as /api/v2/character/search, but returns detailed information of the character.")
    @PostMapping("/details/search")
    public List<CharacterDetailsDTO> detailsSearch(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "Query conditions",
                            content = @Content(examples = @ExampleObject("""
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
                                    """)))
                    @RequestBody
                    @NotNull
                    CharacterQueryDTO query) {
        return characterService.searchDetails(query.toCharacterInfoQuery(), AccountUtils.currentUser()).stream()
                .map(CharacterDetailsDTO::from)
                .toList();
    }

    @Operation(
            operationId = "batchSearchCharacterSummary",
            summary = "Batch Search Character Summaries",
            description = "Batch call shortcut for /api/v2/character/search.")
    @PostMapping("/batch/search")
    public List<List<CharacterSummaryDTO>> batchSearch(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "Query conditions",
                            content = @Content(examples = @ExampleObject("""
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
                                    """)))
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
            description = "Batch call shortcut for /api/v2/character/details/search.")
    @PostMapping("/batch/details/search")
    public List<List<CharacterDetailsDTO>> batchDetailsSearch(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "Query conditions",
                            content = @Content(examples = @ExampleObject("""
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
                                    """)))
                    @RequestBody
                    @NotNull
                    List<CharacterQueryDTO> queries) {
        List<List<CharacterDetailsDTO>> results = new ArrayList<>(queries.size());
        for (CharacterQueryDTO query : queries) {
            results.add(detailsSearch(query));
        }
        return results;
    }

    @Operation(operationId = "createCharacter", summary = "Create Character", description = "Create a character.")
    @PostMapping("")
    @PreAuthorize("hasPermission(#p0.visibility, 'characterCreateOp')")
    public Long create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "Information of the character to be created",
                            content = @Content(examples = @ExampleObject("""
                                    {
                                      "name": "A Test Character",
                                      "description": "A character description",
                                      "profile": "Hello world. I'm Jack",
                                      "tags": [
                                        "test", "demo"
                                      ]
                                    }
                                    """)))
                    @RequestBody
                    @NotNull
                    CharacterCreateDTO character) {
        validateCharacterTools(character.getExt());
        skillPolicy.validate(AccountUtils.currentUser().getUserId(), character.getExt(), character.getDraft());
        connectorPolicy.validate(AccountUtils.currentUser().getUserId(), character.getExt(), character.getDraft());
        var characterInfo = character.toCharacterInfo();
        if (!characterService.create(characterInfo)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create character.");
        }
        return characterInfo.getLeft().getCharacterId();
    }

    @Operation(
            operationId = "updateCharacter",
            summary = "Update Character",
            description =
                    "Update character, refer to /api/v2/character/create, required field: characterId. Returns success or failure.")
    @PutMapping("/{characterId}")
    @PreAuthorize("hasPermission(#p0 + '|' + #p1.visibility, 'characterUpdateOp')")
    public Boolean update(
            @Parameter(description = "The characterId to be updated") @PathVariable("characterId") @Positive
                    Long characterId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "The character information to be updated",
                            content = @Content(examples = @ExampleObject("""
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
                                    """)))
                    @RequestBody
                    @NotNull
                    CharacterUpdateDTO character) {
        AccountUtils.currentUser();
        validateCharacterTools(character.getExt());
        skillPolicy.validateUpdate(characterId, character.getExt(), character.getDraft());
        connectorPolicy.validateUpdate(characterId, character.getExt(), character.getDraft());
        var characterInfo = character.toCharacterInfo(characterId);
        characterInfo.getLeft().setCharacterUid(characterService.getUid(characterId));
        return characterService.update(characterInfo);
    }

    @Operation(operationId = "cloneCharacter", summary = "Clone Character", description = """
                    Enter the characterId, generate a new record, the content is basically the same as the original character, but the following fields are different:
                    - Version number is 1
                    - Visibility is private
                    - The parent character is the source characterId
                    - The creation time is the current moment.
                    - All statistical indicators are zeroed.

                    Return the new characterId.
                    """)
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
            description = "Delete character. Returns success or failure.")
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
            description = "Delete character by name. return the list of successfully deleted characterIds.")
    @DeleteMapping("/name/{name}")
    public List<Long> deleteByName(
            @Parameter(description = "The character name to be deleted") @PathVariable("name") @NotBlank String name) {
        return characterService.deleteByName(name, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "deleteCharacterByUid",
            summary = "Delete Character by Uid",
            description = "Delete character by uid. return the list of successfully deleted characterIds.")
    @DeleteMapping("/uid/{characterUid}")
    public List<Long> deleteByUid(
            @Parameter(description = "The character uid to be deleted") @PathVariable("characterUid") @NotBlank
                    String characterUid) {
        return characterService.deleteByUid(characterUid, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "getCharacterSummary",
            summary = "Get Character Summary",
            description = "Get character summary information.")
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
            description = "Get character detailed information.")
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
            description = "Calculate the number of characters according to the specified query conditions.")
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
            description = "List the versions and corresponding characterIds by character name.")
    @PostMapping("/versions/{name}")
    public List<CharacterItemForNameDTO> versions(
            @Parameter(description = "Character name") @PathVariable("name") @NotBlank String name) {
        return characterService.listVersionsByName(name, AccountUtils.currentUser()).stream()
                .map(CharacterItemForNameDTO::from)
                .toList();
    }

    @Operation(
            operationId = "getCharacterLatestIdByName",
            summary = "Get Latest Character Id by Name",
            description = "Get latest characterId by character name.")
    @PostMapping("/latest/{name}")
    public Long getLatestIdByName(
            @Parameter(description = "Character name") @PathVariable("name") @NotBlank String name) {
        return characterService.getLatestIdByName(name, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "publishCharacter",
            summary = "Publish Character",
            description =
                    "Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.")
    @PostMapping(value = {"/publish/{characterId}/{visibility}", "/publish/{characterId}"})
    @PreAuthorize("hasPermission(#p0 + '|' + #p1, 'characterUpdateOp')")
    public Long publish(
            @Parameter(description = "The characterId to be published") @PathVariable("characterId") @Positive
                    Long characterId,
            @Parameter(description = "Visibility: public | private | ...") @PathVariable("visibility")
                    Optional<String> visibility) {
        User currentUser = AccountUtils.currentUser();
        connectorPolicy.validatePublication(characterId);
        Visibility publishVisibility = visibility.map(Visibility::of).orElse(Visibility.PUBLIC);
        return characterService.publish(characterId, publishVisibility, currentUser);
    }

    private static final java.util.regex.Pattern TG_BOT_TOKEN_PATTERN =
            java.util.regex.Pattern.compile("^(\\d+):[A-Za-z0-9_-]+$");

    private static void normalizeTgBotFields(CharacterBackend backend) {
        String token = backend.getTgBotToken();
        Long suppliedBotId = backend.getTgBotId();
        if (StringUtils.isBlank(token)) {
            if (suppliedBotId != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tgBotId provided without tgBotToken");
            }
            return;
        }
        java.util.regex.Matcher m = TG_BOT_TOKEN_PATTERN.matcher(token);
        if (!m.matches()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid tgBotToken format (expected '<bot_id>:<secret>')");
        }
        Long derivedBotId = Long.parseLong(m.group(1));
        if (suppliedBotId != null && !suppliedBotId.equals(derivedBotId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "tgBotId does not match the bot id encoded in tgBotToken");
        }
        backend.setTgBotId(derivedBotId);
    }

    @Operation(
            operationId = "addCharacterBackend",
            summary = "Add Character Backend",
            description = "Add a backend configuration for a character.")
    @PostMapping(value = "/backend/{characterUid}", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasPermission(#p0 + '|' + #p1.chatPromptTaskId, 'characterBackendCreateOp')")
    public String addBackend(
            @Parameter(description = "The characterUid to be added a backend") @PathVariable("characterUid") @NotBlank
                    String characterUid,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The character backend to be added")
                    @RequestBody
                    @NotNull
                    CharacterBackendDTO backend) {
        CharacterBackend characterBackend = backend.toCharacterBackend(characterUid);
        if (characterBackend == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid character backend");
        }
        normalizeTgBotFields(characterBackend);
        Integer messageWindowSize = characterBackend.getMessageWindowSize();
        if (messageWindowSize == null) {
            characterBackend.setMessageWindowSize(defaultMessageWindowSize);
        } else if (messageWindowSize < minMessageWindowSize || messageWindowSize > maxMessageWindowSize) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Message window size should be between " + minMessageWindowSize + " and " + maxMessageWindowSize);
        }
        Integer longTermMemoryWindowSize = characterBackend.getLongTermMemoryWindowSize();
        if (longTermMemoryWindowSize == null) {
            characterBackend.setLongTermMemoryWindowSize(defaultLongTermMemoryWindowSize);
        } else if (longTermMemoryWindowSize < minLongTermMemoryWindowSize
                || longTermMemoryWindowSize > maxLongTermMemoryWindowSize) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Long term memory window size should be between " + minLongTermMemoryWindowSize + " and "
                            + maxLongTermMemoryWindowSize);
        }
        return characterService.addBackend(characterBackend);
    }

    @Operation(
            operationId = "updateCharacterBackend",
            summary = "Update Character Backend",
            description = "Update a backend configuration.")
    @PutMapping("/backend/{characterBackendId}")
    @PreAuthorize("hasPermission(#p0 + '|' + #p1.chatPromptTaskId, 'characterBackendUpdateOp')")
    public Boolean updateBackend(
            @Parameter(description = "The characterBackendId to be updated")
                    @PathVariable("characterBackendId")
                    @NotBlank
                    String characterBackendId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "The character backend configuration to be updated")
                    @RequestBody
                    @NotNull
                    CharacterBackendDTO backend) {
        CharacterBackend characterBackend = characterService.getBackend(characterBackendId);
        if (characterBackend == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Failed to find character backend: " + characterBackendId);
        }

        CharacterBackend updatedBackend = backend.toCharacterBackend(characterBackend.getCharacterUid());
        updatedBackend.setBackendId(characterBackend.getBackendId());
        normalizeTgBotFields(updatedBackend);

        Integer messageWindowSize = updatedBackend.getMessageWindowSize();
        if (messageWindowSize != null
                && (messageWindowSize < minMessageWindowSize || messageWindowSize > maxMessageWindowSize)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Message window size should be between " + minMessageWindowSize + " and " + maxMessageWindowSize);
        }

        Integer longTermMemoryWindowSize = updatedBackend.getLongTermMemoryWindowSize();
        if (longTermMemoryWindowSize != null
                && (longTermMemoryWindowSize < minLongTermMemoryWindowSize
                        || longTermMemoryWindowSize > maxLongTermMemoryWindowSize)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Long term memory window size should be between " + minLongTermMemoryWindowSize + " and "
                            + maxLongTermMemoryWindowSize);
        }

        return characterService.updateBackend(updatedBackend);
    }

    @Operation(
            operationId = "removeCharacterBackend",
            summary = "Remove Character Backend",
            description = "Remove a backend configuration.")
    @DeleteMapping("/backend/{characterBackendId}")
    @PreAuthorize("hasPermission(#p0, 'characterBackendDefaultOp')")
    public Boolean removeBackend(
            @Parameter(description = "The characterBackendId to be removed")
                    @PathVariable("characterBackendId")
                    @NotBlank
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
            description = "Get the default backend configuration.")
    @GetMapping("/backend/default/{characterUid}")
    @PreAuthorize("hasPermission(#p0, 'characterDefaultOpByUid')")
    public CharacterBackendDetailsDTO getDefaultBackend(
            @Parameter(description = "The characterUid to be queried") @PathVariable("characterUid") @NotBlank
                    String characterUid) {
        return enrichTgFields(CharacterBackendDetailsDTO.from(characterService.getDefaultBackend(characterUid)));
    }

    private CharacterBackendDetailsDTO enrichTgFields(CharacterBackendDetailsDTO dto) {
        if (dto == null || StringUtils.isBlank(dto.getBackendId())) {
            return dto;
        }
        dto.setTgBotUsername(telegramChannelManager.getUsername(dto.getBackendId()));
        dto.setTgBotLink(telegramChannelManager.getInviteLink(dto.getBackendId()));
        return dto;
    }

    @Operation(
            operationId = "listCharacterBackendIds",
            summary = "List Character Backend ids",
            description = "List character backend identifiers.")
    @GetMapping("/backend/ids/{characterUid}")
    @PreAuthorize("hasPermission(#p0, 'characterDefaultOpByUid')")
    public List<String> listBackendIds(
            @Parameter(description = "The characterUid to be queried") @PathVariable("characterUid") @NotBlank
                    String characterUid) {
        return characterService.listBackendIds(characterUid);
    }

    @Operation(
            operationId = "listCharacterBackends",
            summary = "List Character Backends",
            description = "List character backends.")
    @GetMapping("/backends/{characterUid}")
    @PreAuthorize("hasPermission(#p0, 'characterDefaultOpByUid')")
    public List<CharacterBackendDetailsDTO> listBackends(
            @Parameter(description = "The characterUid to be queried") @PathVariable("characterUid") @NotBlank
                    String characterUid) {
        return characterService.listBackends(characterUid).stream()
                .map(CharacterBackendDetailsDTO::from)
                .map(this::enrichTgFields)
                .toList();
    }

    @Operation(
            operationId = "setDefaultCharacterBackend",
            summary = "Set Default Character Backend",
            description = "Set the default backend configuration.")
    @PutMapping("/backend/default/{characterBackendId}")
    @PreAuthorize("hasPermission(#p0, 'characterBackendDefaultOp')")
    public Boolean setDefaultBackend(
            @Parameter(description = "The characterBackendId to be set to default")
                    @PathVariable("characterBackendId")
                    @NotBlank
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
            description = "Upload a picture of the character.")
    @PostMapping(
            value = "/picture/{characterUid}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasPermission(#p2, 'characterDefaultOpByUid')")
    public String uploadPicture(
            HttpServletRequest request,
            @Parameter(description = "Character picture") @RequestParam("file") @NotNull MultipartFile file,
            @Parameter(description = "Character unique identifier") @PathVariable("characterUid") @NotBlank
                    String characterUid) {
        long maxSize = runtimeConfig.getLong(PICTURE_MAX_SIZE_KEY);
        int maxCount = runtimeConfig.getInt(PICTURE_MAX_COUNT_KEY);

        if (file.getSize() > maxSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size should be less than " + maxSize);
        }

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
            description = "Delete a picture of the character by key.")
    @DeleteMapping("/picture/{key}")
    public Boolean deletePicture(@Parameter(description = "Image key") @PathVariable("key") @NotBlank String key) {
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
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IOException e) {
            return false;
        }
    }

    @Operation(
            operationId = "listCharacterPictures",
            summary = "List Character Pictures",
            description = "List pictures of the character.")
    @GetMapping("/pictures/{characterUid}")
    @PreAuthorize("hasPermission(#p1, 'characterDefaultOpByUid')")
    public List<String> listPictures(
            HttpServletRequest request,
            @Parameter(description = "Character unique identifier") @PathVariable("characterUid") @NotBlank
                    String characterUid) {
        FileStore fileStore = StoreUtils.defaultFileStore();
        try {
            String dstDir = PUBLIC_DIR + AccountUtils.currentUser().getUserId() + "/character/picture/" + characterUid;
            return fileStore.list(dstDir, null, false).stream()
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
            operationId = "uploadCharacterVideo",
            summary = "Upload Character Video",
            description = "Upload a video of the character.")
    @PostMapping(
            value = "/video/{characterUid}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasPermission(#p2, 'characterDefaultOpByUid')")
    public String uploadVideo(
            HttpServletRequest request,
            @Parameter(description = "Character video") @RequestParam("file") @NotNull MultipartFile file,
            @Parameter(description = "Character unique identifier") @PathVariable("characterUid") @NotBlank
                    String characterUid) {
        long maxSize = runtimeConfig.getLong(VIDEO_MAX_SIZE_KEY);
        int maxCount = runtimeConfig.getInt(VIDEO_MAX_COUNT_KEY);

        if (file.getSize() > maxSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size should be less than " + maxSize);
        }

        if (StringUtils.isBlank(characterUid)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to find character");
        }

        FileStore fileStore = StoreUtils.defaultFileStore();
        try {
            String dstDir = PUBLIC_DIR + AccountUtils.currentUser().getUserId() + "/character/video/" + characterUid;
            String dstPath = FileUtils.transfer(file, fileStore, dstDir, maxCount);
            String shareUrl = fileStore.getShareUrl(dstPath, Integer.MAX_VALUE);
            if (StringUtils.isBlank(shareUrl)) {
                shareUrl = FileUtils.getDefaultPublicUrlForVideo(request, dstPath);
            }
            return shareUrl;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
        }
    }

    @Operation(
            operationId = "deleteCharacterVideo",
            summary = "Delete Character Video",
            description = "Delete a video of the character by key.")
    @DeleteMapping("/video/{key}")
    public Boolean deleteVideo(@Parameter(description = "Video key") @PathVariable("key") @NotBlank String key) {
        FileStore fileStore = StoreUtils.defaultFileStore();
        try {
            String path = FileUtils.getDefaultPublicPath(key);
            String characterUid = getCharacterUidFromPath(path);
            String userId = characterService.getOwnerByUid(characterUid);
            if (!AccountUtils.currentUser().getUserId().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Video doesn't belong to you");
            }
            fileStore.delete(path);
            return true;
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IOException e) {
            return false;
        }
    }

    @Operation(
            operationId = "listCharacterVideos",
            summary = "List Character Videos",
            description = "List videos of the character.")
    @GetMapping("/videos/{characterUid}")
    @PreAuthorize("hasPermission(#p1, 'characterDefaultOpByUid')")
    public List<String> listVideos(
            HttpServletRequest request,
            @Parameter(description = "Character unique identifier") @PathVariable("characterUid") @NotBlank
                    String characterUid) {
        FileStore fileStore = StoreUtils.defaultFileStore();
        try {
            String dstDir = PUBLIC_DIR + AccountUtils.currentUser().getUserId() + "/character/video/" + characterUid;
            return fileStore.list(dstDir, null, false).stream()
                    .map(path -> {
                        String shareUrl = fileStore.getShareUrl(path, Integer.MAX_VALUE);
                        if (StringUtils.isBlank(shareUrl)) {
                            shareUrl = FileUtils.getDefaultPublicUrlForVideo(request, path);
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
            description = "Upload an avatar of the character.")
    @PostMapping(
            value = "/avatar/{characterUid}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasPermission(#p2, 'characterDefaultOpByUid')")
    public String uploadAvatar(
            HttpServletRequest request,
            @Parameter(description = "Character avatar") @RequestParam("file") @NotNull MultipartFile file,
            @Parameter(description = "Character unique identifier") @PathVariable("characterUid") @NotBlank
                    String characterUid) {
        long maxSize = runtimeConfig.getLong(AVATAR_MAX_SIZE_KEY);
        int maxCount = runtimeConfig.getInt(AVATAR_MAX_COUNT_KEY);

        if (file.getSize() > maxSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size should be less than " + maxSize);
        }

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
            description = "Upload a document of the character.")
    @PostMapping(
            value = "/document/{characterUid}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasPermission(#p2, 'characterDefaultOpByUid')")
    public String uploadDocument(
            HttpServletRequest request,
            @Parameter(description = "Character document") @RequestParam("file") @NotNull MultipartFile file,
            @Parameter(description = "Character unique identifier") @PathVariable("characterUid") @NotBlank
                    String characterUid) {
        long maxSize = runtimeConfig.getLong(DOCUMENT_MAX_SIZE_KEY);
        int maxCount = runtimeConfig.getInt(DOCUMENT_MAX_COUNT_KEY);

        if (file.getSize() > maxSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size should be less than " + maxSize);
        }

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
            description = "Delete a document of the character by key.")
    @DeleteMapping("/document/{key}")
    public Boolean deleteDocument(@Parameter(description = "Document key") @PathVariable("key") @NotBlank String key) {
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
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IOException e) {
            return false;
        }
    }

    @Operation(
            operationId = "listCharacterDocuments",
            summary = "List Character Documents",
            description = "List documents of the character.")
    @GetMapping("/documents/{characterUid}")
    @PreAuthorize("hasPermission(#p1, 'characterDefaultOpByUid')")
    public List<String> listDocuments(
            HttpServletRequest request,
            @Parameter(description = "Character unique identifier") @PathVariable("characterUid") @NotBlank
                    String characterUid) {
        FileStore fileStore = StoreUtils.defaultFileStore();
        String userId = AccountUtils.currentUser().getUserId();
        try {
            String dstDir = PRIVATE_DIR + userId + "/character/document/" + characterUid;
            return fileStore.list(dstDir, null, false).stream()
                    .map(path -> FileUtils.getDefaultPrivateUrlForDocument(request, path, userId))
                    .toList();
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    @Operation(
            operationId = "uploadCharacterVoice",
            summary = "Upload Character Voice",
            description = "Upload a voice of the character.")
    @PostMapping(
            value = "/voice/{characterBackendId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasPermission(#p1, 'characterBackendDefaultOp')")
    public String uploadVoice(
            @Parameter(description = "Character voice") @RequestParam("file") @NotNull MultipartFile file,
            @Parameter(description = "The characterBackendId") @PathVariable("characterBackendId") @NotBlank
                    String characterBackendId) {
        long maxSize = runtimeConfig.getLong(VOICE_MAX_SIZE_KEY);
        int maxCount = runtimeConfig.getInt(VOICE_MAX_COUNT_KEY);

        if (file.getSize() > maxSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size should be less than " + maxSize);
        }

        if (StringUtils.isBlank(characterBackendId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to find character backend");
        }

        if (!ttsService.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "TTS service is unavailable");
        }

        FileStore fileStore = StoreUtils.defaultFileStore();
        String userId = AccountUtils.currentUser().getUserId();
        String characterUid = characterService.getBackendCharacterUid(characterBackendId);
        try {
            String dstDir = PRIVATE_DIR + userId + "/character/voices/" + characterUid + "/" + characterBackendId;
            String dstPath = FileUtils.transfer(file, fileStore, dstDir, maxCount);
            String speaker = ttsService.uploadVoice(dstPath);
            if (StringUtils.isBlank(speaker)) throw new IllegalStateException("TTS returned an empty voice identifier");
            CharacterVoiceFiles.remember(
                    fileStore, dstDir, speaker, Path.of(dstPath).getFileName().toString());
            return speaker;
        } catch (IOException | IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
        }
    }

    @Operation(
            operationId = "deleteCharacterVoice",
            summary = "Delete Character Voice",
            description = "Delete a voice of the character by key.")
    @DeleteMapping("/voice/{characterBackendId}/{key}")
    @PreAuthorize("hasPermission(#p0, 'characterBackendDefaultOp')")
    public Boolean deleteVoice(
            @Parameter(description = "The characterBackendId") @PathVariable("characterBackendId") @NotBlank
                    String characterBackendId,
            @Parameter(description = "Voice key") @PathVariable("key") @NotBlank String key) {
        if (!ttsService.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "TTS service is unavailable");
        }

        FileStore fileStore = StoreUtils.defaultFileStore();
        String userId = AccountUtils.currentUser().getUserId();
        try {
            String characterUid = characterService.getBackendCharacterUid(characterBackendId);
            String ownerId = characterService.getOwnerByUid(characterUid);
            if (!userId.equals(ownerId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Document doesn't belong to you");
            }
            String directory = PRIVATE_DIR + userId + "/character/voices/" + characterUid + "/" + characterBackendId;
            String source = CharacterVoiceFiles.resolve(fileStore, directory, key);
            ttsService.deleteVoice(key);
            fileStore.delete(source);
            fileStore.tryDelete(CharacterVoiceFiles.mappingPath(directory, key));
            return true;
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
        } catch (IOException e) {
            return false;
        }
    }

    @Operation(
            operationId = "listCharacterVoices",
            summary = "List Character Voices",
            description = "List voices of the character.")
    @GetMapping("/voices/{characterBackendId}")
    @PreAuthorize("hasPermission(#p0, 'characterBackendDefaultOp')")
    public List<String> listVoices(
            @Parameter(description = "The characterBackendId") @PathVariable("characterBackendId") @NotBlank
                    String characterBackendId) {
        FileStore fileStore = StoreUtils.defaultFileStore();
        String userId = AccountUtils.currentUser().getUserId();
        String characterUid = characterService.getBackendCharacterUid(characterBackendId);
        try {
            String dstDir = PRIVATE_DIR + userId + "/character/voices/" + characterUid + "/" + characterBackendId;
            return fileStore.list(dstDir, null, false).stream()
                    .map(FileUtils::getKeyFromUrl)
                    .toList();
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    @Operation(
            operationId = "existsCharacterName",
            summary = "Check If Character Name Exists",
            description = "Check if the character name already exists.")
    @GetMapping("/exists/name/{name}")
    public Boolean existsName(@Parameter(description = "Name") @PathVariable("name") @NotBlank String name) {
        return characterService.existsName(name, AccountUtils.currentUser());
    }

    @Operation(
            operationId = "newCharacterName",
            summary = "Create New Character Name",
            description = "Create a new character name starting with a desired name.")
    @GetMapping(value = "/create/name/{desired}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String createName(
            @Parameter(description = "Desired name") @PathVariable("desired") @NotBlank String desired) {
        return CharacterUtils.newUniqueName(desired);
    }

    @Operation(
            operationId = "exportCharacter",
            summary = "Export Character Configuration",
            description =
                    "Export character configuration in tar.gz format, including settings, documents and pictures.")
    @GetMapping(value = "/export/{characterId}", produces = "application/gzip")
    public void exportCharacter(
            @Parameter(description = "Character identifier") @PathVariable("characterId") @Positive Long characterId,
            @Parameter(description = "Include credentialless connector definitions (character owner only)")
                    @RequestParam(defaultValue = "false")
                    boolean includeConnectors,
            HttpServletRequest request,
            HttpServletResponse response) {
        User currentUser = AccountUtils.currentUser();
        var source = characterService.details(characterId, currentUser);
        if (source == null || source.getLeft() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Character not found");
        }
        CharacterInfo character = source.getLeft();
        if (character.getVersion() == null || character.getVersion() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Publish the character before exporting");
        }
        boolean owner = currentUser.getUserId().equals(character.getUserId());
        var connectors = includeConnectors ? exportConnectors(character, currentUser.getUserId()) : null;
        CharacterCreateDTO portable = CharacterCreateDTO.from(Pair.of(character, source.getMiddle()));
        CharacterTransferUtils.sanitize(portable);
        if (!includeConnectors) portable.setExt(CharacterConnectorConfiguration.withoutConnectors(portable.getExt()));
        List<CharacterBackend> backends = owner && source.getRight() != null ? source.getRight() : List.of();
        if (backends.size() > CharacterTransferUtils.MAX_BACKENDS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Too many character backends to export");
        }
        List<CharacterBackendConfigurationDTO> configurations = new ArrayList<>();
        List<TarUtils.Entry> entries = new ArrayList<>();
        Map<String, List<String>> urls = new LinkedHashMap<>();
        List<RagTaskDTO> ragTasks = new ArrayList<>();
        FileStore store = StoreUtils.defaultFileStore();
        String uid = character.getCharacterUid();
        try {
            for (CharacterBackend backend : backends) {
                CharacterBackendDTO dto = CharacterBackendDTO.from(backend);
                CharacterTransferUtils.sanitize(dto);
                PromptTask chat = exportPromptTask(backend.getChatPromptTaskId());
                PromptTask greeting = exportPromptTask(backend.getGreetingPromptTaskId());
                var conf = CharacterBackendConfigurationDTO.builder()
                        .backend(dto)
                        .chatPrompt(exportPrompt(chat, currentUser))
                        .chatPromptTask(CharacterTransferUtils.exportTask(chat))
                        .greetingPrompt(exportPrompt(greeting, currentUser))
                        .greetingPromptTask(CharacterTransferUtils.exportTask(greeting))
                        .build();
                if ("wav".equals(dto.getTtsSpeakerType())) {
                    String directory = PRIVATE_DIR + character.getUserId() + "/character/voices/" + uid + "/"
                            + backend.getBackendId();
                    String path = CharacterVoiceFiles.resolve(store, directory, dto.getTtsSpeakerWav());
                    if (path != null) {
                        String archivePath = "voices/" + configurations.size() + "/"
                                + Path.of(path).getFileName();
                        addFile(entries, archivePath, path, store);
                        conf.setVoicePath(archivePath);
                    }
                }
                configurations.add(conf);
            }
            CharacterConfigurationDTO conf = CharacterConfigurationDTO.builder()
                    .character(portable)
                    .backends(configurations)
                    .connectors(connectors)
                    .resourceUrls(urls)
                    .ragTasks(ragTasks)
                    .build();
            if (StringUtils.isNotBlank(uid)) {
                String publicRoot = PUBLIC_DIR + character.getUserId() + "/character/";
                exportMedia(request, conf, "pictures", publicRoot + "picture/" + uid, entries, store);
                exportMedia(request, conf, "videos", publicRoot + "video/" + uid, entries, store);
                exportAvatar(request, conf, publicRoot + "avatar/" + uid, entries, store);
                if (owner) {
                    String documentRoot = PRIVATE_DIR + character.getUserId() + "/character/document/" + uid;
                    Set<String> documents = new HashSet<>();
                    for (String path : listFiles(store, documentRoot)) {
                        String archivePath = "documents/" + Path.of(path).getFileName();
                        addFile(entries, archivePath, path, store);
                        documents.add(path);
                    }
                    for (RagTask task : ragTaskService.list(uid)) {
                        String sourcePath = task.getSource();
                        if (SourceType.FILE.text().equals(task.getSourceType())) {
                            if (!documents.contains(sourcePath)) {
                                throw new ResponseStatusException(
                                        HttpStatus.CONFLICT, "A knowledge document is unavailable");
                            }
                            sourcePath = "documents/" + Path.of(sourcePath).getFileName();
                        }
                        ragTasks.add(RagTaskDTO.builder()
                                .sourceType(task.getSourceType())
                                .source(sourcePath)
                                .maxSegmentSize(task.getMaxSegmentSize())
                                .maxOverlapSize(task.getMaxOverlapSize())
                                .build());
                    }
                }
            }
            byte[] manifest = new ManifestInfo().toYaml().getBytes(StandardCharsets.UTF_8);
            byte[] content = conf.toJson().getBytes(StandardCharsets.UTF_8);
            if (content.length > 4 * 1024 * 1024)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Character configuration is too large");
            entries.addFirst(
                    new TarUtils.Entry("character.json", () -> new ByteArrayInputStream(content), content.length));
            entries.addFirst(
                    new TarUtils.Entry("manifest.yml", () -> new ByteArrayInputStream(manifest), manifest.length));
            if (entries.size() * 2 > TarUtils.DEFAULT_LIMITS.maxEntries()
                    || entries.stream().anyMatch(entry -> entry.size() > TarUtils.DEFAULT_LIMITS.maxFileBytes())
                    || entries.stream().mapToLong(TarUtils.Entry::size).sum()
                            > TarUtils.DEFAULT_LIMITS.maxTotalBytes()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Character archive is too large");
            }
            String filename = URLEncoder.encode(portable.getName() + ".tar.gz", StandardCharsets.UTF_8)
                    .replace("+", "%20");
            response.setHeader(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"character.tar.gz\"; filename*=UTF-8''" + filename);
            response.setContentType("application/gzip");
            TarUtils.compressGzip(entries, response.getOutputStream());
        } catch (IOException e) {
            log.error("Failed to export character {}", characterId, e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed to export character configuration", e);
        }
    }

    private PromptTask exportPromptTask(String taskId) {
        if (StringUtils.isBlank(taskId)) return null;
        PromptTask task = promptTaskService.get(taskId);
        if (task == null) throw new ResponseStatusException(HttpStatus.CONFLICT, "A prompt task is unavailable");
        return task;
    }

    private PromptCreateDTO exportPrompt(PromptTask task, User user) {
        if (task == null) return null;
        Long id = promptService.getLatestIdByUid(task.getPromptUid(), user);
        var details = id == null ? null : promptService.details(id, user);
        if (details == null
                || details.getLeft().getVersion() == null
                || details.getLeft().getVersion() < 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A published prompt is unavailable");
        }
        PromptCreateDTO prompt = PromptCreateDTO.from(details);
        CharacterTransferUtils.sanitize(prompt);
        return prompt;
    }

    private List<ConnectorConfigurationDTO> exportConnectors(CharacterInfo character, String currentUserId) {
        // Registry metadata is private even when the character is public or the caller is an administrator.
        if (StringUtils.isBlank(currentUserId) || !currentUserId.equals(character.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the character owner can export connectors");
        }
        Set<String> references = new TreeSet<>(
                CharacterConnectorConfiguration.parse(character.getExt()).enabled());
        if (references.isEmpty()) {
            return List.of();
        }
        Map<String, ConnectorService.Summary> owned = new HashMap<>();
        connectorService.list(character.getUserId()).forEach(summary -> owned.put(summary.connectorId(), summary));
        List<ConnectorConfigurationDTO> descriptors = new ArrayList<>();
        for (String reference : references) {
            ConnectorService.Summary summary = owned.get(reference);
            if (summary == null) {
                throw new ConnectorException(ConnectorException.Code.CONNECTOR_ACCESS_DENIED);
            }
            descriptors.add(ConnectorConfigurationDTO.from(summary));
        }
        return descriptors;
    }

    private static List<String> listFiles(FileStore store, String directory) throws IOException {
        return store.exists(directory) ? store.list(directory, null, false) : List.of();
    }

    private static void addFile(List<TarUtils.Entry> entries, String name, String path, FileStore store)
            throws IOException {
        entries.add(new TarUtils.Entry(name, () -> store.newInputStream(path), store.size(path)));
    }

    private void exportMedia(
            HttpServletRequest request,
            CharacterConfigurationDTO conf,
            String category,
            String directory,
            List<TarUtils.Entry> entries,
            FileStore store)
            throws IOException {
        for (String path : listFiles(store, directory)) {
            String name = category + "/" + Path.of(path).getFileName();
            addFile(entries, name, path, store);
            conf.getResourceUrls().put(name, mediaUrls(request, conf, path, category, store));
        }
    }

    private List<String> mediaUrls(
            HttpServletRequest request, CharacterConfigurationDTO conf, String path, String category, FileStore store) {
        Set<String> urls = new LinkedHashSet<>();
        String shared = store.getShareUrl(path, Integer.MAX_VALUE);
        if (StringUtils.isNotBlank(shared)) urls.add(shared);
        urls.add(
                "videos".equals(category)
                        ? FileUtils.getDefaultPublicUrlForVideo(request, path)
                        : FileUtils.getDefaultPublicUrlForImage(request, path));
        String key = FileUtils.getDefaultPublicKey(path);
        var matcher = java.util.regex.Pattern.compile("https?://[^\\s\\\"<>\\\\)\\]]+")
                .matcher(conf.toJson());
        while (matcher.find()) {
            String candidate = matcher.group();
            if (getKeyFromUrl(candidate).equals(key)) urls.add(candidate);
        }
        return List.copyOf(urls);
    }

    private void exportAvatar(
            HttpServletRequest request,
            CharacterConfigurationDTO conf,
            String directory,
            List<TarUtils.Entry> entries,
            FileStore store)
            throws IOException {
        String avatar = conf.getCharacter().getAvatar();
        if (StringUtils.isBlank(avatar)) return;
        for (String path : listFiles(store, directory)) {
            List<String> urls = mediaUrls(request, conf, path, "avatar", store);
            if (urls.contains(avatar)) {
                String name = "avatar/" + Path.of(path).getFileName();
                addFile(entries, name, path, store);
                conf.getResourceUrls().put(name, urls);
                return;
            }
        }
    }

    @Operation(
            operationId = "importCharacter",
            summary = "Import Character Configuration",
            description = "Import character configuration from a tar.gz file.")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long importCharacter(
            HttpServletRequest request,
            @Parameter(description = "Character avatar") @RequestParam("file") @NotNull MultipartFile file) {
        String userId = AccountUtils.currentUser().getUserId();
        Map<String, String> createdConnectors = new LinkedHashMap<>();
        List<String> createdFiles = new ArrayList<>();
        List<ImportedVoice> voices = new ArrayList<>();
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("freechat-character-");
            try {
                TarUtils.extractGzip(file.getInputStream(), tempDir);
            } catch (IOException | IllegalArgumentException e) {
                throw CharacterTransferUtils.invalid();
            }
            String version = handleManifestYaml(tempDir);
            CharacterConfigurationDTO configuration = CharacterConfigurationDTO.fromJson(
                    readConfiguration(tempDir.resolve("character.json"), 4 * 1024 * 1024));
            if ("v2".equals(version)) upgradeLegacyArchive(configuration, tempDir);
            if (configuration.getResourceUrls() == null || configuration.getRagTasks() == null)
                throw CharacterTransferUtils.invalid();
            CharacterTransferUtils.validate(configuration, tempDir);
            validateCharacterTools(configuration.getCharacter().getExt());
            Path root = tempDir;
            Long id = new TransactionTemplate(transactionManager).execute(status -> {
                try {
                    return handleCharacterJson(configuration, request, root, createdConnectors, createdFiles, voices);
                } catch (IOException e) {
                    throw new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store character resources", e);
                }
            });
            voices.forEach(this::registerImportedVoice);
            return id;
        } catch (IOException e) {
            cleanupImportedFiles(createdFiles);
            cleanupImportedConnectors(userId, createdConnectors);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to read character configuration");
        } catch (RuntimeException failure) {
            cleanupImportedFiles(createdFiles);
            cleanupImportedConnectors(userId, createdConnectors);
            throw failure;
        } finally {
            if (tempDir != null) cleanUpTempDirectory(tempDir);
        }
    }

    private static String readConfiguration(Path file, int limit) throws IOException {
        if (!Files.isRegularFile(file) || Files.size(file) > limit) throw CharacterTransferUtils.invalid();
        return Files.readString(file, StandardCharsets.UTF_8);
    }

    private String handleManifestYaml(Path confPath) throws IOException {
        try {
            ManifestInfo manifest =
                    ManifestInfo.fromYaml(readConfiguration(confPath.resolve("manifest.yml"), 64 * 1024));
            if (manifest == null || !Set.of("v2", ManifestInfo.API_VERSION).contains(manifest.getApiVersion())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incompatible character configuration");
            }
            return manifest.getApiVersion();
        } catch (org.yaml.snakeyaml.error.YAMLException | NullPointerException e) {
            throw CharacterTransferUtils.invalid();
        }
    }

    private Long handleCharacterJson(
            CharacterConfigurationDTO conf,
            HttpServletRequest request,
            Path root,
            Map<String, String> createdConnectors,
            List<String> createdFiles,
            List<ImportedVoice> voices)
            throws IOException {
        CharacterCreateDTO character = conf.getCharacter();
        List<ConnectorConfigurationDTO> descriptors = conf.getConnectors() == null ? List.of() : conf.getConnectors();
        Set<String> enabled = descriptors.isEmpty()
                ? Set.of()
                : CharacterConnectorConfiguration.parse(character.getExt()).enabled();
        character.setExt(CharacterConnectorConfiguration.withoutConnectors(character.getExt()));
        var pair = character.toCharacterInfo();
        CharacterUtils.resetCharacterInfoPair(pair, null);
        createImportedCharacter(pair, descriptors, enabled, createdConnectors);
        CharacterInfo info = pair.getLeft();
        String uid = info.getCharacterUid();
        String owner = info.getUserId();
        Map<String, String> paths = new LinkedHashMap<>();
        Map<String, String> urls = new LinkedHashMap<>();
        for (var entry : conf.getResourceUrls().entrySet()) {
            String category = entry.getKey().substring(0, entry.getKey().indexOf('/'));
            String directory = PUBLIC_DIR + owner + "/character/"
                    + switch (category) {
                        case "pictures" -> "picture";
                        case "videos" -> "video";
                        default -> "avatar";
                    }
                    + "/" + uid;
            String path = copyImportedFile(root, entry.getKey(), directory, createdFiles);
            paths.put(entry.getKey(), path);
            String url = StoreUtils.defaultFileStore().getShareUrl(path, Integer.MAX_VALUE);
            if (StringUtils.isBlank(url))
                url = "videos".equals(category)
                        ? FileUtils.getDefaultPublicUrlForVideo(request, path)
                        : FileUtils.getDefaultPublicUrlForImage(request, path);
            for (String source : entry.getValue()) urls.put(source, url);
        }
        conf = CharacterTransferUtils.remapUrls(conf, urls);
        CharacterInfo updated = conf.getCharacter().toCharacterInfo().getLeft();
        updated.setCharacterId(info.getCharacterId());
        updated.setName(info.getName());
        updated.setVersion(info.getVersion());
        updated.setExt(CharacterConnectorConfiguration.withEnabled(
                updated.getExt(),
                CharacterConnectorConfiguration.parse(info.getExt()).enabled()));
        if (!characterService.update(Pair.of(updated, null))) throw importFailed();
        for (var backendConf : conf.getBackends()) {
            CharacterBackendDTO dto = backendConf.getBackend();
            dto.setChatPromptTaskId(createPromptTask(backendConf.getChatPrompt(), backendConf.getChatPromptTask()));
            dto.setGreetingPromptTaskId(
                    createPromptTask(backendConf.getGreetingPrompt(), backendConf.getGreetingPromptTask()));
            boolean enableTts = Boolean.TRUE.equals(dto.getEnableTts());
            if ("wav".equals(dto.getTtsSpeakerType()) || backendConf.getVoicePath() != null) {
                dto.setTtsSpeakerWav(null);
                dto.setEnableTts(false);
            }
            CharacterBackend backend = dto.toCharacterBackend(uid);
            if (StringUtils.isBlank(characterService.addBackend(backend))) throw importFailed();
            if (backendConf.getVoicePath() != null) {
                String directory = PRIVATE_DIR + owner + "/character/voices/" + uid + "/" + backend.getBackendId();
                String sourceName =
                        Path.of(backendConf.getVoicePath()).getFileName().toString();
                String name = IdUtils.newId() + CharacterVoiceFiles.extension(sourceName);
                String path = copyImportedFile(root, backendConf.getVoicePath(), directory, name, createdFiles);
                createdFiles.add(directory + ".sources");
                createdFiles.add(CharacterVoiceFiles.mappingPath(directory, null));
                CharacterVoiceFiles.remember(StoreUtils.defaultFileStore(), directory, null, name);
                voices.add(new ImportedVoice(backend.getBackendId(), uid, path, enableTts));
            }
        }
        Path documents = root.resolve("documents");
        if (Files.isDirectory(documents)) {
            try (Stream<Path> files = Files.list(documents)) {
                for (Path file : files.filter(Files::isRegularFile).toList()) {
                    String source = "documents/" + file.getFileName();
                    paths.put(
                            source,
                            copyImportedFile(
                                    root, source, PRIVATE_DIR + owner + "/character/document/" + uid, createdFiles));
                }
            }
        }
        for (RagTaskDTO task : conf.getRagTasks()) {
            RagTask rag = new RagTask()
                    .withCharacterUid(uid)
                    .withSourceType(task.getSourceType())
                    .withSource("file".equals(task.getSourceType()) ? paths.get(task.getSource()) : task.getSource())
                    .withMaxSegmentSize(task.getMaxSegmentSize())
                    .withMaxOverlapSize(task.getMaxOverlapSize());
            if (!ragTaskService.create(rag)) throw importFailed();
        }
        return info.getCharacterId();
    }

    private record ImportedVoice(String backendId, String characterUid, String source, boolean enableTts) {}

    private void registerImportedVoice(ImportedVoice voice) {
        if (!ttsService.isEnabled()) {
            log.warn("TTS registration deferred for imported backend {}: service unavailable", voice.backendId());
            return;
        }
        try {
            String speaker = ttsService.uploadVoice(voice.source());
            if (StringUtils.isBlank(speaker)) throw new IllegalStateException("TTS returned an empty voice identifier");
            FileStore store = StoreUtils.defaultFileStore();
            Path path = Path.of(voice.source());
            String directory = path.getParent().toString();
            CharacterVoiceFiles.remember(
                    store, directory, speaker, path.getFileName().toString());
            if (!characterService.updateBackend(new CharacterBackend()
                    .withBackendId(voice.backendId())
                    .withCharacterUid(voice.characterUid())
                    .withTtsSpeakerWav(speaker)
                    .withEnableTts(voice.enableTts() ? (byte) 1 : (byte) 0))) {
                throw new IllegalStateException("Failed to save imported voice identifier");
            }
            store.delete(CharacterVoiceFiles.mappingPath(directory, null));
        } catch (IOException | RuntimeException e) {
            log.warn("Failed to finish voice registration for imported backend {}", voice.backendId(), e);
        }
    }

    private static ResponseStatusException importFailed() {
        return new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create character configuration");
    }

    private static String copyImportedFile(Path root, String source, String directory, List<String> created)
            throws IOException {
        return copyImportedFile(
                root, source, directory, Path.of(source).getFileName().toString(), created);
    }

    private static String copyImportedFile(
            Path root, String source, String directory, String name, List<String> created) throws IOException {
        Path file = CharacterTransferUtils.resource(root, source);
        String destination = directory + "/" + name;
        FileStore store = StoreUtils.defaultFileStore();
        if (store.exists(destination)) throw new IOException("Import destination already exists");
        if (!store.exists(directory)) {
            store.createDirectories(directory);
            created.add(directory);
        }
        created.add(destination);
        try (InputStream input = Files.newInputStream(file)) {
            store.write(destination, input);
        }
        return destination;
    }

    private static void cleanupImportedFiles(List<String> paths) {
        FileStore store = StoreUtils.defaultFileStore();
        for (String path : paths.reversed()) {
            try {
                store.delete(path);
            } catch (IOException e) {
                log.warn("Failed to clean up an imported resource", e);
            }
        }
    }

    private void createImportedCharacter(
            Pair<CharacterInfo, List<String>> characterPair,
            List<ConnectorConfigurationDTO> descriptors,
            Set<String> enabled,
            Map<String, String> createdRevisions) {
        if (descriptors.size() > ConnectorConfigurationDTO.MAX_DESCRIPTORS) {
            throw new ConnectorException(ConnectorException.Code.CONNECTOR_INVALID_CONFIGURATION);
        }
        // Validate the entire archive before creating even the first connector. Constructors perform no network I/O.
        Map<String, ConnectorConfiguration> configurations = new LinkedHashMap<>();
        for (ConnectorConfigurationDTO descriptor : descriptors) {
            if (descriptor == null
                    || configurations.putIfAbsent(descriptor.reference(), descriptor.toConfiguration()) != null) {
                throw new ConnectorException(ConnectorException.Code.CONNECTOR_INVALID_CONFIGURATION);
            }
        }
        String ownerId = AccountUtils.currentUser().getUserId();
        CharacterInfo character = characterPair.getLeft();
        character.setUserId(ownerId);
        Map<String, String> mapping = new HashMap<>();
        for (var entry : configurations.entrySet()) {
            if (!enabled.contains(entry.getKey())) continue;
            ConnectorService.Summary created = connectorService.create(ownerId, entry.getValue(), false);
            createdRevisions.put(created.connectorId(), created.revision());
            mapping.put(entry.getKey(), created.connectorId());
        }
        if (!descriptors.isEmpty()) {
            character.setVisibility(Visibility.PRIVATE.text());
        }
        // resetCharacterInfoPair strips bindings for all clones; restore only fresh, importer-owned references
        // here.
        character.setExt(
                CharacterConnectorConfiguration.withEnabled(character.getExt(), remapConnectors(enabled, mapping)));
        connectorPolicy.validate(ownerId, character.getExt(), character.getDraft());
        if (!characterService.create(characterPair)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create character.");
        }
    }

    private void cleanupImportedConnectors(String ownerId, Map<String, String> createdRevisions) {
        var iterator = createdRevisions.entrySet().iterator();
        while (iterator.hasNext()) {
            var created = iterator.next();
            try {
                connectorService.delete(ownerId, created.getKey(), created.getValue());
                iterator.remove();
            } catch (RuntimeException ignored) {
                log.warn("Failed to clean up a connector after character import failed");
            }
        }
    }

    private static Set<String> remapConnectors(Set<String> references, Map<String, String> mapping) {
        Set<String> remapped = new HashSet<>();
        for (String reference : references) {
            String id = mapping.get(reference);
            if (id != null) {
                remapped.add(id);
            }
        }
        return remapped;
    }

    private void upgradeLegacyArchive(CharacterConfigurationDTO conf, Path root) throws IOException {
        Map<String, List<String>> urls = new LinkedHashMap<>();
        CharacterTransferUtils.sanitize(conf.getCharacter());
        if (conf.getBackends() != null) {
            for (var backend : conf.getBackends()) {
                if (backend != null) {
                    CharacterTransferUtils.sanitize(backend.getChatPrompt());
                    CharacterTransferUtils.sanitize(backend.getGreetingPrompt());
                }
            }
        }
        String configuration = conf.toJson();
        for (String category : List.of("avatar", "pictures", "videos")) {
            Path directory = root.resolve(category);
            if (!Files.isDirectory(directory)) continue;
            try (Stream<Path> stream = Files.list(directory)) {
                for (Path file : stream.filter(Files::isRegularFile).toList()) {
                    Set<String> references = new LinkedHashSet<>();
                    var matcher = java.util.regex.Pattern.compile("https?://[^\\s\\\"<>\\\\)\\]]+")
                            .matcher(configuration);
                    while (matcher.find()) {
                        String url = matcher.group();
                        try {
                            String source = FileUtils.getDefaultPublicPath(getKeyFromUrl(url));
                            if (Path.of(source).getFileName().equals(file.getFileName())) references.add(url);
                        } catch (IllegalArgumentException | IOException ignored) {
                        }
                    }
                    if ("avatar".equals(category)
                            && StringUtils.isNotBlank(conf.getCharacter().getAvatar())) {
                        references.add(conf.getCharacter().getAvatar());
                    }
                    urls.put(category + "/" + file.getFileName(), List.copyOf(references));
                }
            }
        }
        conf.setResourceUrls(urls);
        List<RagTaskDTO> tasks = new ArrayList<>();
        Path documents = root.resolve("documents");
        if (Files.isDirectory(documents)) {
            try (Stream<Path> stream = Files.list(documents)) {
                for (Path document : stream.filter(Files::isRegularFile).toList()) {
                    tasks.add(RagTaskDTO.builder()
                            .sourceType("file")
                            .source("documents/" + document.getFileName())
                            .maxSegmentSize(defaultMaxSegmentSize)
                            .maxOverlapSize(defaultMaxOverlapSize)
                            .build());
                }
            }
        }
        conf.setRagTasks(tasks);
        if (conf.getBackends() != null) {
            for (var backend : conf.getBackends()) {
                if (backend == null || backend.getBackend() == null) continue;
                String speaker = backend.getBackend().getTtsSpeakerWav();
                if (StringUtils.isNotBlank(speaker) && Files.isDirectory(root.resolve("voices"))) {
                    try (Stream<Path> sources = Files.list(root.resolve("voices"))) {
                        String source = CharacterVoiceFiles.match(
                                speaker,
                                sources.filter(Files::isRegularFile)
                                        .map(Path::toString)
                                        .toList());
                        backend.setVoicePath("voices/" + Path.of(source).getFileName());
                    }
                }
            }
        }
    }

    private String createPromptTask(PromptCreateDTO promptCreateDTO, PromptTaskDTO promptTaskDTO) {
        if (promptCreateDTO == null || promptTaskDTO == null) {
            return null;
        }

        var promptInfo = promptCreateDTO.toPromptInfo();
        if (promptInfo != null) {
            PromptUtils.resetPromptInfoTriple(promptInfo, null);
            if (!promptService.create(promptInfo)) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create prompt.");
            }

            PromptTask promptTask = CharacterTransferUtils.importTask(
                    promptTaskDTO, promptInfo.getLeft().getPromptUid());

            if (!promptTaskService.create(promptTask)) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create prompt task.");
            }

            return promptTask.getTaskId();
        }
        return null;
    }

    private static void cleanUpTempDirectory(Path tempDir) {
        try (Stream<Path> stream = Files.walk(tempDir)) {
            for (Path path : stream.sorted(Comparator.reverseOrder()).toList()) Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("Failed to clean up character import staging directory", e);
        }
    }

    @Data
    public static class ManifestInfo {
        private static final String API_VERSION = "v3";

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
            var limits = new org.yaml.snakeyaml.LoaderOptions();
            limits.setMaxAliasesForCollections(0);
            limits.setAllowDuplicateKeys(false);
            limits.setCodePointLimit(64 * 1024);
            Object value = new Yaml(new org.yaml.snakeyaml.constructor.SafeConstructor(limits)).load(yaml);
            if (!(value instanceof Map<?, ?> fields) || !(fields.get("apiVersion") instanceof String version)) {
                throw CharacterTransferUtils.invalid();
            }
            ManifestInfo manifest = new ManifestInfo();
            manifest.setApiVersion(version);
            return manifest;
        }

        static Yaml yaml() {
            DumperOptions options = new DumperOptions();
            options.setIndent(2);
            options.setPrettyFlow(true);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            Representer representer = new Representer(options);
            representer.getPropertyUtils().setSkipMissingProperties(true);
            representer.addClassTag(ManifestInfo.class, MAP);

            return new Yaml(representer, options);
        }
    }
}
