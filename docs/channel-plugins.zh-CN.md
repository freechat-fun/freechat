# 开发消息渠道插件

[English](channel-plugins.md) | 中文版

**渠道（channel）**把外部消息服务与 FreeChat 角色连接起来。渠道插件就是为某个消息服务编写的 Java 适配代码，**不是** FreeChat 的 OpenAPI 工具插件。本文先带你运行一个离线回声示例，再说明如何接入真实消息平台，以及如何按需使用 FreeChat 的 AI 回复。

插件是**受信任的 Java 应用代码**，在应用启动时作为 Spring bean 创建。它不是沙箱中的扩展，不支持热加载，也不能通过上传不可信 JAR（Java 归档文件）安装。添加插件需要重新构建、部署并重启应用。SPI（服务提供者接口，即适配器需要实现的一组 Java 契约）只使用 JDK 类型。SDK 是平台的软件开发工具包，通常就是调用其 API 的客户端库；这些类型留在适配器内部，不应进入共享运行时。

请使用当前源码要求的 Java 25 编译目标及兼容的 Maven/JDK 工具链。下面的命令都在仓库根目录执行。[设计文档](channel-plugin-design.md) 在编码前完成，解释了架构选择；其中对旧实现的历史调查不是当前 API 的使用说明。

## 1. 先运行第一个插件

这个示例不需要机器人令牌、数据库、Redis、模型 API 密钥，也不需要启动完整 FreeChat 服务。Maven 可能需要下载构建依赖，但测试本身不访问网络。

完整、可执行的源码是：

- [DemoChannelPlugin.java](../freechat-service/src/test/java/example/channels/DemoChannelPlugin.java)
- [DemoChannelPluginTest.java](../freechat-service/src/test/java/example/channels/DemoChannelPluginTest.java)

执行以下完整命令：

```shell
mvn -B -pl freechat-service -am test -Dtest=DemoChannelPluginTest -Dsurefire.failIfNoSpecifiedTests=false
```

`-pl` 选择 service 模块；`-am` 同时构建它依赖的模块。最后一个选项允许上游依赖模块中不存在同名测试。

下面是插件本身。它刻意只实现**进程内回声和内存发件箱**，不是即时通信平台适配器，也不是生产级收件箱。发件箱是无界的测试观察工具，不能直接拿来做生产消息存储。

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

**Bean** 是由 Spring 创建和管理的对象。测试显式注册这个 bean，因此即使 `example.channels` 不在 FreeChat 默认扫描的 `fun.freechat` 包下，也能发现插件。关键测试代码如下：

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

所需 import，以及插件发现、仅文本能力、停止后拒收等断言，请查看上面的完整测试链接。手动注册 registry/runtime 只是这个最小独立测试上下文的需要；正常 FreeChat 应用已经会注册它们。

### 跟着一条消息走完整个过程

Spring 先发现所有 `ChannelPlugin` bean。`ChannelRegistry` 在连接平台前校验插件 ID 和必要方法，随后 `ChannelRuntime` 异步启动插件。构造函数只负责组装依赖，不应连接平台，也不应反过来依赖 registry/runtime。

`start(runtime)` 打开 `demo-account`，得到 `ChannelInstance<String>`。它是**某一次激活代次（generation）**的句柄：表示账号的一段有效生命周期，而不只是一个名字。示例中的 `ready()` 是测试辅助方法，不属于 SPI。

测试调用 `receive("conversation-1", "Hello")`。真实 SDK 则会调用你注册的**回调（callback）**，也就是事件到来后由 SDK 执行的函数。回调先验证事件，再调用当前实例的 `receive(conversationId, eventId, payload)`；不要在回调里执行账号绑定、调用模型或同步发送消息。示例把可选的平台事件 ID 设为 `null`。

运行时创建消息信封，也就是把地址、事件 ID 和载荷放在一起的小对象，再按会话调度处理器。对运行时来说，`String` 载荷是不透明的：它只知道消息的路由地址，不解释内容。真实插件可以实现 `ChannelPlugin<YourSdkEvent>`，保留平台特有字段，而不必先压缩成一个有损的通用事件。

处理器调用 `turn.outbound().sendText(...)`。这会**调度**发送，不会在接收线程上直接访问平台。独立的发送工作线程调用一次原始 transport；示例的 transport 只把结果放进测试发件箱。`ChannelReceipt` 携带平台消息 ID，表示平台已接受操作，不代表设备已收到或用户已读。

**Future** 表示稍后才会得到的结果；`CompletionStage` 允许在这个结果后串接下一步。`thenApply(receipt -> null)` 把发送成功的回执转换成处理器需要的 `Void` 结果。返回这条 future 链，会让会话一直被本轮占用，直到发送完成。始终返回完整的 future 链：运行时会单独追踪实际发送，但无法发现处理器私自启动、没有纳入完成链的后台数据库或模型任务。测试使用有限超时等待最终结果，是为了断言；平台回调不能照搬这种阻塞等待。

关闭 Spring 上下文时，系统停止接收、取消/排空运行时任务，再关闭插件资源。`stopReceiving()` 和 `close()` 必须具有**幂等性（idempotency）**：重复调用不会重复破坏资源或妨碍清理，包括启动只完成了一部分的情况。运行时会配置 Spring，避免它在销毁 bean 时另行调用插件的 `close()`。清理应放在插件生命周期方法中，不要再添加独立的 `@PreDestroy` 或 `DisposableBean` 钩子，抢在运行时之前关闭同一批资源。

## 2. 把回声替换成真实接收和发送

