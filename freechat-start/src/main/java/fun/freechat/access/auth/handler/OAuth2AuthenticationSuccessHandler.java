package fun.freechat.access.auth.handler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import fun.freechat.access.auth.customizer.OAuth2AuthorizationRequestCustomizer;
import fun.freechat.access.user.SysUserDetails;
import fun.freechat.access.user.SysUserDetailsManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@SuppressWarnings("unused")
public class OAuth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final OAuth2AuthorizedClientService oAuth2ClientService;
    private final SysUserDetailsManager userDetailsManager;
    private final OAuth2AuthorizationRequestCustomizer oAuth2AuthorizationRequestCustomizer;

    public OAuth2AuthenticationSuccessHandler(OAuth2AuthorizedClientService oAuth2ClientService,
                                              SysUserDetailsManager userDetailsManager,
                                              OAuth2AuthorizationRequestCustomizer oAuth2AuthorizationRequestCustomizer) {
        this.oAuth2ClientService = oAuth2ClientService;
        this.userDetailsManager = userDetailsManager;
        this.oAuth2AuthorizationRequestCustomizer = oAuth2AuthorizationRequestCustomizer;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        String state = request.getParameter(OAuth2ParameterNames.STATE);
        Authentication sysAuth = StringUtils.isNotBlank(state) ?
                oAuth2AuthorizationRequestCustomizer.getAndDeleteCachedAuthentication(state) : null;

        if (authentication instanceof OAuth2AuthenticationToken oAuth2Auth) {
            String clientRegistrationId = oAuth2Auth.getAuthorizedClientRegistrationId();
            String principalName = oAuth2Auth.getName();
            OAuth2AuthorizedClient oAuth2Client =
                    oAuth2ClientService.loadAuthorizedClient(clientRegistrationId, principalName);
            OAuth2User oAuth2User = oAuth2Auth.getPrincipal();

            SysUserDetails sysUser;
            if (sysAuth != null && sysAuth.getPrincipal() instanceof SysUserDetails userDetails) {
                sysUser = userDetails;
            } else {
                sysUser = userDetailsManager.createSysUserFromOAuth2UserIfNecessary(oAuth2User, oAuth2Client);
                sysAuth = UsernamePasswordAuthenticationToken.authenticated(sysUser, oAuth2Auth.getCredentials(),
                        sysUser.getAuthorities());
            }
            userDetailsManager.bindSysUserAndOAuth2UserIfNecessary(sysUser, oAuth2User, oAuth2Client);
            ((AbstractAuthenticationToken)sysAuth).setDetails(oAuth2Auth);
            SecurityContextHolder.getContext().setAuthentication(sysAuth);
            authentication = sysAuth;
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
