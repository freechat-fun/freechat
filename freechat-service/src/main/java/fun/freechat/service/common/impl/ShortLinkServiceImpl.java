package fun.freechat.service.common.impl;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isGreaterThan;
import static org.mybatis.dynamic.sql.SqlBuilder.isNull;
import static org.mybatis.dynamic.sql.SqlBuilder.or;

import fun.freechat.mapper.ShortLinkDynamicSqlSupport;
import fun.freechat.mapper.ShortLinkMapper;
import fun.freechat.model.ShortLink;
import fun.freechat.service.cache.LongPeriodCache;
import fun.freechat.service.common.ShortLinkService;
import fun.freechat.util.IdUtils;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service()
@Slf4j
@SuppressWarnings("unused")
public class ShortLinkServiceImpl implements ShortLinkService {
    @Autowired
    private ShortLinkMapper shortLinkMapper;

    @Override
    public String shorten(String path, TemporalAmount duration) {
        LocalDateTime now = LocalDateTime.now();
        Optional<ShortLink> row =
                shortLinkMapper.selectOne(c -> c.where(ShortLinkDynamicSqlSupport.path, isEqualTo(path)));
        if (row.isPresent()) {
            ShortLink shortLink = row.get();
            if (duration == null && shortLink.getExpiresAt() == null) {
                return shortLink.getToken();
            } else if (shortLink.getExpiresAt() == null
                    || shortLink.getExpiresAt().isAfter(now)) {
                shortLink.setExpiresAt(duration != null ? now.plus(duration) : null);
                shortLink.setGmtModified(now);
                shortLinkMapper.updateByPrimaryKey(shortLink);
                return shortLink.getToken();
            }
        }

        ShortLink newShortLink = new ShortLink()
                .withExpiresAt(duration != null ? now.plus(duration) : null)
                .withGmtCreate(now)
                .withGmtModified(now)
                .withPath(path)
                .withToken(IdUtils.newShortId());

        shortLinkMapper.insert(newShortLink);

        if (newShortLink.getId() != null) {
            return newShortLink.getToken();
        }

        return path;
    }

    @Override
    @LongPeriodCache
    public String extract(String token) {
        return shortLinkMapper
                .selectOne(c -> c.where(ShortLinkDynamicSqlSupport.token, isEqualTo(token))
                        .and(
                                ShortLinkDynamicSqlSupport.expiresAt,
                                isNull(),
                                or(ShortLinkDynamicSqlSupport.expiresAt, isGreaterThan(LocalDateTime.now()))))
                .map(ShortLink::getPath)
                .orElse(null);
    }
}
