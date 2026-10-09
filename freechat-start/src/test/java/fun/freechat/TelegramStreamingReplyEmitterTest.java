package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.MockMakers.PROXY;
import static org.mockito.Mockito.*;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.TokenStream;
import fun.freechat.channels.spi.ChannelAddress;
import fun.freechat.channels.spi.ChannelEnvelope;
import fun.freechat.channels.spi.ChannelFailure;
import fun.freechat.channels.spi.ChannelMedia;
import fun.freechat.channels.spi.ChannelOutbound;
import fun.freechat.channels.spi.ChannelReceipt;
import fun.freechat.channels.spi.ChannelText;
import fun.freechat.channels.spi.ChannelTurnContext;
import fun.freechat.channels.telegram.command.HelpCommand;
import fun.freechat.channels.telegram.command.ResetCommand;
import fun.freechat.channels.telegram.command.StartCommand;
import fun.freechat.channels.telegram.handler.ChatBindingTelegramMessageHandler;
import fun.freechat.channels.telegram.handler.TelegramMessageHandler;
import fun.freechat.channels.telegram.handler.TelegramStreamingReplyEmitter;
import fun.freechat.channels.telegram.handler.TelegramUpdateDispatcher;
import fun.freechat.service.chat.ChatService;
import fun.freechat.service.chat.ChatSession;
import fun.freechat.service.chat.ChatSessionService;
import fun.freechat.service.chat.ChatStreamHandle;
import fun.freechat.service.chat.TgChatBindingService;
import fun.freechat.service.chat.TgMessageService;
import fun.freechat.service.enums.ChatVar;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

class TelegramStreamingReplyEmitterTest {
    @Test
    void coalescesTokensAndNeverOvertakesAnInFlightPartial() {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = started(turn);
        for (int i = 0; i < 1000; i++) {
            emitter.append("a");
        }
        assertEquals(1, turn.timers.size());
        assertEquals(Duration.ofMillis(500), turn.timers.getFirst().delay());
        turn.fireTimer();
        assertEquals(2, turn.out.calls.size());
        assertEquals(ChannelText.Format.PLAIN, turn.out.last().text.format());
        emitter.append("b");
        CompletableFuture<TelegramStreamingReplyEmitter.Reply> completed = emitter.complete();
        assertFalse(completed.isDone());
        assertEquals(2, turn.out.calls.size());
        turn.out.last().accept(null);
        assertEquals(3, turn.out.calls.size());
        assertEquals("a".repeat(1000) + "b", turn.out.last().text.text());
        assertEquals(ChannelText.Format.MARKDOWN, turn.out.last().text.format());
        turn.out.last().accept(null);
        assertEquals("1", completed.join().lastMessageId());
    }

    @Test
    void unchangedFinalTextStillAppliesMarkdownAndStopsTypingBeforeDelivery() {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = started(turn);
        emitter.append("same text");
        turn.fireTimer();
        turn.out.last().accept(null);
        CompletableFuture<TelegramStreamingReplyEmitter.Reply> completed = emitter.complete();
        assertEquals(1, turn.typingClosed);
        assertEquals(Duration.ofSeconds(4), turn.typingInterval);
        assertEquals("same text", turn.out.last().text.text());
        assertEquals(ChannelText.Format.MARKDOWN, turn.out.last().text.format());
        assertFalse(completed.isDone()); // runtime may be waiting for retry_after on this very future
        assertSame(completed, emitter.complete());
        turn.out.last().accept(null);
        assertEquals("same text", completed.join().text());
        assertEquals(1, turn.typingClosed);
        assertEquals(3, turn.out.calls.size());
    }

    @Test
    void failedSplitPlaceholderNeverAdvancesIntoOrOverwritesPreviousMessage() {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = started(turn);
        emitter.append("a".repeat(4000) + "tail");
        turn.fireTimer();
        assertEquals("1", turn.out.last().messageId);
        assertEquals("a".repeat(4000), turn.out.last().text.text());
        turn.out.last().accept(null);
        assertEquals("send", turn.out.last().kind);
        CompletableFuture<TelegramStreamingReplyEmitter.Reply> result = emitter.complete();
        turn.out.last().reject(ChannelFailure.Kind.AMBIGUOUS);
        assertThrows(CompletionException.class, result::join);
        emitter.append("late");
        assertSame(result, emitter.complete());
        assertEquals(3, turn.out.calls.size());
        assertEquals("1", emitter.confirmedReply().lastMessageId());
        assertEquals("a".repeat(4000), emitter.confirmedReply().text());
        assertEquals(1, turn.typingClosed);
    }

