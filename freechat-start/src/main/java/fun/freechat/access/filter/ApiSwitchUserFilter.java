package fun.freechat.access.filter;

import fun.freechat.access.user.SysUserDetails;
import fun.freechat.access.user.SysUserDetailsManager;
import fun.freechat.model.User;
import fun.freechat.service.account.SysAuthorityService;
import fun.freechat.service.account.SysUserService;
import fun.freechat.service.organization.OrgService;
import fun.freechat.util.AuthorityUtils;
import fun.freechat.util.IdUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.log.LogMessage;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.switchuser.AuthenticationSwitchUserEvent;
import org.springframework.security.web.authentication.switchuser.SwitchUserAuthorityChanger;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
@Slf4j
public class ApiSwitchUserFilter extends SwitchUserFilter {
    private static final Pattern HEADER_PATTERN = Pattern.compile("([^:@]*)[:]?([^:@]*)[@]?([^:@]*)");
    private static final String CHAIN_ATTR_NAME = ApiSwitchUserFilter.class.getName() + "_chain";

    @Setter
    private String headerName;
    private boolean enableAutoRegister;
    @Setter
    private OrgService orgService;
    private SysUserDetailsManager sysUserDetailsManager;
    private SysUserService sysUserService;
    private SysAuthorityService sysAuthorityService;
    private PasswordEncoder passwordEncoder;
    private SecurityContextHolderStrategy securityContextHolderStrategy =
            SecurityContextHolder.getContextHolderStrategy();
    private UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();
    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource =
            new WebAuthenticationDetailsSource();
    private String switchAuthorityRole = ROLE_PREVIOUS_ADMINISTRATOR;
    private SwitchUserAuthorityChanger switchUserAuthorityChanger;
    private ApplicationEventPublisher eventPublisher;

