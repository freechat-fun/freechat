package fun.freechat.service.common.impl;

import fun.freechat.mapper.*;
import fun.freechat.model.HotTag;
import fun.freechat.model.Tag;
import fun.freechat.model.User;
import fun.freechat.service.common.TagService;
import fun.freechat.service.enums.InfoType;
import fun.freechat.service.enums.Visibility;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
@SuppressWarnings("unused")
public class TagServiceImpl implements TagService {
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private HotTagMapper hotTagMapper;

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

    @Override
    public List<Tag> list(User user, InfoType referType) {
        return tagMapper.select(c -> c.where(TagDynamicSqlSupport.userId, isEqualTo(user.getUserId()))
                .and(TagDynamicSqlSupport.referType, isEqualTo(referType.text())));
    }

    @Override
    public List<HotTag> listHot(InfoType referType, String text, Long limit) {
        var statement = select(
                TagDynamicSqlSupport.content,
                count(TagDynamicSqlSupport.content).as("count"))
                .from(TagDynamicSqlSupport.tag);

        SqlColumn<String> visibilityColumn;
        switch (referType) {
            case CHARACTER -> {
                statement.leftJoin(CharacterInfoDynamicSqlSupport.characterInfo, "i")
                        .on(TagDynamicSqlSupport.referId, equalTo(CharacterInfoDynamicSqlSupport.characterId));
                visibilityColumn = CharacterInfoDynamicSqlSupport.visibility;
            }
            case PROMPT -> {
                statement.leftJoin(PromptInfoDynamicSqlSupport.promptInfo, "i")
                        .on(TagDynamicSqlSupport.referId, equalTo(PromptInfoDynamicSqlSupport.promptId));
                visibilityColumn = PromptInfoDynamicSqlSupport.visibility;
            }
            case AGENT -> {
                statement.leftJoin(AgentInfoDynamicSqlSupport.agentInfo, "i")
                        .on(TagDynamicSqlSupport.referId, equalTo(AgentInfoDynamicSqlSupport.agentId));
                visibilityColumn = AgentInfoDynamicSqlSupport.visibility;
            }
            case PLUGIN -> {
                statement.leftJoin(PluginInfoDynamicSqlSupport.pluginInfo, "i")
                        .on(TagDynamicSqlSupport.referId, equalTo(PluginInfoDynamicSqlSupport.pluginId));
                visibilityColumn = PluginInfoDynamicSqlSupport.visibility;
            }
            default -> {
                return Collections.emptyList();
            }
        }
        statement.where(TagDynamicSqlSupport.content, isNotNull())
                .and(TagDynamicSqlSupport.content,
                        isLike(text).filter(StringUtils::isNotBlank).map(s -> "%" + s + "%"))
                .and(visibilityColumn, isEqualTo(Visibility.PUBLIC.text()))
                .groupBy(TagDynamicSqlSupport.content)
                .orderBy(sortColumn("count").descending());
        Optional.ofNullable(limit).filter(l -> l > 0).ifPresent(statement::limit);
        return hotTagMapper.selectHotTags(statement.build().render(RenderingStrategies.MYBATIS3));
    }
}
