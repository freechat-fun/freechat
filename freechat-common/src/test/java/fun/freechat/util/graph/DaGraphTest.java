package fun.freechat.util.graph;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试图形：
 * @link <a href="https://intranetproxy.alipay.com/skylark/lark/0/2020/png/34693/1582877991697-6ff8d92b-39c1-4660-85c1-868a290d0384.png">测试图形</a>
 */
public class DaGraphTest {
    enum Vertex {
        A0,

        B0,
        B1,

        C00,
        C01,
        C02,
        C03,
        C10,
        C20,
        C30,
        C31,
        C41,
        C40,
        C50,
    }

    private final static String EXPECTED_DOT = """
            digraph{
            C01 -> C02;
            C00 -> C10;
            B0 -> B1;
            A0;
            C02 -> C03;
            C03 -> C10;
            C10 -> C20;
            C10 -> C30;
            C20 -> C30;
            C20 -> C31;
            C31 -> C41;
            C30 -> C40;
            C30 -> C41;
            C40 -> C50;
            }
            """;

    private final List<Vertex> passedVertexes = new LinkedList<>();

    private final Graph<Vertex> graph = new DaGraph<>();

    private boolean sameVertexes(Collection<Vertex> list, Vertex... expected) {
        return CollectionUtils.isEqualCollection(list, List.of(expected));
    }

    private void fill() {
        graph.addAll(List.of(Vertex.values()));
        graph.connect(Vertex.B0, Vertex.B1);
        graph.connect(Vertex.C00, Vertex.C10);
        graph.connect(Vertex.C01, Vertex.C02, Vertex.C03, Vertex.C10);
        graph.connect(Vertex.C10, Vertex.C20, Vertex.C30, Vertex.C40, Vertex.C50);
        graph.connect(Vertex.C10, Vertex.C30, Vertex.C41);
        graph.connect(Vertex.C20, Vertex.C31, Vertex.C41);
    }

    private boolean correctOrder(Vertex v) {
        return switch (v) {
            case A0, B0, C00, C01 -> true;
            case B1 -> passedVertexes.contains(Vertex.B0);
            case C02 -> passedVertexes.contains(Vertex.C01);
            case C03 -> passedVertexes.contains(Vertex.C02);
            case C10 -> passedVertexes.containsAll(List.of(Vertex.C00, Vertex.C03));
            case C20 -> passedVertexes.contains(Vertex.C10);
            case C30 -> passedVertexes.containsAll(List.of(Vertex.C20, Vertex.C10));
            case C31 -> passedVertexes.contains(Vertex.C20);
            case C41 -> passedVertexes.containsAll(List.of(Vertex.C30, Vertex.C31));
            case C40 -> passedVertexes.contains(Vertex.C30);
            case C50 -> passedVertexes.contains(Vertex.C40);
        };
    }

    private boolean correctDegree(Vertex v, int degree) {
        return switch (v) {
            case A0 -> 0 == degree;
            case B0, B1, C00, C01, C50 -> 1 == degree;
            case C02, C03, C31, C40, C41 -> 2 == degree;
            case C20 -> 3 == degree;
            case C10, C30 -> 4 == degree;
        };
    }

    private boolean correctSuccessors(Vertex v, Collection<Vertex> successors) {
        return switch (v) {
            case A0, B1, C41, C50 -> CollectionUtils.isEmpty(successors);
            case B0 -> sameVertexes(successors, Vertex.B1);
            case C00, C03 -> sameVertexes(successors, Vertex.C10);
            case C01 -> sameVertexes(successors, Vertex.C02);
            case C02 -> sameVertexes(successors, Vertex.C03);
            case C10 -> sameVertexes(successors, Vertex.C20, Vertex.C30);
            case C20 -> sameVertexes(successors, Vertex.C30, Vertex.C31);
            case C30 -> sameVertexes(successors, Vertex.C40, Vertex.C41);
            case C31 -> sameVertexes(successors, Vertex.C41);
            case C40 -> sameVertexes(successors, Vertex.C50);
        };
    }

    private boolean correctPredecessors(Vertex v, Collection<Vertex> predecessors) {
        return switch (v) {
            case A0, B0, C00, C01 -> CollectionUtils.isEmpty(predecessors);
            case B1 -> sameVertexes(predecessors, Vertex.B0);
            case C02 -> sameVertexes(predecessors, Vertex.C01);
            case C03 -> sameVertexes(predecessors, Vertex.C02);
            case C10 -> sameVertexes(predecessors, Vertex.C00, Vertex.C03);
            case C20 -> sameVertexes(predecessors, Vertex.C10);
            case C30 -> sameVertexes(predecessors, Vertex.C10, Vertex.C20);
            case C31 -> sameVertexes(predecessors, Vertex.C20);
            case C41 -> sameVertexes(predecessors, Vertex.C30, Vertex.C31);
            case C40 -> sameVertexes(predecessors, Vertex.C30);
            case C50 -> sameVertexes(predecessors, Vertex.C40);
        };
    }

    @Test
    public void testConnect() {
        graph.add(Vertex.B0);
        graph.add(Vertex.B1);

        assertFalse(sameVertexes(graph.getSuccessors(Vertex.B0), Vertex.B1));
        assertFalse(sameVertexes(graph.getPredecessors(Vertex.B1), Vertex.B0));

        graph.connect(Vertex.B0, Vertex.B1);

        assertTrue(sameVertexes(graph.getSuccessors(Vertex.B0), Vertex.B1));
        assertTrue(sameVertexes(graph.getPredecessors(Vertex.B1), Vertex.B0));
    }

