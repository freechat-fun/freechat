package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;

import fun.freechat.service.skills.SkillManagementModel;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;

class SkillModelConfigurationTest {
    private static void loadApplicationDefaults(AnnotationConfigApplicationContext context) throws IOException {
        for (var source :
                new YamlPropertySourceLoader().load("application-defaults", new ClassPathResource("application.yml"))) {
            context.getEnvironment().getPropertySources().addLast(source);
        }
        ConfigurationPropertySources.attach(context.getEnvironment());
    }

    @org.junit.jupiter.api.Test
    void allApplicationPropertyNamesUseLowerCamelCase() throws IOException {
        for (var source :
                new YamlPropertySourceLoader().load("application", new ClassPathResource("application.yml"))) {
            var properties = (org.springframework.core.env.EnumerablePropertySource<?>) source;
            for (String name : properties.getPropertyNames()) {
                for (String part : name.replaceAll("\\[\\d+\\]", "").split("\\.")) {
                    assertTrue(part.matches("[a-z][a-zA-Z0-9]*"), name);
                }
            }
        }
    }

    @org.junit.jupiter.api.Test
    void lowerCamelCasePropertiesOverrideDefaultsAndEnvironment() throws IOException {
        try (var context = new AnnotationConfigApplicationContext()) {
            var sources = context.getEnvironment().getPropertySources();
            sources.remove(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME);
            sources.replace(
                    StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                    new SystemEnvironmentPropertySource(
                            StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                            Map.of("SKILLS_REVIEW_MODELID", "[ollama]environment-model")));
            sources.addFirst(new MapPropertySource(
                    "explicit-settings",
                    Map.of(
                            "skills.review.modelId", "[ollama]camel-model",
                            "skills.review.baseUrl", "http://127.0.0.1:11434",
                            "skills.sources.allowedHosts", "clawhub.ai",
                            "skills.sources.githubToken", "test-only-token")));
            loadApplicationDefaults(context);
            context.registerBean(fun.freechat.service.skills.SkillSourceAnalyzer.class, () -> (input, candidates) -> 0);
            context.register(SkillManagementModel.class, fun.freechat.service.tools.network.SkillSourceFetcher.class);
            context.refresh();
            assertEquals("[ollama]camel-model", context.getEnvironment().getProperty("skills.review.modelId"));
            assertNotNull(ReflectionTestUtils.getField(context.getBean(SkillManagementModel.class), "model"));
            var fetcher = context.getBean(fun.freechat.service.tools.network.SkillSourceFetcher.class);
            assertEquals(java.util.List.of("clawhub.ai"), fetcher.allowedHosts());
            assertEquals("test-only-token", ReflectionTestUtils.getField(fetcher, "githubToken"));
        }
    }

    @org.junit.jupiter.api.Test
    void lowerCamelCaseMultipartLimitsBindToSpringProperties() throws IOException {
        try (var context = new AnnotationConfigApplicationContext()) {
            context.getEnvironment()
                    .getPropertySources()
                    .remove(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME);
            context.getEnvironment()
                    .getPropertySources()
                    .remove(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME);
            loadApplicationDefaults(context);
            var multipart = org.springframework.boot.context.properties.bind.Binder.get(context.getEnvironment())
                    .bind(
                            "spring.servlet.multipart",
                            org.springframework.boot.servlet.autoconfigure.MultipartProperties.class)
                    .get();
            assertEquals(10L * 1024 * 1024, multipart.getMaxFileSize().toBytes());
            assertEquals(20L * 1024 * 1024, multipart.getMaxRequestSize().toBytes());
        }
    }

    @org.junit.jupiter.api.Test
    void defaultsRemainUnconfiguredWithoutEnvironmentVariables() throws IOException {
        try (var context = new AnnotationConfigApplicationContext()) {
            var sources = context.getEnvironment().getPropertySources();
            sources.remove(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME);
            sources.remove(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME);
            loadApplicationDefaults(context);
            context.registerBean(fun.freechat.service.skills.SkillSourceAnalyzer.class, () -> (input, candidates) -> 0);
            context.register(SkillManagementModel.class, fun.freechat.service.tools.network.SkillSourceFetcher.class);
            context.refresh();
            assertNull(ReflectionTestUtils.getField(context.getBean(SkillManagementModel.class), "model"));
            var fetcher = context.getBean(fun.freechat.service.tools.network.SkillSourceFetcher.class);
            assertNull(ReflectionTestUtils.getField(fetcher, "githubToken"));
            assertEquals(java.util.List.of("clawhub.ai", "github.com"), fetcher.allowedHosts());
        }
    }

