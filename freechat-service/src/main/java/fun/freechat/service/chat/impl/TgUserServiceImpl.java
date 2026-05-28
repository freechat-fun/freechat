package fun.freechat.service.chat.impl;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.mapper.TgUserDynamicSqlSupport;
import fun.freechat.mapper.TgUserMapper;
import fun.freechat.model.TgUser;
import fun.freechat.service.chat.TgUserService;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TgUserServiceImpl implements TgUserService {

    private final TgUserMapper tgUserMapper;

    @Override
    public TgUser getOrCreate(String backendId, Long tgUserId, String username, String firstName, String lastName) {
        return findByBackendAndUser(backendId, tgUserId)
                .map(existing -> refreshDisplay(existing, username, firstName, lastName))
                .orElseGet(() -> createNew(backendId, tgUserId, username, firstName, lastName));
    }

    @Override
    public Optional<TgUser> findByBackendAndUser(String backendId, Long tgUserId) {
        return tgUserMapper.selectOne(c -> c.where(TgUserDynamicSqlSupport.backendId, isEqualTo(backendId))
                .and(TgUserDynamicSqlSupport.tgUserId, isEqualTo(tgUserId)));
    }

    @Override
    public boolean update(TgUser user) {
        return tgUserMapper.updateByPrimaryKeySelective(user.withGmtModified(LocalDateTime.now())) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return tgUserMapper.deleteByPrimaryKey(id) > 0;
    }

    private TgUser refreshDisplay(TgUser existing, String username, String firstName, String lastName) {
        boolean changed = false;
        if (username != null && !Objects.equals(username, existing.getUsername())) {
            existing.setUsername(username);
            changed = true;
        }
        if (firstName != null && !Objects.equals(firstName, existing.getFirstName())) {
            existing.setFirstName(firstName);
            changed = true;
        }
        if (lastName != null && !Objects.equals(lastName, existing.getLastName())) {
            existing.setLastName(lastName);
            changed = true;
        }
        if (changed) {
            existing.setGmtModified(LocalDateTime.now());
            tgUserMapper.updateByPrimaryKeySelective(existing);
        }
        return existing;
    }

    private TgUser createNew(String backendId, Long tgUserId, String username, String firstName, String lastName) {
        LocalDateTime now = LocalDateTime.now();
        TgUser user = new TgUser()
                .withGmtCreate(now)
                .withGmtModified(now)
                .withBackendId(backendId)
                .withTgUserId(tgUserId)
                .withUsername(username)
                .withFirstName(firstName)
                .withLastName(lastName);
        tgUserMapper.insertSelective(user);
        return user;
    }
}
