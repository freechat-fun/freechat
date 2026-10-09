# Channel plugins: design and implementation plan

Status: design and implementation plan completed before coding on 2026-10-09.

This document explains the existing Telegram integration, the chosen extension points, the concurrency rules, and the migration steps. The final developer guides will be published in English and Chinese alongside this document. Implementation details may be refined after testing, but preservation rules and acceptance criteria must not be weakened.

## 1. Purpose and terminology

A **channel** is a messaging service through which a person talks to a FreeChat character, such as Telegram. A **plugin** is Java code that connects one such service to FreeChat without requiring changes to the shared channel runtime. It is not the OpenAPI tool-plugin feature already present elsewhere in FreeChat.

A **character backend** is the saved configuration that selects a character's model, prompt, memory settings, and credentials. A **channel account** is a bot or other messaging identity used by a plugin. A **conversation** is a chat with one person or a group. These are different identifiers: the same character backend can serve many external conversations.

A **capability** is an optional feature a channel supports, such as editing an existing message or showing a typing indicator. A **queue** holds work until it can run. **FIFO** means first in, first out. **Backpressure** means refusing or delaying new work when capacity is exhausted rather than accumulating unlimited work in memory.

## 2. Verified current behavior

The source examined is revision `75f72f31`, application version `2.10.0-beta`. The project targets Java 25. Paths below are relative to the repository root; line numbers describe the pre-migration source.

| Area | Current behavior | Source |
| --- | --- | --- |
| Activation | Load and decrypt each backend's Telegram token; create an outbound client and cache the bot username on every application instance. | `freechat-start/src/main/java/fun/freechat/channels/telegram/TelegramChannelManager.java:77` |
| Receiving | One instance polls each backend using a Redis lock. The SDK callback iterates a batch and calls the dispatcher directly. | `freechat-start/src/main/java/fun/freechat/channels/telegram/TelegramChannelManager.java:144` |
| Recovery | A 15-second reconciliation task attempts polling takeover for cached follower bots. It does not currently retry every missing startup activation or revalidate every leader. | `freechat-start/src/main/java/fun/freechat/channels/telegram/TelegramChannelManager.java:209` |
| Configuration events | Local backend changes are broadcast through Redis; every instance activates or deactivates the affected bot. | `freechat-start/src/main/java/fun/freechat/channels/telegram/TelegramChannelEventBridge.java:23` |
| Commands | `/start`, `/reset`, and `/help` are supported, including command names with an `@bot` suffix. Unknown commands go to ordinary message handling. | `freechat-start/src/main/java/fun/freechat/channels/telegram/handler/TelegramUpdateDispatcher.java:32` |
| Binding | A backend plus Telegram chat identifies a FreeChat conversation. Synthetic user IDs use `tg-<chatId>`, including negative group chat IDs. | `freechat-service/src/main/java/fun/freechat/service/chat/impl/TgChatBindingServiceImpl.java:28` |
| Incoming media | Text, photo, voice, video, audio, document, and unsupported kinds are recorded. Only text is currently passed to the AI; this is not incoming image understanding or speech recognition. | `freechat-start/src/main/java/fun/freechat/channels/telegram/handler/ChatBindingTelegramMessageHandler.java:57` |
| Outgoing media | The transport supports text, photos, voice messages, video, audio, and documents. Generated Markdown images are sent as photos after streaming completes. | `freechat-start/src/main/java/fun/freechat/channels/telegram/DefaultTelegramChannel.java:36` |
| Streaming | Send a placeholder; update partial text at most every 500 ms; split long replies around 4,000 characters; apply legacy Telegram Markdown to the final active segment. | `freechat-start/src/main/java/fun/freechat/channels/telegram/handler/TelegramStreamingReplyEmitter.java:94` |
| Reply status | Send typing immediately and refresh every four seconds. Stop on reply completion. This is a typing indicator, not a delivered/read receipt. | `freechat-start/src/main/java/fun/freechat/channels/telegram/handler/TelegramStreamingReplyEmitter.java:238` |
| Rate limits | Telegram `retry_after` delays a later append-triggered edit. There is no shared send scheduler, and a final failed edit is not guaranteed another attempt. | `freechat-start/src/main/java/fun/freechat/channels/telegram/handler/TelegramStreamingReplyEmitter.java:298` |
| Chat memory ordering | The existing chat task queue serializes model/memory operations and holds a Redis coordination lock through stream completion and cleanup. | `freechat-service/src/main/java/fun/freechat/service/chat/ChatTaskQueue.java:89` |
| Public API | Existing backend DTOs expose Telegram-specific configuration, bot username, and invitation link. | `freechat-start/src/main/java/fun/freechat/api/CharacterApi.java:654` |

