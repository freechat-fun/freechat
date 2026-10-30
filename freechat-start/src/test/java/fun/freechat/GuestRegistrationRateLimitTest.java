package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fun.freechat.access.auth.GuestRegistrationRateLimiter;
import fun.freechat.api.dto.UserFullDetailsDTO;
import fun.freechat.config.GuestRegistrationConfig;
import fun.freechat.config.GuestRegistrationProperties;
import fun.freechat.model.User;
import fun.freechat.service.account.SysUserService;
import fun.freechat.web.AuthController;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockSettings;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.redisson.api.ratelimiter.RateLimiterArgs;
import org.redisson.api.ratelimiter.RateLimiterParams;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

class GuestRegistrationRateLimitTest {
    private static final MockSettings PROXY = withSettings().mockMaker(org.mockito.MockMakers.PROXY);

    private final RedissonClient redissonClient = mock(RedissonClient.class, PROXY);
    private final RRateLimiter bucket = mock(RRateLimiter.class, PROXY);

    @Test
    void limiterPublishesConfiguredBucketToRedisAndKeepsSpentPermits() {
        limiter(3, Duration.ofSeconds(30));

        RateLimiterParams published = publishedRate();
        assertEquals(RateType.OVERALL, published.getMode());
        assertEquals(3, published.getRate());
        assertEquals(Duration.ofSeconds(30), published.getRateInterval());
        assertTrue(published.isKeepState(), "A restart must not refund permits of the running window");
    }

    @Test
    void everyRequestSpendsASharedPermitAndDenialsArePropagated() {
        when(bucket.tryAcquire()).thenReturn(true, true, false);
        GuestRegistrationRateLimiter limiter = limiter(2, Duration.ofMinutes(10));

        assertTrue(limiter.tryAcquire());
        assertTrue(limiter.tryAcquire());
        assertFalse(limiter.tryAcquire());
        verify(bucket, times(3)).tryAcquire();
    }

    @Test
    void exhaustedBucketRejectsGuestAndCreatesNoUser() {
        SysUserService userService = mock(SysUserService.class, PROXY);
        AuthController controller = controller(userService, limiterDenied());

        ResponseStatusException failure = assertThrows(ResponseStatusException.class, controller::registerGuest);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, failure.getStatusCode());
        verifyNoInteractions(userService);
    }

    @Test
    void availablePermitRegistersGuest() {
        SysUserService userService = mock(SysUserService.class, PROXY);
        when(userService.create(any(User.class))).thenAnswer(call -> {
            User guest = call.getArgument(0);
            guest.withUserId("guest-user-id").withEnabled((byte) 1).withLocked((byte) 0);
            return true;
        });
        AuthController controller = controller(userService, limiterAllowed());

        UserFullDetailsDTO details = controller.registerGuest();

        ArgumentCaptor<User> guest = ArgumentCaptor.forClass(User.class);
        verify(userService).create(guest.capture());
        assertEquals("guest", guest.getValue().getPlatform());
        assertTrue(guest.getValue().getUsername().endsWith("@guest"));
        assertEquals(guest.getValue().getUsername(), details.getUsername());
        assertEquals(guest.getValue().getPassword(), details.getPassword());
    }

    @Test
    void rejectedConfigurationIsReportedWithItsPropertyPrefix() {
        assertEquals(
                "auth.guest.registration.maxRegistrations must be positive",
                assertThrows(IllegalArgumentException.class, unvalidated(0, Duration.ofMinutes(10))::afterPropertiesSet)
                        .getMessage());

        assertTrue(assertThrows(IllegalArgumentException.class, unvalidated(10, Duration.ZERO)::afterPropertiesSet)
                .getMessage()
                .startsWith("auth.guest.registration.interval"));
    }

    @Test
    void shippedYamlWiresTheBucketWithItsDocumentedDefaults() {
        context().run(context -> {
            assertNull(context.getStartupFailure());
            GuestRegistrationProperties properties = context.getBean(GuestRegistrationProperties.class);
            assertEquals(10, properties.getMaxRegistrations());
            assertEquals(Duration.ofMinutes(10), properties.getInterval());

            RateLimiterParams published = publishedRate();
            assertEquals(RateType.OVERALL, published.getMode());
            assertEquals(10, published.getRate());
            assertEquals(Duration.ofMinutes(10), published.getRateInterval());
        });
    }

    @Test
    void yamlOverridesRetuneTheBucketWithoutCodeChanges() {
        context()
                .withPropertyValues(
                        "auth.guest.registration.maxRegistrations=2", "auth.guest.registration.interval=PT30S")
                .run(context -> {
                    assertNull(context.getStartupFailure());
                    GuestRegistrationProperties properties = context.getBean(GuestRegistrationProperties.class);
                    assertEquals(2, properties.getMaxRegistrations());
                    assertEquals(Duration.ofSeconds(30), properties.getInterval());

                    RateLimiterParams published = publishedRate();
                    assertEquals(2, published.getRate());
                    assertEquals(Duration.ofSeconds(30), published.getRateInterval());
                });
    }

    private ApplicationContextRunner context() {
        when(redissonClient.getRateLimiter(GuestRegistrationRateLimiter.RATE_LIMITER_NAME))
                .thenReturn(bucket);
        return new ApplicationContextRunner()
                .withInitializer(new ConfigDataApplicationContextInitializer())
                .withConfiguration(AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class))
                .withUserConfiguration(GuestRegistrationConfig.class)
                .withBean(RedissonClient.class, () -> redissonClient);
    }

    private RateLimiterParams publishedRate() {
        ArgumentCaptor<RateLimiterArgs> args = ArgumentCaptor.forClass(RateLimiterArgs.class);
        verify(bucket).setRate(args.capture());
        return (RateLimiterParams) args.getValue();
    }

    private GuestRegistrationRateLimiter limiterAllowed() {
        when(bucket.tryAcquire()).thenReturn(true);
        return limiter(10, Duration.ofMinutes(10));
    }

    private GuestRegistrationRateLimiter limiterDenied() {
        when(bucket.tryAcquire()).thenReturn(false);
        return limiter(10, Duration.ofMinutes(10));
    }

    private GuestRegistrationRateLimiter limiter(int maxRegistrations, Duration interval) {
        GuestRegistrationProperties properties = new GuestRegistrationProperties();
        properties.setMaxRegistrations(maxRegistrations);
        properties.setInterval(interval);
        when(redissonClient.getRateLimiter(GuestRegistrationRateLimiter.RATE_LIMITER_NAME))
                .thenReturn(bucket);
        return new GuestRegistrationRateLimiter(redissonClient, properties);
    }

    private static GuestRegistrationProperties unvalidated(int maxRegistrations, Duration interval) {
        GuestRegistrationProperties properties = new GuestRegistrationProperties();
        properties.setMaxRegistrations(maxRegistrations);
        properties.setInterval(interval);
        return properties;
    }

    private static AuthController controller(SysUserService userService, GuestRegistrationRateLimiter limiter) {
        AuthController controller = new AuthController();
        ReflectionTestUtils.setField(controller, "userService", userService);
        ReflectionTestUtils.setField(controller, "guestRegistrationRateLimiter", limiter);
        return controller;
    }
}
