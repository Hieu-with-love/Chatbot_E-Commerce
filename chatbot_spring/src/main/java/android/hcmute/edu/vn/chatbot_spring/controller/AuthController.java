package android.hcmute.edu.vn.chatbot_spring.controller;

import android.hcmute.edu.vn.chatbot_spring.dto.request.LoginRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.request.RegisterRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.response.ResponseData;
import android.hcmute.edu.vn.chatbot_spring.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try{
            boolean isRegistered = authService.register(req);
            ResponseData responseData = ResponseData.builder()
                    .data("Register successfully")
                    .status(200)
                    .message("Register successfully")
                    .build();

            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req){
        try{
            String jwt = authService.login(req.getUsername(), req.getPassword());
            ResponseData responseData = ResponseData.builder()
                    .data(jwt)
                    .status(200)
                    .message("Login successfully")
                    .build();
            return ResponseEntity.ok(responseData);
        }catch (Exception ex){
            ResponseData responseData = ResponseData.builder()
                    .data("Login failed")
                    .status(500)
                    .message("Login failed")
                    .build();
            return ResponseEntity.badRequest().body(responseData);
        }
    }

}
