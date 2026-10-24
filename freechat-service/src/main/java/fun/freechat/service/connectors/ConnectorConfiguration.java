package fun.freechat.service.connectors;

import java.util.Map;

/** Syntax-validated immutable configuration. Runtime must check public DNS before all network I/O. */
public record ConnectorConfiguration(
        String name, String transport, String url, Integer timeout, Map<String, String> headers) {
    public ConnectorConfiguration {
        timeout = timeout == null ? 60_000 : timeout;
        headers = ConnectorConfigurationValidator.validate(name, transport, url, timeout, headers);
    }

    @Override
    public String toString() {
        return "ConnectorConfiguration[redacted]";
    }
}
