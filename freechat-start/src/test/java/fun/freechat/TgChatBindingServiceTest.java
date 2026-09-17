package fun.freechat;

import static fun.freechat.service.util.ChannelUtils.isValidChannelUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fun.freechat.model.CharacterBackend;
import fun.freechat.model.ChatContext;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.ChatContextService;
import fun.freechat.service.chat.ChatSession;
import fun.freechat.service.chat.ChatSessionService;
import fun.freechat.service.chat.TgChatService;
import fun.freechat.service.chat.TgUserService;
import fun.freechat.service.chat.impl.TgChatBindingServiceImpl;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockMakers;

class TgChatBindingServiceTest {
    private final TgUserService users = fake(TgUserService.class);
    private final TgChatService chats = fake(TgChatService.class);
    private final ChatContextService contexts = fake(ChatContextService.class);
    private final ChatSessionService sessions = fake(ChatSessionService.class);
    private final CharacterService characters = fake(CharacterService.class);
    private final TgChatBindingServiceImpl bindings =
            new TgChatBindingServiceImpl(users, chats, contexts, sessions, characters);

    @ParameterizedTest
    @CsvSource({"12345,tg-12345,private", "-1001234567890,tg--1001234567890,supergroup"})
    void creationAndLookupShareThePersistedChatIdentity(long chatId, String expectedUserId, String chatType) {
        when(characters.getBackend("backend")).thenReturn(new CharacterBackend().withBackendId("backend"));
        when(contexts.create(any(ChatContext.class)))
                .thenAnswer(call -> call.getArgument(0, ChatContext.class).withChatId("chat"));
        when(sessions.get(any(ChatContext.class))).thenReturn(fake(ChatSession.class));

        assertEquals(
                "chat",
                bindings.getOrCreate("backend", chatId, chatType, "title", 987654321L, "sender", "First", "Last"));

        ArgumentCaptor<ChatContext> created = ArgumentCaptor.forClass(ChatContext.class);
        verify(contexts).create(created.capture());
        ChatContext context = created.getValue();
        assertEquals(expectedUserId, context.getUserId());
        assertEquals(chatId, context.getTgChatId());
        assertEquals(987654321L, context.getTgUserId());
        assertEquals(chatType, context.getChatType());
        assertTrue(isValidChannelUser(context));
        verify(sessions).get(context);
        verify(users).getOrCreate("backend", 987654321L, "sender", "First", "Last");
        verify(chats).getOrCreate("backend", chatId, chatType, "title");

        when(contexts.getChatIdByBackend(expectedUserId, "backend")).thenReturn("chat");
        assertEquals("chat", bindings.findChatId("backend", chatId));
        verify(contexts, times(2)).getChatIdByBackend(expectedUserId, "backend");
    }

    @ParameterizedTest
    @CsvSource({"12345,tg-12345", "-1001234567890,tg--1001234567890"})
    void existingBindingsRemainDiscoverable(long chatId, String expectedUserId) {
        when(contexts.getChatIdByBackend(expectedUserId, "backend")).thenReturn("existing-chat");

        assertEquals("existing-chat", bindings.getOrCreate("backend", chatId, "private", null, null, null, null, null));
        assertEquals("existing-chat", bindings.findChatId("backend", chatId));

        verify(contexts, times(2)).getChatIdByBackend(expectedUserId, "backend");
        verify(contexts, never()).create(any(ChatContext.class));
        verifyNoInteractions(characters, sessions, users);
    }

    private static <T> T fake(Class<T> type) {
        return mock(type, withSettings().mockMaker(MockMakers.SUBCLASS));
    }
}
