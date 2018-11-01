package sashaVosu.firstWebApplication.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("main")
public class MainController {
    @GetMapping
    public String main() {

        return "Simple web application";
    }
}