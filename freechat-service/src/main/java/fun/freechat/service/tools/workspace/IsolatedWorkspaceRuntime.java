package fun.freechat.service.tools.workspace;

import com.fasterxml.jackson.databind.JsonNode;
import fun.freechat.service.tools.ToolArguments;
import fun.freechat.service.tools.ToolContext;
import fun.freechat.service.tools.ToolDefinition;
import fun.freechat.service.tools.ToolRuntime;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SecureDirectoryStream;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/** Requires an externally isolated, exclusively bound worker; never register in the service JVM. */
public final class IsolatedWorkspaceRuntime implements ToolRuntime, AutoCloseable {
    private static final Map<RootIdentity, Lease> LEASES = new HashMap<>();
    private static final int RESULT_BYTES = 24_000;
    private final Binding binding;
    private final SecureWorkspace workspace;
    private final RootIdentity identity;
    private final Lease lease;
    private volatile boolean closed;

    /** Trusted routing identity, never constructed from tool arguments. Invocation IDs may vary. */
    public record Binding(String chatId, String userId, String characterUid, String backendId) {
        public Binding {
            Objects.requireNonNull(chatId);
            Objects.requireNonNull(userId);
            Objects.requireNonNull(characterUid);
            Objects.requireNonNull(backendId);
        }

        public static Binding from(ToolContext context) {
            return new Binding(context.chatId(), context.userId(), context.characterUid(), context.backendId());
        }

        boolean matches(ToolContext context) {
            return context != null
                    && chatId.equals(context.chatId())
                    && userId.equals(context.userId())
                    && characterUid.equals(context.characterUid())
                    && backendId.equals(context.backendId());
        }
    }

    public IsolatedWorkspaceRuntime(Binding trustedBinding, Path isolatedWorkerRoot) {
        binding = Objects.requireNonNull(trustedBinding);
        SecureWorkspace opened = null;
        try {
            opened = new SecureWorkspace(isolatedWorkerRoot);
            identity = new RootIdentity(opened.rootPath.getFileSystem(), opened.rootKey);
            synchronized (LEASES) {
                Lease existing = LEASES.get(identity);
                if (existing != null && !existing.binding.equals(binding)) {
                    throw SecureWorkspace.failure("Workspace is already bound to another context");
                }
                lease = existing == null ? new Lease(binding) : existing;
                lease.references++;
                LEASES.put(identity, lease);
            }
            workspace = opened;
        } catch (IOException | RuntimeException e) {
            if (opened != null) {
                try {
                    opened.close();
                } catch (IOException ignored) {
                    // Do not disclose provider exceptions or host paths.
                }
            }
            throw new IllegalArgumentException(
                    "Workspace requires an available secure directory and exclusive binding");
        }
    }

    @Override
    public String id() {
        return "isolated-workspace";
    }

    @Override
    public boolean isolated() {
        // A declaration about the required deployment contract, NOT a sandbox capability check.
        return true;
    }

