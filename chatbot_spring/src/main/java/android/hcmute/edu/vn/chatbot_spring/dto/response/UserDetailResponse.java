package android.hcmute.edu.vn.chatbot_spring.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailResponse {
    private Integer id;

    private String fullName;

    private String phone;

    private String email;

    private String avatarUrl;

    private String role;

    private boolean isVerified;
}
