package fun.freechat.api;

import fun.freechat.api.dto.UserBasicInfoDTO;
import fun.freechat.api.dto.UserDetailsDTO;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.FileUtils;
import fun.freechat.model.User;
import fun.freechat.service.account.SysApiTokenService;
import fun.freechat.service.account.SysUserService;
import fun.freechat.service.common.ConfigService;
import fun.freechat.service.common.FileStore;
import fun.freechat.service.util.ConfigUtils;
import fun.freechat.service.util.StoreUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static fun.freechat.api.util.FileUtils.PUBLIC_DIR;

@Controller
@Tag(name = "Account")
@RequestMapping("/api/v1/account")
@ResponseBody
@Validated
@SuppressWarnings("unused")
public class AccountApi {
    private static final String CONFIG_NAME = "user";

    private static final String PICTURE_MAX_SIZE_KEY = "picture.maxSize";

    private static final String PICTURE_MAX_COUNT_KEY = "picture.maxCount";

    private static final long DEFAULT_PICTURE_MAX_SIZE = 2 * 1024 * 1024;

    private static final int DEFAULT_PICTURE_MAX_COUNT = 100;

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysApiTokenService apiTokenService;

    @Autowired
    @Qualifier("mysqlConfigService")
    private ConfigService configService;

    @Operation(
            operationId = "getUserBasic",
            summary = "Get User Basic Information",
            description = "Return user basic information, including: username, nickname, avatar link."
    )
    @GetMapping("/basic/{username}")
    public UserBasicInfoDTO basic(
            @Parameter(description = "Username") @PathVariable("username") @NotBlank
            String username) {
        User user = userService.loadByUsername(username);
        return UserBasicInfoDTO.from(user);
    }

    @Operation(
            operationId = "getUserDetails",
            summary = "Get User Details",
            description = "Return the detailed user information of the current account, the fields refer to the [standard claims](https://openid.net/specs/openid-connect-core-1_0.html#Claims) of OpenID Connect (OIDC)."
    )
    @GetMapping("/details")
    public UserDetailsDTO details() {
        User user = AccountUtils.currentUser();
        return UserDetailsDTO.from(userService.loadByUsernameAndPlatform(
                user.getUsername(), user.getPlatform()));
    }

    @Operation(
            operationId = "updateUserInfo",
            summary = "Update User Details",
            description = "Update the detailed user information of the current account."
    )
    @PutMapping("/details")
    public Boolean update(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User information")
            @RequestBody
            @NotNull
            UserDetailsDTO userDetails) {
        User user =userDetails.toUser();
        user.setUserId(AccountUtils.currentUser().getUserId());
        if (userService.update(user)) {
            AccountUtils.updateCurrentUser();
            return true;
        }
        return false;
    }

    @Operation(
            operationId = "createToken",
            summary = "Create API Token",
            description = "Create an unlimited duration API Token."
    )
    @PostMapping(value = "/token", produces = MediaType.TEXT_PLAIN_VALUE)
    public String createToken() {
        String token = apiTokenService.create(AccountUtils.currentUser());
        if (Objects.isNull(token)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many tokens.");
        }
        return token;
    }

    @Operation(
            operationId = "createTokenWithDuration",
            summary = "Create Timed API Token",
            description = "Create a timed API Token, valid for {duration} seconds."
    )
    @PostMapping(value = "/token/{duration}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String createToken(
            @Parameter(description = "Token validity duration (seconds)") @PathVariable("duration") @NotNull @Positive
            Long duration) {
        String token = apiTokenService.create(AccountUtils.currentUser(), Duration.ofSeconds(duration));
        if (Objects.isNull(token)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many tokens.");
        }
        return token;
    }

    @Operation(
            operationId = "deleteToken",
            summary = "Delete API Token",
            description = "Delete an API Token."
    )
    @DeleteMapping(value = "/token/{token}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String deleteToken(
            @Parameter(description = "Token content") @PathVariable("token") @NotBlank
            String token) {
        User user = apiTokenService.getUser(token);
        if (Objects.isNull(user) ||
                !AccountUtils.currentUser().getUserId().equals(user.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (!apiTokenService.delete(token)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return token;
    }

    @Operation(
            operationId = "disableToken",
            summary = "Disable API Token",
            description = "Disable an API Token, the token is not deleted."
    )
    @PutMapping(value = "/token/{token}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String disableToken(
            @Parameter(description = "Token content") @PathVariable("token") @NotBlank
            String token) {
        User user = apiTokenService.getUser(token);
        if (Objects.isNull(user) ||
                !AccountUtils.currentUser().getUserId().equals(user.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (!apiTokenService.disable(token)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return token;
    }

    @Operation(
            operationId = "listTokens",
            summary = "List API Tokens",
            description = "List currently valid tokens."
    )
    @GetMapping("/tokens")
    public List<String> listTokens() {
        return apiTokenService.list(AccountUtils.currentUser());
    }

    @Operation(
            operationId = "uploadUserPicture",
            summary = "Upload User Picture",
            description = "Upload a picture of the user."
    )
    @PostMapping(value = "/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String uploadPicture(
            HttpServletRequest request,
            @Parameter(description = "User picture", style = ParameterStyle.FORM) @RequestParam("file") @NotNull
            MultipartFile file) {
        Properties properties = ConfigUtils.getProperties(configService, CONFIG_NAME);
        long maxSize = ConfigUtils.getLongOrDefault(properties, PICTURE_MAX_SIZE_KEY, DEFAULT_PICTURE_MAX_SIZE);
        int maxCount = ConfigUtils.getIntOrDefault(properties, PICTURE_MAX_COUNT_KEY, DEFAULT_PICTURE_MAX_COUNT);

        if (file.getSize() > maxSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size should be less than " + maxSize);
        }

        FileStore fileStore = StoreUtils.defaultFileStore();
        try {
            String dstDir = PUBLIC_DIR + AccountUtils.currentUser().getUserId() + "/picture";
            String dstPath = FileUtils.transfer(file, fileStore, dstDir, maxCount);
            String shareUrl = fileStore.getShareUrl(dstPath, Integer.MAX_VALUE);
            if (StringUtils.isBlank(shareUrl)) {
                shareUrl = FileUtils.getDefaultShareUrlForImage(request, dstPath);
            }
            return shareUrl;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
        }
    }
}