### Problems the new runtime must address

The current transport interface exposes Telegram SDK types, so another channel cannot implement it. Lifecycle events, polling coordination, dispatch, and reply timers are Telegram-specific. Blocking binding/model-admission work can stall the polling callback. Sends have no common ordering or capacity limits. Typing performs synchronous transport work from a static scheduler. Lock cleanup uses unconditional `forceUnlock`, which can remove a successor's lock after ownership has changed.

There are also delivery edge cases that must not be carried into the shared implementation: repeated completion can repeat work, a failed new placeholder can leave split offsets inconsistent, and final formatting must still be applied when the visible text has not changed.

## 3. Scope and preservation rules

The implementation will provide startup-registered Java plugins, required and optional interfaces, shared receive/send scheduling, a Telegram implementation, automated tests, and complete bilingual integration instructions. Adding a plugin must not require editing a central `switch` statement or importing its provider SDK into the shared runtime.

The following existing behavior is a migration constraint:

- Preserve Telegram token configuration, encryption, public API field names, invitation links, and bot usernames.
- Preserve `/start` greetings and fallback text, `/help`, `/reset`, and group command addressing.
- Preserve binding keys, conversation IDs, `tg-*` identities, Telegram message records, and deployed long-term-memory data.
- Preserve all six outgoing message kinds and image extraction from generated replies.
- Preserve placeholder updates, long-message splitting, final Markdown, typing updates, and provider rate-limit handling.
- Continue to use `ChatService`; do not call model/session memory directly or replace the existing chat coordination queue.
- Keep provider credentials, message bodies, media URLs, and provider exception bodies out of diagnostics.

The first version will not introduce runtime JAR upload/unloading, a plugin marketplace, frontend configuration screens, generated SDK changes, a database migration, a durable message broker, or a new incoming speech/image-understanding feature. A plugin is trusted application code, not sandboxed third-party code.

## 4. Baseline verification

Before production-code changes, both commands completed successfully on 2026-10-09 using Maven 3.9.16 and JDK 26.0.1 with Java release target 25:

```shell
mvn -B -pl freechat-start -am test \
  -Dtest=MemoryPrivacyLoggingTest,ChannelUtilsTest,TgChatBindingServiceTest \
  -Dsurefire.failIfNoSpecifiedTests=false

mvn -B test
```

The focused run passed 153 tests. The full unit-test run passed 1,083 tests: 24 in `freechat-common`, 67 in `freechat-langchain4j`, 448 in `freechat-service`, and 544 in `freechat-start`, with no failures, errors, or skipped tests.

The existing distributed chat coordination suite also passed all 33 tests against a private Redis container:

```shell
mvn -B -pl freechat-start -am verify \
  -Dit.test=ChatTaskQueueCoordinationIT \
  -Dfailsafe.failIfNoSpecifiedTests=false
```

Docker is available for subsequent focused integration tests. Integration tests that use `AbstractIntegrationTest` delete the data directory under `APP_HOME`; they must run with a newly created, dedicated temporary directory, never a developer's or deployed application's data directory. Live Telegram or AI-provider tests must not be enabled implicitly; the new transport tests will use fabricated credentials and local stubs.

## 5. Acceptance checklist

A green build alone is not sufficient. The following checks define completion of the migration.

### Plugin contracts and discovery