    public ApiSwitchUserFilter() {}

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Assert.notNull(this.headerName, "headerName must be specified");
        Assert.notNull(this.orgService, "orgService must be specified");
        Assert.notNull(this.sysUserService, "sysUserService must be specified");
        Assert.notNull(this.sysAuthorityService, "sysAuthorityService must be specified");
        Assert.notNull(this.passwordEncoder, "passwordEncoder must be specified");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setAttribute(CHAIN_ATTR_NAME, chain);
        super.doFilter(request, response, chain);
        request.removeAttribute(CHAIN_ATTR_NAME);
    }

    @Override
    protected boolean requiresSwitchUser(HttpServletRequest request) {
        return super.requiresSwitchUser(request) &&
                StringUtils.isNotBlank(request.getHeader(headerName));
    }

    @Override
    protected boolean requiresExitUser(HttpServletRequest request) {
        Authentication current = securityContextHolderStrategy.getContext().getAuthentication();
        if (Objects.isNull(current)) {
            return false;
        }
        Authentication original = getSourceAuthentication(current);
        if (Objects.isNull(original)) {
            return false;
        }
        return StringUtils.isBlank(request.getHeader(headerName));
    }

    @Override
    protected Authentication attemptSwitchUser(HttpServletRequest request) throws AuthenticationException {
        Authentication currentAuthentication = getCurrentAuthentication(request);
        if (Objects.isNull(currentAuthentication)) {
            throw new BadCredentialsException("Failed to get current user info");
        }
        SysUserDetails currentUser = (SysUserDetails) currentAuthentication.getPrincipal();
        String headerInfo = request.getHeader(headerName);
        if (StringUtils.isBlank(headerInfo)) {
            throw new AuthenticationCredentialsNotFoundException(
                    "Failed to get target user info from header: " + headerName);
        }
        checkPermission(currentAuthentication);
        var userInfo = resolveHeader(headerInfo, currentUser);
        if (Objects.isNull(userInfo)) {
            throw new AuthenticationCredentialsNotFoundException(
                    "Failed to parse target user info from header value: " + headerInfo);
        }
        SysUserDetails targetUser = loadUser(userInfo);
        if (Objects.isNull(targetUser)) {
            throw new BadCredentialsException(
                    "Failed to query users: " + userInfo.getLeft() + ":" + userInfo.getMiddle());
        }
        userDetailsChecker.check(targetUser);
        // OK, create the switch user token
        Authentication targetUserRequest = createSwitchUserToken(request, targetUser);
        // publish event
        if (Objects.nonNull(eventPublisher)) {
            this.eventPublisher.publishEvent(new AuthenticationSwitchUserEvent(
                    securityContextHolderStrategy.getContext().getAuthentication(), targetUser));
        }
        return targetUserRequest;
    }

    private UsernamePasswordAuthenticationToken createSwitchUserToken(HttpServletRequest request,
                                                                      UserDetails targetUser) {
        UsernamePasswordAuthenticationToken targetUserRequest;
        // grant an additional authority that contains the original Authentication object
        // which will be used to 'exit' from the current switched user.
        Authentication currentAuthentication = getCurrentAuthenticationAndExitUser(request);
        GrantedAuthority switchAuthority = new SwitchUserGrantedAuthority(
                switchAuthorityRole, currentAuthentication);
        // get the original authorities
        Collection<? extends GrantedAuthority> orig = targetUser.getAuthorities();
        // Allow subclasses to change the authorities to be granted
        if (Objects.nonNull(switchUserAuthorityChanger)) {
            orig = switchUserAuthorityChanger.modifyGrantedAuthorities(targetUser, currentAuthentication, orig);
        }
        // add the new switch user authority
        List<GrantedAuthority> newAuths = new ArrayList<>(orig);
        newAuths.add(switchAuthority);
        // create the new authentication token
        targetUserRequest = UsernamePasswordAuthenticationToken.authenticated(
                targetUser, targetUser.getPassword(), newAuths);
        // set details
        targetUserRequest.setDetails(authenticationDetailsSource.buildDetails(request));
        return targetUserRequest;
    }

    private void checkPermission(Authentication currentAuthentication) {
        int requiredPriority = AuthorityUtils.getPriority(AuthorityUtils.BIZ_ADMIN);
        boolean hasPermission = currentAuthentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> AuthorityUtils.getPriority(authority) >= requiredPriority);
        if (!hasPermission) {
            throw new AccessDeniedException("No permission to impersonate.");
        }
    }

    private Authentication getCurrentAuthentication(HttpServletRequest request) {
        Authentication current = securityContextHolderStrategy.getContext().getAuthentication();
        if (Objects.isNull(current)) {
            return null;
        }
        Authentication original = getSourceAuthentication(current);
        return Objects.nonNull(original) ? original : current;
    }

    private Authentication getCurrentAuthenticationAndExitUser(HttpServletRequest request) {
        try {
            // SEC-1763. Check first if we are already switched.
            return attemptExitUser(request);
        } catch (AuthenticationCredentialsNotFoundException ex) {
            return securityContextHolderStrategy.getContext().getAuthentication();
        }
    }

    private Authentication getSourceAuthentication(Authentication current) {
        Authentication original = null;
        // iterate over granted authorities and find the 'switch user' authority
        Collection<? extends GrantedAuthority> authorities = current.getAuthorities();
        for (GrantedAuthority auth : authorities) {
            // check for switch user type of authority
            if (auth instanceof SwitchUserGrantedAuthority) {
                original = ((SwitchUserGrantedAuthority) auth).getSource();
                logger.debug(LogMessage.format("Found original switch user granted authority [%s]", original));
            }
        }
        return original;
    }

    private Triple<String, String, String> resolveHeader(String headerValue, SysUserDetails currentUser) {
        Matcher m = HEADER_PATTERN.matcher(headerValue);
        if (!m.matches()) {
            return null;
        }

        int count = m.groupCount();

        String platform = currentUser.getPlatform();
        if (count > 2 && StringUtils.isNotBlank(m.group(3))) {
            platform = m.group(3);
        } else if (StringUtils.isBlank(platform)) {
            platform = "system";
        }

        String username = "";
        if (count > 0 && StringUtils.isNotBlank(m.group(1))) {
            username = m.group(1);
            if (!"system".equalsIgnoreCase(platform)) {
                username = username + "@" + platform;
            }
        }

        String ownerUsername = currentUser.getUsername();
        if (count > 1 && StringUtils.isNotBlank(m.group(2))) {
            ownerUsername = m.group(2);
            if (!"system".equalsIgnoreCase(platform)) {
                ownerUsername = ownerUsername + "@" + platform;
            }
        }

        if (ownerUsername.equals(username)) {
            ownerUsername = currentUser.getUsername();
        }

        return StringUtils.isNotBlank(username) ? Triple.of(username, ownerUsername, platform) : null;
    }

    public SysUserDetails loadUser(Triple<String, String, String> userInfo) {
        String username = userInfo.getLeft();
        String ownerUsername = userInfo.getMiddle();
        String platform = userInfo.getRight();

        User user = sysUserService.loadByUsernameAndPlatform(username, platform);
        if (Objects.isNull(user) && enableAutoRegister) {
            user = new User()
                    .withUsername(username)
                    .withPassword(IdUtils.newId())
                    .withPlatform(platform);
            if (!sysUserService.create(user)) {
                return null;
            }
        }
        User owner = sysUserService.loadByUsernameAndPlatform(ownerUsername, platform);
        if (Objects.isNull(owner) && enableAutoRegister) {
            owner = new User()
                    .withUsername(ownerUsername)
                    .withPassword(IdUtils.newId())
                    .withPlatform(platform);
            if (!sysUserService.create(owner)) {
                return null;
            }
        }

        if (Objects.isNull(user) || Objects.isNull(owner)) {
            return null;
        }

        if (enableAutoRegister) {
            boolean buildRelationship = true;
            for (String authority : sysAuthorityService.list(owner)) {
                if (AuthorityUtils.ADMIN.equals(authority)) {
                    buildRelationship = false;
                    break;
                }
            }
            if (buildRelationship) {
                orgService.addSubordinates(owner.getUserId(), List.of(user.getUserId()));
            }
        }

        return SysUserDetails.builder()
                .fromUser(user)
                .withAuthorityService(sysAuthorityService)
                .withPasswordEncoder(passwordEncoder)
                .build();
    }

    @Override
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        super.setUserDetailsService(userDetailsService);
        sysUserDetailsManager = (SysUserDetailsManager) userDetailsService;
        sysUserService = sysUserDetailsManager.userService();
        sysAuthorityService = sysUserDetailsManager.authorityService();
        passwordEncoder = sysUserDetailsManager.passwordEncoder();
    }

    @Override
    public void setUserDetailsChecker(UserDetailsChecker userDetailsChecker) {
        super.setUserDetailsChecker(userDetailsChecker);
        this.userDetailsChecker = userDetailsChecker;
    }

    @Override
    public void setSwitchAuthorityRole(String switchAuthorityRole) {
        super.setSwitchAuthorityRole(switchAuthorityRole);
        this.switchAuthorityRole = switchAuthorityRole;
    }

    @Override
    public void setSwitchUserAuthorityChanger(SwitchUserAuthorityChanger switchUserAuthorityChanger) {
        super.setSwitchUserAuthorityChanger(switchUserAuthorityChanger);
        this.switchUserAuthorityChanger = switchUserAuthorityChanger;
    }

    @Override
    public void setAuthenticationDetailsSource(
            AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        super.setAuthenticationDetailsSource(authenticationDetailsSource);
        this.authenticationDetailsSource = authenticationDetailsSource;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) throws BeansException {
        super.setApplicationEventPublisher(eventPublisher);
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void setSuccessHandler(AuthenticationSuccessHandler successHandler) {
        AuthenticationSuccessHandler handler = (request, response, authentication) -> {
            FilterChain chain = (FilterChain) request.getAttribute(CHAIN_ATTR_NAME);
            if (Objects.isNull(chain)) {
                successHandler.onAuthenticationSuccess(request, response, authentication);
            } else {
                successHandler.onAuthenticationSuccess(request, response, chain, authentication);
            }
        };
        super.setSuccessHandler(handler);
    }

    @Override
    public void setFailureHandler(AuthenticationFailureHandler failureHandler) {
        AuthenticationFailureHandler handler = (request, response, exception) -> {
            FilterChain chain = (FilterChain) request.getAttribute(CHAIN_ATTR_NAME);
            failureHandler.onAuthenticationFailure(request, response, exception);
            if (Objects.nonNull(chain)) {
                chain.doFilter(request, response);
            }
        };
        super.setFailureHandler(handler);
    }

    public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
        Assert.notNull(securityContextHolderStrategy, "securityContextHolderStrategy cannot be null");
        this.securityContextHolderStrategy = securityContextHolderStrategy;
    }

    public void setAutoRegister(boolean enableAutoRegister) {
        this.enableAutoRegister = enableAutoRegister;
    }

    public static void main(String[] args) {
        List<String> samples = List.of("a12", "a12:b34", "a12@cd", "a12:b34@cd", "a12:@cd",
                ":b34@cd", "a12:","a12:@", ":@cd", "a12:a12", "a12:a12@cd");
        for (String s : samples) {
            Matcher m = HEADER_PATTERN.matcher(s);
            log.info("'{}', m.matches: {}", s, m.matches());
            if (m.matches()) {
                log.info("m.groupCount: {}", m.groupCount());
                for (int i = 1; i <= m.groupCount(); ++i) {
                    log.info("  m.group[{}]: {}", i, m.group(i));
                }
            }
            log.info("========");
        }
    }
}