保留路由及调度结构，把发件箱 lambda 换成平台 SDK 客户端，把测试里的 `receive()` 调用换成经过认证的 SDK 回调或 HTTP 接收适配器。SDK 必须配置有限网络超时；避免 SDK 自带的自动重试把运行时认为的“一次尝试”变成可能重复发送的多次请求。

**轮询（polling）**由接收端主动向平台询问新事件，长轮询会让请求保持一段时间；**webhook** 则由平台向你的服务 POST 事件。两种方式都应先验证事件，再提交，并及时返回平台。回调应捕获它所属代次的句柄，不要每次从一个可变全局变量取“最新实例”，否则旧账号事件可能误投到新激活的实例。

### 不要混淆各种 ID

**后端（backend）**是 FreeChat 保存的一组角色配置，包括模型、提示词、记忆设置和凭据。**绑定（binding）**把已认证的外部身份/会话映射到有权使用的 FreeChat 账号和聊天。它们都不是外部会话 ID。

| 值 | 含义示例 |
| --- | --- |
| `ChannelAddress.pluginId()` | 适配器类型，如 `demo` 或 `telegram`；必须唯一，格式为 `[a-z][a-z0-9-]{0,63}`。 |
| `ChannelAddress.instanceId()` | 该插件下的机器人/渠道账号；非空白，最多 256 个字符。不一定是 FreeChat 用户或后端。 |
| `ChannelAddress.conversationId()` | 平台私聊或群聊 ID；非空白，最多 1,024 个字符。 |
| `ChannelEnvelope.eventId()` | 可选的平台事件 ID，可用于插件自己实现去重。 |
| `ChannelEnvelope.payload()` | 具有具体类型的平台事件，共享调度器不解释其内容。 |
| FreeChat 的 `chatId` / `backendId` / 账号 ID | 内部身份及授权概念，应通过安全绑定获取，不能把上面的值直接换个名字当作这些 ID。 |

地址由独立字段构成，不是用分隔符拼接出来的字符串键。提供事件 ID **不会**自动启用去重、持久化或恰好一次处理。

### 必须实现的 API 很小

`ChannelPlugin<E>` 提供 `id()`、`transport()`、`inboundHandler()`、可选的 `policy()`（已有默认值）、`start(runtime)`、`stopReceiving()` 和 `close()`。

`ChannelRuntimeContext<E>` 提供 `openInstance(instanceId)`。返回的 `ChannelInstance<E>` 提供 `id()`、`isActive()`、`receive(conversationId, eventId, payload)`、`outbound(conversationId)` 和 `close()`。重新打开同一个实例 ID 前必须先关闭旧句柄：`openInstance` 不会悄悄替换仍然活跃的实例。即使随后创建了同名新句柄，旧回调仍然失效。

`ChannelInboundHandler<E>.handle(envelope, turn)` 返回的 `CompletionStage<Void>` 必须覆盖**完整一轮处理**，包括命令/绑定、使用模型时的服务清理、最终发送，以及需要完成的记录工作。`ChannelTurnContext` 提供 `address()`、`outbound()`、`deadline()`、`isCancelled()`、`onCancel(action)`、`schedule(action, delay)` 和 `typing(interval)`。回复优先使用 turn 级 outbound；实例级 outbound 用于不属于某轮处理的操作，例如尽力发送忙碌提示，它不受某个 turn 的截止时间保护。

`ChannelTransport.sendText(ChannelAddress, ChannelText)` 是原始、同步的**单次发送尝试**，返回 `ChannelReceipt` 或抛出经过脱敏的 `ChannelFailure`。纯文本是必需能力；`ChannelText.Format.MARKDOWN` 只是可选渲染请求，不表示各平台支持同一种 Markdown。处理器、定时器、模型回调都不能直接调用原始 transport。反过来，原始 transport 也不能递归通过 `ChannelOutbound` 入队发送：如果它等待排在自己后面的任务，整个目的地队列会死锁。

### Webhook 的确认语义由平台协议决定

调用 `receive` 前，应验证平台签名或密钥令牌、时间戳/防重放要求、账号范围、请求体大小、载荷结构、文本/媒体限制及允许的事件类型。解析和下载过程也要有上限。端点必须限定范围并执行认证；公开一个接受任意 `chatId` 和文本的接口不等于安全接入。

接收 future 表示**整轮处理结果**，不是单独的持久化入队回执。队列拒绝可能立即发生，也可能入队后才发生其他失败。不要在平台 HTTP 回调线程中等待大语言模型（LLM）的回复，也不要把 Java 方法正常返回解释为“已可靠保存”。只有符合平台确认协议、且与你实际接收/持久化方案一致时才返回 HTTP 成功。如果可靠确认需要持久化收件箱，就要另行设计存储、去重和恢复。

运行时采用有界内存队列，进程退出可能丢失已接收的任务。平台可能重复投递，也可能**不重放**已接收或被拒绝的事件——Telegram 轮询接收器可能已经推进更新确认位置。忙碌提示只能尽力发送，也可能被拒绝。这里没有持久化收件箱或恰好一次保证。

## 3. 让 Spring 发现插件

### 把源码放进应用

将生产代码放到应用会包含的模块，例如 `freechat-start/src/main/java/fun/freechat/channels/yourprovider/`。给插件加 `@Component`，或在 FreeChat 会扫描/导入的配置中用 `@Bean` 注册。两种路径选一种，避免重复 ID。

例如，把 demo 类复制到**主源码集**后（现有测试类不会随应用发布），可以用以下配置注册它，而不修改整个应用的扫描范围：

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

