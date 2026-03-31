package fun.freechat.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController implements org.springframework.boot.webmvc.error.ErrorController {
    @RequestMapping("/error")
    public String handleError() {
        return "redirect:/";
    }
}
