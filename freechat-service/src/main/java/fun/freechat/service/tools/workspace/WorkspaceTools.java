package fun.freechat.service.tools.workspace;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.request.json.JsonArraySchema;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonStringSchema;
import fun.freechat.service.tools.ToolDefinition;
import java.util.List;
import java.util.Set;

/** Pure schema catalog. Loading this class does not open a workspace or register a runtime. */
public final class WorkspaceTools {
    private static final String PATH = "Workspace-relative path, at most 512 characters and 8 components. "
            + "No absolute paths, dot components, backslashes, colons, control characters or symlinks. "
            + "Names starting .freechat-write- are reserved. Parents must exist.";
    private static final String VERSION =
            "64 lowercase hexadecimal SHA-256 characters returned as version by read_file.";
    private static final List<ToolDefinition> DEFINITIONS = List.of(
            tool(
                    "read_file",
                    "Read strict UTF-8 text, at most 128 KiB per file. Pagination uses Unicode code points. "
                            + "Returns content and the SHA-256 version of the complete file.",
                    false,
                    schema().addStringProperty("path", PATH)
                            .addIntegerProperty("offset", "Zero-based code-point offset; default 0.")
                            .addIntegerProperty("limit", "Code points, 1..4096; default 2048.")
                            .required("path")),
            tool(
                    "write_file",
                    "Create a UTF-8 file or replace it, at most 128 KiB. Existing files require expected_hash; "
                            + "omit it for create-only. Uses a worker lock and atomic rename, not OS compare-and-swap "
                            + "against independent writers. Does not create directories.",
                    true,
                    schema().addStringProperty("path", PATH)
                            .addStringProperty("content", "Complete new UTF-8 text.")
                            .addStringProperty("expected_hash", VERSION)
                            .required("path", "content")),
            tool(
                    "edit",
                    "Replace exact, non-overlapping occurrences in a UTF-8 file, at most 128 KiB. "
                            + "Requires expected_hash and either expected_count or allow_multiple=true (not both). "
                            + "Zero matches fail. Worker-serialized atomic rename, not OS CAS against other writers.",
                    true,
                    schema().addStringProperty("path", PATH)
                            .addStringProperty("old_text", "Nonempty exact text.")
                            .addStringProperty("new_text", "Replacement text.")
                            .addStringProperty("expected_hash", VERSION)
                            .addIntegerProperty("expected_count", "Exact occurrence count, 1..131072.")
                            .addBooleanProperty(
                                    "allow_multiple", "Replace all matches without an exact count; default false.")
                            .required("path", "old_text", "new_text", "expected_hash")),
            tool(
                    "list_directory",
                    "Return sorted regular-file/directory entries (name, type, size). "
                            + "Symlinks, special files and unsafe names fail closed. At most 1000 entries inspected; "
                            + "returns truncated when the result limit is reached.",
                    false,
                    directorySchema().addIntegerProperty("limit", "Returned entries, 1..100; default 50.")),
            tool(
                    "glob",
                    "Find regular files in sorted order. Patterns support ? and * within a component, and ** "
                            + "as an entire component (zero or more directories). No braces, classes or escaping. "
                            + "The 1000-entry scan cap fails closed; depth/result limits are reported as truncated.",
                    false,
                    directorySchema()
                            .addStringProperty("pattern", "Relative glob, at most 128 characters, e.g. **/*.java.")
                            .addIntegerProperty("limit", "Returned paths, 1..100; default 50.")
                            .addIntegerProperty("max_depth", "Directory traversal depth, 1..8; default 8.")
                            .required("pattern")),
            tool(
                    "grep_search",
                    "Search strict UTF-8 files by line; at most 128 KiB/file and 1 MiB total. "
                            + "Literal search by default. regex=true uses linear-time RE2 syntax, including groups, "
                            + "alternation and character classes, but no lookarounds or backreferences. "
                            + "At most 1000 entries, 8 levels and 100 matches; line previews "
                            + "are limited to 512 code points. Depth/byte/result limits are reported as truncated; "
                            + "entry-cap overflow, oversized files and invalid UTF-8 fail closed.",
                    false,
                    directorySchema()
                            .addStringProperty("query", "Nonempty single-line expression, at most 128 characters.")
                            .addBooleanProperty("regex", "Use the documented regex subset; default false.")
                            .addIntegerProperty("limit", "Returned matching lines, 1..100; default 50.")
                            .addIntegerProperty("max_depth", "Directory traversal depth, 1..8; default 8.")
                            .required("query")),
            tool(
                    "run_shell_command",
                    "Run one foreground argv invocation in an ALREADY isolated worker. "
                            + "No implicit shell, stdin, PTY, background or session API. Executable is a bare name from "
                            + "/usr/bin or /bin; argv is passed without interpolation. Clean PATH/HOME/TMPDIR environment. "
                            + "Returns bounded stdout/stderr, exit_code (null if unavailable), timed_out and output_limited. "
                            + "Process-tree cleanup is best effort; the worker supervisor must contain/reap detached processes. "
                            + "This tool provides no filesystem, network or privilege isolation.",
                    true,
                    schema().addProperty(
                                    "argv",
                                    JsonArraySchema.builder()
                                            .description(
                                                    "1..32 strings, at most 4096 characters each and 16384 UTF-8 bytes total.")
                                            .items(JsonStringSchema.builder().build())
                                            .build())
                            .addIntegerProperty(
                                    "timeout_ms",
                                    "Execution deadline, 1..30000 ms; default 10000; context may shorten it.")
                            .addIntegerProperty(
                                    "max_output_bytes",
                                    "Combined stdout/stderr bytes, 1..8192; default 4096. "
                                            + "Overflow terminates the process. Non-UTF-8 command output uses replacement characters.")
                            .required("argv")));

    private WorkspaceTools() {}

    public static List<ToolDefinition> definitions() {
        return DEFINITIONS;
    }

    private static JsonObjectSchema.Builder schema() {
        return JsonObjectSchema.builder().additionalProperties(false);
    }

    private static JsonObjectSchema.Builder directorySchema() {
        return schema().addStringProperty("path", PATH + " Omit or use an empty string for the workspace root.");
    }

    private static ToolDefinition tool(
            String name, String description, boolean sideEffects, JsonObjectSchema.Builder schema) {
        return new ToolDefinition(
                ToolSpecification.builder()
                        .name(name)
                        .description(description)
                        .parameters(schema.build())
                        .build(),
                Set.of(
                        "workspace",
                        "isolated",
                        name.equals("run_shell_command") ? "shell" : "filesystem",
                        sideEffects ? "write" : "read"),
                ToolDefinition.Boundary.ISOLATED,
                sideEffects);
    }
}