    @Test
    void splitProgressesOnlyAfterReplacementConfirmationAndRecordsFullTextWithLastId() {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = started(turn);
        String text = "a".repeat(4000) + "tail";
        emitter.append(text);
        CompletableFuture<TelegramStreamingReplyEmitter.Reply> result = emitter.complete();
        turn.out.last().accept(null); // freeze first segment
        assertEquals("send", turn.out.last().kind);
        assertEquals(3, turn.out.calls.size());
        turn.out.last().accept("2");
        assertEquals("2", turn.out.last().messageId);
        assertEquals("tail", turn.out.last().text.text());
        turn.out.last().accept(null);
        assertEquals(text, result.join().text());
        assertEquals("2", result.join().lastMessageId());
    }

    @Test
    void imagesAreExtractedAfterFinalTextAndDuplicateCompletionCannotSendThemTwice() {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = started(turn);
        emitter.append("hello ![img](https://example.invalid/a) and ![img](https://example.invalid/b)");
        var result = emitter.complete();
        assertSame(result, emitter.complete());
        assertEquals("hello  and ", turn.out.last().text.text());
        turn.out.last().accept(null);
        assertEquals(ChannelMedia.Kind.IMAGE, turn.out.last().media.kind());
        turn.out.last().accept("2");
        turn.out.last().accept("3");
        assertEquals(
                List.of(
                        new TelegramStreamingReplyEmitter.ImageReceipt("2", "https://example.invalid/a"),
                        new TelegramStreamingReplyEmitter.ImageReceipt("3", "https://example.invalid/b")),
                result.join().images());
        assertSame(result, emitter.complete());
        assertEquals(4, turn.out.calls.size());
    }

    @Test
    void laterImageFailureRetainsEarlierConfirmedDeliveryForRecording() {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = started(turn);
        emitter.append("hello ![](https://example.invalid/a)![](https://example.invalid/b)");
        var result = emitter.complete();
        turn.out.last().accept(null);
        turn.out.last().accept("2");
        turn.out.last().reject(ChannelFailure.Kind.AMBIGUOUS);
        assertThrows(CompletionException.class, result::join);
        assertEquals("hello ", emitter.confirmedReply().text());
        assertEquals("1", emitter.confirmedReply().lastMessageId());
        assertEquals(1, emitter.confirmedReply().images().size());
        assertSame(result, emitter.complete());
        assertEquals(4, turn.out.calls.size());
    }

    @Test
    void oneFailedImageDoesNotPreventLaterImagesFromBeingDelivered() {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = started(turn);
        emitter.append("hello ![](https://example.invalid/a)![](https://example.invalid/b)");
        var result = emitter.complete();
        turn.out.last().accept(null);
        turn.out.last().reject(ChannelFailure.Kind.AMBIGUOUS);
        assertFalse(result.isDone());
        turn.out.last().accept("3");
        assertThrows(CompletionException.class, result::join);
        assertEquals(
                List.of(new TelegramStreamingReplyEmitter.ImageReceipt("3", "https://example.invalid/b")),
                emitter.confirmedReply().images());
        assertEquals(4, turn.out.calls.size());
    }

    @Test
    void finalTextFailureDoesNotSuppressGeneratedImages() {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = started(turn);
        emitter.append("text ![](https://example.invalid/image)");
        var result = emitter.complete();
        turn.out.last().reject(ChannelFailure.Kind.REJECTED);
        assertEquals(ChannelMedia.Kind.IMAGE, turn.out.last().media.kind());
        assertFalse(result.isDone());
        turn.out.last().accept("2");
        assertThrows(CompletionException.class, result::join);
        assertEquals(
                List.of(new TelegramStreamingReplyEmitter.ImageReceipt("2", "https://example.invalid/image")),
                emitter.confirmedReply().images());
    }

