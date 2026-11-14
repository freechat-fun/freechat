package fun.freechat;

import static fun.freechat.util.TestCommonUtils.MYSQL_IMAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.springframework.test.util.ReflectionTestUtils.*;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fun.freechat.api.CharacterApi;
import fun.freechat.api.dto.*;
import fun.freechat.api.dto.conf.CharacterBackendConfigurationDTO;
import fun.freechat.api.dto.conf.CharacterConfigurationDTO;
import fun.freechat.api.util.CharacterUtils;
import fun.freechat.api.util.CharacterVoiceFiles;
import fun.freechat.api.util.FileUtils;
import fun.freechat.api.util.PromptUtils;
import fun.freechat.mapper.*;
import fun.freechat.model.*;
import fun.freechat.service.character.impl.CharacterServiceImpl;
import fun.freechat.service.chat.TtsService;
import fun.freechat.service.common.EncryptionService;
import fun.freechat.service.common.impl.LocalFileStoreImpl;
import fun.freechat.service.config.RuntimeConfig;
import fun.freechat.service.connectors.ConnectorCharacterPolicy;
import fun.freechat.service.prompt.impl.PromptServiceImpl;
import fun.freechat.service.prompt.impl.PromptTaskServiceImpl;
import fun.freechat.service.rag.impl.RagTaskServiceImpl;
import fun.freechat.service.skills.SkillCharacterPolicy;
import fun.freechat.service.tools.ToolRegistry;
import fun.freechat.service.util.InfoUtils;
import fun.freechat.service.util.StoreUtils;
import fun.freechat.util.TarUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockMakers;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Isolated MySQL and local files only; no application context, external providers, or real credentials. */
@Testcontainers
@Timeout(60)
class CharacterTransferIT {
    @Container
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>(MYSQL_IMAGE)
            .withDatabaseName("freechat")
            .withInitScript("sql/schema.sql")
            .withCommand("--max-allowed-packet=32M");

    private static final List<String> TABLES = List.of(
            "character_info",
            "tag",
            "prompt_info",
            "prompt_task",
            "character_backend",
            "rag_task",
            "ai_model",
            "interactive_stats");
    private static final List<Class<?>> MAPPERS = List.of(
            CharacterInfoMapper.class,
            CharacterBackendMapper.class,
            PromptInfoMapper.class,
            PromptTaskMapper.class,
            TagMapper.class,
            AiModelMapper.class,
            InteractiveStatsMapper.class,
            RagTaskMapper.class);
    private static final String SECRET = "INLINE_KEY_TEST_SENTINEL";
    private static final String DRAFT = "{\"profile\":\"PRIVATE_DRAFT_SENTINEL\"}";
    private static final String TEMPLATE = "{{tone}} {{count}} {{CHARACTER_PROFILE}} {{INPUT}}";
    private static final String VARIABLES = "{\"tone\":\"warm\",\"count\":3,"
            + "\"CHARACTER_PROFILE\":\"SYSTEM_VALUE_SENTINEL\",\"INPUT\":\"SYSTEM_VALUE_SENTINEL\"}";
    private static final Map<String, Object> CUSTOM = Map.of("tone", "warm", "count", 3);
    // Media bytes are opaque to this transfer API: no decoding or provider invocation is needed.
    private static final Map<String, String> RESOURCES = Map.of(
            "avatar/a.png",
            "avatar-bytes",
            "pictures/p.png",
            "picture-bytes",
            "videos/v.mp4",
            "video-bytes",
            "documents/notes.txt",
            "PRIVATE_DOCUMENT_SENTINEL",
            "voices/0/speaker.wav",
            "PRIVATE_VOICE_SENTINEL");
    private static HikariDataSource dataSource;
    private static JdbcTemplate sql;
    private static SqlSessionFactory sqlFactory;
    private static SqlSessionTemplate sessions;

    @TempDir
    Path root;

    private final CharacterApi api = new CharacterApi();
    private final CharacterServiceImpl characters = realSpy(new CharacterServiceImpl());
    private final PromptServiceImpl prompts = new PromptServiceImpl();
    private final PromptTaskServiceImpl tasks = new PromptTaskServiceImpl();
    private final RagTaskServiceImpl rags = realSpy(new RagTaskServiceImpl());
    private final LocalFileStoreImpl store = new LocalFileStoreImpl();
    private final TtsService tts = fake(TtsService.class);
    private Object[] previousStatics;
    private SecurityContext previousSecurity;

