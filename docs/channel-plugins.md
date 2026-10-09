# Build a messaging channel plugin

English | [中文版](channel-plugins.zh-CN.md)

A **channel** connects an external messaging service to a FreeChat character. A channel plugin is the Java adapter you write for that service; it is **not** FreeChat's OpenAPI tool-plugin feature. This guide starts with a working, offline echo plugin, then explains how to add a real provider and, optionally, FreeChat AI replies.

Plugins are **trusted Java application code**, constructed as Spring beans when the application starts. They are not sandboxed, hot-loaded, or installed by uploading untrusted JARs (Java archive files). Adding one requires building/deploying the application and restarting it. The SPI (service provider interface: the Java contracts an adapter implements) uses only JDK types. A provider SDK is its software development kit, usually the client library used to call its API. SDK classes belong in your adapter, not in the shared runtime.

Use the current source with its Java 25 release target and a compatible Maven/JDK toolchain. Commands below run from the repository root. The [design document](channel-plugin-design.md) was completed before implementation and explains the architectural decisions; its historical implementation survey is not a map of the current API.

## 1. Run your first plugin

You do not need a bot token, database, Redis, model API key, or running FreeChat server for this example. Maven may need to download build dependencies, but the test itself performs no network access.

The complete, executable files are:

- [DemoChannelPlugin.java](../freechat-service/src/test/java/example/channels/DemoChannelPlugin.java)
- [DemoChannelPluginTest.java](../freechat-service/src/test/java/example/channels/DemoChannelPluginTest.java)

Run exactly:

```shell
mvn -B -pl freechat-service -am test -Dtest=DemoChannelPluginTest -Dsurefire.failIfNoSpecifiedTests=false
```

`-pl` selects the service module; `-am` also builds its dependencies. The last option permits those dependency modules to have no test with that name.

Here is the plugin. It is deliberately an **in-process echo with an in-memory outbox**, not an instant-messaging (IM) adapter or a production inbox. The outbox is unbounded test instrumentation; do not reuse it as message storage in production.

```java
package example.channels;

import fun.freechat.channels.spi.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public final class DemoChannelPlugin implements ChannelPlugin<String> {
    public record SentMessage(String conversationId, String text) {}

    private final ConcurrentLinkedQueue<SentMessage> sent = new ConcurrentLinkedQueue<>();
    private final AtomicLong messageIds = new AtomicLong();
    private final CompletableFuture<Void> ready = new CompletableFuture<>();
    private volatile ChannelInstance<String> instance;

    @Override
    public String id() {
        return "demo";
    }

    @Override
    public ChannelTransport transport() {
        return (address, text) -> {
            sent.add(new SentMessage(address.conversationId(), text.text()));
            return new ChannelReceipt(Long.toString(messageIds.incrementAndGet()));
        };
    }

    @Override
    public ChannelInboundHandler<String> inboundHandler() {
        return (envelope, turn) -> turn.outbound()
                .sendText(ChannelText.plain("You said: " + envelope.payload()))
                .thenApply(receipt -> null);
    }

    @Override
    public void start(ChannelRuntimeContext<String> runtime) {
        instance = runtime.openInstance("demo-account");
        ready.complete(null);
    }

    public CompletableFuture<Void> ready() {
        return ready.copy();
    }

    public CompletableFuture<Void> receive(String conversationId, String text) {
        if (text == null || text.length() > 4096) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid demo message"));
        }
        ChannelInstance<String> current = instance;
        if (current == null) {
            return CompletableFuture.failedFuture(new ChannelFailure(ChannelFailure.Kind.CANCELLED));
        }
        return current.receive(conversationId, null, text);
    }

    public List<SentMessage> sentMessages() {
        return List.copyOf(sent);
    }

    @Override
    public void stopReceiving() {
        ChannelInstance<String> current = instance;
        if (current != null) {
            current.close();
        }
    }

    @Override
    public void close() {
        stopReceiving();
    }
}
```

A **bean** is an object created and managed by Spring. The test explicitly registers this bean, even though `example.channels` is outside FreeChat's normal `fun.freechat` component scan. Its essential setup is:

```java
try (var context = new AnnotationConfigApplicationContext()) {
    context.registerBean(DemoChannelPlugin.class);
    context.register(ChannelRegistry.class, ChannelRuntime.class);
    context.refresh();
    DemoChannelPlugin plugin = context.getBean(DemoChannelPlugin.class);
    plugin.ready().get(5, TimeUnit.SECONDS);
    plugin.receive("conversation-1", "Hello").get(5, TimeUnit.SECONDS);
    assertEquals(new DemoChannelPlugin.SentMessage("conversation-1", "You said: Hello"),
            plugin.sentMessages().getFirst());
}
```

See the linked test for imports and the additional discovery, text-only capability, and stopped-source assertions. Registering the registry/runtime manually is only necessary for this tiny standalone test context, not for the normal FreeChat application.

### Follow one message through the example

Spring discovers `ChannelPlugin` beans. `ChannelRegistry` checks their IDs and required methods before provider startup; `ChannelRuntime` then starts them asynchronously. Constructors should only assemble dependencies, not connect to the provider or depend on the registry/runtime themselves.

`start(runtime)` opens `demo-account` and receives a `ChannelInstance<String>`. This is a handle for **one activation generation**: a particular lifetime of that account, not simply its name. The demo's `ready()` is its own test convenience, not an SPI method.

