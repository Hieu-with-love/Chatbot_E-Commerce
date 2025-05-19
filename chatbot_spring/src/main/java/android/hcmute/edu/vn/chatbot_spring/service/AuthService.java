package android.hcmute.edu.vn.chatbot_spring.service;

import android.hcmute.edu.vn.chatbot_spring.dto.request.RegisterRequest;

public interface AuthService {
    boolean login(String username, String password);
    boolean register(RegisterRequest req);
}