    @org.junit.jupiter.api.Test
    void githubTokenEnvironmentVariableOverridesBlankDefault() throws IOException {
        try (var context = new AnnotationConfigApplicationContext()) {
            var environment = context.getEnvironment();
            environment.getPropertySources().remove(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME);
            environment
                    .getPropertySources()
                    .replace(
                            StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                            new SystemEnvironmentPropertySource(
                                    StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                                    Map.of(
                                            "SKILLS_SOURCES_GITHUBTOKEN",
                                            "test-only-token",
                                            "SKILLS_SOURCES_ALLOWEDHOSTS",
                                            "github.com")));
            loadApplicationDefaults(context);
            context.registerBean(fun.freechat.service.skills.SkillSourceAnalyzer.class, () -> (input, candidates) -> 0);
            context.register(fun.freechat.service.tools.network.SkillSourceFetcher.class);
            context.refresh();
            assertEquals(
                    "test-only-token",
                    ReflectionTestUtils.getField(
                            context.getBean(fun.freechat.service.tools.network.SkillSourceFetcher.class),
                            "githubToken"));
        }
    }

    @org.junit.jupiter.api.Test
    void unconfiguredModelRejectsBothImportPathsBeforeQuotaStorageOrDownloads() {
        var settings = org.mockito.Mockito.withSettings().mockMaker(org.mockito.MockMakers.SUBCLASS);
        var skills = org.mockito.Mockito.mock(fun.freechat.mapper.SkillInfoMapper.class, settings);
        var drafts = org.mockito.Mockito.mock(fun.freechat.mapper.SkillDraftMapper.class, settings);
        var users = org.mockito.Mockito.mock(fun.freechat.mapper.UserMapper.class, settings);
        var coordination = org.mockito.Mockito.mock(fun.freechat.mapper.SkillCoordinationMapper.class, settings);
        var files = org.mockito.Mockito.mock(fun.freechat.service.common.FileStore.class, settings);
        var transactions =
                org.mockito.Mockito.mock(org.springframework.transaction.PlatformTransactionManager.class, settings);
        var reviewer = new fun.freechat.service.skills.ModelSkillReviewer("", "", "");
        try {
            var service = new fun.freechat.service.skills.DatabaseSkillService(
                    skills,
                    drafts,
                    users,
                    coordination,
                    files,
                    new fun.freechat.service.skills.SkillBundleValidator(),
                    reviewer,
                    transactions);
            assertEquals(
                    fun.freechat.service.skills.SkillException.Code.SKILL_MODEL_NOT_CONFIGURED,
                    assertThrows(
                                    fun.freechat.service.skills.SkillException.class,
                                    () -> service.prepare("owner", null, null, Map.of("SKILL.md", "text")))
                            .code());
            assertEquals(
                    fun.freechat.service.skills.SkillException.Code.SKILL_MODEL_NOT_CONFIGURED,
                    assertThrows(
                                    fun.freechat.service.skills.SkillException.class,
                                    () -> service.prepareSource("owner", () -> {
                                        throw new AssertionError("No download or analysis before configuration");
                                    }))
                            .code());
            org.mockito.Mockito.verifyNoInteractions(skills, drafts, users, coordination, files, transactions);
        } finally {
            reviewer.destroy();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"open_ai", "dash_scope", "ollama"})
    void documentedEnvironmentVariablesOverrideEmptyApplicationDefaults(String provider) throws IOException {
        try (var context = new AnnotationConfigApplicationContext()) {
            var environment = context.getEnvironment();
            environment.getPropertySources().remove(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME);
            environment.getPropertySources().remove(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME);
            environment
                    .getPropertySources()
                    .addFirst(new SystemEnvironmentPropertySource(
                            StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                            Map.of(
                                    "SKILLS_REVIEW_MODELID",
                                    "[" + provider + "]fixture",
                                    "SKILLS_REVIEW_BASEURL",
                                    provider.equals("ollama") ? "http://127.0.0.1:11434" : "https://example.invalid/v1",
                                    "SKILLS_REVIEW_APIKEY",
                                    provider.equals("ollama") ? "" : "test-only-key")));
            loadApplicationDefaults(context);
            context.register(SkillManagementModel.class);
            context.refresh();
            assertNotNull(ReflectionTestUtils.getField(context.getBean(SkillManagementModel.class), "model"));
            assertEquals("[" + provider + "]fixture", environment.getProperty("skills.review.modelId"));
            if (provider.equals("ollama")) assertEquals("", environment.getProperty("skills.review.apiKey"));
        }
    }
}
