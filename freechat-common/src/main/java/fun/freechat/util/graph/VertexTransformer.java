package fun.freechat.util.graph;

import org.apache.commons.collections4.Transformer;

public class VertexTransformer<T> implements Transformer<DiGraph.Vertex<T>, T> {
    @Override
    public T transform(DiGraph.Vertex<T> v) {
        return v.getData();
    }
}
