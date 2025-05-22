package android.hcmute.edu.vn.chatbot_spring.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductImageResponse {
    private Integer id;
    private String imageUrl;
}
