package fun.freechat.service.connectors;

/** Stable, payload-free failures shared by management and runtime. */
public final class ConnectorException extends RuntimeException {
    public enum Code {
        CONNECTOR_INVALID_CONFIGURATION,
        CONNECTOR_NOT_FOUND,
        CONNECTOR_ACCESS_DENIED,
        CONNECTOR_VERSION_CONFLICT,
        CONNECTOR_LIMIT_EXCEEDED,
        CONNECTOR_UNAVAILABLE,
        CONNECTOR_UNSAFE_ADDRESS,
        CONNECTOR_TIMEOUT
    }

    private final Code code;

    public ConnectorException(Code code) {
        super(code.name());
        this.code = code;
    }

    public Code code() {
        return code;
    }
}
