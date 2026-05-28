package fun.freechat.service.chat.impl;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.mapper.TgChatDynamicSqlSupport;
import fun.freechat.mapper.TgChatMapper;
import fun.freechat.model.TgChat;
import fun.freechat.service.chat.TgChatService;
import fun.freechat.util.IdUtils;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TgChatServiceImpl implements TgChatService {

    private final TgChatMapper tgChatMapper;

    @Override
    public TgChat getOrCreate(String backendId, Long tgChatId, String chatType, String title) {
        return findByBackendAndChat(backendId, tgChatId).orElseGet(() -> {
            LocalDateTime now = LocalDateTime.now();
            TgChat chat = new TgChat()
                    .withChatId(IdUtils.newId())
                    .withGmtCreate(now)
                    .withGmtModified(now)
                    .withBackendId(backendId)
                    .withTgChatId(tgChatId)
                    .withChatType(chatType)
                    .withTitle(title);
            tgChatMapper.insertSelective(chat);
            return chat;
        });
    }

    @Override
    public Optional<TgChat> findByBackendAndChat(String backendId, Long tgChatId) {
        return tgChatMapper.selectOne(c -> c.where(TgChatDynamicSqlSupport.backendId, isEqualTo(backendId))
                .and(TgChatDynamicSqlSupport.tgChatId, isEqualTo(tgChatId)));
    }

    @Override
    public boolean delete(String chatId) {
        return tgChatMapper.deleteByPrimaryKey(chatId) > 0;
    }
}
