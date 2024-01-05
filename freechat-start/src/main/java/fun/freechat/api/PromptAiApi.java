package fun.freechat.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.langchain4j.model.output.Response;
import fun.freechat.api.dto.*;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.AiModelUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.AiModelInfo;
import fun.freechat.model.User;
import fun.freechat.service.ai.message.ChatMessage;
import fun.freechat.service.ai.message.ChatPromptContent;
import fun.freechat.service.enums.PromptFormat;
import fun.freechat.service.enums.PromptRole;
import fun.freechat.service.enums.PromptType;
import fun.freechat.service.prompt.PromptAiService;
import fun.freechat.service.prompt.PromptService;
import fun.freechat.service.util.InfoUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Controller
@Tag(name = "Prompt")
@RequestMapping("/api/v1/prompt")
@ResponseBody
@Validated
@SuppressWarnings("unused")
public class PromptAiApi {
    @Autowired
    private PromptService promptService;

    @Autowired
    private PromptAiService promptAiService;

    private String getString(Map<String, Object> parameters, String key) {
        if (Objects.isNull(parameters)) {
            return null;
        }
        Object value = parameters.get(key);
        return Objects.nonNull(value) ? value.toString() : null;
    }

    @Operation(
            operationId = "sendPrompt",
            summary = "Send Prompt",
            description = "Send the prompt to the AI service. Note that if the embedding model is called, the return is an embedding array, placed in the details field of the result; the original text is in the text field of the result."
    )
    @PostMapping("/send")
    @PreAuthorize("hasPermission(#p0, 'aiForPromptOp')")
    public LlmResultDTO send(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Call parameters",
                    content = @Content(
                            examples = @ExampleObject("""
                                    {
                                            "prompt": "say 'hello'",
                                            "params": {
                                                    "apiKey": "",
                                                    "modelId": "[open_ai]gpt-3.5-turbo"
                                            }
                                    }
                                    """
                            )
                    )
            )
            @RequestBody
            @NotNull
            PromptAiParamDTO aiRequest) {
        PromptAiParam promptAiParam = toPromptAiParam(aiRequest);

        String prompt = promptAiParam.getPrompt();
        PromptType promptType = promptAiParam.getPromptType();
        User user = promptAiParam.getUser();
        String apiKeyInfo = promptAiParam.getApiKeyInfo();
        AiModelInfo modelInfo = promptAiParam.getModelInfo();
        Map<String, Object> parameters = promptAiParam.getParameters();

        Response<ChatMessage> response =
                promptAiService.send(prompt, promptType, user, apiKeyInfo, modelInfo, parameters);
        if (Objects.isNull(response)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, prompt);
        }
        return LlmResultDTO.from(response);
    }

    @Operation(
            operationId = "streamSendPrompt",
            summary = "Send Prompt by Streaming Back",
            description = "Refer to /api/v1/prompt/send, stream back chunks of the response."
    )
    @PostMapping(value = "/send/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasPermission(#p0, 'aiForPromptOp')")
    public SseEmitter streamSend(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Call parameters",
                    content = @Content(
                            examples = @ExampleObject("""
                                    {
                                            "prompt": "say 'hello'",
                                            "params": {
                                                    "apiKey": "",
                                                    "modelId": "[open_ai]gpt-3.5-turbo"
                                            }
                                    }
                                    """
                            )
                    )
            )
            @RequestBody
            @NotNull
            PromptAiParamDTO aiRequest) {
        PromptAiParam promptAiParam = toPromptAiParam(aiRequest);

        String prompt = promptAiParam.getPrompt();
        PromptType promptType = promptAiParam.getPromptType();
        User user = promptAiParam.getUser();
        String apiKeyInfo = promptAiParam.getApiKeyInfo();
        AiModelInfo modelInfo = promptAiParam.getModelInfo();
        Map<String, Object> parameters = promptAiParam.getParameters();

        SseEmitter sseEmitter = new SseEmitter();
        promptAiService.streamSend(prompt, promptType, user, apiKeyInfo, modelInfo, parameters,
                CommonUtils.streamingResponseHandlerOf(sseEmitter));
        return sseEmitter;
    }

    private PromptAiParam toPromptAiParam(PromptAiParamDTO aiRequest) {
        Map<String, Object> parameters = aiRequest.getParams();
        String modelId = getString(parameters, "modelId");
        if (StringUtils.isBlank(modelId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "modelId must be defined.");
        }
        AiModelInfo modelInfo = AiModelUtils.getModelInfoDTO(getString(parameters, "modelId")).toAiModelInfo();
        User user;
        String apiKeyInfo;
        String apiKey = getString(parameters, "apiKey");
        String apiKeyName = getString(parameters, "apiKeyName");
        if (StringUtils.isBlank(apiKey) && StringUtils.isBlank(apiKeyName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "apiKey or apiKeyName must be defined.");
        } else if (StringUtils.isBlank(apiKey)) {
            user = AccountUtils.currentUser();
            apiKeyInfo = apiKeyName;
        } else {
            user = null;
            apiKeyInfo = apiKey;
        }

        String prompt = null;
        PromptType promptType  = null;
        if (StringUtils.isNotBlank(aiRequest.getPrompt())) {
            prompt = aiRequest.getPrompt();
            promptType = PromptType.STRING;
        } else if (Objects.nonNull(aiRequest.getPromptTemplate())) {
            PromptTemplateDTO promptTemplate = aiRequest.getPromptTemplate();
            if (StringUtils.isNotBlank(promptTemplate.getTemplate())) {
                prompt = promptService.apply(promptTemplate.getTemplate(),
                        promptTemplate.getVariables(),
                        PromptFormat.of(promptTemplate.getFormat()));
                promptType = PromptType.STRING;
            } else if (Objects.nonNull(promptTemplate.getChatTemplate())) {
                ChatPromptContentDTO chatTemplate = promptTemplate.getChatTemplate();
                Map<String, Object> variables = promptTemplate.getVariables();
                PromptFormat format = PromptFormat.of(promptTemplate.getFormat());
                if (Objects.isNull(chatTemplate.getMessageToSend()) &&
                        MapUtils.isNotEmpty(variables) &&
                        variables.containsKey("input")) {
                    ChatMessageDTO chatMessage = new ChatMessageDTO();
                    chatMessage.setRole(PromptRole.USER.text());
                    chatMessage.setGmtCreate(new Date());
                    chatMessage.setContent(format == PromptFormat.F_STRING ? "{input}" : "{{input}}");
                    chatTemplate.setMessageToSend(chatMessage);
                }
                try {
                    ChatPromptContent promptContent = promptService.apply(
                            chatTemplate.toChatPromptContent(), variables, format);
                    prompt = InfoUtils.defaultMapper().writeValueAsString(promptContent);
                    promptType = PromptType.CHAT;
                } catch (JsonProcessingException e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage(), e);
                }
            }
        } else if(Objects.nonNull(aiRequest.getPromptRef())) {
            PromptRefDTO promptRef = aiRequest.getPromptRef();
            Pair<String, PromptType> applied = promptService.apply(promptRef.getPromptId(),
                    promptRef.getVariables(), promptRef.getDraft());
            prompt = applied.getLeft();
            promptType = applied.getRight();
        }
        if (StringUtils.isBlank(prompt)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No prompt found.");
        }

        return new PromptAiParam(prompt, promptType, user, apiKeyInfo, modelInfo, parameters);
    }

    @Data
    @AllArgsConstructor
    private static class PromptAiParam {
        private String prompt;
        private PromptType promptType;
        private User user;
        private String apiKeyInfo;
        private AiModelInfo modelInfo;
        private Map<String, Object> parameters;
    }
}
