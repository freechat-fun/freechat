package fun.freechat.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Loads .env.test.local style files (lines of "export KEY=VALUE" or "KEY=VALUE")
 * into System properties before Spring context starts. Existing env vars and
 * system properties are not overridden. Reads from the first file found among
 * candidate paths (cwd, parent, grandparent), allowing the file to live at the
 * project root regardless of which module's tests are running.
 */
public final class EnvFileLoader {

    private static final Pattern LINE = Pattern.compile("^(?:export\\s+)?([A-Za-z_][A-Za-z0-9_]*)\\s*=\\s*(.*)$");

    static {
        for (Path candidate : List.of(
                Path.of(".env.test.local"), Path.of("..", ".env.test.local"), Path.of("..", "..", ".env.test.local"))) {
            if (Files.isRegularFile(candidate)) {
                load(candidate);
                break;
            }
        }
    }

    private EnvFileLoader() {}

    /** Forces class init to load the env file. Safe to call multiple times. */
    public static void ensureLoaded() {}

    private static void load(Path path) {
        try {
            for (String raw : Files.readAllLines(path)) {
                String line = raw.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                var m = LINE.matcher(line);
                if (!m.matches()) {
                    continue;
                }
                String key = m.group(1);
                String value = stripQuotes(m.group(2));
                if (System.getenv(key) != null || System.getProperty(key) != null) {
                    continue;
                }
                System.setProperty(key, value);
            }
        } catch (IOException e) {
            System.err.println("EnvFileLoader failed to read " + path + ": " + e.getMessage());
        }
    }

    private static String stripQuotes(String v) {
        if (v.length() >= 2 && ((v.startsWith("\"") && v.endsWith("\"")) || (v.startsWith("'") && v.endsWith("'")))) {
            return v.substring(1, v.length() - 1);
        }
        return v;
    }
}