    @Test
    void failedPartialEditCanRecoverAtFinalDelivery() {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = started(turn);
        emitter.append("partial");
        turn.fireTimer();
        turn.out.last().reject(ChannelFailure.Kind.RETRYABLE);
        assertFalse(emitter.completion().isDone());
        emitter.append(" final");
        var result = emitter.complete();
        assertEquals("partial final", turn.out.last().text.text());
        turn.out.last().accept(null);
        assertEquals("partial final", result.join().text());
    }

    @Test
    void finalFailureIsVisibleAndNeverLocallyRetried() {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = started(turn);
        emitter.append("final");
        var result = emitter.complete();
        turn.out.last().reject(ChannelFailure.Kind.RATE_LIMITED);
        assertEquals(
                ChannelFailure.Kind.RATE_LIMITED,
                ((ChannelFailure) assertThrows(CompletionException.class, result::join)
                                .getCause())
                        .kind());
        assertNull(emitter.confirmedReply().lastMessageId());
        assertEquals(2, turn.out.calls.size());
        assertEquals(1, turn.typingClosed);
    }

    @Test
    void failedFinalRetainsOnlyConfirmedPartialTextAndSanitizesSuppressedFailures() {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = started(turn);
        emitter.append("confirmed");
        turn.fireTimer();
        turn.out.last().accept(null);
        emitter.append(" not delivered");
        var result = emitter.complete();
        ChannelFailure failure = new ChannelFailure(ChannelFailure.Kind.RETRYABLE);
        failure.addSuppressed(new IllegalStateException("private provider body"));
        turn.out.last().result.completeExceptionally(failure);
        Throwable safe = assertThrows(CompletionException.class, result::join).getCause();
        assertNull(safe.getCause());
        assertEquals(0, safe.getSuppressed().length);
        assertEquals("confirmed", emitter.confirmedReply().text());
        assertEquals("1", emitter.confirmedReply().lastMessageId());
    }

    @Test
    void emptyReplyStopsTypingWithoutRecordingAPlaceholder() {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = started(turn);
        var result = emitter.complete().join();
        assertEquals("", result.text());
        assertNull(result.lastMessageId());
        assertEquals(1, turn.out.calls.size());
        assertEquals(1, turn.typingClosed);
    }

    @Test
    void cancellationDisarmsStaleTimersAndWaitsForAlreadySubmittedDelivery() {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = started(turn);
        emitter.append("partial");
        FakeTimer timer = turn.timers.getFirst();
        turn.fireTimer();
        turn.cancel();
        assertEquals(1, turn.typingClosed);
        assertFalse(emitter.completion().isDone());
        timer.action().run();
        assertEquals(2, turn.out.calls.size());
        turn.out.last().accept(null);
        assertThrows(CompletionException.class, () -> emitter.completion().join());
        assertSame(emitter.completion(), emitter.complete());
        assertEquals(2, turn.out.calls.size());
    }

    @Test
    void placeholderFailureAndCancellationBeforeStartShareTerminalFuture() {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = new TelegramStreamingReplyEmitter(turn);
        var start = emitter.start();
        turn.out.last().reject(ChannelFailure.Kind.REJECTED);
        assertTrue(start.isCompletedExceptionally());
        assertTrue(emitter.complete().isCompletedExceptionally());
        assertEquals(1, turn.typingClosed);
        FakeTurn cancelled = new FakeTurn();
        cancelled.cancel();
        TelegramStreamingReplyEmitter notStarted = new TelegramStreamingReplyEmitter(cancelled);
        assertTrue(notStarted.start().isCompletedExceptionally());
        assertTrue(cancelled.out.calls.isEmpty());
    }

    @Test
    void textAndImageBuffersAreBounded() {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = started(turn);
        emitter.append("a".repeat(128_001));
        assertTrue(emitter.completion().isCompletedExceptionally());
        assertEquals(1, turn.out.calls.size());
        FakeTurn pictures = new FakeTurn();
        TelegramStreamingReplyEmitter withImages = started(pictures);
        withImages.append("![](https://example.invalid/image)".repeat(33));
        assertTrue(withImages.complete().isCompletedExceptionally());
        assertEquals(1, pictures.out.calls.size());
        assertEquals(1, pictures.typingClosed);
    }

