package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import fun.freechat.access.ConnectorRequestFilter;
import fun.freechat.api.ConnectorApi;
import fun.freechat.api.ConnectorApiErrors;
import fun.freechat.api.access.RoleBasedPermissionEvaluator;
import fun.freechat.api.dto.ConnectorDetailsDTO;
import fun.freechat.api.dto.ConnectorWriteDTO;
import fun.freechat.config.JacksonConfig;
import fun.freechat.model.User;
import fun.freechat.service.connectors.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockMakers;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ConnectorApiSecurityTest {
    private static final String ID = "a".repeat(32);
    private static final String REVISION = "b".repeat(32);
    private static final ConnectorService.Summary SUMMARY = new ConnectorService.Summary(
            ID,
            REVISION,
            "name",
            "http",
            "https://example.com/mcp",
            60000,
            true,
            Set.of("Authorization"),
            Instant.EPOCH);
    private AnnotationConfigApplicationContext context;
    private ConnectorApi api;
    private ConnectorService service;

    @BeforeEach
    void setup() {
        context = new AnnotationConfigApplicationContext(Security.class);
        api = context.getBean(ConnectorApi.class);
        service = context.getBean(ConnectorService.class);
    }

    @AfterEach
    void close() {
        SecurityContextHolder.clearContext();
        context.close();
    }

    @Test
    void anonymousRequestsCannotEnterManagement() {
        assertThrows(AuthenticationCredentialsNotFoundException.class, api::list);
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> api.create(new ConnectorWriteDTO()));
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> api.get(ID));
        verifyNoInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ROLE_USER", "ROLE_ADMIN"})
    void ownershipIsCheckedBeforeAdminShortcut(String role) {
        authenticate("intruder", role);
        when(service.list("intruder")).thenReturn(List.of());
        assertThrows(AccessDeniedException.class, () -> api.get(ID));
        assertThrows(AccessDeniedException.class, () -> api.update(ID, request()));
        assertThrows(AccessDeniedException.class, () -> api.delete(ID, REVISION));
        verify(service, never()).get(anyString(), anyString());
        verify(service, never()).update(anyString(), anyString(), any(), any(), any(), anyBoolean());
        verify(service, never()).delete(anyString(), anyString(), any());
    }

    @Test
    void ownerIsDerivedFromPrincipalAndResponsesHaveNamesOnly() throws Exception {
        authenticate("owner", "ROLE_USER");
        when(service.list("owner")).thenReturn(List.of(SUMMARY));
        when(service.create(eq("owner"), any(), eq(true))).thenReturn(SUMMARY);
        when(service.get("owner", ID)).thenReturn(SUMMARY);
        when(service.update(eq("owner"), eq(ID), eq(REVISION), any(), eq(Set.of()), eq(true)))
                .thenReturn(SUMMARY);
        ConnectorWriteDTO request = request();
        assertEquals(ID, api.create(request).getConnectorId());
        request.setExpectedRevision(REVISION);
        request.setKeepHeaderNames(List.of());
        assertEquals(ID, api.update(ID, request).getConnectorId());
        api.delete(ID, REVISION);
        verify(service).delete("owner", ID, REVISION);
        var mapper = new JacksonConfig().objectMapper();
        String response = mapper.writeValueAsString(api.get(ID));
        assertTrue(response.contains("headerNames"));
        assertFalse(response.contains("secret-value"));
        assertFalse(response.contains("\"headers\""));
        assertFalse(mapper.writeValueAsString(request).contains("secret-value"));
        assertFalse(request.toString().contains("example.com"));
        assertFalse(ConnectorDetailsDTO.from(SUMMARY).toString().contains("example.com"));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "{\"ownerId\":\"private-token\"}",
                "{\"command\":\"private-token\"}",
                "{\"headers\":{\"X-Key\":\"private-token\",\"x-key\":\"other\"}}",
                "{\"headers\":{\"X-Key\":\"private-token\",\"X-Key\":\"other\"}}",
                "{\"name\":\"one\",\"name\":\"private-token\"}",
                "{\"headers\":{\"X-Key\":123}}",
                "{\"active\":\"private-token\"}",
                "{\"timeout\":1.5}",
                "{\"timeout\":2147483648}",
                "{\"keepHeaderNames\":[\"X-Key\",\"x-key\"]}",
                "{\"url\":{\"private-token\":true}}"
            })
    void malformedJsonAndUnknownFieldsAreRejectedAndSanitized(String json) throws Exception {
        authenticate("owner", "ROLE_USER");
        var response = mvc().perform(post("/api/v2/connector")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn()
                .getResponse();
        assertEquals(400, response.getStatus());
        assertEquals("no-store", response.getHeader("Cache-Control"));
        assertTrue(response.getContentAsString().contains("CONNECTOR_INVALID_CONFIGURATION"));
        assertFalse(response.getContentAsString().contains("private-token"));
        verifyNoInteractions(service);
    }

    @Test
    void missingAndForeignIdsAreIndistinguishableInHttpResponses() throws Exception {
        authenticate("intruder", "ROLE_ADMIN");
        when(service.list("intruder")).thenReturn(List.of());
        for (String id : List.of(ID, "c".repeat(32))) {
            var response =
                    mvc().perform(get("/api/v2/connector/" + id)).andReturn().getResponse();
            assertEquals(404, response.getStatus());
            assertTrue(response.getContentAsString().contains("CONNECTOR_NOT_FOUND"));
            assertEquals("no-store", response.getHeader("Cache-Control"));
        }
    }

    @Test
    void invalidConfigurationAndRevisionErrorsUseStableCodes() throws Exception {
        authenticate("owner", "ROLE_USER");
        var response = mvc().perform(post("/api/v2/connector")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"name\",\"transport\":\"stdio\",\"url\":\"https://secret.example\"}"))
                .andReturn()
                .getResponse();
        assertEquals(422, response.getStatus());
        assertFalse(response.getContentAsString().contains("secret.example"));
        assertEquals(
                409,
                new ConnectorApiErrors()
                        .handle(new ConnectorException(ConnectorException.Code.CONNECTOR_VERSION_CONFLICT))
                        .getStatusCode()
                        .value());
        verifyNoInteractions(service);
    }

    private MockMvc mvc() {
        return MockMvcBuilders.standaloneSetup(api)
                .setControllerAdvice(new ConnectorApiErrors())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(new JacksonConfig().objectMapper()))
                .addFilters(new ConnectorRequestFilter())
                .build();
    }

    private static ConnectorWriteDTO request() {
        ConnectorWriteDTO result = new ConnectorWriteDTO();
        result.setName("name");
        result.setTransport("http");
        result.setUrl("https://example.com/mcp");
        result.setHeaders(Map.of("Authorization", "secret-value"));
        return result;
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
        ConnectorService connectors() {
            return mock(ConnectorService.class, withSettings().mockMaker(MockMakers.SUBCLASS));
        }

        @Bean
        ConnectorApi api(ConnectorService connectors) {
            return new ConnectorApi(connectors);
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
