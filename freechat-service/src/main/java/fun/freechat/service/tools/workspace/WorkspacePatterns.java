package fun.freechat.service.tools.workspace;

import com.google.re2j.Pattern;
import com.google.re2j.PatternSyntaxException;
import fun.freechat.service.tools.ToolArguments;
import fun.freechat.service.tools.ToolContext;

final class WorkspacePatterns {
    private WorkspacePatterns() {}

    static String[] glob(String pattern) {
        if (pattern.isEmpty() || pattern.length() > 128) {
            throw ToolArguments.invalid();
        }
        String[] parts = pattern.split("/", -1);
        if (parts.length > 8) {
            throw ToolArguments.invalid();
        }
        for (String part : parts) {
            if (part.isEmpty()
                    || part.equals(".")
                    || part.equals("..")
                    || (part.contains("**") && !part.equals("**"))) {
                throw ToolArguments.invalid();
            }
            for (int c : part.codePoints().toArray()) {
                if (Character.isISOControl(c) || "\\:[]{}".indexOf(c) >= 0) {
                    throw ToolArguments.invalid();
                }
            }
        }
        return parts;
    }

    static boolean globMatches(String[] pattern, String path) {
        String[] parts = path.split("/");
        boolean[] active = new boolean[parts.length + 1];
        active[0] = true;
        for (String token : pattern) {
            boolean[] next = new boolean[active.length];
            for (int j = 0; j <= parts.length; j++) {
                if (token.equals("**")) {
                    next[j] = active[j] || (j > 0 && next[j - 1]);
                } else if (j > 0) {
                    next[j] = active[j - 1] && componentMatches(token, parts[j - 1]);
                }
            }
            active = next;
        }
        return active[parts.length];
    }

    private static boolean componentMatches(String pattern, String value) {
        int[] chars = value.codePoints().toArray();
        boolean[] active = new boolean[chars.length + 1];
        active[0] = true;
        for (int token : pattern.codePoints().toArray()) {
            boolean[] next = new boolean[active.length];
            for (int j = 0; j <= chars.length; j++) {
                next[j] = token == '*'
                        ? active[j] || (j > 0 && next[j - 1])
                        : j > 0 && active[j - 1] && (token == '?' || token == chars[j - 1]);
            }
            active = next;
        }
        return active[chars.length];
    }

    static LinearRegex regex(String expression) {
        if (expression.isEmpty() || expression.length() > 128) {
            throw ToolArguments.invalid();
        }
        try {
            return new LinearRegex(Pattern.compile(expression));
        } catch (PatternSyntaxException ignored) {
            throw ToolArguments.invalid();
        }
    }

    record LinearRegex(Pattern pattern) {
        boolean matches(String text, ToolContext context) {
            context.check();
            boolean found = pattern.matcher(text).find();
            context.check();
            return found;
        }
    }
}
