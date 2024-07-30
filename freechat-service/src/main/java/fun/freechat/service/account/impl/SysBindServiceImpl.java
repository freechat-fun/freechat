package fun.freechat.service.account.impl;

import fun.freechat.service.account.SysBindService;
import fun.freechat.mapper.BindingDynamicSqlSupport;
import fun.freechat.mapper.BindingMapper;
import fun.freechat.model.Binding;
import fun.freechat.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Service
@SuppressWarnings("unused")
public class SysBindServiceImpl implements SysBindService {
    @Autowired
    private BindingMapper bindingMapper;

    private String arrayToString(Collection<String> arr) {
        if (CollectionUtils.isEmpty(arr)) {
            return null;
        }
        return String.join(",", arr);
    }
    @Override
    public boolean bind(User user, String platform, String sub, URL iss, Collection<String> aud,
                        String refreshToken, Date issuedAt, Date expiresAt) {
        Date now = new Date();
        Binding binding = new Binding()
                .withGmtCreate(now)
                .withGmtModified(now)
                .withUserId(user.getUserId())
                .withPlatform(platform)
                .withSub(sub)
                .withIss(Objects.nonNull(iss) ? iss.toExternalForm() : null)
                .withAud(arrayToString(aud))
                .withRefreshToken(refreshToken)
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt);

        Binding oldBinding = bindingMapper.selectOne(c ->
                        c.where(BindingDynamicSqlSupport.userId, isEqualTo(user.getUserId()))
                                .and(BindingDynamicSqlSupport.platform, isEqualTo(platform)))
                .orElse(null);

        int rows;
        if (Objects.nonNull(oldBinding)) {
            binding.setGmtCreate(oldBinding.getGmtCreate());
            binding.setId(oldBinding.getId());
            rows = bindingMapper.updateByPrimaryKeySelective(binding);
        } else {
            rows = bindingMapper.insertSelective(binding);
        }

        return rows > 0;
    }

    @Override
    public boolean unbind(User user, String platform) {
        int rows = bindingMapper.delete(c -> c.where(BindingDynamicSqlSupport.userId, isEqualTo(user.getUserId()))
                .and(BindingDynamicSqlSupport.platform, isEqualTo(platform)));
        return rows > 0;
    }

    @Override
    public boolean isBound(User user, String platform) {
        return bindingMapper.selectOne(c -> c.where(BindingDynamicSqlSupport.userId, isEqualTo(user.getUserId()))
                        .and(BindingDynamicSqlSupport.platform, isEqualTo(platform)))
                .filter(binding -> {
                    Date expiresAt = binding.getExpiresAt();
                    return expiresAt == null || expiresAt.after(new Date());
                })
                .isPresent();
    }

    @Override
    public String getRefreshToken(User user, String platform) {
        return bindingMapper.selectOne(c -> c.where(BindingDynamicSqlSupport.userId, isEqualTo(user.getUserId()))
                        .and(BindingDynamicSqlSupport.platform, isEqualTo(platform)))
                .map(Binding::getRefreshToken)
                .orElse(null);
    }

    @Override
    public String getUserIdByPlatformAndSub(String platform, String sub) {
        return bindingMapper.selectOne(c -> c.where(BindingDynamicSqlSupport.sub, isEqualTo(sub))
                        .and(BindingDynamicSqlSupport.platform, isEqualTo(platform)))
                .map(Binding::getUserId)
                .orElse(null);
    }
}