    @BeforeAll
    static void configureSqlOnly() throws Exception {
        HikariConfig pool = new HikariConfig();
        pool.setJdbcUrl(MYSQL.getJdbcUrl());
        pool.setUsername(MYSQL.getUsername());
        pool.setPassword(MYSQL.getPassword());
        pool.setMaximumPoolSize(5);
        pool.setConnectionInitSql("SET time_zone = '+00:00'");
        dataSource = new HikariDataSource(pool);
        sql = new JdbcTemplate(dataSource);
        Configuration configuration = new Configuration();
        configuration.setLogImpl(NoLoggingImpl.class);
        MAPPERS.forEach(configuration::addMapper);
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setConfiguration(configuration);
        sqlFactory = Objects.requireNonNull(factory.getObject());
        sessions = new SqlSessionTemplate(sqlFactory);
    }

    @AfterAll
    static void closePool() {
        if (dataSource != null) dataSource.close();
    }

    @BeforeEach
    void setup() throws Exception {
        previousSecurity = SecurityContextHolder.getContext();
        previousStatics = new Object[] {
            getField(StoreUtils.class, "localFileStore"),
            getField(CharacterUtils.class, "characterService"),
            getField(PromptUtils.class, "promptService"),
            getField(FileUtils.class, "runtimeConfig")
        };
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        authenticate("creator");
        for (String table : TABLES) sql.update("DELETE FROM " + table);
        injectMappers(
                characters,
                CharacterInfoMapper.class,
                CharacterBackendMapper.class,
                TagMapper.class,
                InteractiveStatsMapper.class);
        injectMappers(
                prompts, PromptInfoMapper.class, TagMapper.class, AiModelMapper.class, InteractiveStatsMapper.class);
        injectMappers(tasks, PromptTaskMapper.class);
        injectMappers(rags, RagTaskMapper.class);
        setField(characters, "sqlSessionFactory", sqlFactory);
        setField(prompts, "sqlSessionFactory", sqlFactory);
        setField(tasks, "promptService", prompts);
        setField(rags, "characterService", characters);
        EncryptionService encryption = fake(EncryptionService.class);
        // Identity encryption keeps sentinel leaks observable instead of silently replacing keys with null.
        when(encryption.encrypt(any())).thenAnswer(call -> call.getArgument(0));
        when(encryption.decrypt(any())).thenAnswer(call -> call.getArgument(0));
        setField(characters, "encryptionService", encryption);
        setField(tasks, "encryptionService", encryption);
        ToolRegistry tools = new ToolRegistry(List.of());
        SkillCharacterPolicy skills = fake(SkillCharacterPolicy.class);
        when(skills.validateUpdate(anyLong(), nullable(String.class), nullable(String.class)))
                .thenAnswer(call -> sessions.getMapper(CharacterInfoMapper.class)
                        .selectByPrimaryKey(call.getArgument(0))
                        .orElseThrow()
                        .getUserId());
        setField(characters, "toolRegistry", tools);
        setField(characters, "skillPolicy", skills);
        setField(characters, "connectorPolicy", fake(ConnectorCharacterPolicy.class));
        setField(characters, "eventPublisher", fake(ApplicationEventPublisher.class));
        setField(api, "characterService", characters);
        setField(api, "promptService", prompts);
        setField(api, "promptTaskService", tasks);
        setField(api, "ragTaskService", rags);
        setField(api, "toolRegistry", tools);
        setField(api, "connectorPolicy", fake(ConnectorCharacterPolicy.class));
        setField(api, "ttsService", tts);
        when(tts.isEnabled()).thenReturn(true);
        when(tts.uploadVoice(anyString())).thenAnswer(call -> {
            assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
            String name = Path.of((String) call.getArgument(0)).getFileName().toString();
            return name.substring(
                            0,
                            name.length() - CharacterVoiceFiles.extension(name).length()) + ".wav";
        });
        setField(api, "transactionManager", new DataSourceTransactionManager(dataSource));
        setField(store, "basePathStr", root.resolve("store").toString());
        store.postConstruct();
        store.createDirectories("");
        setField(StoreUtils.class, "localFileStore", store);
        setField(CharacterUtils.class, "characterService", characters);
        setField(PromptUtils.class, "promptService", prompts);
        setField(FileUtils.class, "runtimeConfig", fake(RuntimeConfig.class));
    }

    @AfterEach
    void restore() {
        if (previousStatics != null) {
            setField(StoreUtils.class, "localFileStore", previousStatics[0]);
            setField(CharacterUtils.class, "characterService", previousStatics[1]);
            setField(PromptUtils.class, "promptService", previousStatics[2]);
            setField(FileUtils.class, "runtimeConfig", previousStatics[3]);
        }
        if (previousSecurity != null) SecurityContextHolder.setContext(previousSecurity);
    }

