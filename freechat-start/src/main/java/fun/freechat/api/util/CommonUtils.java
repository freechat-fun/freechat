package fun.freechat.api.util;

import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.output.FinishReason;
import dev.langchain4j.model.output.Response;
import fun.freechat.api.dto.LlmResultDTO;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

@Slf4j
public class CommonUtils {
    @NonNull
    public static <S, T> T convert(@NonNull S source, @NonNull Class<T> clazz) {
        try {
            Objects.requireNonNull(source);
            T response = clazz.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, response);
            return response;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Failed to construct instance of {}", clazz.getSimpleName(), e);
            throw new RuntimeException(e);
        }
    }

    public static <T> StreamingResponseHandler<T> streamingResponseHandlerOf(SseEmitter sseEmitter) {
        return new StreamingResponseHandler<T>() {
            private boolean abort = false;

            @Override
            public void onNext(String partialResult) {
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
            public void onComplete(Response<T> response) {
                if (abort) {
                    return;
                }
                try {
                    LlmResultDTO result = LlmResultDTO.from(response);
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
        };
    }
}
