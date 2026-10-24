package fun.freechat.api;

import fun.freechat.service.connectors.ConnectorException;
import java.util.Map;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = {ConnectorApi.class, ConnectorTestApi.class})
public class ConnectorApiErrors {
    @ExceptionHandler(ConnectorException.class)
    public ResponseEntity<Map<String, Object>> handle(ConnectorException error) {
        HttpStatus status =
                switch (error.code()) {
                    case CONNECTOR_NOT_FOUND -> HttpStatus.NOT_FOUND;
                    case CONNECTOR_ACCESS_DENIED -> HttpStatus.FORBIDDEN;
                    case CONNECTOR_VERSION_CONFLICT -> HttpStatus.CONFLICT;
                    case CONNECTOR_LIMIT_EXCEEDED -> HttpStatus.TOO_MANY_REQUESTS;
                    case CONNECTOR_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
                    case CONNECTOR_TIMEOUT -> HttpStatus.GATEWAY_TIMEOUT;
                    default -> HttpStatus.UNPROCESSABLE_CONTENT;
                };
        return response(status, error.code());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<Map<String, Object>> invalidRequest(Exception ignored) {
        return response(HttpStatus.BAD_REQUEST, ConnectorException.Code.CONNECTOR_INVALID_CONFIGURATION);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> inaccessible(AccessDeniedException ignored) {
        // Missing and foreign connector identifiers are indistinguishable, including for administrators.
        return response(HttpStatus.NOT_FOUND, ConnectorException.Code.CONNECTOR_NOT_FOUND);
    }

    private static ResponseEntity<Map<String, Object>> response(HttpStatus status, ConnectorException.Code code) {
        return ResponseEntity.status(status)
                .cacheControl(CacheControl.noStore())
                .body(Map.of(
                        "code",
                        status.value(),
                        "error",
                        status.getReasonPhrase(),
                        "message",
                        code.name(),
                        "errorCode",
                        code.name()));
    }
}
