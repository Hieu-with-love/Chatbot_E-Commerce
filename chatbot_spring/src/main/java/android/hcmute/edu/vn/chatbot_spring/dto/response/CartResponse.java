package android.hcmute.edu.vn.chatbot_spring.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartResponse {
    private Integer id;
    private Integer userId;
    private List<CartItemResponse> cartItems;
}