    @Override
    public boolean supports(ToolDefinition tool, ToolContext context) {
        if (closed
                || tool == null
                || !binding.matches(context)
                || !WorkspaceTools.definitions().contains(tool)) {
            return false;
        }
        try {
            context.check();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    @Override
    public String execute(ToolDefinition tool, JsonNode arguments, ToolContext context) throws Exception {
        if (context == null || !binding.matches(context)) {
            throw SecureWorkspace.failure("Workspace context binding mismatch");
        }
        context.check();
        if (!supports(tool, context)) {
            throw SecureWorkspace.failure("Workspace tool unavailable");
        }
        boolean locked = false;
        try {
            locked = lease.lock.tryLock(context.remaining().toNanos(), TimeUnit.NANOSECONDS);
            context.check();
            if (!locked || closed) {
                throw SecureWorkspace.failure("Workspace tool unavailable");
            }
            Object value =
                    switch (tool.name()) {
                        case "read_file" -> read(arguments, context);
                        case "write_file" -> write(arguments, context);
                        case "edit" -> edit(arguments, context);
                        case "list_directory" -> list(arguments, context);
                        case "glob" -> search(arguments, context, false);
                        case "grep_search" -> search(arguments, context, true);
                        case "run_shell_command" -> command(arguments, context);
                        default -> throw SecureWorkspace.failure("Workspace tool unavailable");
                    };
            context.check();
            return ToolArguments.result(value);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw SecureWorkspace.failure("Tool invocation expired");
        } catch (IOException | RuntimeException e) {
            // Provider exceptions can include absolute host paths. Never retain their cause or message.
            if (e instanceof IllegalArgumentException) {
                throw ToolArguments.invalid();
            }
            String message = e.getMessage();
            if (e instanceof IllegalStateException
                    && message != null
                    && (message.startsWith("Workspace ") || message.equals("Tool invocation expired"))) {
                throw new IllegalStateException(message);
            }
            throw SecureWorkspace.failure("Workspace operation failed");
        } finally {
            if (locked) {
                lease.lock.unlock();
            }
        }
    }

    private Object read(JsonNode args, ToolContext context) throws IOException {
        ToolArguments.only(args, "path", "offset", "limit");
        Path path = filePath(args);
        int offset = ToolArguments.integer(args, "offset", 0, 0, SecureWorkspace.MAX_FILE_BYTES);
        int limit = ToolArguments.integer(args, "limit", 2048, 1, 4096);
        var snapshot = workspace.read(path, context);
        String text = snapshot.text();
        int total = text.codePointCount(0, text.length());
        if (offset > total) {
            throw ToolArguments.invalid();
        }
        int count = Math.min(limit, total - offset);
        int start = text.offsetByCodePoints(0, offset);
        return Map.of(
                "path",
                path.toString(),
                "content",
                text.substring(start, text.offsetByCodePoints(start, count)),
                "version",
                snapshot.version(),
                "offset",
                offset,
                "next_offset",
                offset + count,
                "eof",
                offset + count == total,
                "total_characters",
                total);
    }

    private Object write(JsonNode args, ToolContext context) throws IOException {
        ToolArguments.only(args, "path", "content", "expected_hash");
        Path path = filePath(args);
        byte[] content = ToolArguments.text(args, "content", SecureWorkspace.MAX_FILE_BYTES)
                .getBytes(StandardCharsets.UTF_8);
        String expected = expectedHash(args, false);
        String version = workspace.write(path, content, expected, context);
        return Map.of("path", path.toString(), "version", version, "bytes", content.length);
    }

    private Object edit(JsonNode args, ToolContext context) throws IOException {
        ToolArguments.only(args, "path", "old_text", "new_text", "expected_hash", "expected_count", "allow_multiple");
        Path path = filePath(args);
        String oldText = ToolArguments.text(args, "old_text", SecureWorkspace.MAX_FILE_BYTES);
        String newText = ToolArguments.text(args, "new_text", SecureWorkspace.MAX_FILE_BYTES);
        String expected = expectedHash(args, true);
        boolean multiple = ToolArguments.bool(args, "allow_multiple", false);
        if (oldText.isEmpty() || multiple == args.has("expected_count")) {
            throw ToolArguments.invalid();
        }
        int expectedCount = ToolArguments.integer(args, "expected_count", 1, 1, SecureWorkspace.MAX_FILE_BYTES);
        var snapshot = workspace.read(path, context);
        if (!snapshot.version().equals(expected)) {
            throw SecureWorkspace.failure("Workspace version conflict");
        }
        int count = 0;
        for (int offset = 0; (offset = snapshot.text().indexOf(oldText, offset)) >= 0; offset += oldText.length()) {
            context.check();
            count++;
        }
        if (count == 0 || (!multiple && count != expectedCount)) {
            throw SecureWorkspace.failure("Workspace replacement count conflict");
        }
        long newBytes = snapshot.bytes().length
                + (long) count
                        * (newText.getBytes(StandardCharsets.UTF_8).length
                                - oldText.getBytes(StandardCharsets.UTF_8).length);
        if (newBytes > SecureWorkspace.MAX_FILE_BYTES) {
            throw SecureWorkspace.failure("Workspace file size limit exceeded");
        }
        byte[] content = snapshot.text().replace(oldText, newText).getBytes(StandardCharsets.UTF_8);
        String version = workspace.write(path, content, expected, context);
        return Map.of("path", path.toString(), "version", version, "replacements", count);
    }

    private Object list(JsonNode args, ToolContext context) throws IOException {
        ToolArguments.only(args, "path", "limit");
        int limit = ToolArguments.integer(args, "limit", 50, 1, 100);
        Results results = new Results(limit);
        try (var directory = workspace.directory(directoryPath(args))) {
            for (Path name : SecureWorkspace.names(directory, new SecureWorkspace.Counter(), context)) {
                context.check();
                var attributes = SecureWorkspace.attributes(directory, name);
                if (!results.add(Map.of(
                        "name",
                        name.toString(),
                        "type",
                        attributes.isDirectory() ? "directory" : "file",
                        "size",
                        attributes.size()))) {
                    break;
                }
            }
        }
        return Map.of("entries", results.values, "truncated", results.truncated);
    }

    private Object search(JsonNode args, ToolContext context, boolean grep) throws IOException {
        if (grep) {
            ToolArguments.only(args, "path", "query", "regex", "limit", "max_depth");
        } else {
            ToolArguments.only(args, "path", "pattern", "limit", "max_depth");
        }
        int depth = ToolArguments.integer(args, "max_depth", 8, 1, 8);
        Results results = new Results(ToolArguments.integer(args, "limit", 50, 1, 100));
        String query = grep ? ToolArguments.text(args, "query", 128) : null;
        if (grep && (query.isEmpty() || query.codePoints().anyMatch(Character::isISOControl))) {
            throw ToolArguments.invalid();
        }
        String[] glob = grep ? null : WorkspacePatterns.glob(ToolArguments.text(args, "pattern", 128));
        var regex = grep && ToolArguments.bool(args, "regex", false) ? WorkspacePatterns.regex(query) : null;
        Path start = directoryPath(args);
        int availableDepth = start.toString().isEmpty() ? 8 : 8 - start.getNameCount();
        Search search = new Search(results, new SecureWorkspace.Counter(), glob, query, regex);
        search.pathBudget =
                start.toString().isEmpty() ? 512 : 511 - start.toString().length();
        search.resultPrefix = start.toString().isEmpty() ? "" : start + "/";
        try (var directory = workspace.directory(start)) {
            walk(directory, "", Math.min(depth, availableDepth), search, context);
        }
        if (!grep) {
            results.values.sort(java.util.Comparator.comparing(Object::toString));
        }
        return Map.of(
                grep ? "matches" : "paths",
                results.values,
                "truncated",
                results.truncated,
                "entries_scanned",
                search.counter.entries,
                "bytes_scanned",
                search.bytes);
    }

    private void walk(
            SecureDirectoryStream<Path> directory, String prefix, int depth, Search search, ToolContext context)
            throws IOException {
        if (depth == 0) {
            search.results.truncated = true;
            return;
        }
        for (Path name : SecureWorkspace.names(directory, search.counter, context)) {
            context.check();
            String relative = prefix + name;
            if (relative.length() > search.pathBudget) {
                search.results.truncated = true;
                continue;
            }
            BasicFileAttributes attributes = SecureWorkspace.attributes(directory, name);
            if (attributes.isDirectory()) {
                if (depth == 1) {
                    search.results.truncated = true;
                } else {
                    try (var child = directory.newDirectoryStream(name, LinkOption.NOFOLLOW_LINKS)) {
                        walk(child, relative + "/", depth - 1, search, context);
                    }
                }
            } else if (search.query == null) {
                if (WorkspacePatterns.globMatches(search.glob, relative)
                        && !search.results.add(search.resultPrefix + relative)) {
                    return;
                }
            } else {
                if (attributes.size() > 1_048_576 - search.bytes) {
                    search.results.truncated = true;
                    search.stopped = true;
                    return;
                }
                var file = SecureWorkspace.read(directory, name, context);
                search.bytes += file.bytes().length;
                if (search.bytes > 1_048_576) {
                    search.results.truncated = true;
                    search.stopped = true;
                    return;
                }
                String text = file.text();
                int number = 0;
                for (int start = 0; start < text.length(); ) {
                    context.check();
                    int end = text.indexOf('\n', start);
                    if (end < 0) {
                        end = text.length();
                    }
                    String line = text.substring(start, end);
                    if (line.endsWith("\r")) {
                        line = line.substring(0, line.length() - 1);
                    }
                    number++;
                    if (search.regex == null ? line.contains(search.query) : search.regex.matches(line, context)) {
                        int length = line.codePointCount(0, line.length());
                        String preview = line.substring(0, line.offsetByCodePoints(0, Math.min(512, length)));
                        if (!search.results.add(Map.of(
                                "path",
                                search.resultPrefix + relative,
                                "line",
                                number,
                                "text",
                                preview,
                                "line_truncated",
                                length > 512))) {
                            search.stopped = true;
                            return;
                        }
                    }
                    start = end + 1;
                }
            }
            if (search.stopped || search.results.full) {
                return;
            }
        }
    }

    private static Path filePath(JsonNode args) {
        return SecureWorkspace.path(ToolArguments.text(args, "path", 512), false);
    }

    private static Path directoryPath(JsonNode args) {
        return SecureWorkspace.path(ToolArguments.optionalText(args, "path", "", 512), true);
    }

    private static String expectedHash(JsonNode args, boolean required) {
        if (!required && !args.has("expected_hash")) {
            return null;
        }
        String value = ToolArguments.text(args, "expected_hash", 64);
        if (value.length() != 64 || !value.chars().allMatch(c -> (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f'))) {
            throw ToolArguments.invalid();
        }
        return value;
    }

    private Object command(JsonNode args, ToolContext context) throws IOException, InterruptedException {
        ToolArguments.only(args, "argv", "timeout_ms", "max_output_bytes");
        JsonNode argvNode = args.get("argv");
        if (argvNode == null || !argvNode.isArray() || argvNode.isEmpty() || argvNode.size() > 32) {
            throw ToolArguments.invalid();
        }
        List<String> argv = new ArrayList<>();
        int argumentBytes = 0;
        for (JsonNode value : argvNode) {
            var wrapper = ToolArguments.JSON.createObjectNode().set("value", value);
            String argument = ToolArguments.text(wrapper, "value", 4096);
            if (argument.indexOf('\0') >= 0
                    || (argumentBytes += argument.getBytes(StandardCharsets.UTF_8).length) > 16_384) {
                throw ToolArguments.invalid();
            }
            argv.add(argument);
        }
        String executable = argv.getFirst();
        if (executable.isEmpty()
                || executable.length() > 64
                || !executable
                        .chars()
                        .allMatch(c -> c >= 'a' && c <= 'z'
                                || c >= 'A' && c <= 'Z'
                                || c >= '0' && c <= '9'
                                || c == '_'
                                || c == '-'
                                || c == '.')
                || executable.equals(".")
                || executable.equals("..")) {
            throw ToolArguments.invalid();
        }
        Path program = null;
        for (String bin : List.of("/usr/bin", "/bin")) {
            Path candidate = Path.of(bin, executable);
            if (Files.isRegularFile(candidate) && Files.isExecutable(candidate)) {
                program = candidate;
                break;
            }
        }
        if (program == null) {
            throw SecureWorkspace.failure("Workspace command executable unavailable");
        }
        argv.set(0, program.toString());
        int timeout = ToolArguments.integer(args, "timeout_ms", 10_000, 1, 30_000);
        Output output = new Output(ToolArguments.integer(args, "max_output_bytes", 4096, 1, 8192));
        context.check();
        // ProcessBuilder cannot chdir through SecureDirectoryStream. The trusted supervisor owns root ancestors.
        if (!workspace.rootKey.equals(
                Files.readAttributes(workspace.rootPath, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS)
                        .fileKey())) {
            throw SecureWorkspace.failure("Workspace root changed");
        }
        ProcessBuilder builder = new ProcessBuilder(argv).directory(workspace.rootPath.toFile());
        builder.environment().clear();
        builder.environment()
                .putAll(Map.of(
                        "PATH",
                        "/usr/bin:/bin",
                        "HOME",
                        workspace.rootPath.toString(),
                        "TMPDIR",
                        workspace.rootPath.toString(),
                        "LANG",
                        "C",
                        "LC_ALL",
                        "C"));
        long deadline = System.nanoTime()
                + Math.min(
                        Duration.ofMillis(timeout).toNanos(),
                        context.remaining().toNanos());
        Process process = builder.start();
        Set<ProcessHandle> children = new LinkedHashSet<>();
        Thread stdout = null;
        Thread stderr = null;
        boolean timedOut = false;
        try {
            process.getOutputStream().close();
            stdout = Thread.ofVirtual().start(() -> output.drain(process.getInputStream(), output.stdout));
            stderr = Thread.ofVirtual().start(() -> output.drain(process.getErrorStream(), output.stderr));
            while (true) {
                context.check();
                try (var descendants = process.descendants()) {
                    descendants.limit(129).forEach(children::add);
                }
                if (children.size() > 128) {
                    throw SecureWorkspace.failure("Workspace process count limit exceeded");
                }
                if (output.limited.get() || output.failed.get()) {
                    break;
                }
                if (!process.isAlive() && !stdout.isAlive() && !stderr.isAlive()) {
                    break;
                }
                long remaining = deadline - System.nanoTime();
                if (remaining <= 0) {
                    timedOut = true;
                    break;
                }
                // Also wait for pipes after the direct child exits, without a busy loop or unbounded join.
                if (process.isAlive()) {
                    process.waitFor(Math.min(remaining, TimeUnit.MILLISECONDS.toNanos(10)), TimeUnit.NANOSECONDS);
                } else {
                    stdout.join(Duration.ofNanos(Math.min(remaining, TimeUnit.MILLISECONDS.toNanos(5))));
                    stderr.join(Duration.ofNanos(
                            Math.min(Math.max(1, deadline - System.nanoTime()), TimeUnit.MILLISECONDS.toNanos(5))));
                }
            }
        } finally {
            output.stopping.set(true);
            cleanup(process, children, stdout, stderr);
        }
        context.check();
        if (output.failed.get()) {
            throw SecureWorkspace.failure("Workspace command output unavailable");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        synchronized (output) {
            result.put("stdout", output.stdout.toString(StandardCharsets.UTF_8));
            result.put("stderr", output.stderr.toString(StandardCharsets.UTF_8));
        }
        result.put("exit_code", process.isAlive() ? null : process.exitValue());
        result.put("timed_out", timedOut);
        result.put("output_limited", output.limited.get());
        return result;
    }

    private static void cleanup(Process process, Set<ProcessHandle> children, Thread stdout, Thread stderr) {
        // Java cannot discover descendants which double-fork/reparent before a snapshot. OS supervision is required.
        try (var descendants = process.descendants()) {
            descendants.limit(129).forEach(children::add);
        }
        for (ProcessHandle child : children) {
            child.destroyForcibly();
        }
        process.destroyForcibly();
        try {
            process.waitFor(200, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (stdout != null) {
            stdout.interrupt();
        }
        if (stderr != null) {
            stderr.interrupt();
        }
        // Pipe close can wait behind a blocked reader if an undiscovered detached child holds the fd.
        // Do not let that delay the invocation indefinitely; the worker supervisor owns final reaping.
        Thread closer = Thread.ofVirtual().start(() -> {
            for (InputStream stream : List.of(process.getInputStream(), process.getErrorStream())) {
                try {
                    stream.close();
                } catch (IOException ignored) {
                    // Cleanup must not disclose command output or provider messages.
                }
            }
        });
        try {
            closer.join(Duration.ofMillis(100));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void close() {
        lease.lock.lock();
        try {
            if (!closed) {
                closed = true;
                try {
                    workspace.close();
                } catch (IOException e) {
                    throw SecureWorkspace.failure("Workspace close failed");
                } finally {
                    synchronized (LEASES) {
                        if (--lease.references == 0) {
                            LEASES.remove(identity);
                        }
                    }
                }
            }
        } finally {
            lease.lock.unlock();
        }
    }

    private record RootIdentity(Object fileSystem, Object fileKey) {}

    private static final class Lease {
        final Binding binding;
        final ReentrantLock lock = new ReentrantLock();
        int references;

        Lease(Binding binding) {
            this.binding = binding;
        }
    }

    private static final class Results {
        final List<Object> values = new ArrayList<>();
        final int limit;
        int bytes;
        boolean truncated;
        boolean full;

        Results(int limit) {
            this.limit = limit;
        }

        boolean add(Object value) {
            int size = ToolArguments.result(value).getBytes(StandardCharsets.UTF_8).length + 1;
            if (values.size() == limit || size > RESULT_BYTES - bytes) {
                truncated = true;
                full = true;
                return false;
            }
            values.add(value);
            bytes += size;
            return true;
        }
    }

    private static final class Search {
        final Results results;
        final SecureWorkspace.Counter counter;
        final String[] glob;
        final String query;
        final WorkspacePatterns.LinearRegex regex;
        int bytes;
        int pathBudget;
        String resultPrefix;
        boolean stopped;

        Search(
                Results results,
                SecureWorkspace.Counter counter,
                String[] glob,
                String query,
                WorkspacePatterns.LinearRegex regex) {
            this.results = results;
            this.counter = counter;
            this.glob = glob;
            this.query = query;
            this.regex = regex;
        }
    }

    private static final class Output {
        final ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        final ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        final AtomicBoolean limited = new AtomicBoolean();
        final AtomicBoolean failed = new AtomicBoolean();
        final AtomicBoolean stopping = new AtomicBoolean();
        int remaining;

        Output(int remaining) {
            this.remaining = remaining;
        }

        void drain(InputStream stream, ByteArrayOutputStream target) {
            byte[] buffer = new byte[1024];
            try {
                int count;
                while ((count = stream.read(buffer)) >= 0) {
                    synchronized (this) {
                        int accepted = Math.min(count, remaining);
                        target.write(buffer, 0, accepted);
                        remaining -= accepted;
                        if (accepted < count) {
                            limited.set(true);
                            return;
                        }
                    }
                }
            } catch (IOException e) {
                // Closing a pipe during deliberate termination is not a command error.
                if (!stopping.get() && !Thread.currentThread().isInterrupted()) {
                    failed.set(true);
                }
            }
        }
    }
}
