package android.hcmute.edu.vn.chatbot_spring.dto.request;

import lombok.Data;
import lombok.Getter;

@Getter
public class PurchaseProductRequest {
    private Integer productId;
    private Integer quantity;
    private Integer addressId;
}