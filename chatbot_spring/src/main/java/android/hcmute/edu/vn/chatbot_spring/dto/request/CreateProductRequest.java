package android.hcmute.edu.vn.chatbot_spring.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CreateProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;
    private Integer categoryId;
    private int quantity;
}
