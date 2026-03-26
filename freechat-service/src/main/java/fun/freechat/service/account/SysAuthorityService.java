package fun.freechat.service.account;

import fun.freechat.model.User;
import java.util.Set;
import org.springframework.lang.NonNull;

public interface SysAuthorityService {
    Set<String> list(@NonNull User user);

    boolean update(String userId, Set<String> authorities);
}
