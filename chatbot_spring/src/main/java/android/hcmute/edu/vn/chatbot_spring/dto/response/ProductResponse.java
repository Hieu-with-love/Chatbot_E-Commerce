package android.hcmute.edu.vn.chatbot_spring.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class ProductResponse {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private String thumbnailUrl;
    private List<ProductImageResponse> productImages;
    private Integer categoryId;
}
