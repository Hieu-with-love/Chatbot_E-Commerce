package android.hcmute.edu.vn.chatbot_spring.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String fullName;
    private String avatarUrl;
    private boolean isVerified;
    private String role;
    private String tokenType;
    private long expiresIn;
}