- A non-Telegram, text-only test plugin registers through ordinary Spring configuration and handles a received message using the shared scheduler.
- Adding that test plugin requires no modification to the registry or runtime.
- Duplicate or invalid plugin IDs fail at registration rather than silently selecting one plugin.
- Optional media, editing, status, and invitation features are discovered explicitly; unsupported features do not cause a basic text conversation to fail.
- Shared contracts and runtime classes do not import Telegram SDK classes.
- The documentation explains how a plugin packaged outside the main application becomes a Spring bean; placing an arbitrary JAR beside the executable is not advertised as installation.

### Receive scheduling

- Two messages in the same external conversation run in admission order, including binding and command handling.
- The first message continues to own its queue position until its complete AI response and final delivery have finished.
- A blocked conversation does not block unrelated conversations or another plugin.
- Admission has finite per-conversation and per-plugin bounds, with a distinguishable rejection result.
- A failed handler does not permanently block later messages in its conversation.
- Cancellation closes the active stream and rejects stale queued work when an account is disabled or replaced.
- Provider callback threads do not perform binding, AI calls, or synchronous message delivery.
- The existing chat-level Redis coordination remains responsible for shared chat-memory integrity.

### Send scheduling and status

- Sends, edits, and media operations for one destination do not overtake one another.
- Receive workers can wait for send completion without exhausting the send execution capacity.
- One slow plugin cannot consume all other plugins' send capacity.
- Provider rate-limit feedback is honored by the runtime, including a rate limit encountered on the final edit.
- A network failure with an unknown send result does not cause an automatic duplicate message.
- Typing timers only schedule work; they do not perform network I/O themselves.
- Typing stops after completion, error, cancellation, or shutdown, and queued stale ticks cannot appear after a completed reply.
- Repeated terminal callbacks cannot send final images twice or repeat final message recording.
- Shutdown and queue cleanup release permits, complete pending futures, and terminate owned executors.

### Telegram parity

- Existing token configuration still activates the same bot and returns the same username/invitation fields through the existing public APIs.
- Token rotation, deletion, startup failures, polling takeover, and shutdown have explicit tests.
- A stale polling owner cannot release a successor's Redis lock.
- Private chats and negative-ID group chats retain their binding and memory identities.
- `/start` uses the configured greeting or existing fallback; `/help` remains available; `/reset` clears existing memory without creating a new conversation.
- Unknown commands and `@bot`-addressed commands preserve their routing behavior.
- Incoming photo, voice, video, audio, and document messages retain their current recording behavior.
- Text, photo, voice, video, audio, and document sending are tested through the adapter, including media captions and repeatable upload resources where supported.
- Streaming preserves incremental text, long-reply splitting, final Markdown, typing updates, extracted images, and outbound message recording.
- Empty responses, repeated completion, failed split placeholders, identical-text final formatting, rate-limited final updates, and failures in image delivery are covered.
- Existing privacy tests continue to verify that provider exceptions, credentials, message content, and URLs are not logged.

### Documentation and verification

- English and Chinese integration guides cover the same functionality and use the implemented interface names.
- Each guide begins with a small working plugin before introducing optional features.
- Every example includes the necessary registration and explains where its files belong.
- Technical terms are explained when introduced, including backend, binding, callback, capability, FIFO, backpressure, idempotency, polling, and webhook.
- Guides explain authenticated account binding, provider secrets, webhook authentication, logging privacy, media restrictions, queue rejection, retry rules, and the limits of in-memory delivery.
- The minimal example is compiled or represented by a compiled test fixture; documentation is not verified solely by reading prose.
- Both root READMEs link to the appropriate guide.
- The full unit suite, targeted scheduler/plugin tests, private-Redis coordination tests, local Telegram transport tests, and changed-file formatting checks pass.
- Any untested live-service behavior is identified explicitly; local stubs are not described as live Telegram verification.

## 6. Architecture and contracts

### Module boundaries

Keep the current Maven modules rather than moving Telegram and its tests merely to create a new directory structure:

```text
freechat-common: fun.freechat.channels.spi
  JDK-only plugin contracts and message/delivery value types
       ↑
freechat-service: fun.freechat.service.channel
  registration, lifecycle, bounded schedulers, delivery, polling ownership
  small adapter to the existing chat service
       ↑
freechat-start: fun.freechat.channels.telegram
  Telegram SDK, existing backend configuration, binding, commands, rendering
```

