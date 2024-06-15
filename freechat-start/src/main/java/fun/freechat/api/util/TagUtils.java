package fun.freechat.api.util;

import fun.freechat.model.Tag;
import fun.freechat.service.common.TagService;
import fun.freechat.service.enums.InfoType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TagUtils  implements ApplicationContextAware {
    private static TagService tagService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        tagService = applicationContext.getBean(TagService.class);
    }

    public static List<String> getTags(InfoType referType, String referId) {
        return tagService.get(referType, referId)
                .stream()
                .map(Tag::getContent)
                .distinct()
                .toList();
    }
}
