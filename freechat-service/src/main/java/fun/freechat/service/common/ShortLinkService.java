package fun.freechat.service.common;

import java.time.temporal.TemporalAmount;

public interface ShortLinkService {
    String shorten(String path, TemporalAmount duration);
    default String shorten(String path) {
        return shorten(path, null);
    }
    String extract(String token);
}
