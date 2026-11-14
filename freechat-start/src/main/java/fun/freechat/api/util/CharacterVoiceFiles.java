package fun.freechat.api.util;

import fun.freechat.service.common.FileStore;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public final class CharacterVoiceFiles {
    private CharacterVoiceFiles() {}

    public static String resolve(FileStore store, String directory, String speaker) throws IOException {
        String mapping = mappingPath(directory, speaker);
        if (store.exists(mapping)) {
            if (!store.isRegularFile(mapping)) throw new IOException("Invalid voice source mapping");
            if (store.size(mapping) > 1024) throw new IOException("Invalid voice source mapping");
            String name = store.readString(mapping);
            requireFilename(name);
            String source = directory + "/" + name;
            if (!store.exists(source) || !store.isRegularFile(source)) throw new NoSuchFileException(source);
            return source;
        }
        if (StringUtils.isBlank(speaker)) return null;
        String source = match(speaker, store.exists(directory) ? store.list(directory, null, false) : List.of());
        if (!store.isRegularFile(source)) throw new IOException("Invalid voice source file");
        return source;
    }

    public static String match(String speaker, List<String> sources) throws IOException {
        requireFilename(speaker);
        String stem = stem(speaker);
        List<String> matches = sources.stream()
                .filter(source -> stem(Path.of(source).getFileName().toString()).equals(stem))
                .toList();
        if (matches.isEmpty()) throw new NoSuchFileException(speaker);
        if (matches.size() != 1) throw new IOException("Ambiguous voice source for " + speaker);
        return matches.getFirst();
    }

    public static void remember(FileStore store, String directory, String speaker, String source) throws IOException {
        requireFilename(source);
        String mapping = mappingPath(directory, speaker);
        String mappings = directory + ".sources";
        store.writeRegularFile(mapping, source.getBytes(StandardCharsets.UTF_8));
        for (String previous : store.list(mappings, null, false)) {
            if (previous.equals(mapping)) continue;
            if (!store.isRegularFile(previous)) throw new IOException("Invalid voice source mapping");
            if (store.size(previous) > 1024) throw new IOException("Invalid voice source mapping");
            String previousSource = store.readString(previous);
            requireFilename(previousSource);
            if (!store.exists(directory + "/" + previousSource)) store.delete(previous);
        }
    }

    public static String mappingPath(String directory, String speaker) throws IOException {
        String key = "pending";
        if (StringUtils.isNotBlank(speaker)) {
            requireFilename(speaker);
            try {
                key = HexFormat.of()
                        .formatHex(
                                MessageDigest.getInstance("SHA-256").digest(speaker.getBytes(StandardCharsets.UTF_8)));
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e);
            }
        }
        return directory + ".sources/" + key;
    }

    public static String extension(String name) {
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(dot) : "";
    }

    private static String stem(String name) {
        return name.substring(0, name.length() - extension(name).length());
    }

    private static void requireFilename(String name) throws IOException {
        if (StringUtils.isBlank(name)
                || name.equals(".")
                || name.equals("..")
                || name.indexOf('/') >= 0
                || name.indexOf('\\') >= 0
                || name.indexOf(':') >= 0
                || name.chars().anyMatch(Character::isISOControl)) {
            throw new IOException("Invalid voice source filename");
        }
    }
}
