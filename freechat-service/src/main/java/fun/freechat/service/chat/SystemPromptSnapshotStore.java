package fun.freechat.service.chat;

import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.data.message.SystemMessage;
import fun.freechat.service.common.FileStore;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class SystemPromptSnapshotStore {
    private static final String HOME = "private/messages/snapshots/";
    private static final int MAX_BYTES = 1024 * 1024;
    private final FileStore files;

    public SystemPromptSnapshotStore(FileStore files) {
        this.files = Objects.requireNonNull(files);
    }

    public String save(String chatId, SystemMessage snapshot) {
        if (snapshot == null) {
            return null;
        }
        try {
            String directory = scope(chatId);
            byte[] body = ChatMessageSerializer.messageToJson(snapshot).getBytes(StandardCharsets.UTF_8);
            if (body.length > MAX_BYTES) {
                throw new IOException();
            }
            // Unique names keep generic, overwrite-capable stores from modifying a referenced snapshot.
            String reference = directory + "/" + contentHash(body) + "-"
                    + UUID.randomUUID().toString().replace("-", "");
            files.createDirectories(HOME + directory);
            if (files.write(path(reference), body) != body.length
                    || !MessageDigest.isEqual(body, readBytes(reference))) {
                throw new IOException();
            }
            return reference;
        } catch (Exception ignored) {
            throw new IllegalStateException("System prompt snapshot persistence failed");
        }
    }

    public String read(String chatId, String reference) {
        try {
            if (reference == null
                    || !reference.matches("[0-9a-f]{64}/[A-Za-z0-9_-]{43}-[0-9a-f]{32}")
                    || !reference.startsWith(scope(chatId) + "/")) {
                throw new IOException();
            }
            byte[] body = readBytes(reference);
            String expected = reference.substring(reference.indexOf('/') + 1, reference.lastIndexOf('-'));
            if (!expected.equals(contentHash(body))) {
                throw new IOException();
            }
            return new String(body, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            throw new IllegalStateException("System prompt snapshot unavailable");
        }
    }

    private static String path(String reference) {
        return HOME + reference + ".json";
    }

    private byte[] readBytes(String reference) throws IOException {
        String path = path(reference);
        if (files.size(path) > MAX_BYTES) {
            throw new IOException();
        }
        try (InputStream input = files.newInputStream(path)) {
            byte[] body = input.readNBytes(MAX_BYTES + 1);
            if (body.length > MAX_BYTES) {
                throw new IOException();
            }
            return body;
        }
    }

    private static String scope(String chatId) {
        if (chatId == null || chatId.isBlank() || chatId.length() > 64) {
            throw new IllegalArgumentException();
        }
        return HexFormat.of().formatHex(digest(chatId.getBytes(StandardCharsets.UTF_8)));
    }

    private static String contentHash(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest(bytes));
    }

    private static byte[] digest(byte[] bytes) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(bytes);
        } catch (NoSuchAlgorithmException impossible) {
            throw new IllegalStateException();
        }
    }
}
