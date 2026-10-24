package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fun.freechat.api.CharacterApi;
import fun.freechat.api.dto.CharacterCreateDTO;
import fun.freechat.api.dto.CharacterUpdateDTO;
import fun.freechat.mapper.CharacterInfoMapper;
import fun.freechat.model.CharacterInfo;
import fun.freechat.model.User;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.character.impl.CharacterServiceImpl;
import fun.freechat.service.connectors.*;
import fun.freechat.service.enums.Visibility;
import fun.freechat.service.skills.SkillCharacterPolicy;
import fun.freechat.service.skills.SkillService;
import fun.freechat.service.tools.ToolArguments;
import fun.freechat.service.tools.ToolRegistry;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockMakers;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

class ConnectorCharacterPolicyTest {
    private static final String OWNED = "a".repeat(32);
    private static final String FOREIGN = "b".repeat(32);
    private final ConnectorService connectors = fake(ConnectorService.class);
    private final CharacterInfoMapper characters = fake(CharacterInfoMapper.class);
    private final CharacterInfo stored =
            new CharacterInfo().withCharacterId(1L).withUserId("creator").withExt(ext(OWNED));
    private final ConnectorCharacterPolicy policy = new ConnectorCharacterPolicy(connectors, characters);
    private final SkillCharacterPolicy skills = new SkillCharacterPolicy(fake(SkillService.class), characters);
    private SecurityContext previousSecurity;

    @BeforeEach
    void setup() {
        previousSecurity = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        doReturn(Optional.of(stored)).when(characters).selectByPrimaryKey(1L);
        when(connectors.list("creator")).thenReturn(List.of(summary(OWNED)));
        when(connectors.list("viewer")).thenReturn(List.of(summary(FOREIGN)));
    }

    @AfterEach
    void restore() {
        SecurityContextHolder.setContext(previousSecurity);
    }

    @Test
    void absentSelectionsDoNotReadRegistry() {
        policy.validate("creator", null, null);
        policy.validate("creator", "{}", "{\"profile\":\"persona\"}");
        verifyNoInteractions(connectors);
    }

