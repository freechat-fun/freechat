package fun.freechat.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.output.FinishReason;
import fun.freechat.api.dto.ChatPromptContentDTO;
import fun.freechat.api.dto.LlmResultDTO;
import fun.freechat.api.dto.PromptAiParamDTO;
import fun.freechat.api.dto.PromptRefDTO;
import fun.freechat.api.dto.PromptTemplateDTO;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.AiModelUtils;
import fun.freechat.model.AiModelInfo;
import fun.freechat.model.User;
import fun.freechat.service.enums.PromptFormat;
import fun.freechat.service.enums.PromptType;
import fun.freechat.service.prompt.ChatPromptContent;
import fun.freechat.service.prompt.PromptAiService;
import fun.freechat.service.prompt.PromptService;
import fun.freechat.service.util.InfoUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@RestController
@Tag(name = "Prompt")
@RequestMapping("/api/v2/prompt")
@Validated
@Slf4j
@SuppressWarnings("unused")
public class PromptAiApi {
    @Autowired
    private PromptService promptService;
    @Autowired
    private PromptAiService promptAiService;

    private String getString(Map<String, Object> parameters, String key) {
        if (parameters == null) {
            return null;
        }
        Object value = parameters.get(key);
        return value != null ? value.toString() : null;
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

        ChatResponse response =
                promptAiService.send(prompt, promptType, user, apiKeyInfo, modelInfo, parameters);
        if (response == null) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, prompt);
        }
        return LlmResultDTO.from(response, null);
    }

    @Operation(
            operationId = "streamSendPrompt",
            summary = "Send Prompt by Streaming Back",
            description = "Refer to /api/v2/prompt/send, stream back chunks of the response."
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
            PromptAiParamDTO aiRequest,
            HttpServletResponse servletResponse) {
        PromptAiParam promptAiParam = toPromptAiParam(aiRequest);

        String prompt = promptAiParam.getPrompt();
        PromptType promptType = promptAiParam.getPromptType();
        User user = promptAiParam.getUser();
        String apiKeyInfo = promptAiParam.getApiKeyInfo();
        AiModelInfo modelInfo = promptAiParam.getModelInfo();
        Map<String, Object> parameters = promptAiParam.getParameters();

        SseEmitter sseEmitter = new SseEmitter();
        promptAiService.streamSend(prompt, promptType, user, apiKeyInfo, modelInfo, parameters,
                new StreamingChatResponseHandler() {
                    private boolean abort = false;

                    @Override
                    public void onPartialResponse(String partialResult) {
                        if (abort) {
                            return;
                        }
                        try {
                            LlmResultDTO result = LlmResultDTO.from(
                                    partialResult, null, null, null);
                            result.setRequestId(null);
                            sseEmitter.send(result);
                        } catch (NullPointerException | IOException e) {
                            log.error("Error when sending message.", e);
                            sseEmitter.completeWithError(e);
                            abort = true;
                        }
                    }

                    @Override
                    public void onCompleteResponse(ChatResponse response) {
                        if (abort) {
                            return;
                        }
                        try {
                            LlmResultDTO result = LlmResultDTO.from(response, null);
                            Objects.requireNonNull(result).setText(null);
                            result.setRequestId(null);
                            sseEmitter.send(result);
                            sseEmitter.complete();
                        } catch (NullPointerException | IOException e) {
                            log.error("Error when sending message.", e);
                            sseEmitter.completeWithError(e);
                        }
                        abort = true;
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (abort) {
                            return;
                        }
                        log.error("SSE exception", throwable);
                        try {
                            LlmResultDTO result = LlmResultDTO.from(
                                    throwable.getMessage(), null, null, null);
                            result.setFinishReason(FinishReason.OTHER.name().toLowerCase());
                            result.setRequestId(null);
                            sseEmitter.send(result);
                            sseEmitter.complete();
                        } catch (IOException e) {
                            log.error("Error when sending message.", e);
                            sseEmitter.completeWithError(e);
                        }
                        abort = true;
                    }
                });

        servletResponse.addHeader("X-Accel-Buffering", "no");
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
        if (StringUtils.isBlank(apiKeyName) || StringUtils.isNotBlank(apiKey)) {
            user = null;
            apiKeyInfo = apiKey;
        } else {
            user = AccountUtils.currentUser();
            apiKeyInfo = apiKeyName;
        }

        String prompt = null;
        PromptType promptType  = null;
        if (StringUtils.isNotBlank(aiRequest.getPrompt())) {
            prompt = aiRequest.getPrompt();
            promptType = PromptType.STRING;
        } else if (aiRequest.getPromptTemplate() != null) {
            PromptTemplateDTO promptTemplate = aiRequest.getPromptTemplate();
            if (StringUtils.isNotBlank(promptTemplate.getTemplate())) {
                prompt = promptService.apply(promptTemplate.getTemplate(),
                        promptTemplate.getVariables(),
                        PromptFormat.of(promptTemplate.getFormat()));
                promptType = PromptType.STRING;
            } else if (promptTemplate.getChatTemplate() != null) {
                ChatPromptContentDTO chatTemplate = promptTemplate.getChatTemplate();
                Map<String, Object> variables = promptTemplate.getVariables();
                PromptFormat format = PromptFormat.of(promptTemplate.getFormat());

                try {
                    ChatPromptContent promptContent = promptService.apply(
                            chatTemplate.toChatPromptContent(), variables, format);
                    prompt = InfoUtils.defaultMapper().writeValueAsString(promptContent);
                    promptType = PromptType.CHAT;
                } catch (JsonProcessingException e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage(), e);
                }
            }
        } else if(aiRequest.getPromptRef() != null) {
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
