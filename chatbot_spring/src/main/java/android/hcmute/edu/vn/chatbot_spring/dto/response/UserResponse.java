package android.hcmute.edu.vn.chatbot_spring.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserResponse {
    private int chatSessionId;
    private int userId;
    private String email;
    private String phone;
    private String fullName;
    private String role;
    private String avatar;
}