The test calls `receive("conversation-1", "Hello")`. A real SDK would invoke a **callback**, meaning a function you registered to receive an event later. That callback validates the event and calls the current instance's `receive(conversationId, eventId, payload)`; it must not perform binding, call the model, or send synchronously. The demo supplies `null` for the optional provider event ID.

The runtime creates an envelope—a small object holding the address, event ID, and payload—and schedules the handler for that conversation. The `String` payload is opaque to the runtime: it knows where to route it, not what the text means. A real plugin could implement `ChannelPlugin<YourSdkEvent>` without converting away provider-specific information.

The handler uses `turn.outbound().sendText(...)`. This **schedules** a send; it does not call the provider on the receiving thread. The separate send worker calls the raw transport once, which here only appends to the test outbox. A `ChannelReceipt` carries the provider message ID; it means acceptance, not delivery to a device or a read receipt.

A **future** represents a result available later; `CompletionStage` lets you chain work on that result. `thenApply(receipt -> null)` converts the successful send result into the handler's `Void` result. Returning this chain keeps the conversation occupied until delivery finishes. Always return the full chain: the runtime tracks physical sends separately, but cannot discover arbitrary background database or model work that your handler starts and forgets. The test waits with a finite timeout because it needs to assert the final result; a provider callback must not wait this way.

Closing the Spring context stops receiving, cancels/drains runtime work, and closes plugin resources. `stopReceiving()` and `close()` must be **idempotent**: repeating them must not repeat destructive effects or break cleanup, including after partial startup failure. The runtime configures Spring not to call plugin `close()` independently during bean destruction. Keep cleanup in the plugin lifecycle methods; do not add a separate `@PreDestroy` or `DisposableBean` hook that closes the same resources ahead of the runtime.

## 2. Replace the echo with real ingress and transport

Keep the routing and scheduling structure. Replace the outbox lambda with a provider SDK client, and replace the test's `receive()` calls with authenticated SDK callbacks or an HTTP ingress adapter. Keep the SDK client's network timeouts finite, and avoid SDK-level automatic retries that could duplicate a request the runtime considers a single attempt.

A **polling** receiver asks the provider for new events, often holding a long-poll request open. A **webhook** receiver lets the provider POST events to your server. In either case, validate the event before submission, then return promptly to the provider. Use a captured generation handle in the callback, not a mutable lookup that could redirect an old event into a newly activated account.

### Keep identifiers separate

A **backend** is FreeChat's saved character configuration: model, prompt, memory settings, and credentials. A **binding** is a mapping from an authenticated external identity/conversation to a permitted FreeChat account and chat. Neither is an external conversation ID.

| Value | Example meaning |
| --- | --- |
| `ChannelAddress.pluginId()` | Adapter type, such as `demo` or `telegram`. IDs match `[a-z][a-z0-9-]{0,63}` and must be unique. |
| `ChannelAddress.instanceId()` | A bot/channel account within that plugin; nonblank, at most 256 characters. Not necessarily a FreeChat user or backend. |
| `ChannelAddress.conversationId()` | The provider's private chat or group ID; nonblank, at most 1,024 characters. |
| `ChannelEnvelope.eventId()` | Optional provider event ID, useful for plugin-owned deduplication. |
| `ChannelEnvelope.payload()` | The typed provider event, not inspected by core scheduling. |
| FreeChat `chatId` / `backendId` / account ID | Internal identity and authorization concepts; obtain them through a secure binding, never by reinterpreting the values above. |

The address stores independent fields, not a delimiter-concatenated key. An event ID does **not** activate automatic deduplication, durable storage, or exactly-once processing.

### The small required API

`ChannelPlugin<E>` supplies `id()`, `transport()`, `inboundHandler()`, optional `policy()` (defaults are provided), `start(runtime)`, `stopReceiving()`, and `close()`.

`ChannelRuntimeContext<E>` supplies `openInstance(instanceId)`. The resulting `ChannelInstance<E>` has `id()`, `isActive()`, `receive(conversationId, eventId, payload)`, `outbound(conversationId)`, and `close()`. Close the old handle before reopening the same instance ID: `openInstance` does not silently replace an active instance. Old callbacks remain invalid even after a new handle with that ID exists.

`ChannelInboundHandler<E>.handle(envelope, turn)` returns `CompletionStage<Void>` for the **whole turn**, including commands/binding, model settlement if used, final delivery, and any required recording. Inside it, `ChannelTurnContext` provides `address()`, `outbound()`, `deadline()`, `isCancelled()`, `onCancel(action)`, `schedule(action, delay)`, and `typing(interval)`. Prefer turn-scoped outbound for replies; instance-scoped outbound is for work not belonging to a turn, such as a best-effort busy notice, and is not protected by a turn's deadline.

`ChannelTransport.sendText(ChannelAddress, ChannelText)` is a raw, synchronous **single attempt**, returning `ChannelReceipt` or throwing a sanitized `ChannelFailure`. Plain text is required; `ChannelText.Format.MARKDOWN` is an optional rendering request, not a promise every provider understands the same Markdown dialect. Do not call raw transport from handlers, timers, or model callbacks. Conversely, a raw transport must never recursively enqueue sends through `ChannelOutbound`: waiting for a send queued behind itself can deadlock the destination.

### Webhook acceptance is a protocol decision

Before `receive`, verify the provider's signature or secret token, timestamp/replay requirements, account scope, request size, payload structure, text/media limits, and allowed event types. Bound parsing and downloads too. Keep endpoints scoped and authenticated; a public endpoint accepting arbitrary `chatId` and text is not an integration.

