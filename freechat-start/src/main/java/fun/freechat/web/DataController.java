package fun.freechat.web;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.FileUtils;
import fun.freechat.service.common.FileStore;
import fun.freechat.service.util.StoreUtils;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.springframework.http.MediaType.*;

@Controller
@Slf4j
@SuppressWarnings("unused")
public class DataController {
    private static final List<String> AVAILABLE_IMAGE_TYPES = List.of(
            IMAGE_GIF_VALUE, IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE
    );

    @Autowired
    private Tika tika;

    @GetMapping(value = "/public/image/{key}", produces = {IMAGE_GIF_VALUE, IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE})
    @ResponseBody
    public ResponseEntity<Resource> getPublicImage(@PathVariable("key") @NotBlank String key) {
        FileStore fileStore = StoreUtils.defaultFileStore();
        try {
            String pathStr = FileUtils.getDefaultPublicPath(key);
            Path path = fileStore.toPath(pathStr);
            Resource resource = new PathResource(path);
            HttpHeaders headers = getResponseHeaders(pathStr, IMAGE_JPEG, AVAILABLE_IMAGE_TYPES);

            return ResponseEntity.ok().headers(headers).body(resource);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping(value = "/my/document/{key}", produces = ALL_VALUE)
    @ResponseBody
    public ResponseEntity<Resource> getPrivateDocument(@PathVariable("key") @NotBlank String key) {
        FileStore fileStore = StoreUtils.defaultFileStore();
        try {
            String pathStr = FileUtils.getDefaultPrivatePath(key, AccountUtils.currentUser().getUserId());
            Path path = fileStore.toPath(pathStr);
            Resource resource = new FileUrlResource(path.toString());
            HttpHeaders headers = getResponseHeaders(pathStr, APPLICATION_OCTET_STREAM, null);

            return ResponseEntity.ok().headers(headers).body(resource);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    private HttpHeaders getResponseHeaders(String path,
                                           MediaType defaultMimeType,
                                           List<String> availableTypes) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        FileStore fileStore = StoreUtils.defaultFileStore();
        String mimeType = null;

        try {
            mimeType = tika.detect(fileStore.toPath(path));
            if (CollectionUtils.isEmpty(availableTypes) || availableTypes.contains(mimeType)) {
                headers.setContentType(MediaType.parseMediaType(mimeType));
            } else {
                headers.setContentType(defaultMimeType);
            }
        } catch (IOException ex) {
            log.warn("Failed to read {}", path, ex);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to find resource");
        } catch (InvalidMediaTypeException ex1) {
            log.warn("Unknown mime-type: {}", mimeType, ex1);
            headers.setContentType(defaultMimeType);
        }

        return headers;
    }
}
