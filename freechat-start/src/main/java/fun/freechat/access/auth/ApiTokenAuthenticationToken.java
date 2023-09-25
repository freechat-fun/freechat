package fun.freechat.access.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import fun.freechat.access.user.SysUserDetails;
import fun.freechat.util.AuthorityUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ApiTokenAuthenticationToken extends AbstractAuthenticationToken {
    private final Set<String> tokens;

    private SysUserDetails sysUser;

    private static List<GrantedAuthority> getAuthorities(SysUserDetails sysUser) {
        List<GrantedAuthority> authorities = new LinkedList<>(sysUser.getAuthorities());
        authorities.add(new SimpleGrantedAuthority(AuthorityUtils.CLIENT));
        return authorities;
    }

    public ApiTokenAuthenticationToken(SysUserDetails sysUser, Set<String> tokens) {
        super(getAuthorities(sysUser));
        this.sysUser = sysUser;
        this.tokens = tokens;
        setAuthenticated(true);
    }

    public ApiTokenAuthenticationToken(Set<String> tokens) {
        super(null);
        this.tokens = tokens;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return tokens;
    }

    @Override
    public Object getPrincipal() {
        return sysUser;
    }
}