The returned receive future reports the **entire turn**, not a distinct durable admission receipt. Queue rejection can be immediate; a later failure can occur after the event was admitted. Do not wait for a large language model (LLM) response on a provider HTTP callback thread, or translate every successful method return into “durably accepted.” Return HTTP success only according to that provider's acknowledgment semantics and your actual acceptance/persistence design. If reliable acknowledgment requires a durable inbox, design that separately, including deduplication and recovery.

The runtime uses bounded, in-memory queues. Process loss can lose admitted work. A provider may redeliver; it may also **not** replay an admitted or rejected event—Telegram's polling receiver may already have advanced its update acknowledgment. A busy reply is best effort and can itself be rejected. There is no durable inbox or exactly-once guarantee here.

## 3. Make Spring discover your plugin

### Source inside the application

Place production source in a module included by the application, for example under `freechat-start/src/main/java/fun/freechat/channels/yourprovider/`. Annotate your plugin with `@Component`, or expose it with `@Bean` in a configuration that FreeChat scans/imports. Choose one registration path to avoid duplicate IDs.

For example, after copying the demo class into a **main source set** (its existing test class is not shipped), this explicit configuration registers it without changing the application-wide package scan:

```java
package fun.freechat.channels.demo;

import example.channels.DemoChannelPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class DemoChannelConfiguration {
    @Bean
    DemoChannelPlugin demoChannelPlugin() {
        return new DemoChannelPlugin();
    }
}
```

This makes the bean discoverable; it does not create a real ingress endpoint. Your production plugin still needs startup/session management and provider authentication.

### An external dependency JAR

Use an ordinary library JAR on the application's build/runtime classpath—the set of classes and libraries available to the Java application. Compile against the **matching source revision** of `fun.freechat:freechat-common`, plus `fun.freechat:freechat-service` if you use the AI bridge/runtime helpers. Do not compile against the executable `freechat-start` fat JAR: its `BOOT-INF/classes` layout is not a normal library classpath. `freechat-sdk` is the REST client, not the channel SPI.

Do not assume these internal artifacts are published. Build/install them from the same checkout into your local Maven repository, or publish matching builds to a repository your organization controls:

```shell
mvn -B -pl freechat-service -am install -DskipTests
```

The repository uses `${revision}` in parent POM versions. For a standalone Maven consumer, also verify that the installed/published POMs resolve their parent and transitive dependency versions. A successful reactor install (Maven's multi-module build) or a copied JAR alone does not establish that. If the consumer reports an unresolved `${revision}` parent, build against the matching source reactor or use your organization's publication process to provide resolved/flattened POMs; do not substitute a different FreeChat version or the executable JAR.

An external plugin's POM can contain the following fragment (use the versions of the host you actually build; these match the current root POM). `provided` means the deployed FreeChat application supplies these libraries; your plugin still packages its own required SDK dependencies through the host build.

```xml
<properties>
    <freechat.version>2.10.0-beta</freechat.version>
    <spring-boot.version>4.1.1</spring-boot.version>
    <maven.compiler.release>25</maven.compiler.release>
</properties>
<dependencies>
    <dependency>
        <groupId>fun.freechat</groupId>
        <artifactId>freechat-common</artifactId>
        <version>${freechat.version}</version>
        <scope>provided</scope>
    </dependency>
    <!-- Include this when using ChannelChatBridge or service helpers. -->
    <dependency>
        <groupId>fun.freechat</groupId>
        <artifactId>freechat-service</artifactId>
        <version>${freechat.version}</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-autoconfigure</artifactId>
        <version>${spring-boot.version}</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

For automatic bean registration from outside `fun.freechat`, put this configuration in your library's `src/main/java/example/channels/DemoChannelAutoConfiguration.java`, alongside the demo copied into main sources:

```java
package example.channels;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class DemoChannelAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(DemoChannelPlugin.class)
    DemoChannelPlugin demoChannelPlugin() {
        return new DemoChannelPlugin();
    }
}
```

Create `src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` in that library containing:

```text
example.channels.DemoChannelAutoConfiguration
```

Add the resulting library as an ordinary Maven dependency of the host, rebuild, and restart. Alternatively, explicitly import its configuration with Spring's `@Import`. Neither placing an arbitrary JAR beside the application nor adding a Java `ServiceLoader` descriptor registers a plugin here. Do not combine the demo's explicit configuration with its auto-configuration unnecessarily; a conditional bean does not resolve different plugin classes claiming the same ID.

## 4. Connect an authorized conversation to FreeChat AI

First get the echo working. It intentionally has no account-linking or AI logic, so it cannot accidentally pretend that an external username is a FreeChat user.

### Establish the security boundary before calling a service

Your plugin must own an authenticated account-link mapping in its own storage. For example, an already signed-in FreeChat account can approve a short-lived, one-use linking challenge, which is then proven by the verified provider identity. Scope the saved mapping to the plugin, bot account, external sender/conversation, and intended audience; store the real FreeChat account, permitted backend/chat, and revocation state. A provider signature proves which provider sent an event, **not** that the sender may use an arbitrary FreeChat account.

When the queued handler actually executes, resolve that binding and recheck account status, chat ownership, backend/character permissions, organization restrictions as applicable, and revocation. Reject mismatches and cancellation before invoking the model. Controller authorization annotations do not automatically run when a plugin calls `ChatService` directly.

An application may call its own design `AuthorizedBindings`, but **FreeChat does not supply a helper by that name**. Implement and review that authorization/storage layer yourself. The code below begins *after* those checks; it is not an HTTP endpoint or an authorization implementation.

Default account/backend conversations can be shared with web chat: `ChatService.start` reuses an existing account/backend chat, and `getDefaultChatId` can find it. Explain this to users before linking. A new external group must **never silently use an individual's private chat history or long-term memory**. A different external conversation ID alone does not isolate the underlying FreeChat memory. Refuse group linking unless your account, audience, backend, and memory isolation policy explicitly supports it.

Existing long-term memory (LTM) recognizes real FreeChat accounts and the deployed Telegram identity model. Do not fabricate a `User`, fill Telegram fields for a different provider, or invent `plugin-*` user IDs and expect LTM to work. A plugin can instead use its own engine and storage with the same runtime; that is not automatic integration with FreeChat LTM.

### Use the production final-text bridge

[ChannelChatBridge](../freechat-service/src/main/java/fun/freechat/service/channel/ChannelChatBridge.java) is a Spring service. Inject it into your plugin/handler and return its future as part of the handler's completion. Here is a small method to place in your handler class:

```java
// Imports: dev.langchain4j.data.message.UserMessage;
// fun.freechat.channels.spi.ChannelTurnContext;
// fun.freechat.service.channel.ChannelChatBridge;
// java.util.concurrent.CompletableFuture;

