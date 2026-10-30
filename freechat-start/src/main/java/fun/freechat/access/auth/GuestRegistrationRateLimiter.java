package fun.freechat.access.auth;

import fun.freechat.config.GuestRegistrationProperties;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.redisson.api.ratelimiter.RateLimiterArgs;

/** Redis token bucket shared by every node, capping automatically registered guests. */
public class GuestRegistrationRateLimiter {
    public static final String RATE_LIMITER_NAME = "guest-registration";

    private final RRateLimiter rateLimiter;

    public GuestRegistrationRateLimiter(RedissonClient redissonClient, GuestRegistrationProperties properties) {
        rateLimiter = redissonClient.getRateLimiter(RATE_LIMITER_NAME);
        // keepState(true): a restart re-reads the configured limit without refunding spent permits.
        rateLimiter.setRate(
                RateLimiterArgs.of(RateType.OVERALL, properties.getMaxRegistrations(), properties.getInterval())
                        .keepState(true));
    }

    public boolean tryAcquire() {
        return rateLimiter.tryAcquire();
    }
}
