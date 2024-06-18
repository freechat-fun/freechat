package fun.freechat.service.account;

import fun.freechat.model.User;
import fun.freechat.service.enums.ApiTokenType;
import fun.freechat.util.PolicyUtils;

import java.time.temporal.TemporalAmount;
import java.util.List;

public interface SysApiTokenService {
    String create(User user, ApiTokenType type, String policy, TemporalAmount duration);
    default String create(User user, TemporalAmount duration) {
        return create(user, ApiTokenType.ACCESS, PolicyUtils.all(), duration);
    }
    default String create(User user) {
        return create(user, null);
    }
    List<MaskedApiToken> list(User user);
    String getById(Long id);
    boolean deleteByUserId(String userId);
    boolean delete(String token);
    boolean deleteById(Long id);
    boolean disable(String token);
    boolean disableById(Long id);
    boolean isEnabled(String token);
    User getUser(String token);
    String getOwner(String token);
    String getOwner(Long id);
}
