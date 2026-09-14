package fun.freechat.service.chat.memory;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;

@Getter
@Setter
public class LongTermMemoryProperties implements InitializingBean {
    private Duration idleTimeout = Duration.ofHours(1);
    private Duration scanInterval = Duration.ofSeconds(30);
    private int workersPerNode = 2;
    private int dispatchBatchSize = 100;
    private Duration turnLease = Duration.ofSeconds(60);
    private Duration leaseRenewInterval = Duration.ofSeconds(15);
    private Duration turnMaxDuration = Duration.ofMinutes(10);
    private Duration jobMaxDuration = Duration.ofMinutes(10);
    private Duration extractionClaimLease = Duration.ofMinutes(5);
    private Duration extractionTimeout = Duration.ofSeconds(60);
    private Duration foregroundTimeout = Duration.ofSeconds(90);
    private int maxForegroundBatches = 2;
    private int maxAttempts = 5;
    private Duration retryInitialDelay = Duration.ofSeconds(30);
    private Duration retryMaxDelay = Duration.ofMinutes(30);
    private int extractionMaxInputTokens = 6000;
    private int maxInputTokens = 8192;
    private int responseReserveTokens = 2048;
    private int summaryMaxTokens = 768;
    private int profileMaxTokens = 512;
    private int profileMaxFacts = 32;
    private double searchMinScore = 0.3;
    private int searchQueryMaxChars = 2000;
    private int toolResultMaxTokens = 1024;
    private int toolTotalMaxTokens = 4096;
    private int maxToolRounds = 10;
    private Duration gcGracePeriod = Duration.ofHours(24);
    private String tokenEstimatorBean = "";

    @Override
    public void afterPropertiesSet() {
        positive(idleTimeout, "idle-timeout");
        positive(scanInterval, "scan-interval");
        positive(turnLease, "turn-lease");
        positive(leaseRenewInterval, "lease-renew-interval");
        positive(turnMaxDuration, "turn-max-duration");
        positive(jobMaxDuration, "job-max-duration");
        positive(extractionClaimLease, "extraction-claim-lease");
        positive(extractionTimeout, "extraction-timeout");
        positive(foregroundTimeout, "foreground-timeout");
        positive(retryInitialDelay, "retry-initial-delay");
        positive(retryMaxDelay, "retry-max-delay");
        positive(gcGracePeriod, "gc-grace-period");
        positive(workersPerNode, "workers-per-node");
        positive(dispatchBatchSize, "dispatch-batch-size");
        positive(maxForegroundBatches, "max-foreground-batches");
        positive(maxAttempts, "max-attempts");
        positive(extractionMaxInputTokens, "extraction-max-input-tokens");
        positive(maxInputTokens, "max-input-tokens");
        positive(responseReserveTokens, "response-reserve-tokens");
        positive(summaryMaxTokens, "summary-max-tokens");
        positive(profileMaxTokens, "profile-max-tokens");
        positive(profileMaxFacts, "profile-max-facts");
        positive(searchQueryMaxChars, "search-query-max-chars");
        positive(toolResultMaxTokens, "tool-result-max-tokens");
        positive(toolTotalMaxTokens, "tool-total-max-tokens");
        positive(maxToolRounds, "max-tool-rounds");
        require(
                Double.isFinite(searchMinScore) && searchMinScore >= 0 && searchMinScore <= 1,
                "search-min-score must be between zero and one");
        require(
                leaseRenewInterval.compareTo(turnLease) < 0 && leaseRenewInterval.compareTo(extractionClaimLease) < 0,
                "lease-renew-interval must be shorter than both leases");
        require(turnLease.compareTo(turnMaxDuration) <= 0, "turn-lease exceeds turn-max-duration");
        require(extractionTimeout.compareTo(jobMaxDuration) <= 0, "extraction-timeout exceeds job-max-duration");
        require(foregroundTimeout.compareTo(turnMaxDuration) <= 0, "foreground-timeout exceeds turn-max-duration");
        require(extractionTimeout.compareTo(foregroundTimeout) <= 0, "extraction-timeout exceeds foreground-timeout");
        require(
                gcGracePeriod.compareTo(turnMaxDuration) > 0 && gcGracePeriod.compareTo(jobMaxDuration) > 0,
                "gc-grace-period must exceed both absolute lifetimes");
        require(retryInitialDelay.compareTo(retryMaxDelay) <= 0, "retry-initial-delay exceeds retry-max-delay");
        require(toolResultMaxTokens <= toolTotalMaxTokens, "tool-result-max-tokens exceeds tool-total-max-tokens");
        require(
                (long) summaryMaxTokens + 2L * profileMaxTokens < maxInputTokens,
                "fixed memory must leave room for conversation input");
        require(
                (long) summaryMaxTokens + 2L * profileMaxTokens < extractionMaxInputTokens,
                "extraction budget must leave room for source evidence");
        require(tokenEstimatorBean != null, "token-estimator-bean must not be null");
    }

    private static void positive(Duration value, String name) {
        require(
                value != null
                        && value.compareTo(Duration.ofMillis(1)) >= 0
                        && value.compareTo(Duration.ofDays(365)) <= 0,
                name + " must be between one millisecond and one year");
    }

    private static void positive(int value, String name) {
        require(value > 0, name + " must be positive");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException("chat.memory.long-term." + message);
        }
    }
}
