package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fun.freechat.mapper.*;
import fun.freechat.model.*;
import fun.freechat.service.rag.EmbeddingModelService;
import fun.freechat.service.skills.*;
import fun.freechat.service.tools.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockMakers;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;

class SkillRuntimePolicyTest {
    private static final String ID = "a".repeat(32);
    private final ChatContextMapper contexts = fake(ChatContextMapper.class);
    private final CharacterBackendMapper backends = fake(CharacterBackendMapper.class);
    private final CharacterInfoMapper characters = fake(CharacterInfoMapper.class);
    private final SkillService skills = fake(SkillService.class);
    private final OrgRelationshipMapper relationships = fake(OrgRelationshipMapper.class);
    private final EmbeddingModelService embeddings = fake(EmbeddingModelService.class);
    private final CharacterInfo character = new CharacterInfo()
            .withUserId("creator")
            .withCharacterUid("character")
            .withLang("en")
            .withVisibility("public")
            .withExt(ToolArguments.result(Map.of("skills", Map.of("enabled", List.of(ID)))));
    private SkillRuntimeBinder binder;

    @BeforeEach
    void setup() {
        doReturn(Optional.of(new ChatContext()
                        .withChatId("chat")
                        .withUserId("viewer")
                        .withBackendId("backend")))
                .when(contexts)
                .selectByPrimaryKey("chat");
        doReturn(Optional.of(new CharacterBackend().withBackendId("backend").withCharacterUid("character")))
                .when(backends)
                .selectByPrimaryKey("backend");
        doReturn(Optional.of(character)).when(characters).selectOne(any(SelectStatementProvider.class));
        when(skills.get("creator", ID))
                .thenReturn(new SkillService.Installed(
                        ID,
                        "creator",
                        "revision",
                        new SkillBundle("story", "description", Map.of("SKILL.md", "body"), "digest"),
                        Instant.EPOCH));
        when(skills.list("creator"))
                .thenReturn(List.of(new SkillService.Summary(ID, "revision", "story", "description", Instant.EPOCH)));
        when(embeddings.tokenCountEstimatorForLang("en")).thenReturn(SkillChatTest.estimator());
        var broker = new ToolRuntimeBroker(
                new ToolRegistry(SkillTools.definitions()),
                chat -> new ToolPolicySource.Policy(
                        "viewer",
                        "character",
                        "backend",
                        CharacterToolConfiguration.parse(character.getExt()).enabled(),
                        false,
                        false),
                List.of(),
                SkillTools.NAMES);
        binder = new SkillRuntimeBinder(
                contexts,
                backends,
                characters,
                skills,
                embeddings,
                broker,
                new SkillCharacterAccess(relationships),
                8192);
    }

    @Test
    void publicCharacterUsesCreatorSkillsWithoutGrantingVisitorLibraryAccess() {
        SkillInvocation invocation =
                binder.binder("chat", "viewer", "character", "backend").bind();
        assertNotNull(invocation);
        verify(skills).get("creator", ID);
        verify(skills, never()).get(eq("viewer"), anyString());
    }

    @Test
    void forgedChatIdentityNeverLoadsAnySkill() {
        assertThrows(
                IllegalStateException.class,
                () -> binder.binder("chat", "attacker", "character", "backend").bind());
        verifyNoInteractions(skills);
    }

    @Test
    void currentRevisionAndConfigurationAreCheckedAfterBinding() {
        SkillInvocation invocation =
                binder.binder("chat", "viewer", "character", "backend").bind();
        var executors = invocation.restrict(invocation.executors());
        when(skills.list("creator"))
                .thenReturn(
                        List.of(new SkillService.Summary(ID, "replacement", "story", "description", Instant.EPOCH)));
        assertThrows(
                IllegalStateException.class,
                () -> executors
                        .get(SkillTools.LOAD.specification())
                        .execute(
                                BuiltinToolChatTest.call("skill", ToolArguments.result(Map.of("skillId", ID))),
                                "chat"));
        when(skills.list("creator"))
                .thenReturn(List.of(new SkillService.Summary(ID, "revision", "story", "description", Instant.EPOCH)));
        character.setExt("{\"skills\":{\"enabled\":[]}}");
        assertThrows(
                IllegalStateException.class,
                () -> invocation.messages(List.of(dev.langchain4j.data.message.UserMessage.from("hi"))));
    }

    @Test
    void privateVisibilityRevokesCapturedLoaderAndFutureBindings() {
        SkillInvocation invocation =
                binder.binder("chat", "viewer", "character", "backend").bind();
        var executor = invocation.restrict(invocation.executors()).get(SkillTools.LOAD.specification());
        character.setVisibility("private");
        assertThrows(
                IllegalStateException.class,
                () -> executor.execute(
                        BuiltinToolChatTest.call("skill", ToolArguments.result(Map.of("skillId", ID))), "chat"));
        assertThrows(
                IllegalStateException.class,
                () -> binder.binder("chat", "viewer", "character", "backend").bind());
    }

    @Test
    void organizationRemovalRevokesAccessWithoutCachedRelationshipReads() {
        character.setVisibility("public_org");
        doReturn(List.of(new OrgRelationship()
                        .withUserId("creator")
                        .withOwnerId("viewer")
                        .withEnabled((byte) 1)
                        .withIsVirtual((byte) 0)))
                .when(relationships)
                .selectMany(any(SelectStatementProvider.class));
        SkillInvocation invocation =
                binder.binder("chat", "viewer", "character", "backend").bind();
        var executor = invocation.restrict(invocation.executors()).get(SkillTools.LOAD.specification());
        doReturn(List.of()).when(relationships).selectMany(any(SelectStatementProvider.class));
        assertThrows(
                IllegalStateException.class,
                () -> executor.execute(
                        BuiltinToolChatTest.call("skill", ToolArguments.result(Map.of("skillId", ID))), "chat"));
        assertThrows(
                IllegalStateException.class,
                () -> binder.binder("chat", "viewer", "character", "backend").bind());
    }

    @Test
    void legacyCharacterNeedsNoSkillLibraryOrTokenEstimator() {
        character.setExt(null);
        assertNull(binder.binder("chat", "viewer", "character", "backend").bind());
        verifyNoInteractions(skills);
    }

    @Test
    void characterBindingsMustBelongToCreatorAndTransfersClearReferences() {
        SkillCharacterPolicy policy = new SkillCharacterPolicy(skills, characters);
        policy.validate("creator", character.getExt(), null);
        when(skills.list("viewer")).thenReturn(List.of());
        assertEquals(
                SkillException.Code.SKILL_ACCESS_DENIED,
                assertThrows(SkillException.class, () -> policy.validate("viewer", character.getExt(), null))
                        .code());
    }

    private static <T> T fake(Class<T> type) {
        return mock(
                type,
                withSettings()
                        .mockMaker(MockMakers.SUBCLASS)
                        .defaultAnswer(invocation -> invocation.getMethod().isDefault()
                                ? CALLS_REAL_METHODS.answer(invocation)
                                : RETURNS_DEFAULTS.answer(invocation)));
    }
}
