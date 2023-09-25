package fun.freechat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import fun.freechat.access.trace.TraceInterceptor;

import java.util.Objects;

@Configuration
@SuppressWarnings("unused")
public class InterceptorConfig implements WebMvcConfigurer {
    @Value("${perf.trace.excludeUri:#{null}}")
    private String[] traceExcludeUri;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration =
                registry.addInterceptor(traceInterceptor())
                        .addPathPatterns("/**");
        if (Objects.nonNull(traceExcludeUri) && traceExcludeUri.length > 0) {
            registration.excludePathPatterns(traceExcludeUri);
        }

        WebMvcConfigurer.super.addInterceptors(registry);
    }

    @Bean
    public TraceInterceptor traceInterceptor() {
        return new TraceInterceptor();
    }
}