这只解决 bean 发现，不会自动生成真实接收端点。生产插件仍须实现会话启动管理和平台认证。

### 作为外部依赖 JAR

使用应用构建/运行 classpath 上的普通库 JAR；classpath 就是 Java 应用能够使用的类和库的集合。编译时依赖与宿主**源码修订匹配**的 `fun.freechat:freechat-common`；使用 AI 桥接或运行时辅助类时再依赖 `fun.freechat:freechat-service`。不要依赖可执行的 `freechat-start` fat JAR：其中的 `BOOT-INF/classes` 不是普通库 classpath 布局。`freechat-sdk` 是 REST 客户端，不是渠道 SPI。

不要假定这些内部模块已发布到公共仓库。可从同一个 checkout 构建并安装到本地 Maven 仓库，或者由组织把匹配构建发布到自行管理的仓库：

```shell
mvn -B -pl freechat-service -am install -DskipTests
```

仓库在父 POM 版本中使用 `${revision}`。独立 Maven 项目还必须确认已安装/发布 POM 的父版本及传递依赖版本都可解析。Reactor 是 Maven 的多模块构建机制；仅 reactor 安装成功，或者复制了 JAR，都不足以证明这一点。如果消费端提示无法解析 `${revision}` 父版本，应使用匹配源码的 reactor 构建，或者通过组织的制品发布流程提供版本已解析/扁平化的 POM；不要改用另一个 FreeChat 版本或可执行 JAR。

外部插件 POM 可以使用以下片段。版本必须与实际构建的宿主一致；这里的数值对应当前根 POM。`provided` 表示部署后的 FreeChat 应用提供这些库；插件所需的平台 SDK 依赖仍需通过宿主构建带入应用。

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
    <!-- 使用 ChannelChatBridge 或 service 辅助类时需要。 -->
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

如果希望位于 `fun.freechat` 之外的包也能自动注册，在库的 `src/main/java/example/channels/DemoChannelAutoConfiguration.java` 中加入下面的配置；把 demo 也复制到该库的主源码集：

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

在库中创建 `src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`，内容为：

```text
example.channels.DemoChannelAutoConfiguration
```

把生成的库作为普通 Maven 依赖加入宿主，再构建并重启。也可以用 Spring 的 `@Import` 显式导入库配置。把任意 JAR 放在应用旁边，或者只提供 Java `ServiceLoader` 描述文件，都不会在这里注册插件。不要无故同时启用 demo 的显式配置和自动配置；条件 bean 也不能解决不同插件类声明同一个 ID 的冲突。

## 4. 把已授权的聊天接入 FreeChat AI

先把回声跑通。它刻意不包含账号关联或 AI 逻辑，因此不会误导你把外部用户名当作 FreeChat 用户。

### 调用 service 之前先建立安全边界

插件必须在自己的存储中维护经过认证的账号关联。例如，已登录的 FreeChat 用户批准一个短时、一次性的关联挑战，再由经过验证的平台身份完成证明。保存的映射应明确插件、机器人账号、外部发送者/会话和预期接收人范围，同时记录真实 FreeChat 账号、获准使用的后端/聊天及撤销状态。平台签名证明事件来自哪个平台，**不证明**发送者可以使用任意 FreeChat 账号。

队列中的处理器实际开始执行时，要解析绑定，重新检查账号状态、聊天归属、后端/角色权限、适用的组织限制及撤销状态。发现不匹配或已取消时，不得调用模型。插件直接调用 `ChatService` 不会自动执行 REST 控制器上的授权注解。

你可以把自己设计的授权层叫作 `AuthorizedBindings`，但 **FreeChat 没有提供这个名称的辅助类**。授权和存储层需要应用自行实现并审查。下面的代码从这些检查**已经完成之后**开始，不是 HTTP 端点，也不是授权实现。

默认账号/后端聊天可能与网页聊天共享历史：`ChatService.start` 会复用已有的账号/后端聊天，`getDefaultChatId` 也可能找到它。关联前应向用户解释这一点。外部群聊**绝不能悄悄复用某个人的私聊历史或长期记忆**。仅改变外部会话 ID 并不能隔离底层 FreeChat 记忆。除非你的账号、接收人、后端和记忆隔离策略明确支持，否则应拒绝群聊关联。

现有长期记忆（LTM）识别真实 FreeChat 账号及已部署的 Telegram 身份模型。不要伪造 `User`，不要为其他平台填写 Telegram 字段，也不要凭空构造 `plugin-*` 用户 ID 并期待 LTM 正常工作。插件也可以在共享渠道运行时之上使用自己的对话引擎和存储，但这不等于自动接入 FreeChat LTM。

### 使用生产级最终文本桥接服务

