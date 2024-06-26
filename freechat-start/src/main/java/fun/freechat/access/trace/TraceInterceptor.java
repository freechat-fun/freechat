package fun.freechat.access.trace;

import fun.freechat.exception.BadRequestException;
import fun.freechat.util.TraceUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Nullable;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class TraceInterceptor implements HandlerInterceptor {
    private final List<Class<? extends Exception>> badRequestExceptions = List.of(
            BadRequestException.class,
            ServletRequestBindingException.class,
            RequestRejectedException.class,
            AuthenticationException.class,
            ConstraintViolationException.class,
            ClientAbortException.class
    );

    private boolean isBadRequest(int status, Exception ex) {
        if (Objects.nonNull(ex) && badRequestExceptions.contains(ex.getClass())) {
            return true;
        } else {
            return status >= 400 && status < 500;
        }
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Object handler) {
        String eagleEyeId = request.getHeader("EagleEye-TraceId");
        if (StringUtils.isNotBlank(eagleEyeId)) {
            TraceUtils.startTrace(eagleEyeId);
        } else {
            TraceUtils.startTrace();
        }
        Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .ifPresent(username -> TraceUtils.setTraceAttribute("TRACE_USER", username));
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                @Nullable Object handler, Exception ex) {
        if (!TraceUtils.isTracing()) {
            return;
        }

        String username = Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .orElse((String) TraceUtils.getTraceAttribute("TRACE_USER"));

        String service = request.getServletPath();
        String method = request.getMethod();
        String[] args = new String[]{ request.getQueryString() };
        int responseCode = response.getStatus();
        TraceUtils.TraceStatus status = TraceUtils.TraceStatus.SUCCESSFUL;
        if (Objects.nonNull(ex) || responseCode >= 400) {
            status = isBadRequest(responseCode, ex) ?
                    TraceUtils.TraceStatus.BAD_REQUEST :
                    TraceUtils.TraceStatus.FAILED;
        }
        long elapseTime = TraceUtils.endTrace();

        TraceUtils.TraceInfoBuilder builder = new TraceUtils.TraceInfoBuilder();
        builder.setTraceId(TraceUtils.getTraceId())
                .setUsername(username)
                .setMethod(service + "::" + method)
                .setStatus(status)
                .setArgs(args)
                .setResponse(responseCode)
                .setThrowable(ex)
                .setElapseTime(elapseTime);

        log.trace(builder.build());
    }
}
