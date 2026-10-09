package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fun.freechat.channels.spi.ChannelAddress;
import fun.freechat.channels.spi.ChannelFailure;
import fun.freechat.channels.spi.ChannelMedia;
import fun.freechat.channels.spi.ChannelText;
import fun.freechat.channels.telegram.DefaultTelegramChannel;
import fun.freechat.channels.telegram.TelegramChannelManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.ResponseParameters;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

class TelegramTransportTest {
    private final ChannelAddress address = new ChannelAddress("telegram", "backend", "-123");
    private TelegramChannelManager manager;
    private TelegramClient client;
    private DefaultTelegramChannel transport;

    @BeforeEach
    void setUp() {
        manager = mock(TelegramChannelManager.class);
        client = mock(TelegramClient.class, withSettings().mockMaker(org.mockito.MockMakers.PROXY));
        when(manager.getClient("backend")).thenReturn(client);
        transport = new DefaultTelegramChannel(manager);
    }

    @Test
    void plainMarkdownReceiptsAndInvitationMetadata() throws Exception {
        when(client.execute(any(SendMessage.class))).thenReturn(message(17));
        assertEquals(
                "17", transport.sendText(address, ChannelText.plain("*plain*")).messageId());
        transport.sendText(address, new ChannelText("*bold*", ChannelText.Format.MARKDOWN));
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(client, times(2)).execute(captor.capture());
        assertEquals("-123", captor.getAllValues().getFirst().getChatId());
        assertNull(captor.getAllValues().getFirst().getParseMode());
        assertEquals(ParseMode.MARKDOWN, captor.getAllValues().getLast().getParseMode());
        when(manager.getUsername("backend")).thenReturn("test_bot");
        when(manager.getInviteLink("backend")).thenReturn("https://t.me/test_bot");
        assertEquals("test_bot", transport.username("backend"));
        assertEquals("https://t.me/test_bot", transport.invitationLink("backend"));
    }

    @ParameterizedTest
    @EnumSource(ChannelMedia.Kind.class)
    void mapsEveryMediaKindAndCaptionForReferencesAndRepeatableUploads(ChannelMedia.Kind kind) throws Exception {
        List<InputFile> files = new ArrayList<>();
        List<String> captions = new ArrayList<>();
        switch (kind) {
            case IMAGE ->
                when(client.execute(any(SendPhoto.class))).thenAnswer(call -> {
                    SendPhoto send = call.getArgument(0);
                    files.add(send.getPhoto());
                    captions.add(send.getCaption());
                    return message(18);
                });
            case VOICE ->
                when(client.execute(any(SendVoice.class))).thenAnswer(call -> {
                    SendVoice send = call.getArgument(0);
                    files.add(send.getVoice());
                    captions.add(send.getCaption());
                    return message(18);
                });
            case VIDEO ->
                when(client.execute(any(SendVideo.class))).thenAnswer(call -> {
                    SendVideo send = call.getArgument(0);
                    files.add(send.getVideo());
                    captions.add(send.getCaption());
                    return message(18);
                });
            case AUDIO ->
                when(client.execute(any(SendAudio.class))).thenAnswer(call -> {
                    SendAudio send = call.getArgument(0);
                    files.add(send.getAudio());
                    captions.add(send.getCaption());
                    return message(18);
                });
            case DOCUMENT ->
                when(client.execute(any(SendDocument.class))).thenAnswer(call -> {
                    SendDocument send = call.getArgument(0);
                    files.add(send.getDocument());
                    captions.add(send.getCaption());
                    return message(18);
                });
        }
        assertTrue(transport.mediaKinds().contains(kind));
        assertEquals(
                "18",
                transport
                        .sendMedia(address, ChannelMedia.reference(kind, "provider-file-id", "caption"))
                        .messageId());
        transport.sendMedia(address, ChannelMedia.reference(kind, "https://example.invalid/image", "caption"));
        List<TrackedInput> opened = new ArrayList<>();
        ChannelMedia upload = new ChannelMedia(
                kind,
                new ChannelMedia.Upload("asset.bin", () -> {
                    TrackedInput input = new TrackedInput();
                    opened.add(input);
                    return input;
                }),
                "caption");
        transport.sendMedia(address, upload);
        transport.sendMedia(address, upload);
        assertEquals(2, opened.size());
        assertNotSame(opened.getFirst(), opened.getLast());
        assertTrue(opened.stream().allMatch(input -> input.closed));
        assertEquals("provider-file-id", files.get(0).getAttachName());
        assertEquals("https://example.invalid/image", files.get(1).getAttachName());
        assertFalse(files.getFirst().isNew());
        assertTrue(files.get(2).isNew());
        assertEquals("asset.bin", files.get(2).getMediaName());
        assertSame(opened.getFirst(), files.get(2).getNewMediaStream());
        assertEquals(List.of("caption", "caption", "caption", "caption"), captions);
    }

