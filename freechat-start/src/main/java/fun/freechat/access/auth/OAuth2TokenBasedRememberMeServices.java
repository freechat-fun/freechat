package fun.freechat.access.auth;

import fun.freechat.access.user.SysUserDetailsManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Optional;

public class OAuth2TokenBasedRememberMeServices extends TokenBasedRememberMeServices {
    private final String BLACKLIST_KEY_PREFIX = "remember_me:blacklist:";
    private final RedissonClient redissonClient;

    public OAuth2TokenBasedRememberMeServices(
            String key, UserDetailsService userDetailsService, RedissonClient redissonClient) {
        super(key, userDetailsService);
        this.redissonClient = redissonClient;
    }

    @Override
    protected UserDetails processAutoLoginCookie(
            String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) {
        String tokenSignature = Optional.of(cookieTokens)
                .filter(tokens -> tokens.length > 2)
                .map(OAuth2TokenBasedRememberMeServices::getTokenSignature)
                .filter(StringUtils::hasLength)
                .orElseThrow(() -> new InvalidCookieException("Cookie token did not contain expected token signature"));

        RBucket<String> blacklistedBucket = redissonClient.getBucket(BLACKLIST_KEY_PREFIX + tokenSignature);
        if (blacklistedBucket.isExists()) {
            throw new InvalidCookieException(
                    "Invalid remember-me token signature: " + tokenSignature);
        }

        return super.processAutoLoginCookie(cookieTokens, request, response);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String rememberMeCookie = extractRememberMeCookie(request);
        if (rememberMeCookie != null) {
            try {
                String[] tokens = decodeCookie(rememberMeCookie);
                if (tokens.length >= 3 && StringUtils.hasText(tokens[2])) {
                    String username = tokens[0];
                    String tokenSignature = getTokenSignature(tokens);
                    logger.debug("Blacklisting remember-me, username: " + username + ", token: " + tokenSignature);
                    RBucket<String> blacklistedBucket = redissonClient.getBucket(BLACKLIST_KEY_PREFIX + tokenSignature);
                    blacklistedBucket.set(username, Duration.ofSeconds(getTokenValiditySeconds()));
                }
            } catch (Exception e) {
                logger.error("Failed to decode remember-me cookie for blacklisting.", e);
            }
        }

        super.logout(request, response, authentication);
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

    private static String getTokenSignature(String[] cookieTokens) {
        return cookieTokens.length == 4 ? cookieTokens[3] : cookieTokens[2];
    }
}
