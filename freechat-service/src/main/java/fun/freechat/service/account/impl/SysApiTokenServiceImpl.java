package fun.freechat.service.account.impl;

import fun.freechat.mapper.ApiTokenDynamicSqlSupport;
import fun.freechat.mapper.ApiTokenMapper;
import fun.freechat.model.ApiToken;
import fun.freechat.model.User;
import fun.freechat.service.enums.ApiTokenType;
import fun.freechat.service.account.MaskedApiToken;
import fun.freechat.service.account.SysApiTokenService;
import fun.freechat.util.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.temporal.TemporalAmount;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Service
@SuppressWarnings("unused")
public class SysApiTokenServiceImpl implements SysApiTokenService {
    @Value("${auth.token.prefix:#{null}}")
    private String prefix;
    @Value("${auth.token.limits:#{null}}")
    private Integer maxCount;
    @Autowired
    private SysApiTokenRepo apiTokenRepo;
    @Autowired
    private ApiTokenMapper apiTokenMapper;

    @Override
    public String create(User user, ApiTokenType type, String policy, TemporalAmount duration) {
        if (Objects.nonNull(maxCount) && list(user).size() >= maxCount) {
            return null;
        }
        Date now = new Date();
        String token = prefix == null ? "" : prefix;
        token += IdUtils.newId();
        int rows = apiTokenMapper.insertSelective(new ApiToken()
                .withGmtCreate(now)
                .withGmtModified(now)
                .withIssuedAt(now)
                .withExpiresAt(Objects.nonNull(duration) ? Date.from(now.toInstant().plus(duration)) : null)
                .withPolicy(policy)
                .withType(type.getType())
                .withUserId(user.getUserId())
                .withToken(token));
        return rows > 0 ? token : null;
    }

    @Override
    public List<MaskedApiToken> list(User user) {
        return apiTokenMapper.select(c -> c.where(ApiTokenDynamicSqlSupport.userId, isEqualTo(user.getUserId())))
                .stream()
                .filter(this::isEnabled)
                .map(MaskedApiToken::of)
                .toList();
    }

    @Override
    public String getById(Long id) {
        ApiToken apiToken = apiTokenRepo.getApiTokenById(id);
        return Objects.nonNull(apiToken) ? apiToken.getToken() : null;
    }

    @Override
    public boolean deleteByUserId(String userId) {
        int rows = apiTokenMapper.delete(c ->
                c.where(ApiTokenDynamicSqlSupport.userId, isEqualTo(userId)));
        return rows > 0;
    }

    @Override
    public boolean delete(String token) {
        int rows = apiTokenMapper.delete(c -> c.where(ApiTokenDynamicSqlSupport.token, isEqualTo(token)));
        if (rows > 0 ) {
            apiTokenRepo.onDelete(token);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteById(Long id) {
        int rows = apiTokenMapper.deleteByPrimaryKey(id);
        if (rows > 0 ) {
            apiTokenRepo.onDeleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean disable(String token) {
        int rows = 0;
        ApiToken apiToken = apiTokenRepo.getApiToken(token);
        if (Objects.nonNull(apiToken)) {
            apiToken.setExpiresAt(apiToken.getIssuedAt());
            rows = apiTokenMapper.updateByPrimaryKeySelective(apiToken);
        }
        if (rows > 0) {
            apiTokenRepo.onDisable(token);
            return true;
        }
        return false;
    }

    @Override
    public boolean disableById(Long id) {
        int rows = 0;
        ApiToken apiToken = apiTokenRepo.getApiTokenById(id);
        if (Objects.nonNull(apiToken)) {
            apiToken.setExpiresAt(apiToken.getIssuedAt());
            rows = apiTokenMapper.updateByPrimaryKeySelective(apiToken);
        }
        if (rows > 0) {
            apiTokenRepo.onDisableById(id);
            return true;
        }
        return false;
    }

    private boolean isEnabled(ApiToken apiToken) {
        Date now = new Date();
        return Objects.nonNull(apiToken) &&
                apiToken.getIssuedAt().before(now) &&
                (apiToken.getExpiresAt() == null || apiToken.getExpiresAt().after(now));
    }

    @Override
    public boolean isEnabled(String token) {
        return isEnabled(apiTokenRepo.getApiToken(token));
    }

    @Override
    public User getUser(String token) {
        return isEnabled(token) ? apiTokenRepo.getUser(token) : null;
    }

    @Override
    public String getOwner(String token) {
        ApiToken apiToken = apiTokenRepo.getApiToken(token);
        return Objects.nonNull(apiToken) ? apiToken.getUserId() : null;
    }

    @Override
    public String getOwner(Long id) {
        ApiToken apiToken = apiTokenRepo.getApiTokenById(id);
        return Objects.nonNull(apiToken) ? apiToken.getUserId() : null;
    }
}
