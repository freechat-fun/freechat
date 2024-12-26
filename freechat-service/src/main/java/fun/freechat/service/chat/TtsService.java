package fun.freechat.service.chat;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public interface TtsService {
    CompletableFuture<InputStream> playBySpeakerIdx(String speaker, String text, String lang);
    CompletableFuture<InputStream> playBySpeakerWav(String wav, String text, String lang);
}
