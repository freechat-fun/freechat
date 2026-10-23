package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fun.freechat.api.SkillApi;
import fun.freechat.api.SkillApiErrors;
import fun.freechat.api.access.RoleBasedPermissionEvaluator;
import fun.freechat.api.dto.*;
import fun.freechat.model.User;
import fun.freechat.service.skills.*;
import fun.freechat.service.tools.network.SkillSourceFetcher;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockMakers;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

class SkillApiSecurityTest {
    private AnnotationConfigApplicationContext context;
    private SkillApi api;
    private SkillService service;
    private static final String ID = "a".repeat(32);
    private static final SkillBundle BUNDLE =
            new SkillBundle("story", "Story guidance", Map.of("SKILL.md", "text"), "f".repeat(64));

    @BeforeEach
    void setup() {
        context = new AnnotationConfigApplicationContext(Security.class);
        api = context.getBean(SkillApi.class);
        service = context.getBean(SkillService.class);
    }

    @AfterEach
    void close() {
        SecurityContextHolder.clearContext();
        context.close();
    }

    @Test
    void anonymousCallsCannotListOrPrepareOrConfirm() {
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> api.list());
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> api.prepare(new SkillInstallDTO()));
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> api.confirm(ID, new SkillConfirmDTO()));
        verifyNoInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ROLE_USER", "ROLE_ADMIN"})
    void ownershipIsRequiredEvenForAdministratorThroughGenericEndpoints(String role) {
        authenticate("intruder", role);
        when(service.list("intruder")).thenReturn(List.of());
        assertThrows(AccessDeniedException.class, () -> api.get(ID));
        assertThrows(AccessDeniedException.class, () -> api.update(ID, new SkillUpdateDTO()));
        assertThrows(AccessDeniedException.class, () -> api.delete(ID, "revision"));
        verify(service, never()).get(anyString(), anyString());
        verify(service, never()).delete(anyString(), anyString(), anyString());
    }

    @Test
    void serverDerivesOwnerForInstallConfirmAndUpdate() {
        authenticate("owner", "ROLE_USER");
        when(service.list("owner"))
                .thenReturn(List.of(new SkillService.Summary(ID, "revision", "story", "description", Instant.EPOCH)));
        var draft =
                new SkillService.Draft("draft", ID, null, BUNDLE, Instant.now().plusSeconds(60));
        when(service.prepare("owner", null, null, BUNDLE.files())).thenReturn(draft);
        var installed = new SkillService.Installed(ID, "owner", "revision", BUNDLE, Instant.EPOCH);
        when(service.confirm("owner", "draft", BUNDLE.digest())).thenReturn(installed);
        when(service.update("owner", ID, "revision", BUNDLE.files())).thenReturn(installed);
        SkillInstallDTO install = new SkillInstallDTO();
        install.setFiles(BUNDLE.files());
        assertEquals("draft", api.prepare(install).getDraftId());
        SkillConfirmDTO confirm = new SkillConfirmDTO();
        confirm.setDigest(BUNDLE.digest());
        assertEquals(ID, api.confirm("draft", confirm).getSkillId());
        SkillUpdateDTO update = new SkillUpdateDTO();
        update.setExpectedRevision("revision");
        update.setFiles(BUNDLE.files());
        assertEquals(BUNDLE.files(), api.update(ID, update).getFiles());
        verify(service).prepare("owner", null, null, BUNDLE.files());
        verify(service).confirm("owner", "draft", BUNDLE.digest());
        verify(service).update("owner", ID, "revision", BUNDLE.files());
    }

    @Test
    void rejectsAmbiguousAndUnapprovedSourcesWithoutPreparingInstallation() {
        authenticate("owner", "ROLE_USER");
        SkillInstallDTO input = new SkillInstallDTO();
        input.setFiles(BUNDLE.files());
        input.setSourceUrl("https://example.com/SKILL.md");
        assertEquals(
                SkillException.Code.SKILL_INVALID_FORMAT,
                assertThrows(SkillException.class, () -> api.prepare(input)).code());
        input.setFiles(null);
        assertEquals(
                SkillException.Code.SKILL_SOURCE_NOT_ALLOWED,
                assertThrows(SkillException.class, () -> api.prepare(input)).code());
        verifyNoInteractions(service);
    }

    @Test
    void validationErrorsExposeOnlyStableCodes() {
        var response = new SkillApiErrors().handle(new SkillException(SkillException.Code.SKILL_UNSAFE_INSTRUCTIONS));
        assertEquals(422, response.getStatusCode().value());
        assertEquals("no-store", response.getHeaders().getCacheControl());
        assertEquals("SKILL_UNSAFE_INSTRUCTIONS", response.getBody().get("errorCode"));
        assertEquals(
                409,
                new SkillApiErrors()
                        .handle(new SkillException(SkillException.Code.SKILL_VERSION_CONFLICT))
                        .getStatusCode()
                        .value());
    }

    private static void authenticate(String id, String role) {
        SecurityContextHolder.getContext()
                .setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
                        new User().withUserId(id), null, List.of(new SimpleGrantedAuthority(role))));
    }

    @Configuration
    @EnableMethodSecurity
    static class Security {
        @Bean
        SkillService skills() {
            return mock(SkillService.class, withSettings().mockMaker(MockMakers.SUBCLASS));
        }

        @Bean
        SkillSourceFetcher sources() {
            return new SkillSourceFetcher("");
        }

        @Bean
        SkillApi api(SkillService skills, SkillSourceFetcher sources) {
            return new SkillApi(skills, sources);
        }

        @Bean
        RoleBasedPermissionEvaluator permissions() {
            return new RoleBasedPermissionEvaluator();
        }

        @Bean
        static MethodSecurityExpressionHandler expressions(RoleBasedPermissionEvaluator permissions) {
            DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
            handler.setPermissionEvaluator(permissions);
            return handler;
        }
    }
}
