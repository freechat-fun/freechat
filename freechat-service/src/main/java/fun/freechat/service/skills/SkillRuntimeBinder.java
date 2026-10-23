package fun.freechat.service.skills;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isNotEqualTo;

import fun.freechat.mapper.CharacterBackendMapper;
import fun.freechat.mapper.CharacterInfoDynamicSqlSupport;
import fun.freechat.mapper.CharacterInfoMapper;
import fun.freechat.mapper.ChatContextMapper;
import fun.freechat.service.rag.EmbeddingModelService;
import fun.freechat.service.tools.ToolContext;
import fun.freechat.service.tools.ToolRuntimeBroker;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SkillRuntimeBinder {
    private final ChatContextMapper contexts;
    private final CharacterBackendMapper backends;
    private final CharacterInfoMapper characters;
    private final SkillService skills;
    private final EmbeddingModelService embeddings;
    private final ToolRuntimeBroker tools;
    private final SkillCharacterAccess access;
    private final int maxTokens;

    public SkillRuntimeBinder(
            ChatContextMapper contexts,
            CharacterBackendMapper backends,
            CharacterInfoMapper characters,
            SkillService skills,
            EmbeddingModelService embeddings,
            ToolRuntimeBroker tools,
            SkillCharacterAccess access,
            @Value("${skills.max-input-tokens:8192}") int maxTokens) {
        this.contexts = contexts;
        this.backends = backends;
        this.characters = characters;
        this.skills = skills;
        this.embeddings = embeddings;
        this.tools = tools;
        this.access = access;
        this.maxTokens = maxTokens;
    }

    public SkillBinder binder(String chatId, String userId, String characterUid, String backendId) {
        return () -> {
            ToolContext context = new ToolContext(chatId, userId, characterUid, backendId, Duration.ofMinutes(10));
            Policy policy = load(context);
            if (policy.enabled().isEmpty()) return null;
            List<SkillService.Installed> catalog = policy.loadAllowed()
                    ? policy.enabled().stream()
                            .sorted()
                            .map(id -> skills.get(policy.ownerId(), id))
                            .toList()
                    : List.of();
            Map<String, String> revisions = catalog.stream()
                    .collect(Collectors.toUnmodifiableMap(
                            SkillService.Installed::skillId, SkillService.Installed::revision));
            Runnable authorize = () -> {
                if (!policy.equals(load(context))) throw unavailable();
                if (!policy.loadAllowed()) return;
                Map<String, String> current = skills.list(policy.ownerId()).stream()
                        .filter(skill -> policy.enabled().contains(skill.skillId()))
                        .collect(Collectors.toMap(SkillService.Summary::skillId, SkillService.Summary::revision));
                if (!revisions.equals(current)) throw unavailable();
            };
            return new SkillInvocation(
                    catalog, context, authorize, embeddings.tokenCountEstimatorForLang(policy.lang()), maxTokens);
        };
    }

    private Policy load(ToolContext scope) {
        try {
            var context = contexts.selectByPrimaryKey(scope.chatId()).orElseThrow();
            if (!scope.userId().equals(context.getUserId())
                    || !scope.backendId().equals(context.getBackendId())) throw unavailable();
            var backend = backends.selectByPrimaryKey(scope.backendId()).orElseThrow();
            if (!scope.characterUid().equals(backend.getCharacterUid())) throw unavailable();
            var character = characters
                    .selectOne(query -> query.where(
                                    CharacterInfoDynamicSqlSupport.characterUid, isEqualTo(scope.characterUid()))
                            .and(CharacterInfoDynamicSqlSupport.visibility, isNotEqualTo("hidden"))
                            .orderBy(
                                    CharacterInfoDynamicSqlSupport.version.descending(),
                                    CharacterInfoDynamicSqlSupport.characterId.descending())
                            .limit(1))
                    .orElseThrow();
            Set<String> enabled =
                    CharacterSkillConfiguration.parse(character.getExt()).enabled();
            if (!enabled.isEmpty() && !access.permits(scope.userId(), character)) throw unavailable();
            return new Policy(
                    character.getUserId(),
                    character.getExt(),
                    character.getLang(),
                    enabled,
                    tools.permits("skill", scope),
                    tools.permits("read_skill_resource", scope));
        } catch (RuntimeException ignored) {
            throw unavailable();
        }
    }

    private record Policy(
            String ownerId, String ext, String lang, Set<String> enabled, boolean loadAllowed, boolean readAllowed) {}

    private static IllegalStateException unavailable() {
        return new IllegalStateException("Chat skill authorization is unavailable");
    }
}
