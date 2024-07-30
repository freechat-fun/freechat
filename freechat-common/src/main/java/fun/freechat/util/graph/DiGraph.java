package fun.freechat.util.graph;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Directed Graph, uses adjacency list storage structure, refer to:
 * @link <a href="https://en.wikipedia.org/wiki/Adjacency_list">Adjacency List</a>
 * <p>
 * Sorting logic:
 * 1. Vertices with only outgoing edges are root vertices, with a value of 0. A directed graph may have multiple root vertices or no root vertex.
 * 2. For a non-root vertex, its value is the maximum number of edges in all paths from the root vertices to this vertex.
 * 3. If the graph contains a cycle, the vertices in the cycle will have the same value, which is the value of the vertex farthest from the root vertex in the cycle (i.e., the maximum value among the vertices in the cycle).
 * Explanation:
 * A directed acyclic graph always has at least one root vertex.
 * For a non-root vertex n, its value must be greater than the values of all its predecessors, otherwise it contradicts the fact that the value is calculated based on the "longest path".
 * If a cycle exists, the vertices in the cycle have no absolute predecessor or successor relationship. The entire cycle is treated as a single vertex in terms of logic and has the same value.
 * For vertices without predecessor or successor relationships, their values may be the same (but not necessarily). The order of traversal is not fixed.
 */
@SuppressWarnings({"unused", "NullableProblems"})
public class DiGraph<T> implements Graph<T> {
    public static class Vertex<T> implements Serializable {
        private final List<Vertex<T>> successors = new LinkedList<>();
        private final List<Vertex<T>> predecessors = new LinkedList<>();
        private final T o;
        private int v;

        Vertex(T o) {
            this.o = o;
            this.v = 0;
        }

        public T getData() {
            return o;
        }

        public int getValue() {
            return v;
        }

        public void setValue(int v) {
            this.v = v;
        }

        public int calculateValue(List<Vertex<T>> excludeVertexes) {
            // 1. 如果没有前继顶点，值为0
            // 2. 如果有前继顶点，找到其中的最大数值（即位于最长路径中的前继顶点的数值），增加 1 作为新值

            return predecessors.stream()
                    .filter(v -> !excludeVertexes.contains(v))
                    .mapToInt(Vertex::getValue)
                    .max()
                    .orElse(-1) + 1;
        }

        public boolean updateValueIfNecessary() {
            int nextValue = calculateValue(Collections.emptyList());

            if (v != nextValue) {
                v = nextValue;
                return true;
            }
            return false;
        }

        @SuppressWarnings("UnusedReturnValue")
        boolean addSuccessor(Vertex<T> successor) {
            return successors.add(successor);
        }

        @SuppressWarnings("UnusedReturnValue")
        boolean removeSuccessor(Vertex<T> successor) {
            return successors.remove(successor);
        }

        boolean hasSuccessor(Vertex<T> successor) {
            return successors.contains(successor);
        }

        List<Vertex<T>> getSuccessors() {
            return successors;
        }

        int successorCount() {
            return successors.size();
        }

        @SuppressWarnings("UnusedReturnValue")
        boolean addPredecessor(Vertex<T> predecessor) {
            return predecessors.add(predecessor);
        }

        @SuppressWarnings("UnusedReturnValue")
        boolean removePredecessor(Vertex<T> predecessor) {
            return predecessors.remove(predecessor);
        }

        int predecessorCount() {
            return predecessors.size();
        }

        List<Vertex<T>> getPredecessors() {
            return predecessors;
        }

        public String toDot(Function<T, String> converter) {
            if (CollectionUtils.isEmpty(successors)) {
                if (CollectionUtils.isEmpty(predecessors)) {
                    return converter.apply(o) + ";\n";
                } else {
                    return "";
                }
            } else {
                return successors.stream()
                        .map(s -> converter.apply(o) + " -> " + converter.apply(s.getData()) + ";\n")
                        .collect(Collectors.joining());
            }
        }

        @Override
        public String toString() {
            return "Vertex{" +
                    "successors=" + successors +
                    ", predecessors=" + predecessors +
                    ", o=" + o +
                    ", v=" + v +
                    '}';
        }
    }

    private final LinkedList<Vertex<T>> vertexList = new LinkedList<>();

    // 对象->顶点映射。
    private final HashMap<T, Vertex<T>> indexMap = new HashMap<>();

    private List<Vertex<T>> findVertexesInCycle(Vertex<T> entryVertex) {
        return entryVertex.getSuccessors()
                .stream()
                .filter(successor -> canReach(successor, entryVertex, new LinkedList<>()))
                .toList();
    }

