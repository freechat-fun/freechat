package fun.freechat.config;

import fun.freechat.util.AppMetaUtils;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Configuration
@Slf4j
@SuppressWarnings("unused")
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI(
            @Value("${auth.token.headerName:#{null}}") String securityHeaderName,
            @Value("${auth.token.parameterName:#{null}}") String securityParameterName) {
        OpenAPI openAPI = new OpenAPI();
        if (securityHeaderName == null && securityParameterName == null) {
            openAPI.components(new Components()
                            .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                    .type(SecurityScheme.Type.HTTP)
                                    .scheme("bearer")))
                    .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
        } else {
            Components components = new Components();
            SecurityRequirement securityRequirement = new SecurityRequirement();
            if (securityHeaderName != null) {
                components.addSecuritySchemes("SysApiTokenHeader", new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name(securityHeaderName));
                securityRequirement.addList("SysApiTokenHeader");
            }

            if (securityParameterName != null) {
                components.addSecuritySchemes("SysApiTokenParameter", new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.QUERY)
                        .name(securityParameterName));
                securityRequirement.addList("SysApiTokenParameter");
            }
            openAPI.components(components)
                    .addSecurityItem(securityRequirement);
        }

        openAPI.info(new Info()
                        .title("FreeChat OpenAPI Definition")
                        .description(getReadmeContent())
                        .version(AppMetaUtils.getVersion()))
                .addServersItem(new Server().url("/"));

        return openAPI;
    }

    private String getReadmeContent() {
        String path = "/README.md";
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (InputStream in = OpenApiConfig.class.getResourceAsStream(path)) {
            if (in == null) {
                return AppMetaUtils.getUrl();
            }
            byte[] data = new byte[512];
            int n;
            while ((n = in.read(data)) != -1) {
                buffer.write(data, 0, n);
            }
        } catch (IOException e) {
            log.error("Failed to load {}", path, e);
            return AppMetaUtils.getUrl();
        }

        return buffer.toString(StandardCharsets.UTF_8);
    }
}
