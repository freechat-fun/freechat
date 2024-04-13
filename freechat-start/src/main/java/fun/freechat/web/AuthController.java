package fun.freechat.web;

import fun.freechat.api.dto.UserFullDetailsDTO;
import fun.freechat.model.User;
import fun.freechat.service.account.SysUserService;
import fun.freechat.util.IdUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

@Controller
@Validated
@SuppressWarnings("unused")
public class AuthController {
    @Value("${auth.login.uri}")
    private String loginUri;
    @Autowired
    private OAuth2AuthorizedClientService oAuth2ClientService;
    @Autowired
    private SysUserService userService;

    private User authenticationToUser(Authentication authenticated) {
        Object principal = authenticated.getPrincipal();
        if (principal instanceof User user) {
            return user;
        } else if (principal instanceof String userName) {
            return userService.loadByUsername(userName);
        }
        return null;
    }

    @GetMapping("/login/oauth2/success")
    public String oAuth2Success(HttpServletRequest request) {
        return "redirect:/w";
    }

    @GetMapping("/login/oauth2/failure")
    public String oAuth2Failure() {
        return "redirect:" + loginUri;
    }

    @GetMapping("/login/portal/success")
    public String portalSuccess() {
        return "redirect:/w";
    }

    @GetMapping("/login/portal/failure")
    public String portalFailure() {
        return "redirect:" + loginUri;
    }

    @PostMapping("/public/register/guest")
    @ResponseBody
    public UserFullDetailsDTO registerGuest() {
        String username = IdUtils.newId() + "@guest";
        String password = IdUtils.newId();

        User guest = new User()
                .withUsername(IdUtils.newId() + "@guest")
                .withPassword(IdUtils.newId())
                .withPlatform("guest");

        if (!userService.create(guest)) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Failed to create guest user");
        }

        return UserFullDetailsDTO.from(guest);
    }
}
