package android.hcmute.edu.vn.chatbot_spring.service;

import android.hcmute.edu.vn.chatbot_spring.dto.response.UserResponse;

public interface UserService {
    UserResponse getChatSessionByEmail(String token);
}
