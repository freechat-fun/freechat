package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.MockMakers.PROXY;
import static org.mockito.Mockito.*;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
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
import fun.freechat.service.chat.ChatRunEvent;
import fun.freechat.service.chat.ChatRunHandle;
import fun.freechat.service.chat.ChatService;
import fun.freechat.service.chat.ChatSession;
import fun.freechat.service.chat.ChatSessionService;
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
        assertEquals(1, turn.out.calls.size());
        assertEquals(ChannelText.Format.PLAIN, turn.out.last().text.format());
        emitter.append("b");
        CompletableFuture<TelegramStreamingReplyEmitter.Reply> completed = emitter.complete();
        assertFalse(completed.isDone());
        assertEquals(1, turn.out.calls.size());
        turn.out.last().accept(null);
        assertEquals(2, turn.out.calls.size());
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
        assertEquals(2, turn.out.calls.size());
    }

    @Test
    void failedNewSegmentNeverOverwritesPreviousMessageOrRetriesAnAmbiguousSend() {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = started(turn);
        emitter.append("a".repeat(4000) + "tail");
        turn.fireTimer();
        assertNull(turn.out.last().messageId);
        assertEquals("send", turn.out.last().kind);
        assertEquals("a".repeat(4000), turn.out.last().text.text());
        turn.out.last().accept(null);
        assertEquals("send", turn.out.last().kind);
        CompletableFuture<TelegramStreamingReplyEmitter.Reply> result = emitter.complete();
        turn.out.last().reject(ChannelFailure.Kind.AMBIGUOUS);
        assertThrows(CompletionException.class, result::join);
        emitter.append("late");
        assertSame(result, emitter.complete());
        assertEquals(2, turn.out.calls.size());
        assertEquals("1", emitter.confirmedReply().lastMessageId());
        assertEquals("a".repeat(4000), emitter.confirmedReply().text());
        assertEquals(1, turn.typingClosed);
    }

    @Test
    void splitSendsRealContentAndRecordsFullTextOnlyAfterConfirmation() {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = started(turn);
        String text = "a".repeat(4000) + "tail";
        emitter.append(text);
        CompletableFuture<TelegramStreamingReplyEmitter.Reply> result = emitter.complete();
        turn.out.last().accept("1"); // confirm first segment before sending the next real segment
        assertEquals("send", turn.out.last().kind);
        assertEquals(2, turn.out.calls.size());
        assertNull(turn.out.last().messageId);
        assertEquals("tail", turn.out.last().text.text());
        assertFalse(result.isDone());
        turn.out.last().accept("2");
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
        assertEquals(3, turn.out.calls.size());
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
        assertEquals(3, turn.out.calls.size());
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
        assertEquals(3, turn.out.calls.size());
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
        emitter.append("part");
        turn.fireTimer();
        turn.out.last().accept("1");
        emitter.append("ial");
        turn.fireTimer();
        assertEquals("edit", turn.out.last().kind);
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
        assertEquals(1, turn.out.calls.size());
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
        assertEquals(0, turn.out.calls.size());
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
        assertEquals(1, turn.out.calls.size());
        turn.out.last().accept(null);
        assertThrows(CompletionException.class, () -> emitter.completion().join());
        assertSame(emitter.completion(), emitter.complete());
        assertEquals(1, turn.out.calls.size());
    }

    @Test
    void startIsImmediatelyReadyWithoutMessagesOrTypingAndPreCancelledStartFails() {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = new TelegramStreamingReplyEmitter(turn);
        var start = emitter.start();
        assertTrue(start.isDone());
        assertFalse(start.isCompletedExceptionally());
        assertTrue(turn.out.calls.isEmpty());
        assertNull(turn.typingInterval);
        assertSame(start, emitter.start());
        assertTrue(emitter.complete().isDone());
        assertEquals(0, turn.typingClosed);
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
        assertEquals(0, turn.out.calls.size());
        FakeTurn pictures = new FakeTurn();
        TelegramStreamingReplyEmitter withImages = started(pictures);
        withImages.append("![](https://example.invalid/image)".repeat(33));
        assertTrue(withImages.complete().isCompletedExceptionally());
        assertEquals(0, pictures.out.calls.size());
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
        assertEquals("ail", turn.out.last().text.text());
        assertEquals("send", turn.out.last().kind);
        turn.out.last().accept("2");
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
        turn.out.last().accept("1");
        assertEquals(4000, turn.out.last().text.text().length());
        assertTrue(turn.out.last().text.text().endsWith("]"));
        turn.out.last().accept("2");
        assertEquals(4000, result.join().text().length());
    }

    @Test
    void serviceStatusAloneControlsTypingAndPrivateEventsNeverSendMessages() {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = started(turn);
        emitter.onRunEvent(event("run.started", Map.of("phase", "preparing")));
        for (String phase : List.of("waiting_model", "thinking", "tool_execution", "responding")) {
            emitter.onRunEvent(event("run.status", Map.of("phase", phase)));
        }
        emitter.onRunEvent(event("reasoning.delta", Map.of("delta", "private reasoning")));
        emitter.onRunEvent(event("tool.started", Map.of("tool", "private tool", "tool_call_id", "id")));
        emitter.onRunEvent(event("tool.completed", Map.of("tool", "private tool", "tool_call_id", "id")));
        assertTrue(turn.out.calls.isEmpty());
        assertEquals(1, turn.typingStarted);
        assertEquals(Duration.ofSeconds(4), turn.typingInterval);
        emitter.onRunEvent(event("message.delta", Map.of("delta", "body")));
        assertEquals(0, turn.typingClosed);
        emitter.onRunEvent(event("run.status", Map.of("phase", "finalizing")));
        assertEquals(1, turn.typingClosed);
        assertTrue(turn.out.calls.isEmpty());
        emitter.onRunEvent(event("run.completed", Map.of()));
        assertEquals("body", turn.out.last().text.text());
        turn.out.last().accept("1");
        assertEquals("body", emitter.completion().join().text());
        assertEquals(1, turn.typingClosed);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " \n\t", "  "})
    void whitespaceAndImageRemovalNeverCreateEmptyTelegramMessages(String whitespace) {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = started(turn);
        emitter.append(whitespace + "![](https://example.invalid/image)" + whitespace);
        assertTrue(turn.timers.isEmpty());
        assertTrue(turn.out.calls.isEmpty());
        var result = emitter.complete();
        assertEquals("media", turn.out.last().kind);
        turn.out.last().accept("2");
        assertNull(result.join().lastMessageId());
        assertEquals("", result.join().text());
        assertEquals(1, turn.out.calls.size());
    }

    @Test
    void failedRunWaitsForInFlightBodyAndSafeNoticeAndNeverBecomesComplete() {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = started(turn);
        emitter.append("confirmed");
        turn.fireTimer();
        emitter.onRunEvent(event("run.failed", Map.of("message", "Safe failure", "partial", true)));
        emitter.onRunEvent(event("run.completed", Map.of()));
        assertFalse(emitter.completion().isDone());
        assertEquals(1, turn.typingClosed);
        turn.out.last().accept("1");
        assertEquals("send", turn.out.last().kind);
        assertEquals("Safe failure", turn.out.last().text.text());
        assertEquals("confirmed", emitter.confirmedReply().text());
        assertFalse(emitter.completion().isDone());
        turn.out.last().accept("2");
        assertTrue(emitter.completion().isCompletedExceptionally());
        assertEquals("1", emitter.confirmedReply().lastMessageId());
        assertSame(emitter.completion(), emitter.complete());
        assertEquals(2, turn.out.calls.size());
    }

    @Test
    void longWhitespacePrefixNeverCreatesABlankSplitMessage() {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = started(turn);
        emitter.append(" ".repeat(8000) + "body");
        var result = emitter.complete();
        assertEquals("body", turn.out.last().text.text().strip());
        turn.out.last().accept("1");
        assertEquals(turn.out.last().text.text(), result.join().text());
        assertEquals(1, turn.out.calls.size());
    }

    @Test
    void runCancellationStopsTypingAndNeverSendsBufferedOrLateBody() {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = started(turn);
        emitter.append("not yet sent");
        emitter.onRunEvent(event("run.status", Map.of("phase", "cancelling")));
        assertEquals(1, turn.typingClosed);
        emitter.onRunEvent(event("run.cancelled", Map.of("completed", false, "partial", false)));
        emitter.onRunEvent(event("run.started", Map.of("phase", "preparing")));
        emitter.onRunEvent(event("message.delta", Map.of("delta", "late")));
        var error = assertThrows(
                CompletionException.class, () -> emitter.completion().join());
        assertEquals(ChannelFailure.Kind.CANCELLED, ((ChannelFailure) error.getCause()).kind());
        assertTrue(turn.out.calls.isEmpty());
        assertEquals(1, turn.typingStarted);
    }

    @Test
    void missingFirstSendReceiptFailsWithoutInventingConfirmedContent() {
        FakeTurn turn = new FakeTurn();
        TelegramStreamingReplyEmitter emitter = started(turn);
        emitter.append("body");
        emitter.complete();
        turn.out.last().result.complete(null);
        assertTrue(emitter.completion().isCompletedExceptionally());
        assertNull(emitter.confirmedReply().lastMessageId());
    }

    @Test
    void handlerWaitsForBothSettlementAndDeliveryAndRecordsOnce() throws Exception {
        HandlerFixture f = new HandlerFixture();
        var result = f.handler.handle("backend", f.update, f.turn).toCompletableFuture();
        assertTrue(f.turn.out.calls.isEmpty());
        f.emit("message.delta", Map.of("delta", "reply ![](https://example.invalid/image)"));
        f.settled.complete(null);
        assertFalse(result.isDone(), "Settlement alone cannot replace the terminal event");
        f.emit("run.completed", Map.of());
        f.emit("run.completed", Map.of());
        f.emit("run.failed", Map.of("message", "late error"));
        assertFalse(result.isDone());
        f.turn.out.last().accept(null);
        assertFalse(result.isDone());
        f.turn.out.last().accept("2");
        result.get(2, TimeUnit.SECONDS);
        verify(f.messages).record("chat", 1L, null, "out", "text", "reply ", null);
        verify(f.messages).record("chat", 2L, null, "out", "photo", "https://example.invalid/image", null);
        verify(f.chat).startRun(eq("chat"), any(UserMessage.class), isNull(), any());
        verifyNoMoreInteractions(f.chat);
    }

    @Test
    void deliveryFailureCannotReleaseLaneBeforeServiceSettlementOrRetryAi() throws Exception {
        HandlerFixture f = new HandlerFixture();
        var result = f.handler.handle("backend", f.update, f.turn).toCompletableFuture();
        f.emit("message.delta", Map.of("delta", "reply"));
        f.turn.fireTimer();
        f.turn.out.last().reject(ChannelFailure.Kind.AMBIGUOUS);
        assertFalse(result.isDone());
        assertTrue(f.cancellations > 0);
        f.settled.complete(null);
        assertThrows(Exception.class, () -> result.get(2, TimeUnit.SECONDS));
        verify(f.messages, never())
                .record(eq("chat"), anyLong(), isNull(), eq("out"), anyString(), anyString(), isNull());
        verify(f.chat, times(1)).startRun(eq("chat"), any(), isNull(), any());
    }

    @Test
    void exceptionalSettlementStillWaitsForInFlightDeliveryAndConfirmedRecording() throws Exception {
        HandlerFixture f = new HandlerFixture();
        var result = f.handler.handle("backend", f.update, f.turn).toCompletableFuture();
        f.emit("message.delta", Map.of("delta", "confirmed after cleanup failure"));
        f.turn.fireTimer();
        f.settled.completeExceptionally(new IllegalStateException("private cleanup body"));
        assertFalse(result.isDone());
        f.turn.out.last().accept("1");
        var failure =
                assertThrows(java.util.concurrent.ExecutionException.class, () -> result.get(2, TimeUnit.SECONDS));
        assertInstanceOf(ChannelFailure.class, failure.getCause());
        assertNull(failure.getCause().getCause());
        verify(f.messages).record("chat", 1L, null, "out", "text", "confirmed after cleanup failure", null);
        assertEquals(1, f.turn.out.calls.size());
    }

    @Test
    void handlerCancellationBeforeHandleAssignmentIsForwardedAndAwaitsCleanup() throws Exception {
        HandlerFixture f = new HandlerFixture();
        f.duringStart = f.turn::cancel;
        var result = f.handler.handle("backend", f.update, f.turn).toCompletableFuture();
        assertEquals(1, f.cancellations);
        f.emit("message.delta", Map.of("delta", "late body"));
        assertTrue(f.turn.out.calls.isEmpty());
        assertFalse(result.isDone());
        f.settled.complete(null);
        assertThrows(Exception.class, () -> result.get(2, TimeUnit.SECONDS));
        assertEquals(1, f.turn.typingClosed);
    }

    @Test
    void emptyCompletionAndSetupFailureNeverSendPlaceholdersOrExposeProviderErrors() throws Exception {
        HandlerFixture empty = new HandlerFixture();
        var result = empty.handler.handle("backend", empty.update, empty.turn).toCompletableFuture();
        empty.settled.complete(null);
        empty.emit("run.completed", Map.of());
        result.get(2, TimeUnit.SECONDS);
        assertTrue(empty.turn.out.calls.isEmpty());
        HandlerFixture broken = new HandlerFixture();
        broken.duringStart = () -> {
            throw new IllegalStateException("private body");
        };
        var failed =
                broken.handler.handle("backend", broken.update, broken.turn).toCompletableFuture();
        assertFalse(failed.isDone());
        assertFalse(broken.turn.out.last().text.text().contains("private body"));
        broken.turn.out.last().accept("1");
        assertThrows(Exception.class, () -> failed.get(2, TimeUnit.SECONDS));
        verify(broken.messages, never())
                .record(eq("chat"), anyLong(), isNull(), eq("out"), anyString(), anyString(), isNull());
    }

    @Test
    void eventDeliveryFailureCannotLeaveTelegramWaitingAfterSettlement() throws Exception {
        HandlerFixture f = new HandlerFixture();
        var result = f.handler.handle("backend", f.update, f.turn).toCompletableFuture();
        f.settled.complete(null);
        assertFalse(result.isDone());
        f.eventDelivery.completeExceptionally(new IllegalStateException("private delivery failure"));
        assertThrows(java.util.concurrent.ExecutionException.class, () -> result.get(2, TimeUnit.SECONDS));
        assertEquals(1, f.turn.typingClosed);
        assertTrue(f.turn.out.calls.isEmpty());
        assertEquals(1, f.cancellations);
    }

    @Test
    void whitespaceDeltasDoNotDiscardTheFinalResponse() throws Exception {
        HandlerFixture f = new HandlerFixture();
        var result = f.handler.handle("backend", f.update, f.turn).toCompletableFuture();
        f.emit("message.delta", Map.of("delta", " \n\t"));
        assertTrue(f.turn.out.calls.isEmpty());
        f.settled.complete(null);
        f.emit(
                "run.completed",
                Map.of(
                        "response",
                        ChatResponse.builder()
                                .aiMessage(AiMessage.from("answer"))
                                .build()));
        assertEquals("answer", f.turn.out.last().text.text());
        f.turn.out.last().accept("1");
        result.get(2, TimeUnit.SECONDS);
    }

    @Test
    void finalOnlyRunResponseIsDeliveredAndFailedRunKeepsOnlyConfirmedHistory() throws Exception {
        HandlerFixture finalOnly = new HandlerFixture();
        var success = finalOnly
                .handler
                .handle("backend", finalOnly.update, finalOnly.turn)
                .toCompletableFuture();
        finalOnly.settled.complete(null);
        finalOnly.emit(
                "run.completed",
                Map.of(
                        "response",
                        ChatResponse.builder()
                                .aiMessage(AiMessage.from("final only"))
                                .build()));
        assertEquals("final only", finalOnly.turn.out.last().text.text());
        finalOnly.turn.out.last().accept("1");
        success.get(2, TimeUnit.SECONDS);
        verify(finalOnly.messages).record("chat", 1L, null, "out", "text", "final only", null);

        HandlerFixture failedRun = new HandlerFixture();
        var failed = failedRun
                .handler
                .handle("backend", failedRun.update, failedRun.turn)
                .toCompletableFuture();
        failedRun.emit("message.delta", Map.of("delta", "confirmed"));
        failedRun.turn.fireTimer();
        failedRun.turn.out.last().accept("1");
        failedRun.emit("message.delta", Map.of("delta", " undelivered"));
        failedRun.settled.complete(null);
        failedRun.emit("run.failed", Map.of("errorCode", "MODEL_ERROR", "message", "Safe error", "partial", true));
        assertFalse(failed.isDone());
        assertEquals("Safe error", failedRun.turn.out.last().text.text());
        failedRun.turn.out.last().accept("2");
        assertThrows(Exception.class, () -> failed.get(2, TimeUnit.SECONDS));
        verify(failedRun.messages).record("chat", 1L, null, "out", "text", "confirmed", null);
        verify(failedRun.messages, never()).record("chat", 2L, null, "out", "text", "Safe error", null);
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
        f.emit("message.delta", Map.of("delta", "reply ![](https://example.invalid/image)"));
        f.emit("run.completed", Map.of()); // adversarial early terminal still cannot release before settlement
        f.turn.out.last().accept(null);
        f.turn.out.last().accept("2");
        assertFalse(result.isDone());
        f.settled.complete(null);
        assertThrows(Exception.class, () -> result.get(2, TimeUnit.SECONDS));
        verify(f.messages).record("chat", 1L, null, "out", "text", "reply ", null);
        verify(f.messages).record("chat", 2L, null, "out", "photo", "https://example.invalid/image", null);
        assertEquals(2, f.turn.out.calls.size());
    }

    private static TelegramStreamingReplyEmitter started(FakeTurn turn) {
        TelegramStreamingReplyEmitter emitter = new TelegramStreamingReplyEmitter(turn);
        var started = emitter.start();
        assertTrue(started.isDone());
        assertTrue(turn.out.calls.isEmpty());
        assertNull(turn.typingInterval);
        emitter.onRunEvent(event("run.queued", Map.of("phase", "queued")));
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
        final CompletableFuture<Void> settled = new CompletableFuture<>();
        final CompletableFuture<Void> eventDelivery = new CompletableFuture<>();
        final AtomicReference<Consumer<ChatRunEvent>> observer = new AtomicReference<>();
        Runnable duringStart = () -> {};
        final Update update = new Update();
        final ChatBindingTelegramMessageHandler handler =
                new ChatBindingTelegramMessageHandler(bindings, messages, chat);
        int cancellations;

        HandlerFixture() {
            update.setMessage(inbound());
            when(bindings.getOrCreate("backend", -123L, "group", null, null, null, null, null))
                    .thenReturn("chat");
            when(chat.startRun(eq("chat"), any(UserMessage.class), isNull(), any()))
                    .thenAnswer(call -> {
                        observer.set(call.getArgument(3));
                        emit("run.queued", Map.of("phase", "queued"));
                        duringStart.run();
                        return new ChatRunHandle() {
                            public String runId() {
                                return "run";
                            }

                            public CompletableFuture<Void> settled() {
                                return settled;
                            }

                            public CompletableFuture<Void> eventsDelivered() {
                                return eventDelivery;
                            }

                            public void cancel() {
                                cancellations++;
                            }
                        };
                    });
        }

        void emit(String name, Map<String, Object> data) {
            observer.get().accept(event(name, data));
        }
    }

    private static ChatRunEvent event(String name, Map<String, Object> data) {
        return new ChatRunEvent(name, "run", "chat", 1L, Instant.now(), data);
    }

    private record FakeTimer(Runnable action, Duration delay, ScheduledFuture<?> future) {}

    private static final class FakeTurn implements ChannelTurnContext {
        final FakeOutbound out = new FakeOutbound();
        final List<Runnable> cancellations = new ArrayList<>();
        final List<FakeTimer> timers = new ArrayList<>();
        boolean cancelled;
        int typingClosed;
        int typingStarted;
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
            typingStarted++;
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
            result.complete(id == null && !kind.equals("send") ? null : new ChannelReceipt(id == null ? "1" : id));
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
