package fun.freechat.service.common;

import fun.freechat.model.User;
import fun.freechat.service.enums.ConfigFormat;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

public interface ConfigService {
    Integer publish(User user, String name, ConfigFormat format, String content);
    Triple<String, ConfigFormat, Integer> load(String name);
    Pair<String, ConfigFormat> load(String name, Integer version);
    List<String> listNames();
}