    @Test
    void explicitRateLimitIsSafeAndSingleAttemptIncludingFinalEdit() throws Exception {
        TelegramApiRequestException provider = request(429, "secret-token private message https://private.invalid");
        ResponseParameters parameters = new ResponseParameters();
        parameters.setRetryAfter(7);
        when(provider.getParameters()).thenReturn(parameters);
        when(client.execute(any(SendMessage.class))).thenThrow(provider);
        when(client.execute(any(EditMessageText.class))).thenThrow(provider);
        ChannelFailure send =
                assertThrows(ChannelFailure.class, () -> transport.sendText(address, ChannelText.plain("private")));
        assertEquals(ChannelFailure.Kind.RATE_LIMITED, send.kind());
        assertEquals(Duration.ofSeconds(7), send.retryAfter());
        assertNull(send.getCause());
        assertEquals("Channel operation rate_limited", send.getMessage());
        ChannelFailure edit = assertThrows(
                ChannelFailure.class,
                () -> transport.editText(address, "17", new ChannelText("final", ChannelText.Format.MARKDOWN)));
        assertEquals(ChannelFailure.Kind.RATE_LIMITED, edit.kind());
        verify(client).execute(any(SendMessage.class));
        verify(client).execute(any(EditMessageText.class));
    }

    @Test
    void ambiguousSendsNeverRetryButKnownIdEditsAreRetryable() throws Exception {
        TelegramApiException provider = new TelegramApiException("secret-token private message");
        when(client.execute(any(SendMessage.class))).thenThrow(provider);
        when(client.execute(any(EditMessageText.class))).thenThrow(provider);
        ChannelFailure send = assertThrows(
                ChannelFailure.class,
                () -> transport.sendText(address, new ChannelText("*text*", ChannelText.Format.MARKDOWN)));
        assertEquals(ChannelFailure.Kind.AMBIGUOUS, send.kind());
        assertFalse(send.retryable());
        assertNull(send.getCause());
        ChannelFailure edit =
                assertThrows(ChannelFailure.class, () -> transport.editText(address, "17", ChannelText.plain("text")));
        assertEquals(ChannelFailure.Kind.RETRYABLE, edit.kind());
        verify(client).execute(any(SendMessage.class));
    }

    @Test
    void definiteUnmodifiedEditSucceedsAndParseRejectionDoesNotBlindlyRetry() throws Exception {
        TelegramApiRequestException unchanged = request(400, "Bad Request: message is not modified");
        TelegramApiRequestException parseRejected = request(400, "can't parse entities secret");
        when(client.execute(any(EditMessageText.class))).thenThrow(unchanged);
        assertDoesNotThrow(() -> transport.editText(address, "17", ChannelText.plain("same")));
        when(client.execute(any(SendMessage.class))).thenThrow(parseRejected);
        ChannelFailure failure = assertThrows(
                ChannelFailure.class,
                () -> transport.sendText(address, new ChannelText("*", ChannelText.Format.MARKDOWN)));
        assertEquals(ChannelFailure.Kind.REJECTED, failure.kind());
        assertNull(failure.getCause());
        verify(client).execute(any(SendMessage.class));
    }

    @Test
    void rejectedUploadClosesAndReopensOnNextRuntimeAttempt() throws Exception {
        TelegramApiRequestException limited = request(429, "private provider body");
        when(client.execute(any(SendDocument.class))).thenThrow(limited).thenReturn(message(20));
        List<TrackedInput> streams = new ArrayList<>();
        ChannelMedia media = new ChannelMedia(
                ChannelMedia.Kind.DOCUMENT,
                new ChannelMedia.Upload("file", () -> {
                    TrackedInput input = new TrackedInput();
                    streams.add(input);
                    return input;
                }),
                null);
        assertThrows(ChannelFailure.class, () -> transport.sendMedia(address, media));
        assertTrue(streams.getFirst().closed);
        assertEquals("20", transport.sendMedia(address, media).messageId());
        assertEquals(2, streams.size());
        assertTrue(streams.getLast().closed);
    }