    @Test
    void incompleteImageAcrossSplitBoundaryIsNotFrozenIntoText() {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = started(turn);
        emitter.append("a".repeat(3999) + "![img]");
        turn.fireTimer();
        assertEquals("a".repeat(3999), turn.out.last().text.text());
        emitter.append("(https://example.invalid/image)tail");
        turn.out.last().accept(null);
        var result = emitter.complete();
        assertEquals(4000, turn.out.last().text.text().length());
        turn.out.last().accept(null);
        turn.out.last().accept("2");
        assertEquals("ail", turn.out.last().text.text());
        turn.out.last().accept(null);
        turn.out.last().accept("3");
        assertEquals("a".repeat(3999) + "tail", result.join().text());
        assertEquals(1, result.join().images().size());
    }

    @Test
    void markdownBalancingAlsoRespectsMessageLimit() {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = started(turn);
        emitter.append("[".repeat(4000));
        var result = emitter.complete();
        turn.out.last().accept(null);
        turn.out.last().accept("2");
        assertEquals(4000, turn.out.last().text.text().length());
        assertTrue(turn.out.last().text.text().endsWith("]"));
        turn.out.last().accept(null);
        assertEquals(4000, result.join().text().length());
    }

    @Test
    void handlerWaitsForBothSettlementAndDeliveryAndRecordsOnce() throws Exception {
        HandlerFixture f = new HandlerFixture();
        var result = f.handler.handle("backend", f.update, f.turn).toCompletableFuture();
        f.turn.out.last().accept("1");
        f.ready.complete(f.stream);
        f.partial.get().accept("reply ![](https://example.invalid/image)");
        f.completed.get().accept(null);
        f.completed.get().accept(null);
        f.error.get().accept(new IllegalStateException("private provider error"));
        f.settled.complete(null);
        assertFalse(result.isDone());
        f.turn.out.last().accept(null);
        assertFalse(result.isDone());
        f.turn.out.last().accept("2");
        result.get(2, TimeUnit.SECONDS);
        verify(f.messages).record("chat", 1L, null, "out", "text", "reply ", null);
        verify(f.messages).record("chat", 2L, null, "out", "photo", "https://example.invalid/image", null);
        verify(f.chat).streamSendManaged(eq("chat"), any(UserMessage.class), isNull());
        verifyNoMoreInteractions(f.chat);
    }

    @Test
    void deliveryFailureCannotReleaseLaneBeforeServiceSettlementOrRetryAi() throws Exception {
        HandlerFixture f = new HandlerFixture();
        var result = f.handler.handle("backend", f.update, f.turn).toCompletableFuture();
        f.turn.out.last().accept("1");
        f.ready.complete(f.stream);
        f.partial.get().accept("reply");
        f.completed.get().accept(null);
        f.turn.out.last().reject(ChannelFailure.Kind.AMBIGUOUS);
        assertFalse(result.isDone());
        assertTrue(f.cancellations > 0);
        f.settled.complete(null);
        assertThrows(Exception.class, () -> result.get(2, TimeUnit.SECONDS));
        verify(f.messages, never())
                .record(eq("chat"), anyLong(), isNull(), eq("out"), anyString(), anyString(), isNull());
        verify(f.chat, times(1)).streamSendManaged(eq("chat"), any(), isNull());
    }

    @Test
    void handlerCancellationBeforeReadyDoesNotStartStreamAndAwaitsCleanup() throws Exception {
        HandlerFixture f = new HandlerFixture();
        var result = f.handler.handle("backend", f.update, f.turn).toCompletableFuture();
        f.turn.out.last().accept("1");
        f.turn.cancel();
        assertTrue(f.cancellations > 0);
        f.ready.complete(f.stream);
        verify(f.stream, never()).start();
        assertFalse(result.isDone());
        f.settled.complete(null);
        assertThrows(Exception.class, () -> result.get(2, TimeUnit.SECONDS));
        assertEquals(1, f.turn.typingClosed);
    }

