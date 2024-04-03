package fun.freechat.api.access;

import fun.freechat.api.dto.PromptAiParamDTO;
import fun.freechat.api.dto.PromptRefDTO;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.model.CharacterInfo;
import fun.freechat.model.User;
import fun.freechat.service.account.SysApiTokenService;
import fun.freechat.service.ai.AiApiKeyService;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.ChatContextService;
import fun.freechat.service.enums.Visibility;
import fun.freechat.service.agent.AgentService;
import fun.freechat.service.organization.OrgService;
import fun.freechat.service.plugin.PluginService;
import fun.freechat.service.prompt.PromptService;
import fun.freechat.service.prompt.PromptTaskService;
import fun.freechat.service.rag.RagTaskService;
import fun.freechat.util.AuthorityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Objects;

@Component
@Slf4j
@SuppressWarnings("unused")
public class RoleBasedPermissionEvaluator implements PermissionEvaluator, ApplicationContextAware {
    private ApplicationContext applicationContext;
    private PromptService promptService;
    private PromptTaskService promptTaskService;
    private AgentService agentService;
    private PluginService pluginService;
    private CharacterService characterService;
    private ChatContextService chatContextService;
    private AiApiKeyService aiApiKeyService;
    private OrgService orgService;
    private SysApiTokenService sysApiTokenService;
    private RagTaskService ragTaskService;

