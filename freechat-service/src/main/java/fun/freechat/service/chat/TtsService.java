package fun.freechat.service.chat;

import fun.freechat.service.enums.TtsSpeakerType;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public interface TtsService {
    CompletableFuture<InputStream> speak(
            String speaker, TtsSpeakerType speakerType, String text, String lang);
    void uploadVoice(String path);
    void deleteVoice(String name);
    boolean existsVoice(String name);
    boolean isEnabled();
}