    @Test
    void nullReadyAndSetupFailureFinishWithoutLosingSettlement() throws Exception {
        HandlerFixture empty = new HandlerFixture();
        var result = empty.handler.handle("backend", empty.update, empty.turn).toCompletableFuture();
        empty.turn.out.last().accept("1");
        empty.ready.complete(null);
        assertFalse(result.isDone());
        empty.settled.complete(null);
        result.get(2, TimeUnit.SECONDS);
        HandlerFixture broken = new HandlerFixture();
        var failed =
                broken.handler.handle("backend", broken.update, broken.turn).toCompletableFuture();
        broken.turn.out.last().accept("1");
        doThrow(new IllegalStateException("private body")).when(broken.stream).start();
        broken.ready.complete(broken.stream);
        assertTrue(broken.cancellations > 0);
        assertFalse(failed.isDone());
        broken.settled.complete(null);
        assertThrows(Exception.class, () -> failed.get(2, TimeUnit.SECONDS));
    }

    @Test
    void dispatcherParsesBotSuffixAndForwardsUnknownCommandsWithoutTransport() {
        FakeTurn turn = new FakeTurn();
        TelegramMessageHandler messages =
                mock(TelegramMessageHandler.class, withSettings().mockMaker(PROXY));
        TelegramUpdateDispatcher dispatcher = new TelegramUpdateDispatcher(List.of(new HelpCommand()), messages);
        Message message = inbound();
        when(message.isCommand()).thenReturn(true);
        when(message.getText()).thenReturn("/help@test_bot arguments");
        Update update = new Update();
        update.setMessage(message);
        var result = dispatcher
                .handle(new ChannelEnvelope<>(turn.address(), "1", update), turn)
                .toCompletableFuture();
        assertTrue(turn.out.last().text.text().contains("/reset"));
        assertFalse(result.isDone());
        turn.out.last().accept("1");
        result.join();
        verifyNoInteractions(messages);
        when(message.getText()).thenReturn("/unknown@test_bot");
        CompletableFuture<Void> forwarded = new CompletableFuture<>();
        when(messages.handle("backend", update, turn)).thenReturn(forwarded);
        assertSame(forwarded, dispatcher.handle(new ChannelEnvelope<>(turn.address(), "2", update), turn));
    }

    @Test
    void startFallbackAndResetOnlyExistingBindingUseScheduledDelivery() {
        FakeTurn turn = new FakeTurn();
        TgChatBindingService bindings =
                mock(TgChatBindingService.class, withSettings().mockMaker(PROXY));
        ChatService chat = mock(ChatService.class, withSettings().mockMaker(PROXY));
        ChatSessionService sessions =
                mock(ChatSessionService.class, withSettings().mockMaker(PROXY));
        Message message = inbound();
        Update update = new Update();
        update.setMessage(message);
        when(bindings.getOrCreate("backend", -123L, "group", null, null, null, null, null))
                .thenReturn("chat");
        var greeting = new StartCommand(bindings, sessions)
                .execute("backend", update, turn)
                .toCompletableFuture();
        assertEquals(
                "Hi! I'm ready when you are — just send a message.",
                turn.out.last().text.text());
        assertFalse(greeting.isDone());
        turn.out.last().accept("1");
        greeting.join();
        clearInvocations(bindings);
        var reset = new ResetCommand(bindings, chat)
                .execute("backend", update, turn)
                .toCompletableFuture();
        assertTrue(turn.out.last().text.text().contains("no active conversation"));
        turn.out.last().accept("2");
        reset.join();
        verify(bindings).findChatId("backend", -123L);
        verifyNoMoreInteractions(bindings);
        verifyNoInteractions(chat);
        when(bindings.findChatId("backend", -123L)).thenReturn("chat");
        new ResetCommand(bindings, chat).execute("backend", update, turn);
        verify(chat).clearMemory("chat");
        assertEquals(
                "Memory cleared. Say something to start fresh.",
                turn.out.last().text.text());
    }

