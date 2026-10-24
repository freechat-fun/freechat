package fun.freechat.service.connectors;

import static fun.freechat.service.connectors.ConnectorException.Code.*;
import static fun.freechat.service.connectors.ConnectorMcpMessages.*;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.service.tool.ToolExecutionResult;
import fun.freechat.util.HttpUtils;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public final class ConnectorMcpClientFactory implements ConnectorClientFactory {
    private static final Object ADMISSION = new Object();
    private static final Map<String, Integer> OWNERS = new HashMap<>();
    private static int active;
    private final Predicate<String> publicDestination;

    public ConnectorMcpClientFactory() {
        this(HttpUtils::isSafeUrl);
    }

    // Test-only destination seam, not a Spring property or production bypass/allowlist.
    ConnectorMcpClientFactory(Predicate<String> publicDestination) {
        this.publicDestination = publicDestination;
    }

    @Override
    public Connection open(
            String ownerId, ConnectorConfiguration configuration, Duration lifetime, Runnable authorize) {
        if (ownerId == null
                || ownerId.isBlank()
                || ownerId.length() > 256
                || configuration == null
                || lifetime == null
                || lifetime.isNegative()
                || lifetime.isZero()
                || authorize == null) {
            throw new ConnectorException(CONNECTOR_INVALID_CONFIGURATION);
        }
        Runnable release = acquire(ownerId);
        ConnectorMcpTransport transport = null;
        boolean opened = false;
        try {
            transport = new ConnectorMcpTransport(configuration, lifetime, authorize, publicDestination, release);
            ClientConnection connection = new ClientConnection(transport);
            connection.initialize(configuration.timeout());
            opened = true;
            return connection;
        } catch (Exception error) {
            throw sanitized(error);
        } finally {
            if (!opened) {
                try {
                    if (transport != null) transport.close();
                } finally {
                    release.run();
                }
            }
        }
    }

    private static Runnable acquire(String ownerId) {
        synchronized (ADMISSION) {
            int owned = OWNERS.getOrDefault(ownerId, 0);
            if (active >= 64 || owned >= 8) {
                throw limit();
            }
            active++;
            OWNERS.put(ownerId, owned + 1);
        }
        AtomicBoolean released = new AtomicBoolean();
        return () -> {
            if (released.compareAndSet(false, true)) {
                synchronized (ADMISSION) {
                    active--;
                    int owned = OWNERS.get(ownerId) - 1;
                    if (owned == 0) {
                        OWNERS.remove(ownerId);
                    } else {
                        OWNERS.put(ownerId, owned);
                    }
                }
            }
        };
    }

    private static final class ClientConnection implements Connection {
        private final ConnectorMcpTransport transport;
        private volatile DefaultMcpClient client;
        private volatile Set<String> discovered = Set.of();

        private ClientConnection(ConnectorMcpTransport transport) {
            this.transport = transport;
        }

        private void initialize(int timeout) {
            transport.withinDeadline(() -> {
                Duration deadline = Duration.ofMillis(timeout);
                DefaultMcpClient built = DefaultMcpClient.builder()
                        .transport(transport)
                        .clientName("freechat")
                        .clientVersion("1")
                        .protocolVersion(transport.initialProtocol())
                        .initializationTimeout(deadline)
                        .toolExecutionTimeout(deadline)
                        .resourcesTimeout(deadline)
                        .promptsTimeout(deadline)
                        .pingTimeout(deadline)
                        .autoHealthCheck(false)
                        .cacheToolList(false)
                        .cacheResourceList(false)
                        .cachePromptList(false)
                        .subscribeToToolListChanges(false)
                        .subscribeToResourceListChanges(false)
                        .subscribeToPromptListChanges(false)
                        .multiRoundTripMaxRetries(0)
                        .roots(List.of())
                        .logHandler(ignored -> {})
                        .toolExecutionTimeoutErrorMessage(CONNECTOR_TIMEOUT.name())
                        .toolResultConverter((content, error) -> {
                            // Transport has normalized bounded text/structured content and remote errors.
                            if (content.size() != 1
                                    || !"text".equals(content.getFirst().get("type"))
                                    || !(content.getFirst().get("text") instanceof String text)) {
                                throw unavailable();
                            }
                            bytes(text, RESULT_BYTES);
                            if (error && CONNECTOR_TIMEOUT.name().equals(text)) {
                                // Only LC's local deadline marker is a terminal timeout, not isError itself.
                                throw new ConnectorException(CONNECTOR_TIMEOUT);
                            }
                            return ToolExecutionResult.builder()
                                    .resultText(error ? REMOTE_TOOL_ERROR : text)
                                    .build();
                        })
                        .build();
                client = built;
                try {
                    transport.checkHealth();
                } catch (Exception error) {
                    built.close();
                    throw error;
                }
                return null;
            });
        }

        @Override
        public List<ToolSpecification> listTools() {
            return transport.withinDeadline(() -> {
                List<ToolSpecification> tools = List.copyOf(client.listTools());
                if (tools.size() > 32) {
                    throw limit();
                }
                for (ToolSpecification tool : tools) {
                    toolName(tool.name());
                    if (tool.description() != null) {
                        bytes(tool.description(), 4096);
                    }
                }
                discovered = tools.stream().map(ToolSpecification::name).collect(Collectors.toUnmodifiableSet());
                return tools;
            });
        }

        @Override
        public String execute(String name, String arguments) {
            return transport.withinDeadline(() -> {
                toolName(name);
                if (!discovered.contains(name)) {
                    throw new ConnectorException(CONNECTOR_NOT_FOUND);
                }
                if (arguments != null) {
                    bytes(arguments, REQUEST_BYTES);
                }
                String args = arguments == null || arguments.isBlank() ? "{}" : arguments;
                parse(args, REQUEST_BYTES);
                ToolExecutionResult result = client.executeTool(ToolExecutionRequest.builder()
                        .name(name)
                        .arguments(args)
                        .build());
                String text = result.isError() ? REMOTE_TOOL_ERROR : result.resultText();
                bytes(text, RESULT_BYTES);
                return text;
            });
        }

        @Override
        public void close() {
            transport.close();
            DefaultMcpClient current = client;
            if (current != null) {
                current.close();
            }
        }
    }
}
