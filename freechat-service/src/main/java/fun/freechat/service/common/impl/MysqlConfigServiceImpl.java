package fun.freechat.service.common.impl;

import fun.freechat.model.NonRealTimeConfig;
import fun.freechat.model.User;
import fun.freechat.service.common.ConfigService;
import fun.freechat.service.enums.ConfigFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service("mysqlConfigService")
@SuppressWarnings("unused")
public class MysqlConfigServiceImpl implements ConfigService {
    @Autowired
    private MysqlConfigRepo configRepo;

    @Override
    public Integer publish(User user, String name, ConfigFormat format, String content) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return configRepo.publish(name, format.text(), content, user.getUserId());
    }

    @Override
    public Triple<String, ConfigFormat, Integer> load(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        NonRealTimeConfig config = configRepo.load(name);
        if (Objects.isNull(config)) {
            return null;
        }
        return Triple.of(Objects.isNull(config.getContent()) ? "" : config.getContent(),
                ConfigFormat.of(config.getFormat()), config.getVersion());
    }

    @Override
    public Pair<String, ConfigFormat> load(String name, Integer version) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        NonRealTimeConfig config = configRepo.load(name, version);
        if (Objects.isNull(config)) {
            return null;
        }
        return Pair.of(Objects.isNull(config.getContent()) ? "" : config.getContent(),
                ConfigFormat.of(config.getFormat()));
    }

    @Override
    public List<String> listNames() {
        return configRepo.listNames();
    }
}
