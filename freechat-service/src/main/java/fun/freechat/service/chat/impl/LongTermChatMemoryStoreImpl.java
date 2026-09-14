package fun.freechat.service.chat.impl;

import static fun.freechat.service.enums.EmbeddingStoreType.longTermMemoryTypeForLang;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import fun.freechat.model.ChatMemoryState;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.ChatContextService;
import fun.freechat.service.chat.ChatSession;
import fun.freechat.service.chat.LongTermChatMemoryStore;
import fun.freechat.service.chat.memory.*;
import fun.freechat.service.prompt.PromptService;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class LongTermChatMemoryStoreImpl implements LongTermChatMemoryStore {
    private final ChatContextService contexts;
    private final CharacterService characters;
    private final PromptService prompts;
    private final MemoryModelResolver models;
    private final MemoryLifecycleRepository lifecycle;
    private final MemoryHistoryReconciler reconciler;
    private final MemoryTurnRepository turns;
    private final MemorySourceReader sources;
    private final MemoryPublicationRepository publications;
    private final MemoryPublisher publisher;
    private final MemoryVectorRepository vectors;
    private final MemoryConsolidator consolidator;
    private final MemoryBoundsFactory boundsFactory;
    private final LongTermMemoryProperties properties;
    private final ScheduledExecutorService renewals;

    public LongTermChatMemoryStoreImpl(
            ChatContextService contexts,
            CharacterService characters,
            PromptService prompts,
            MemoryModelResolver models,
            MemoryLifecycleRepository lifecycle,
            MemoryHistoryReconciler reconciler,
            MemoryTurnRepository turns,
            MemorySourceReader sources,
            MemoryPublicationRepository publications,
            MemoryPublisher publisher,
            MemoryVectorRepository vectors,
            MemoryConsolidator consolidator,
            MemoryBoundsFactory boundsFactory,
            LongTermMemoryProperties properties,
            @Qualifier("memoryTurnRenewals") ScheduledExecutorService renewals) {
        this.contexts = contexts;
        this.characters = characters;
        this.prompts = prompts;
        this.models = models;
        this.lifecycle = lifecycle;
        this.reconciler = reconciler;
        this.turns = turns;
        this.sources = sources;
        this.publications = publications;
        this.publisher = publisher;
        this.vectors = vectors;
        this.consolidator = consolidator;
        this.boundsFactory = boundsFactory;
        this.properties = properties;
        this.renewals = renewals;
    }

    @Override
    public boolean enabled(String chatId) {
        if (chatId == null || chatId.endsWith("-assist")) {
            return false;
        }
        var context = contexts.get(chatId);
        if (context == null) {
            return false;
        }
        var backend = characters.getBackend(context.getBackendId());
        return backend != null
                && backend.getLongTermMemoryWindowSize() != null
                && backend.getLongTermMemoryWindowSize() > 0;
    }

    @Override
    public Optional<Binding> open(String chatId, ChatSession session) {
        if (chatId.endsWith("-assist")) {
            return Optional.empty();
        }
        var resolution = models.resolveForSession(chatId, session.getMemoryFingerprint());
        if (resolution.isEmpty()) {
            lifecycle.disable(chatId);
            return Optional.empty();
        }
        var model = resolution.get().resolved();
        List<ChatMessage> examples = resolution.get().configuration().examples();
        ChatMemoryState state = lifecycle.activate(
                new MemoryScope(
                        chatId, model.userId(), model.characterUid(), 1, longTermMemoryTypeForLang(model.language())),
                model.fingerprint());
        MemoryScope scope = new MemoryScope(
                chatId,
                state.getUserId(),
                state.getCharacterUid(),
                state.getGeneration(),
                longTermMemoryTypeForLang(model.language()));
        turns.recoverExpired(scope);
        var reconciliation = reconciler.reconcile(scope, model.fingerprint(), examples);
        if (!reconciliation.complete()) {
            throw new IllegalStateException("Chat history reconciliation is in progress; retry the request");
        }
        var lease = turns.begin(scope, model.fingerprint());
        MemoryTurnLifetime lifetime = null;
        try {
            MemoryBounds bounds = boundsFactory.forLanguage(model.language());
            MemoryInvocation memory = new MemoryInvocation(
                    lease, model, turns, sources, publications, publisher, consolidator, bounds, properties);
            lifetime = new MemoryTurnLifetime(turns, memory, properties, renewals);
            var recall = new MemoryRecallTools(
                    scope, model.recallLimit(), vectors, publisher, bounds, properties, lifetime::check);
            var streaming = new MemoryStreamingModel(session.getStreamingChatModel(), memory, lifetime);
            ChatSession invocation = session.forInvocation(
                    memory, streaming, List.of(recall), properties.getMaxToolRounds(), lifetime::check);
            memory.tools(invocation.getToolSpecifications());
            memory.add(SystemMessage.from(prompts.apply(
                    invocation.getPrompt().getSystem(), invocation.getVariables(), invocation.getPromptFormat())));
            if (reconciliation.throughId() == 0) {
                examples.forEach(memory::addExample);
            }
            return Optional.of(new Binding(invocation, memory, lifetime));
        } catch (Throwable failure) {
            try {
                if (lifetime != null) {
                    lifetime.close();
                } else {
                    turns.abort(lease);
                }
            } catch (Throwable ignored) {
                // A failed close remains fenced by the durable lease and absolute deadline.
            }
            if (failure instanceof Error) {
                throw new Error("Chat memory initialization failed");
            }
            throw new IllegalStateException("Chat memory initialization failed");
        }
    }
}
