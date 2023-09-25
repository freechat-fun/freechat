package fun.freechat.util.graph;
import java.io.Serializable;
import java.util.Collection;
import java.util.SortedSet;
import java.util.function.Function;
/**
 * This interface represents a "graph" data structure, which can be directed or undirected, cyclic or acyclic.
 * <p>
 * The graph vertices can be traversed using the for-each syntax:
 * For a directed acyclic graph, the traversal order is guaranteed to follow the direction, i.e., all vertices always appear after their predecessor vertices (directly or indirectly).
 * For a directed cyclic graph, there is no absolute predecessor-successor relationship among the vertices, and the traversal order depends on which vertex enters the cycle.
 * For an undirected graph, the traversal order is not guaranteed.
 * In addition, for vertices without any direct or indirect predecessor/successor relationship, the traversal order is not guaranteed.
 * Graph<String> g = new DaGraph<>();
 * // add nodes and edges
 * for (String node : g) {
 *     // do something
 * }
 * <p>
 * The graph can be processed using stream operations, for example:
 * Graph<String> g = new DaGraph<>();
 * // add nodes and edges
 * g.stream().map(...).filter(...).collect(...);
 * <p>
 * The graph can be output in DOT language (a graph description language).
 * String dot = g.toDot();
 * Paste it into the following website to view the graph online:
 * @link <a href="https://dreampuf.github.io/GraphvizOnline/">GraphvizOnline</a>
 * If the output is saved as a file, it can be viewed using software such as XDoc. The file can also be converted to a PNG image using the dot tool in Linux.
 * $ dot example1.dot –Tpng –o example1.png
 *
 */
@SuppressWarnings("unused")
public interface Graph<T> extends SortedSet<T>, Serializable {
    /**
     * Connects vertices in the graph (an edge is created between every two vertices). The vertices must exist in the graph.
     * For a directed graph, the edge direction is from vertexes[i] to vertexes[i+1].
     * @param vertexes The collection of vertices. The order of vertices represents the predecessor-successor relationship in a directed graph.
     * @throws IllegalArgumentException If a vertex does not exist in the graph.
     * @throws UnsupportedOperationException If attempting to add an edge that forms a cycle in a directed acyclic graph.
     */
    @SuppressWarnings("unchecked")
    void connect(T... vertexes);
    /**
     * Disconnects vertices in the graph. The vertices must exist in the graph.
     * For a directed graph, the edge from vertexes[i+1] to vertexes[i] will not be disconnected.
     * @param vertexes The collection of vertices. The order of vertices represents the predecessor-successor relationship in a directed graph.
     * @throws IllegalArgumentException If a vertex does not exist in the graph.
     */
    @SuppressWarnings("unchecked")
    void disconnect(T... vertexes);
    /**
     * Checks if two vertices are connected. The vertices must exist in the graph. Indirect connections are not considered.
     * For a directed graph with the edge direction source -> sink:
     *   isConnected(source, sink) returns true
     *   isConnected(sink, source) returns false
     * @param source The first vertex, acting as the source in a directed graph.
     * @param sink The second vertex, acting as the sink in a directed graph.
     * @return True if the vertices are connected, false otherwise.
     * @throws IllegalArgumentException If a vertex does not exist in the graph.
     */
    boolean isConnected(T source, T sink);
    /**
     * Returns the number of vertices in the graph, regardless of their connectivity to other vertices.
     * @return The number of vertices.
     */
    int vertexCount();
    /**
     * Returns the number of edges in the graph.
     * @return The number of edges.
     */
    int edgeCount();
    /**
     * Returns the degree of a vertex. For a directed graph, the degree is the sum of the outdegree and the indegree.
     * (The number of edges connected to a vertex is called the degree for an undirected graph, and the outdegree and the indegree for a directed graph.)
     * @param vertex The vertex for which to calculate the degree.
     * @return The degree of the vertex.
     * @throws IllegalArgumentException If a vertex does not exist in the graph.
     */
    int degree(T vertex);
    /**
     * Returns the successors of a vertex. An empty list is returned if there are no successors.
     * @param vertex The current vertex.
     * @return The collection of successor vertices.
     * @throws IllegalArgumentException If a vertex does not exist in the graph.
     */
    Collection<T> getSuccessors(T vertex);
    /**
     * Returns the predecessors of a vertex. An empty list is returned if there are no predecessors.
     * @param vertex The current vertex.
     * @return The collection of predecessor vertices.
     * @throws IllegalArgumentException If a vertex does not exist in the graph.
     */
    Collection<T> getPredecessors(T vertex);
    /**
     * Returns the root vertices that have no predecessors. An empty list is returned if there are no root vertices (such as a pure cycle).
     * @return The collection of root vertices.
     */
    Collection<T> getRoots();
    /**
     * Adds a vertex. The newly added vertex is isolated in the graph.
     * @param o The vertex to be added.
     * @return True if the vertex is added successfully, false if the vertex already exists.
     */
    boolean add(T o);
    /**
     * Removes a vertex. First, the edges between this vertex and its predecessors/successors are removed, and then the vertex itself is removed.
     * @param o The vertex to be removed.
     * @return True if the vertex is removed successfully, false if the vertex does not exist in the graph.
     */
    boolean remove(Object o);
    /**
     * Outputs the graph content in DOT syntax. Introduction to DOT language:
     * @link <a href="https://zhuanlan.zhihu.com/p/21993254">Graphviz Beginner's Guide</a>
     * @return The graph description in dot format.
     */
    String toDot();
    /**
     * Outputs the graph content in DOT syntax. Introduction to DOT language:
     * @link <a href="https://zhuanlan.zhihu.com/p/21993254">Graphviz Beginner's Guide</a>
     * Allows the content to be converted with the specified converter before output.
     * @param converter The method to map objects in the nodes to strings. The default is Object::toString.
     * @return The graph description in dot format.
     */
    String toDot(Function<T, String> converter);
}