package android.hcmute.edu.vn.chatbot_spring.mapper;

import android.hcmute.edu.vn.chatbot_spring.dto.response.UserResponse;
import android.hcmute.edu.vn.chatbot_spring.model.User;

public class UserMapper {
    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .chatSessionId(user.getChatSessions().get(0).getId())
                .userId(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }
}
