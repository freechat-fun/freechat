package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import fun.freechat.api.CharacterApi;
import fun.freechat.api.ConnectorApiErrors;
import fun.freechat.api.ConnectorCharacterApiErrors;
import fun.freechat.api.ConnectorTestApi;
import fun.freechat.mapper.CharacterInfoMapper;
import fun.freechat.model.User;
import fun.freechat.service.connectors.*;
import fun.freechat.service.skills.SkillCharacterPolicy;
import fun.freechat.service.skills.SkillService;
import fun.freechat.service.tools.ToolArguments;
import fun.freechat.service.tools.ToolRegistry;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.MockMakers;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ConnectorErrorMappingTest {
    @Test
    void headerNameWireFieldsAreListsForGeneratedSdkSerialization() throws Exception {
        assertEquals(
                List.class,
                fun.freechat.api.dto.ConnectorWriteDTO.class
                        .getMethod("getKeepHeaderNames")
                        .getReturnType());
        assertEquals(
                List.class,
                fun.freechat.api.dto.ConnectorDetailsDTO.class
                        .getMethod("getHeaderNames")
                        .getReturnType());
    }

    @Test
    void connectorControllersShareOneOpenApiTagDefinition() {
        assertEquals(
                fun.freechat.api.ConnectorApi.class.getAnnotation(io.swagger.v3.oas.annotations.tags.Tag.class),
                ConnectorTestApi.class.getAnnotation(io.swagger.v3.oas.annotations.tags.Tag.class));
    }

    @Test
    void characterSelectionErrorsUseConnectorCodesWithoutChangingOtherPermissions() throws Exception {
        var previous = SecurityContextHolder.getContext();
        try {
            var context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(
                    new UsernamePasswordAuthenticationToken(new User().withUserId("owner"), null, List.of()));
            SecurityContextHolder.setContext(context);
            var api = new CharacterApi();
            var registry = fake(ConnectorService.class);
            var characters = fake(CharacterInfoMapper.class);
            ReflectionTestUtils.setField(api, "toolRegistry", new ToolRegistry(List.of()));
            ReflectionTestUtils.setField(
                    api, "skillPolicy", new SkillCharacterPolicy(fake(SkillService.class), characters));
            ReflectionTestUtils.setField(api, "connectorPolicy", new ConnectorCharacterPolicy(registry, characters));
            var mvc = MockMvcBuilders.standaloneSetup(api)
                    .setControllerAdvice(new ConnectorCharacterApiErrors(new ConnectorApiErrors()))
                    .build();
            for (String id : List.of("a".repeat(32), "invalid")) {
                String ext = ToolArguments.result(
                        Map.of("tools", Map.of("enabled", List.of()), "connectors", Map.of("enabled", List.of(id))));
                var response = mvc.perform(post("/api/v2/character")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(ToolArguments.result(
                                        Map.of("name", "Role", "visibility", "private", "ext", ext))))
                        .andReturn()
                        .getResponse();
                assertEquals(id.equals("invalid") ? 422 : 403, response.getStatus());
                assertEquals(
                        id.equals("invalid") ? "CONNECTOR_INVALID_CONFIGURATION" : "CONNECTOR_ACCESS_DENIED",
                        ToolArguments.parse(response.getContentAsString())
                                .path("errorCode")
                                .asText());
                assertEquals("no-store", response.getHeader("Cache-Control"));
            }
        } finally {
            SecurityContextHolder.setContext(previous);
        }
    }

    @Test
    void discoveryFailuresUseTheSameSanitizedErrorEnvelope() throws Exception {
        var previous = SecurityContextHolder.getContext();
        try {
            var context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(
                    new UsernamePasswordAuthenticationToken(new User().withUserId("owner"), null, List.of()));
            SecurityContextHolder.setContext(context);
            var probe = fake(ConnectorProbe.class);
            when(probe.tools("owner", "id", "revision"))
                    .thenThrow(new ConnectorException(ConnectorException.Code.CONNECTOR_UNSAFE_ADDRESS));
            var mvc = MockMvcBuilders.standaloneSetup(new ConnectorTestApi(probe))
                    .setControllerAdvice(new ConnectorApiErrors())
                    .build();
            var response = mvc.perform(post("/api/v2/connector/id/test").param("expectedRevision", "revision"))
                    .andReturn()
                    .getResponse();
            assertEquals(422, response.getStatus());
            assertEquals(
                    "CONNECTOR_UNSAFE_ADDRESS",
                    ToolArguments.parse(response.getContentAsString())
                            .path("errorCode")
                            .asText());
        } finally {
            SecurityContextHolder.setContext(previous);
        }
    }

    private static <T> T fake(Class<T> type) {
        return mock(type, withSettings().mockMaker(MockMakers.SUBCLASS));
    }
}
