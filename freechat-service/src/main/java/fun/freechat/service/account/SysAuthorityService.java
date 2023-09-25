package fun.freechat.service.account;

import org.springframework.lang.NonNull;
import fun.freechat.model.User;

import java.util.Set;

public interface SysAuthorityService {
    Set<String> list(@NonNull User user);

    boolean update(User user, Set<String> authorities);
}
