package fun.freechat.api.admin;

import fun.freechat.api.dto.ApiTokenInfoDTO;
import fun.freechat.api.dto.UserBasicInfoDTO;
import fun.freechat.api.dto.UserDetailsDTO;
import fun.freechat.api.dto.UserFullDetailsDTO;
import fun.freechat.model.User;
import fun.freechat.service.account.SysApiTokenService;
import fun.freechat.service.account.SysAuthorityService;
import fun.freechat.service.account.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Controller
@Tag(name = "Account Manager (for admin)", description = "Manage users, callable only by super administrators.")
@RequestMapping("/api/v1/admin")
@ResponseBody
@Validated
@SuppressWarnings("unused")
public class AccountManagerApi {
    @Autowired
    private SysUserService userService;

    @Autowired
    private SysAuthorityService authorityService;

    @Autowired
    private SysApiTokenService apiTokenService;

    @Operation(
            operationId = "listUsers",
            summary = "List User Information",
            description = "Return user information by page, return the pageNum page, up to pageSize user information."
    )
    @GetMapping(value = {
            "/users/{pageSize}/{pageNum}",
            "/users/{pageSize}",
            "/users"})
    public List<UserBasicInfoDTO> listUsers(
            @Parameter(description = "Maximum quantity") @PathVariable("pageSize") Optional<Long> pageSize,
            @Parameter(description = "Current page number") @PathVariable("pageNum") Optional<Long> pageNum) {
        long limit = pageSize.filter(size -> size > 0).orElse(10L);
        long offset = pageNum.filter(num -> num >= 0).orElse(0L) * limit;
        return userService.list(limit, offset)
                .stream()
                .map(UserBasicInfoDTO::from)
                .toList();
    }

    @Operation(
            operationId = "getDetailsOfUser",
            summary = "Get User Details",
            description = "Return detailed user information."
    )
    @GetMapping("/user/{username}")
    public UserDetailsDTO userDetails(
            @Parameter(description = "Username") @PathVariable("username") @NotBlank
            String username) {
        User user = userService.loadByUsername(username);
        return UserDetailsDTO.from(user);
    }

    @Operation(
            operationId = "createUser",
            summary = "Create User",
            description = "Create user."
    )
    @PostMapping("/user")
    public Boolean createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User information",
                    content = @Content(
                            examples = @ExampleObject("""
                                    {
                                      "username": "Jack",
                                      "password": "jack",
                                      "nickname": "Jack（测试账号）"
                                    }
                                    """
                            )
                    )
            )
            @RequestBody
            @NotNull
            UserFullDetailsDTO userDetails) {
        return userService.create(userDetails.toUser());
    }

    @Operation(
            operationId = "updateUser",
            summary = "Update User",
            description = "Update user information."
    )
    @PutMapping("/user")
    public Boolean updateUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User information")
            @RequestBody
            @NotNull
            UserFullDetailsDTO userDetails) {
        return userService.update(userDetails.toUser());
    }

    @Operation(
            operationId = "deleteUser",
            summary = "Delete User",
            description = "Delete user by username."
    )
    @DeleteMapping("/user/{username}")
    public Boolean deleteUser(
            @Parameter(description = "Username") @PathVariable("username") @NotBlank
            String username) {
        return userService.delete(username);
    }

    @Operation(
            operationId = "listAuthoritiesOfUser",
            summary = "List User Permissions",
            description = "List the user's permissions."
    )
    @GetMapping("/authority/{username}")
    public Set<String> listAuthorities(
            @Parameter(description = "Username") @PathVariable("username") @NotBlank
            String username) {
        User user = userService.loadByUsername(username);
        if (Objects.isNull(user)) {
            return null;
        }
        return authorityService.list(user);
    }

    @Operation(
            operationId = "updateAuthoritiesOfUser",
            summary = "Update User Permissions",
            description = "Update the user's permission list."
    )
    @PutMapping("/authority/{username}")
    public Boolean updateAuthorities(
            @Parameter(description = "Username") @PathVariable("username") @NotBlank
            String username,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Permission list") @RequestBody @NotNull
            Set<String> authorities) {
        User user = userService.loadByUsername(username);
        if (Objects.isNull(user)) {
            return null;
        }
        return authorityService.update(user.getUserId(), authorities);
    }

    @Operation(
            operationId = "listTokensOfUser",
            summary = "Get API Token of User",
            description = "Get the list of API Tokens of the user."
    )
    @GetMapping("/token/{username}")
    public List<ApiTokenInfoDTO> listTokens(
            @Parameter(description = "Username") @PathVariable("username") @NotBlank
            String username) {
        User user = userService.loadByUsername(username);
        if (Objects.isNull(user)) {
            return null;
        }
        return apiTokenService.list(user)
                .stream()
                .map(ApiTokenInfoDTO::from)
                .toList();
    }

    @Operation(
            operationId = "createTokenForUser",
            summary = "Create API Token for User.",
            description = "Create an API Token for a specified user, valid for duration seconds."
    )
    @PostMapping(value = "/token/{username}/{duration}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String createToken(
            @Parameter(description = "Username") @PathVariable("username") @NotBlank
            String username,
            @Parameter(description = "Validity period (seconds)") @PathVariable("duration") @PositiveOrZero
            Long duration) {
        User user = userService.loadByUsername(username);
        if (Objects.isNull(user)) {
            return null;
        }
        return apiTokenService.create(user, Duration.ofSeconds(duration));
    }

    @Operation(
            operationId = "deleteTokenForUser",
            summary = "Delete API Token",
            description = "Delete the specified API Token."
    )
    @DeleteMapping("/token/{token}")
    public Boolean deleteToken(
            @Parameter(description = "API Token") @PathVariable("token") @NotBlank
            String token) {
        return apiTokenService.delete(token);
    }

    @Operation(
            operationId = "disableTokenForUser",
            summary = "Disable API Token",
            description = "Disable the specified API Token."
    )
    @PutMapping("/token/{token}")
    public Boolean disableToken(
            @Parameter(description = "API Token") @PathVariable("token") @NotBlank
            String token) {
        return apiTokenService.disable(token);
    }

    @Operation(
            operationId = "getUserByToken",
            summary = "Get User by API Token",
            description = "Get the detailed user information corresponding to the API Token."
    )
    @GetMapping("/tokenBy/{token}")
    public UserDetailsDTO tokenBy(
            @Parameter(description = "API Token") @PathVariable("token") @NotBlank
            String token) {
        return UserDetailsDTO.from(apiTokenService.getUser(token));
    }
}