The API and runtime must not import Telegram classes. Keeping the Telegram implementation in `freechat-start` does not make the API Telegram-dependent. An independently packaged plugin can depend on `freechat-common` and, when it needs built-in AI conversations, `freechat-service`.

### Required contracts

| Contract | Responsibilities |
| --- | --- |
| `ChannelPlugin<E>` | Supply a stable ID, inbound handler, text-capable transport, and scheduling policy; start receiving with a runtime context; stop receiving; close owned resources. |
| `ChannelInboundHandler<E>` | Process an envelope with a turn context and return a completion stage covering the entire turn. |
| `ChannelTransport` | Perform one plain-text delivery attempt and return an opaque message receipt. It is called by the send scheduler, not directly by receiving callbacks. |
| `ChannelRuntimeContext<E>` | The host's interface given to a plugin at startup: open/replace account instances, submit incoming envelopes, use shared timers, and access scheduled delivery. |
| `ChannelTurnContext` | Expose scheduled outgoing operations, deadline/cancellation signals, and cleanup registration for one received message. |

A **completion stage** is Java's promise of a result that may become available later. Completing the handler's stage means the turn is done; calling a method that starts streaming is not sufficient.

The inbound type parameter `E` allows a plugin to retain its full provider event, such as Telegram's `Update`, without teaching the shared runtime about that type. Core scheduling sees the envelope's routing fields and treats its payload as opaque. This avoids a lossy universal event schema and preserves provider-specific media and command metadata.

Supporting value types include:

- `ChannelAddress`: plugin ID, account/instance ID, and external conversation ID. They are separate fields, not a delimiter-concatenated key vulnerable to collisions.
- `ChannelEnvelope<E>`: address, optional provider event ID, and payload. Event IDs do not imply automatic durable deduplication.
- `ChannelText`: text plus a declared rendering format; plain text is the required format.
- `ChannelReceipt`: provider message ID as a string. It means the provider accepted the operation, not that a person read it.
- `ChannelMedia`: media kind, caption, and a repeatable resource. Resources can represent a provider file reference, URL, local file, or an input-stream factory; provider SDK objects stay inside the adapter.
- `ChannelFailure`: a safe failure category, optional retry delay, and whether non-acceptance is known. It must not expose a provider response body in its public message or diagnostics.

### Optional contracts

| Contract | When to implement it |
| --- | --- |
| `ChannelMediaTransport` | The provider can send one or more advertised media kinds: image, voice, video, audio, or document. |
| `ChannelMessageEditor` | The provider can update a message whose ID is known. |
| `ChannelStatusTransport` | The provider supports transient activity such as typing. |
| `ChannelInvitationProvider` | The account has a display name or invitation link. |
| `ChannelBackendListener` | The plugin's account configuration depends on character backend changes and needs periodic reconciliation. |

Optional transport interfaces are implemented by the transport object. Optional lifecycle interfaces are implemented by the plugin. There is no base class with a large collection of unsupported methods. Capability checks happen before scheduling, and unsupported explicit operations return a clear result. A basic plugin can simply accumulate an AI answer and send it once as plain text.

Provider calls are leaf operations: they do not submit another task to the same send queue. Application-facing delivery goes through the runtime. This separation prevents a queue worker from waiting for work queued behind itself.

### Registration

Spring constructs plugin beans first. `ChannelRegistry` validates all IDs and freezes an immutable lookup map before any provider startup. Duplicate IDs fail startup; a transient failure to activate one configured account does not prevent other accounts or plugins from running.

There are two supported installation paths:

1. Source inside the application: create a Spring component or explicit bean.
2. A build-time dependency JAR: register the bean through explicit imported configuration or Spring Boot auto-configuration metadata, including packages outside `fun.freechat`.

Both paths require application deployment/restart. No directory-watching classloader or remote plugin installation is included. Plugin constructors do not call provider services and do not depend on the registry/runtime; the host passes runtime access during startup. Telegram's current manager–dispatcher–transport constructor cycle will be removed, not hidden behind another lazy proxy.

## 7. Generic concurrency design

### Separate execution domains

