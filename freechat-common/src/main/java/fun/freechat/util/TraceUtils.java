package fun.freechat.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class TraceUtils {
    private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> TRACE_TIME = new ThreadLocal<>();

    public static String getTraceId() {
        String traceId = TRACE_ID.get();
        if (StringUtils.isNotBlank(traceId)) {
            return traceId;
        }
        traceId = UUID.randomUUID().toString().replaceAll("-", "");
        setTraceId(traceId);
        return traceId;
    }

    public static void setTraceId(String traceId) {
        TRACE_ID.set(traceId);
        TRACE_TIME.set(System.currentTimeMillis());
    }

    public static void startTrace(String traceId) {
        setTraceId(traceId);
    }

    public static void startTrace() {
        setTraceId(UUID.randomUUID().toString());
    }

    public static long stopTrace() {
        Long traceTime = TRACE_TIME.get();
        return traceTime == null ? 0 : System.currentTimeMillis() - traceTime;
    }

    public static boolean isTracing() {
        return TRACE_ID.get() != null;
    }

    public static void putTraceAttribute(String key, String value) {
        MDC.put(key, value);
    }

    public static String getTraceAttribute(String key) {
        return MDC.get(key);
    }

    public static void endTrace() {
        TRACE_ID.remove();
        TRACE_TIME.remove();
        MDC.clear();
    }

    public static Map<String, String> getTraceAttributes() {
        return MDC.getCopyOfContextMap();
    }

    public static Logger getPerfLogger() {
        return PerfLogger.logger;
    }

    public enum TraceStatus {
        SUCCESSFUL("S"),
        FAILED("F"),
        BAD_REQUEST("B");

        private final String info;

        TraceStatus(String info) {
            this.info = info;
        }

        public String info() {
            return info;
        }
    }

    public static class TraceInfoBuilder {
        // traceId|username|className::methodName|status(S/F/B)|elapseTime(ms)|args|return|errorMessage|extInfo

        private static final String SEPARATOR = "|";
        private static final String PLACEHOLDER = "-";
        private static final String COMMA = ",";

        private String traceId;
        private String username;
        private Object method;
        private TraceStatus status;
        private long elapseTime;
        private Object[] args;
        private Object response;
        private Throwable throwable;
        private String extInfo;

        public TraceInfoBuilder() {}

        public TraceInfoBuilder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public TraceInfoBuilder username(String username) {
            this.username = username;
            return this;
        }

        public TraceInfoBuilder method(Object method) {
            this.method = method;
            return this;
        }

        public TraceInfoBuilder status(TraceStatus status) {
            this.status = status;
            return this;
        }

        public TraceInfoBuilder elapseTime(long elapseTime) {
            this.elapseTime = elapseTime;
            return this;
        }

        public TraceInfoBuilder args(Object[] args) {
            this.args = args;
            return this;
        }

        public TraceInfoBuilder response(Object response) {
            this.response = response;
            return this;
        }

        public TraceInfoBuilder throwable(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        public TraceInfoBuilder extInfo(String extInfo) {
            this.extInfo = extInfo;
            return this;
        }

        private String wrap(String info) {
            return StringUtils.isBlank(info) ? PLACEHOLDER : info;
        }

        private String shortenPackageName(String info) {
            if (StringUtils.isNotBlank(info)) {
                char first = info.charAt(0);
                if (Character.isLowerCase(first)) {
                    return info.substring(0, 1);
                }
            }
            return info;
        }

        private String shortenClassName(String fullName) {
            return Arrays.stream(fullName.split("\\."))
                    .map(this::shortenPackageName)
                    .collect(Collectors.joining("."));
        }

        public String build() {
            String methodInfo = PLACEHOLDER;
            if (method != null) {
                if (method instanceof Method methodObj) {
                    String className = shortenClassName(methodObj.getDeclaringClass().getCanonicalName());
                    String methodName = methodObj.getName();
                    methodInfo = className + "::" + methodName;
                } else if (method instanceof String) {
                    methodInfo = (String) method;
                }
            }

            String statusInfo = Optional.ofNullable(status).map(TraceStatus::info).orElse(PLACEHOLDER);

            String argsInfo = PLACEHOLDER;
            if (args != null && args.length > 0) {
                argsInfo = Arrays.stream(args)
                        .map(PojoUtils::object2JsonString)
                        .collect(Collectors.joining(COMMA));
            }
            String responseInfo = Optional.ofNullable(response)
                    .map(PojoUtils::object2JsonString)
                    .map(this::wrap)
                    .orElse(PLACEHOLDER);

            String throwableMessage = Optional.ofNullable(throwable)
                    .map(Throwable::getMessage)
                    .map(this::wrap)
                    .orElse(PLACEHOLDER);

            return wrap(traceId) + SEPARATOR +
                    wrap(username) + SEPARATOR +
                    methodInfo + SEPARATOR +
                    statusInfo + SEPARATOR +
                    elapseTime + SEPARATOR +
                    argsInfo + SEPARATOR +
                    responseInfo + SEPARATOR +
                    throwableMessage + SEPARATOR +
                    wrap(extInfo);
        }
    }

    public static class PerfLogger {
        static Logger logger = LoggerFactory.getLogger(PerfLogger.class);
    }
}
