package android.hcmute.edu.vn.chatbot_spring.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemResponse {
    private Integer id;
    private Integer productId;
    private String productName;
    private String thumbnailUrl;
    private BigDecimal priceAtPurchase;
    private Integer quantity;
}