Each registered plugin owns independent receive and send schedulers, so a slow plugin cannot occupy another plugin's capacity. A small shared timer triggers scheduling; it never performs provider I/O. Lifecycle/reconciliation and cancellation have their own execution path and do not wait behind ordinary messages.

Both schedulers use the same bounded keyed implementation. A lane is a FIFO queue for one `ChannelAddress`. Only a lane's head may execute. Ready lanes compete fairly for a finite number of execution slots. Slots are assigned before starting virtual-thread work; queued messages and delayed work cannot create an unbounded number of threads.

Initial policy defaults are 128 active receive turns, 32 active send operations, 4,096 accepted receive operations and 4,096 accepted send operations per plugin, 32 receives and 64 sends per conversation, and a finite account-instance limit. Counts include active work. The plugin policy can override these values from its own configuration. Tests use small limits to exercise saturation deterministically.

A turn deadline defaults to ten minutes. Delivery has bounded attempts and a bounded overall deadline, separate from the provider client's network timeout. The Telegram reply buffer and image references have explicit finite limits. A provider network call must have a finite SDK timeout; cancelling a future is not proof that its underlying I/O has stopped.

### Receive path

```text
provider callback
  → validate/authenticate provider event in plugin
  → submit envelope through current account-generation handle
  → bounded receive lane
  → binding and command/message handler
  → ChatService (existing chat queue and Redis lock)
  → response callbacks and scheduled delivery
  → service settlement + delivery/history completion
  → release receive lane
```

Admission returns a completion future immediately; a full queue produces a distinguishable rejection and does not invoke the AI. Polling callbacks do not wait for the AI or a reply. Telegram may already have acknowledged an update to its SDK when admission fails, so the runtime will not claim the provider will replay it. A best-effort busy response and safe diagnostics are allowed; persistence/replay needs a separately designed durable inbox.

Commands use the same conversation order. `/reset` clears memory when it reaches the head; it is not redesigned as a priority interrupt. Unrelated conversations run concurrently. FIFO is a local admission guarantee, not a claim of a total arrival order across independent application instances.

### Stream lifetime and cancellation

Add a narrow `ChatStreamHandle` to the existing service, with:

- `ready()`: the stream has been constructed and can be configured/started.
- `settled()`: stream cleanup is finished and the service's chat coordination lock has been released.
- `cancel()`: request cancellation on the owning chat task, including cancellation before construction finishes.

`ChatService` gains a managed streaming entry point; existing `streamSend` remains available and uses the same implementation. A queued cancellation removes or skips the task. A cancellation racing with stream construction closes the eventual stream without starting it. Settlement is completed by the chat queue after coordination release, not by the first terminal model callback.

This is necessary because interrupting the current blocking `streamSend` wait can leave the already-submitted task alive. It is not sufficient to wrap that method in a timeout.

Never wrap `ChatService.streamSend` inside another task on the same chat queue: it would wait for itself. Channel ordering is outside the service queue. The channel turn completes after both service settlement and final delivery/recording. Provider retry delays therefore need not hold the service's memory lock.

On a deadline, shutdown, or account replacement, cancellation is requested immediately. Work does not release its execution slot merely because its public future was cancelled; actual cleanup must settle first. A stuck plugin remains bounded and is reported as unhealthy rather than admitting unlimited replacement work.

### Send path and retry rules

All text, placeholder, edit, media, command response, and typing operations use the send scheduler. Receive and send slots are independent, so a receive task can await a send without deadlocking the runtime. Per-destination ordering is maintained during retries.

Provider adapters classify failures, while the generic runtime applies the policy:

| Outcome | Runtime action |
| --- | --- |
| Confirmed success | Return the receipt; never retry because later history recording failed. |
| Explicit rate limit with known non-acceptance | Honor the advertised delay, subject to attempt/deadline bounds. |
| Retryable known-ID edit | Retry the same edit under the same bounds. |
| Ambiguous text/media send, such as a timeout after possible acceptance | Report uncertainty; do not send a duplicate automatically. |
| Permanent failure or exhausted bounds | Complete exceptionally with safe diagnostics; continue the lane. |

No delivery failure retries the whole AI turn. An adapter may treat Telegram's definite “message is not modified” response as a successful edit. A definite Markdown parse rejection can fall back to plain text; an ambiguous send cannot.