[ChannelChatBridge](../freechat-service/src/main/java/fun/freechat/service/channel/ChannelChatBridge.java) 是 Spring service。将它注入插件/处理器，并把它返回的 future 纳入处理器完成链。下面这个小方法可以放在你的处理器类中：

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
    // 调用者刚刚重新验证了本轮的持久化绑定和权限。
    return bridge.reply(authorizedChatId, UserMessage.from(verifiedText), turn, 4000);
}
```

真实方法签名是 `reply(String authorizedChatId, ChatMessage message, ChannelTurnContext turn, int messageLimit)`，返回 `CompletableFuture<Void>`。这里的 `UserMessage` 是模型输入消息，**不是**伪造的账号 `User`。请按平台限制选择长度：参数范围为 2–65,536 个 UTF-16 代码单元，示例值并非所有平台通用的限制。有的平台按字节、Unicode 码点或格式化后的长度计数。

桥接服务最多缓冲 **65,536 个 UTF-16 代码单元**；超限时取消并失败，不会静默截断。生成结束后，按顺序发送纯文本分段，不拆开代理项对（surrogate pair，即某些 Unicode 字符在 Java 字符串中占用的两个 `char`）。平台支持时可以每四秒显示输入状态。它**不会**逐步编辑占位消息、提取生成图片，也不会自动把收到的音频/图片变成模型输入。空文本不会触发文本发送。它观察 `ChatStreamHandle.ready()` 并启动流，等服务清理和最终发送都结束后才完成；发送失败不会重跑 LLM。[ChannelChatBridgeTest](../freechat-service/src/test/java/fun/freechat/service/channel/ChannelChatBridgeTest.java) 提供了这些边界的可执行示例。

### 进阶：自己管理流式回复

仅在需要平台特有展示方式（例如合并中间文本编辑）时使用。`ChatService.streamSendManaged(chatId, message, context)` 返回 [ChatStreamHandle](../freechat-service/src/main/java/fun/freechat/service/chat/ChatStreamHandle.java)：

- `ready()` 表示 `TokenStream` 已构造好，不代表回复完成；流也可能为 null。
- `settled()` 表示服务清理完毕、聊天协调资源已释放，取消路径也包含在内。
- `cancel()` 向所属聊天任务请求取消，即使构造还未结束也可调用。

某些模型流没有真正有效的取消 API。如果它已经启动，FreeChat 会继续持有聊天协调资源，直到流自然终止、回调和清理都完成；截止时间不能安全地把它变成已结束的流。因此，永不结束的平台调用可能持续占用该聊天，但这比让两轮操作并发修改同一份记忆更安全。

拿到 handle 后，立即注册 `turn.onCancel(handle::cancel)`，必须在**观察 `ready()` 或启动流之前**完成。在 ready 回调中处理失败/null，再检查取消状态，安装增量/完成/错误回调，最后调用 `start()`。回调只更新有界状态或把发送任务入队，不进行网络 I/O、阻塞数据库操作或等待这些操作。最终处理要幂等；重复终止回调不能再次发送图片或记录回复。

返回的 future 应组合 `handle.settled()` 和最终发送/记录 future，例如使用 `CompletableFuture.allOf(...)`。**绝不能在流自己的回调中对 settlement 调用 `join()`/`get()`**：清理过程正在等待这些回调返回。也不要在同一个聊天队列里再包一层任务调用 `ChatService.streamSend`，否则可能等待自己。原有聊天队列仍负责模型/记忆协调，渠道运行时不会替代它。

## 5. 只添加平台支持的可选能力

**能力（capability）**是编辑、媒体等可选功能。纯文本 transport 已足够运行。transport 对象还可以实现：

| 接口 | 方法 |
| --- | --- |
| `ChannelMediaTransport` | `Set<ChannelMedia.Kind> mediaKinds()`，以及返回 `ChannelReceipt` 的 `sendMedia(address, media)`。种类为 `IMAGE`、`VOICE`、`VIDEO`、`AUDIO`、`DOCUMENT`。 |
| `ChannelMessageEditor` | `void editText(address, messageId, text)`。 |
| `ChannelStatusTransport` | `void sendTyping(address)`，表示临时活动状态，不是已读/送达回执。 |
| `ChannelInvitationProvider` | `username(instanceId)` 和 `invitationLink(instanceId)`，提供账号元数据。Telegram 在 transport 上实现它，调用者检查实际提供能力的对象。 |

依赖后端配置的插件还可单独实现 `ChannelBackendListener.backendChanged(backendId)` 和 `reconcile()`。邀请信息和后端通知不是发送操作，也不是所有插件都必须具备。

应用代码应调用经过调度的 `ChannelOutbound`。例如，以下处理器方法在支持编辑时使用占位消息；不支持时直接发送一条最终文本：

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

导入 SPI 类型和 `CompletableFuture` 即可。这个方法演示能力选择，不是完整的增量流式渲染器。结果不确定的编辑/发送失败，绝不意味着可以另发一条可能重复的消息。

发送图片时，transport 必须同时声明媒体接口和具体图片种类。下面的方法可以放在插件类中；这里仅用 `transport()` 检查能力，不直接发送：

```java
CompletableFuture<ChannelReceipt> sendImage(ChannelTurnContext turn, java.nio.file.Path image) {
    if (!(transport() instanceof ChannelMediaTransport media)
            || !media.mediaKinds().contains(ChannelMedia.Kind.IMAGE)) {
        return turn.outbound().sendText(ChannelText.plain("Images are not supported here."));
    }
    return turn.outbound().sendMedia(ChannelMedia.file(ChannelMedia.Kind.IMAGE, image, "Result"));
}
```

`outbound.supports(ChannelMediaTransport.class)` 只检查接口，不检查具体种类。不支持的显式发送/编辑/状态操作会返回带 `UNSUPPORTED` 的失败 future，不会悄悄忽略。

`ChannelMedia.Reference` 可以表示平台文件 ID，**也可以**表示 URL，由具体适配器/SDK 解释。例如 `ChannelMedia.reference(IMAGE, providerFileId, "Result")` 不会触发核心层下载。`ChannelMedia.file(...)` 创建的 `Upload` 每次都会重新打开文件。自定义 `new ChannelMedia.Upload("report.txt", () -> Files.newInputStream(path))` 时，opener 必须为**每一次尝试创建全新的输入流**，不能返回已被消耗的旧流。资源在任务实际结束前必须一直可用，路径也必须安全。

原始适配器负责打开和关闭每次上传的流，包括失败路径；关闭要发生在 SDK 消费完流之后。确认平台已接受后的 close 失败，不能把成功变成重试并重复发送。运行时不会代替适配器关闭任意平台资源，也不下载 Reference。URL/文件访问和媒体限制的验证见后文。

输入状态可以用 `turn.outbound().supports(ChannelStatusTransport.class)` 判断；`turn.typing(Duration.ofSeconds(4))` 在不支持时本来就会返回无操作句柄。应在异步回复进入终态时关闭这个 `AutoCloseable`，而不是启动 future 后马上退出 try-with-resources，把它提前关掉。turn 完成/取消也会停止定时器。底层的 `outbound.sendTyping(BooleanSupplier stillNeeded)` 会在执行前检查 supplier。输入状态是尽力而为、合并调度的，发送层不会为它重试；它失败不应导致正文失败。

## 6. 理解顺序、容量和停机

### FIFO 与背压的实际含义

**FIFO** 即先进先出。在同一个外部地址（插件、账号、会话）内，下一条事件要等待上一条处理器**完整完成**，而不是等它从 Java 方法返回或模型输出最后一个 token。命令和绑定操作也因此遵守同一局部顺序。另一个会话可以并行运行，不同插件还有独立的执行容量。

发送也按目的地 FIFO 执行，重试期间不允许后续发送越过。接收和发送容量彼此独立，因此接收任务等待发送时，不会占用完成该发送所需的发送执行槽。Java 虚拟线程降低了等待成本，并不会消除内存、网络和平台限流。运行时先接纳有界任务，再启动工作线程。

这是**本机接纳顺序**，不是应用多个副本或独立平台回调之间的全局总顺序。FreeChat 原有聊天服务协调保护跨实例共享模型/记忆操作，但不能把外部消息发送变成恰好一次。

**背压（backpressure）**是指容量不足时拒绝额外工作，而不是无限积压。当前 [ChannelPolicy.defaults()](../freechat-common/src/main/java/fun/freechat/channels/spi/ChannelPolicy.java) 为：

| 设置 | 默认值 |
| --- | --- |
| `receiveConcurrency` / `sendConcurrency` | 每插件 128 个活跃接收轮次 / 32 个活跃发送 |
| `receiveCapacity` / `sendCapacity` | 每插件各 4,096，包含正在执行的任务 |
| `conversationReceiveCapacity` / `conversationSendCapacity` | 每外部地址 32 / 64，包含正在执行的任务 |
| `maxInstances` | 每插件最多 1,024 个活跃账号实例 |
| `turnTimeout` | 10 分钟，从提交接纳阶段开始计算，包含队列等待 |
| `deliveryTimeout` / `deliveryAttempts` | 总计 90 秒 / 最多 3 次尝试，包含首次 |
| `shutdownTimeout` | 10 秒 |

发送时间从提交操作时开始，排队和重试等待都计入预算。它不能替代 SDK 网络超时，也不能物理终止一个不响应取消的在途请求。连接、读取、总调用都应设置有限超时，并与发送/停机预算协调。

这些设置没有统一的、自动生效的 YAML 键。应由插件自己的已验证配置创建 `ChannelPolicy`，再由插件重写 `policy()` 返回它。例如，下面这个 Java 配置值只降低接收并发，其余保留默认值：

```java
static ChannelPolicy policyFor(int configuredReceiveConcurrency) {
    ChannelPolicy d = ChannelPolicy.defaults();
    return new ChannelPolicy(configuredReceiveConcurrency, d.sendConcurrency(),
            d.receiveCapacity(), d.sendCapacity(),
            d.conversationReceiveCapacity(), d.conversationSendCapacity(), d.maxInstances(),
            d.turnTimeout(), d.deliveryTimeout(), d.deliveryAttempts(), d.shutdownTimeout());
}
```

从你自己的 Spring 配置把 `policyFor(8)` 传给插件构造函数，保存到 `ChannelPolicy configuredPolicy` 字段，并重写插件方法：

```java
@Override
public ChannelPolicy policy() {
    return configuredPolicy;
}
```

这是你对插件的明确扩展，不是现有 demo 已有的构造函数。如果绑定外部属性，键名需要由插件定义并说明。仅声明一个 `ChannelPolicy` bean 不会配置所有插件。运行时创建时会读取策略，因此修改后需要重新部署/启动。

每个 turn 最多有 **8 个活跃定时任务**和 **16 个取消钩子**。`turn.schedule` 回调只应更新少量状态或把工作入队，不能在共享定时器线程上执行 SDK I/O 或阻塞等待。重试定时器只唤醒/调度工作，真正的尝试由发送工作线程执行。增量 token 更新需要合并，不能让过时编辑占满队列并挡住最终答案。

### 取消信号不等于资源已经清理完

`turn.deadline()` 包含队列等待。及时注册取消动作，并在启动新工作前检查 `isCancelled()`。关闭实例会废弃该代次，取消它的排队/执行中轮次及相关定时器/重试，并拒绝迟到回调。旧 outbound 句柄不能复用于下一次激活。

对外 future 被取消只是一个**信号**，不证明模型、socket、上传流或清理回调已停止。即使观察结果的 future 被取消，turn 仍会等待内部追踪的已接纳发送真正结束。如果所属轮次被取消时发送已经开始，稍后确认接受的回执仍会传到正常完成链；应记录这个回执，不能假装从未发送。活跃任务会继续占用执行槽，直到底层处理器/操作真正结束。卡住的插件仍然是有界的，不会靠无限接纳新任务替换它，但会持续占用会话和容量。应诊断并修复根因，而不是使用无界队列或不断添加取消钩子。

`stopReceiving()` 应及时停止入口并废弃句柄。`close()` 释放自己持有的会话、客户端、流和执行器。二者都必须容忍重复调用及不完整初始化。停机先停止接收，再趁聊天协调设施仍可用时取消/排空渠道工作，最后在有限时间内清理资源。有限平台超时和协作式取消都是插件的责任。

### 需要时为每个账号只保留一个轮询者

[ChannelPollingLease](../freechat-service/src/main/java/fun/freechat/service/channel/ChannelPollingLease.java) 是可选的 service 辅助类，适合要求单一轮询者的平台。`start(redisson, lockName, onAcquired, onLost, checkInterval)` 原样使用传入的锁名，不自动添加命名空间。应使用稳定、不含密钥、限定账号范围且被各副本共同使用的名称。

辅助类的所有者虚拟线程获取/检查 Redis 锁，执行 `onAcquired` 创建轮询注册，最后关闭该注册，并在**持锁线程上只释放自己的锁**。`onLost` 应废弃代次、停止接收，失败路径也一样；两个回调都必须兼容只完成部分启动的情况。某次租约尝试结束后是否重新获取，应由你的对账逻辑决定。

`close()` 只请求停止，不等待完成；`isActive()` 只是本地快照，不是实时 Redis 所有权证明。把清理视为完成或替换轮询注册前，应在所有权回调之外等待 `settled()`。不要使用 `forceUnlock`，它可能删除继任者的锁。失去所有权或无法确认 Redis 状态时必须停止本地入口，但租约无法撤回暂停或网络分区期间已经发出的 HTTP 请求。

## 7. 处理发送失败，而不是制造重复消息

适配器负责分类失败，[ChannelDelivery](../freechat-service/src/main/java/fun/freechat/service/channel/ChannelDelivery.java) 负责尝试次数、期限和等待。`ChannelFailure` 只暴露 `kind()` 和 `retryAfter()`，没有另一个“是否接受”的标志，所以适配器分类必须准确。

| 类型 | 含义与运行时行为 |
| --- | --- |
| `REJECTED` | 本地容量/参数验证拒绝、账号不可用，或者平台明确拒绝。不自动重试；先判断在哪个边界被拒绝，再给用户建议。 |
| `UNSUPPORTED` | 缺少可选接口或不支持该媒体种类。不重试，应在发送前选择受支持的展示方式。 |
| `CANCELLED` | 代次失效、轮次取消或工作停止。不重试；对外 future 也可能直接表现为 Java `CancellationException`。 |
| `FAILED` | 不可重试/未分类错误，或尝试前已达到发送期限。不重试，查看安全诊断。 |
| `RETRYABLE` | 适配器明确保证可以安全重复：已确定未接受，或对已知消息 ID 进行幂等编辑。在策略范围内重试。 |
| `RATE_LIMITED` | 平台明确因限流拒绝。在策略范围内重试，遵守从平台 `retry_after` 转换来的 `retryAfter()`。 |
| `AMBIGUOUS` | 平台可能已经接受，例如请求发出后超时，或缺少回执。**绝不自动重试。** |

**幂等性**意味着重复操作与只执行一次的结果相同。替换已知消息的文本可能幂等；创建一条新文本/图片通常不幂等。不要把新消息的普通网络异常标记为 `RETRYABLE`。运行时信任这个分类，无法自行推断平台是否已接受。未分类异常会变成 `FAILED`，因此适配器应把结果不确定的发送显式分类为 `AMBIGUOUS`。

默认是 90 秒内最多三次尝试，不是首次之外再重试三次。没有指定延迟时使用 250 ms；不能为了塞进下一次尝试而缩短平台要求的等待。重试期间维持目的地顺序，即使没有新 token，最后一次编辑也能按计划重试。状态操作不参与这些重试。

发送失败不会重跑 LLM。平台接受后，历史记录写入失败也不能触发重发。应单独核对已知回执；结果不确定时留给人工或平台特定流程调查，而不是盲目发送。只有明确的格式拒绝才能允许纯文本回退，未知发送结果不能。

## 8. 安全与运维隐私

插件拥有应用的权限。应按应用代码的标准审查它的 SDK、依赖和网络访问，不能当作沙箱扩展。

把平台凭据放在合适的密钥管理服务或加密存储中，控制解密密钥、访问权限和轮换。不要把令牌放进源码、示例配置、日志 URL、线程名或轮询锁名。新平台不会自动继承 Telegram 的令牌加密路径，必须实现自己的安全配置方式。

把载荷、发送者标识、模型回复、媒体引用和异常响应体都视为私密数据。核心值类型避免打印消息正文，但不会自动清理 SDK 日志或你自己的 `payload.toString()`。日志只输出安全类别、有界计数和允许记录的关联 ID；不要把原始平台异常作为 cause 或 suppressed exception 附加到公开错误上。响应体、上传流关闭异常尤其可能包含凭据或私密 URL。如果确需更详细诊断，应另外设计有权限控制的脱敏流程。

媒体 URL 是不可信输入。**SSRF（服务端请求伪造）**是攻击者诱导服务端/SDK 请求内网或其他禁止访问地址的攻击。应明确允许的协议/主机，检查 DNS 解析和重定向，除明确业务要求外拒绝内网、回环和链路本地地址，限制出站网络，并约束下载大小、时间和内容类型。先确定 `Reference` 实际由你的服务、SDK 还是平台下载；SPI 不验证 URL 安全性。上传路径及文件权限也要限制，Java record 接受了一个 URL 不代表它安全。

过载时避免重试风暴：安全反馈 `REJECTED`，对尽力发送的忙碌回复限流，并观察 `ChannelRuntime.pendingReceives(pluginId)` / `pendingSends(pluginId)`，这些计数包含执行中的任务。接纳不等于可靠处理。开放真实流量前先落实群聊/私聊记忆隔离、撤销和载荷保留策略。

## 9. Telegram 适配器保留了什么

真实实现是 [TelegramChannelPlugin](../freechat-start/src/main/java/fun/freechat/channels/telegram/TelegramChannelPlugin.java)，配合其 [manager](../freechat-start/src/main/java/fun/freechat/channels/telegram/TelegramChannelManager.java)、[原始 transport](../freechat-start/src/main/java/fun/freechat/channels/telegram/DefaultTelegramChannel.java) 和 [流式回复展示器](../freechat-start/src/main/java/fun/freechat/channels/telegram/handler/TelegramStreamingReplyEmitter.java)。可以参考其中的平台适配方式，但不要因此把 Telegram 身份复制到其他渠道。

- 保留现有后端令牌字段/加密、机器人用户名和邀请链接 API 行为。令牌轮换/删除会使旧代次失效。各节点可以缓存发送客户端和元数据，轮询由租约所有者执行。
- 保留 `/start`（配置的问候语或回退内容）、`/help`、`/reset`，以及带 `@bot` 的命令。未知命令继续走普通消息路由。reset 按会话 FIFO 执行，不会抢先中断之前的一轮。
- 保留现有后端/聊天绑定、`tg-<chatId>` 身份（包括负数群聊 ID）、消息记录和已部署记忆范围。
- 记录输入文本、图片、语音、视频、音频、文档及不支持的消息类型。**只有输入文本进入模型**；本次迁移没有新增图像理解或语音识别。
- 输出支持文本、图片、语音、视频、音频和文档。Telegram 仍发送占位消息，按 500 ms 合并中间编辑，在约 4,000 字符处分段，最终使用原有 Markdown，提取生成的 Markdown 图片，并每四秒刷新输入状态。其有界回复缓冲为 128,000 字符、最多 32 个图片引用，与通用桥接服务的 65,536 字符最终文本缓冲不同。
- 最终编辑通过调度器处理限流重试，不需要等待下一个 token。终止处理幂等，已确认发送与记录分开处理，后续失败不会重复发送已被接受的图片。最终文本编辑失败或其中一张图片失败，都不会阻止继续发送其余生成图片。

Telegram 自己持有轮询工作线程和 HTTP 客户端，不依赖一个内部行为不透明的 SDK 会话来清理它们。[TelegramPollingSession](../freechat-start/src/main/java/fun/freechat/channels/telegram/TelegramPollingSession.java) 在退役前等待轮询和 HTTP 操作真正结束；[TelegramHttpClient](../freechat-start/src/main/java/fun/freechat/channels/telegram/TelegramHttpClient.java) 阻止隐藏的 HTTP 重发，包括收到特定响应后自动发起的后续请求。轮询错误只报告安全信息，不保留平台异常响应体。

后端通知现在通过共享的 [ChannelBackendEvents](../freechat-service/src/main/java/fun/freechat/service/channel/ChannelBackendEvents.java) 主题 `freechat:channels:backend-changed` 传递，仅包含后端 ID，不含凭据。运行时把监听处理调度到 Redis 回调线程之外，并每 **15 秒进行一次期望状态对账（desired-state reconciliation）**：重试遗漏的激活、发现删除/变化的令牌、尝试恢复轮询。单靠通知不构成持久化配置队列。

部署时，在启用新所有者**之前**，应协调排空/停止仍含无条件 `forceUnlock` 的旧 Telegram 实例。即使新实现很谨慎，新旧混跑也可能破坏锁所有权。保留轮询锁身份，等待清理完成，并针对你的部署验证轮换/接管。本地测试不能证明真实机器人已验证，更不代表恰好一次发送。

## 10. 安全测试与常见问题

引入凭据前先运行定向测试。service/unit 测试覆盖插件发现、顺序、容量、重试、取消和最终文本桥接：

```shell
mvn -B -pl freechat-service -am test \
  -Dtest=DemoChannelPluginTest,ChannelRuntimeTest,ChannelTaskSchedulerTest,ChannelDeliveryTest,ChannelChatBridgeTest,ChannelPluginDestructionTest \
  -Dsurefire.failIfNoSpecifiedTests=false
