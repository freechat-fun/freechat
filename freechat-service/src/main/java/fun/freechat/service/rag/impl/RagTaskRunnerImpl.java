package fun.freechat.service.rag.impl;

import dev.langchain4j.data.document.*;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.source.FileSystemSource;
import dev.langchain4j.data.document.source.UrlSource;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.document.transformer.HtmlTextExtractor;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.HuggingFaceTokenizer;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import fun.freechat.model.RagTask;
import fun.freechat.service.enums.SourceType;
import fun.freechat.service.rag.*;
import fun.freechat.service.util.StoreUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@SuppressWarnings("unused")
public class RagTaskRunnerImpl implements RagTaskRunner {
    private static final String LOCK_PREFIX = "RagTaskLock-";

    @Value("${rag.maxSegmentSize}")
    private Integer maxSegmentSize;
    @Value("${rag.maxOverlapSize}")
    private Integer maxOverlapSize;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    @Qualifier("inMemoryEmbeddingStoreService")
    private EmbeddingStoreService<TextSegment> embeddingStoreService;
    @Autowired
    @Qualifier("spiEmbeddingModelService")
    private EmbeddingModelService embeddingModelService;
    @Autowired
    private RedissonClient redisson;
    @Autowired
    private Tika tika;

    @Override
    @Async("defaultExecutor")
    public CompletableFuture<Void> start(RagTask task) {
        String memoryId = task.getCharacterUid();
        RLock lock = redisson.getLock(LOCK_PREFIX + memoryId);
        boolean locked = false;

        try {
            locked = lock.tryLock(30, 60, TimeUnit.SECONDS);
            eventPublisher.publishEvent(new RagTaskStartedEvent(task));
            SourceType sourceType = SourceType.of(task.getSourceType());
            DocumentSource source = switch (sourceType) {
                case FILE -> FileSystemSource.from(StoreUtils.defaultFileStore().toPath(task.getSource()));
                case URL -> UrlSource.from(task.getSource());
                default -> throw new NotImplementedException("Not implemented.");
            };

            Metadata tikaMetadata = new Metadata();
            DocumentParser parser = new ApacheTikaDocumentParser(null, null, tikaMetadata, null);
            Document document = DocumentLoader.load(source, parser);
            for (String metaName: tikaMetadata.names()) {
                document.metadata().add(metaName, tikaMetadata.get(metaName));
            }

            EmbeddingModel embeddingModel = embeddingModelService.from(memoryId);
            EmbeddingStore<TextSegment> embeddingStore = embeddingStoreService.from(memoryId);
            DocumentTransformer documentTransformer = isHtml( document) ? new HtmlTextExtractor() : null;
            DocumentSplitter documentSplitter = DocumentSplitters.recursive(
                    maxSegmentSize, maxOverlapSize, new HuggingFaceTokenizer());

            EmbeddingStoreIngestor.builder()
                    .embeddingModel(embeddingModel)
                    .embeddingStore(embeddingStore)
                    .documentTransformer(documentTransformer)
                    .documentSplitter(documentSplitter)
                    .build()
                    .ingest(document);

            embeddingStoreService.save(memoryId, embeddingStore);

            eventPublisher.publishEvent(new RagTaskSucceededEvent(task));
            return CompletableFuture.completedFuture(null);
        } catch (Exception ex) {
            log.error("Failed to run rag task for [{}]", task.getSource(), ex);
            return CompletableFuture.failedFuture(ex);
        } finally {
            if (locked) {
                lock.unlock();
            }
        }
    }

    private boolean isHtml(Document document) {
        byte[] data = document.text().getBytes(StandardCharsets.UTF_8);
        return "text/html".equals(tika.detect(data));
    }
}
