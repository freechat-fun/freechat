package fun.freechat.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController implements org.springframework.boot.webmvc.error.ErrorController {
    @RequestMapping("/error")
    public ResponseEntity<?> handleError(HttpServletRequest request) {
        int statusCode = extractStatus(request);
        String originalUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        if (isApiRequest(originalUri, request)) {
            return ResponseEntity.status(statusCode)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(buildApiError(statusCode, originalUri, request));
        }

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/")).build();
    }

    // ---- 工具方法 ----

    private int extractStatus(HttpServletRequest req) {
        Object s = req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        return s != null ? Integer.parseInt(s.toString()) : HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    private boolean isApiRequest(String originalUri, HttpServletRequest req) {
        if (originalUri != null && originalUri.startsWith("/api/")) return true;
        String accept = req.getHeader("Accept");
        return accept != null && accept.contains("application/json");
    }

    private Map<String, Object> buildApiError(int code, String originalUri, HttpServletRequest req) {
        String msg = (String) req.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        return Map.of(
                "code",
                code,
                "error",
                HttpStatus.valueOf(code).getReasonPhrase(),
                "message",
                msg != null ? msg : "Request failed",
                "path",
                originalUri);
    }
}
