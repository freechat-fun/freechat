package fun.freechat.langchain4j.rag.query.transformer;

import dev.langchain4j.rag.query.Query;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import lombok.Builder;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;

public class DelegatedQueryTransformer implements QueryTransformer {
    private final QueryTransformer queryTransformer;
    private final Function<Collection<Query>, Collection<Query>> postProcessor;

    @Builder
    public DelegatedQueryTransformer(QueryTransformer queryTransformer,
                                     Function<Collection<Query>, Collection<Query>> postProcessor) {
        this.queryTransformer = queryTransformer;
        this.postProcessor = postProcessor;
    }

    @Override
    public Collection<Query> transform(Query query) {
        Collection<Query> queries = Objects.nonNull(queryTransformer) ?
                queryTransformer.transform(query) :
                Collections.singleton(query);

        return Objects.nonNull(postProcessor) ? postProcessor.apply(queries) : queries;
    }
}