    @Test
    void configuredStartGreetingIsPreserved() {
        FakeTurn turn = new FakeTurn();
        TgChatBindingService bindings =
                mock(TgChatBindingService.class, withSettings().mockMaker(PROXY));
        ChatSessionService sessions =
                mock(ChatSessionService.class, withSettings().mockMaker(PROXY));
        ChatSession session = mock(ChatSession.class);
        when(session.getVariables()).thenReturn(Map.of(ChatVar.CHARACTER_GREETING.text(), "*Hello character*"));
        when(sessions.get("chat")).thenReturn(session);
        when(bindings.getOrCreate("backend", -123L, "group", null, null, null, null, null))
                .thenReturn("chat");
        Update update = new Update();
        update.setMessage(inbound());
        new StartCommand(bindings, sessions).execute("backend", update, turn);
        assertEquals("*Hello character*", turn.out.last().text.text());
        assertEquals(ChannelText.Format.MARKDOWN, turn.out.last().text.format());
    }

    @ParameterizedTest
    @ValueSource(strings = {"photo", "voice", "video", "audio", "document", "unsupported"})
    void nativeNonTextKindsAreRecordedWithoutCallingAi(String kind) {
        HandlerFixture f = new HandlerFixture();
        Message message = f.update.getMessage();
        when(message.hasText()).thenReturn(false);
        when(message.hasPhoto()).thenReturn(kind.equals("photo"));
        when(message.hasVoice()).thenReturn(kind.equals("voice"));
        when(message.hasVideo()).thenReturn(kind.equals("video"));
        when(message.hasAudio()).thenReturn(kind.equals("audio"));
        when(message.hasDocument()).thenReturn(kind.equals("document"));
        f.handler.handle("backend", f.update, f.turn).toCompletableFuture().join();
        verify(f.messages).record("chat", 42L, null, "in", kind, null, null);
        verifyNoInteractions(f.chat);
        assertTrue(f.turn.out.calls.isEmpty());
    }

    @Test
    void recordingFailureNeverResendsTextOrPhotosAndDoesNotSkipOtherConfirmedRecords() throws Exception {
        HandlerFixture f = new HandlerFixture();
        doThrow(new IllegalStateException("private persistence body"))
                .when(f.messages)
                .record("chat", 1L, null, "out", "text", "reply ", null);
        var result = f.handler.handle("backend", f.update, f.turn).toCompletableFuture();
        f.turn.out.last().accept("1");
        f.ready.complete(f.stream);
        f.partial.get().accept("reply ![](https://example.invalid/image)");
        f.completed.get().accept(null);
        f.turn.out.last().accept(null);
        f.turn.out.last().accept("2");
        assertFalse(result.isDone());
        f.settled.complete(null);
        assertThrows(Exception.class, () -> result.get(2, TimeUnit.SECONDS));
        verify(f.messages).record("chat", 1L, null, "out", "text", "reply ", null);
        verify(f.messages).record("chat", 2L, null, "out", "photo", "https://example.invalid/image", null);
        assertEquals(3, f.turn.out.calls.size());
    }

    private static TelegramStreamingReplyEmitter started(FakeTurn turn) {
        TelegramStreamingReplyEmitter emitter = new TelegramStreamingReplyEmitter(turn);
        var started = emitter.start();
        assertFalse(started.isDone());
        turn.out.last().accept("1");
        started.join();
        return emitter;
    }

    private static Message inbound() {
        Chat chat = Chat.builder().id(-123L).type("group").build();
        Message message = mock(Message.class);
        when(message.getChat()).thenReturn(chat);
        when(message.getMessageId()).thenReturn(42);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("hello");
        return message;
    }

    private static final class HandlerFixture {
        final FakeTurn turn = new FakeTurn();
        final TgChatBindingService bindings =
                mock(TgChatBindingService.class, withSettings().mockMaker(PROXY));
        final TgMessageService messages =
                mock(TgMessageService.class, withSettings().mockMaker(PROXY));
        final ChatService chat = mock(ChatService.class, withSettings().mockMaker(PROXY));
        final TokenStream stream =
                mock(TokenStream.class, withSettings().mockMaker(PROXY).defaultAnswer(RETURNS_SELF));
        final CompletableFuture<TokenStream> ready = new CompletableFuture<>();
        final CompletableFuture<Void> settled = new CompletableFuture<>();
        final AtomicReference<Consumer<String>> partial = new AtomicReference<>();
        final AtomicReference<Consumer<ChatResponse>> completed = new AtomicReference<>();
        final AtomicReference<Consumer<Throwable>> error = new AtomicReference<>();
        final Update update = new Update();
        final ChatBindingTelegramMessageHandler handler =
                new ChatBindingTelegramMessageHandler(bindings, messages, chat);
        int cancellations;

