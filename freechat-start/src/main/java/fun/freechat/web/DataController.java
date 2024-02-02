package fun.freechat.web;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.service.common.FileStore;
import fun.freechat.service.util.StoreUtils;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import static fun.freechat.api.util.FileUtils.PRIVATE_DIR;
import static fun.freechat.api.util.FileUtils.PUBLIC_DIR;
import static org.springframework.http.MediaType.*;

@Controller
@Slf4j
@SuppressWarnings("unused")
public class DataController {
    private static final List<String> AVAILABLE_IMAGE_TYPES = List.of(
            IMAGE_GIF_VALUE, IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE
    );

    @GetMapping(value = "/public/image/{key}", produces = {IMAGE_GIF_VALUE, IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE})
    @ResponseBody
    public ResponseEntity<byte[]> getPublicImage(@PathVariable("key") @NotBlank String key) {
        FileStore fileStore = StoreUtils.defaultFileStore();
        try {
            byte[] imageData = fileStore.readBytes(PUBLIC_DIR +
                    new String(Base64.getUrlDecoder().decode(key), StandardCharsets.UTF_8));

            return new ResponseEntity<>(imageData, getImageHeaders(imageData), HttpStatus.OK);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping(value = "/my/image/{key}", produces = {IMAGE_GIF_VALUE, IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE})
    @ResponseBody
    public ResponseEntity<byte[]> getPrivateImage(@PathVariable("key") @NotBlank String key) {
        FileStore fileStore = StoreUtils.defaultFileStore();
        try {
            byte[] imageData = fileStore.readBytes(PRIVATE_DIR + AccountUtils.currentUser().getUserId() + "/" +
                    new String(Base64.getUrlDecoder().decode(key), StandardCharsets.UTF_8));

            return new ResponseEntity<>(imageData, getImageHeaders(imageData), HttpStatus.OK);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    private HttpHeaders getImageHeaders(byte[] imageData) throws IOException {
        String mimeType = IMAGE_JPEG_VALUE;
        if (Objects.nonNull(imageData)) {
            String type = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(imageData));
            if (StringUtils.isNotBlank(type) && AVAILABLE_IMAGE_TYPES.contains(type)) {
                mimeType = type;
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(mimeType));

        return headers;
    }
}