    private void updateSubGraphValuesIfNecessary(Vertex<T> root) {
        List<Vertex<T>> rootVertexes = new LinkedList<>();
        rootVertexes.add(root);

        if (allowCycle()) {
            // 如果存在环，环中的顶点无绝对的前后继关系，统一更新值，环中顶点的子图，都可能需要更新值
            // 简而言之，整个环逻辑上被当作一个顶点
            List<Vertex<T>> cycleVertexes = findVertexesInCycle(root);

            if (CollectionUtils.isNotEmpty(cycleVertexes)) {
                int maxValue = cycleVertexes.stream()
                        .mapToInt(v -> v.calculateValue(cycleVertexes))
                        .max()
                        .orElse(0);

                for (Vertex<T> vertex : cycleVertexes) {
                    if (vertex.getValue() == maxValue) {
                        continue;
                    }

                    vertex.setValue(maxValue);
                    if (vertex != root) {
                        rootVertexes.add(vertex);
                    }
                }
            }
        }

        for (Vertex<T> rootVertex : rootVertexes) {
            List<Vertex<T>> activeSuccessors = new LinkedList<>(rootVertex.getSuccessors());
            activeSuccessors.removeAll(rootVertexes);
            activeSuccessors.stream()
                    .filter(successor -> !rootVertexes.contains(successor))
                    .filter(Vertex::updateValueIfNecessary)
                    .forEach(this::updateSubGraphValuesIfNecessary);
        }
    }

    private void updateSinkGraph(Vertex<T> sinkVertex) {
        if (sinkVertex.updateValueIfNecessary()) {
            // sink 有更新，所有后继顶点也可能需要更新数值
            updateSubGraphValuesIfNecessary(sinkVertex);
            // 顶点值更新完毕，需重新排序
            vertexList.sort(Comparator.comparingInt(Vertex::getValue));
        }
    }

    private boolean canReach(Vertex<T> sourceVertex, Vertex<T> sinkVertex,
                             List<Vertex<T>> touchedVertexList) {
        // 如果已经遍历过这个顶点，说明已经成环，不再继续遍历
        if (touchedVertexList.contains(sourceVertex)) {
            return false;
        }

        if (sourceVertex == sinkVertex || sourceVertex.hasSuccessor(sinkVertex)) {
            return true;
        }

        touchedVertexList.add(sourceVertex);

        return sourceVertex.getSuccessors().stream()
                .anyMatch(successor -> canReach(successor, sinkVertex, touchedVertexList));
    }

    private Vertex<T> getVertex(T o) {
        Objects.requireNonNull(o);
        Vertex<T> vertex = indexMap.get(o);
        if (vertex == null) {
            throw new IllegalArgumentException("Vertex does not in graph: " + o);
        }
        return vertex;
    }

    private SortedSet<T> subSet(Vertex<T> fromVertex, Vertex<T> toVertex) {
        return vertexList.stream()
                .filter(v -> v.getValue() >= fromVertex.getValue() &&
                        v.getValue() < toVertex.getValue())
                .map(Vertex::getData)
                .collect(Collectors.toCollection(() -> new TreeSet<>(comparator())));
    }

    protected boolean allowCycle() {
        return true;
    }

    private void doConnect(Vertex<T> sourceVertex, Vertex<T> sinkVertex) {
        if (sourceVertex.hasSuccessor(sinkVertex)) {
            return;
        }

        // 如果已经有 sink -> source 的路径，那么这条边建立起来之后将构成环
        boolean beingCycle = canReach(sinkVertex, sourceVertex, new LinkedList<>());

        if (beingCycle && !allowCycle()) {
            throw new UnsupportedOperationException("Cycle is not allowed!");
        }

        sourceVertex.addSuccessor(sinkVertex);
        sinkVertex.addPredecessor(sourceVertex);

        updateSinkGraph(sinkVertex);
    }

    private void doDisconnect(Vertex<T> sourceVertex, Vertex<T> sinkVertex) {
        if (!sourceVertex.hasSuccessor(sinkVertex)) {
            return;
        }

        sourceVertex.removeSuccessor(sinkVertex);
        sinkVertex.removePredecessor(sourceVertex);

        updateSinkGraph(sinkVertex);
    }

    @SafeVarargs
    @Override
    public final void connect(T... arr) {
        if (arr == null) {
            return;
        }

        int sourceCount = arr.length - 1;

        for (int i = 0; i < sourceCount; ++i) {
            Vertex<T> sourceVertex = getVertex(arr[i]);
            Vertex<T> sinkVertex = getVertex(arr[i + 1]);

            doConnect(sourceVertex, sinkVertex);
        }
    }

    @SafeVarargs
    @Override
    public final void disconnect(T... arr) {
        if (arr == null) {
            return;
        }

        int sourceCount = arr.length - 1;

        for (int i = 0; i < sourceCount; ++i) {
            Vertex<T> sourceVertex = getVertex(arr[i]);
            Vertex<T> sinkVertex = getVertex(arr[i + 1]);

            doDisconnect(sourceVertex, sinkVertex);
        }
    }

