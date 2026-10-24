package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;

import fun.freechat.service.skills.SkillManagementModel;
import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

class SkillModelConfigurationTest {
    @org.junit.jupiter.api.Test
    void githubTokenEnvironmentVariableOverridesBlankDefault() {
        try (var context = new AnnotationConfigApplicationContext()) {
            var environment = context.getEnvironment();
            environment.getPropertySources().remove(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME);
            environment
                    .getPropertySources()
                    .replace(
                            StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                            new SystemEnvironmentPropertySource(
                                    StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                                    Map.of("SKILLS_SOURCES_GITHUB_TOKEN", "test-only-token")));
            environment
                    .getPropertySources()
                    .addLast(new MapPropertySource("application-defaults", Map.of("skills.sources.github-token", "")));
            ConfigurationPropertySources.attach(environment);
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
    void documentedEnvironmentVariablesOverrideEmptyApplicationDefaults(String provider) {
        try (var context = new AnnotationConfigApplicationContext()) {
            var environment = context.getEnvironment();
            environment.getPropertySources().remove(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME);
            environment.getPropertySources().remove(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME);
            environment
                    .getPropertySources()
                    .addFirst(new SystemEnvironmentPropertySource(
                            StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                            Map.of(
                                    "SKILLS_REVIEW_MODEL_ID",
                                    "[" + provider + "]fixture",
                                    "SKILLS_REVIEW_BASE_URL",
                                    provider.equals("ollama") ? "http://127.0.0.1:11434" : "https://example.invalid/v1",
                                    "SKILLS_REVIEW_API_KEY",
                                    provider.equals("ollama") ? "" : "test-only-key")));
            environment
                    .getPropertySources()
                    .addLast(new MapPropertySource(
                            "application-defaults",
                            Map.of(
                                    "skills.review.model-id",
                                    "",
                                    "skills.review.base-url",
                                    "",
                                    "skills.review.api-key",
                                    "")));
            ConfigurationPropertySources.attach(environment);
            context.register(SkillManagementModel.class);
            context.refresh();
            assertNotNull(ReflectionTestUtils.getField(context.getBean(SkillManagementModel.class), "model"));
            assertEquals("[" + provider + "]fixture", environment.getProperty("skills.review.model-id"));
            if (provider.equals("ollama")) assertEquals("", environment.getProperty("skills.review.api-key"));
        }
    }
}