    private boolean targetNotBlank(String target) {
        return StringUtils.isNotBlank(target) && !"null".equals(target);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetObject, Object permission) {
        if (!(permission instanceof String)) {
            return false;
        }
        String currentRole = AccountUtils.currentRole();
        User currentUser = AccountUtils.currentUser();
        if (AuthorityUtils.adminRole().equals(currentRole)) {
            return true;
        }

        boolean allow;
        String[] targets;
        String ownerId;
        try {
            switch ((String)permission) {
                case "promptDefaultOp" -> allow = targetObject instanceof Long infoId &&
                        currentUser.getUserId().equals(getPromptService().getOwner(infoId));
                case "promptTaskDefaultOp" -> allow = targetObject instanceof String infoId &&
                        currentUser.getUserId().equals(getPromptTaskService().getOwner(infoId));
                case "characterDefaultOp" -> allow = targetObject instanceof Long infoId &&
                        currentUser.getUserId().equals(getCharacterService().getOwner(infoId));
                case "characterBackendDefaultOp" -> allow = targetObject instanceof String backendId &&
                        currentUser.getUserId().equals(getCharacterService().getBackendOwner(backendId));
                case "chatDefaultOp" -> allow = targetObject instanceof String chatId &&
                        currentUser.getUserId().equals(getChatContextService().getChatOwner(chatId));
                case "ragDefaultOp" -> allow = targetObject instanceof Long taskId &&
                        currentUser.getUserId().equals(getRagTaskService().getOwner(taskId));
                case "chatCreateOp" -> {
                    Long characterId  =(Long) targetObject;
                    if (Objects.isNull(characterId)) {
                        allow = false;
                    } else {
                        String chatUserId = currentUser.getUserId();
                        CharacterInfo summary = getCharacterService().summary(characterId);
                        String characterOwnerId = summary.getUserId();
                        Visibility visibility = Visibility.of(summary.getVisibility());
                        allow = chatUserId.equals(characterOwnerId) ||
                                visibility == Visibility.PUBLIC;
                        if (!allow && visibility == Visibility.PUBLIC_ORG) {
                                allow = getOrgService().getOwners(characterOwnerId).contains(chatUserId) ||
                                        getOrgService().getSubordinates(characterOwnerId).contains(chatUserId);
                        }
                    }
                }
                case "characterBackendCreateOp" -> {
                    targets = ((String) targetObject).split("\\|");
                    if (targets.length != 2) {
                        throw new RuntimeException("Wrong format of target: " + targetObject);
                    }
                    Long infoId = Long.parseLong(targets[0]);
                    ownerId = getCharacterService().getOwner(infoId);
                    allow = currentUser.getUserId().equals(ownerId);
                    if (targetNotBlank(targets[1])) {
                        allow = allow && currentUser.getUserId().equals(getPromptTaskService().getOwner(targets[1]));
                    }
                }
                case "characterBackendUpdateOp" -> {
                    targets = ((String) targetObject).split("\\|");
                    if (targets.length != 2) {
                        throw new RuntimeException("Wrong format of target: " + targetObject);
                    }
                    ownerId = getCharacterService().getBackendOwner(targets[0]);
                    allow = currentUser.getUserId().equals(ownerId);
                    if (targetNotBlank(targets[1])) {
                        allow = allow && currentUser.getUserId().equals(getPromptTaskService().getOwner(targets[1]));
                    }
                }
                case "promptCreateOp", "agentCreateOp", "pluginCreateOp", "characterCreateOp" ->
                        allow = checkVisibility((String) targetObject, currentRole);
                case "promptUpdateOp" -> {
                    targets = ((String) targetObject).split("\\|");
                    if (targets.length != 2) {
                        throw new RuntimeException("Wrong format of target: " + targetObject);
                    }
                    Long infoId = Long.parseLong(targets[0]);
                    ownerId = getPromptService().getOwner(infoId);
                    allow = currentUser.getUserId().equals(ownerId) &&
                            checkVisibility(targets[1], currentRole);
                }
                case "agentUpdateOp" -> {
                    targets = ((String) targetObject).split("\\|");
                    if (targets.length != 2) {
                        throw new RuntimeException("Wrong format of target: " + targetObject);
                    }
                    Long infoId = Long.parseLong(targets[0]);
                    ownerId = getAgentService().getOwner(infoId);
                    allow = currentUser.getUserId().equals(ownerId) &&
                            checkVisibility(targets[1], currentRole);
                }
                case "pluginUpdateOp" -> {
                    targets = ((String) targetObject).split("\\|");
                    if (targets.length != 2) {
                        throw new RuntimeException("Wrong format of target: " + targetObject);
                    }
                    Long infoId = Long.parseLong(targets[0]);
                    ownerId = getPluginService().getOwner(infoId);
                    allow = currentUser.getUserId().equals(ownerId) &&
                            checkVisibility(targets[1], currentRole);
                }
                case "characterUpdateOp" -> {
                    targets = ((String) targetObject).split("\\|");
                    if (targets.length != 2) {
                        throw new RuntimeException("Wrong format of target: " + targetObject);
                    }
                    Long infoId = Long.parseLong(targets[0]);
                    ownerId = getCharacterService().getOwner(infoId);
                    allow = currentUser.getUserId().equals(ownerId) &&
                            checkVisibility(targets[1], currentRole);
                }
                case "promptTaskUpdateOp" -> {
                    targets = ((String) targetObject).split("\\|");
                    if (targets.length != 2) {
                        throw new RuntimeException("Wrong format of target: " + targetObject);
                    }
                    ownerId = getPromptTaskService().getOwner(targets[0]);
                    allow = currentUser.getUserId().equals(ownerId);
                    if (targetNotBlank(targets[1])) {
                        Long infoId = Long.parseLong(targets[1]);
                        allow = allow && currentUser.getUserId().equals(getPromptService().getOwner(infoId));
                    }
                }
                case "promptDeleteOp" -> {
                    var info = getPromptService().summary((Long) targetObject, currentUser);
                    allow = Objects.nonNull(info) &&
                            currentUser.getUserId().equals(info.getLeft().getUserId()) &&
                            checkVisibility(info.getLeft().getVisibility(), currentRole);
                }
                case "agentDeleteOp" -> {
                    var info = getAgentService().summary((Long) targetObject, currentUser);
                    allow = Objects.nonNull(info) &&
                            currentUser.getUserId().equals(info.getLeft().getUserId()) &&
                            checkVisibility(info.getLeft().getVisibility(), currentRole);
                }
                case "pluginDeleteOp" -> {
                    var info = getPluginService().summary((Long) targetObject, currentUser);
                    allow = Objects.nonNull(info) &&
                            currentUser.getUserId().equals(info.getLeft().getUserId()) &&
                            checkVisibility(info.getLeft().getVisibility(), currentRole);
                }
                case "characterDeleteOp" -> {
                    var info = getCharacterService().summary((Long) targetObject, currentUser);
                    allow = Objects.nonNull(info) &&
                            currentUser.getUserId().equals(info.getLeft().getUserId()) &&
                            checkVisibility(info.getLeft().getVisibility(), currentRole);
                }
                case "aiApiKeyOp" -> {
                    ownerId = getAiApiKeyService().getOwner((Long) targetObject);
                    allow = currentUser.getUserId().equals(ownerId);
                }
                case "aiForPromptOp" -> {
                    allow = true;
                    PromptAiParamDTO aiRequest = (PromptAiParamDTO) targetObject;
                    if (Objects.nonNull(aiRequest.getPromptRef())) {
                        ownerId = getPromptService().getOwner(aiRequest.getPromptRef().getPromptId());
                        allow = currentUser.getUserId().equals(ownerId);
                    }
                    if (allow &&
                            Objects.nonNull(aiRequest.getParams()) &&
                            Objects.nonNull(aiRequest.getParams().get("apiKeyName"))) {
                        ownerId = getAiApiKeyService().getOwner(
                                currentUser.getUserId(), aiRequest.getParams().get("apiKeyName").toString());
                        allow = Objects.nonNull(ownerId);
                    }
                }
                case "promptApplyOp" -> {
                    PromptRefDTO promptRef = (PromptRefDTO) targetObject;
                    allow = Objects.nonNull(promptRef.getPromptId());
                    if (allow) {
                        ownerId = getPromptService().getOwner(promptRef.getPromptId());
                        allow = currentUser.getUserId().equals(ownerId);
                    }
                }
                case "apiTokenByContentDefaultOp" -> {
                    ownerId = getSysApiTokenService().getOwner((String) targetObject);
                    allow = currentUser.getUserId().equals(ownerId);
                }
                case "apiTokenByIdDefaultOp" -> {
                    ownerId = getSysApiTokenService().getOwner((Long) targetObject);
                    allow = currentUser.getUserId().equals(ownerId);
                }
                default -> allow = false;
            }
        } catch (Exception e) {
            log.error("Failed to check permission for user({}), target({}), permission({})!",
                    authentication.getName(), targetObject, permission, e);
            allow = false;
        }
        if (!allow) {
            log.warn("{} has no permission of {} to {}", authentication.getName(), permission, targetObject);
        }
        return allow;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }

