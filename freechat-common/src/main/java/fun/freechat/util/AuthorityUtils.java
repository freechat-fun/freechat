package fun.freechat.util;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("unused")
public class AuthorityUtils {
    private static final String ROLE_PREFIX = "ROLE_";
    private static final String SCOPE_PREFIX = "SCOPE_";
    public static final String ADMIN = "ROLE_ADMIN";
    public static final String BIZ_ADMIN = "ROLE_BIZ_ADMIN";
    public static final String ORG_ADMIN = "ROLE_ORG_ADMIN";
    public static final String RES_ADMIN = "ROLE_RES_ADMIN";
    public static final String USER = "ROLE_USER";
    public static final String CLIENT = "ROLE_CLIENT";

    public static String toRole(String authority) {
        if (StringUtils.isNotBlank(authority) && authority.startsWith(ROLE_PREFIX)) {
            return authority.substring(ROLE_PREFIX.length());
        }
        return authority;
    }

    public static String fromRole(String role) {
        return ROLE_PREFIX + role;
    }

    public static String toScope(String authority) {
        if (StringUtils.isNotBlank(authority) && authority.startsWith(SCOPE_PREFIX)) {
            return authority.substring(SCOPE_PREFIX.length());
        }
        return authority;
    }

    public static String adminRole() {
        return toRole(ADMIN);
    }

    public static String bizRole() {
        return toRole(BIZ_ADMIN);
    }

    public static String orgRole() {
        return toRole(ORG_ADMIN);
    }

    public static String resRole() {
        return toRole(RES_ADMIN);
    }

    public static String userRole() {
        return toRole(USER);
    }

    public static String clientRole() {
        return toRole(CLIENT);
    }

    public static int getPriority(String authority) {
        return switch (authority) {
            case ADMIN -> 5;
            case BIZ_ADMIN -> 4;
            case ORG_ADMIN -> 3;
            case RES_ADMIN -> 2;
            case USER -> 1;
            default -> 0;
        };
    }

    public static int getPriorityOfRole(String role) {
        return getPriority(fromRole(role));
    }
}
