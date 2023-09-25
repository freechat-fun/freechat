package fun.freechat.web;

import fun.freechat.model.User;
import fun.freechat.service.account.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;

@Controller
@Validated
@SuppressWarnings("unused")
public class AuthController {
    @Autowired
    private InMemoryClientRegistrationRepository clientRegistrationRepository;

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

    @GetMapping("/login")
    public String login(Model model) {
        List<String> registrations = new LinkedList<>();
        clientRegistrationRepository.iterator().forEachRemaining(
                r -> registrations.add(r.getRegistrationId()));
        model.addAttribute("registrations", registrations);
        return "login";
    }

    @RequestMapping("/login/oauth2/success")
    public String oAuth2Success(HttpServletRequest request) {

        return "redirect:/public/docs/api/explorer";
    }

    @RequestMapping("/login/oauth2/failure")
    public String oAuth2Failure() {
        return "redirect:/login";
    }

    @RequestMapping("/login/portal/success")
    public String portalSuccess() {
        return "redirect:/public/docs/api/explorer";
    }

    @RequestMapping("/login/portal/failure")
    public String portalFailure() {
        return "redirect:/login";
    }
}
