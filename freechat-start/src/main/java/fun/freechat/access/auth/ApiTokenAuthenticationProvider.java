package fun.freechat.access.auth;

import fun.freechat.access.user.SysUserDetails;
import fun.freechat.model.User;
import fun.freechat.service.account.SysApiTokenService;
import fun.freechat.service.account.SysAuthorityService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.thymeleaf.util.ArrayUtils;

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
    private SecurityContextHolderStrategy securityContextHolderStrategy =
            SecurityContextHolder.getContextHolderStrategy();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SysUserDetails sysUser = (SysUserDetails)authentication.getPrincipal();
        Set<String> tokens = (Set<String>)authentication.getCredentials();
        if (sysUser == null) {
            throw new BadCredentialsException("Invalid tokens: " + String.join(",", tokens));
        }

        ApiTokenAuthenticationToken authenticated = new ApiTokenAuthenticationToken(
                sysUser, tokens, true);
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

        if (CollectionUtils.isEmpty(tokens)) {
            return null;
        }

        SysUserDetails sysUser = null;
        String currentUserId = null;
        Authentication origAuthentication = securityContextHolderStrategy.getContext().getAuthentication();
        if (origAuthentication != null && origAuthentication.getPrincipal() instanceof User currentUser) {
            currentUserId = currentUser.getUserId();
        }
        for (String token : tokens) {
            User user = apiTokenService.getUser(token);
            if (user != null) {
                if (Objects.equals(user.getUserId(), currentUserId)) {
                    return null;
                }
                sysUser = SysUserDetails.builder()
                        .fromUser(user)
                        .withAuthorityService(authorityService)
                        .withPasswordEncoder(passwordEncoder)
                        .build();
                break;
            }
        }

        return new ApiTokenAuthenticationToken(sysUser, tokens, false);
    }

    private boolean validateToken(String token) {
        return prefix == null ? StringUtils.isNotBlank(token) : token.startsWith(prefix);
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

    public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
        Assert.notNull(securityContextHolderStrategy, "securityContextHolderStrategy cannot be null");
        this.securityContextHolderStrategy = securityContextHolderStrategy;
    }
}
