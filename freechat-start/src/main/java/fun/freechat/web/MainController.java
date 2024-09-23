package fun.freechat.web;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.model.User;
import fun.freechat.service.common.ConfigService;
import fun.freechat.service.enums.GenderType;
import fun.freechat.util.AppMetaUtils;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import static fun.freechat.api.util.ConfigUtils.WEB_VERSION_KEY;

@Controller
@SuppressWarnings("unused")
public class MainController {
    @Value("${app.icpCode:#{null}}")
    private String icpCode;
    @Autowired
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
    public String page(Model model) {
        model.addAttribute("canonical", true);
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
        Properties properties = configService.load();
        String webVersion = properties.getProperty(WEB_VERSION_KEY);

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

        if ("CN".equalsIgnoreCase((String) model.getAttribute("location")) &&
                StringUtils.isNotBlank(icpCode)) {
            model.addAttribute("icpCode", icpCode);
        }

        return "index";
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