    @Test
    void roundTripCreatesPrivateFreshRowsAndRemapsMediaWithoutLosingCustomVariables() throws Exception {
        Stored source = seedSource();
        byte[] archive = export(source.character().getCharacterId());
        Path extracted = root.resolve("exported");
        TarUtils.extractGzip(new ByteArrayInputStream(archive), extracted);
        String json = Files.readString(extracted.resolve("character.json"));
        for (String forbidden : List.of(SECRET, "PRIVATE_DRAFT_SENTINEL", "SYSTEM_VALUE_SENTINEL"))
            assertFalse(json.contains(forbidden), forbidden);
        authenticate("importer");
        Stored imported = stored(importArchive(archive));
        assertNotEquals(
                source.character().getCharacterId(), imported.character().getCharacterId());
        assertNotEquals(
                source.character().getCharacterUid(), imported.character().getCharacterUid());
        assertNotEquals(source.backend().getBackendId(), imported.backend().getBackendId());
        assertNotEquals(source.task().getTaskId(), imported.task().getTaskId());
        assertNotEquals(source.prompt().getPromptId(), imported.prompt().getPromptId());
        assertNotEquals(source.prompt().getPromptUid(), imported.prompt().getPromptUid());
        assertEquals("importer", imported.character().getUserId());
        assertEquals("importer", imported.prompt().getUserId());
        assertPortable(imported);
        CharacterInfo c = imported.character();
        for (var media : Map.of(
                        "avatar/a.png", c.getAvatar(), "pictures/p.png", c.getPicture(), "videos/v.mp4", c.getVideo())
                .entrySet()) {
            String directory = media.getKey().split("/")[0];
            directory = directory.equals("pictures") ? "picture" : directory.equals("videos") ? "video" : directory;
            String path = "public/importer/character/" + directory + "/" + c.getCharacterUid() + "/"
                    + Path.of(media.getKey()).getFileName();
            assertEquals(path, FileUtils.getDefaultPublicPath(FileUtils.getKeyFromUrl(media.getValue())));
            assertEquals(RESOURCES.get(media.getKey()), store.readString(path));
        }
        assertNotEquals(source.character().getVideo(), c.getVideo());
        assertEquals("Look: " + c.getPicture() + " Watch: " + c.getVideo(), c.getProfile());
        assertFalse(store.exists("public/importer/character/videos/" + c.getCharacterUid()));
        List<RagTask> importedRags = rags.list(c.getCharacterUid());
        assertEquals(1, importedRags.size());
        RagTask rag = importedRags.getFirst();
        assertEquals(777, rag.getMaxSegmentSize());
        assertEquals(53, rag.getMaxOverlapSize());
        assertEquals("private/importer/character/document/" + c.getCharacterUid() + "/notes.txt", rag.getSource());
        assertEquals(RESOURCES.get("documents/notes.txt"), store.readString(rag.getSource()));
        assertEquals(
                DRAFT, stored(source.character().getCharacterId()).character().getDraft());
        assertEquals(SECRET, stored(source.character().getCharacterId()).task().getApiKeyValue());
    }

    @Test
    void legacyV2ArchiveDropsDraftsAndRebuildsMediaReferences() throws Exception {
        Stored source = seedSource();
        Path extracted = root.resolve("legacy-source");
        TarUtils.extractGzip(new ByteArrayInputStream(export(source.character().getCharacterId())), extracted);
        ObjectNode json =
                (ObjectNode) InfoUtils.defaultMapper().readTree(Files.readString(extracted.resolve("character.json")));
        json.remove(List.of("resourceUrls", "ragTasks"));
        ((ObjectNode) json.path("character")).put("draft", DRAFT);
        ((ObjectNode) json.path("character"))
                .putNull("picture")
                .putNull("video")
                .put(
                        "profile",
                        "![portrait](" + source.character().getPicture() + ") [video]("
                                + source.character().getVideo() + ")");
        ((ObjectNode) json.path("backends").get(0)).remove("voicePath");
        ((ObjectNode) json.path("backends").get(0).path("backend")).put("ttsSpeakerWav", "speaker.wav");
        List<TarUtils.Entry> entries = new ArrayList<>();
        Map<String, String> contents = new LinkedHashMap<>(RESOURCES);
        contents.put("manifest.yml", "apiVersion: v2\n");
        contents.put("character.json", json.toString());
        contents.forEach((name, text) -> {
            byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
            entries.add(new TarUtils.Entry(
                    name.replace("voices/0/", "voices/"), () -> new ByteArrayInputStream(bytes), bytes.length));
        });
        ByteArrayOutputStream archive = new ByteArrayOutputStream();
        TarUtils.compressGzip(entries, archive);
        setField(api, "defaultMaxSegmentSize", 256);
        setField(api, "defaultMaxOverlapSize", 32);
        authenticate("importer");
        Stored imported = stored(importArchive(archive.toByteArray()));
        assertPortable(imported);
        String publicRoot = "public/importer/character/";
        String uid = imported.character().getCharacterUid();
        String picture = FileUtils.getDefaultPublicUrlForImage(
                new MockHttpServletRequest(), publicRoot + "picture/" + uid + "/p.png");
        String video = FileUtils.getDefaultPublicUrlForVideo(
                new MockHttpServletRequest(), publicRoot + "video/" + uid + "/v.mp4");
        assertEquals(
                "![portrait](" + picture + ") [video](" + video + ")",
                imported.character().getProfile());
        assertEquals(
                256,
                rags.list(imported.character().getCharacterUid()).getFirst().getMaxSegmentSize());
        assertTrue(imported.backend().getTtsSpeakerWav().endsWith(".wav"));
        assertNotEquals("speaker.wav", imported.backend().getTtsSpeakerWav());
    }

