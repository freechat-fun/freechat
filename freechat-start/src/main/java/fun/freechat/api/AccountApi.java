package fun.freechat.api;

import fun.freechat.api.dto.UserBasicInfoDTO;
import fun.freechat.api.dto.UserDetailsDTO;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.model.User;
import fun.freechat.service.account.SysApiTokenService;
import fun.freechat.service.account.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Controller
@Tag(name = "Account")
@RequestMapping("/api/v1/account")
@ResponseBody
@Validated
@SuppressWarnings("unused")
public class AccountApi {
    @Autowired
    private SysUserService userService;

    @Autowired
    private SysApiTokenService apiTokenService;

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
        return UserBasicInfoDTO.fromUser(user);
    }

    @Operation(
            operationId = "getUserDetails",
            summary = "Get User Details",
            description = "Return the detailed user information of the current account, the fields refer to the [standard claims](https://openid.net/specs/openid-connect-core-1_0.html#Claims) of OpenID Connect (OIDC)."
    )
    @GetMapping("/details")
    public UserDetailsDTO details() {
        User user = AccountUtils.currentUser();
        return UserDetailsDTO.fromUser(user);
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
        return userService.update(user);
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
}
