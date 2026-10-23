package fun.freechat.mapper;

import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

/** Database clock for replica-independent skill admission, expiry and garbage collection. */
@Mapper
public interface SkillCoordinationMapper {
    @Select("SELECT UTC_TIMESTAMP(6)")
    @Options(useCache = false, flushCache = Options.FlushCachePolicy.TRUE)
    LocalDateTime databaseNow();
}
