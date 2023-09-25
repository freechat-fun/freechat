package fun.freechat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"fun.freechat"})
@MapperScan({"fun.freechat.mapper"})
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@EnableAspectJAutoProxy
@EnableCaching
@EnableRedisHttpSession(redisNamespace = "freechat:session")
@EnableWebSecurity
@EnableMethodSecurity
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