Partial edits are coalesced at the reply emitter: keep the latest buffered text instead of enqueuing one task per token. Typing updates are best-effort, coalesced, and generation/turn checked before execution. The emitter permits at most one ordinary partial update in flight before terminal work is appended, so partial/status traffic cannot fill the lane with obsolete work ahead of finalization.

### Polling ownership and account generations

Provide `ChannelPollingLease` as an optional shared helper for plugins using long polling. A long-lived virtual-thread ownership task acquires, checks, and normally unlocks its Redis lock on the same owning thread. Other threads request shutdown; they never call `forceUnlock`. The number of such tasks is bounded by the account-instance limit.

Telegram retains the existing polling lock namespace. A callback opens the provider session after acquisition and closes it before normal lock release. Registration failure releases only the current task's own lock. Ownership loss or Redis uncertainty stops admission and closes polling; a cached non-null session does not establish ownership.

Each account activation has a generation. Rotation, deletion, or ownership replacement revokes the old generation, cancels its turns/timers/retries, and rejects late callbacks. A new activation receives a new handle. Polling lease callbacks and provider completion callbacks must check that handle before publishing new work.

Redis leases cannot fence an HTTP request already sent to Telegram during a network partition or long JVM pause. The design prevents stale local work and stale unlocks; it does not promise exactly-once external delivery. The existing chat-service Redis lock remains the cross-instance protection for model/memory execution.

## 8. Telegram migration

### Lifecycle

`TelegramChannelPlugin` becomes the registered plugin. `TelegramChannelManager` retains uncached encrypted-backend loading, SDK-client/session ownership, and cached username/link access needed by the public APIs. It no longer depends on the message dispatcher; startup receives the runtime ingress handle instead.

A generic backend-event bridge publishes backend IDs, not credentials. The generic listener schedules reconciliation rather than doing network I/O on the Redis listener thread. Periodic reconciliation scans desired configuration, including missing startup activations, removed bots, and token changes. Startup stays asynchronous so provider availability does not hold the application readiness event open.

Shutdown order is: stop admission and polling; cancel/drain channel turns while chat queues still exist; finish bounded outbound cleanup; close clients and owned timers; release only owned polling locks. Verify the actual Spring lifecycle phases with tests rather than trusting the existing queue comment.

A deployment should drain old Telegram instances before enabling the new polling owners. Old instances still containing unconditional `forceUnlock` can undermine the new ownership rule; no unsafe rolling-compatibility claim is made.

### Messages, commands, and reply presentation

Keep the provider-specific event and command parsing in the Telegram package. Existing binding services and persistence tables are unchanged. Change the handler/dispatcher to return a stage for the entire turn and use `ChatStreamHandle` for cancellation and settlement.

Refactor the raw transport behind the neutral contracts. Convert IDs, `InputFile`, parse modes, SDK receipts, and SDK failures only inside Telegram. Update internal command/handler callers to the new API rather than leaving an obsolete parallel transport path.

Retain `TelegramStreamingReplyEmitter` for Telegram presentation rules, with state transitions:

```text
NEW → STREAMING → FINALIZING → TERMINAL
```

Success, error, timeout, and cancellation converge on one terminal completion future. The emitter buffers token callbacks, schedules coalesced partial updates, stops typing before finalization, and freezes a final delivery plan. It tracks confirmed message IDs and formatting as well as text. Split offsets are advanced only when the corresponding delivery step is confirmed. Failed replacement placeholders cannot cause the previous message to be overwritten with the next segment.

Final rate-limited edits receive scheduled retries without another token arriving. Image sends are attempted once per planned image except for confirmed non-acceptance retries. Completion cannot send images twice. Recording delivery results is separate from sending so a persistence failure cannot resend a delivered photo.

The generic runtime handles scheduling, capacity, cancellation, and retries. Telegram retains Markdown balancing, approximately 4,000-character segments, image-markdown extraction, 500 ms partial-update timing, and four-second typing refresh. Other plugins are not required to implement Telegram formatting.

## 9. Identity and authorization boundaries

