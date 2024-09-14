package fun.freechat.service.account;

import fun.freechat.model.User;
import org.springframework.lang.NonNull;

import java.util.Set;

public interface SysAuthorityService {
    Set<String> list(@NonNull User user);
    boolean update(String userId, Set<String> authorities);
}
