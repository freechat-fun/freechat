package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;

import fun.freechat.channels.spi.ChannelPlugin;
import fun.freechat.service.channel.ChannelRegistry;
import fun.freechat.service.channel.ChannelRuntime;
import java.io.ByteArrayOutputStream;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import javax.tools.ToolProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.annotation.ImportCandidates;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AliasFor;

class ExternalChannelPluginTest {
    @TempDir
    Path temporary;

    @Test
    void separatelyCompiledJarIsDiscoveredFromBootMetadataAndUsesTheHostRuntime() throws Exception {
        String name = "external.sample.ExternalChannelConfiguration";
        Path source = temporary.resolve("ExternalChannelConfiguration.java");
        Path classes = Files.createDirectory(temporary.resolve("classes"));
        Files.writeString(source, SOURCE);
        String classpath = List.of(
                        ChannelPlugin.class, AutoConfiguration.class, Bean.class, AliasFor.class, BeanDefinition.class)
                .stream()
                .map(type -> Path.of(type.getProtectionDomain()
                                .getCodeSource()
                                .getLocation()
                                .getPath())
                        .toString())
                .distinct()
                .collect(Collectors.joining(java.io.File.pathSeparator));
        ByteArrayOutputStream diagnostics = new ByteArrayOutputStream();
        assertEquals(
                0,
                ToolProvider.getSystemJavaCompiler()
                        .run(
                                null,
                                diagnostics,
                                diagnostics,
                                "--release",
                                "25",
                                "-classpath",
                                classpath,
                                "-d",
                                classes.toString(),
                                source.toString()),
                diagnostics.toString(StandardCharsets.UTF_8));
        Path jar = temporary.resolve("external-channel.jar");
        try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(jar));
                var paths = Files.walk(classes)) {
            for (Path path : paths.filter(Files::isRegularFile).toList()) {
                output.putNextEntry(
                        new JarEntry(classes.relativize(path).toString().replace('\\', '/')));
                Files.copy(path, output);
                output.closeEntry();
            }
            output.putNextEntry(
                    new JarEntry("META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports"));
            output.write((name + "\n").getBytes(StandardCharsets.UTF_8));
            output.closeEntry();
        }
        try (URLClassLoader loader = new URLClassLoader(
                new java.net.URL[] {jar.toUri().toURL()}, getClass().getClassLoader())) {
            assertTrue(ImportCandidates.load(AutoConfiguration.class, loader)
                    .getCandidates()
                    .contains(name));
            Class<?> configuration = Class.forName(name, true, loader);
            assertSame(loader, configuration.getClassLoader());
            ClassLoader previous = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(loader);
                new ApplicationContextRunner()
                        .withClassLoader(loader)
                        .withUserConfiguration(ChannelRegistry.class, ChannelRuntime.class)
                        .withConfiguration(AutoConfigurations.of(configuration))
                        .run(context -> {
                            assertNull(context.getStartupFailure());
                            Object plugin = context.getBean("externalChannelPlugin");
                            assertSame(loader, plugin.getClass().getClassLoader());
                            ((CompletableFuture<?>)
                                            plugin.getClass().getMethod("ready").invoke(plugin))
                                    .get(5, TimeUnit.SECONDS);
                            ((CompletableFuture<?>) plugin.getClass()
                                            .getMethod("receive", String.class)
                                            .invoke(plugin, "hello"))
                                    .get(5, TimeUnit.SECONDS);
                            assertEquals(
                                    "hello", plugin.getClass().getMethod("last").invoke(plugin));
                            assertTrue(context.getBean(ChannelRegistry.class)
                                    .find("external-jar")
                                    .isPresent());
                        });
            } finally {
                Thread.currentThread().setContextClassLoader(previous);
            }
        }
    }

    private static final String SOURCE = """
            package external.sample;
            import fun.freechat.channels.spi.*;
            import java.util.concurrent.CompletableFuture;
            import java.util.concurrent.ConcurrentLinkedQueue;
            import org.springframework.boot.autoconfigure.AutoConfiguration;
            import org.springframework.context.annotation.Bean;
            @AutoConfiguration
            public class ExternalChannelConfiguration {
                @Bean
                public ChannelPlugin<String> externalChannelPlugin() { return new Plugin(); }
                public static final class Plugin implements ChannelPlugin<String> {
                    private volatile ChannelInstance<String> instance;
                    private final CompletableFuture<Void> started = new CompletableFuture<>();
                    private final ConcurrentLinkedQueue<String> sent = new ConcurrentLinkedQueue<>();
                    public String id() { return "external-jar"; }
                    public ChannelTransport transport() {
                        return (address, text) -> { sent.add(text.text()); return new ChannelReceipt("1"); };
                    }
                    public ChannelInboundHandler<String> inboundHandler() {
                        return (envelope, turn) -> turn.outbound().sendText(ChannelText.plain(envelope.payload()))
                                .thenApply(receipt -> null);
                    }
                    public void start(ChannelRuntimeContext<String> runtime) {
                        instance = runtime.openInstance("account"); started.complete(null);
                    }
                    public CompletableFuture<Void> ready() { return started.copy(); }
                    public CompletableFuture<Void> receive(String text) { return instance.receive("room", null, text); }
                    public String last() { return sent.peek(); }
                    public void stopReceiving() { if (instance != null) instance.close(); }
                    public void close() { stopReceiving(); }
                }
            }
            """;
}