        HandlerFixture() {
            update.setMessage(inbound());
            when(bindings.getOrCreate("backend", -123L, "group", null, null, null, null, null))
                    .thenReturn("chat");
            when(chat.streamSendManaged(eq("chat"), any(UserMessage.class), isNull()))
                    .thenReturn(new ChatStreamHandle() {
                        public CompletableFuture<TokenStream> ready() {
                            return ready;
                        }

                        public CompletableFuture<Void> settled() {
                            return settled;
                        }

                        public void cancel() {
                            cancellations++;
                        }
                    });
            when(stream.onPartialResponse(any())).thenAnswer(call -> {
                partial.set(call.getArgument(0));
                return stream;
            });
            when(stream.onCompleteResponse(any())).thenAnswer(call -> {
                completed.set(call.getArgument(0));
                return stream;
            });
            when(stream.onError(any())).thenAnswer(call -> {
                error.set(call.getArgument(0));
                return stream;
            });
        }
    }

    private record FakeTimer(Runnable action, Duration delay, ScheduledFuture<?> future) {}

    private static final class FakeTurn implements ChannelTurnContext {
        final FakeOutbound out = new FakeOutbound();
        final List<Runnable> cancellations = new ArrayList<>();
        final List<FakeTimer> timers = new ArrayList<>();
        boolean cancelled;
        int typingClosed;
        Duration typingInterval;

        public ChannelAddress address() {
            return new ChannelAddress("telegram", "backend", "-123");
        }

        public ChannelOutbound outbound() {
            return out;
        }

        public Instant deadline() {
            return Instant.MAX;
        }

        public boolean isCancelled() {
            return cancelled;
        }

        public void onCancel(Runnable action) {
            if (cancelled) action.run();
            else cancellations.add(action);
        }

        public ScheduledFuture<?> schedule(Runnable action, Duration delay) {
            ScheduledFuture<?> future =
                    mock(ScheduledFuture.class, withSettings().mockMaker(PROXY));
            timers.add(new FakeTimer(action, delay, future));
            return future;
        }

        public AutoCloseable typing(Duration interval) {
            typingInterval = interval;
            return () -> typingClosed++;
        }

        void fireTimer() {
            timers.removeFirst().action().run();
        }

        void cancel() {
            cancelled = true;
            List.copyOf(cancellations).forEach(Runnable::run);
            cancellations.clear();
        }
    }

    private static final class Pending {
        final String kind;
        final String messageId;
        final ChannelText text;
        final ChannelMedia media;
        final CompletableFuture<ChannelReceipt> result = new CompletableFuture<>();

        Pending(String kind, String messageId, ChannelText text, ChannelMedia media) {
            this.kind = kind;
            this.messageId = messageId;
            this.text = text;
            this.media = media;
        }

        void accept(String id) {
            result.complete(id == null ? null : new ChannelReceipt(id));
        }

        void reject(ChannelFailure.Kind kind) {
            result.completeExceptionally(new ChannelFailure(kind));
        }
    }

    private static final class FakeOutbound implements ChannelOutbound {
        final List<Pending> calls = new ArrayList<>();

        public boolean supports(Class<?> capability) {
            return true;
        }

        public CompletableFuture<ChannelReceipt> sendText(ChannelText text) {
            Pending pending = new Pending("send", null, text, null);
            calls.add(pending);
            return pending.result;
        }

        public CompletableFuture<ChannelReceipt> sendMedia(ChannelMedia media) {
            Pending pending = new Pending("media", null, null, media);
            calls.add(pending);
            return pending.result;
        }

        public CompletableFuture<Void> editText(String messageId, ChannelText text) {
            Pending pending = new Pending("edit", messageId, text, null);
            calls.add(pending);
            return pending.result.thenApply(ignored -> null);
        }

        public CompletableFuture<Void> sendTyping(BooleanSupplier stillNeeded) {
            return CompletableFuture.completedFuture(null);
        }

        Pending last() {
            return calls.getLast();
        }
    }
}