    @Test
    void historicalMp3SourceExportsWithoutRenamingItToTheTtsWavIdentifier() throws Exception {
        Stored source = seedSource();
        String directory = voiceDirectory(source);
        String original =
                CharacterVoiceFiles.resolve(store, directory, source.backend().getTtsSpeakerWav());
        String mp3 = directory + "/historical-s_voice.mp3";
        store.move(original, mp3);
        sql.update(
                "UPDATE character_backend SET tts_speaker_wav = ? WHERE backend_id = ?",
                "historical-s_voice.wav",
                source.backend().getBackendId());
        CharacterConfigurationDTO exported =
                exportedConfiguration(source.character().getCharacterId());
        assertEquals(
                "voices/0/historical-s_voice.mp3",
                exported.getBackends().getFirst().getVoicePath());
        assertEquals(
                "historical-s_voice.wav",
                exported.getBackends().getFirst().getBackend().getTtsSpeakerWav());
        assertEquals(RESOURCES.get("voices/0/speaker.wav"), store.readString(mp3));
        assertFalse(store.exists(directory + "/historical-s_voice.wav"));
    }

    @Test
    void importedMp3UsesAndRemembersTheActualTtsIdentifierAfterCommit() throws Exception {
        when(tts.uploadVoice(anyString())).thenAnswer(call -> {
            assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
            assertTrue(((String) call.getArgument(0)).endsWith(".mp3"));
            return "provider-assigned-name.wav";
        });
        Stored imported = stored(importArchive(mp3Archive()));
        assertEquals("provider-assigned-name.wav", imported.backend().getTtsSpeakerWav());
        assertEquals((byte) 1, imported.backend().getEnableTts());
        String directory = voiceDirectory(imported);
        String original =
                CharacterVoiceFiles.resolve(store, directory, imported.backend().getTtsSpeakerWav());
        assertTrue(original.endsWith(".mp3"));
        assertNotEquals("speaker.mp3", Path.of(original).getFileName().toString());
        assertEquals(RESOURCES.get("voices/0/speaker.wav"), store.readString(original));
        assertFalse(store.exists(CharacterVoiceFiles.mappingPath(directory, null)));
        CharacterConfigurationDTO exported =
                exportedConfiguration(imported.character().getCharacterId());
        assertEquals(
                "voices/0/" + Path.of(original).getFileName(),
                exported.getBackends().getFirst().getVoicePath());
        verify(tts).uploadVoice(original);
    }

