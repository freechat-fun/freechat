package fun.freechat.service.common.impl;

import fun.freechat.mapper.ShortLinkDynamicSqlSupport;
import fun.freechat.mapper.ShortLinkMapper;
import fun.freechat.model.ShortLink;
import fun.freechat.service.cache.LongPeriodCache;
import fun.freechat.service.common.ShortLinkService;
import fun.freechat.util.IdUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.TemporalAmount;
import java.util.Date;
import java.util.Optional;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service()
@Slf4j
@SuppressWarnings("unused")
public class ShortLinkServiceImpl implements ShortLinkService {
    @Autowired
    private ShortLinkMapper shortLinkMapper;

    @Override
    public String shorten(String path, TemporalAmount duration) {
        Date now = new Date();
        Optional<ShortLink> row = shortLinkMapper.selectOne(c ->
                c.where(ShortLinkDynamicSqlSupport.path, isEqualTo(path)));
        if (row.isPresent()) {
            ShortLink shortLink = row.get();
            if (duration == null && shortLink.getExpiresAt() == null) {
                return shortLink.getToken();
            } else if (shortLink.getExpiresAt() == null || shortLink.getExpiresAt().after(now)) {
                shortLink.setExpiresAt(duration != null ? Date.from(now.toInstant().plus(duration)) : null);
                shortLink.setGmtModified(now);
                shortLinkMapper.updateByPrimaryKey(shortLink);
                return shortLink.getToken();
            }
        }

        ShortLink newShortLink = new ShortLink()
                .withExpiresAt(duration != null ? Date.from(now.toInstant().plus(duration)) : null)
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
    public String getFullPath(String token) {
        return shortLinkMapper.selectOne(c -> c.where(ShortLinkDynamicSqlSupport.token, isEqualTo(token))
                .and(ShortLinkDynamicSqlSupport.expiresAt, isNull(), or(
                        ShortLinkDynamicSqlSupport.expiresAt, isGreaterThan(new Date())
                )))
                .map(ShortLink::getPath)
                .orElse(null);
    }
}
