package fun.freechat.config;

import static org.assertj.core.api.Assertions.assertThat;

import fun.freechat.service.chat.memory.LongTermMemoryProperties;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.core.io.ClassPathResource;

class LongTermMemoryConfigTest {
    private static final String PREFIX = "chat.memory.longTerm.";

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
            assertThat(properties.getMaxForegroundBatches()).isEqualTo(20);
            assertThat(properties.getGcGracePeriod()).isEqualTo(Duration.ofHours(24));
            assertThat(properties.getTokenEstimatorBean()).isEmpty();
        });
    }

    @Test
    void bindsAllConfiguredValuesFromApplicationYaml() throws IOException {
        var yamlSources =
                new YamlPropertySourceLoader().load("applicationYaml", new ClassPathResource("application.yml"));
        contextRunner
                .withInitializer(context -> yamlSources.forEach(
                        source -> context.getEnvironment().getPropertySources().addLast(source)))
                .run(context -> {
                    assertThat(context).hasNotFailed().hasSingleBean(LongTermMemoryProperties.class);
                    LongTermMemoryProperties bound = context.getBean(LongTermMemoryProperties.class);
                    assertThat(bound.getMaxForegroundBatches()).isEqualTo(20);
                    BeanWrapper properties = new BeanWrapperImpl(bound);
                    for (PropertyDescriptor setting : properties.getPropertyDescriptors()) {
                        if (setting.getWriteMethod() == null) {
                            continue;
                        }
                        String propertyName = PREFIX + setting.getName();
                        // Check raw YAML keys so matching Java defaults cannot hide a missing camel-case setting.
                        Object configuredValue = yamlSources.stream()
                                .map(source -> source.getProperty(propertyName))
                                .filter(Objects::nonNull)
                                .findFirst()
                                .orElse(null);
                        assertThat(configuredValue).as(propertyName).isNotNull();
                        assertThat(properties.getPropertyValue(setting.getName()))
                                .as(propertyName)
                                .isEqualTo(ApplicationConversionService.getSharedInstance()
                                        .convert(configuredValue, setting.getPropertyType()));
                    }
                });
    }

    @Test
    void standardEnvironmentVariablesOverrideApplicationYamlDefaults() throws IOException {
        var yamlSources =
                new YamlPropertySourceLoader().load("applicationYaml", new ClassPathResource("application.yml"));
        contextRunner
                .withInitializer(context -> {
                    var sources = context.getEnvironment().getPropertySources();
                    yamlSources.forEach(sources::addLast);
                    sources.addFirst(new SystemEnvironmentPropertySource(
                            StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                            Map.of(
                                    "CHAT_MEMORY_LONGTERM_IDLETIMEOUT", "PT2H",
                                    "CHAT_MEMORY_LONGTERM_MAXFOREGROUNDBATCHES", "7")));
                })
                .run(context -> {
                    assertThat(context).hasNotFailed().hasSingleBean(LongTermMemoryProperties.class);
                    LongTermMemoryProperties properties = context.getBean(LongTermMemoryProperties.class);
                    assertThat(properties.getIdleTimeout()).isEqualTo(Duration.ofHours(2));
                    assertThat(properties.getScanInterval()).isEqualTo(Duration.ofSeconds(30));
                    assertThat(properties.getMaxForegroundBatches()).isEqualTo(7);
                });
    }

    @Test
    void bindsCustomDurationsAndAllOtherSettingsWithoutApplicationServices() {
        contextRunner
                .withPropertyValues(
                        PREFIX + "idleTimeout=PT2H",
                        PREFIX + "scanInterval=750ms",
                        PREFIX + "workersPerNode=3",
                        PREFIX + "dispatchBatchSize=25",
                        PREFIX + "turnLease=40s",
                        PREFIX + "leaseRenewInterval=5s",
                        PREFIX + "turnMaxDuration=20m",
                        PREFIX + "jobMaxDuration=PT15M",
                        PREFIX + "extractionClaimLease=3m",
                        PREFIX + "extractionTimeout=PT25S",
                        PREFIX + "foregroundTimeout=45s",
                        PREFIX + "maxForegroundBatches=3",
                        PREFIX + "maxAttempts=7",
                        PREFIX + "retryInitialDelay=2s",
                        PREFIX + "retryMaxDelay=PT2M",
                        PREFIX + "extractionMaxInputTokens=7000",
                        PREFIX + "maxInputTokens=10000",
                        PREFIX + "responseReserveTokens=1500",
                        PREFIX + "summaryMaxTokens=600",
                        PREFIX + "profileMaxTokens=400",
                        PREFIX + "profileMaxFacts=20",
                        PREFIX + "searchMinScore=0.75",
                        PREFIX + "searchQueryMaxChars=1500",
                        PREFIX + "toolResultMaxTokens=800",
                        PREFIX + "toolTotalMaxTokens=2400",
                        PREFIX + "maxToolRounds=4",
                        PREFIX + "gcGracePeriod=2d",
                        PREFIX + "tokenEstimatorBean=testTokenEstimator")
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
                "idleTimeout=0ms|idleTimeout must be between one millisecond and one year",
                "idleTimeout=-1ms|idleTimeout must be between one millisecond and one year",
                "idleTimeout=PT0.000999999S|idleTimeout must be between one millisecond and one year",
                "scanInterval=P366D|scanInterval must be between one millisecond and one year",
                "workersPerNode=0|workersPerNode must be positive",
                "maxAttempts=-1|maxAttempts must be positive",
                "searchMinScore=NaN|searchMinScore must be between zero and one",
                "searchMinScore=Infinity|searchMinScore must be between zero and one",
                "searchMinScore=-Infinity|searchMinScore must be between zero and one",
                "searchMinScore=-0.01|searchMinScore must be between zero and one",
                "searchMinScore=1.01|searchMinScore must be between zero and one",
                "leaseRenewInterval=60s|leaseRenewInterval must be shorter than both leases",
                "extractionClaimLease=15s|leaseRenewInterval must be shorter than both leases",
                "gcGracePeriod=10m|gcGracePeriod must exceed both absolute lifetimes",
                "retryInitialDelay=31m|retryInitialDelay exceeds retryMaxDelay",
                "maxInputTokens=1792|fixed memory must leave room for conversation input",
                "extractionMaxInputTokens=1792|extraction budget must leave room for source evidence",
                "toolResultMaxTokens=4097|toolResultMaxTokens exceeds toolTotalMaxTokens"
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
        contextRunner.withPropertyValues(PREFIX + "idleTimeout=not-a-duration").run(context -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).hasRootCauseInstanceOf(IllegalArgumentException.class);
        });
    }

    @Test
    void acceptsExplicitEmptyEstimatorName() {
        contextRunner.withPropertyValues(PREFIX + "tokenEstimatorBean=").run(context -> {
            assertThat(context).hasNotFailed().hasSingleBean(LongTermMemoryProperties.class);
            assertThat(context.getBean(LongTermMemoryProperties.class).getTokenEstimatorBean())
                    .isEmpty();
        });
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties
    static class BindingConfiguration {}
}