```

注册表断言位于 demo/runtime 测试中。第 1 节的示例命令是最小入口。

start 模块的本地适配器测试使用 mock/stub 和虚构凭据，不连接真实机器人：

```shell
mvn -B -pl freechat-start -am test \
  -Dtest=ChannelPollingLeaseTest,ChatStreamHandleTest,TelegramTransportTest,TelegramChannelManagerTest,TelegramChannelPipelineTest,TelegramStreamingReplyEmitterTest,ChannelUtilsTest,TgChatBindingServiceTest,MemoryPrivacyLoggingTest \
  -Dsurefire.failIfNoSpecifiedTests=false
```

下面这些测试还会让真实 Telegram SDK 访问本地 HTTP 桩服务，验证轮询资源清理，并通过 Spring Boot 元数据加载一个单独编译的 JAR。它们使用虚构凭据，不连接 Telegram：

```shell
mvn -B -pl freechat-start -am test \
  -Dtest=TelegramTransportHttpTest,TelegramPollingHttpTest,ExternalChannelPluginTest \
  -Dsurefire.failIfNoSpecifiedTests=false
```

需要实际验证 Redis 所有权/协调时，启动 Docker，由 Testcontainers 创建**私有 Redis 容器**，不要把测试连接到共享 Redis：

```shell
mvn -B -pl freechat-start -am verify \
  -Dit.test=ChannelPollingLeaseIT,ChatTaskQueueCoordinationIT \
  -Dfailsafe.failIfNoSpecifiedTests=false
