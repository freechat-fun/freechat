package fun.freechat.util;

import fun.freechat.model.User;
import fun.freechat.service.account.SysApiTokenService;
import fun.freechat.service.account.SysAuthorityService;
import fun.freechat.service.account.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TestAccountUtils implements ApplicationContextAware {
    private static SysUserService userService;
    private static SysAuthorityService authorityService;
    private static SysApiTokenService apiTokenService;

    public static Pair<String, String> createUserAndToken(String username) {
        return createUserAndToken(username, null);
    }

    public static Pair<String, String> createUserAndToken(String username, Set<String> roles) {
        String randomInfo = IdUtils.newId();
        User user = new User().withUsername(username).withPassword("test-" + randomInfo);
        userService.create(user);
        if (CollectionUtils.isNotEmpty(roles)) {
            authorityService.update(user,
                    roles.stream().map(AuthorityUtils::fromRole).collect(Collectors.toSet()));
        }
        return Pair.of(user.getUserId(), apiTokenService.create(user));
    }

    public static void deleteUserAndToken(String username) {
        User user = userService.loadByUsername(username);
        if (Objects.isNull(user)) {
            return;
        }
        apiTokenService.delete(user);
        authorityService.update(user, Collections.emptySet());
        userService.delete(username);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        userService = applicationContext.getBean(SysUserService.class);
        authorityService = applicationContext.getBean(SysAuthorityService.class);
        apiTokenService = applicationContext.getBean(SysApiTokenService.class);
    }
}
