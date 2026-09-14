package fun.freechat.service.chat.memory;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.time.Duration;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class LongTermMemoryPropertiesTest {
    private static final String PREFIX = "chat.memory.long-term.";
    private static final int FIXED_MEMORY_TOKENS = 768 + 2 * 512;

    @Test
    void defaultsAreValid() {
        LongTermMemoryProperties properties = new LongTermMemoryProperties();

        assertDoesNotThrow(properties::afterPropertiesSet);
        assertEquals(Duration.parse("PT1H"), properties.getIdleTimeout());
        assertEquals(0.3, properties.getSearchMinScore());
        assertEquals("", properties.getTokenEstimatorBean());
    }

    @ParameterizedTest(name = "{0} default")
    @MethodSource("durationSettings")
    void durationDefaults(DurationSetting setting) {
        assertEquals(setting.defaultValue(), setting.getter().apply(new LongTermMemoryProperties()));
    }

    @ParameterizedTest(name = "{0} default")
    @MethodSource("numericSettings")
    void numericDefaults(NumericSetting setting) {
        assertEquals(
                setting.defaultValue(),
                setting.getter().apply(new LongTermMemoryProperties()).intValue());
    }

    @ParameterizedTest(name = "{0} rejects {1}")
    @MethodSource("invalidDurations")
    void rejectsNullNonpositiveSubMillisecondAndOverlargeDurations(DurationSetting setting, Duration value) {
        LongTermMemoryProperties properties = new LongTermMemoryProperties();
        setting.setter().accept(properties, value);

        assertInvalid(properties, setting.name() + " must be between one millisecond and one year");
    }

    @ParameterizedTest
    @MethodSource("validIdleTimeouts")
    void acceptsCustomIdleTimeoutIncludingDurationBoundaries(Duration idleTimeout) {
        LongTermMemoryProperties properties = new LongTermMemoryProperties();
        properties.setIdleTimeout(idleTimeout);

        assertDoesNotThrow(properties::afterPropertiesSet);
        assertEquals(idleTimeout, properties.getIdleTimeout());
    }

    @ParameterizedTest(name = "{0} rejects {1}")
    @MethodSource("nonpositiveNumbers")
    void rejectsNonpositiveNumericSettings(NumericSetting setting, int value) {
        LongTermMemoryProperties properties = new LongTermMemoryProperties();
        setting.setter().accept(properties, value);

        assertInvalid(properties, setting.name() + " must be positive");
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.0001, 1.0001, Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY})
    void rejectsNonfiniteOrOutOfRangeSearchScores(double score) {
        LongTermMemoryProperties properties = new LongTermMemoryProperties();
        properties.setSearchMinScore(score);

        assertInvalid(properties, "search-min-score must be between zero and one");
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 0.5, 1.0})
    void acceptsFiniteSearchScoresIncludingEndpoints(double score) {
        LongTermMemoryProperties properties = new LongTermMemoryProperties();
        properties.setSearchMinScore(score);

        assertDoesNotThrow(properties::afterPropertiesSet);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1})
    void renewalMustBeStrictlyShorterThanTurnLease(long extraNanos) {
        LongTermMemoryProperties properties = new LongTermMemoryProperties();
        properties.setLeaseRenewInterval(properties.getTurnLease().plusNanos(extraNanos));

        assertInvalid(properties, "lease-renew-interval must be shorter than both leases");
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1})
    void renewalMustBeStrictlyShorterThanExtractionClaimLease(long extraNanos) {
        LongTermMemoryProperties properties = new LongTermMemoryProperties();
        properties.setExtractionClaimLease(properties.getLeaseRenewInterval().minusNanos(extraNanos));

        assertInvalid(properties, "lease-renew-interval must be shorter than both leases");
    }

    @Test
    void renewalMayBeOneNanosecondShorterThanBothLeases() {
        LongTermMemoryProperties properties = new LongTermMemoryProperties();
        properties.setExtractionClaimLease(properties.getTurnLease());
        properties.setLeaseRenewInterval(properties.getTurnLease().minusNanos(1));

        assertDoesNotThrow(properties::afterPropertiesSet);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidTimeoutRelationships")
    void rejectsTimeoutsBeyondTheirEnclosingLifetime(String message, Consumer<LongTermMemoryProperties> configure) {
        LongTermMemoryProperties properties = new LongTermMemoryProperties();
        configure.accept(properties);

        assertInvalid(properties, message);
    }

    @Test
    void acceptsTimeoutsEqualToTheirEnclosingLifetime() {
        LongTermMemoryProperties properties = new LongTermMemoryProperties();
        properties.setTurnLease(properties.getTurnMaxDuration());
        properties.setForegroundTimeout(properties.getTurnMaxDuration());
        properties.setExtractionTimeout(properties.getJobMaxDuration());

        assertDoesNotThrow(properties::afterPropertiesSet);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1})
    void gcGraceMustExceedAbsoluteTurnDurationEvenWhenJobDurationIsShorter(long missingNanos) {
        LongTermMemoryProperties properties = new LongTermMemoryProperties();
        properties.setTurnMaxDuration(Duration.ofMinutes(11));
        properties.setGcGracePeriod(properties.getTurnMaxDuration().minusNanos(missingNanos));

        assertInvalid(properties, "gc-grace-period must exceed both absolute lifetimes");
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1})
    void gcGraceMustExceedAbsoluteJobDurationEvenWhenTurnDurationIsShorter(long missingNanos) {
        LongTermMemoryProperties properties = new LongTermMemoryProperties();
        properties.setJobMaxDuration(Duration.ofMinutes(11));
        properties.setGcGracePeriod(properties.getJobMaxDuration().minusNanos(missingNanos));

        assertInvalid(properties, "gc-grace-period must exceed both absolute lifetimes");
    }

    @Test
    void gcGraceMayExceedBothAbsoluteDurationsByOneNanosecond() {
        LongTermMemoryProperties properties = new LongTermMemoryProperties();
        properties.setGcGracePeriod(properties.getTurnMaxDuration().plusNanos(1));

        assertDoesNotThrow(properties::afterPropertiesSet);
    }

    @Test
    void rejectsRetryInitialDelayGreaterThanMaximum() {
        LongTermMemoryProperties properties = new LongTermMemoryProperties();
        properties.setRetryInitialDelay(properties.getRetryMaxDelay().plusNanos(1));

        assertInvalid(properties, "retry-initial-delay exceeds retry-max-delay");
    }

    @Test
    void acceptsEqualRetryDelays() {
        LongTermMemoryProperties properties = new LongTermMemoryProperties();
        properties.setRetryInitialDelay(properties.getRetryMaxDelay());

        assertDoesNotThrow(properties::afterPropertiesSet);
    }

    @ParameterizedTest
    @ValueSource(ints = {FIXED_MEMORY_TOKENS - 1, FIXED_MEMORY_TOKENS})
    void fixedSummaryAndTwoProfilesMustLeaveConversationInputRoom(int budget) {
        LongTermMemoryProperties properties = new LongTermMemoryProperties();
        properties.setMaxInputTokens(budget);

        assertInvalid(properties, "fixed memory must leave room for conversation input");
    }

    @ParameterizedTest
    @ValueSource(ints = {FIXED_MEMORY_TOKENS - 1, FIXED_MEMORY_TOKENS})
    void fixedSummaryAndTwoProfilesMustLeaveExtractionEvidenceRoom(int budget) {
        LongTermMemoryProperties properties = new LongTermMemoryProperties();
        properties.setExtractionMaxInputTokens(budget);

        assertInvalid(properties, "extraction budget must leave room for source evidence");
    }

    @Test
    void fixedMemoryMayLeaveOneTokenInBothInputBudgets() {
        LongTermMemoryProperties properties = new LongTermMemoryProperties();
        properties.setMaxInputTokens(FIXED_MEMORY_TOKENS + 1);
        properties.setExtractionMaxInputTokens(FIXED_MEMORY_TOKENS + 1);

        assertDoesNotThrow(properties::afterPropertiesSet);
    }

    @ParameterizedTest
    @ValueSource(ints = {1_000_000_000, 1_500_000_000, Integer.MAX_VALUE})
    void fixedMemoryArithmeticCannotOverflowIntoAnAcceptedBudget(int profileTokens) {
        LongTermMemoryProperties properties = new LongTermMemoryProperties();
        properties.setProfileMaxTokens(profileTokens);
        properties.setSummaryMaxTokens(1_000_000_000);
        properties.setMaxInputTokens(Integer.MAX_VALUE);
        properties.setExtractionMaxInputTokens(Integer.MAX_VALUE);

        assertInvalid(properties, "fixed memory must leave room for conversation input");
    }

    @Test
    void toolPerCallBudgetCannotExceedInvocationBudget() {
        LongTermMemoryProperties properties = new LongTermMemoryProperties();
        properties.setToolResultMaxTokens(properties.getToolTotalMaxTokens() + 1);

        assertInvalid(properties, "tool-result-max-tokens exceeds tool-total-max-tokens");
    }

    @Test
    void toolPerCallBudgetMayEqualInvocationBudget() {
        LongTermMemoryProperties properties = new LongTermMemoryProperties();
        properties.setToolResultMaxTokens(properties.getToolTotalMaxTokens());

        assertDoesNotThrow(properties::afterPropertiesSet);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "testTokenEstimator")
    void estimatorNameMayBeEmptyOrNamedButNotNull(String beanName) {
        LongTermMemoryProperties properties = new LongTermMemoryProperties();
        properties.setTokenEstimatorBean(beanName);

        if (beanName == null) {
            assertInvalid(properties, "token-estimator-bean must not be null");
        } else {
            assertDoesNotThrow(properties::afterPropertiesSet);
            assertEquals(beanName, properties.getTokenEstimatorBean());
        }
    }

    private static void assertInvalid(LongTermMemoryProperties properties, String message) {
        IllegalArgumentException failure = assertThrows(IllegalArgumentException.class, properties::afterPropertiesSet);
        assertEquals(PREFIX + message, failure.getMessage());
    }

    private static Stream<DurationSetting> durationSettings() {
        return Stream.of(
                new DurationSetting(
                        "idle-timeout",
                        Duration.parse("PT1H"),
                        LongTermMemoryProperties::getIdleTimeout,
                        LongTermMemoryProperties::setIdleTimeout),
                new DurationSetting(
                        "scan-interval",
                        Duration.ofSeconds(30),
                        LongTermMemoryProperties::getScanInterval,
                        LongTermMemoryProperties::setScanInterval),
                new DurationSetting(
                        "turn-lease",
                        Duration.ofSeconds(60),
                        LongTermMemoryProperties::getTurnLease,
                        LongTermMemoryProperties::setTurnLease),
                new DurationSetting(
                        "lease-renew-interval",
                        Duration.ofSeconds(15),
                        LongTermMemoryProperties::getLeaseRenewInterval,
                        LongTermMemoryProperties::setLeaseRenewInterval),
                new DurationSetting(
                        "turn-max-duration",
                        Duration.ofMinutes(10),
                        LongTermMemoryProperties::getTurnMaxDuration,
                        LongTermMemoryProperties::setTurnMaxDuration),
                new DurationSetting(
                        "job-max-duration",
                        Duration.ofMinutes(10),
                        LongTermMemoryProperties::getJobMaxDuration,
                        LongTermMemoryProperties::setJobMaxDuration),
                new DurationSetting(
                        "extraction-claim-lease",
                        Duration.ofMinutes(5),
                        LongTermMemoryProperties::getExtractionClaimLease,
                        LongTermMemoryProperties::setExtractionClaimLease),
                new DurationSetting(
                        "extraction-timeout",
                        Duration.ofSeconds(60),
                        LongTermMemoryProperties::getExtractionTimeout,
                        LongTermMemoryProperties::setExtractionTimeout),
                new DurationSetting(
                        "foreground-timeout",
                        Duration.ofSeconds(90),
                        LongTermMemoryProperties::getForegroundTimeout,
                        LongTermMemoryProperties::setForegroundTimeout),
                new DurationSetting(
                        "retry-initial-delay",
                        Duration.ofSeconds(30),
                        LongTermMemoryProperties::getRetryInitialDelay,
                        LongTermMemoryProperties::setRetryInitialDelay),
                new DurationSetting(
                        "retry-max-delay",
                        Duration.ofMinutes(30),
                        LongTermMemoryProperties::getRetryMaxDelay,
                        LongTermMemoryProperties::setRetryMaxDelay),
                new DurationSetting(
                        "gc-grace-period",
                        Duration.ofHours(24),
                        LongTermMemoryProperties::getGcGracePeriod,
                        LongTermMemoryProperties::setGcGracePeriod));
    }

    private static Stream<Arguments> invalidDurations() {
        return durationSettings()
                .flatMap(setting -> Stream.of(
                                (Duration) null,
                                Duration.ZERO,
                                Duration.ofNanos(-1),
                                Duration.ofNanos(999_999),
                                Duration.ofDays(365).plusNanos(1),
                                Duration.ofSeconds(Long.MAX_VALUE))
                        .map(value -> arguments(setting, value)));
    }

    private static Stream<Duration> validIdleTimeouts() {
        return Stream.of(
                Duration.ofMillis(1), Duration.ofNanos(1_000_001), Duration.ofMinutes(7), Duration.ofDays(365));
    }

    private static Stream<NumericSetting> numericSettings() {
        return Stream.of(
                new NumericSetting(
                        "workers-per-node",
                        2,
                        LongTermMemoryProperties::getWorkersPerNode,
                        LongTermMemoryProperties::setWorkersPerNode),
                new NumericSetting(
                        "dispatch-batch-size",
                        100,
                        LongTermMemoryProperties::getDispatchBatchSize,
                        LongTermMemoryProperties::setDispatchBatchSize),
                new NumericSetting(
                        "max-foreground-batches",
                        2,
                        LongTermMemoryProperties::getMaxForegroundBatches,
                        LongTermMemoryProperties::setMaxForegroundBatches),
                new NumericSetting(
                        "max-attempts",
                        5,
                        LongTermMemoryProperties::getMaxAttempts,
                        LongTermMemoryProperties::setMaxAttempts),
                new NumericSetting(
                        "extraction-max-input-tokens",
                        6000,
                        LongTermMemoryProperties::getExtractionMaxInputTokens,
                        LongTermMemoryProperties::setExtractionMaxInputTokens),
                new NumericSetting(
                        "max-input-tokens",
                        8192,
                        LongTermMemoryProperties::getMaxInputTokens,
                        LongTermMemoryProperties::setMaxInputTokens),
                new NumericSetting(
                        "response-reserve-tokens",
                        2048,
                        LongTermMemoryProperties::getResponseReserveTokens,
                        LongTermMemoryProperties::setResponseReserveTokens),
                new NumericSetting(
                        "summary-max-tokens",
                        768,
                        LongTermMemoryProperties::getSummaryMaxTokens,
                        LongTermMemoryProperties::setSummaryMaxTokens),
                new NumericSetting(
                        "profile-max-tokens",
                        512,
                        LongTermMemoryProperties::getProfileMaxTokens,
                        LongTermMemoryProperties::setProfileMaxTokens),
                new NumericSetting(
                        "profile-max-facts",
                        32,
                        LongTermMemoryProperties::getProfileMaxFacts,
                        LongTermMemoryProperties::setProfileMaxFacts),
                new NumericSetting(
                        "search-query-max-chars",
                        2000,
                        LongTermMemoryProperties::getSearchQueryMaxChars,
                        LongTermMemoryProperties::setSearchQueryMaxChars),
                new NumericSetting(
                        "tool-result-max-tokens",
                        1024,
                        LongTermMemoryProperties::getToolResultMaxTokens,
                        LongTermMemoryProperties::setToolResultMaxTokens),
                new NumericSetting(
                        "tool-total-max-tokens",
                        4096,
                        LongTermMemoryProperties::getToolTotalMaxTokens,
                        LongTermMemoryProperties::setToolTotalMaxTokens),
                new NumericSetting(
                        "max-tool-rounds",
                        10,
                        LongTermMemoryProperties::getMaxToolRounds,
                        LongTermMemoryProperties::setMaxToolRounds));
    }

    private static Stream<Arguments> nonpositiveNumbers() {
        return numericSettings()
                .flatMap(setting -> Stream.of(0, -1, Integer.MIN_VALUE).map(value -> arguments(setting, value)));
    }

    private static Stream<Arguments> invalidTimeoutRelationships() {
        return Stream.of(
                arguments("turn-lease exceeds turn-max-duration", (Consumer<LongTermMemoryProperties>) properties ->
                        properties.setTurnLease(properties.getTurnMaxDuration().plusNanos(1))),
                arguments("extraction-timeout exceeds job-max-duration", (Consumer<LongTermMemoryProperties>)
                        properties -> {
                            properties.setJobMaxDuration(
                                    properties.getExtractionTimeout().minusNanos(1));
                        }),
                arguments("foreground-timeout exceeds turn-max-duration", (Consumer<LongTermMemoryProperties>)
                        properties -> properties.setForegroundTimeout(
                                properties.getTurnMaxDuration().plusNanos(1))),
                arguments("extraction-timeout exceeds foreground-timeout", (Consumer<LongTermMemoryProperties>)
                        properties -> properties.setExtractionTimeout(
                                properties.getForegroundTimeout().plusNanos(1))));
    }

    private record DurationSetting(
            String name,
            Duration defaultValue,
            Function<LongTermMemoryProperties, Duration> getter,
            BiConsumer<LongTermMemoryProperties, Duration> setter) {
        @Override
        public String toString() {
            return name;
        }
    }

    private record NumericSetting(
            String name,
            int defaultValue,
            Function<LongTermMemoryProperties, Integer> getter,
            BiConsumer<LongTermMemoryProperties, Integer> setter) {
        @Override
        public String toString() {
            return name;
        }
    }
}
