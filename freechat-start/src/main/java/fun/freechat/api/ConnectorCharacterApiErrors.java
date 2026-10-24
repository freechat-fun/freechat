package fun.freechat.api;

import fun.freechat.service.connectors.ConnectorException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = CharacterApi.class)
@RequiredArgsConstructor
public class ConnectorCharacterApiErrors {
    private final ConnectorApiErrors errors;

    @ExceptionHandler(ConnectorException.class)
    public ResponseEntity<Map<String, Object>> handle(ConnectorException error) {
        return errors.handle(error);
    }
}