Telegram keeps its existing `TgChatBindingService`, `tg-*` identities, negative group IDs, tables, and long-term-memory scope values. Do not replace them with plugin-prefixed or sender-based identities.

A new plugin using built-in FreeChat AI conversations should bind its external conversation to a real, authenticated FreeChat account and a permitted backend, retaining that mapping in plugin-owned storage. It must verify the external service's request and explicit account-link proof, check account/backend/chat authorization, and revalidate revocation when queued work executes. Calling a service directly does not automatically run the REST controller's authorization annotations.

The existing account/backend conversation may be shared with web chat. The guide must explain that consequence, and group chats must not silently share a person's private memory. The small runnable plugin example uses an echo response and therefore does not pretend to implement account linking. A separate AI-binding walkthrough explains the necessary application-specific authorization work.

Plugins may instead use their own conversation engine/storage while still using the shared channel runtime. Arbitrary synthetic users are not automatically supported by built-in long-term memory. Do not fake a `User`, fill Telegram fields for another provider, or broaden identity validation to accept any string prefix. Generalizing that service-domain identity model is outside this migration.

## 10. Implementation sequence and file plan

1. **Contracts and reusable scheduler.** Add the SPI/value types under `freechat-common/src/main/java/fun/freechat/channels/spi/`; add `ChannelRegistry`, `ChannelRuntime`, the keyed scheduler, delivery/timer support, and polling ownership under `freechat-service/src/main/java/fun/freechat/service/channel/`. Add focused tests for registration, FIFO, limits, cancellation, retries, and optional capabilities before migrating Telegram.
2. **Cancellable chat-service integration.** Add `ChatStreamHandle`; update `ChatTask`, `ChatTaskQueue`, `ChatService`, and `ChatServiceImpl` narrowly. Extend tests for cancellation before/during construction, late callbacks, queue draining, and settlement after coordination release. Preserve existing queue and LTM tests.
3. **Telegram registration and raw transport.** Add `TelegramChannelPlugin`; adapt the manager and transport to the shared runtime; remove the old constructor cycle and redundant Telegram-only event plumbing. Preserve existing API consumers and token encryption. Test a Spring context with an additional text-only plugin.
4. **Telegram receive/reply path.** Adapt dispatcher, commands, and binding handler to full-turn completion; update the emitter state machine, timer usage, splitting, terminal retries, and confirmed delivery recording. Keep incoming media recording and outgoing media types unchanged.
5. **Lifecycle and transport integration tests.** Use private Redis for lease ownership/takeover and WireMock or local SDK stubs for Telegram. Test token rotation, deletion, saturation, cancelled generations, ambiguous sends, terminal 429s, and shutdown. No real user messages or credentials are needed.
6. **Developer documentation.** Write `docs/channel-plugins.md` and `docs/channel-plugins.zh-CN.md`; link them from the matching root READMEs. Include a complete minimal plugin, registration from another package/JAR, an AI-binding walkthrough, optional media/edit/status examples, limits and retries, security, testing, deployment, and troubleshooting. Keep an executable test fixture aligned with the minimal example.
7. **Completion audit.** Run the full unit suite, targeted Redis and transport tests, and changed-file formatting checks; inspect the actual diff and all acceptance criteria in section 5. Check both language guides against the compiled API. Report any remaining live-service verification limits explicitly.

No database schema, generated DAL/SDK code, CI pipeline, live deployment, or user configuration secrets are changed by this plan. Existing untracked `.claude/` content is unrelated and will be left untouched.

## 11. Refinements found during implementation and testing

The public contracts and preservation requirements remained the same. Checking real dependency behavior exposed several details that required stronger implementation rules:

