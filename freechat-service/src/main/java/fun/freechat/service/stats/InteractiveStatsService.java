package fun.freechat.service.stats;

import fun.freechat.model.InteractiveStats;
import fun.freechat.model.InteractiveStatsScoreDetails;
import fun.freechat.service.enums.InfoType;
import fun.freechat.service.enums.StatsType;

import java.util.List;

public interface InteractiveStatsService {
    long add(String userId, InfoType infoType, String infoId, StatsType statsType, long delta);
    InteractiveStats get(InfoType infoType, String infoId);
    InteractiveStatsScoreDetails getScore(String userId, InfoType infoType, String infoId);
    List<InteractiveStats> list(
            InfoType infoType, StatsType statsType, Long limit, Long offset, boolean desc);
}