```

这两个选定的 IT 不使用 `AbstractIntegrationTest`、应用数据库、真实平台凭据或在线模型。`mvn -B test` 运行完整单元测试；不加过滤的 `verify` 可能启动无关集成测试。不要为了测试插件而开启旧的 `TelegramChannelIT` 或 Telegram live 模式。其环境变量/系统属性中的凭据可能激活更广的基础设施或平台路径，应先使用上面的本地测试。

如果确实需要运行继承 [AbstractIntegrationTest](../freechat-start/src/test/java/fun/freechat/AbstractIntegrationTest.java) 的其他集成测试，应先阅读前置条件。它的初始化会递归删除 **`System.getProperty("APP_HOME")/data`**。创建全新、专用的临时目录，并显式传入 `-DAPP_HOME=/absolute/path/to/that/temporary-directory`；绝不能使用开发或部署应用的数据目录。仅有 Spring 测试属性不能代替这个 JVM 系统属性。这些测试还可能启动 MySQL/Milvus，并从环境加载平台凭据。

为自己的适配器测试以下情况：慢会话旁边的快会话、队列饱和、关闭/重开后的迟到回调、stream ready 前取消、上传流重新打开及关闭、不支持的能力、明确 429 与不确定超时的区别、最终编辑重试、重复终止回调、接受发送后记录失败，以及排队期间权限被撤销。等待必须有超时，同时断言实际清理结束，而不只是 future 被取消。平台 SDK 的真实行为仍需单独获准的预发布验证；本文不声称已验证任何真实机器人。

| 现象 | 优先检查 |
| --- | --- |
| 找不到插件 bean | 是否放在主源码集并进入宿主 classpath？包是否被扫描、配置是否被导入、自动配置元数据是否被打包？测试类或应用旁的 JAR 不够。 |
| 启动时报插件 ID 重复 | 检查 `@Component` 与 `@Bean` 是否同时注册、自动配置是否重复，以及是否另一个适配器声明同名 ID。不要随意选一个来掩盖错误。 |
| 没有活跃实例 / 立即 `CANCELLED` | 启动是异步的。检查激活/凭据、轮询所有权、`isActive()`，以及回调是否捕获了已退役代次。重开同名实例前先关闭旧实例。 |
| 饱和 / `REJECTED` | 查看每会话和每插件的收发计数、慢平台请求、重试等待、回复缓冲。只增大容量而不解决慢任务可能加剧过载。 |
| 一轮始终不结束 | 所有成功/错误/取消路径是否都完成了处理器 future？是否在自己的回调里等待 `settled()`、忘记 `stream.start()`，或 I/O 没有超时？ |
| 消息重复 | 检查 SDK 重试、不安全的 `RETRYABLE` 分类、处理器过早完成、平台重复投递但未自行去重、重复终止回调、记录失败后重发。 |
| 输入状态停止 | 检查能力支持、终止/取消状态、定时器上限、过早关闭句柄、发送饱和和平台限流。输入状态本来就只是尽力而为。 |
| 模型授权/记忆失败 | 检查执行时真实账号绑定和权限。平台 ID 或伪造 `User` 不能建立账号授权；群聊不能继承私密记忆。 |

## 小型源码导航

先读下面这些文件，不必一开始就阅读整个运行时：

| 想做什么 | 阅读哪里 |
| --- | --- |
| 运行/修改第一个例子 | [DemoChannelPlugin](../freechat-service/src/test/java/example/channels/DemoChannelPlugin.java) 和[测试](../freechat-service/src/test/java/example/channels/DemoChannelPluginTest.java) |
| 实现接口和值类型 | [channels/spi](../freechat-common/src/main/java/fun/freechat/channels/spi/) |
| 了解发现、生命周期和容量 | [ChannelRegistry](../freechat-service/src/main/java/fun/freechat/service/channel/ChannelRegistry.java)、[ChannelRuntime](../freechat-service/src/main/java/fun/freechat/service/channel/ChannelRuntime.java)、[ChannelPluginRuntime](../freechat-service/src/main/java/fun/freechat/service/channel/ChannelPluginRuntime.java) |
| 添加覆盖完整生命周期的 AI 回复 | [ChannelChatBridge](../freechat-service/src/main/java/fun/freechat/service/channel/ChannelChatBridge.java) 和 [ChatStreamHandle](../freechat-service/src/main/java/fun/freechat/service/chat/ChatStreamHandle.java) |
| 审查重试或轮询所有权 | [ChannelDelivery](../freechat-service/src/main/java/fun/freechat/service/channel/ChannelDelivery.java) 和 [ChannelPollingLease](../freechat-service/src/main/java/fun/freechat/service/channel/ChannelPollingLease.java) |
| 理解更深层的设计 | [设计与实施计划](channel-plugin-design.md) |
