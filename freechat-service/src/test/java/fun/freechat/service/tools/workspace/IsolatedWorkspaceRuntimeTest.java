package fun.freechat.service.tools.workspace;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import fun.freechat.service.tools.ToolArguments;
import fun.freechat.service.tools.ToolContext;
import fun.freechat.service.tools.ToolDefinition;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

@Timeout(20)
class IsolatedWorkspaceRuntimeTest {
    @TempDir
    Path temporary;

    private Path root;
    private Path outside;
    private IsolatedWorkspaceRuntime runtime;

    @BeforeEach
    void open() throws Exception {
        root = Files.createDirectory(temporary.resolve("workspace"));
        outside = Files.createDirectory(temporary.resolve("outside"));
        runtime = new IsolatedWorkspaceRuntime(IsolatedWorkspaceRuntime.Binding.from(context()), root);
    }

    @AfterEach
    void close() {
        runtime.close();
    }

    @Test
    void catalogIsPureIsolatedAndDoesNotRegisterSpringComponents() {
        List<ToolDefinition> definitions = WorkspaceTools.definitions();
        assertEquals(
                Set.of("read_file", "write_file", "edit", "list_directory", "glob", "grep_search", "run_shell_command"),
                definitions.stream().map(ToolDefinition::name).collect(java.util.stream.Collectors.toSet()));
        assertEquals(7, definitions.size());
        assertThrows(UnsupportedOperationException.class, definitions::clear);
        for (ToolDefinition tool : definitions) {
            assertEquals(ToolDefinition.Boundary.ISOLATED, tool.boundary());
            assertEquals(
                    Set.of(
                            "workspace",
                            "isolated",
                            tool.name().equals("run_shell_command") ? "shell" : "filesystem",
                            tool.sideEffects() ? "write" : "read"),
                    tool.tags());
            assertEquals(Set.of("write_file", "edit", "run_shell_command").contains(tool.name()), tool.sideEffects());
            assertEquals(false, tool.specification().parameters().additionalProperties());
            assertTrue(runtime.supports(tool, context()));
        }
        assertEquals(0, IsolatedWorkspaceRuntime.class.getDeclaredAnnotations().length);
        assertTrue(runtime.isolated());
        assertFalse(runtime.supports(null, context()));
        assertFalse(runtime.supports(tool("read_file"), null));
    }

    @Test
    void readsCodePointPagesAndFullFileSha256() throws Exception {
        String text = "a\uD83D\uDE00b\nsecond line";
        Files.writeString(root.resolve("text.txt"), text);
        JsonNode first = call("read_file", Map.of("path", "text.txt", "limit", 2));
        assertEquals("a\uD83D\uDE00", first.get("content").asText());
        assertEquals(2, first.get("next_offset").asInt());
        assertFalse(first.get("eof").asBoolean());
        assertEquals(
                SecureWorkspace.hash(text.getBytes(StandardCharsets.UTF_8)),
                first.get("version").asText());
        JsonNode next = call("read_file", Map.of("path", "text.txt", "offset", 2));
        assertEquals("b\nsecond line", next.get("content").asText());
        assertEquals(first.get("version"), next.get("version"));
        assertTrue(next.get("eof").asBoolean());
        assertThrows(
                IllegalArgumentException.class, () -> call("read_file", Map.of("path", "text.txt", "offset", 100)));
        Files.writeString(root.resolve("empty"), "");
        assertTrue(call("read_file", Map.of("path", "empty")).get("eof").asBoolean());
    }

    @Test
    void rejectsTraversalAbsoluteBackslashControlAndUnrecognizedArguments() {
        for (String path : List.of(
                "../outside/file",
                "/etc/passwd",
                "a/../b",
                "a/./b",
                "a//b",
                "a/",
                "C:/x",
                "a\\b",
                "a\nb",
                "a\u007fb",
                "",
                ".",
                "..",
                "a/b/c/d/e/f/g/h/i",
                ".freechat-write-fake")) {
            Exception error = assertThrows(Exception.class, () -> call("read_file", Map.of("path", path)), path);
            assertNull(error.getCause());
            assertFalse(error.getMessage().contains(temporary.toString()));
        }
        assertThrows(
                IllegalArgumentException.class,
                () -> call("write_file", Map.of("path", "file", "content", "value", "root", outside.toString())));
        assertThrows(IllegalArgumentException.class, () -> call("read_file", Map.of("path", "file", "limit", 4097)));
        assertThrows(
                IllegalArgumentException.class, () -> call("write_file", Map.of("path", "file", "content", "\uD800")));
    }

