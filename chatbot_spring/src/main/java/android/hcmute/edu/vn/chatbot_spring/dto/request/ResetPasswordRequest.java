package android.hcmute.edu.vn.chatbot_spring.dto.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String email;
    private String oldPassword;
}
