package fun.freechat.access.auth;

import fun.freechat.access.user.SysUserDetails;
import fun.freechat.model.User;
import fun.freechat.service.account.SysApiTokenService;
import fun.freechat.service.account.SysAuthorityService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.ArrayUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@SuppressWarnings("unused")
public class ApiTokenAuthenticationProvider implements AuthenticationProvider, AuthenticationConverter {
    @Value("${auth.token.parameterName:#{null}}")
    private String parameterName;

    @Value("${auth.token.headerName:#{null}}")
    private String headerName;

    @Value("${auth.token.prefix:#{null}}")
    private String prefix;

    @Autowired
    private SysApiTokenService apiTokenService;

    @Autowired
    private SysAuthorityService authorityService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        ApiTokenAuthenticationToken apiTokenAuthentication = (ApiTokenAuthenticationToken) authentication;
        Set<String> tokens = (Set<String>)apiTokenAuthentication.getCredentials();
        SysUserDetails sysUser = null;
        for (String token : tokens) {
            User user = apiTokenService.getUser(token);
            if (Objects.nonNull(user)) {
                sysUser = SysUserDetails.builder()
                        .fromUser(user)
                        .withAuthorityService(authorityService)
                        .withPasswordEncoder(passwordEncoder)
                        .build();
                break;
            }
        }

        if (Objects.isNull(sysUser)) {
            throw new BadCredentialsException("Invalid tokens: " + String.join(",", tokens));
        }

        ApiTokenAuthenticationToken authenticated = new ApiTokenAuthenticationToken(sysUser, tokens);
        authenticated.setDetails(authentication.getDetails());
        return authenticated;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(ApiTokenAuthenticationToken.class);
    }

    @Override
    public Authentication convert(HttpServletRequest request) {
        Set<String> tokens = resolveTokensFromQuery(request);
        if (CollectionUtils.isEmpty(tokens)) {
            tokens = resolveTokensFromHeader(request);
        }

        return CollectionUtils.isNotEmpty(tokens) ? new ApiTokenAuthenticationToken(tokens) : null;
    }

    private boolean validateToken(String token) {
        return Objects.isNull(prefix) ? StringUtils.isNotBlank(token) : token.startsWith(prefix);
    }

    private Set<String> resolveTokensFromQuery(HttpServletRequest request) {
        if (StringUtils.isBlank(parameterName)) {
            return null;
        }
        String[] tokens = request.getParameterValues(parameterName);
        return ArrayUtils.isEmpty(tokens) ? null
                : Arrays.stream(tokens).filter(this::validateToken).collect(Collectors.toSet());
    }

    private Set<String> resolveTokensFromHeader(HttpServletRequest request) {
        String tokenValue;
        if (StringUtils.isBlank(headerName)) {
            tokenValue = StringUtils.removeStart(request.getHeader(AUTHORIZATION), "Bearer ");
        } else {
            tokenValue = request.getHeader(headerName);
        }
        return StringUtils.isBlank(tokenValue) ? null
                : Arrays.stream(tokenValue.trim().split(",")).filter(this::validateToken).collect(Collectors.toSet());
    }
}
