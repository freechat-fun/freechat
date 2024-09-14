package fun.freechat.api.util;

import fun.freechat.access.auth.ApiTokenAuthenticationToken;
import fun.freechat.access.user.SysUserDetails;
import fun.freechat.access.user.SysUserDetailsManager;
import fun.freechat.model.User;
import fun.freechat.service.account.SysUserService;
import fun.freechat.util.AuthorityUtils;
import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;

@Component
public class AccountUtils implements ApplicationContextAware {
    private static SysUserService userService;
    private static UserDetailsManager userDetailsManager;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        userService = applicationContext.getBean(SysUserService.class);
        userDetailsManager = applicationContext.getBean(UserDetailsManager.class);
    }

    @NonNull
    public static User currentUser() {
        Authentication authenticated = SecurityContextHolder.getContext().getAuthentication();
        if (authenticated == null || authenticated instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        Object principal = authenticated.getPrincipal();
        User user = null;
        if (principal instanceof User) {
            user = (User) principal;
        } else if (principal instanceof String userName) {
            user = userService.loadByUsername(userName);
        }
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return user;
    }

    public static void updateCurrentUser() {
        User currentUser = currentUser();
        SysUserDetails sysUser = ((SysUserDetailsManager) userDetailsManager).loadUserByUsernameAndPlatform(
                currentUser.getUsername(), currentUser.getPlatform());
        Authentication authenticated = SecurityContextHolder.getContext().getAuthentication();
        Authentication newAuthenticated = authenticated;

        if (authenticated instanceof UsernamePasswordAuthenticationToken) {
            newAuthenticated = UsernamePasswordAuthenticationToken.authenticated(
                    sysUser, authenticated.getCredentials(), authenticated.getAuthorities());
        } else if (authenticated instanceof ApiTokenAuthenticationToken) {
            newAuthenticated = new ApiTokenAuthenticationToken(
                    sysUser, (Set<String>)authenticated.getCredentials(), true);
        }

        SecurityContextHolder.getContext().setAuthentication(newAuthenticated);
    }

    @NonNull
    public static String currentRole() {
        Authentication authenticated = SecurityContextHolder.getContext().getAuthentication();
        if (authenticated == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        String maxAuthority = AuthorityUtils.USER;
        int maxPriority = AuthorityUtils.getPriority(maxAuthority);
        for (var grantedAuthority : authenticated.getAuthorities()) {
            String authority = grantedAuthority.getAuthority();
            int priority = AuthorityUtils.getPriority(authority);
            if (priority > maxPriority) {
                maxAuthority = authority;
                maxPriority = priority;
            }
        }
        return AuthorityUtils.toRole(maxAuthority);
    }

    public static String userIdToName(String userId) {
        return Optional.ofNullable(userId)
                .map(userService::loadByUserId)
                .map(User::getUsername)
                .orElse(userId);
    }

    public static String userNameToId(String userName) {
        return Optional.ofNullable(userName)
                .map(userService::loadByUsername)
                .map(User::getUserId)
                .orElse(userName);
    }

    public static boolean hasSufficientPermission(String authority) {
        return AuthorityUtils.getPriorityOfRole(AccountUtils.currentRole()) >= AuthorityUtils.getPriority(authority);
    }
}
