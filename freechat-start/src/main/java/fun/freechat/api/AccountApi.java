package fun.freechat.api;

import fun.freechat.api.dto.ApiTokenInfoDTO;
import fun.freechat.api.dto.UserBasicInfoDTO;
import fun.freechat.api.dto.UserDetailsDTO;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.FileUtils;
import fun.freechat.model.User;
import fun.freechat.service.account.SysApiTokenService;
import fun.freechat.service.account.SysUserService;
import fun.freechat.service.common.FileStore;
import fun.freechat.service.config.RuntimeConfig;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static fun.freechat.api.util.ConfigUtils.AVATAR_MAX_COUNT_KEY;
import static fun.freechat.api.util.ConfigUtils.AVATAR_MAX_SIZE_KEY;
import static fun.freechat.service.util.StoreUtils.PUBLIC_DIR;

@RestController
@Tag(name = "Account")
@RequestMapping("/api/v2/account")
@Validated
@SuppressWarnings("unused")
public class AccountApi {
    @Autowired
    private SysUserService userService;
    @Autowired
    private SysApiTokenService apiTokenService;
    @Autowired
    private RuntimeConfig runtimeConfig;

    @Operation(
            operationId = "getUserBasic",
            summary = "Get User Basic Information",
            description = "Return user basic information, including: username, nickname, avatar link."
    )
    @GetMapping(value = {"/basic/{username}", "/basic"})
    public UserBasicInfoDTO basic(
            @Parameter(description = "Username") @PathVariable("username")
            Optional<String> username) {
        User user;
        if (username.isPresent()) {
            user = userService.loadByUsername(username.get());
        } else {
            user = AccountUtils.currentUser();
        }
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
            description = "Create a timed API Token, valid for {duration} seconds."
    )
    @PostMapping(value = {"/token", "/token/{duration}"}, produces = MediaType.TEXT_PLAIN_VALUE)
    public String createToken(
            @Parameter(description = "Token validity duration (seconds)") @PathVariable("duration")
            Optional<Long> duration) {
        String token = apiTokenService.create(AccountUtils.currentUser(),
                    duration.map(Duration::ofSeconds).orElse(null));
        if (token == null) {
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
    @PreAuthorize("hasPermission(#p0, 'apiTokenByContentDefaultOp')")
    public String deleteToken(
            @Parameter(description = "Token content") @PathVariable("token") @NotBlank
            String token) {
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
    @PreAuthorize("hasPermission(#p0, 'apiTokenByContentDefaultOp')")
    public String disableToken(
            @Parameter(description = "Token content") @PathVariable("token") @NotBlank
            String token) {
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
    public List<ApiTokenInfoDTO> listTokens() {
        return apiTokenService.list(AccountUtils.currentUser())
                .stream()
                .map(ApiTokenInfoDTO::from)
                .toList();
    }

    @Operation(
            operationId = "getTokenById",
            summary = "Get API Token by Id",
            description = "Get the API token by id."
    )
    @GetMapping(value = "/token/id/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasPermission(#p0, 'apiTokenByIdDefaultOp')")
    public String getTokenById(
            @Parameter(description = "Token id") @PathVariable("id") @Positive
            Long id) {
        return apiTokenService.getById(id);
    }

    @Operation(
            operationId = "deleteTokenById",
            summary = "Delete API Token by Id",
            description = "Delete the API token by id."
    )
    @DeleteMapping("/token/id/{id}")
    @PreAuthorize("hasPermission(#p0, 'apiTokenByIdDefaultOp')")
    public Boolean deleteTokenById(
            @Parameter(description = "Token id") @PathVariable("id") @Positive
            Long id) {
        return apiTokenService.deleteById(id);
    }

    @Operation(
            operationId = "disableTokenById",
            summary = "Disable API Token by Id",
            description = "Disable the API token by id."
    )
    @PutMapping("/token/id/{id}")
    @PreAuthorize("hasPermission(#p0, 'apiTokenByIdDefaultOp')")
    public Boolean disableTokenById(
            @Parameter(description = "Token id") @PathVariable("id") @Positive
            Long id) {
        return apiTokenService.deleteById(id);
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
        long maxSize = runtimeConfig.getLong(AVATAR_MAX_SIZE_KEY);
        int maxCount = runtimeConfig.getInt(AVATAR_MAX_COUNT_KEY);;

        if (file.getSize() > maxSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size should be less than " + maxSize);
        }

        FileStore fileStore = StoreUtils.defaultFileStore();
        try {
            String dstDir = PUBLIC_DIR + AccountUtils.currentUser().getUserId() + "/picture";
            String dstPath = FileUtils.transfer(file, fileStore, dstDir, maxCount);
            String shareUrl = fileStore.getShareUrl(dstPath, Integer.MAX_VALUE);
            if (StringUtils.isBlank(shareUrl)) {
                shareUrl = FileUtils.getDefaultPublicUrlForImage(request, dstPath);
            }
            return shareUrl;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
        }
    }
}
