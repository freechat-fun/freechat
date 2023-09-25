package fun.freechat.util;

import org.apache.commons.collections4.MapUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

@SuppressWarnings("unused")
public class TraceThreadPoolExecutor extends ThreadPoolExecutor {

    public TraceThreadPoolExecutor(int corePoolSize,
                                   int maximumPoolSize,
                                   long keepAliveTime,
                                   TimeUnit milliseconds,
                                   BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, milliseconds, workQueue);
    }

    public TraceThreadPoolExecutor(int corePoolSize,
                                   int maximumPoolSize,
                                   long keepAliveTime,
                                   TimeUnit milliseconds,
                                   LinkedBlockingQueue<Runnable> workQueue,
                                   ThreadFactory factory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, milliseconds, workQueue, factory);
    }

    public TraceThreadPoolExecutor(int corePoolSize,
                                   int maximumPoolSize,
                                   long keepAliveTime,
                                   TimeUnit unit,
                                   BlockingQueue<Runnable> workQueue,
                                   ThreadFactory threadFactory,
                                   RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                threadFactory, handler);
    }

    @Override
    public void execute(@SuppressWarnings("NullableProblems") Runnable runnable) {
        Objects.requireNonNull(runnable);
        String traceId = TraceUtils.getTraceId();
        Map<String, Object> traceAttributes = TraceUtils.getTraceAttributes();
        super.execute(() -> {
            TraceUtils.startTrace(traceId);
            if (MapUtils.isNotEmpty(traceAttributes)) {
                traceAttributes.forEach(TraceUtils::setTraceAttribute);
            }
            try {
                runnable.run();
            } finally {
                TraceUtils.endTrace();
            }
        });
    }


}