    @Test
    void topLevelAndDraftSelectionsBothBelongToCharacterOwner() {
        policy.validate("creator", ext(OWNED), draft(ext(OWNED)));
        denied(() -> policy.validate("creator", ext(FOREIGN), null));
        denied(() -> policy.validate("creator", ext(OWNED), draft(ext(FOREIGN))));
        denied(() -> policy.validate(null, ext(OWNED), null));
        verify(connectors, never()).list("viewer");
        verify(connectors, never()).resolve(anyString(), anyString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ROLE_USER", "ROLE_ADMIN"})
    void updateResolvesStoredOwnerRatherThanViewerOrAdministrator(String role) {
        authenticate(role);
        assertEquals("creator", policy.validateUpdate(1L, ext(OWNED), null));
        denied(() -> policy.validateUpdate(1L, ext(FOREIGN), null));
        denied(() -> policy.validateUpdate(1L, null, draft(ext(FOREIGN))));
        verify(connectors, never()).list("viewer");
    }

    @Test
    void publicationValidatesEffectiveDraftOrFormalSelectionAndCurrentOwnership() {
        policy.validatePublication(1L);
        stored.setDraft(draft(ext(FOREIGN)));
        denied(() -> policy.validatePublication(1L));
        stored.setDraft("{\"profile\":\"persona\"}");
        when(connectors.list("creator")).thenReturn(List.of());
        denied(() -> policy.validatePublication(1L));
        stored.setDraft(draft("{}"));
        assertDoesNotThrow(() -> policy.validatePublication(1L));
    }

    @Test
    void missingCharacterFailsClosed() {
        doReturn(Optional.empty()).when(characters).selectByPrimaryKey(2L);
        assertEquals(
                ConnectorException.Code.CONNECTOR_NOT_FOUND,
                assertThrows(ConnectorException.class, () -> policy.validateUpdate(2L, ext(OWNED), null))
                        .code());
        assertThrows(ConnectorException.class, () -> policy.validatePublication(2L));
        verifyNoInteractions(connectors);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ROLE_USER", "ROLE_ADMIN"})
    void apiChecksCreateUpdateAndPublicationBeforeCharacterWrites(String role) {
        authenticate(role);
        CharacterService service = fake(CharacterService.class);
        CharacterApi api = new CharacterApi();
        ReflectionTestUtils.setField(api, "characterService", service);
        ReflectionTestUtils.setField(api, "connectorPolicy", policy);
        ReflectionTestUtils.setField(api, "skillPolicy", skills);
        ReflectionTestUtils.setField(api, "toolRegistry", new ToolRegistry(List.of()));
        // The caller owns FOREIGN, not OWNED: create must use the creator of the new character.
        denied(() -> api.create(
                CharacterCreateDTO.builder().name("Persona").ext(ext(OWNED)).build()));
        CharacterUpdateDTO update = new CharacterUpdateDTO();
        update.setExt(ext(FOREIGN));
        denied(() -> api.update(1L, update));
        update.setExt(null);
        update.setDraft(draft(ext(FOREIGN)));
        denied(() -> api.update(1L, update));
        stored.setDraft(draft(ext(FOREIGN)));
        denied(() -> api.publish(1L, Optional.of("public")));
        verifyNoInteractions(service);
    }

    @Test
    void directServiceCallsCannotBypassConnectorValidation() {
        CharacterServiceImpl service = service();
        SqlSessionFactory sessions = fake(SqlSessionFactory.class);
        SqlSession session = fake(SqlSession.class);
        when(sessions.openSession()).thenReturn(session);
        ReflectionTestUtils.setField(service, "sqlSessionFactory", sessions);
        assertFalse(service.create(Pair.of(
                new CharacterInfo().withUserId("creator").withName("Persona").withExt(ext(FOREIGN)), List.of())));
        verify(characters, never()).insertSelective(any());
        verify(session).rollback();
        CharacterInfo forged =
                new CharacterInfo().withCharacterId(1L).withUserId("viewer").withExt(ext(FOREIGN));
        denied(() -> service.update(Pair.of(forged, List.of())));
        verify(characters, never()).updateByPrimaryKeySelective(any());
        stored.setDraft(draft(ext(FOREIGN)));
        denied(() -> service.publish(1L, Visibility.PUBLIC, new User().withUserId("viewer")));
        verify(sessions, times(1)).openSession();
        verify(connectors, never()).list("viewer");
    }

    @Test
    void serviceUpdateRetainsStoredOwnerAndAllowsTheirInactiveConnectors() {
        CharacterServiceImpl service = service();
        SqlSessionFactory sessions = fake(SqlSessionFactory.class);
        when(sessions.openSession()).thenReturn(fake(SqlSession.class));
        ReflectionTestUtils.setField(service, "sqlSessionFactory", sessions);
        CharacterInfo update =
                new CharacterInfo().withCharacterId(1L).withUserId("viewer").withExt(ext(OWNED));
        assertTrue(service.update(Pair.of(update, List.of())));
        assertEquals("creator", update.getUserId());
        verify(connectors).list("creator");
        verify(connectors, never()).list("viewer");
    }

    private CharacterServiceImpl service() {
        CharacterServiceImpl service = new CharacterServiceImpl();
        ReflectionTestUtils.setField(service, "connectorPolicy", policy);
        ReflectionTestUtils.setField(service, "skillPolicy", skills);
        ReflectionTestUtils.setField(service, "characterInfoMapper", characters);
        ReflectionTestUtils.setField(service, "toolRegistry", new ToolRegistry(List.of()));
        return service;
    }

    private static void authenticate(String role) {
        SecurityContextHolder.getContext()
                .setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
                        new User().withUserId("viewer"), null, List.of(new SimpleGrantedAuthority(role))));
    }

    private static ConnectorService.Summary summary(String id) {
        return new ConnectorService.Summary(
                id, "revision", "Connector", "http", "https://example.com/mcp", 60_000, false, Set.of(), Instant.EPOCH);
    }

    private static String ext(String id) {
        return ToolArguments.result(Map.of("connectors", Map.of("enabled", List.of(id))));
    }

    private static String draft(String ext) {
        return ToolArguments.result(Map.of("ext", ext));
    }

    private static void denied(org.junit.jupiter.api.function.Executable operation) {
        assertEquals(
                ConnectorException.Code.CONNECTOR_ACCESS_DENIED,
                assertThrows(ConnectorException.class, operation).code());
    }

    private static <T> T fake(Class<T> type) {
        return mock(type, withSettings().mockMaker(MockMakers.SUBCLASS));
    }
}