static CompletableFuture<Void> replyAfterAuthorization(
        ChannelChatBridge bridge,
        String authorizedChatId,
        String verifiedText,
        ChannelTurnContext turn) {
    // Caller has just revalidated the stored binding and permissions for this turn.
    return bridge.reply(authorizedChatId, UserMessage.from(verifiedText), turn, 4000);
}
```

The actual signature is `reply(String authorizedChatId, ChatMessage message, ChannelTurnContext turn, int messageLimit)`, returning `CompletableFuture<Void>`. `UserMessage` above is a model input message, **not** a fabricated account `User`. Choose the limit for your provider: the accepted range is 2–65,536 UTF-16 code units, and the example value is not a universal provider limit. Providers may count bytes, code points, or formatting differently.

The bridge buffers at most **65,536 UTF-16 code units**, cancels/fails on overflow rather than silently truncating, and sends the final answer as sequential plain-text segments without splitting a surrogate pair (the two Java `char` values used for some Unicode characters). It can show typing at four-second intervals when supported. It does **not** progressively edit a placeholder, extract generated images, or turn incoming audio/images into model input automatically. Empty text produces no text send. It observes `ChatStreamHandle.ready()`, starts the stream, and completes only after both service settlement and final delivery; delivery errors do not rerun the LLM. See [ChannelChatBridgeTest](../freechat-service/src/test/java/fun/freechat/service/channel/ChannelChatBridgeTest.java) for executable boundary examples.

### Advanced: manage a stream yourself

Use this only if you need provider-specific presentation such as coalesced partial edits. `ChatService.streamSendManaged(chatId, message, context)` returns a [ChatStreamHandle](../freechat-service/src/main/java/fun/freechat/service/chat/ChatStreamHandle.java):

- `ready()` completes when a `TokenStream` is constructed; it is not response completion. A null stream is possible.
- `settled()` completes after service cleanup and release of chat coordination, including cancellation paths.
- `cancel()` requests cancellation of the owning chat task, even before construction finishes.

Some model streams have no effective cancellation API. If one has already started, FreeChat retains chat coordination until its natural terminal callback and cleanup finish; a deadline cannot safely turn it into a finished stream. A provider that never terminates can therefore keep that conversation occupied, which is safer than allowing two turns to modify the same memory concurrently.

Immediately register `turn.onCancel(handle::cancel)` **before observing `ready()` or starting the stream**. In the ready callback, handle failure/null, check cancellation again, attach partial/completion/error handlers, then call `start()`. Callbacks should update bounded state and enqueue outbound work, not do network I/O, blocking database work, or wait for it. Make finalization idempotent; a repeated terminal callback must not send another image or record another reply.

Return a future combining `handle.settled()` and your final delivery/recording future, for example with `CompletableFuture.allOf(...)`. **Never `join()`/`get()` settlement inside the stream's own callbacks**: cleanup waits for those callbacks to return. Do not nest `ChatService.streamSend` inside another task on the same chat queue; it can wait for itself. The existing chat queue remains responsible for model/memory coordination; the channel runtime does not replace it.

## 5. Add only the capabilities your provider supports

A **capability** is an optional feature, such as editing or media. A text-only transport is sufficient. The transport object may also implement:

| Interface | Methods |
| --- | --- |
| `ChannelMediaTransport` | `Set<ChannelMedia.Kind> mediaKinds()` and `sendMedia(address, media)` returning `ChannelReceipt`. Kinds: `IMAGE`, `VOICE`, `VIDEO`, `AUDIO`, `DOCUMENT`. |
| `ChannelMessageEditor` | `void editText(address, messageId, text)`. |
| `ChannelStatusTransport` | `void sendTyping(address)`. This is transient activity, not a read/delivery receipt. |
| `ChannelInvitationProvider` | `username(instanceId)` and `invitationLink(instanceId)` for account metadata. Telegram implements this on its transport; callers check the actual provider object. |

A plugin that depends on backend configuration can separately implement `ChannelBackendListener.backendChanged(backendId)` and `reconcile()`. Invitation metadata and backend notifications are not send operations; don't expect every plugin to have them.

Application code uses scheduled `ChannelOutbound` methods. For example, this method in a handler supports an optional placeholder edit, falling back to one final text message when editing is absent:

```java
static CompletableFuture<Void> present(ChannelTurnContext turn, String answer) {
    ChannelOutbound out = turn.outbound();
    if (!out.supports(ChannelMessageEditor.class)) {
        return out.sendText(ChannelText.plain(answer)).thenApply(receipt -> null);
    }
    return out.sendText(ChannelText.plain("Working..."))
            .thenCompose(receipt -> out.editText(receipt.messageId(), ChannelText.plain(answer)));
}
```

Use SPI imports and `CompletableFuture`. This demonstrates capability selection, not a progressive streaming renderer. Never treat an ambiguous failed edit/send as permission to send a fresh duplicate.

For an image, the transport must advertise both the media interface and that particular kind. The following method can live on your plugin; `transport()` is its configured transport, used here only for capability inspection:

```java
CompletableFuture<ChannelReceipt> sendImage(ChannelTurnContext turn, java.nio.file.Path image) {
    if (!(transport() instanceof ChannelMediaTransport media)
            || !media.mediaKinds().contains(ChannelMedia.Kind.IMAGE)) {
        return turn.outbound().sendText(ChannelText.plain("Images are not supported here."));
    }
    return turn.outbound().sendMedia(ChannelMedia.file(ChannelMedia.Kind.IMAGE, image, "Result"));
}
```

`outbound.supports(ChannelMediaTransport.class)` checks interface support, not individual kinds. Unsupported explicit sends/edits/status operations return failed futures with `UNSUPPORTED`; they do not silently vanish.

`ChannelMedia.Reference` can hold a provider file ID **or** a URL, interpreted by that adapter/SDK. For example, `ChannelMedia.reference(IMAGE, providerFileId, "Result")` uses no core downloader. `ChannelMedia.file(...)` creates an `Upload` whose opener reopens the file. A custom `new ChannelMedia.Upload("report.txt", () -> Files.newInputStream(path))` must produce a **fresh stream for every attempt**, not return a captured, already-consumed stream. Keep the resource available through settlement and use safe paths.

The raw adapter owns opening and closing each upload stream, including failed attempts, after the SDK has consumed it. A close failure after confirmed acceptance must not turn success into a retry that duplicates the message. The runtime does not itself close arbitrary provider resources or download references. Validate URL/file access and media limits as described below.

For typing, check `turn.outbound().supports(ChannelStatusTransport.class)` if you need to branch; `turn.typing(Duration.ofSeconds(4))` already returns a no-op handle if unsupported. Close that `AutoCloseable` when your asynchronous reply becomes terminal, not immediately after starting a future in a try-with-resources block. Turn completion/cancellation also stops timers. The lower-level `outbound.sendTyping(BooleanSupplier stillNeeded)` tests the supplier before execution. Typing is best effort, coalesced, and not retried by delivery; failure must not fail the answer.

## 6. Understand ordering, limits, and shutdown

### FIFO and backpressure in practice

**FIFO** means first in, first out. Within one external address (plugin, account, conversation), the next received event waits for the previous handler's **full completion**, not merely its return from Java code or the model's last token. Thus commands and binding changes stay in the same local order. A second conversation can run concurrently, and plugins have separate execution capacity.

Sends have their own FIFO per destination, including retries. Receive and send capacities are separate, so a receiving turn awaiting delivery does not consume the send execution slots needed to finish it. Java virtual threads make waiting cheaper; they do not remove memory, network, or provider rate limits. The runtime admits bounded work before creating workers.

This is **local admission order**, not a global total order across application replicas or independent provider callbacks. FreeChat's existing chat-service coordination protects shared model/memory work across instances; it cannot make external messaging exactly once.

**Backpressure** means refusing excess work rather than accumulating it indefinitely. Current [ChannelPolicy.defaults()](../freechat-common/src/main/java/fun/freechat/channels/spi/ChannelPolicy.java) values are:

| Setting | Default |
| --- | --- |
| `receiveConcurrency` / `sendConcurrency` | 128 active receive turns / 32 active sends per plugin |
| `receiveCapacity` / `sendCapacity` | 4,096 each per plugin, including active work |
| `conversationReceiveCapacity` / `conversationSendCapacity` | 32 / 64 per external address, including active work |
| `maxInstances` | 1,024 active account instances per plugin |
| `turnTimeout` | 10 minutes from submission/admission, including receive-queue waiting |
| `deliveryTimeout` / `deliveryAttempts` | 90 seconds overall / 3 total attempts, including the first |
| `shutdownTimeout` | 10 seconds |

Delivery time starts when the operation is submitted, so queue/retry waits count. It is not a replacement for an SDK network timeout and cannot physically stop an uncooperative in-flight call. Choose finite connect/read/call timeouts fitting your delivery/shutdown budget.

There are no universal YAML keys automatically configuring these settings. Your plugin's own validated configuration must build a `ChannelPolicy`, and your plugin must override `policy()` to return it. For example, this Java configuration value lowers receive concurrency while preserving the other defaults:

```java
static ChannelPolicy policyFor(int configuredReceiveConcurrency) {
    ChannelPolicy d = ChannelPolicy.defaults();
    return new ChannelPolicy(configuredReceiveConcurrency, d.sendConcurrency(),
            d.receiveCapacity(), d.sendCapacity(),
            d.conversationReceiveCapacity(), d.conversationSendCapacity(), d.maxInstances(),
            d.turnTimeout(), d.deliveryTimeout(), d.deliveryAttempts(), d.shutdownTimeout());
}
```

Pass `policyFor(8)` from your own Spring configuration into your plugin constructor, store it in a `ChannelPolicy configuredPolicy` field, and override the plugin method:

```java
@Override
public ChannelPolicy policy() {
    return configuredPolicy;
}
```

This is a deliberate extension to your plugin, not an existing demo constructor. If you bind external properties, define/document those keys in your plugin; merely declaring a `ChannelPolicy` bean does not configure every plugin. Policies are captured when the runtime is created, so deploy/restart after changing them.

A turn permits at most **8 active scheduled timers** and **16 cancellation hooks**. `turn.schedule` callbacks must only update small state or enqueue work; never run SDK I/O or blocking waits on the shared timer threads. Retry timers only wake/enqueue scheduled work; the send worker makes the attempt. Coalesce token updates so obsolete partial edits do not fill the lane ahead of the final answer.

### Cancellation is not physical settlement

`turn.deadline()` includes queue time. Register cancellation actions promptly and check `isCancelled()` before starting new work. Closing an instance invalidates that generation, cancels its queued/active turns and associated timers/retries, and rejects late callbacks. An old outbound handle cannot be reused for the next activation.

A cancelled public future is a **signal**, not proof that a model, socket, upload stream, or cleanup callback has stopped. A turn also waits for the private physical completion of its admitted sends, even if an observation future was cancelled. When owner cancellation races with an already running send, a late accepted receipt is still delivered to its normal completion chain; record it rather than pretending nothing was sent. Active work retains its execution slot until the underlying handler/operation actually settles. A stuck plugin therefore stays bounded rather than admitting unlimited replacement work, but can keep its conversation and capacity occupied. Diagnose and fix it; do not solve it by unbounded queues or repeated cancellation hooks.

`stopReceiving()` should stop ingress and invalidate handles promptly. `close()` releases owned sessions, clients, streams, and executors; both must tolerate repeated calls and partial initialization. Shutdown stops sources, cancels/drains channel work while chat coordination is still available, and performs bounded resource cleanup. Finite provider timeouts and cooperative cancellation are part of the plugin's responsibility.

### One poller per account, when needed

[ChannelPollingLease](../freechat-service/src/main/java/fun/freechat/service/channel/ChannelPollingLease.java) is an optional service helper for providers requiring a sole poller. `start(redisson, lockName, onAcquired, onLost, checkInterval)` takes an exact lock name; no namespace is added. Use a stable, non-secret, account-scoped name shared by replicas.

The helper's owning virtual thread acquires/checks the Redis lock, runs `onAcquired` to create the polling registration, and eventually closes that registration and unlocks **only its own lock on the owning thread**. `onLost` should invalidate the generation and stop admission, including failure paths. Make both callbacks safe for partial startup. Retrying acquisition after a settled attempt belongs to your reconciliation logic.

`close()` requests shutdown without waiting; `isActive()` is only a local snapshot, not a fresh Redis assertion. Wait for `settled()` outside the ownership callbacks before treating cleanup as finished or replacing the polling registration. Never use `forceUnlock`: it can remove a successor's lock. Ownership loss/Redis uncertainty must stop local ingress, but leases cannot recall an HTTP request already sent during a pause or partition.

## 7. Handle delivery failures without duplicate messages

The adapter classifies failures; [ChannelDelivery](../freechat-service/src/main/java/fun/freechat/service/channel/ChannelDelivery.java) applies bounds and retry delays. `ChannelFailure` exposes only `kind()` and `retryAfter()`—there is no separate “accepted” flag. That makes correct adapter classification essential.

| Kind | Meaning and runtime behavior |
| --- | --- |
| `REJECTED` | Local capacity/validation rejection, unavailable account, or a definitive provider refusal. Not automatically retried. Determine which boundary rejected it before advising the user. |
| `UNSUPPORTED` | Missing optional interface or unsupported media kind. Not retried; choose a supported presentation before sending. |
| `CANCELLED` | Inactive generation, cancelled turn, or stopped work. Not retried. Public futures may also surface Java `CancellationException`. |
| `FAILED` | Nonretryable/unclassified failure or delivery deadline reached before an attempt. Not retried; inspect sanitized diagnostics. |
| `RETRYABLE` | Adapter explicitly asserts repetition is safe: definite non-acceptance or an idempotent edit to a known message ID. Retried within policy bounds. |
| `RATE_LIMITED` | Definitive provider rate-limit rejection. Retried within bounds, honoring `retryAfter()` translated from provider `retry_after`. |
| `AMBIGUOUS` | A send might already have been accepted, for example a timeout after transmitting it or a missing receipt. **Never automatically retried.** |

**Idempotency** means repeating an operation has the same effect as doing it once. Replacing the text of a known message may be idempotent; creating a new text/photo message usually is not. Do not label a generic network exception `RETRYABLE` for a new send. The runtime trusts the label; it cannot infer provider acceptance. Unexpected exceptions become `FAILED`, so adapters must explicitly classify uncertain sends as `AMBIGUOUS`.

The default is three total attempts within 90 seconds, not three retries. A zero retry delay uses 250 ms; a provider-specified delay is not shortened to squeeze in another attempt. Retry waits hold destination order and include the final edit even when no more model tokens arrive. Status operations are excluded from retries.

A delivery failure never reruns the LLM. A history-write failure after provider acceptance must not resend the message. Reconcile known receipts separately; surface uncertainty for manual/provider-specific investigation instead of blindly resending. Likewise, only a definite formatting rejection can justify a plain-text fallback—not an unknown delivery outcome.

## 8. Security and operational privacy

A plugin runs with the application's privileges. Review its SDK/dependencies and network access as application code, not as a sandboxed extension.

Store provider credentials in an appropriate secret store or encrypted persistence, with controlled decryption keys, access, and rotation. Do not put tokens in source, example configuration, URLs in logs, thread names, or polling lock names. A new provider does not automatically inherit Telegram's encrypted token storage: implement its own secure configuration path.

Treat payloads, sender identifiers, model responses, media references, and exception bodies as private. Core value types avoid printing message contents, but that does not sanitize your SDK logs or your own `payload.toString()`. Emit safe categories, bounded counters, and approved correlation IDs; do not attach raw provider exceptions as causes or suppressed exceptions to public failures. In particular, response bodies and upload-close exceptions can contain credentials or private URLs. If detailed diagnostics are necessary, design a separately access-controlled redacted workflow.

Media URLs are untrusted input. **SSRF** (server-side request forgery) happens when an attacker makes your server/SDK fetch internal or otherwise forbidden addresses. Define allowed schemes/hosts, check DNS resolution and redirects, deny internal/loopback/link-local destinations unless explicitly required, restrict outbound network access, and bound download size/time and content types. Decide whether your server, SDK, or provider will fetch a `Reference`; the SPI performs no URL safety validation. Restrict upload paths and file permissions too. Do not imply a URL accepted by the Java record is safe.

During overload, avoid retry storms: report `REJECTED` safely, rate-limit best-effort busy replies, and monitor `ChannelRuntime.pendingReceives(pluginId)` / `pendingSends(pluginId)` (counts include active work). Admission is not durable processing. Protect group/private memory boundaries, revocation, and payload retention before enabling real traffic.

## 9. What the Telegram adapter preserves

The real implementation is [TelegramChannelPlugin](../freechat-start/src/main/java/fun/freechat/channels/telegram/TelegramChannelPlugin.java), with its [manager](../freechat-start/src/main/java/fun/freechat/channels/telegram/TelegramChannelManager.java), [raw transport](../freechat-start/src/main/java/fun/freechat/channels/telegram/DefaultTelegramChannel.java), and [streaming reply emitter](../freechat-start/src/main/java/fun/freechat/channels/telegram/handler/TelegramStreamingReplyEmitter.java). Use it for provider-specific examples, not as a reason to copy Telegram identity into another channel.

- Existing backend token schema/encryption, bot username, and invitation-link API behavior remain. Token rotation/deletion invalidates the previous generation. Each node can cache outbound client/metadata; polling belongs to the lease owner.
- `/start` (configured greeting or fallback), `/help`, `/reset`, and commands addressed with `@bot` remain. Unknown commands retain ordinary message routing. Reset follows conversation FIFO, rather than interrupting an earlier turn.
- Existing backend/chat bindings, `tg-<chatId>` identities (including negative group IDs), message records, and deployed memory scopes remain unchanged.
- Incoming text, photo, voice, video, audio, document, and unsupported message kinds are recorded. **Only incoming text reaches the model**; this migration does not add image understanding or speech recognition.
- Outgoing text, image/photo, voice, video, audio, and document are supported. Telegram presentation still sends placeholders, coalesces partial edits at 500 ms, splits around 4,000 characters, applies final legacy Markdown, extracts generated Markdown images, and refreshes typing every four seconds. Its bounded reply buffer is 128,000 characters with at most 32 image references; this differs from the generic bridge's 65,536-character final-text buffer.
- Final edits use scheduled rate-limit retries without requiring another token. Terminal handling is idempotent; confirmed sends/recording are kept separate so a later failure does not duplicate an accepted photo. A failed final text edit or one failed image does not suppress the remaining generated images.

Telegram owns its polling worker and HTTP client rather than relying on an opaque SDK session to clean them up. [TelegramPollingSession](../freechat-start/src/main/java/fun/freechat/channels/telegram/TelegramPollingSession.java) waits for actual polling/HTTP completion before retirement; [TelegramHttpClient](../freechat-start/src/main/java/fun/freechat/channels/telegram/TelegramHttpClient.java) prevents hidden HTTP retransmissions, including response-based follow-ups. Polling errors are reported without retaining provider exception bodies.

Backend notifications now use the shared [ChannelBackendEvents](../freechat-service/src/main/java/fun/freechat/service/channel/ChannelBackendEvents.java) topic `freechat:channels:backend-changed`, carrying backend IDs, not credentials. The runtime schedules listener work away from the Redis callback, and a **15-second desired-state reconciliation** retries missing activation, notices removed/changed tokens, and attempts polling recovery. Notifications alone are not a durable configuration queue.

For deployment, coordinate a drain/stop of old Telegram instances containing unconditional `forceUnlock` **before** enabling new owners. Mixed old/new processes can violate ownership even though the new implementation is careful. Preserve polling lock identity, wait for settlement, and test token rotation/takeover with your deployment. Local tests do not establish live bot validation or exactly-once delivery.

## 10. Test safely, then troubleshoot

Use the focused suites before introducing credentials. Service/unit tests exercise discovery, ordering, bounds, retries, cancellation, and the final-text bridge:

```shell
mvn -B -pl freechat-service -am test \
  -Dtest=DemoChannelPluginTest,ChannelRuntimeTest,ChannelTaskSchedulerTest,ChannelDeliveryTest,ChannelChatBridgeTest,ChannelPluginDestructionTest \
  -Dsurefire.failIfNoSpecifiedTests=false
