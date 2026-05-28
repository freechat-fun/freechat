package fun.freechat.service.chat.impl;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.select;

import fun.freechat.mapper.TgMessageDynamicSqlSupport;
import fun.freechat.mapper.TgMessageMapper;
import fun.freechat.model.TgMessage;
import fun.freechat.service.chat.TgMessageService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TgMessageServiceImpl implements TgMessageService {

    private static final int DEFAULT_LIMIT = 100;

    private final TgMessageMapper tgMessageMapper;

    @Override
    public Long record(
            String chatId,
            Long tgMessageId,
            Long tgUserId,
            String direction,
            String messageType,
            String content,
            String payload) {
        LocalDateTime now = LocalDateTime.now();
        TgMessage message = new TgMessage()
                .withGmtCreate(now)
                .withGmtModified(now)
                .withChatId(chatId)
                .withTgMessageId(tgMessageId)
                .withTgUserId(tgUserId)
                .withDirection(direction)
                .withMessageType(messageType)
                .withContent(content)
                .withPayload(payload);
        tgMessageMapper.insertSelective(message);
        return message.getId();
    }

    @Override
    public List<TgMessage> listByChat(String chatId, Integer limit, Integer offset) {
        int actualLimit = limit != null && limit > 0 ? limit : DEFAULT_LIMIT;
        long actualOffset = offset != null && offset > 0 ? offset : 0L;
        var statement = select(TgMessageMapper.selectList)
                .from(TgMessageDynamicSqlSupport.tgMessage)
                .where(TgMessageDynamicSqlSupport.chatId, isEqualTo(chatId))
                .orderBy(TgMessageDynamicSqlSupport.id.descending())
                .limit(actualLimit)
                .offset(actualOffset)
                .build()
                .render(RenderingStrategies.MYBATIS3);
        return tgMessageMapper.selectMany(statement);
    }
}
