package fun.freechat.web;

import fun.freechat.service.common.FileStore;
import fun.freechat.service.util.StoreUtils;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static fun.freechat.api.util.FileUtils.PUBLIC_DIR;
import static org.springframework.http.MediaType.*;

@Controller
@Slf4j
@SuppressWarnings("unused")
public class DataController {
    @GetMapping(value = "/public/image", produces = {IMAGE_GIF_VALUE, IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE})
    @ResponseBody
    public byte[] getImage(@RequestParam("key") @NotBlank String key) {
        FileStore fileStore = StoreUtils.defaultFileStore();
        try {
            return fileStore.readBytes(PUBLIC_DIR + key);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
