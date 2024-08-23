package fun.freechat.access.auth;

import fun.freechat.access.user.SysUserDetailsManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

public class OAuth2TokenBasedRememberMeServices extends TokenBasedRememberMeServices {
    public OAuth2TokenBasedRememberMeServices(String key, UserDetailsService userDetailsService) {
        super(key, userDetailsService);
    }

    @Override
    protected String retrieveUserName(Authentication authentication) {
        if (getUserDetailsService() instanceof SysUserDetailsManager sysUserDetailsManager &&
                authentication instanceof OAuth2AuthenticationToken oAuth2Authentication) {
            OAuth2User oAuth2User = oAuth2Authentication.getPrincipal();
            String platform = oAuth2Authentication.getAuthorizedClientRegistrationId();
            return sysUserDetailsManager.getUsername(oAuth2User, platform);
        }
        return super.retrieveUserName(authentication);
    }

    @Override
    protected String retrievePassword(Authentication authentication) {
        if (getUserDetailsService() instanceof SysUserDetailsManager sysUserDetailsManager &&
                authentication instanceof OAuth2AuthenticationToken oAuth2Authentication) {
            OAuth2User oAuth2User = oAuth2Authentication.getPrincipal();
            String platform = oAuth2Authentication.getAuthorizedClientRegistrationId();
            return sysUserDetailsManager.getPassword(oAuth2User, platform);
        }
        return super.retrievePassword(authentication);
    }
}
