package fun.freechat.service.account.impl;

import fun.freechat.mapper.UserDynamicSqlSupport;
import fun.freechat.mapper.UserMapper;
import fun.freechat.model.User;
import fun.freechat.service.account.SysUserService;
import fun.freechat.service.cache.MiddlePeriodCache;
import fun.freechat.service.cache.MiddlePeriodCacheEvict;
import fun.freechat.service.common.EncryptionService;
import fun.freechat.util.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Service
@SuppressWarnings("unused")
public class SysUserServiceImpl implements SysUserService {
    private final static String CACHE_KEY = "'SysUserServiceImpl_' + #p0";
    private final static String CACHE_KEY_FROM_USER = CACHE_KEY + ".username";

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private EncryptionService encryptionService;

    @Override
    public boolean create(User user) {
        Date now = new Date();
        String plainPassword = user.getPassword();
        String encryptedPassword = encryptionService.encrypt(plainPassword);
        int rows = userMapper.insertSelective(user
                .withGmtCreate(now)
                .withGmtModified(now)
                .withPassword(encryptedPassword)
                .withUserId(IdUtils.newId()));

        user.setPassword(plainPassword);

        if (rows == 0) {
            user.withUserId(null)
                    .withGmtCreate(null)
                    .withGmtModified(null);
            return false;
        }

        if (user.getPlatform() == null) {
            user.setPlatform("system");
        }
        if (user.getEmailVerified() == null) {
            user.setEmailVerified((byte)0);
        }
        if (user.getPhoneNumberVerified() == null) {
            user.setPhoneNumberVerified((byte)0);
        }
        if (user.getEnabled() == null) {
            user.setEnabled((byte)1);
        }
        if (user.getLocked() == null) {
            user.setLocked((byte)0);
        }
        return true;
    }

    @Override
    @MiddlePeriodCacheEvict(keyBy = CACHE_KEY_FROM_USER)
    public boolean update(User user) {
        Date now = new Date();
        String password = user.getPassword();
        int rows = userMapper.updateByPrimaryKeySelective(
                user.withGmtModified(now).withPassword(null));
        user.setPassword(password);
        return rows > 0;
    }

    @Override
    @MiddlePeriodCacheEvict(keyBy = CACHE_KEY)
    public boolean delete(String username) {
        int rows = userMapper.delete(c -> c.where(UserDynamicSqlSupport.username, isEqualTo(username)));
        return rows > 0;
    }

    @Override
    public boolean deleteByUserId(String userId) {
        int rows = userMapper.deleteByPrimaryKey(userId);
        return rows > 0;
    }

    @Override
    public boolean exists(String username) {
        return Objects.nonNull(loadByUsername(username));
    }

    @Override
    public boolean changePassword(User user, String newPassword) {
        Date now = new Date();
        String encryptedPassword = encryptionService.encrypt(newPassword);
        int rows = userMapper.updateByPrimaryKeySelective(
                user.withGmtModified(now).withPassword(encryptedPassword));
        return rows > 0;
    }

    @Override
    @MiddlePeriodCache(keyBy = CACHE_KEY)
    public User loadByUserId(String userId) {
        return userMapper.selectOne(c ->
                        c.where(UserDynamicSqlSupport.userId, isEqualTo(userId))
                                .and(UserDynamicSqlSupport.enabled, isEqualTo((byte)1)))
                .map(user -> user.withPassword(encryptionService.decrypt(user.getPassword())))
                .orElse(null);
    }

    @Override
    @MiddlePeriodCache(keyBy = CACHE_KEY)
    public User loadByUsername(String username) {
        return userMapper.selectOne(c ->
                        c.where(UserDynamicSqlSupport.username, isEqualTo(username))
                                .and(UserDynamicSqlSupport.enabled, isEqualTo((byte)1)))
                .map(user -> user.withPassword(encryptionService.decrypt(user.getPassword())))
                .orElse(null);
    }

    @Override
    public User loadByUsernameAndPassword(String username, String password) {
        String encryptedPassword = encryptionService.encrypt(password);
        return userMapper.selectOne(c -> c.where(UserDynamicSqlSupport.username, isEqualTo(username))
                        .and(UserDynamicSqlSupport.password, isEqualTo(encryptedPassword))
                        .and(UserDynamicSqlSupport.enabled, isEqualTo((byte)1)))
                .map(user -> user.withPassword(encryptionService.decrypt(user.getPassword())))
                .orElse(null);
    }

    @Override
    public User loadByUsernameAndPlatform(String username, String platform) {
        return userMapper.selectOne(c -> c.where(UserDynamicSqlSupport.username, isEqualTo(username))
                        .and(UserDynamicSqlSupport.platform, isEqualTo(platform))
                        .and(UserDynamicSqlSupport.enabled, isEqualTo((byte)1)))
                .map(user -> user.withPassword(encryptionService.decrypt(user.getPassword())))
                .orElse(null);
    }

    @Override
    public List<User> list(long limit, long offset) {
        return userMapper.select(c -> {
            if (limit > 0) {
                c.limit(limit);
            }
            if (offset > 0) {
                c.offset(offset);
            }
            return c;
        }).stream()
                .peek(user -> user.setPassword(encryptionService.decrypt(user.getPassword())))
                .toList();
    }
}
