package fun.freechat.service.account;

import fun.freechat.model.User;
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

    List<String> list(User user);
    boolean deleteByUserId(String userId);

    boolean delete(String token);

    boolean disable(String token);

    boolean isEnabled(String token);

    User getUser(String token);
}
