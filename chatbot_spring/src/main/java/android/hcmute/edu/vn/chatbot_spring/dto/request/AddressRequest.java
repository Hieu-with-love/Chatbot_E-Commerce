package android.hcmute.edu.vn.chatbot_spring.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressRequest {
    @NotBlank(message = "Tên người nhận không được để trống")
    private String recipientName;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String fullAddress;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;
}