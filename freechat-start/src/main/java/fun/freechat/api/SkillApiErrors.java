package fun.freechat.api;

import fun.freechat.service.skills.SkillException;
import java.util.Map;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {SkillApi.class, CharacterApi.class})
public class SkillApiErrors {
    @ExceptionHandler(SkillException.class)
    public ResponseEntity<Map<String, Object>> handle(SkillException error) {
        HttpStatus status =
                switch (error.code()) {
                    case SKILL_NOT_FOUND -> HttpStatus.NOT_FOUND;
                    case SKILL_ACCESS_DENIED -> HttpStatus.FORBIDDEN;
                    case SKILL_VERSION_CONFLICT, SKILL_DRAFT_EXPIRED -> HttpStatus.CONFLICT;
                    case SKILL_LIMIT_EXCEEDED -> HttpStatus.TOO_MANY_REQUESTS;
                    case SKILL_REVIEW_UNAVAILABLE, SKILL_SOURCE_UNAVAILABLE, SKILL_STORAGE_UNAVAILABLE ->
                        HttpStatus.SERVICE_UNAVAILABLE;
                    default -> HttpStatus.UNPROCESSABLE_CONTENT;
                };
        return ResponseEntity.status(status)
                .cacheControl(CacheControl.noStore())
                .body(Map.of(
                        "code",
                        status.value(),
                        "error",
                        status.getReasonPhrase(),
                        "message",
                        error.code().name(),
                        "errorCode",
                        error.code().name()));
    }
}
