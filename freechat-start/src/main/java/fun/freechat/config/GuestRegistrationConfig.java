package fun.freechat.config;

import fun.freechat.access.auth.GuestRegistrationRateLimiter;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SuppressWarnings("unused")
public class GuestRegistrationConfig {
    @Bean
    @ConfigurationProperties("auth.guest.registration")
    public GuestRegistrationProperties guestRegistrationProperties() {
        return new GuestRegistrationProperties();
    }

    @Bean
    public GuestRegistrationRateLimiter guestRegistrationRateLimiter(
            RedissonClient redissonClient, GuestRegistrationProperties properties) {
        return new GuestRegistrationRateLimiter(redissonClient, properties);
    }
}
