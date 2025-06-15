package fun.freechat.web;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.FileUtils;
import fun.freechat.service.common.FileStore;
import fun.freechat.service.util.StoreUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.MediaType.*;

@RestController
@Slf4j
@SuppressWarnings("unused")
public class DataController {
    private static final List<String> AVAILABLE_IMAGE_TYPES = List.of(
            IMAGE_GIF_VALUE, IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE
    );
    
    private static final List<String> AVAILABLE_VIDEO_TYPES = List.of(
            "video/mp4", "video/webm", "video/ogg", "video/quicktime", "video/x-msvideo",
            "video/x-flv", "video/3gpp", "video/3gpp2", "video/x-matroska", "video/mpeg", "video/x-m4v"
    );

    @Autowired
    private Tika tika;

    @GetMapping(value = "/public/image/{key}", produces = {IMAGE_GIF_VALUE, IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE})
    @CrossOrigin(originPatterns = "*")
    public ResponseEntity<Resource> getPublicImage(
            HttpServletRequest request,
            @PathVariable("key") @NotBlank String key) {
        FileStore fileStore = StoreUtils.defaultFileStore();
        try {
            String pathStr = FileUtils.getDefaultPublicPath(key);
            long lastModified = fileStore.getLastModifiedTime(pathStr);
            String eTag = "\"" + DigestUtils.md5Hex(String.valueOf(lastModified)) + "\"";

            String ifNoneMatch = request.getHeader(HttpHeaders.IF_NONE_MATCH);
            long ifModifiedSince = request.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE);
            if (eTag.equals(ifNoneMatch) || (ifModifiedSince >= lastModified)) {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
            }

            HttpHeaders headers = getResponseHeaders(pathStr, lastModified, eTag, IMAGE_JPEG, AVAILABLE_IMAGE_TYPES);
            Resource resource = new PathResource(fileStore.toPath(pathStr));
            return ResponseEntity.ok().headers(headers).body(resource);
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping(value = "/public/video/{key}", produces = {"video/*"})
    @CrossOrigin(originPatterns = "*")
    public ResponseEntity<Resource> getPublicVideo(
            HttpServletRequest request,
            @PathVariable("key") @NotBlank String key) {
        FileStore fileStore = StoreUtils.defaultFileStore();
        try {
            String pathStr = FileUtils.getDefaultPublicPath(key);
            long lastModified = fileStore.getLastModifiedTime(pathStr);
            String eTag = "\"" + DigestUtils.md5Hex(String.valueOf(lastModified)) + "\"";

            String ifNoneMatch = request.getHeader(HttpHeaders.IF_NONE_MATCH);
            long ifModifiedSince = request.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE);
            if (eTag.equals(ifNoneMatch) || (ifModifiedSince >= lastModified)) {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
            }

            HttpHeaders headers = getResponseHeaders(pathStr, lastModified, eTag,
                    MediaType.parseMediaType("video/mp4"), AVAILABLE_VIDEO_TYPES);
            Resource resource = new PathResource(fileStore.toPath(pathStr));
            return ResponseEntity.ok().headers(headers).body(resource);
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping(value = "/my/document/{key}", produces = ALL_VALUE)
    public ResponseEntity<Resource> getPrivateDocument(
            HttpServletRequest request,
            @PathVariable("key") @NotBlank String key) {
        FileStore fileStore = StoreUtils.defaultFileStore();
        try {
            String pathStr = FileUtils.getDefaultPrivatePath(key, AccountUtils.currentUser().getUserId());
            long lastModified = fileStore.getLastModifiedTime(pathStr);
            String eTag = "\"" + DigestUtils.md5Hex(String.valueOf(lastModified)) + "\"";

            String ifNoneMatch = request.getHeader(HttpHeaders.IF_NONE_MATCH);
            long ifModifiedSince = request.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE);
            if (eTag.equals(ifNoneMatch) || (ifModifiedSince >= lastModified)) {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
            }

            HttpHeaders headers = getResponseHeaders(pathStr, lastModified, eTag, APPLICATION_OCTET_STREAM, null);
            Resource resource = new PathResource(fileStore.toPath(pathStr));
            return ResponseEntity.ok().headers(headers).body(resource);
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    private HttpHeaders getResponseHeaders(String path,
                                           long lastModified,
                                           String eTag,
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

        headers.setCacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic());
        headers.setLastModified(lastModified);
        headers.setETag(eTag);

        return headers;
    }
}
