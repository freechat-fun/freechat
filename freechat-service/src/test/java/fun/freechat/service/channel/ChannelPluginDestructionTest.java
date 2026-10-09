package fun.freechat.service.channel;

import static org.junit.jupiter.api.Assertions.*;

import fun.freechat.channels.spi.*;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class ChannelPluginDestructionTest {
    @Test
    void componentStyleRegistrationHasExactlyOneDestructionOwner() throws Exception {
        CountingPlugin plugin = new CountingPlugin(false);
        try (var context = new AnnotationConfigApplicationContext()) {
            context.registerBean(CountingPlugin.class, () -> plugin);
            context.register(ChannelRegistry.class, ChannelRuntime.class);
            context.refresh();
            assertTrue(plugin.entered.await(5, TimeUnit.SECONDS));
        }
        assertTrue(plugin.closed.await(5, TimeUnit.SECONDS));
        assertEquals(1, plugin.closeCount.get());
    }

    @Test
    void beanMethodDefaultDestroyInferenceIsDisabledByTheRuntime() throws Exception {
        CountingPlugin plugin;
        try (var context = new AnnotationConfigApplicationContext()) {
            context.register(PluginConfiguration.class, ChannelRegistry.class, ChannelRuntime.class);
            context.refresh();
            plugin = context.getBean(CountingPlugin.class);
            assertTrue(plugin.entered.await(5, TimeUnit.SECONDS));
        }
        assertEquals(1, plugin.closeCount.get());
    }

    @Test
    void springDoesNotBypassBoundedShutdownWhilePluginStartupIsStillActive() throws Exception {
        CountingPlugin plugin = new CountingPlugin(true);
        var context = new AnnotationConfigApplicationContext();
        try (var closer = Executors.newVirtualThreadPerTaskExecutor()) {
            context.registerBean(CountingPlugin.class, () -> plugin);
            context.register(ChannelRegistry.class, ChannelRuntime.class);
            context.refresh();
            assertTrue(plugin.entered.await(5, TimeUnit.SECONDS));
            closer.submit(context::close).get(2, TimeUnit.SECONDS);
            assertEquals(0, plugin.closeCount.get());
            plugin.allowStartup.countDown();
            assertTrue(plugin.closed.await(5, TimeUnit.SECONDS));
            assertEquals(1, plugin.closeCount.get());
            assertFalse(plugin.concurrentClose);
        } finally {
            plugin.allowStartup.countDown();
            context.close();
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class PluginConfiguration {
        @Bean
        CountingPlugin plugin() {
            return new CountingPlugin(false);
        }
    }

    static final class CountingPlugin implements ChannelPlugin<String> {
        final CountDownLatch entered = new CountDownLatch(1);
        final CountDownLatch allowStartup;
        final CountDownLatch closed = new CountDownLatch(1);
        final AtomicInteger closeCount = new AtomicInteger();
        volatile boolean startupFinished;
        volatile boolean concurrentClose;

        CountingPlugin(boolean blockStartup) {
            allowStartup = new CountDownLatch(blockStartup ? 1 : 0);
        }

        public String id() {
            return "destruction-test";
        }

        public ChannelTransport transport() {
            return (address, text) -> new ChannelReceipt("1");
        }

        public ChannelInboundHandler<String> inboundHandler() {
            return (envelope, turn) -> CompletableFuture.completedFuture(null);
        }

        public ChannelPolicy policy() {
            return new ChannelPolicy(
                    1, 1, 2, 2, 2, 2, 1, Duration.ofSeconds(1), Duration.ofSeconds(1), 1, Duration.ofMillis(50));
        }

        public void start(ChannelRuntimeContext<String> runtime) throws InterruptedException {
            entered.countDown();
            assertTrue(allowStartup.await(5, TimeUnit.SECONDS));
            startupFinished = true;
        }

        public void stopReceiving() {}

        public void close() {
            concurrentClose |= !startupFinished;
            closeCount.incrementAndGet();
            closed.countDown();
        }
    }
}
