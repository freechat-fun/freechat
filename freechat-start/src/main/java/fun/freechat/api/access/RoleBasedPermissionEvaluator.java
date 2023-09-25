package fun.freechat.api.access;

import fun.freechat.api.dto.PromptAiParamDTO;
import fun.freechat.api.dto.PromptRefDTO;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.model.User;
import fun.freechat.service.ai.AiApiKeyService;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.enums.Visibility;
import fun.freechat.service.flow.FlowService;
import fun.freechat.service.plugin.PluginService;
import fun.freechat.service.prompt.PromptService;
import fun.freechat.service.prompt.PromptTaskService;
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

    private FlowService flowService;

    private PluginService pluginService;

    private CharacterService characterService;

    private AiApiKeyService aiApiKeyService;

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
                case "promptDefaultOp" -> allow = targetObject instanceof String infoId &&
                        currentUser.getUserId().equals(getPromptService().getOwner(infoId));
                case "promptTaskDefaultOp" -> allow = targetObject instanceof String infoId &&
                        currentUser.getUserId().equals(getPromptTaskService().getOwner(infoId));
                case "characterDefaultOp" -> allow = targetObject instanceof String infoId &&
                        currentUser.getUserId().equals(getCharacterService().getOwner(infoId));
                case "characterBackendDefaultOp" -> allow = targetObject instanceof String backendId &&
                        currentUser.getUserId().equals(getCharacterService().getBackendOwner(backendId));
                case "characterBackendCreateOp" -> {
                    targets = ((String) targetObject).split("\\|");
                    if (targets.length != 2) {
                        throw new RuntimeException("Wrong format of target: " + targetObject);
                    }
                    ownerId = getCharacterService().getOwner(targets[0]);
                    allow = currentUser.getUserId().equals(ownerId);
                    if (StringUtils.isNotBlank(targets[1])) {
                        allow = allow && currentUser.getUserId().equals(getPromptService().getOwner(targets[1]));
                    }
                }
                case "characterBackendUpdateOp" -> {
                    targets = ((String) targetObject).split("\\|");
                    if (targets.length != 2) {
                        throw new RuntimeException("Wrong format of target: " + targetObject);
                    }
                    ownerId = getCharacterService().getBackendOwner(targets[0]);
                    allow = currentUser.getUserId().equals(ownerId);
                    if (StringUtils.isNotBlank(targets[1])) {
                        allow = allow && currentUser.getUserId().equals(getPromptService().getOwner(targets[1]));
                    }
                }
                case "promptCreateOp", "flowCreateOp", "pluginCreateOp", "characterCreateOp" ->
                        allow = checkVisibility((String) targetObject, currentRole);
                case "promptUpdateOp" -> {
                    targets = ((String) targetObject).split("\\|");
                    if (targets.length != 2) {
                        throw new RuntimeException("Wrong format of target: " + targetObject);
                    }
                    ownerId = getPromptService().getOwner(targets[0]);
                    allow = currentUser.getUserId().equals(ownerId) &&
                            checkVisibility(targets[1], currentRole);
                }
                case "flowUpdateOp" -> {
                    targets = ((String) targetObject).split("\\|");
                    if (targets.length != 2) {
                        throw new RuntimeException("Wrong format of target: " + targetObject);
                    }
                    ownerId = getFlowService().getOwner(targets[0]);
                    allow = currentUser.getUserId().equals(ownerId) &&
                            checkVisibility(targets[1], currentRole);
                }
                case "pluginUpdateOp" -> {
                    targets = ((String) targetObject).split("\\|");
                    if (targets.length != 2) {
                        throw new RuntimeException("Wrong format of target: " + targetObject);
                    }
                    ownerId = getPluginService().getOwner(targets[0]);
                    allow = currentUser.getUserId().equals(ownerId) &&
                            checkVisibility(targets[1], currentRole);
                }
                case "characterUpdateOp" -> {
                    targets = ((String) targetObject).split("\\|");
                    if (targets.length != 2) {
                        throw new RuntimeException("Wrong format of target: " + targetObject);
                    }
                    ownerId = getCharacterService().getOwner(targets[0]);
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
                    if (StringUtils.isNotBlank(targets[1])) {
                        allow = allow && currentUser.getUserId().equals(getPromptService().getOwner(targets[1]));
                    }
                }
                case "promptDeleteOp" -> {
                    var info = getPromptService().summary((String) targetObject, currentUser);
                    allow = Objects.nonNull(info) &&
                            currentUser.getUserId().equals(info.getLeft().getUserId()) &&
                            checkVisibility(info.getLeft().getVisibility(), currentRole);
                }
                case "flowDeleteOp" -> {
                    var info = getFlowService().summary((String) targetObject, currentUser);
                    allow = Objects.nonNull(info) &&
                            currentUser.getUserId().equals(info.getLeft().getUserId()) &&
                            checkVisibility(info.getLeft().getVisibility(), currentRole);
                }
                case "pluginDeleteOp" -> {
                    var info = getPluginService().summary((String) targetObject, currentUser);
                    allow = Objects.nonNull(info) &&
                            currentUser.getUserId().equals(info.getLeft().getUserId()) &&
                            checkVisibility(info.getLeft().getVisibility(), currentRole);
                }
                case "characterDeleteOp" -> {
                    var info = getCharacterService().summary((String) targetObject, currentUser);
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
                    allow = StringUtils.isNotBlank(promptRef.getPromptId());
                    if (allow) {
                        ownerId = getPromptService().getOwner(promptRef.getPromptId());
                        allow = currentUser.getUserId().equals(ownerId);
                    }
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
            case PUBLIC_ORG ->
                    AuthorityUtils.getPriorityOfRole(role) >= AuthorityUtils.getPriority(AuthorityUtils.RES_ADMIN);
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

    private FlowService getFlowService() {
        if (Objects.isNull(flowService)) {
            flowService = applicationContext.getBean(FlowService.class);
        }
        return flowService;
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

    private AiApiKeyService getAiApiKeyService() {
        if (Objects.isNull(aiApiKeyService)) {
            aiApiKeyService = applicationContext.getBean(AiApiKeyService.class);
        }
        return aiApiKeyService;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