    @Test
    void rejectsSymlinkFilesAndParentsForReadsWritesAndTraversal() throws Exception {
        Files.writeString(outside.resolve("secret"), "outside secret");
        Files.createSymbolicLink(root.resolve("link"), outside.resolve("secret"));
        Files.createSymbolicLink(root.resolve("parent"), outside);
        for (String path : List.of("link", "parent/secret")) {
            assertThrows(Exception.class, () -> call("read_file", Map.of("path", path)));
            assertThrows(Exception.class, () -> call("write_file", Map.of("path", path, "content", "overwrite")));
        }
        assertThrows(Exception.class, () -> call("list_directory", Map.of()));
        assertThrows(Exception.class, () -> call("glob", Map.of("pattern", "**")));
        assertThrows(Exception.class, () -> call("grep_search", Map.of("query", "secret")));
        assertEquals("outside secret", Files.readString(outside.resolve("secret")));
        assertThrows(
                IllegalArgumentException.class,
                () -> new IsolatedWorkspaceRuntime(
                        IsolatedWorkspaceRuntime.Binding.from(context()), root.resolve("parent")));
    }

    @Test
    void heldDirectoryHandleCannotFollowAParentSwappedToAnOutsideSymlink() throws Exception {
        Path sub = Files.createDirectory(root.resolve("sub"));
        Files.writeString(sub.resolve("secret"), "inside");
        Files.writeString(outside.resolve("secret"), "outside");
        try (SecureWorkspace secure = new SecureWorkspace(root);
                var held = secure.directory(Path.of("sub"))) {
            Files.move(sub, root.resolve("old-sub"));
            Files.createSymbolicLink(sub, outside);
            assertEquals(
                    "inside",
                    SecureWorkspace.read(held, Path.of("secret"), context()).text());
            assertThrows(Exception.class, () -> call("read_file", Map.of("path", "sub/secret")));
        }
    }

    @Test
    void rootHandleRemainsPinnedButCommandsRejectAReplacedRoot() throws Exception {
        Files.writeString(root.resolve("text"), "inside");
        Files.writeString(outside.resolve("text"), "outside");
        Files.move(root, temporary.resolve("old-root"));
        Files.createSymbolicLink(root, outside);
        assertEquals(
                "inside",
                call("read_file", Map.of("path", "text")).get("content").asText());
        assertThrows(IllegalStateException.class, () -> command(List.of("true"), Map.of()));
    }

    @Test
    void rejectsMalformedUtf8OversizedFilesAndDirectoriesAsFiles() throws Exception {
        Files.write(root.resolve("bad"), new byte[] {(byte) 0xc3, 0x28});
        assertEquals(
                "Workspace file is not valid UTF-8",
                assertThrows(IllegalStateException.class, () -> call("read_file", Map.of("path", "bad")))
                        .getMessage());
        assertThrows(IllegalStateException.class, () -> call("grep_search", Map.of("query", "x")));
        Files.delete(root.resolve("bad"));
        Files.write(root.resolve("large"), new byte[SecureWorkspace.MAX_FILE_BYTES + 1]);
        assertThrows(IllegalStateException.class, () -> call("read_file", Map.of("path", "large", "limit", 1)));
        assertThrows(IllegalStateException.class, () -> call("grep_search", Map.of("query", "x")));
        assertThrows(
                IllegalStateException.class,
                () -> call("write_file", Map.of("path", "new", "content", "é".repeat(70_000))));
        Files.createDirectory(root.resolve("dir"));
        assertThrows(IllegalStateException.class, () -> call("read_file", Map.of("path", "dir")));
        assertThrows(IllegalStateException.class, () -> call("write_file", Map.of("path", "dir", "content", "x")));
        assertFalse(Files.exists(root.resolve("new")));
    }

