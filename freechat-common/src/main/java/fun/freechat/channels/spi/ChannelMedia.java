package fun.freechat.channels.spi;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public record ChannelMedia(Kind kind, Resource resource, String caption) {
    public enum Kind {
        IMAGE,
        VOICE,
        VIDEO,
        AUDIO,
        DOCUMENT
    }

    public sealed interface Resource permits Reference, Upload {}

    public record Reference(String value) implements Resource {
        public Reference {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("Empty channel media reference");
            }
        }

        @Override
        public String toString() {
            return "ChannelMedia.Reference";
        }
    }

    @FunctionalInterface
    public interface Opener {
        InputStream open() throws IOException;
    }

    public record Upload(String filename, Opener opener) implements Resource {
        public Upload {
            Objects.requireNonNull(filename);
            Objects.requireNonNull(opener);
        }

        @Override
        public String toString() {
            return "ChannelMedia.Upload";
        }
    }

    public ChannelMedia {
        Objects.requireNonNull(kind);
        Objects.requireNonNull(resource);
    }

    public static ChannelMedia reference(Kind kind, String reference, String caption) {
        return new ChannelMedia(kind, new Reference(reference), caption);
    }

    public static ChannelMedia file(Kind kind, Path path, String caption) {
        return new ChannelMedia(
                kind, new Upload(path.getFileName().toString(), () -> Files.newInputStream(path)), caption);
    }

    @Override
    public String toString() {
        return "ChannelMedia[kind=" + kind + "]";
    }
}
