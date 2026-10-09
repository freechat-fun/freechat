package fun.freechat.channels.telegram;

import fun.freechat.channels.spi.ChannelAddress;
import fun.freechat.channels.spi.ChannelFailure;
import fun.freechat.channels.spi.ChannelInvitationProvider;
import fun.freechat.channels.spi.ChannelMedia;
import fun.freechat.channels.spi.ChannelMediaTransport;
import fun.freechat.channels.spi.ChannelMessageEditor;
import fun.freechat.channels.spi.ChannelReceipt;
import fun.freechat.channels.spi.ChannelStatusTransport;
import fun.freechat.channels.spi.ChannelText;
import fun.freechat.channels.spi.ChannelTransport;
import java.io.InputStream;
import java.time.Duration;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ActionType;
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
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/** Raw, single-attempt adapter. Ordering, deadlines and retries belong to the channel runtime. */
@Component
@RequiredArgsConstructor
public class DefaultTelegramChannel
        implements ChannelTransport,
                ChannelMediaTransport,
                ChannelMessageEditor,
                ChannelStatusTransport,
                ChannelInvitationProvider {

    private final TelegramChannelManager manager;

    private TelegramClient client(ChannelAddress address) {
        TelegramClient client = manager.getClient(address.instanceId());
        if (client == null) {
            throw new ChannelFailure(ChannelFailure.Kind.REJECTED);
        }
        return client;
    }

    @Override
    public ChannelReceipt sendText(ChannelAddress address, ChannelText text) {
        try {
            return receipt(client(address)
                    .execute(SendMessage.builder()
                            .chatId(address.conversationId())
                            .text(text.text())
                            .parseMode(parseMode(text))
                            .build()));
        } catch (Exception failure) {
            throw classify(failure, false);
        }
    }

    @Override
    public Set<ChannelMedia.Kind> mediaKinds() {
        return Set.of(ChannelMedia.Kind.values());
    }

    @Override
    public ChannelReceipt sendMedia(ChannelAddress address, ChannelMedia media) {
        // Reopen uploads on every runtime attempt, and close even after SDK rejection.
        // A close failure after confirmed acceptance must not turn into a duplicate send.
        ChannelReceipt accepted = null;
        try {
            TelegramClient client = client(address);
            if (media.resource() instanceof ChannelMedia.Reference reference) {
                return sendMedia(client, address, media, new InputFile(reference.value()));
            }
            ChannelMedia.Upload upload = (ChannelMedia.Upload) media.resource();
            try (InputStream input = upload.opener().open()) {
                accepted = sendMedia(client, address, media, new InputFile(input, upload.filename()));
            }
            return accepted;
        } catch (Exception failure) {
            if (accepted != null) {
                return accepted;
            }
            throw classify(failure, false);
        }
    }

    private ChannelReceipt sendMedia(TelegramClient client, ChannelAddress address, ChannelMedia media, InputFile file)
            throws TelegramApiException {
        String chatId = address.conversationId();
        String caption = media.caption();
        Message sent =
                switch (media.kind()) {
                    case IMAGE ->
                        client.execute(SendPhoto.builder()
                                .chatId(chatId)
                                .photo(file)
                                .caption(caption)
                                .build());
                    case VOICE ->
                        client.execute(SendVoice.builder()
                                .chatId(chatId)
                                .voice(file)
                                .caption(caption)
                                .build());
                    case VIDEO ->
                        client.execute(SendVideo.builder()
                                .chatId(chatId)
                                .video(file)
                                .caption(caption)
                                .build());
                    case AUDIO ->
                        client.execute(SendAudio.builder()
                                .chatId(chatId)
                                .audio(file)
                                .caption(caption)
                                .build());
                    case DOCUMENT ->
                        client.execute(SendDocument.builder()
                                .chatId(chatId)
                                .document(file)
                                .caption(caption)
                                .build());
                };
        return receipt(sent);
    }

    @Override
    public void editText(ChannelAddress address, String messageId, ChannelText text) {
        int id;
        try {
            id = Integer.parseInt(messageId);
        } catch (RuntimeException invalidId) {
            throw new ChannelFailure(ChannelFailure.Kind.REJECTED);
        }
        try {
            client(address)
                    .execute(EditMessageText.builder()
                            .chatId(address.conversationId())
                            .messageId(id)
                            .text(text.text())
                            .parseMode(parseMode(text))
                            .build());
        } catch (TelegramApiRequestException failure) {
            String description = failure.getApiResponse() == null
                    ? ""
                    : failure.getApiResponse().toLowerCase(Locale.ROOT);
            if (Integer.valueOf(400).equals(failure.getErrorCode())
                    && (description.startsWith("bad request: message is not modified")
                            || description.equals("message is not modified"))) {
                return;
            }
            throw classify(failure, true);
        } catch (Exception failure) {
            throw classify(failure, true);
        }
    }

    @Override
    public void sendTyping(ChannelAddress address) {
        try {
            client(address)
                    .execute(SendChatAction.builder()
                            .chatId(address.conversationId())
                            .action(ActionType.TYPING.toString())
                            .build());
        } catch (Exception failure) {
            throw classify(failure, true);
        }
    }

    @Override
    public String invitationLink(String instanceId) {
        return manager.getInviteLink(instanceId);
    }

    @Override
    public String username(String instanceId) {
        return manager.getUsername(instanceId);
    }

    private static String parseMode(ChannelText text) {
        return text.format() == ChannelText.Format.MARKDOWN ? ParseMode.MARKDOWN : null;
    }

    private static ChannelReceipt receipt(Message message) {
        if (message == null || message.getMessageId() == null) {
            throw new ChannelFailure(ChannelFailure.Kind.AMBIGUOUS);
        }
        return new ChannelReceipt(message.getMessageId().toString());
    }

    private static ChannelFailure classify(Exception failure, boolean knownIdOrStatus) {
        if (failure instanceof ChannelFailure safe) {
            // try-with-resources may have attached a private upload-close exception as suppressed.
            return new ChannelFailure(safe.kind(), safe.retryAfter());
        }
        if (failure instanceof TelegramApiValidationException) {
            return new ChannelFailure(ChannelFailure.Kind.REJECTED);
        }
        if (failure instanceof TelegramApiRequestException request) {
            var parameters = request.getParameters();
            if (parameters != null && parameters.getRetryAfter() != null && parameters.getRetryAfter() > 0) {
                return new ChannelFailure(
                        ChannelFailure.Kind.RATE_LIMITED, Duration.ofSeconds(parameters.getRetryAfter()));
            }
            Integer code = request.getErrorCode();
            if (Integer.valueOf(429).equals(code)) {
                return new ChannelFailure(ChannelFailure.Kind.RATE_LIMITED);
            }
            if (code != null && code >= 400 && code < 500) {
                return new ChannelFailure(ChannelFailure.Kind.REJECTED);
            }
        }
        // A timeout/server error can follow acceptance. Only edits to a known ID are safe to repeat.
        return new ChannelFailure(knownIdOrStatus ? ChannelFailure.Kind.RETRYABLE : ChannelFailure.Kind.AMBIGUOUS);
    }
}
