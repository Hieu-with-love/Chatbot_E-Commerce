package android.hcmute.edu.vn.chatbot_spring.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressResponse {
    private Integer id;

    private String recipientName;

    private String fullAddress;

    private String phone;

    private Integer userId;
}
