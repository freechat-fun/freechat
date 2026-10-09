package fun.freechat.channels.spi;

import java.util.Objects;

public record ChannelText(String text, Format format) {
    public enum Format {
        PLAIN,
        MARKDOWN
    }

    public ChannelText {
        Objects.requireNonNull(text);
        Objects.requireNonNull(format);
    }

    public static ChannelText plain(String text) {
        return new ChannelText(text, Format.PLAIN);
    }

    @Override
    public String toString() {
        return "ChannelText[format=" + format + "]";
    }
}
