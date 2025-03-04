package fun.freechat.api.access;

import fun.freechat.api.dto.PromptAiParamDTO;
import fun.freechat.api.dto.PromptRefDTO;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.model.CharacterInfo;
import fun.freechat.model.User;
import fun.freechat.service.account.SysApiTokenService;
import fun.freechat.service.agent.AgentService;
import fun.freechat.service.ai.AiApiKeyService;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.ChatContextService;
import fun.freechat.service.chat.ChatMemoryService;
import fun.freechat.service.chat.ChatMessageRecord;
import fun.freechat.service.enums.Visibility;
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
    private ChatMemoryService chatMemoryService;
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
                case "characterDefaultOpByUid" -> allow = targetObject instanceof String infoUid &&
                        currentUser.getUserId().equals(getCharacterService().getOwnerByUid(infoUid));
                case "characterBackendDefaultOp" -> allow = targetObject instanceof String backendId &&
                        currentUser.getUserId().equals(getCharacterService().getBackendOwner(backendId));
                case "chatDefaultOp" -> allow = targetObject instanceof String chatId &&
                        currentUser.getUserId().equals(getChatContextService().getChatOwner(chatId));
                case "ragDefaultOp" -> allow = targetObject instanceof Long taskId &&
                        currentUser.getUserId().equals(getRagTaskService().getOwner(taskId));
                case "chatCreateOp" -> {
                    String characterUid  =(String) targetObject;
                    if (StringUtils.isBlank(characterUid)) {
                        allow = false;
                    } else {
                        String chatUserId = currentUser.getUserId();
                        CharacterInfo summary = getCharacterService().summaryByUid(characterUid);
                        String characterOwnerId = summary.getUserId();
                        Visibility visibility = Visibility.of(summary.getVisibility());
                        allow = chatUserId.equals(characterOwnerId) || visibility == Visibility.PUBLIC;
                        if (!allow && visibility == Visibility.PUBLIC_ORG) {
                                allow = getOrgService().getOwners(characterOwnerId).contains(chatUserId) ||
                                        getOrgService().getSubordinates(characterOwnerId).contains(chatUserId);
                        }
                    }
                }
                case "characterBackendCreateOp" -> {
                    targets = ((String) targetObject).split("\\|");
                    if (targets.length != 2) {
                        throw new IllegalStateException("Wrong format of target: " + targetObject);
                    }
                    String infoUid = targets[0];
                    ownerId = getCharacterService().getOwnerByUid(infoUid);
                    allow = currentUser.getUserId().equals(ownerId);
                    if (allow && targetNotBlank(targets[1])) {
                        allow = currentUser.getUserId().equals(getPromptTaskService().getOwner(targets[1]));
                    }
                }
                case "characterBackendUpdateOp" -> {
                    targets = ((String) targetObject).split("\\|");
                    if (targets.length != 2) {
                        throw new IllegalStateException("Wrong format of target: " + targetObject);
                    }
                    ownerId = getCharacterService().getBackendOwner(targets[0]);
                    allow = currentUser.getUserId().equals(ownerId);
                    if (allow && targetNotBlank(targets[1])) {
                        allow = currentUser.getUserId().equals(getPromptTaskService().getOwner(targets[1]));
                    }
                }
                case "promptCreateOp", "agentCreateOp", "pluginCreateOp", "characterCreateOp" ->
                        allow = checkVisibility((String) targetObject, currentRole);
                case "promptUpdateOp" -> {
                    targets = ((String) targetObject).split("\\|");
                    if (targets.length != 2) {
                        throw new IllegalStateException("Wrong format of target: " + targetObject);
                    }
                    Long infoId = Long.parseLong(targets[0]);
                    ownerId = getPromptService().getOwner(infoId);
                    allow = currentUser.getUserId().equals(ownerId) &&
                            checkVisibility(targets[1], currentRole);
                }
                case "agentUpdateOp" -> {
                    targets = ((String) targetObject).split("\\|");
                    if (targets.length != 2) {
                        throw new IllegalStateException("Wrong format of target: " + targetObject);
                    }
                    Long infoId = Long.parseLong(targets[0]);
                    ownerId = getAgentService().getOwner(infoId);
                    allow = currentUser.getUserId().equals(ownerId) &&
                            checkVisibility(targets[1], currentRole);
                }
                case "pluginUpdateOp" -> {
                    targets = ((String) targetObject).split("\\|");
                    if (targets.length != 2) {
                        throw new IllegalStateException("Wrong format of target: " + targetObject);
                    }
                    Long infoId = Long.parseLong(targets[0]);
                    ownerId = getPluginService().getOwner(infoId);
                    allow = currentUser.getUserId().equals(ownerId) &&
                            checkVisibility(targets[1], currentRole);
                }
                case "characterUpdateOp" -> {
                    targets = ((String) targetObject).split("\\|");
                    if (targets.length != 2) {
                        throw new IllegalStateException("Wrong format of target: " + targetObject);
                    }
                    Long infoId = Long.parseLong(targets[0]);
                    ownerId = getCharacterService().getOwner(infoId);
                    allow = currentUser.getUserId().equals(ownerId) &&
                            checkVisibility(targets[1], currentRole);
                }
                case "promptTaskUpdateOp" -> {
                    targets = ((String) targetObject).split("\\|");
                    if (targets.length != 2) {
                        throw new IllegalStateException("Wrong format of target: " + targetObject);
                    }
                    ownerId = getPromptTaskService().getOwner(targets[0]);
                    allow = currentUser.getUserId().equals(ownerId);
                    if (allow && targetNotBlank(targets[1])) {
                        Long infoId = Long.parseLong(targets[1]);
                        allow = currentUser.getUserId().equals(getPromptService().getOwner(infoId));
                    }
                }
                case "promptDeleteOp" -> {
                    var info = getPromptService().summary((Long) targetObject, currentUser);
                    allow = info != null &&
                            currentUser.getUserId().equals(info.getLeft().getUserId()) &&
                            checkVisibility(info.getLeft().getVisibility(), currentRole);
                }
                case "agentDeleteOp" -> {
                    var info = getAgentService().summary((Long) targetObject, currentUser);
                    allow = info != null &&
                            currentUser.getUserId().equals(info.getLeft().getUserId()) &&
                            checkVisibility(info.getLeft().getVisibility(), currentRole);
                }
                case "pluginDeleteOp" -> {
                    var info = getPluginService().summary((Long) targetObject, currentUser);
                    allow = info != null &&
                            currentUser.getUserId().equals(info.getLeft().getUserId()) &&
                            checkVisibility(info.getLeft().getVisibility(), currentRole);
                }
                case "characterDeleteOp" -> {
                    var info = getCharacterService().summary((Long) targetObject, currentUser);
                    allow = info != null &&
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
                    if (aiRequest.getPromptRef() != null) {
                        ownerId = getPromptService().getOwner(aiRequest.getPromptRef().getPromptId());
                        allow = currentUser.getUserId().equals(ownerId);
                    }
                    if (allow &&
                            aiRequest.getParams() != null &&
                            aiRequest.getParams().get("apiKeyName") != null) {
                        ownerId = getAiApiKeyService().getOwner(
                                currentUser.getUserId(), aiRequest.getParams().get("apiKeyName").toString());
                        allow = ownerId != null;
                    }
                }
                case "promptApplyOp" -> {
                    PromptRefDTO promptRef = (PromptRefDTO) targetObject;
                    allow = promptRef.getPromptId() != null;
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
                case "chatAssistantDefaultOp" -> {
                    targets = ((String) targetObject).split("\\|");
                    if (targets.length != 2) {
                        throw new IllegalStateException("Wrong format of target: " + targetObject);
                    }
                    ownerId = getChatContextService().getChatOwner(targets[0]);
                    allow = currentUser.getUserId().equals(ownerId);
                    if (allow && targetNotBlank(targets[1])) {
                        String assistantUid = targets[1];
                        allow = getCharacterService().isOfficialByUid(assistantUid) ||
                                currentUser.getUserId().equals(getCharacterService().getOwnerByUid(assistantUid));
                    }
                }
                case "ttsSpeakDefaultOp" -> {
                    long messageId = (Long) targetObject;
                    ChatMessageRecord messageRecord = getChatMemoryService().get(messageId);
                    if (messageRecord == null) {
                        throw new IllegalArgumentException("Message not found.");
                    }
                    allow = currentUser.getUserId().equals(messageRecord.getChatOwnerId());
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
        if (promptService == null) {
            promptService = applicationContext.getBean(PromptService.class);
        }
        return promptService;
    }

    private PromptTaskService getPromptTaskService() {
        if (promptTaskService == null) {
            promptTaskService = applicationContext.getBean(PromptTaskService.class);
        }
        return promptTaskService;
    }

    private AgentService getAgentService() {
        if (agentService == null) {
            agentService = applicationContext.getBean(AgentService.class);
        }
        return agentService;
    }

    private PluginService getPluginService() {
        if (pluginService == null) {
            pluginService = applicationContext.getBean(PluginService.class);
        }
        return pluginService;
    }

    private CharacterService getCharacterService() {
        if (characterService == null) {
            characterService = applicationContext.getBean(CharacterService.class);
        }
        return characterService;
    }

    private ChatContextService getChatContextService() {
        if (chatContextService == null) {
            chatContextService = applicationContext.getBean(ChatContextService.class);
        }
        return chatContextService;
    }

    private ChatMemoryService getChatMemoryService() {
        if (chatMemoryService == null) {
            chatMemoryService = applicationContext.getBean(ChatMemoryService.class);
        }
        return chatMemoryService;
    }

    private AiApiKeyService getAiApiKeyService() {
        if (aiApiKeyService == null) {
            aiApiKeyService = applicationContext.getBean(AiApiKeyService.class);
        }
        return aiApiKeyService;
    }

    private OrgService getOrgService() {
        if (orgService == null) {
            orgService = applicationContext.getBean(OrgService.class);
        }
        return orgService;
    }

    private SysApiTokenService getSysApiTokenService() {
        if (sysApiTokenService == null) {
            sysApiTokenService = applicationContext.getBean(SysApiTokenService.class);
        }
        return sysApiTokenService;
    }

    private RagTaskService getRagTaskService() {
        if (ragTaskService == null) {
            ragTaskService = applicationContext.getBean((RagTaskService.class));
        }
        return ragTaskService;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