```

Registry assertions live in the demo/runtime tests. The executable starter command in section 1 is the smallest entry point.

The start-module local adapter suites use mocks/stubs and fabricated credentials, not a live bot:

```shell
mvn -B -pl freechat-start -am test \
  -Dtest=ChannelPollingLeaseTest,ChatStreamHandleTest,TelegramTransportTest,TelegramChannelManagerTest,TelegramChannelPipelineTest,TelegramStreamingReplyEmitterTest,ChannelUtilsTest,TgChatBindingServiceTest,MemoryPrivacyLoggingTest \
  -Dsurefire.failIfNoSpecifiedTests=false
```

The following tests additionally exercise the real Telegram SDK against local HTTP stubs, owned polling resources, and a separately compiled JAR loaded through Spring Boot metadata. They use fabricated credentials and do not contact Telegram:

```shell
mvn -B -pl freechat-start -am test \
  -Dtest=TelegramTransportHttpTest,TelegramPollingHttpTest,ExternalChannelPluginTest \
  -Dsurefire.failIfNoSpecifiedTests=false
```

For actual Redis ownership/coordination, enable Docker and let Testcontainers create **private Redis containers**. Do not point these tests at a shared Redis instance:

```shell
mvn -B -pl freechat-start -am verify \
  -Dit.test=ChannelPollingLeaseIT,ChatTaskQueueCoordinationIT \
  -Dfailsafe.failIfNoSpecifiedTests=false
