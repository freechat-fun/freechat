package fun.freechat.service.connectors;

@FunctionalInterface
public interface ConnectorBinder {
    ConnectorInvocation bind();
}
