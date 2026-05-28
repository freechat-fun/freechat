package fun.freechat.service.chat;

import fun.freechat.model.TgUser;
import java.util.Optional;

public interface TgUserService {

    TgUser getOrCreate(String backendId, Long tgUserId, String username, String firstName, String lastName);

    Optional<TgUser> findByBackendAndUser(String backendId, Long tgUserId);

    boolean update(TgUser user);

    boolean delete(Long id);
}