    @Test()
    public void testConnectNonExistsVertex() {
        graph.add(Vertex.A0);
        assertThrows(IllegalArgumentException.class, () -> graph.connect(Vertex.A0, Vertex.B0));
    }

    @Test
    public void testDisconnect() {
        graph.add(Vertex.B0);
        graph.add(Vertex.B1);
        graph.connect(Vertex.B0, Vertex.B1);

        assertTrue(sameVertexes(graph.getSuccessors(Vertex.B0), Vertex.B1));
        assertTrue(sameVertexes(graph.getPredecessors(Vertex.B1), Vertex.B0));

        graph.disconnect(Vertex.B0, Vertex.B1);

        assertFalse(sameVertexes(graph.getSuccessors(Vertex.B0), Vertex.B1));
        assertFalse(sameVertexes(graph.getPredecessors(Vertex.B1), Vertex.B0));
    }

    @Test
    public void testIsConnected() {
        graph.add(Vertex.B0);
        graph.add(Vertex.B1);
        graph.connect(Vertex.B0, Vertex.B1);

        assertTrue(graph.isConnected(Vertex.B0, Vertex.B1));
        assertFalse(graph.isConnected(Vertex.B1, Vertex.B0));
    }

    @Test
    public void testVertexCount() {
        fill();
        assertEquals(Vertex.values().length, graph.vertexCount());
    }

    @Test
    public void testEdgeCount() {
        fill();
        assertEquals(13, graph.edgeCount());
    }

    @Test
    public void testDegree() {
        fill();
        assertFalse(Arrays.stream(Vertex.values())
                .anyMatch(v -> !correctDegree(v, graph.degree(v)))
        );
    }

    @Test
    public void testGetSuccessors() {
        fill();
        assertFalse(Arrays.stream(Vertex.values())
                .anyMatch(v -> !correctSuccessors(v, graph.getSuccessors(v)))
        );
    }

    @Test
    public void testGetPredecessors() {
        fill();
        assertFalse(Arrays.stream(Vertex.values())
                .anyMatch(v -> !correctPredecessors(v, graph.getPredecessors(v)))
        );
    }

    @Test
    public void testGetRoots() {
        fill();
        assertTrue(sameVertexes(graph.getRoots(),
                Vertex.A0, Vertex.B0, Vertex.C00, Vertex.C01));
    }

    @Test
    public void testAdd() {
        assertEquals(0, graph.size());
        assertTrue(graph.add(Vertex.A0));
        assertEquals(1, graph.size());
        assertFalse(graph.add(Vertex.A0));
    }

    @Test
    public void testRemove() {
        fill();
        assertEquals(Vertex.values().length, graph.size());
        assertTrue(graph.remove(Vertex.C10));
        assertEquals(Vertex.values().length - 1, graph.size());
        assertEquals(0, graph.degree(Vertex.C00));
        assertEquals(1, graph.degree(Vertex.C03));
        assertEquals(2, graph.degree(Vertex.C20));
        assertEquals(3, graph.degree(Vertex.C30));
        assertTrue(sameVertexes(graph.getRoots(),
                Vertex.A0, Vertex.B0, Vertex.C00, Vertex.C01, Vertex.C20));
        assertFalse(graph.remove(Vertex.C10));
    }

    @Test
    public void testToDot() {
        fill();
        assertEquals(EXPECTED_DOT, graph.toDot());
    }

    @Test
    public void testSubSet() {
        fill();
        Set<Vertex> subSet = graph.subSet(Vertex.C02, Vertex.C50);
        assertEquals(9, subSet.size());
    }

    @Test
    public void testHeadSet() {
        fill();
        Set<Vertex> headSet = graph.headSet(Vertex.C50);
        assertEquals(13, headSet.size());
    }

    @Test
    public void testTailSet() {
        fill();
        Set<Vertex> tailSet = graph.tailSet(Vertex.C02);
        assertEquals(10, tailSet.size());
    }

    @Test
    public void testFirst() {
        fill();
        assertTrue(List.of(Vertex.A0, Vertex.B0, Vertex.C00, Vertex.C01)
                .contains(graph.first()));
    }

    @Test
    public void testLast() {
        fill();
        assertEquals(Vertex.C50, graph.last());
    }

    @Test
    public void testRetainAll() {
        fill();
        graph.retainAll(List.of(Vertex.A0, Vertex.C20, Vertex.C30));
        assertEquals(3, graph.vertexCount());
        assertEquals(1, graph.edgeCount());
    }

    @Test
    public void testRemoveAll() {
        fill();
        //noinspection SlowAbstractSetRemoveAll
        graph.removeAll(List.of(Vertex.A0, Vertex.C20, Vertex.C30));
        assertEquals(11, graph.vertexCount());
        assertEquals(7, graph.edgeCount());
    }

    @Test
    public void testForEach() {
        fill();
        for(Vertex v : graph) {
            assertTrue(correctOrder(v));
            passedVertexes.add(v);
        }
    }

    @Test
    public void testStream() {
        fill();
        List<Vertex> filteredVertexes = graph.stream()
                .filter(v -> {
                    assertTrue(correctOrder(v));
                    passedVertexes.add(v);
                    return v == Vertex.A0 || v == Vertex.C20 || v == Vertex.C30;
                }).collect(Collectors.toList());
        assertTrue(sameVertexes(filteredVertexes, Vertex.A0, Vertex.C20, Vertex.C30));
    }
}