    @Override
    public boolean isConnected(T source, T sink) {
        Vertex<T> sourceVertex = getVertex(source);
        Vertex<T> sinkVertex = getVertex(sink);

        return sourceVertex.hasSuccessor(sinkVertex);
    }

    @Override
    public int vertexCount() {
        return vertexList.size();
    }

    @Override
    public int edgeCount() {
        return vertexList.stream().mapToInt(Vertex::successorCount).sum();
    }

    @Override
    public int degree(T o) {
        Vertex<T> vertex = getVertex(o);
        return vertex.successorCount() + vertex.predecessorCount();
    }

    @Override
    public Collection<T> getSuccessors(T o) {
        Vertex<T> vertex = getVertex(o);

        return vertex.getSuccessors().stream().map(Vertex::getData).collect(Collectors.toList());
    }

    @Override
    public Collection<T> getPredecessors(T o) {
        Vertex<T> vertex = getVertex(o);

        return vertex.getPredecessors().stream().map(Vertex::getData).collect(Collectors.toList());
    }

    @Override
    public Collection<T> getRoots() {
        return vertexList.stream()
                .filter(v -> v.getValue() == 0)
                .map(Vertex::getData)
                .collect(Collectors.toSet());
    }

    @Override
    public String toDot() {
        return toDot(Object::toString);
    }

    @Override
    public String toDot(Function<T, String> converter) {
        StringBuilder builder = new StringBuilder("digraph{\n");
        vertexList.stream().map(v -> v.toDot(converter)).forEach(builder::append);
        builder.append("}\n");
        return builder.toString();
    }

    @Override
    public Comparator<? super T> comparator() {
        // vertexList 已有序
        return Comparator.comparingInt(o ->
                Optional.ofNullable(indexMap.get(o))
                        .map(vertexList::indexOf)
                        .orElse(0));
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        Vertex<T> fromVertex = getVertex(fromElement);
        Vertex<T> toVertex = getVertex(toElement);

        return subSet(fromVertex, toVertex);
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        Vertex<T> toVertex = getVertex(toElement);

        return subSet(vertexList.getFirst(), toVertex);
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        Vertex<T> fromVertex = getVertex(fromElement);
        Vertex<T> fakeEndVertex = new Vertex<>(null);

        fakeEndVertex.setValue(Integer.MAX_VALUE);

        return subSet(fromVertex, fakeEndVertex);
    }

    @Override
    public T first() {
        return vertexList.getFirst().getData();
    }

    @Override
    public T last() {
        return vertexList.getLast().getData();
    }

    @Override
    public int size() {
        return vertexList.size();
    }

    @Override
    public boolean isEmpty() {
        return vertexList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        //noinspection SuspiciousMethodCalls
        return indexMap.containsKey(o);
    }

    @Override
    public Iterator<T> iterator() {
        return IteratorUtils.transformedIterator(vertexList.iterator(), new VertexTransformer<>());
    }

    @Override
    public Object[] toArray() {
        return indexMap.keySet().toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return indexMap.keySet().toArray(a);
    }

    @Override
    public boolean add(T o) {
        if (contains(o)) {
            return false;
        }

        Vertex<T> vertex = new Vertex<>(o);
        vertexList.addFirst(vertex);
        indexMap.put(o, vertex);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        Vertex<T> vertex = indexMap.remove(o);
        if (vertex != null) {
            // 断开前继顶点与本顶点的边
            List<Vertex<T>> predecessors = vertex.getPredecessors();
            for (Iterator<Vertex<T>> it = predecessors.iterator(); it.hasNext();) {
                it.next().removeSuccessor(vertex);
                it.remove();
            }

            // Trick，避免断开后要针对多个后继顶点做更新子图顶点值的操作
            vertex.setValue(-1);
            updateSubGraphValuesIfNecessary(vertex);

            // 子图顶点值都已经更新，可以安全断开连接
            List<Vertex<T>> successors = vertex.getSuccessors();
            for (Iterator<Vertex<T>> it = successors.iterator(); it.hasNext();) {
                it.next().removePredecessor(vertex);
                it.remove();
            }

            boolean result = vertexList.remove(vertex);
            vertexList.sort(Comparator.comparingInt(Vertex::getValue));

            return result;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return indexMap.keySet().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return c.stream()
                .map(this::add)
                .reduce((result, status) -> result || status)
                .orElse(false);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        List<T> keys = indexMap.keySet().stream()
                .filter(o -> !c.contains(o))
                .toList();

        //noinspection SlowAbstractSetRemoveAll
        return removeAll(keys);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return c.stream()
                .map(this::remove)
                .reduce((result, status) -> result || status)
                .orElse(false);
    }

    @Override
    public void clear() {
        vertexList.clear();
        indexMap.clear();
    }
}
