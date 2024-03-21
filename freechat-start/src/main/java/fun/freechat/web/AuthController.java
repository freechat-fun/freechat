package fun.freechat.web;

import fun.freechat.model.User;
import fun.freechat.service.account.SysUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @RequestMapping("/login/oauth2/success")
    public String oAuth2Success(HttpServletRequest request) {
        return "redirect:/w";
    }

    @RequestMapping("/login/oauth2/failure")
    public String oAuth2Failure() {
        return "redirect:" + loginUri;
    }

    @RequestMapping("/login/portal/success")
    public String portalSuccess() {
        return "redirect:/w";
    }

    @RequestMapping("/login/portal/failure")
    public String portalFailure() {
        return "redirect:" + loginUri;
    }
}
