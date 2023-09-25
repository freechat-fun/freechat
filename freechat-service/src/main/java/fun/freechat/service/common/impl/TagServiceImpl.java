package fun.freechat.service.common.impl;

import fun.freechat.mapper.TagDynamicSqlSupport;
import fun.freechat.mapper.TagMapper;
import fun.freechat.model.Tag;
import fun.freechat.model.User;
import fun.freechat.service.common.TagService;
import fun.freechat.service.enums.InfoType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Service
@SuppressWarnings("unused")
public class TagServiceImpl implements TagService {
    @Autowired
    private TagMapper tagMapper;

    @Override
    public boolean create(User user, InfoType referType, String referId, String content) {
        Tag existedTag = tagMapper.selectOne(c ->
                c.where(TagDynamicSqlSupport.referType, isEqualTo(referType.text()))
                        .and(TagDynamicSqlSupport.referId, isEqualTo(referId))
                        .and(TagDynamicSqlSupport.content, isEqualTo(content)))
                .orElse(null);
        if (Objects.nonNull(existedTag)) {
            return true;
        }
        Date now = new Date();
        Tag newTag = new Tag()
                .withGmtCreate(now)
                .withGmtModified(now)
                .withUserId(user.getUserId())
                .withReferType(referType.text())
                .withReferId(referId)
                .withContent(content);

        int rows = tagMapper.insert(newTag);
        return rows > 0;
    }

    @Override
    public boolean delete(User user, InfoType referType, String referId, String content) {
        int rows = tagMapper.delete(c ->
                c.where(TagDynamicSqlSupport.referType, isEqualTo(referType.text()))
                        .and(TagDynamicSqlSupport.referId, isEqualTo(referId))
                        .and(TagDynamicSqlSupport.content, isEqualTo(content)));
        return rows > 0;
    }
}