- **Physical sends have their own completion signal.** Cancelling a caller's observation future does not complete a turn's delivery barrier. Owner cancellation preserves a receipt from an already running, subsequently accepted send. Receive capacity is released only after the handler/recording chain and all admitted physical sends settle.
- **Some model streams cannot cancel.** LangChain4j's ordinary streaming implementation is not `AutoCloseable`. A started noncancellable stream retains chat coordination until its natural terminal event and callbacks finish; a never-started cancelled stream can settle without invocation. A timeout or thread interruption is not proof of termination.
- **The runtime is the sole plugin destruction owner.** `ChannelPluginBeanPostProcessor` disables Spring's inferred `close()` hook for plugin beans. Lifecycle state is checked inside the same monitor used for startup and disposal, so delayed reconciliation cannot restart a disposed plugin.
- **Telegram polling resources are explicitly owned.** `TelegramPollingSession` replaces the opaque SDK application/session registry with a finite-timeout HTTP client and virtual polling worker. Failed registration cleans up transactionally; retirement waits for the worker and HTTP dispatcher before the Redis owner releases its lock.
- **HTTP libraries must not retry underneath the scheduler.** `TelegramHttpClient` disables connection retries and redirects and guards against a second network transmission within one call, including a `503` response with `Retry-After: 0`. Polling errors are sanitized at the owned polling loop rather than logged by the SDK's internal session.
- **Delivery failures are isolated.** A failed final text edit still permits the generated image plan to run, and one failed image does not suppress later images. Partial edit failures can recover at final delivery. Only confirmed text/media are recorded, without replaying successful sends after recording errors.
- **SDK-shaped test data matters.** A successful Telegram message response needs a positive `date`; the SDK interprets `date: 0` as an inaccessible-message variant. Local HTTP tests use real SDK parsing and case-insensitive API method matching, not only mocked Java method calls.

The implementation adds an executable external-JAR test, Spring destruction tests, cancellation/physical-settlement tests, local Telegram HTTP tests, and private-Redis ownership tests. The integration guides explain the resulting guarantees and deliberately do not promise durable delivery, arbitrary synthetic-account support, or live Telegram validation.

## 12. Verification and requirement audit

The final clean verification completed successfully on 2026-10-09:

```shell
mvn -B -Pspotless clean verify spotless:check \
  -Dit.test=ChannelPollingLeaseIT,ChatTaskQueueCoordinationIT \
  -Dfailsafe.failIfNoSpecifiedTests=false
```

It passed **1,282 unit/local-HTTP tests** and **46 private-Redis integration tests**, with no failures, errors, or skipped tests in those selected suites. Changed-file formatting also passed. The unit total includes 24 common, 67 LangChain4j, 526 service, and 665 start-module tests. Integration coverage consists of 40 chat-coordination cases and six polling-lease cases.

| Requested outcome | Concrete evidence |
| --- | --- |
| Examine the existing Telegram integration | Section 2 records the original lifecycle, scheduling, commands, media, typing, storage, and API behavior; section 4 records the pre-change baseline. |
| Required/optional plugin interfaces and registration | `freechat-common/src/main/java/fun/freechat/channels/spi/`, `ChannelRegistry`, the executable `DemoChannelPluginTest`, and `ExternalChannelPluginTest`, which compiles a separate JAR and loads its Spring Boot metadata. |
| Generic receive/send concurrency | `ChannelTaskScheduler`, `ChannelPluginRuntime`, `ChannelDelivery`, and their tests cover FIFO, independent capacities, saturation, cancellation, actual completion barriers, retries, generation changes, and shutdown. |
| Telegram migration without losing media/status features | `TelegramChannelPlugin`, the neutral transport, command/handler/emitter migration, and manager/pipeline/transport/emitter/HTTP suites exercise media kinds, uploads, captions, typing, partial/final edits, splitting, images, failures, and lifecycle recovery. Existing identity/privacy tests continue to pass. |
| Detailed design and plan before coding | Sections 1–10 were completed before implementation; section 11 records later refinements found through dependency inspection and tests. |
| Junior-friendly English and Chinese integration documentation | [English guide](channel-plugins.md) and [Chinese guide](channel-plugins.zh-CN.md), linked from their root READMEs, explain terminology and include a tested starter, registration, secure AI binding, optional features, limits, troubleshooting, and deployment. Local links, starter-source alignment, and bilingual code/command parity were checked. |

No live Telegram bot, external model service, deployment, or database migration was exercised by this verification. Provider-facing tests use the actual Telegram SDK against local HTTP stubs with fabricated credentials; Redis tests use privately owned containers. Unrelated integration suites requiring application databases or live-provider credentials were not selected.
