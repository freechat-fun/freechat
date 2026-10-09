package example.channels;

import static org.junit.jupiter.api.Assertions.*;

import fun.freechat.channels.spi.ChannelMessageEditor;
import fun.freechat.service.channel.ChannelRegistry;
import fun.freechat.service.channel.ChannelRuntime;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class DemoChannelPluginTest {
    @Test
    void anExplicitBeanOutsideApplicationPackagesUsesTheSharedRuntime() throws Exception {
        try (var context = new AnnotationConfigApplicationContext()) {
            context.registerBean(DemoChannelPlugin.class);
            context.register(ChannelRegistry.class, ChannelRuntime.class);
            context.refresh();
            DemoChannelPlugin plugin = context.getBean(DemoChannelPlugin.class);
            plugin.ready().get(5, TimeUnit.SECONDS);
            plugin.receive("conversation-1", "Hello").get(5, TimeUnit.SECONDS);
            assertEquals(
                    new DemoChannelPlugin.SentMessage("conversation-1", "You said: Hello"),
                    plugin.sentMessages().getFirst());
            assertTrue(context.getBean(ChannelRegistry.class).find("demo").isPresent());
            assertFalse(ChannelMessageEditor.class.isInstance(plugin.transport()));
        }
    }

    @Test
    void stoppedSourcesRejectNewMessages() throws Exception {
        try (var context = new AnnotationConfigApplicationContext()) {
            context.registerBean(DemoChannelPlugin.class);
            context.register(ChannelRegistry.class, ChannelRuntime.class);
            context.refresh();
            DemoChannelPlugin plugin = context.getBean(DemoChannelPlugin.class);
            plugin.ready().get(5, TimeUnit.SECONDS);
            plugin.stopReceiving();
            assertThrows(
                    java.util.concurrent.ExecutionException.class,
                    () -> plugin.receive("conversation-1", "Hello").get(5, TimeUnit.SECONDS));
            assertTrue(plugin.sentMessages().isEmpty());
        }
    }
}