    @Test
    void uploadCloseFailureCannotEraseConfirmedAcceptance() throws Exception {
        when(client.execute(any(SendDocument.class))).thenReturn(message(21));
        ChannelMedia media = new ChannelMedia(
                ChannelMedia.Kind.DOCUMENT,
                new ChannelMedia.Upload("file", () -> new ByteArrayInputStream(new byte[0]) {
                    @Override
                    public void close() throws IOException {
                        throw new IOException("private path");
                    }
                }),
                null);
        assertEquals("21", transport.sendMedia(address, media).messageId());
        verify(client).execute(any(SendDocument.class));
        when(client.execute(any(SendDocument.class))).thenReturn(null);
        ChannelFailure failure = assertThrows(ChannelFailure.class, () -> transport.sendMedia(address, media));
        assertEquals(ChannelFailure.Kind.AMBIGUOUS, failure.kind());
        assertNull(failure.getCause());
        assertEquals(0, failure.getSuppressed().length);
    }

    @Test
    void missingClientAndInvalidEditIdsAreSanitizedAndTypingIsMapped() throws Exception {
        when(client.execute(any(SendChatAction.class))).thenReturn(true);
        transport.sendTyping(address);
        ArgumentCaptor<SendChatAction> action = ArgumentCaptor.forClass(SendChatAction.class);
        verify(client).execute(action.capture());
        assertEquals("typing", action.getValue().getAction());
        assertEquals("-123", action.getValue().getChatId());
        assertEquals(
                ChannelFailure.Kind.REJECTED,
                assertThrows(
                                ChannelFailure.class,
                                () -> transport.editText(address, "secret", ChannelText.plain("text")))
                        .kind());
        when(manager.getClient("backend")).thenReturn(null);
        ChannelFailure failure =
                assertThrows(ChannelFailure.class, () -> transport.sendText(address, ChannelText.plain("private")));
        assertEquals(ChannelFailure.Kind.REJECTED, failure.kind());
        assertNull(failure.getCause());
    }

    @Test
    void localFileResourceIsRepeatableAndSdkReadsWhileOpen(@TempDir Path directory) throws Exception {
        Path file = directory.resolve("voice.bin");
        Files.write(file, new byte[] {1, 2, 3});
        when(client.execute(any(SendVoice.class))).thenAnswer(call -> {
            SendVoice send = call.getArgument(0);
            assertArrayEquals(
                    new byte[] {1, 2, 3}, send.getVoice().getNewMediaStream().readAllBytes());
            return message(22);
        });
        ChannelMedia media = ChannelMedia.file(ChannelMedia.Kind.VOICE, file, "voice caption");
        assertEquals("22", transport.sendMedia(address, media).messageId());
        assertEquals("22", transport.sendMedia(address, media).messageId());
        verify(client, times(2)).execute(any(SendVoice.class));
    }

    @Test
    void ambiguousPhotoAndMissingReceiptAreSafeWithoutRetry() throws Exception {
        when(client.execute(any(SendPhoto.class))).thenThrow(new TelegramApiException("private url and token"));
        ChannelFailure failure = assertThrows(
                ChannelFailure.class,
                () -> transport.sendMedia(address, ChannelMedia.reference(ChannelMedia.Kind.IMAGE, "file-id", null)));
        assertEquals(ChannelFailure.Kind.AMBIGUOUS, failure.kind());
        assertNull(failure.getCause());
        verify(client).execute(any(SendPhoto.class));
        assertEquals(
                ChannelFailure.Kind.AMBIGUOUS,
                assertThrows(ChannelFailure.class, () -> transport.sendText(address, ChannelText.plain("text")))
                        .kind());
    }

    private static Message message(int id) {
        Message message = new Message();
        message.setMessageId(id);
        return message;
    }

    private static TelegramApiRequestException request(int code, String body) {
        TelegramApiRequestException failure = mock(TelegramApiRequestException.class);
        when(failure.getErrorCode()).thenReturn(code);
        when(failure.getApiResponse()).thenReturn(body);
        return failure;
    }

    private static final class TrackedInput extends ByteArrayInputStream {
        private boolean closed;

        private TrackedInput() {
            super(new byte[] {1, 2, 3});
        }

        @Override
        public void close() {
            closed = true;
        }
    }
}
