package fun.freechat.service.account;

import fun.freechat.model.User;

import java.util.List;

@SuppressWarnings("UnusedReturnValue")
public interface SysUserService {
    boolean create(User user);
    boolean update(User user);
    boolean delete(String username);
    boolean deleteByUserId(String userId);
    boolean exists(String username);
    boolean changePassword(User user, String newPassword);
    User loadByUserId(String userId);
    User loadByUsername(String username);
    User loadByUsernameAndPassword(String username, String password);
    User loadByUsernameAndPlatform(String username, String platform);
    List<User> list(long limit, long offset);
}
