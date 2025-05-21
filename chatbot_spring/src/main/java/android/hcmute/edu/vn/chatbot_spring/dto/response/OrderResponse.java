package android.hcmute.edu.vn.chatbot_spring.dto.response;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponse {
    private Integer id;
    private Integer userId;
    private LocalDateTime orderDate;
    private String status;
    private String shippingAddress;
    private String shippingPhone;
    private BigDecimal totalPrice;
    private List<OrderItemResponse> orderItems;
}
