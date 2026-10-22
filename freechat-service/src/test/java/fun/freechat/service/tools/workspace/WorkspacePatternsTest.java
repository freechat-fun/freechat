package fun.freechat.service.tools.workspace;

import static org.junit.jupiter.api.Assertions.*;

import fun.freechat.service.tools.ToolContext;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class WorkspacePatternsTest {
    @Test
    void restrictedRegexAgreesWithExpectedSearchSemanticsIncludingUnicode() {
        List<String> texts = new ArrayList<>(List.of("", ".", "a.b", "\uD83D\uDE00", "a\uD83D\uDE00b"));
        for (int length = 1; length <= 6; length++) {
            for (int bits = 0; bits < (1 << length); bits++) {
                StringBuilder text = new StringBuilder();
                for (int bit = 0; bit < length; bit++) {
                    text.append((bits & (1 << bit)) == 0 ? 'a' : 'b');
                }
                texts.add(text.toString());
            }
        }
        for (String expression : List.of(
                "^$", ".*", "a?", "a+", "a*b", "ab?", "a.*b", "a.+b", "a?a?a?b", "^a+$", ".", "\\.", "^\\.$", "a$",
                "^a", "a.?b", "b?a+b*", "^a?b?$", "^.$", "\\$")) {
            Pattern reference = Pattern.compile(expression); // Fixed benign test expressions only.
            var matcher = WorkspacePatterns.regex(expression);
            for (String text : texts) {
                assertEquals(
                        reference.matcher(text).find(), matcher.matches(text, context()), expression + " / " + text);
            }
        }
    }

    @Test
    void ambiguousOptionalChainsAreBoundedRatherThanBacktracking() {
        var matcher = WorkspacePatterns.regex("a?".repeat(60) + "b");
        assertTimeoutPreemptively(
                Duration.ofSeconds(3), () -> assertFalse(matcher.matches("a".repeat(100_000), context())));
        assertTimeoutPreemptively(
                Duration.ofSeconds(3),
                () -> assertFalse(WorkspacePatterns.regex("(a+)+$").matches("a".repeat(100_000) + "!", context())));
    }

    @Test
    void matchingHonorsExpiredContexts() {
        var matcher = WorkspacePatterns.regex("a*b");
        ToolContext expired = new ToolContext("chat", "user", "character", "backend", Duration.ofNanos(1));
        assertThrows(IllegalStateException.class, () -> matcher.matches("aaaa", expired));
    }

    @Test
    void globDoubleStarMatchesZeroOrManyComponentsNotPartialNames() {
        String[] pattern = WorkspacePatterns.glob("**/*.java");
        assertTrue(WorkspacePatterns.globMatches(pattern, "File.java"));
        assertTrue(WorkspacePatterns.globMatches(pattern, "a/b/File.java"));
        assertFalse(WorkspacePatterns.globMatches(pattern, "a/b/File.javac"));
        assertTrue(WorkspacePatterns.globMatches(WorkspacePatterns.glob("a/**/b?"), "a/b1"));
        assertTrue(WorkspacePatterns.globMatches(WorkspacePatterns.glob("a/**/b?"), "a/x/y/b1"));
        assertFalse(WorkspacePatterns.globMatches(WorkspacePatterns.glob("a/*/b?"), "a/x/y/b1"));
        assertTrue(WorkspacePatterns.globMatches(WorkspacePatterns.glob("?.txt"), "\uD83D\uDE00.txt"));
    }

    private static ToolContext context() {
        return new ToolContext("chat", "user", "character", "backend", Duration.ofSeconds(10));
    }
}
