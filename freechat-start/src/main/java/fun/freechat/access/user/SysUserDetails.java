package fun.freechat.access.user;

import fun.freechat.model.User;
import fun.freechat.service.account.SysAuthorityService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

public class SysUserDetails extends User implements UserDetails {
    private List<? extends GrantedAuthority> authorities;

    private SysUserDetails() {}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return Optional.ofNullable(getExpiresAt())
                .map(expiresAt -> expiresAt.after(new Date()))
                .orElse(Boolean.TRUE);
    }

    @Override
    public boolean isAccountNonLocked() {
        Byte locked = getLocked();
        return locked == null || locked == (byte) 0;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return Optional.ofNullable(getPasswordExpiresAt())
                .map(expiresAt -> expiresAt.after(new Date()))
                .orElse(Boolean.TRUE);
    }

    @Override
    public boolean isEnabled() {
        Byte enabled = getEnabled();
        return enabled == null || enabled == (byte) 1;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private transient User user;
        private transient PasswordEncoder passwordEncoder;
        private transient SysAuthorityService authorityService;

        public Builder fromUser(User user) {
            this.user = user;
            return this;
        }

        public Builder withPasswordEncoder(PasswordEncoder passwordEncoder) {
            this.passwordEncoder = passwordEncoder;
            return this;
        }

        public Builder withAuthorityService(SysAuthorityService authorityService) {
            this.authorityService = authorityService;
            return this;
        }

        public SysUserDetails build() {
            SysUserDetails sysUser = new SysUserDetails();

            BeanUtils.copyProperties(user, sysUser);

            if (passwordEncoder != null) {
                sysUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            if (authorityService != null) {
                sysUser.authorities = authorityService.list(user)
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();
            }

            return sysUser;
        }
    }
}
