package fun.freechat.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import fun.freechat.service.character.CharacterInfoDraft;
import fun.freechat.util.PojoUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.function.Function;

@Slf4j
public class CharacterUtils {
    public static CharacterInfoDraft getDraftInfo(String draft) {
        if (StringUtils.isBlank(draft)) {
            return null;
        }
        CharacterInfoDraft draftInfo = new CharacterInfoDraft();
        try {
            JsonNode rootNode = InfoUtils.defaultMapper().readTree(draft);
            Function<String, String> converter = key -> Optional.ofNullable(rootNode.get(key))
                    .filter(JsonNode::isTextual)
                    .map(JsonNode::asText)
                    .orElse(null);

            PojoUtils.mapWhenExists(() -> "profile", draftInfo::setProfile, converter);
            PojoUtils.mapWhenExists(() -> "greeting", draftInfo::setGreeting, converter);
            PojoUtils.mapWhenExists(() -> "chatStyle", draftInfo::setChatStyle, converter);
            PojoUtils.mapWhenExists(() -> "chatExample", draftInfo::setChatExample, converter);
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse draft: {}", draft);
        }
        return draftInfo;
    }
}