```

These selected ITs do not use `AbstractIntegrationTest`, application databases, real provider credentials, or live model calls. `mvn -B test` runs the full unit suite; an unfiltered `verify` can run unrelated integration tests. Do **not** enable the legacy `TelegramChannelIT` or live Telegram mode just to test a plugin. Its environment/property credentials can activate broader infrastructure/provider paths; the local suites above are the intended starting point.

If you intentionally run other integration tests extending [AbstractIntegrationTest](../freechat-start/src/test/java/fun/freechat/AbstractIntegrationTest.java), inspect their prerequisites first. Its setup recursively deletes **`System.getProperty("APP_HOME")/data`**. Create a fresh dedicated temporary directory and pass it explicitly as `-DAPP_HOME=/absolute/path/to/that/temporary-directory`; never use a developer or deployed application's home. The Spring test property alone is not a substitute for this JVM system property. Such tests can also start MySQL/Milvus and load provider credentials from the environment.

For your own adapter, test one slow conversation alongside a fast one; queue saturation; close/reopen with late callbacks; cancellation before stream readiness; upload reopening/closing; unsupported capabilities; definitive 429 versus ambiguous timeout; final edit retry; repeated terminal callbacks; failed recording after accepted send; and authorization revocation while queued. Use finite waits and assert physical cleanup as well as cancelled futures. Provider SDK behavior still needs a separately authorized staging validation; these instructions do not claim any live bot was validated.

| Symptom | Where to look first |
| --- | --- |
| Bean not discovered | Is the class in main sources and on the host classpath? Is its package scanned, configuration imported, or auto-configuration metadata packaged? A test class/JAR beside the app is not enough. |
| Duplicate plugin ID at startup | Check both `@Component` and `@Bean`, auto-configuration, and a second adapter claiming the same ID. Do not hide the error by selecting one arbitrarily. |
| No active instance / `CANCELLED` immediately | Startup is asynchronous. Check activation/credentials, polling ownership, `isActive()`, and whether the callback captured a retired generation. Close before reopening an ID. |
| Saturation / `REJECTED` | Inspect per-conversation and per-plugin receive/send counts, slow provider calls, retry waits, and response buffers. Raising limits without fixing slow work can worsen overload. |
| Turn never completes | Did every success/error/cancellation path complete the handler's future? Did you await `settled()` inside its own callback, forget `stream.start()`, or leave I/O without a timeout? |
| Duplicate messages | Check SDK retries, unsafe `RETRYABLE` classification, early handler completion, provider redelivery without your own deduplication, repeated terminal callbacks, and resend-on-recording-failure. |
| Typing stops | Check capability support, terminal/cancelled turn, timer limits, early handle closure, send saturation, and provider rate limits. Typing is intentionally best effort. |
| Model authorization/memory failure | Verify the real-account binding and permissions at execution time. A provider ID or invented `User` is not account authorization; group chats must not inherit private memory. |

## A small source map

Start with these files rather than reading the entire runtime:

| When you need to… | Read |
| --- | --- |
| Run/modify the first example | [DemoChannelPlugin](../freechat-service/src/test/java/example/channels/DemoChannelPlugin.java) and [its test](../freechat-service/src/test/java/example/channels/DemoChannelPluginTest.java) |
| Implement contracts and value types | [channels/spi](../freechat-common/src/main/java/fun/freechat/channels/spi/) |
| Follow discovery, lifecycle, and capacity | [ChannelRegistry](../freechat-service/src/main/java/fun/freechat/service/channel/ChannelRegistry.java), [ChannelRuntime](../freechat-service/src/main/java/fun/freechat/service/channel/ChannelRuntime.java), [ChannelPluginRuntime](../freechat-service/src/main/java/fun/freechat/service/channel/ChannelPluginRuntime.java) |
| Add AI with full-turn completion | [ChannelChatBridge](../freechat-service/src/main/java/fun/freechat/service/channel/ChannelChatBridge.java) and [ChatStreamHandle](../freechat-service/src/main/java/fun/freechat/service/chat/ChatStreamHandle.java) |
| Audit retries or polling ownership | [ChannelDelivery](../freechat-service/src/main/java/fun/freechat/service/channel/ChannelDelivery.java) and [ChannelPollingLease](../freechat-service/src/main/java/fun/freechat/service/channel/ChannelPollingLease.java) |
| Understand the deeper design | [Design and implementation plan](channel-plugin-design.md) |
