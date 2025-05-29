package android.hcmute.edu.vn.chatbot_spring.dto.request;

import android.hcmute.edu.vn.chatbot_spring.enums.OrderStatus;
import lombok.Getter;

@Getter
public class UpdateOrderStatusRequest {
    private OrderStatus status;
}
