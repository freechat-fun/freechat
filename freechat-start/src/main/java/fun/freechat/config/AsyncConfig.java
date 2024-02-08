package fun.freechat.config;

import fun.freechat.util.TraceUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;

@Configuration
@SuppressWarnings("unused")
public class AsyncConfig {
    public static final String DEFAULT_EXECUTOR = "defaultExecutor";
    public static final String EVENT_EXECUTOR = "eventExecutor";

    @Configuration
    public static class DefaultExecutorConfiguration {
        @Bean(name = DEFAULT_EXECUTOR + "Properties")
        @Primary
        @ConfigurationProperties(prefix = "spring.task.default-execution")
        public TaskExecutionProperties taskExecutionProperties() {
            return new TaskExecutionProperties();
        }

        @Bean(name = DEFAULT_EXECUTOR)
        public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
            return builderFor(taskExecutionProperties())
                    .build(TraceThreadPoolTaskExecutor.class);
        }
    }

    @Configuration
    public static class EventExecutorConfiguration {
        @Bean(name = EVENT_EXECUTOR + "Properties")
        @ConfigurationProperties(prefix = "spring.task.event-execution")
        public TaskExecutionProperties taskExecutionProperties() {
            return new TaskExecutionProperties();
        }

        @Bean(name = EVENT_EXECUTOR)
        public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
            return builderFor(taskExecutionProperties())
                    .build(TraceThreadPoolTaskExecutor.class);
        }
    }

    // other executors...

    // --------
    private static ThreadPoolTaskExecutorBuilder builderFor(TaskExecutionProperties properties) {
        // pool properties
        TaskExecutionProperties.Pool pool = properties.getPool();
        ThreadPoolTaskExecutorBuilder builder = new ThreadPoolTaskExecutorBuilder();
        builder = builder.queueCapacity(pool.getQueueCapacity());
        builder = builder.corePoolSize(pool.getCoreSize());
        builder = builder.maxPoolSize(pool.getMaxSize());
        builder = builder.allowCoreThreadTimeOut(pool.isAllowCoreThreadTimeout());
        builder = builder.keepAlive(pool.getKeepAlive());

        // shutdown properties
        TaskExecutionProperties.Shutdown shutdown = properties.getShutdown();
        builder = builder.awaitTermination(shutdown.isAwaitTermination());
        builder = builder.awaitTerminationPeriod(shutdown.getAwaitTerminationPeriod());

        // other properties
        builder = builder.threadNamePrefix(properties.getThreadNamePrefix());

        return builder;
    }

    //========
    public static class TraceThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
        @Override
        public void execute(@NonNull Runnable task) {
            // make task traceable
            String traceId = TraceUtils.getTraceId();
            Map<String, Object> traceAttributes = TraceUtils.getTraceAttributes();
            super.execute(() -> {
                TraceUtils.startTrace(traceId);
                if (MapUtils.isNotEmpty(traceAttributes)) {
                    traceAttributes.forEach(TraceUtils::setTraceAttribute);
                }
                try {
                    task.run();
                } finally {
                    TraceUtils.endTrace();
                }
            });
        }
    }
}
