package fun.freechat.service.account.impl;

import fun.freechat.service.account.SysAuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import fun.freechat.mapper.AuthorityDynamicSqlSupport;
import fun.freechat.mapper.AuthorityMapper;
import fun.freechat.model.Authority;
import fun.freechat.model.User;
import fun.freechat.util.AuthorityUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Service
@SuppressWarnings("unused")
public class SysAuthorityServiceImpl implements SysAuthorityService {
    @Autowired
    private AuthorityMapper authorityMapper;

    @Override
    public Set<String> list(@NonNull User user) {
        Set<String> authorities = new HashSet<>();
        authorities.add(AuthorityUtils.USER);
        authorityMapper.select(c -> c.where(AuthorityDynamicSqlSupport.userId, isEqualTo(user.getUserId())))
                .stream()
                .map(Authority::getScope)
                .forEach(authorities::add);

        return authorities;
    }

    @Override
    @Transactional
    public boolean update(User user, Set<String> authorities) {
        Date now = new Date();
        authorityMapper.delete(c -> c.where(AuthorityDynamicSqlSupport.userId, isEqualTo(user.getUserId())));
        for (String authority : authorities) {
            if (AuthorityUtils.USER.equalsIgnoreCase(authority)) {
                continue;
            }
            Authority row = new Authority()
                    .withUserId(user.getUserId())
                    .withGmtCreate(now)
                    .withGmtModified(now)
                    .withScope(authority);
             authorityMapper.insertSelective(row);
        }
        return true;
    }
}
