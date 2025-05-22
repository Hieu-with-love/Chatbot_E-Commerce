package android.hcmute.edu.vn.chatbot_spring.dto.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductSearchRequest {
    private String name;
    private String categoryName;
    private BigDecimal price;
}
