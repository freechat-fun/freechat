package fun.freechat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SuppressWarnings("unused")
public class TomcatConfig {
    @Value("${spring.mvc.async.requestTimeout}")
    private long asyncRequestTimeout;

    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(
                connector -> connector.setAsyncTimeout(asyncRequestTimeout)
        );
        return factory;
    }
}