    @Test
    void repeatedImportsDoNotReuseSourceAccountVoiceIdentifiersOrFilenames() throws Exception {
        byte[] archive = mp3Archive();
        Stored first = stored(importArchive(archive));
        authenticate("importer");
        Stored second = stored(importArchive(archive));
        assertNotEquals(first.backend().getTtsSpeakerWav(), second.backend().getTtsSpeakerWav());
        String firstSource = CharacterVoiceFiles.resolve(
                store, voiceDirectory(first), first.backend().getTtsSpeakerWav());
        String secondSource = CharacterVoiceFiles.resolve(
                store, voiceDirectory(second), second.backend().getTtsSpeakerWav());
        assertNotEquals(
                Path.of(firstSource).getFileName(), Path.of(secondSource).getFileName());
        assertTrue(firstSource.endsWith(".mp3"));
        assertTrue(secondSource.endsWith(".mp3"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"offline", "failure", "blank", "database-update"})
    void registrationFailureKeepsCommittedSourceExportableWithoutBorrowingForeignVoice(String failure)
            throws Exception {
        switch (failure) {
            case "offline" -> when(tts.isEnabled()).thenReturn(false);
            case "failure" ->
                when(tts.uploadVoice(anyString())).thenThrow(new IllegalStateException("TTS unavailable"));
            case "blank" -> when(tts.uploadVoice(anyString())).thenReturn(" ");
            case "database-update" -> doReturn(false).when(characters).updateBackend(any(CharacterBackend.class));
            default -> throw new AssertionError(failure);
        }
        Stored imported = stored(importArchive(mp3Archive()));
        assertNull(imported.backend().getTtsSpeakerWav());
        assertEquals((byte) 0, imported.backend().getEnableTts());
        String original = CharacterVoiceFiles.resolve(store, voiceDirectory(imported), null);
        assertTrue(original.endsWith(".mp3"));
        assertEquals(RESOURCES.get("voices/0/speaker.wav"), store.readString(original));
        CharacterConfigurationDTO exported =
                exportedConfiguration(imported.character().getCharacterId());
        assertEquals(
                "voices/0/" + Path.of(original).getFileName(),
                exported.getBackends().getFirst().getVoicePath());
        assertNull(exported.getBackends().getFirst().getBackend().getTtsSpeakerWav());
    }

    @Test
    void missingVoiceLogsOriginalExceptionWithoutDumpingCharacterCredentialsOrDrafts() throws Exception {
        Stored source = seedSource();
        String original = CharacterVoiceFiles.resolve(
                store, voiceDirectory(source), source.backend().getTtsSpeakerWav());
        store.delete(original);
        var logger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(CharacterApi.class);
        var events = new ch.qos.logback.core.read.ListAppender<ch.qos.logback.classic.spi.ILoggingEvent>();
        boolean additive = logger.isAdditive();
        events.start();
        logger.setAdditive(false);
        logger.addAppender(events);
        try {
            ResponseStatusException error = assertThrows(
                    ResponseStatusException.class,
                    () -> export(source.character().getCharacterId()));
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, error.getStatusCode());
            assertInstanceOf(java.nio.file.NoSuchFileException.class, error.getCause());
            assertEquals(1, events.list.size());
            var event = events.list.getFirst();
            assertSame(
                    error.getCause(),
                    ((ch.qos.logback.classic.spi.ThrowableProxy) event.getThrowableProxy()).getThrowable());
            String log = event.getFormattedMessage()
                    + ch.qos.logback.classic.spi.ThrowableProxyUtil.asString(event.getThrowableProxy());
            assertTrue(log.contains("CharacterApi.exportCharacter"));
            assertTrue(log.contains("NoSuchFileException"));
            assertFalse(log.contains(SECRET));
            assertFalse(log.contains("PRIVATE_DRAFT_SENTINEL"));
            assertFalse(log.contains("SYSTEM_VALUE_SENTINEL"));
        } finally {
            logger.detachAppender(events);
            logger.setAdditive(additive);
            events.stop();
        }
    }

    @Test
    void uploadsAndDeletesUseSeparateSourceAndTtsNames() throws Exception {
        Stored source = seedSource();
        RuntimeConfig config = fake(RuntimeConfig.class);
        when(config.getLong(anyString())).thenReturn(1024L * 1024);
        when(config.getInt(anyString())).thenReturn(1);
        setField(api, "runtimeConfig", config);
        when(tts.uploadVoice(anyString())).thenReturn("new-provider-voice.wav");
        String speaker = api.uploadVoice(
                new MockMultipartFile("file", "upload.mp3", "audio/mpeg", new byte[] {1, 2, 3}),
                source.backend().getBackendId());
        String directory = voiceDirectory(source);
        String original = CharacterVoiceFiles.resolve(store, directory, speaker);
        assertTrue(original.endsWith("-upload.mp3"));
        assertEquals("new-provider-voice.wav", speaker);
        assertEquals(
                source.backend().getTtsSpeakerWav(),
                stored(source.character().getCharacterId()).backend().getTtsSpeakerWav());
        assertTrue(api.deleteVoice(source.backend().getBackendId(), speaker));
        verify(tts).deleteVoice(speaker);
        assertFalse(store.exists(original));
        assertFalse(store.exists(CharacterVoiceFiles.mappingPath(directory, speaker)));
    }

