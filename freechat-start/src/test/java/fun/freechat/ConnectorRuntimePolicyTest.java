package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fun.freechat.mapper.*;
import fun.freechat.model.*;
import fun.freechat.service.connectors.*;
import fun.freechat.service.skills.SkillCharacterAccess;
import fun.freechat.service.tools.ToolContext;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockMakers;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;

class ConnectorRuntimePolicyTest {
    private static final String ID = "a".repeat(32);
    private final ChatContextMapper contexts = fake(ChatContextMapper.class);
    private final CharacterBackendMapper backends = fake(CharacterBackendMapper.class);
    private final CharacterInfoMapper characters = fake(CharacterInfoMapper.class);
    private final OrgRelationshipMapper relationships = fake(OrgRelationshipMapper.class);
    private final UserMapper users = fake(UserMapper.class);
    private final CharacterInfo character = new CharacterInfo()
            .withUserId("creator")
            .withCharacterUid("character")
            .withVisibility("public")
            .withExt("{\"connectors\":{\"enabled\":[\"" + ID + "\"]}}");
    private final DatabaseConnectorPolicySource policies = new DatabaseConnectorPolicySource(
            contexts, backends, characters, new SkillCharacterAccess(relationships), users);

    @BeforeEach
    void setup() {
        doReturn(Optional.of(
                        new User().withUserId("viewer").withEnabled((byte) 1).withLocked((byte) 0)))
                .when(users)
                .selectByPrimaryKey("viewer");
        doReturn(Optional.of(
                        new User().withUserId("creator").withEnabled((byte) 1).withLocked((byte) 0)))
                .when(users)
                .selectByPrimaryKey("creator");
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
    }

    @Test
    void sharedRoleUsesCreatorAndNeverTheVisitorAsConnectorOwner() {
        var policy = policies.load(scope("viewer", "character", "backend"));
        assertEquals("creator", policy.ownerId());
        assertEquals(Set.of(ID), policy.enabled());
        assertTrue(policy.permitted());
    }

    @ParameterizedTest
    @ValueSource(strings = {"user", "character", "backend"})
    void forgedInvocationScopeIsDenied(String changed) {
        assertThrows(
                IllegalStateException.class,
                () -> policies.load(scope(
                        changed.equals("user") ? "attacker" : "viewer",
                        changed.equals("character") ? "other" : "character",
                        changed.equals("backend") ? "other" : "backend")));
    }

    @Test
    void privateVisibilityAndOrganizationChangesAreReadFresh() {
        character.setVisibility("private");
        assertThrows(IllegalStateException.class, () -> policies.load(scope("viewer", "character", "backend")));
        character.setVisibility("public_org");
        doReturn(List.of(new OrgRelationship()
                        .withUserId("creator")
                        .withOwnerId("viewer")
                        .withEnabled((byte) 1)
                        .withIsVirtual((byte) 0)))
                .when(relationships)
                .selectMany(any(SelectStatementProvider.class));
        assertTrue(policies.load(scope("viewer", "character", "backend")).permitted());
        doReturn(List.of()).when(relationships).selectMany(any(SelectStatementProvider.class));
        assertThrows(IllegalStateException.class, () -> policies.load(scope("viewer", "character", "backend")));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {"\"tools\":{\"enabled\":[]}", "\"skills\":{\"enabled\":[\"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb\"]}"})
    void explicitToolFreeOrPromptOnlySkillRolesNeverGrantConnectors(String restriction) {
        character.setExt("{\"connectors\":{\"enabled\":[\"" + ID + "\"]}," + restriction + "}");
        assertFalse(policies.load(scope("viewer", "character", "backend")).permitted());
    }

    @ParameterizedTest
    @ValueSource(strings = {"viewer", "creator"})
    void disabledOrLockedAccountRevokesConnectorUse(String identity) {
        doReturn(Optional.of(new User().withUserId(identity).withEnabled((byte) 0)))
                .when(users)
                .selectByPrimaryKey(identity);
        assertThrows(IllegalStateException.class, () -> policies.load(scope("viewer", "character", "backend")));
        doReturn(Optional.of(
                        new User().withUserId(identity).withEnabled((byte) 1).withLocked((byte) 1)))
                .when(users)
                .selectByPrimaryKey(identity);
        assertThrows(IllegalStateException.class, () -> policies.load(scope("viewer", "character", "backend")));
    }

    @Test
    void noConfiguredConnectorsIsBackwardCompatible() {
        character.setExt(null);
        assertTrue(
                policies.load(scope("viewer", "character", "backend")).enabled().isEmpty());
    }

    private static ToolContext scope(String user, String character, String backend) {
        return new ToolContext("chat", user, character, backend, Duration.ofSeconds(10));
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
