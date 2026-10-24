package fun.freechat.service.connectors;

import static fun.freechat.mapper.ConnectorInfoDynamicSqlSupport.connectorInfo;
import static fun.freechat.service.connectors.ConnectorException.Code.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fun.freechat.mapper.ConnectorInfoMapper;
import fun.freechat.mapper.UserDynamicSqlSupport;
import fun.freechat.mapper.UserMapper;
import fun.freechat.model.ConnectorInfo;
import fun.freechat.service.common.EncryptionService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/** Owner-row locks coordinate quotas across replicas; no operation performs network I/O. */
@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class DatabaseConnectorService implements ConnectorService {
    private static final int MAX_CONNECTORS = 32;
    private static final String ENVELOPE = "connector-header:v1:";
    private static final ObjectMapper JSON = new ObjectMapper().enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
    private final ConnectorInfoMapper connectors;
    private final UserMapper users;
    private final EncryptionService encryption;
    private final TransactionTemplate transactions;

    public DatabaseConnectorService(
            ConnectorInfoMapper connectors,
            UserMapper users,
            EncryptionService encryption,
            PlatformTransactionManager transactionManager) {
        this.connectors = connectors;
        this.users = users;
        this.encryption = encryption;
        transactions = new TransactionTemplate(transactionManager);
        transactions.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactions.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
    }

    @Override
    public List<Summary> list(String ownerId) {
        owner(ownerId);
        return transaction(() -> connectors
                .select(q -> q.where(connectorInfo.userId, isEqualTo(ownerId))
                        .orderBy(connectorInfo.gmtModified.descending(), connectorInfo.connectorId)
                        .limit(MAX_CONNECTORS))
                .stream()
                .filter(row -> ownerId.equals(row.getUserId()))
                .map(this::summary)
                .toList());
    }

    @Override
    public Summary get(String ownerId, String connectorId) {
        owner(ownerId);
        identifier(connectorId, CONNECTOR_NOT_FOUND);
        return transaction(() -> summary(find(ownerId, connectorId)));
    }

    @Override
    public Resolved resolve(String ownerId, String connectorId) {
        owner(ownerId);
        identifier(connectorId, CONNECTOR_NOT_FOUND);
        return transaction(() -> {
            lockOwner(ownerId);
            ConnectorInfo row = find(ownerId, connectorId);
            Map<String, String> plain = new LinkedHashMap<>();
            storedHeaders(row).forEach((name, cipher) -> plain.put(name, decrypt(cipher)));
            try {
                return new Resolved(
                        row.getConnectorId(),
                        ownerId,
                        row.getRevision(),
                        new ConnectorConfiguration(
                                row.getName(), row.getTransport(), row.getUrl(), row.getTimeout(), plain),
                        Byte.valueOf((byte) 1).equals(row.getActive()));
            } catch (RuntimeException ignored) {
                throw unavailable();
            }
        });
    }

    @Override
    public Summary create(String ownerId, ConnectorConfiguration configuration, boolean active) {
        owner(ownerId);
        requireConfiguration(configuration);
        return transaction(() -> {
            lockOwner(ownerId);
            if (connectors.count(q -> q.where(connectorInfo.userId, isEqualTo(ownerId))) >= MAX_CONNECTORS) {
                throw new ConnectorException(CONNECTOR_LIMIT_EXCEEDED);
            }
            LocalDateTime now = now();
            ConnectorInfo row = metadata(configuration, active)
                    .withConnectorId(uuid())
                    .withUserId(ownerId)
                    .withRevision(uuid())
                    .withGmtCreate(now)
                    .withGmtModified(now)
                    .withHeaders(json(encrypt(configuration.headers())))
                    .withHeaderNames(json(configuration.headers().keySet()));
            requireOne(connectors.insert(row));
            return summary(row);
        });
    }

    @Override
    public Summary update(
            String ownerId,
            String connectorId,
            String expectedRevision,
            ConnectorConfiguration configuration,
            Set<String> keepHeaderNames,
            boolean active) {
        owner(ownerId);
        identifier(connectorId, CONNECTOR_NOT_FOUND);
        identifier(expectedRevision, CONNECTOR_VERSION_CONFLICT);
        requireConfiguration(configuration);
        Set<String> retained = retainedNames(keepHeaderNames);
        return transaction(() -> {
            lockOwner(ownerId);
            ConnectorInfo previous = find(ownerId, connectorId);
            expected(previous, expectedRevision);
            if (!retained.isEmpty()
                    && !ConnectorConfigurationValidator.sameOrigin(previous.getUrl(), configuration.url())) {
                throw ConnectorConfigurationValidator.invalid();
            }
            Map<String, String> merged = new LinkedHashMap<>(configuration.headers());
            Map<String, String> encrypted = new LinkedHashMap<>();
            if (!retained.isEmpty()) {
                Map<String, String> stored = storedHeaders(previous);
                Set<String> seen = new HashSet<>();
                configuration
                        .headers()
                        .keySet()
                        .forEach(name -> seen.add(ConnectorConfigurationValidator.headerName(name)));
                for (String name : retained) {
                    if (!seen.add(name)) {
                        throw ConnectorConfigurationValidator.invalid();
                    }
                    String original = stored.keySet().stream()
                            .filter(key -> ConnectorConfigurationValidator.headerName(key)
                                    .equals(name))
                            .findFirst()
                            .orElseThrow(ConnectorConfigurationValidator::invalid);
                    String cipher = stored.get(original);
                    encrypted.put(original, cipher);
                    merged.put(original, decrypt(cipher));
                }
            }
            ConnectorConfiguration complete = new ConnectorConfiguration(
                    configuration.name(),
                    configuration.transport(),
                    configuration.url(),
                    configuration.timeout(),
                    merged);
            encrypted.putAll(encrypt(configuration.headers()));
            ConnectorInfo next = metadata(complete, active)
                    .withConnectorId(connectorId)
                    .withUserId(ownerId)
                    .withRevision(uuid())
                    .withGmtCreate(previous.getGmtCreate())
                    .withGmtModified(now())
                    .withHeaders(json(encrypted))
                    .withHeaderNames(json(complete.headers().keySet()));
            requireOne(connectors.update(q -> q.set(connectorInfo.revision)
                    .equalTo(next.getRevision())
                    .set(connectorInfo.name)
                    .equalTo(next.getName())
                    .set(connectorInfo.transport)
                    .equalTo(next.getTransport())
                    .set(connectorInfo.url)
                    .equalTo(next.getUrl())
                    .set(connectorInfo.timeout)
                    .equalTo(next.getTimeout())
                    .set(connectorInfo.active)
                    .equalTo(next.getActive())
                    .set(connectorInfo.headers)
                    .equalTo(next.getHeaders())
                    .set(connectorInfo.headerNames)
                    .equalTo(next.getHeaderNames())
                    .set(connectorInfo.gmtModified)
                    .equalTo(next.getGmtModified())
                    .where(connectorInfo.connectorId, isEqualTo(connectorId))
                    .and(connectorInfo.userId, isEqualTo(ownerId))
                    .and(connectorInfo.revision, isEqualTo(expectedRevision))));
            return summary(next);
        });
    }

    @Override
    public void delete(String ownerId, String connectorId, String expectedRevision) {
        owner(ownerId);
        identifier(connectorId, CONNECTOR_NOT_FOUND);
        identifier(expectedRevision, CONNECTOR_VERSION_CONFLICT);
        transaction(() -> {
            lockOwner(ownerId);
            expected(find(ownerId, connectorId), expectedRevision);
            requireOne(connectors.delete(q -> q.where(connectorInfo.connectorId, isEqualTo(connectorId))
                    .and(connectorInfo.userId, isEqualTo(ownerId))
                    .and(connectorInfo.revision, isEqualTo(expectedRevision))));
            return null;
        });
    }

    private ConnectorInfo find(String ownerId, String connectorId) {
        return connectors
                .selectOne(q -> q.where(connectorInfo.connectorId, isEqualTo(connectorId))
                        .and(connectorInfo.userId, isEqualTo(ownerId)))
                .filter(row -> ownerId.equals(row.getUserId()) && connectorId.equals(row.getConnectorId()))
                .orElseThrow(() -> new ConnectorException(CONNECTOR_NOT_FOUND));
    }

    private void lockOwner(String ownerId) {
        var user = users.selectOne(q -> q.where(UserDynamicSqlSupport.userId, isEqualTo(ownerId))
                        .forUpdate())
                .orElseThrow(() -> new ConnectorException(CONNECTOR_ACCESS_DENIED));
        // The legacy user table is case-insensitive, while principal identities are exact.
        if (!ownerId.equals(user.getUserId())
                || !Byte.valueOf((byte) 1).equals(user.getEnabled())
                || !Byte.valueOf((byte) 0).equals(user.getLocked())) {
            throw new ConnectorException(CONNECTOR_ACCESS_DENIED);
        }
    }

    private Summary summary(ConnectorInfo row) {
        return new Summary(
                row.getConnectorId(),
                row.getRevision(),
                row.getName(),
                row.getTransport(),
                row.getUrl(),
                row.getTimeout(),
                Byte.valueOf((byte) 1).equals(row.getActive()),
                headerNames(row),
                row.getGmtModified().toInstant(ZoneOffset.UTC));
    }

    private Set<String> headerNames(ConnectorInfo row) {
        try {
            JsonNode names = tree(row.getHeaderNames());
            if (!names.isArray() || names.size() > ConnectorConfigurationValidator.MAX_HEADERS) {
                throw unavailable();
            }
            Set<String> result = new LinkedHashSet<>();
            Set<String> normalized = new HashSet<>();
            int bytes = 0;
            for (JsonNode name : names) {
                if (!name.isTextual()
                        || !normalized.add(ConnectorConfigurationValidator.headerName(name.textValue()))) {
                    throw unavailable();
                }
                bytes += name.textValue().length();
                if (bytes > ConnectorConfigurationValidator.MAX_HEADER_BYTES) {
                    throw unavailable();
                }
                result.add(name.textValue());
            }
            return result;
        } catch (RuntimeException ignored) {
            throw unavailable();
        }
    }

    private Map<String, String> storedHeaders(ConnectorInfo row) {
        JsonNode node = tree(row.getHeaders());
        if (!node.isObject() || node.size() > ConnectorConfigurationValidator.MAX_HEADERS) {
            throw unavailable();
        }
        Map<String, String> result = new LinkedHashMap<>();
        node.properties().forEach(entry -> {
            if (!entry.getValue().isTextual()) {
                throw unavailable();
            }
            result.put(entry.getKey(), entry.getValue().textValue());
        });
        if (!result.keySet().equals(headerNames(row))) {
            throw unavailable();
        }
        return result;
    }

    private Map<String, String> encrypt(Map<String, String> plain) {
        Map<String, String> result = new LinkedHashMap<>();
        // The existing encryption implementation uses mutable ciphers and can return input on failure.
        synchronized (encryption) {
            plain.forEach((name, value) -> {
                String envelope = ENVELOPE + value;
                String cipher = encryption.encrypt(envelope);
                if (cipher == null
                        || cipher.isBlank()
                        || cipher.equals(envelope)
                        || !envelope.equals(encryption.decrypt(cipher))) {
                    throw unavailable();
                }
                result.put(name, cipher);
            });
        }
        return result;
    }

    private String decrypt(String cipher) {
        synchronized (encryption) {
            String plain = encryption.decrypt(cipher);
            if (plain == null || plain.equals(cipher) || !plain.startsWith(ENVELOPE)) {
                throw unavailable();
            }
            return plain.substring(ENVELOPE.length());
        }
    }

    private static Set<String> retainedNames(Set<String> names) {
        if (names == null || names.isEmpty()) {
            return Set.of();
        }
        if (names.size() > ConnectorConfigurationValidator.MAX_HEADERS) {
            throw ConnectorConfigurationValidator.invalid();
        }
        Set<String> result = new LinkedHashSet<>();
        int bytes = 0;
        for (String name : names) {
            if (!result.add(ConnectorConfigurationValidator.headerName(name))) {
                throw ConnectorConfigurationValidator.invalid();
            }
            bytes += name.length();
            if (bytes > ConnectorConfigurationValidator.MAX_HEADER_BYTES) {
                throw ConnectorConfigurationValidator.invalid();
            }
        }
        return result;
    }

    private static ConnectorInfo metadata(ConnectorConfiguration config, boolean active) {
        if (active && config.headers().values().stream().anyMatch(String::isBlank)) {
            throw ConnectorConfigurationValidator.invalid();
        }
        return new ConnectorInfo()
                .withName(config.name())
                .withTransport(config.transport())
                .withUrl(config.url())
                .withTimeout(config.timeout())
                .withActive(active ? (byte) 1 : (byte) 0);
    }

    private static JsonNode tree(String value) {
        if (value == null || value.length() > 64 * 1024) {
            throw unavailable();
        }
        try {
            JsonNode node = JSON.readTree(value);
            if (node == null) {
                throw unavailable();
            }
            return node;
        } catch (JsonProcessingException ignored) {
            throw unavailable();
        }
    }

    private static String json(Object value) {
        try {
            return JSON.writeValueAsString(value);
        } catch (JsonProcessingException ignored) {
            throw unavailable();
        }
    }

    private <T> T transaction(Supplier<T> operation) {
        try {
            return transactions.execute(status -> operation.get());
        } catch (ConnectorException failure) {
            throw failure;
        } catch (RuntimeException ignored) {
            // Never propagate SQL, crypto, provider, URL, or header payloads into logs or API errors.
            throw unavailable();
        }
    }

    private static void requireConfiguration(ConnectorConfiguration configuration) {
        if (configuration == null) {
            throw ConnectorConfigurationValidator.invalid();
        }
    }

    private static void owner(String ownerId) {
        if (ownerId == null || ownerId.isBlank() || ownerId.length() > 32) {
            throw new ConnectorException(CONNECTOR_ACCESS_DENIED);
        }
    }

    private static void identifier(String value, ConnectorException.Code code) {
        if (value == null || !value.matches("[0-9a-f]{32}")) {
            throw new ConnectorException(code);
        }
    }

    private static void expected(ConnectorInfo row, String revision) {
        if (!revision.equals(row.getRevision())) {
            throw new ConnectorException(CONNECTOR_VERSION_CONFLICT);
        }
    }

    private static void requireOne(int count) {
        if (count != 1) {
            throw new ConnectorException(CONNECTOR_VERSION_CONFLICT);
        }
    }

    private static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private static LocalDateTime now() {
        return LocalDateTime.now(ZoneOffset.UTC).truncatedTo(java.time.temporal.ChronoUnit.MICROS);
    }

    private static ConnectorException unavailable() {
        return new ConnectorException(CONNECTOR_UNAVAILABLE);
    }
}