    @Test
    void backendsSharingOneArchiveVoiceGetIndependentSourceFilesAndRegistrations() throws Exception {
        CharacterConfigurationDTO conf = configuration();
        var first = conf.getBackends().getFirst();
        first.getBackend().setEnableTts(true);
        var second =
                CharacterConfigurationDTO.fromJson(conf.toJson()).getBackends().getFirst();
        second.getBackend().setIsDefault(false);
        second.getBackend().setEnableTts(false);
        conf.setBackends(List.of(first, second));
        Long id = importArchive(archive(conf.toJson(), "apiVersion: v3\n"));
        CharacterInfo character = sessions.getMapper(CharacterInfoMapper.class)
                .selectByPrimaryKey(id)
                .orElseThrow();
        var backends = sessions.getMapper(CharacterBackendMapper.class)
                .select(query -> query.where(
                        CharacterBackendDynamicSqlSupport.characterUid, isEqualTo(character.getCharacterUid())));
        assertEquals(2, backends.size());
        assertEquals(
                2,
                backends.stream()
                        .map(CharacterBackend::getTtsSpeakerWav)
                        .distinct()
                        .count());
        assertEquals(
                Set.of((byte) 0, (byte) 1),
                new HashSet<>(
                        backends.stream().map(CharacterBackend::getEnableTts).toList()));
        for (CharacterBackend backend : backends) {
            String directory =
                    "private/creator/character/voices/" + character.getCharacterUid() + "/" + backend.getBackendId();
            String source = CharacterVoiceFiles.resolve(store, directory, backend.getTtsSpeakerWav());
            assertEquals(RESOURCES.get("voices/0/speaker.wav"), store.readString(source));
        }
        verify(tts, times(2)).uploadVoice(anyString());
    }

    private CharacterConfigurationDTO exportedConfiguration(Long id) throws Exception {
        Path extracted = Files.createTempDirectory(root, "voice-export-");
        TarUtils.extractGzip(new ByteArrayInputStream(export(id)), extracted);
        try (var files = Files.walk(extracted)) {
            assertTrue(files.noneMatch(path -> path.toString().contains(".sources")));
        }
        return CharacterConfigurationDTO.fromJson(Files.readString(extracted.resolve("character.json")));
    }

    private String voiceDirectory(Stored character) {
        return "private/" + character.character().getUserId() + "/character/voices/"
                + character.character().getCharacterUid() + "/"
                + character.backend().getBackendId();
    }

    private byte[] mp3Archive() throws Exception {
        CharacterConfigurationDTO conf = configuration();
        conf.getBackends().getFirst().setVoicePath("voices/0/speaker.mp3");
        conf.getBackends().getFirst().getBackend().setEnableTts(true);
        Map<String, String> resources = new LinkedHashMap<>(RESOURCES);
        resources.put("voices/0/speaker.mp3", resources.remove("voices/0/speaker.wav"));
        return archive(conf.toJson(), "apiVersion: v3\n", resources);
    }