    private boolean checkVisibility(String visibility, String role) {
        return switch (Visibility.of(visibility)) {
            case HIDDEN, PRIVATE, PUBLIC -> true;
            case PUBLIC_ORG -> AccountUtils.hasSufficientPermission(AuthorityUtils.RES_ADMIN);
        };
    }

    private PromptService getPromptService() {
        if (Objects.isNull(promptService)) {
            promptService = applicationContext.getBean(PromptService.class);
        }
        return promptService;
    }

    private PromptTaskService getPromptTaskService() {
        if (Objects.isNull(promptTaskService)) {
            promptTaskService = applicationContext.getBean(PromptTaskService.class);
        }
        return promptTaskService;
    }

    private AgentService getAgentService() {
        if (Objects.isNull(agentService)) {
            agentService = applicationContext.getBean(AgentService.class);
        }
        return agentService;
    }

    private PluginService getPluginService() {
        if (Objects.isNull(pluginService)) {
            pluginService = applicationContext.getBean(PluginService.class);
        }
        return pluginService;
    }

    private CharacterService getCharacterService() {
        if (Objects.isNull(characterService)) {
            characterService = applicationContext.getBean(CharacterService.class);
        }
        return characterService;
    }

    private ChatContextService getChatContextService() {
        if (Objects.isNull(chatContextService)) {
            chatContextService = applicationContext.getBean(ChatContextService.class);
        }
        return chatContextService;
    }

    private AiApiKeyService getAiApiKeyService() {
        if (Objects.isNull(aiApiKeyService)) {
            aiApiKeyService = applicationContext.getBean(AiApiKeyService.class);
        }
        return aiApiKeyService;
    }

    private OrgService getOrgService() {
        if (Objects.isNull(orgService)) {
            orgService = applicationContext.getBean(OrgService.class);
        }
        return orgService;
    }

    private SysApiTokenService getSysApiTokenService() {
        if (Objects.isNull(sysApiTokenService)) {
            sysApiTokenService = applicationContext.getBean(SysApiTokenService.class);
        }
        return sysApiTokenService;
    }

    private RagTaskService getRagTaskService() {
        if (Objects.isNull(ragTaskService)) {
            ragTaskService = applicationContext.getBean((RagTaskService.class));
        }
        return ragTaskService;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
