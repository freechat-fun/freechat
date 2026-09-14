package fun.freechat.service.chat.memory;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import fun.freechat.service.enums.EmbeddingStoreType;
import fun.freechat.service.rag.EmbeddingModelService;
import fun.freechat.service.rag.ExactEmbeddingStoreService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class MemoryVectorRepository {
    private final ExactEmbeddingStoreService stores;
    private final EmbeddingModelService models;
    private final MemoryBoundsFactory boundsFactory;
    private final LongTermMemoryProperties properties;

    public MemoryVectorRepository(
            ExactEmbeddingStoreService stores,
            EmbeddingModelService models,
            MemoryBoundsFactory boundsFactory,
            LongTermMemoryProperties properties) {
        this.stores = Objects.requireNonNull(stores);
        this.models = Objects.requireNonNull(models);
        this.boundsFactory = Objects.requireNonNull(boundsFactory);
        this.properties = Objects.requireNonNull(properties);
    }

    public MemoryDocumentCodec codec(MemoryScope scope) {
        try {
            return new MemoryDocumentCodec(boundsFactory.forLanguage(language(scope.storeType())), properties);
        } catch (RuntimeException ignored) {
            throw new IllegalStateException("Memory vector codec initialization failed");
        }
    }

    public void insertPrepared(
            MemoryScope scope,
            String commitId,
            MemoryManifest manifest,
            List<MemoryDocument> documents,
            Runnable checkLease) {
        MemoryDocumentCodec codec = codec(scope);
        if (documents.stream()
                        .anyMatch(document -> !scope.equals(document.scope()) || !commitId.equals(document.commitId()))
                || !manifest.equals(MemoryManifest.of(documents, codec))) {
            throw new IllegalArgumentException("Memory records do not match the prepared manifest");
        }
        List<TextSegment> segments = documents.stream().map(codec::encode).toList();
        List<TextSegment> inputs = documents.stream()
                .map(document -> TextSegment.from(embeddingText(document)))
                .toList();
        try {
            checkLease.run();
            List<Embedding> embeddings = models.modelForLang(language(scope.storeType()))
                    .embedAll(inputs)
                    .content();
            checkLease.run();
            stores.of(scope.chatId(), scope.storeType()).addAll(manifest.ids(), embeddings, segments);
            checkLease.run();
            for (MemoryManifest.Entry entry : manifest.entries()) {
                read(scope, commitId, entry)
                        .orElseThrow(() -> new IllegalStateException("Memory vector verification failed"));
                checkLease.run();
            }
        } catch (RuntimeException ignored) {
            throw new IllegalStateException("Prepared memory vector write failed");
        }
    }

    public Optional<String> findCommit(MemoryScope scope, String recordId) {
        if (!MemoryDocumentCodec.canonicalUuid(recordId)) {
            throw new IllegalArgumentException("Invalid memory record identifier");
        }
        try {
            return stores.get(scope.storeType(), recordId, scope.filter()).map(segment -> {
                String commitId = segment.metadata().getString("commit_id");
                if (!MemoryDocumentCodec.canonicalUuid(commitId)) {
                    throw new IllegalStateException("Invalid memory commit identifier");
                }
                return commitId;
            });
        } catch (RuntimeException ignored) {
            throw new IllegalStateException("Memory commit lookup failed");
        }
    }

    public Optional<MemoryDocument> read(MemoryScope scope, String commitId, MemoryManifest.Entry entry) {
        try {
            return stores.get(scope.storeType(), entry.id(), scope.filter()).map(segment -> {
                MemoryDocument document = codec(scope).decode(segment, scope, entry.id(), entry.hash());
                if (!commitId.equals(document.commitId())
                        || entry.kind() != document.kind()
                        || entry.archive() != document.archive()) {
                    throw new IllegalStateException("Memory record does not match its manifest");
                }
                return document;
            });
        } catch (RuntimeException ignored) {
            throw new IllegalStateException("Memory vector read failed");
        }
    }

    public EmbeddingSearchResult<TextSegment> candidates(MemoryScope scope, String query, int limit) {
        if (query == null
                || query.isBlank()
                || query.codePointCount(0, query.length()) > properties.getSearchQueryMaxChars()
                || limit <= 0
                || limit > 90) {
            throw new IllegalArgumentException("Invalid memory search arguments");
        }
        try {
            String language = language(scope.storeType());
            Embedding embedding = models.modelForLang(language)
                    .embed(models.queryPrefixForLang(language) + query)
                    .content();
            return stores.of(scope.chatId(), scope.storeType())
                    .search(EmbeddingSearchRequest.builder()
                            .queryEmbedding(embedding)
                            .filter(scope.filter()
                                    .and(
                                            dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey(
                                                            "record_kind")
                                                    .isEqualTo(MemoryDocument.Kind.EPISODE_SUMMARY.name())
                                                    .or(dev.langchain4j.store.embedding.filter.MetadataFilterBuilder
                                                            .metadataKey("record_kind")
                                                            .isEqualTo(MemoryDocument.Kind.WINDOW_SUMMARY.name())
                                                            .and(dev.langchain4j.store.embedding.filter
                                                                    .MetadataFilterBuilder.metadataKey("archive")
                                                                    .isEqualTo(1)))))
                            .maxResults(limit)
                            .minScore(properties.getSearchMinScore())
                            .build());
        } catch (RuntimeException ignored) {
            throw new IllegalStateException("Memory vector search failed");
        }
    }

    private static String embeddingText(MemoryDocument document) {
        List<String> values = new ArrayList<>();
        if (!document.summary().isEmpty()) {
            values.add(document.summary());
        }
        document.userFacts().forEach(fact -> values.add(fact.value()));
        document.characterDeltas().forEach(fact -> values.add(fact.value()));
        return values.isEmpty() ? "Session profile has no retained facts." : String.join("\n", values);
    }

    private static String language(EmbeddingStoreType type) {
        return switch (type) {
            case EN_LONG_TERM_MEMORY -> "en";
            case ZH_LONG_TERM_MEMORY -> "zh";
            case DEFAULT_LONG_TERM_MEMORY -> "default";
            default -> throw new IllegalArgumentException("Unsupported memory embedding collection");
        };
    }
}
