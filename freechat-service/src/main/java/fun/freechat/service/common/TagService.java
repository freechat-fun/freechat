package fun.freechat.service.common;

import fun.freechat.model.HotTag;
import fun.freechat.model.Tag;
import fun.freechat.model.User;
import fun.freechat.service.enums.InfoType;

import java.util.List;

public interface TagService {
    boolean create(User user, InfoType referType, String referId, String content);
    boolean delete(User user, InfoType referType, String referId, String content);
    List<Tag> list(User user, InfoType referType);
    default List<HotTag> listHot(InfoType referType, Long limit) {
        return listHot(referType, null, limit);
    }
    List<HotTag> listHot(InfoType referType, String text, Long limit);
}
