package fun.freechat.config;

import static org.assertj.core.api.Assertions.assertThat;

import fun.freechat.service.chat.memory.LongTermMemoryProperties;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.StandardEnvironment;

class LongTermMemoryConfigTest {
    private static final String PREFIX = "chat.memory.long-term.";

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(context -> {
                context.getEnvironment()
                        .getPropertySources()
                        .remove(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME);
                context.getEnvironment()
                        .getPropertySources()
                        .remove(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME);
            })
            .withUserConfiguration(BindingConfiguration.class, LongTermMemoryConfig.class);

    @Test
    void registersOnePropertiesBeanWithValidDefaults() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed().hasSingleBean(LongTermMemoryProperties.class);
            LongTermMemoryProperties properties = context.getBean(LongTermMemoryProperties.class);
            assertThat(context.getBean("longTermMemoryProperties")).isSameAs(properties);
            assertThat(properties.getIdleTimeout()).isEqualTo(Duration.parse("PT1H"));
            assertThat(properties.getScanInterval()).isEqualTo(Duration.ofSeconds(30));
            assertThat(properties.getGcGracePeriod()).isEqualTo(Duration.ofHours(24));
            assertThat(properties.getTokenEstimatorBean()).isEmpty();
        });
    }

    @Test
    void bindsCustomDurationsAndAllOtherSettingsWithoutApplicationServices() {
        contextRunner
                .withPropertyValues(
                        PREFIX + "idle-timeout=PT2H",
                        PREFIX + "scan-interval=750ms",
                        PREFIX + "workers-per-node=3",
                        PREFIX + "dispatch-batch-size=25",
                        PREFIX + "turn-lease=40s",
                        PREFIX + "lease-renew-interval=5s",
                        PREFIX + "turn-max-duration=20m",
                        PREFIX + "job-max-duration=PT15M",
                        PREFIX + "extraction-claim-lease=3m",
                        PREFIX + "extraction-timeout=PT25S",
                        PREFIX + "foreground-timeout=45s",
                        PREFIX + "max-foreground-batches=3",
                        PREFIX + "max-attempts=7",
                        PREFIX + "retry-initial-delay=2s",
                        PREFIX + "retry-max-delay=PT2M",
                        PREFIX + "extraction-max-input-tokens=7000",
                        PREFIX + "max-input-tokens=10000",
                        PREFIX + "response-reserve-tokens=1500",
                        PREFIX + "summary-max-tokens=600",
                        PREFIX + "profile-max-tokens=400",
                        PREFIX + "profile-max-facts=20",
                        PREFIX + "search-min-score=0.75",
                        PREFIX + "search-query-max-chars=1500",
                        PREFIX + "tool-result-max-tokens=800",
                        PREFIX + "tool-total-max-tokens=2400",
                        PREFIX + "max-tool-rounds=4",
                        PREFIX + "gc-grace-period=2d",
                        PREFIX + "token-estimator-bean=testTokenEstimator")
                .run(context -> {
                    assertThat(context).hasNotFailed().hasSingleBean(LongTermMemoryProperties.class);
                    LongTermMemoryProperties properties = context.getBean(LongTermMemoryProperties.class);
                    assertThat(properties.getIdleTimeout()).isEqualTo(Duration.ofHours(2));
                    assertThat(properties.getScanInterval()).isEqualTo(Duration.ofMillis(750));
                    assertThat(properties.getWorkersPerNode()).isEqualTo(3);
                    assertThat(properties.getDispatchBatchSize()).isEqualTo(25);
                    assertThat(properties.getTurnLease()).isEqualTo(Duration.ofSeconds(40));
                    assertThat(properties.getLeaseRenewInterval()).isEqualTo(Duration.ofSeconds(5));
                    assertThat(properties.getTurnMaxDuration()).isEqualTo(Duration.ofMinutes(20));
                    assertThat(properties.getJobMaxDuration()).isEqualTo(Duration.ofMinutes(15));
                    assertThat(properties.getExtractionClaimLease()).isEqualTo(Duration.ofMinutes(3));
                    assertThat(properties.getExtractionTimeout()).isEqualTo(Duration.ofSeconds(25));
                    assertThat(properties.getForegroundTimeout()).isEqualTo(Duration.ofSeconds(45));
                    assertThat(properties.getMaxForegroundBatches()).isEqualTo(3);
                    assertThat(properties.getMaxAttempts()).isEqualTo(7);
                    assertThat(properties.getRetryInitialDelay()).isEqualTo(Duration.ofSeconds(2));
                    assertThat(properties.getRetryMaxDelay()).isEqualTo(Duration.ofMinutes(2));
                    assertThat(properties.getExtractionMaxInputTokens()).isEqualTo(7000);
                    assertThat(properties.getMaxInputTokens()).isEqualTo(10000);
                    assertThat(properties.getResponseReserveTokens()).isEqualTo(1500);
                    assertThat(properties.getSummaryMaxTokens()).isEqualTo(600);
                    assertThat(properties.getProfileMaxTokens()).isEqualTo(400);
                    assertThat(properties.getProfileMaxFacts()).isEqualTo(20);
                    assertThat(properties.getSearchMinScore()).isEqualTo(0.75);
                    assertThat(properties.getSearchQueryMaxChars()).isEqualTo(1500);
                    assertThat(properties.getToolResultMaxTokens()).isEqualTo(800);
                    assertThat(properties.getToolTotalMaxTokens()).isEqualTo(2400);
                    assertThat(properties.getMaxToolRounds()).isEqualTo(4);
                    assertThat(properties.getGcGracePeriod()).isEqualTo(Duration.ofDays(2));
                    assertThat(properties.getTokenEstimatorBean()).isEqualTo("testTokenEstimator");
                });
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource(
            delimiter = '|',
            value = {
                "idle-timeout=0ms|idle-timeout must be between one millisecond and one year",
                "idle-timeout=-1ms|idle-timeout must be between one millisecond and one year",
                "idle-timeout=PT0.000999999S|idle-timeout must be between one millisecond and one year",
                "scan-interval=P366D|scan-interval must be between one millisecond and one year",
                "workers-per-node=0|workers-per-node must be positive",
                "max-attempts=-1|max-attempts must be positive",
                "search-min-score=NaN|search-min-score must be between zero and one",
                "search-min-score=Infinity|search-min-score must be between zero and one",
                "search-min-score=-Infinity|search-min-score must be between zero and one",
                "search-min-score=-0.01|search-min-score must be between zero and one",
                "search-min-score=1.01|search-min-score must be between zero and one",
                "lease-renew-interval=60s|lease-renew-interval must be shorter than both leases",
                "extraction-claim-lease=15s|lease-renew-interval must be shorter than both leases",
                "gc-grace-period=10m|gc-grace-period must exceed both absolute lifetimes",
                "retry-initial-delay=31m|retry-initial-delay exceeds retry-max-delay",
                "max-input-tokens=1792|fixed memory must leave room for conversation input",
                "extraction-max-input-tokens=1792|extraction budget must leave room for source evidence",
                "tool-result-max-tokens=4097|tool-result-max-tokens exceeds tool-total-max-tokens"
            })
    void invalidBoundSettingsFailContextInitialization(String property, String message) {
        contextRunner.withPropertyValues(PREFIX + property).run(context -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure())
                    .hasRootCauseInstanceOf(IllegalArgumentException.class)
                    .hasRootCauseMessage(PREFIX + message);
        });
    }

    @Test
    void malformedDurationFailsBinding() {
        contextRunner.withPropertyValues(PREFIX + "idle-timeout=not-a-duration").run(context -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).hasRootCauseInstanceOf(IllegalArgumentException.class);
        });
    }

    @Test
    void acceptsExplicitEmptyEstimatorName() {
        contextRunner.withPropertyValues(PREFIX + "token-estimator-bean=").run(context -> {
            assertThat(context).hasNotFailed().hasSingleBean(LongTermMemoryProperties.class);
            assertThat(context.getBean(LongTermMemoryProperties.class).getTokenEstimatorBean())
                    .isEmpty();
        });
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties
    static class BindingConfiguration {}
}
