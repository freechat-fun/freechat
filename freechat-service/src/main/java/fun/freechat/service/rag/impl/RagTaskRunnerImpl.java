package fun.freechat.service.rag.impl;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentLoader;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSource;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.DocumentTransformer;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.source.FileSystemSource;
import dev.langchain4j.data.document.source.UrlSource;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.document.transformer.jsoup.HtmlToTextDocumentTransformer;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.internal.Utils;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import fun.freechat.annotation.Trace;
import fun.freechat.model.CharacterInfo;
import fun.freechat.model.RagTask;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.enums.SourceType;
import fun.freechat.service.rag.EmbeddingModelService;
import fun.freechat.service.rag.EmbeddingStoreService;
import fun.freechat.service.rag.RagTaskRunner;
import fun.freechat.service.rag.RagTaskStartedEvent;
import fun.freechat.service.rag.RagTaskSucceededEvent;
import fun.freechat.service.util.StoreUtils;
import fun.freechat.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.tika.Tika;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static fun.freechat.service.enums.EmbeddingRecordMeta.MEMORY_ID;
import static fun.freechat.service.enums.EmbeddingRecordMeta.TASK_ID;
import static fun.freechat.service.enums.EmbeddingStoreType.documentTypeForLang;

@Service
@Slf4j
@SuppressWarnings("unused")
public class RagTaskRunnerImpl implements RagTaskRunner {
    private static final String LOCK_PREFIX = "RagTaskRunnerLock-";

    @Value("${chat.rag.defaultMaxSegmentSize}")
    private Integer defaultMaxSegmentSize;
    @Value("${chat.rag.defaultMaxOverlapSize}")
    private Integer defaultMaxOverlapSize;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private CharacterService characterService;
    @Autowired
    private EmbeddingStoreService<TextSegment> embeddingStoreService;
    @Autowired
    private EmbeddingModelService embeddingModelService;
    @Autowired
    private RedissonClient redisson;
    @Autowired
    private Tika tika;

    @Override
    @Async("ragExecutor")
    @Trace(ignoreReturn = true)
    public CompletableFuture<Void> start(RagTask task) {
        String memoryId = task.getCharacterUid();
        RLock lock = redisson.getLock(LOCK_PREFIX + memoryId);
        boolean locked = false;

        try {
            locked = lock.tryLock(30, 5400, TimeUnit.SECONDS);
            eventPublisher.publishEvent(new RagTaskStartedEvent(task));

            String lang = Optional.ofNullable(memoryId)
                    .map(characterService::summaryByUid)
                    .map(CharacterInfo::getLang)
                    .orElseThrow(() -> new IllegalArgumentException("Can't find character by uid[" + memoryId + "]"));

            SourceType sourceType = SourceType.of(task.getSourceType());
            DocumentSource source = switch (sourceType) {
                case FILE -> FileSystemSource.from(StoreUtils.defaultFileStore().toPath(task.getSource()));
                case URL -> {
                    if (!HttpUtils.isSafeUrl(task.getSource())) {
                        throw new IllegalArgumentException("Illegal url: " + task.getSource());
                    }
                    yield UrlSource.from(task.getSource());
                }
                default -> throw new NotImplementedException("Not implemented.");
            };
            Integer maxSegmentSize = Utils.getOrDefault(task.getMaxSegmentSize(), defaultMaxSegmentSize);
            Integer maxOverlapSize = Utils.getOrDefault(task.getMaxOverlapSize(), defaultMaxOverlapSize);

            DocumentParser parser = new ApacheTikaDocumentParser(true);
            Document document = DocumentLoader.load(source, parser);
            document.metadata().put(MEMORY_ID.text(), memoryId);
            document.metadata().put(TASK_ID.text(), task.getId());


            EmbeddingModel embeddingModel = embeddingModelService.modelForLang(lang);
            TokenCountEstimator tokenCountEstimator = embeddingModelService.tokenCountEstimatorForLang(lang);
            EmbeddingStore<TextSegment> embeddingStore =
                    embeddingStoreService.of(memoryId, documentTypeForLang(lang));
            DocumentTransformer documentTransformer = isHtml(document) ? new HtmlToTextDocumentTransformer() : null;
            DocumentSplitter documentSplitter = DocumentSplitters.recursive(maxSegmentSize, maxOverlapSize, tokenCountEstimator);

            EmbeddingStoreIngestor.builder()
                    .embeddingModel(embeddingModel)
                    .embeddingStore(embeddingStore)
                    .documentTransformer(documentTransformer)
                    .documentSplitter(documentSplitter)
                    .build()
                    .ingest(document);

            embeddingStoreService.flush(memoryId, documentTypeForLang(lang), embeddingStore);

            eventPublisher.publishEvent(new RagTaskSucceededEvent(task));
            return CompletableFuture.completedFuture(null);
        } catch (Exception ex) {
            log.error("Failed to run rag task for [{}]", task.getSource(), ex);
            return CompletableFuture.failedFuture(ex);
        } finally {
            if (locked) {
                try {
                    lock.unlock();
                } catch (Exception unlockEx) {
                    log.warn("Unlock failed!", unlockEx);
                }
            }
        }
    }

    private boolean isHtml(Document document) {
        byte[] data = document.text().getBytes(StandardCharsets.UTF_8);
        return "text/html".equals(tika.detect(data));
    }
}
