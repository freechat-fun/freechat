package fun.freechat.web;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.model.User;
import fun.freechat.service.common.ShortLinkService;
import fun.freechat.service.config.RuntimeConfig;
import fun.freechat.service.enums.GenderType;
import fun.freechat.util.AppMetaUtils;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedList;
import java.util.List;

import static fun.freechat.api.util.ConfigUtils.WEB_VERSION_KEY;

@Controller
@SuppressWarnings("unused")
public class MainController {
    @Value("${app.icpCode:#{null}}")
    private String icpCode;
    @Autowired
    private RuntimeConfig runtimeConfig;
    @Autowired
    private InMemoryClientRegistrationRepository clientRegistrationRepository;
    @Autowired
    private ShortLinkService shortLinkService;
    private String registrations;


    @PostConstruct
    public void cacheRegistrations() {
        List<String> registrationList = new LinkedList<>();
        clientRegistrationRepository.forEach(clientRegistration ->
                registrationList.add(clientRegistration.getRegistrationId()));

        registrations = String.join(",", registrationList);
    }

    @RequestMapping("/w/**")
    public String page() {
        return "forward:/";
    }

    @RequestMapping(value = {"/", "/undefined"})
    public String index(HttpServletRequest request, Model model) {
        try {
            User user = AccountUtils.currentUser();
            model.addAttribute("username", user.getUsername());
            model.addAttribute("nickname", user.getNickname());
            model.addAttribute("platform", user.getPlatform());
            model.addAttribute("gender", user.getGender());
        } catch (ResponseStatusException e) {
            model.addAttribute("username", "");
            model.addAttribute("nickname", "");
            model.addAttribute("platform", "");
            model.addAttribute("gender", GenderType.OTHER.text());
        }

        String script = "/assets/index.js";
        String webVersion = runtimeConfig.get(WEB_VERSION_KEY);

        if (StringUtils.isNotBlank(webVersion)) {
            script = "/assets/index-" + webVersion + ".js";
        }
        model.addAttribute("script", script);

        if (!AppMetaUtils.isTestEnv()) {
            model.addAttribute("registrations", registrations);
        }

        String location = request.getHeader("x-location");
        String host = request.getServerName();
        if (StringUtils.isNotBlank(location)) {
            model.addAttribute("location", request.getHeader("x-location"));
        } else if (host.startsWith("cn.")) {
            model.addAttribute("location", "CN");
        }

        if ("CN".equalsIgnoreCase((String) model.getAttribute("location"))) {
            model.addAttribute("title", "狐狸猜 | 智能虚拟角色 | 聊天智能体 | 智能聊天 | 角色扮演");
            model.addAttribute("description", "狐狸猜用AI帮助您创造朋友。");
            if (StringUtils.isNotBlank(icpCode)) {
                model.addAttribute("icpCode", icpCode);
            }
        } else {
            model.addAttribute("title", "FreeChat | Character AI | Chat Agent | Chat AI | Role Play");
            model.addAttribute("description", "FreeChat uses AI to help you create friends.");
        }

        return "index";
    }

    @GetMapping("/s/{token}")
    public String shortPage(@PathVariable("token") @NotBlank String token) {
        String origPath = shortLinkService.getFullPath(token);
        if (StringUtils.isBlank(origPath)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:" + origPath;
    }
}
