package fun.freechat.access.auth.handler;

import fun.freechat.access.auth.customizer.OAuth2AuthorizationRequestCustomizer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@SuppressWarnings("unused")
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final OAuth2AuthorizationRequestCustomizer oAuth2AuthorizationRequestCustomizer;

    public OAuth2AuthenticationFailureHandler(OAuth2AuthorizationRequestCustomizer oAuth2AuthorizationRequestCustomizer) {
        this.oAuth2AuthorizationRequestCustomizer = oAuth2AuthorizationRequestCustomizer;
    }

    public OAuth2AuthenticationFailureHandler(String defaultFailureUrl, OAuth2AuthorizationRequestCustomizer oAuth2AuthorizationRequestCustomizer) {
        super(defaultFailureUrl);
        this.oAuth2AuthorizationRequestCustomizer = oAuth2AuthorizationRequestCustomizer;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String state = request.getParameter(OAuth2ParameterNames.STATE);
        Authentication sysAuth = StringUtils.isNotBlank(state) ?
                oAuth2AuthorizationRequestCustomizer.getAndDeleteCachedAuthentication(state) : null;

        if (sysAuth != null) {
            SecurityContextHolder.getContext().setAuthentication(sysAuth);
        }

        log.warn("Failed to authenticate by oauth2!", exception);
        super.onAuthenticationFailure(request, response, exception);
    }
}
