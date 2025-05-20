package android.hcmute.edu.vn.chatbot_spring.service;

import android.hcmute.edu.vn.chatbot_spring.dto.request.RegisterRequest;

public interface AuthService {
    String login(String username, String password);
    boolean register(RegisterRequest req);
}
