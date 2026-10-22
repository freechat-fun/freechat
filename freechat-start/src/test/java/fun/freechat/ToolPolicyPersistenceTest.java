package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fun.freechat.mapper.CharacterBackendMapper;
import fun.freechat.mapper.CharacterInfoMapper;
import fun.freechat.mapper.ChatContextMapper;
import fun.freechat.model.CharacterBackend;
import fun.freechat.model.CharacterInfo;
import fun.freechat.model.ChatContext;
import fun.freechat.service.tools.DatabaseToolPolicySource;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;

class ToolPolicyPersistenceTest {
    @Test
    void loadsCurrentNonhiddenCharacterPolicyWithoutSessionOrCharacterCaches() {
        ChatContextMapper contexts = mock(
                ChatContextMapper.class,
                withSettings().mockMaker(org.mockito.MockMakers.SUBCLASS).defaultAnswer(CALLS_REAL_METHODS));
        CharacterBackendMapper backends = mock(
                CharacterBackendMapper.class,
                withSettings().mockMaker(org.mockito.MockMakers.SUBCLASS).defaultAnswer(CALLS_REAL_METHODS));
        CharacterInfoMapper characters = mock(
                CharacterInfoMapper.class,
                withSettings().mockMaker(org.mockito.MockMakers.SUBCLASS).defaultAnswer(CALLS_REAL_METHODS));
        ChatContext context =
                new ChatContext().withChatId("chat").withBackendId("backend").withUserId("user");
        CharacterBackend backend = new CharacterBackend()
                .withBackendId("backend")
                .withCharacterUid("character")
                .withEnableAlbumTool((byte) 0)
                .withLongTermMemoryWindowSize(0);
        CharacterInfo character =
                new CharacterInfo().withCharacterUid("character").withExt("{\"tools\":{\"enabled\":[\"calculate\"]}}");
        when(contexts.selectOne(any(SelectStatementProvider.class))).thenReturn(Optional.of(context));
        when(backends.selectOne(any(SelectStatementProvider.class))).thenReturn(Optional.of(backend));
        when(characters.selectOne(any(SelectStatementProvider.class))).thenReturn(Optional.of(character));
        DatabaseToolPolicySource source = new DatabaseToolPolicySource(contexts, backends, characters);
        var initial = source.load("chat");
        assertEquals("user", initial.userId());
        assertEquals("character", initial.characterUid());
        assertEquals("backend", initial.backendId());
        assertEquals(Set.of("calculate"), initial.enabled());
        assertFalse(initial.memory());
        assertFalse(initial.album());
        character.setExt("{\"tools\":{\"enabled\":[]}}");
        assertTrue(source.load("chat").enabled().isEmpty());
        character.setExt(null);
        backend.setEnableAlbumTool((byte) 1);
        backend.setLongTermMemoryWindowSize(5);
        var defaults = source.load("chat");
        assertTrue(defaults.permits("find_image"));
        assertTrue(defaults.permits("search_memory"));
        assertTrue(defaults.permits("read_memory"));
        var sql = ArgumentCaptor.forClass(SelectStatementProvider.class);
        verify(characters, times(3)).selectOne(sql.capture());
        for (var statement : sql.getAllValues()) {
            assertTrue(statement.getSelectStatement().contains("order by version DESC, character_id DESC"));
            assertTrue(statement.getSelectStatement().contains("visibility <>"));
            assertTrue(statement.getParameters().containsValue("hidden"));
            assertTrue(statement.getParameters().containsValue("character"));
        }
        verify(contexts, times(3)).selectByPrimaryKey("chat");
        verify(backends, times(3)).selectByPrimaryKey("backend");
    }

    @Test
    void deletedOrMalformedPolicyIsSanitizedAndNeverInherited() {
        ChatContextMapper contexts = mock(
                ChatContextMapper.class,
                withSettings().mockMaker(org.mockito.MockMakers.SUBCLASS).defaultAnswer(CALLS_REAL_METHODS));
        CharacterBackendMapper backends = mock(
                CharacterBackendMapper.class,
                withSettings().mockMaker(org.mockito.MockMakers.SUBCLASS).defaultAnswer(CALLS_REAL_METHODS));
        CharacterInfoMapper characters = mock(
                CharacterInfoMapper.class,
                withSettings().mockMaker(org.mockito.MockMakers.SUBCLASS).defaultAnswer(CALLS_REAL_METHODS));
        when(contexts.selectOne(any(SelectStatementProvider.class))).thenReturn(Optional.empty());
        DatabaseToolPolicySource source = new DatabaseToolPolicySource(contexts, backends, characters);
        assertUnavailable(source);
        verifyNoInteractions(backends, characters);
        when(contexts.selectOne(any(SelectStatementProvider.class)))
                .thenReturn(
                        Optional.of(new ChatContext().withBackendId("backend").withUserId("user")));
        when(backends.selectOne(any(SelectStatementProvider.class)))
                .thenReturn(Optional.of(
                        new CharacterBackend().withBackendId("backend").withCharacterUid("character")));
        when(characters.selectOne(any(SelectStatementProvider.class)))
                .thenReturn(Optional.of(new CharacterInfo()
                        .withCharacterUid("character")
                        .withExt("{\"tools\":\"private-malformed-value\"}")));
        assertUnavailable(source);
        when(characters.selectOne(any(SelectStatementProvider.class)))
                .thenThrow(new IllegalArgumentException("private-database-detail"));
        assertUnavailable(source);
    }

    private static void assertUnavailable(DatabaseToolPolicySource source) {
        var error = assertThrows(IllegalStateException.class, () -> source.load("chat"));
        assertEquals("Tool authorization unavailable", error.getMessage());
        assertNull(error.getCause());
    }
}
