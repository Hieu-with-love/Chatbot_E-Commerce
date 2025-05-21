package android.hcmute.edu.vn.chatbot_spring.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartItemResponse {
    private Integer id;
    private Integer productId;
    private String name;
    private String thumbnailUrl;
    private BigDecimal price;
    private int quantity;
}
