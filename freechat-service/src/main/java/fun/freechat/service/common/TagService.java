package fun.freechat.service.common;

import fun.freechat.model.User;
import fun.freechat.service.enums.InfoType;

public interface TagService {
    boolean create(User user, InfoType referType, String referId, String content);
    boolean delete(User user, InfoType referType, String referId, String content);
}
