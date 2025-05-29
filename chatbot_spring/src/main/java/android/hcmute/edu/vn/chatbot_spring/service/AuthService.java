package android.hcmute.edu.vn.chatbot_spring.service;

import android.hcmute.edu.vn.chatbot_spring.dto.request.RegisterRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.response.AuthResponse;
import android.hcmute.edu.vn.chatbot_spring.model.User;

public interface AuthService {
    String login(String username, String password);
    User register(RegisterRequest req) throws IllegalAccessException;
    boolean verifyOtp(String otp);
    boolean resetPassword(String email, String password);
    boolean logout(String token);
    AuthResponse getCurrentUser(String token) throws IllegalAccessException;
}
