package fun.freechat.api.util;

import fun.freechat.model.User;
import fun.freechat.service.account.SysUserService;
import fun.freechat.util.AuthorityUtils;
import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.Optional;

@Component
public class AccountUtils implements ApplicationContextAware {
    private static SysUserService userService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        userService = applicationContext.getBean(SysUserService.class);
    }

    @NonNull
    public static User currentUser() {
        Authentication authenticated = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authenticated) || authenticated instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        Object principal = authenticated.getPrincipal();
        if (principal instanceof User user) {
            return user;
        } else if (principal instanceof String userName) {
            return userService.loadByUsername(userName);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @NonNull
    public static String currentRole() {
        Authentication authenticated = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authenticated)) {
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
}
