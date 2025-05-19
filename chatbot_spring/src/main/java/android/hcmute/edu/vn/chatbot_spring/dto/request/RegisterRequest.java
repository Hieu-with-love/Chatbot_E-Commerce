package android.hcmute.edu.vn.chatbot_spring.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String fullName;
    private String phone;
    private String password;
    private String role;
}
