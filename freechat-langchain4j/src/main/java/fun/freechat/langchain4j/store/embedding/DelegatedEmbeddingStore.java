package fun.freechat.langchain4j.store.embedding;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static dev.langchain4j.internal.Utils.getOrDefault;
import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;

@Slf4j
public class DelegatedEmbeddingStore implements EmbeddingStore<TextSegment> {
    private static final int DEFAULT_BATCH_SIZE = 100;

    private final EmbeddingStore<TextSegment> embeddingStore;
    private final int batchSize;

    @Builder
    public DelegatedEmbeddingStore(EmbeddingStore<TextSegment> embeddingStore, Integer batchSize) {
        this.embeddingStore = ensureNotNull(embeddingStore, "embeddingStore");
        this.batchSize = getOrDefault(batchSize, DEFAULT_BATCH_SIZE);
    }

    @Override
    public String add(Embedding embedding) {
        return embeddingStore.add(embedding);
    }

    @Override
    public void add(String id, Embedding embedding) {
        embeddingStore.add(id, embedding);
    }

    @Override
    public String add(Embedding embedding, TextSegment textSegment) {
        return embeddingStore.add(embedding, textSegment);
    }

    @Override
    public List<String> addAll(List<Embedding> embeddings) {
        int size = embeddings.size();
        if (size <= batchSize) {
            return embeddingStore.addAll(embeddings);
        }

        List<String> ids = new ArrayList<>(size);
        int fromIndex = 0;
        int toIndex = batchSize;
        while (toIndex < size) {
            List<String> batchIds = embeddingStore.addAll(embeddings.subList(fromIndex, toIndex));
            ids.addAll(batchIds);
            log.info("Added {}/{} embeddings to embedding-store", toIndex, size);
            fromIndex = toIndex;
            toIndex = Math.min(size, fromIndex + batchSize);
        }

        return ids;
    }

    @Override
    public List<String> addAll(List<Embedding> embeddings, List<TextSegment> embedded) {
        int size = embeddings.size();
        if (size <= batchSize) {
            return embeddingStore.addAll(embeddings, embedded);
        }

        List<String> ids = new ArrayList<>(size);
        int fromIndex = 0;
        int toIndex = batchSize;
        while (fromIndex < size) {
            List<Embedding> batchEmbeddings = embeddings.subList(fromIndex, toIndex);
            List<TextSegment> batchEmbedded = embedded.subList(fromIndex, toIndex);
            List<String> batchIds = embeddingStore.addAll(batchEmbeddings, batchEmbedded);
            ids.addAll(batchIds);
            log.info("Added {}/{} embeddings and embedded to embedding-store", toIndex, size);
            fromIndex = toIndex;
            toIndex = Math.min(size, fromIndex + batchSize);
        }

        return ids;
    }

    @Override
    public EmbeddingSearchResult<TextSegment> search(EmbeddingSearchRequest request) {
        return embeddingStore.search(request);
    }

    @Override
    public List<EmbeddingMatch<TextSegment>> findRelevant(Embedding referenceEmbedding, int maxResults) {
        return embeddingStore.findRelevant(referenceEmbedding, maxResults);
    }

    @Override
    public List<EmbeddingMatch<TextSegment>> findRelevant(Embedding referenceEmbedding, int maxResults, double minScore) {
        return embeddingStore.findRelevant(referenceEmbedding, maxResults, minScore);
    }

    @Override
    public List<EmbeddingMatch<TextSegment>> findRelevant(
            Object memoryId, Embedding referenceEmbedding, int maxResults) {
        return embeddingStore.findRelevant(memoryId, referenceEmbedding, maxResults);
    }

    @Override
    public List<EmbeddingMatch<TextSegment>> findRelevant(
            Object memoryId, Embedding referenceEmbedding, int maxResults, double minScore) {
        return embeddingStore.findRelevant(memoryId, referenceEmbedding, maxResults, minScore);
    }
}
