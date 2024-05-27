package fun.freechat.web;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.model.User;
import fun.freechat.service.common.ConfigService;
import fun.freechat.service.enums.GenderType;
import fun.freechat.service.util.ConfigUtils;
import fun.freechat.util.AppMetaUtils;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

@Controller
@SuppressWarnings("unused")
public class MainController {
    private static final String CONFIG_NAME = "system";
    private static final String WEB_VERSION_KEY = "web.version";

    @Autowired
    @Qualifier("mysqlConfigService")
    private ConfigService configService;

    @Autowired
    private InMemoryClientRegistrationRepository clientRegistrationRepository;

    private String registrations;


    @PostConstruct
    public void cacheRegistrations() {
        List<String> registrationList = new LinkedList<>();
        clientRegistrationRepository.forEach(clientRegistration ->
                registrationList.add(clientRegistration.getRegistrationId()));

        registrations = String.join(",", registrationList);
    }

    @RequestMapping("/w/**")
    public String index(Model model) {
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
        Properties properties = ConfigUtils.getProperties(configService, CONFIG_NAME);
        String webVersion = properties.getProperty(WEB_VERSION_KEY);
        if (StringUtils.isNotBlank(webVersion)) {
            script = "/assets/index-" + webVersion + ".js";
        }
        model.addAttribute("script", script);
        if (!AppMetaUtils.isTestingEnv()) {
            model.addAttribute("registrations", registrations);
        }

        return "index";
    }

    @RequestMapping("/")
    public String home(Model model) {
        return "redirect:/w";
    }


    @GetMapping({"/public/check/live"})
    @ResponseBody
    public String live() {
        return "success";
    }

    @GetMapping("/public/check/ready")
    @ResponseBody
    public String ready() {
        // TODO: check services statuses
        return "success";
    }
}
