package android.hcmute.edu.vn.chatbot_spring.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @RequestMapping("/test")
    public String test() {
        return "Test endpoint!";
    }
}