    @Test
    void failureAfterRealRagInsertRollsBackEveryTableAndCleansOnlyNewFiles() throws Exception {
        seedSource();
        Map<String, Long> beforeRows = rowCounts();
        Map<String, String> beforeFiles = files();
        clearInvocations(rags);
        doAnswer(call -> {
                    assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
                    assertEquals(true, call.callRealMethod());
                    Map<String, Long> during = rowCounts();
                    for (String table : TABLES) {
                        long delta = table.equals("tag") ? 2 : table.equals("interactive_stats") ? 0 : 1;
                        assertEquals(beforeRows.get(table) + delta, during.get(table), table);
                    }
                    assertEquals(beforeFiles.size() + RESOURCES.size() + 1, files().size());
                    return false;
                })
                .when(rags)
                .create(any(RagTask.class));
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> importArchive(archive(configuration().toJson(), "apiVersion: v3\n")));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, error.getStatusCode());
        verify(rags).create(any(RagTask.class));
        assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
        assertEquals(beforeRows, rowCounts());
        assertEquals(beforeFiles, files());
    }

    @ParameterizedTest
    @ValueSource(strings = {"missing-backends", "null-backends", "null-backend", "malformed-manifest"})
    void invalidArchiveIsRejectedBeforeCharacterOrResourceWrites(String invalid) throws Exception {
        ObjectNode json = InfoUtils.defaultMapper().valueToTree(configuration());
        if (invalid.equals("missing-backends")) json.remove("backends");
        if (invalid.equals("null-backends")) json.putNull("backends");
        if (invalid.equals("null-backend")) json.putArray("backends").addNull();
        Map<String, Long> before = rowCounts();
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> importArchive(archive(
                        json.toString(),
                        invalid.equals("malformed-manifest") ? "apiVersion: [\n" : "apiVersion: v3\n")));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verify(characters, never()).create(any());
        verifyNoInteractions(rags);
        assertEquals(before, rowCounts());
        assertTrue(files().isEmpty());
    }

    @Test
    void publicNonOwnerExportUsesRealDetailsPermissionsButExcludesPrivateResources() throws Exception {
        Stored source = seedSource();
        authenticate("viewer");
        Path extracted = root.resolve("public-export");
        TarUtils.extractGzip(new ByteArrayInputStream(export(source.character().getCharacterId())), extracted);
        String json = Files.readString(extracted.resolve("character.json"));
        CharacterConfigurationDTO conf = CharacterConfigurationDTO.fromJson(json);
        assertTrue(conf.getBackends().isEmpty());
        assertTrue(conf.getRagTasks().isEmpty());
        assertNull(conf.getCharacter().getDraft());
        assertEquals(
                Set.of("avatar/a.png", "pictures/p.png", "videos/v.mp4"),
                conf.getResourceUrls().keySet());
        for (String resource : conf.getResourceUrls().keySet())
            assertEquals(RESOURCES.get(resource), Files.readString(extracted.resolve(resource)));
        assertFalse(Files.exists(extracted.resolve("documents")));
        assertFalse(Files.exists(extracted.resolve("voices")));
        assertFalse(json.contains(SECRET));
        assertFalse(json.contains("PRIVATE_DRAFT_SENTINEL"));
        sql.update(
                "UPDATE character_info SET visibility = 'private' WHERE character_id = ?",
                source.character().getCharacterId());
        assertEquals(
                HttpStatus.NOT_FOUND,
                assertThrows(
                                ResponseStatusException.class,
                                () -> export(source.character().getCharacterId()))
                        .getStatusCode());
    }

    private CharacterConfigurationDTO configuration() {
        String picture = "https://source.invalid/pictures/p.png", video = "https://source.invalid/videos/v.mp4";
        Map<String, List<String>> urls = new LinkedHashMap<>();
        for (String name : List.of("avatar/a.png", "pictures/p.png", "videos/v.mp4"))
            urls.put(name, List.of("https://source.invalid/" + name));
        return CharacterConfigurationDTO.builder()
                .character(CharacterCreateDTO.builder()
                        .name("Transfer persona")
                        .visibility("public")
                        .draft(DRAFT)
                        .tags(List.of("character-tag"))
                        .avatar("https://source.invalid/avatar/a.png")
                        .picture(picture)
                        .video(video)
                        .profile("Look: " + picture + " Watch: " + video)
                        .build())
                .backends(List.of(CharacterBackendConfigurationDTO.builder()
                        .backend(CharacterBackendDTO.builder()
                                .isDefault(true)
                                .moderationApiKeyValue(SECRET)
                                .imageApiKeyValue(SECRET)
                                .tgBotToken(SECRET)
                                .ttsSpeakerType("wav")
                                .ttsSpeakerWav("speaker.wav")
                                .build())
                        .chatPrompt(PromptCreateDTO.builder()
                                .name("Transfer prompt")
                                .template(TEMPLATE)
                                .inputs(VARIABLES)
                                .visibility("public")
                                .draft(DRAFT)
                                .tags(List.of("prompt-tag"))
                                .aiModels(List.of("test-model"))
                                .build())
                        .chatPromptTask(PromptTaskDTO.builder()
                                .modelId("test-model")
                                .apiKeyValue(SECRET)
                                .apiKeyName(SECRET)
                                .params(Map.of("temperature", 0.25))
                                .promptRef(PromptRefDTO.from(
                                        99L,
                                        Map.of("tone", "warm", "count", 3, "INPUT", "SYSTEM_VALUE_SENTINEL"),
                                        true))
                                .build())
                        .voicePath("voices/0/speaker.wav")
                        .build()))
                .resourceUrls(urls)
                .ragTasks(List.of(RagTaskDTO.builder()
                        .sourceType("file")
                        .source("documents/notes.txt")
                        .maxSegmentSize(777)
                        .maxOverlapSize(53)
                        .build()))
                .build();
    }

    private Stored seedSource() throws Exception {
        Stored source = stored(importArchive(archive(configuration().toJson(), "apiVersion: v3\n")));
        assertPortable(source); // Also prove inbound sanitization on an untrusted archive, independently of export.
        sql.update(
                "UPDATE character_info SET visibility = 'public', draft = ? WHERE character_id = ?",
                DRAFT,
                source.character().getCharacterId());
        sql.update(
                "UPDATE prompt_info SET inputs = ?, draft = ? WHERE prompt_id = ?",
                VARIABLES,
                DRAFT,
                source.prompt().getPromptId());
        sql.update(
                "UPDATE prompt_task SET variables = ?, draft = 1, api_key_value = ?, api_key_name = ? WHERE task_id = ?",
                VARIABLES,
                SECRET,
                SECRET,
                source.task().getTaskId());
        sql.update(
                "UPDATE character_backend SET moderation_api_key_value = ?, image_api_key_value = ?, tg_bot_token = ? WHERE backend_id = ?",
                SECRET,
                SECRET,
                SECRET,
                source.backend().getBackendId());
        return stored(source.character().getCharacterId());
    }

    private void assertPortable(Stored value) throws Exception {
        assertEquals("private", value.character().getVisibility());
        assertNull(value.character().getDraft());
        assertEquals("private", value.prompt().getVisibility());
        assertNull(value.prompt().getDraft());
        assertEquals(TEMPLATE, value.prompt().getTemplate());
        assertEquals(
                InfoUtils.defaultMapper().valueToTree(CUSTOM),
                InfoUtils.defaultMapper().readTree(value.prompt().getInputs()));
        assertEquals(
                InfoUtils.defaultMapper().valueToTree(CUSTOM),
                InfoUtils.defaultMapper().readTree(value.task().getVariables()));
        assertEquals((byte) 0, value.task().getDraft());
        assertNull(value.task().getApiKeyValue());
        assertNull(value.task().getApiKeyName());
        assertNull(value.backend().getModerationApiKeyValue());
        assertNull(value.backend().getImageApiKeyValue());
        assertNull(value.backend().getTgBotToken());
    }

    private record Stored(CharacterInfo character, CharacterBackend backend, PromptTask task, PromptInfo prompt) {}

    private Stored stored(Long id) {
        CharacterInfo c = sessions.getMapper(CharacterInfoMapper.class)
                .selectByPrimaryKey(id)
                .orElseThrow();
        List<CharacterBackend> backends = sessions.getMapper(CharacterBackendMapper.class)
                .select(q -> q.where(CharacterBackendDynamicSqlSupport.characterUid, isEqualTo(c.getCharacterUid())));
        assertEquals(1, backends.size());
        CharacterBackend b = backends.getFirst();
        PromptTask t = sessions.getMapper(PromptTaskMapper.class)
                .selectByPrimaryKey(b.getChatPromptTaskId())
                .orElseThrow();
        PromptInfo p = sessions.getMapper(PromptInfoMapper.class)
                .selectOne(q -> q.where(PromptInfoDynamicSqlSupport.promptUid, isEqualTo(t.getPromptUid())))
                .orElseThrow();
        return new Stored(c, b, t, p);
    }

    private byte[] export(Long id) {
        MockHttpServletResponse response = new MockHttpServletResponse();
        api.exportCharacter(id, false, new MockHttpServletRequest(), response);
        return response.getContentAsByteArray();
    }

    private Long importArchive(byte[] archive) {
        return api.importCharacter(
                new MockHttpServletRequest(),
                new MockMultipartFile("file", "character.tar.gz", "application/gzip", archive));
    }

    private byte[] archive(String json, String manifest) throws Exception {
        return archive(json, manifest, RESOURCES);
    }

    private byte[] archive(String json, String manifest, Map<String, String> resources) throws Exception {
        Map<String, String> contents = new LinkedHashMap<>(resources);
        contents.put("manifest.yml", manifest);
        contents.put("character.json", json);
        List<TarUtils.Entry> entries = new ArrayList<>();
        contents.forEach((name, text) -> {
            byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
            entries.add(new TarUtils.Entry(name, () -> new ByteArrayInputStream(bytes), bytes.length));
        });
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        TarUtils.compressGzip(entries, out);
        return out.toByteArray();
    }

    private Map<String, Long> rowCounts() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (String table : TABLES) counts.put(table, sql.queryForObject("SELECT COUNT(*) FROM " + table, Long.class));
        return counts;
    }

    private Map<String, String> files() throws Exception {
        Map<String, String> contents = new TreeMap<>();
        for (String file : store.list("", null, true))
            contents.put(file, Base64.getEncoder().encodeToString(store.readBytes(file)));
        return contents;
    }

    private static void injectMappers(Object target, Class<?>... types) {
        for (Class<?> type : types) {
            String name = type.getSimpleName();
            setField(target, Character.toLowerCase(name.charAt(0)) + name.substring(1), sessions.getMapper(type));
        }
    }

    private static void authenticate(String id) {
        SecurityContextHolder.getContext()
                .setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
                        new User().withUserId(id), null, List.of(new SimpleGrantedAuthority("ROLE_USER"))));
    }

    private static <T> T fake(Class<T> type) {
        return mock(type, withSettings().mockMaker(MockMakers.SUBCLASS));
    }

    @SuppressWarnings("unchecked")
    private static <T> T realSpy(T instance) {
        return (T) mock(
                instance.getClass(),
                withSettings()
                        .mockMaker(MockMakers.SUBCLASS)
                        .spiedInstance(instance)
                        .defaultAnswer(CALLS_REAL_METHODS));
    }
}
