package fun.freechat.web;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@SuppressWarnings("unused")
public class MainController {
    private static final List<String> FAVICON_STYLES = List.of("dark", "light");

    @RequestMapping("/w/**")
    public String index(Model model) {
        try {
            User user = AccountUtils.currentUser();
            model.addAttribute("username", user.getUsername());
            model.addAttribute("nickname", user.getNickname());
        } catch (ResponseStatusException e) {
            model.addAttribute("username", "");
            model.addAttribute("nickname", "");
        }
        return "index";
    }

    @RequestMapping("/")
    public String home(Model model) {
        return "redirect:/w";
    }


    @GetMapping({"/public/check/live", "/status.taobao", "/checkpreload.htm"})
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
