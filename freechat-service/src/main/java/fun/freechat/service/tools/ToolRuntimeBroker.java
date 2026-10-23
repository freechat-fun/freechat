package fun.freechat.service.tools;

import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.request.json.JsonArraySchema;
import dev.langchain4j.model.chat.request.json.JsonBooleanSchema;
import dev.langchain4j.model.chat.request.json.JsonIntegerSchema;
import dev.langchain4j.model.chat.request.json.JsonNumberSchema;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchemaElement;
import dev.langchain4j.model.chat.request.json.JsonStringSchema;
import dev.langchain4j.service.tool.ToolExecutor;
import java.time.Duration;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ToolRuntimeBroker {
    private static final Set<String> DOMAIN_TOOLS =
            Set.of("search_memory", "read_memory", "find_image", "skill", "read_skill_resource");
    private final ToolRegistry registry;
    private final ToolPolicySource policies;
    private final CopyOnWriteArrayList<ToolRuntime> runtimes = new CopyOnWriteArrayList<>();
    private volatile Set<String> allowed;

    public ToolRuntimeBroker(
            ToolRegistry registry, ToolPolicySource policies, Collection<ToolRuntime> runtimes, Set<String> allowed) {
        this.registry = registry;
        this.policies = policies;
        this.allowed = Set.copyOf(allowed);
        runtimes.forEach(this::registerRuntime);
    }

    public synchronized void registerRuntime(ToolRuntime runtime) {
        if (runtimes.stream().anyMatch(existing -> existing.id().equals(runtime.id()))) {
            throw new IllegalArgumentException("Duplicate tool runtime");
        }
        runtimes.add(runtime);
    }

    public boolean unregisterRuntime(String id) {
        return runtimes.removeIf(runtime -> runtime.id().equals(id));
    }

    public void setAllowed(Set<String> names) {
        allowed = Set.copyOf(names);
    }

    public ToolBinder binder(String chatId, String userId, String characterUid, String backendId) {
        return candidates ->
                bind(new ToolContext(chatId, userId, characterUid, backendId, Duration.ofMinutes(10)), candidates);
    }

    public Map<ToolSpecification, ToolExecutor> bind(
            ToolContext context, Map<ToolSpecification, ToolExecutor> candidates) {
        if (context.chatId().endsWith("-assist")) {
            return Map.of();
        }
        ToolPolicySource.Policy policy;
        try {
            policy = policies.load(context.chatId());
            if (!policy.matches(context)) {
                return Map.of();
            }
        } catch (RuntimeException ignored) {
            return Map.of();
        }
        Map<String, Map.Entry<ToolSpecification, ToolExecutor>> domain = new LinkedHashMap<>();
        for (var entry : candidates.entrySet()) {
            if (domain.putIfAbsent(entry.getKey().name(), entry) != null) {
                throw new IllegalStateException("Duplicate chat tool name");
            }
        }
        Map<ToolSpecification, ToolExecutor> selected = new LinkedHashMap<>();
        for (ToolDefinition definition : registry.list()) {
            if (!allowed.contains(definition.name()) || !policy.permits(definition.name())) {
                continue;
            }
            var candidate = domain.get(definition.name());
            boolean domainTool = DOMAIN_TOOLS.contains(definition.name());
            ToolRuntime runtime = domainTool ? null : runtimeFor(definition, context);
            if (domainTool && candidate == null || !domainTool && runtime == null) {
                continue;
            }
            ToolSpecification specification = domainTool ? candidate.getKey() : definition.specification();
            ToolExecutor delegate = domainTool ? candidate.getValue() : null;
            selected.put(specification, (request, memoryId) -> {
                try {
                    context.admit();
                    if (!context.chatId().equals(memoryId) || !definition.name().equals(request.name())) {
                        throw new IllegalStateException("Tool scope mismatch");
                    }
                    authorize(definition, context);
                    JsonNode arguments = ToolArguments.parse(request.arguments());
                    validate(arguments, specification.parameters());
                    String result;
                    if (delegate != null) {
                        result = delegate.execute(request, memoryId);
                    } else {
                        if (!runtimes.contains(runtime) || !supports(runtime, definition, context)) {
                            throw new IllegalStateException("Tool runtime unavailable");
                        }
                        result = runtime.execute(definition, arguments, context);
                    }
                    context.check();
                    context.recordResult(result);
                    return result;
                } catch (Exception ignored) {
                    throw new IllegalStateException("Chat tool execution failed");
                }
            });
        }
        return java.util.Collections.unmodifiableMap(selected);
    }

    public boolean permits(String name, ToolContext context) {
        try {
            authorize(registry.find(name).orElseThrow(), context);
            return true;
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private void authorize(ToolDefinition definition, ToolContext context) {
        ToolPolicySource.Policy policy = policies.load(context.chatId());
        if (registry.find(definition.name()).orElse(null) != definition
                || !allowed.contains(definition.name())
                || !policy.matches(context)
                || !policy.permits(definition.name())) {
            throw new IllegalStateException("Tool access denied");
        }
    }

    private ToolRuntime runtimeFor(ToolDefinition definition, ToolContext context) {
        List<ToolRuntime> matches = runtimes.stream()
                .filter(runtime -> supports(runtime, definition, context))
                .toList();
        return matches.size() == 1 ? matches.getFirst() : null;
    }

    private boolean supports(ToolRuntime runtime, ToolDefinition definition, ToolContext context) {
        return (definition.boundary() != ToolDefinition.Boundary.ISOLATED || runtime.isolated())
                && runtime.supports(definition, context);
    }

    private static void validate(JsonNode value, JsonSchemaElement schema) {
        if (schema instanceof JsonObjectSchema object) {
            if (!value.isObject()) {
                throw ToolArguments.invalid();
            }
            if (object.required() != null) {
                for (String name : object.required()) {
                    if (!value.hasNonNull(name)) {
                        throw ToolArguments.invalid();
                    }
                }
            }
            value.fields().forEachRemaining(entry -> {
                JsonSchemaElement property = object.properties().get(entry.getKey());
                if (property == null) {
                    throw ToolArguments.invalid();
                }
                validate(entry.getValue(), property);
            });
        } else if (schema instanceof JsonStringSchema && !value.isTextual()
                || schema instanceof JsonIntegerSchema && !value.isIntegralNumber()
                || schema instanceof JsonNumberSchema && !value.isNumber()
                || schema instanceof JsonBooleanSchema && !value.isBoolean()) {
            throw ToolArguments.invalid();
        } else if (schema instanceof JsonArraySchema array) {
            if (!value.isArray() || value.size() > 256) {
                throw ToolArguments.invalid();
            }
            value.forEach(item -> validate(item, array.items()));
        }
    }
}
