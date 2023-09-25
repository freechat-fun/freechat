package fun.freechat.util.graph;

/**
 * Directed Acyclic Graph
 */
@SuppressWarnings("unused")
public class DaGraph<T> extends DiGraph<T>{
    protected boolean allowCycle() {
        return false;
    }
}