    @Test
    void rejectsSpecialFilesWithoutOpeningThem() throws Exception {
        // mkfifo is a fixed, harmless worker command; reading it must reject before a blocking open.
        assertEquals(
                0, command(List.of("mkfifo", "pipe"), Map.of()).get("exit_code").asInt());
        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {
            assertThrows(IllegalStateException.class, () -> call("read_file", Map.of("path", "pipe")));
            assertThrows(IllegalStateException.class, () -> call("list_directory", Map.of()));
            assertThrows(IllegalStateException.class, () -> call("write_file", Map.of("path", "pipe", "content", "x")));
        });
    }

    @Test
    void createOnlyAndHashCheckedReplacementDoNotBlindlyOverwrite() throws Exception {
        JsonNode created = call("write_file", Map.of("path", "file", "content", "original"));
        assertEquals("original", Files.readString(root.resolve("file")));
        assertThrows(IllegalStateException.class, () -> call("write_file", Map.of("path", "file", "content", "bad")));
        String version =
                call("read_file", Map.of("path", "file")).get("version").asText();
        assertEquals(created.get("version").asText(), version);
        call("write_file", Map.of("path", "file", "content", "replacement", "expected_hash", version));
        assertThrows(
                IllegalStateException.class,
                () -> call("write_file", Map.of("path", "file", "content", "stale", "expected_hash", version)));
        assertThrows(
                IllegalStateException.class,
                () -> call("write_file", Map.of("path", "missing", "content", "x", "expected_hash", version)));
        assertEquals("replacement", Files.readString(root.resolve("file")));
        assertEquals(1, call("list_directory", Map.of()).get("entries").size());
    }

    @Test
    void writesPreserveExistingModesAndCreatePrivateFiles() throws Exception {
        call("write_file", Map.of("path", "private", "content", "before"));
        assertEquals(
                java.nio.file.attribute.PosixFilePermissions.fromString("rw-------"),
                Files.getPosixFilePermissions(root.resolve("private")));
        for (String mode : List.of("rw-------", "rwxr-x---")) {
            Files.setPosixFilePermissions(
                    root.resolve("private"), java.nio.file.attribute.PosixFilePermissions.fromString(mode));
            String version =
                    call("read_file", Map.of("path", "private")).get("version").asText();
            call("write_file", Map.of("path", "private", "content", "before", "expected_hash", version));
            assertEquals(
                    java.nio.file.attribute.PosixFilePermissions.fromString(mode),
                    Files.getPosixFilePermissions(root.resolve("private")));
            version =
                    call("read_file", Map.of("path", "private")).get("version").asText();
            call(
                    "edit",
                    Map.of(
                            "path",
                            "private",
                            "old_text",
                            "before",
                            "new_text",
                            "after",
                            "expected_count",
                            1,
                            "expected_hash",
                            version));
            assertEquals(
                    java.nio.file.attribute.PosixFilePermissions.fromString(mode),
                    Files.getPosixFilePermissions(root.resolve("private")));
        }
    }

    @Test
    void editRequiresVersionAndExactCountOrExplicitMultiple() throws Exception {
        call("write_file", Map.of("path", "file", "content", "one one one"));
        String version =
                call("read_file", Map.of("path", "file")).get("version").asText();
        Map<String, Object> edit = new java.util.HashMap<>(
                Map.of("path", "file", "old_text", "one", "new_text", "two", "expected_hash", version));
        assertThrows(IllegalArgumentException.class, () -> call("edit", edit));
        edit.put("expected_count", 2);
        assertThrows(IllegalStateException.class, () -> call("edit", edit));
        assertEquals("one one one", Files.readString(root.resolve("file")));
        edit.put("expected_count", 3);
        JsonNode result = call("edit", edit);
        assertEquals(3, result.get("replacements").asInt());
        assertEquals("two two two", Files.readString(root.resolve("file")));
        assertThrows(IllegalStateException.class, () -> call("edit", edit));
        edit.put("expected_hash", result.get("version").asText());
        edit.put("allow_multiple", true);
        assertThrows(IllegalArgumentException.class, () -> call("edit", edit));
        edit.remove("expected_count");
        edit.put("old_text", "two");
        edit.put("new_text", "");
        assertEquals(3, call("edit", edit).get("replacements").asInt());
        assertEquals("  ", Files.readString(root.resolve("file")));
        String last = call("read_file", Map.of("path", "file")).get("version").asText();
        edit.put("expected_hash", last);
        assertThrows(IllegalStateException.class, () -> call("edit", edit));
        edit.put("old_text", "");
        assertThrows(IllegalArgumentException.class, () -> call("edit", edit));
    }

    @Test
    void boundsExpandedEditsBeforeAllocatingTheReplacement() throws Exception {
        Files.writeString(root.resolve("file"), "x".repeat(10_000));
        String version =
                call("read_file", Map.of("path", "file")).get("version").asText();
        assertThrows(
                IllegalStateException.class,
                () -> call(
                        "edit",
                        Map.of(
                                "path",
                                "file",
                                "old_text",
                                "x",
                                "new_text",
                                "y".repeat(10_000),
                                "expected_hash",
                                version,
                                "allow_multiple",
                                true)));
        assertEquals(10_000, Files.size(root.resolve("file")));
    }

    @Test
    void concurrentRuntimeInstancesShareTheWorkerCasLock() throws Exception {
        Files.writeString(root.resolve("file"), "initial");
        String version =
                call("read_file", Map.of("path", "file")).get("version").asText();
        try (var second = new IsolatedWorkspaceRuntime(IsolatedWorkspaceRuntime.Binding.from(context()), root);
                var executor = Executors.newFixedThreadPool(2)) {
            CountDownLatch start = new CountDownLatch(1);
            List<java.util.concurrent.Future<Boolean>> attempts = new ArrayList<>();
            for (IsolatedWorkspaceRuntime worker : List.of(runtime, second)) {
                attempts.add(executor.submit(() -> {
                    start.await();
                    try {
                        worker.execute(
                                tool("write_file"),
                                ToolArguments.JSON.valueToTree(Map.of(
                                        "path",
                                        "file",
                                        "content",
                                        Thread.currentThread().getName(),
                                        "expected_hash",
                                        version)),
                                context());
                        return true;
                    } catch (IllegalStateException e) {
                        assertEquals("Workspace version conflict", e.getMessage());
                        return false;
                    }
                }));
            }
            start.countDown();
            assertNotEquals(
                    attempts.get(0).get(5, TimeUnit.SECONDS), attempts.get(1).get(5, TimeUnit.SECONDS));
        }
        assertEquals(1, call("list_directory", Map.of()).get("entries").size());
    }

    @Test
    void bindsAllFourRoutingFieldsAndDeniesRootSharingAcrossChats() {
        List<ToolContext> wrong = List.of(
                new ToolContext("other", "user", "character", "backend", Duration.ofSeconds(5)),
                new ToolContext("chat", "other", "character", "backend", Duration.ofSeconds(5)),
                new ToolContext("chat", "user", "other", "backend", Duration.ofSeconds(5)),
                new ToolContext("chat", "user", "character", "other", Duration.ofSeconds(5)));
        for (ToolContext context : wrong) {
            assertFalse(runtime.supports(tool("read_file"), context));
            assertThrows(
                    IllegalStateException.class,
                    () -> runtime.execute(
                            tool("write_file"), ToolArguments.parse("{\"path\":\"file\",\"content\":\"x\"}"), context));
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new IsolatedWorkspaceRuntime(IsolatedWorkspaceRuntime.Binding.from(context), root));
        }
        var forged = new ToolDefinition(
                tool("read_file").specification(), Set.of("workspace"), ToolDefinition.Boundary.IN_PROCESS, false);
        assertFalse(runtime.supports(forged, context()));
        assertFalse(Files.exists(root.resolve("file")));
    }

    @Test
    void separateChatRootsDoNotShareFiles() throws Exception {
        ToolContext other = new ToolContext("other-chat", "user", "character", "backend", Duration.ofSeconds(5));
        try (var second = new IsolatedWorkspaceRuntime(IsolatedWorkspaceRuntime.Binding.from(other), outside)) {
            call("write_file", Map.of("path", "file", "content", "chat one"));
            second.execute(
                    tool("write_file"),
                    ToolArguments.JSON.valueToTree(Map.of("path", "file", "content", "chat two")),
                    other);
            assertEquals("chat one", Files.readString(root.resolve("file")));
            assertEquals("chat two", Files.readString(outside.resolve("file")));
            assertFalse(second.supports(tool("read_file"), context()));
        }
    }

    @Test
    void deadlineExpiresWhileWaitingForTheSharedWorkerLock() throws Exception {
        try (var watcher = root.getFileSystem().newWatchService();
                var executor = Executors.newSingleThreadExecutor()) {
            root.register(watcher, java.nio.file.StandardWatchEventKinds.ENTRY_CREATE);
            var running = executor.submit(() -> command(
                    List.of("sh", "-c", "printf ready > ready; while [ ! -f release ]; do sleep 0.05; done"),
                    Map.of()));
            try {
                assertNotNull(watcher.poll(5, TimeUnit.SECONDS));
                ToolContext late = new ToolContext("chat", "user", "character", "backend", Duration.ofMillis(30));
                assertThrows(
                        IllegalStateException.class,
                        () -> runtime.execute(
                                tool("write_file"),
                                ToolArguments.JSON.valueToTree(Map.of("path", "should-not-exist", "content", "x")),
                                late));
                assertFalse(Files.exists(root.resolve("should-not-exist")));
            } finally {
                Files.createFile(root.resolve("release"));
            }
            assertEquals(0, running.get(3, TimeUnit.SECONDS).get("exit_code").asInt());
        }
    }

    @Test
    void expiredAndInterruptedContextsCannotReadWriteOrStartCommands() throws Exception {
        ToolContext expired = new ToolContext("chat", "user", "character", "backend", Duration.ofNanos(1));
        JsonNode args = ToolArguments.parse("{\"path\":\"file\",\"content\":\"x\"}");
        assertFalse(runtime.supports(tool("write_file"), expired));
        assertThrows(IllegalStateException.class, () -> runtime.execute(tool("write_file"), args, expired));
        assertThrows(
                IllegalStateException.class,
                () -> runtime.execute(
                        tool("run_shell_command"),
                        ToolArguments.JSON.valueToTree(Map.of("argv", List.of("touch", "file"))),
                        expired));
        Thread.currentThread().interrupt();
        try {
            assertThrows(IllegalStateException.class, () -> call("write_file", Map.of("path", "file", "content", "x")));
        } finally {
            Thread.interrupted();
        }
        assertFalse(Files.exists(root.resolve("file")));
    }

    @Test
    void directoryAndGlobAreSortedBoundedAndDepthAware() throws Exception {
        Files.writeString(root.resolve("z.txt"), "z");
        Files.writeString(root.resolve("a.txt"), "a");
        Files.createDirectories(root.resolve("a/deeper"));
        Files.writeString(root.resolve("a/b.txt"), "b");
        Files.writeString(root.resolve("a/deeper/c.txt"), "c");
        JsonNode entries = call("list_directory", Map.of("limit", 2));
        assertEquals("a", entries.get("entries").get(0).get("name").asText());
        assertEquals("a.txt", entries.get("entries").get(1).get("name").asText());
        assertTrue(entries.get("truncated").asBoolean());
        JsonNode glob = call("glob", Map.of("pattern", "**/*.txt"));
        assertEquals(List.of("a.txt", "a/b.txt", "a/deeper/c.txt", "z.txt"), strings(glob.get("paths")));
        assertFalse(glob.get("truncated").asBoolean());
        assertEquals(
                List.of("a.txt", "z.txt"),
                strings(call("glob", Map.of("pattern", "?.txt")).get("paths")));
        JsonNode shallow = call("glob", Map.of("pattern", "**", "max_depth", 1));
        assertEquals(List.of("a.txt", "z.txt"), strings(shallow.get("paths")));
        assertTrue(shallow.get("truncated").asBoolean());
        assertTrue(call("glob", Map.of("pattern", "**", "limit", 1))
                .get("truncated")
                .asBoolean());
        assertEquals(
                List.of("a/b.txt", "a/deeper/c.txt"),
                strings(call("glob", Map.of("path", "a", "pattern", "**/*.txt")).get("paths")));
        String match = call("grep_search", Map.of("path", "a", "query", "c"))
                .get("matches")
                .get(0)
                .get("path")
                .asText();
        assertEquals("a/deeper/c.txt", match);
        assertEquals(
                "c", call("read_file", Map.of("path", match)).get("content").asText());
        for (String pattern : List.of("../*", "/x", "a\\b", "a/**b", "[a]", "{a,b}", "a//b")) {
            assertThrows(IllegalArgumentException.class, () -> call("glob", Map.of("pattern", pattern)));
        }
    }

    @Test
    void traversalEntryBudgetFailsClosed() throws Exception {
        for (int i = 0; i <= SecureWorkspace.MAX_ENTRIES; i++) {
            Files.createFile(root.resolve("f" + i));
        }
        assertEquals(
                "Workspace traversal entry limit exceeded",
                assertThrows(IllegalStateException.class, () -> call("glob", Map.of("pattern", "**")))
                        .getMessage());
        assertThrows(IllegalStateException.class, () -> call("list_directory", Map.of("limit", 1)));
    }

    @Test
    void grepSupportsLiteralAndRestrictedLinearRegexAndBoundsPreviews() throws Exception {
        Files.writeString(root.resolve("text"), "literal a.*b\naaaab\nno match\n" + "x".repeat(800) + " needle\n");
        JsonNode literal = call("grep_search", Map.of("query", "a.*b"));
        assertEquals(1, literal.get("matches").size());
        assertEquals(1, literal.get("matches").get(0).get("line").asInt());
        JsonNode regex = call("grep_search", Map.of("query", "^a+b$", "regex", true));
        assertEquals(1, regex.get("matches").size());
        assertEquals(2, regex.get("matches").get(0).get("line").asInt());
        JsonNode longLine =
                call("grep_search", Map.of("query", "needle")).get("matches").get(0);
        assertEquals(512, longLine.get("text").asText().length());
        assertTrue(longLine.get("line_truncated").asBoolean());
        assertTrue(call("grep_search", Map.of("query", "a", "limit", 1))
                .get("truncated")
                .asBoolean());
        for (String query : List.of("[ab]", "a|b", "a{2}", "\\w")) {
            assertFalse(call("grep_search", Map.of("query", query, "regex", true))
                    .get("matches")
                    .isEmpty());
        }
        for (String query : List.of("\\1", "(?=a)", "a**", "")) {
            assertThrows(
                    IllegalArgumentException.class, () -> call("grep_search", Map.of("query", query, "regex", true)));
        }
    }

    @Test
    void grepTotalReadBudgetAndEscapedResultSizeStayBounded() throws Exception {
        for (int i = 0; i < 9; i++) {
            Files.writeString(root.resolve("text" + i), "x".repeat(SecureWorkspace.MAX_FILE_BYTES));
        }
        JsonNode result = call("grep_search", Map.of("query", "absent"));
        assertEquals(1_048_576, result.get("bytes_scanned").asInt());
        assertTrue(result.get("truncated").asBoolean());
        Files.writeString(root.resolve("a"), ("\u0001".repeat(510) + "match\n").repeat(100));
        String encoded = runtime.execute(
                tool("grep_search"), ToolArguments.JSON.valueToTree(Map.of("query", "match")), context());
        assertTrue(encoded.getBytes(StandardCharsets.UTF_8).length < 65_536);
        assertTrue(ToolArguments.JSON.readTree(encoded).get("truncated").asBoolean());
    }

    @Test
    void directCommandHasNoImplicitShellAndAClosedStdin() throws Exception {
        JsonNode result = command(List.of("printf", "%s", "literal; $(touch injected)"), Map.of());
        assertEquals("literal; $(touch injected)", result.get("stdout").asText());
        assertEquals("", result.get("stderr").asText());
        assertEquals(0, result.get("exit_code").asInt());
        assertFalse(result.get("timed_out").asBoolean());
        assertFalse(Files.exists(root.resolve("injected")));
        assertEquals("", command(List.of("cat"), Map.of()).get("stdout").asText());
    }

    @Test
    void commandsHaveCleanEnvironmentAndExplicitBoundedStderrAndExitCode() throws Exception {
        JsonNode result = command(List.of("sh", "-c", "printf out; printf err >&2; exit 7"), Map.of());
        assertEquals("out", result.get("stdout").asText());
        assertEquals("err", result.get("stderr").asText());
        assertEquals(7, result.get("exit_code").asInt());
        JsonNode environment = command(List.of("env"), Map.of());
        Map<String, String> values = new java.util.HashMap<>();
        for (String line : environment.get("stdout").asText().split("\n")) {
            String[] pair = line.split("=", 2);
            values.put(pair[0], pair[1]);
        }
        assertEquals(Set.of("PATH", "HOME", "TMPDIR", "LANG", "LC_ALL"), values.keySet());
        assertEquals("/usr/bin:/bin", values.get("PATH"));
        assertEquals(root.toRealPath().toString(), values.get("HOME"));
        assertEquals(root.toRealPath().toString(), values.get("TMPDIR"));
        assertEquals(
                root.toRealPath().toString(),
                command(List.of("pwd"), Map.of()).get("stdout").asText().strip());
    }

    @Test
    void commandOutputCapsAndTimeoutsTerminateRatherThanBackgroundTheInvocation() throws Exception {
        JsonNode output = command(
                List.of("sh", "-c", "while :; do printf 1234567890; printf abcdefghij >&2; done"),
                Map.of("max_output_bytes", 128));
        int bytes = output.get("stdout").asText().getBytes(StandardCharsets.UTF_8).length
                + output.get("stderr").asText().getBytes(StandardCharsets.UTF_8).length;
        assertTrue(bytes <= 128);
        assertTrue(output.get("output_limited").asBoolean());
        JsonNode timeout = assertTimeoutPreemptively(
                Duration.ofSeconds(3),
                () -> command(
                        List.of("sh", "-c", "sleep 10 & child=$!; printf '%s' \"$child\"; wait"),
                        Map.of("timeout_ms", 200)));
        assertTrue(timeout.get("timed_out").asBoolean());
        long pid = Long.parseLong(timeout.get("stdout").asText());
        ProcessHandle.of(pid).ifPresent(child -> assertFalse(child.isAlive(), "Observed child must be terminated"));
    }

    @Test
    void contextDeadlineAlsoStopsRunningCommandsAndUnknownInputsNeverLeakPaths() throws Exception {
        ToolContext late = new ToolContext("chat", "user", "character", "backend", Duration.ofMillis(100));
        assertTimeoutPreemptively(
                Duration.ofSeconds(3),
                () -> assertThrows(
                        IllegalStateException.class,
                        () -> runtime.execute(
                                tool("run_shell_command"),
                                ToolArguments.JSON.valueToTree(Map.of("argv", List.of("sleep", "10"))),
                                late)));
        for (Map<String, ?> args : List.<Map<String, ?>>of(
                Map.of("argv", List.of("/bin/true")),
                Map.of("argv", List.of("true"), "background", true),
                Map.of("argv", List.of()),
                Map.of("argv", List.of("printf", "x\0y")),
                Map.of("argv", List.of("true"), "cwd", outside.toString()),
                Map.of("argv", List.of("true"), "timeout_ms", 30001))) {
            Exception error = assertThrows(Exception.class, () -> call("run_shell_command", args));
            assertFalse(error.getMessage().contains(temporary.toString()));
            assertNull(error.getCause());
        }
        Exception missing = assertThrows(Exception.class, () -> call("read_file", Map.of("path", "missing")));
        assertEquals("Workspace operation failed", missing.getMessage());
        assertNull(missing.getCause());
    }

    @Test
    void closedRuntimeCannotBeReused() {
        runtime.close();
        assertFalse(runtime.supports(tool("read_file"), context()));
        assertThrows(IllegalStateException.class, () -> call("read_file", Map.of("path", "file")));
    }

    private JsonNode command(List<String> argv, Map<String, ?> options) throws Exception {
        Map<String, Object> args = new java.util.HashMap<>(options);
        args.put("argv", argv);
        return call("run_shell_command", args);
    }

    private JsonNode call(String name, Map<String, ?> args) throws Exception {
        return ToolArguments.JSON.readTree(
                runtime.execute(tool(name), ToolArguments.JSON.valueToTree(args), context()));
    }

    private static ToolDefinition tool(String name) {
        return WorkspaceTools.definitions().stream()
                .filter(tool -> tool.name().equals(name))
                .findFirst()
                .orElseThrow();
    }

    private static ToolContext context() {
        return new ToolContext("chat", "user", "character", "backend", Duration.ofSeconds(15));
    }

    private static List<String> strings(JsonNode array) {
        List<String> values = new ArrayList<>();
        array.forEach(value -> values.add(value.asText()));
        return values;
    }
}
