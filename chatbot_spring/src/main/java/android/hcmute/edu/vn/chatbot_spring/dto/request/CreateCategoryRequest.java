package android.hcmute.edu.vn.chatbot_spring.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCategoryRequest {
    private String name;
    private String code;
    private String description;
    private String thumbnailUrl;

}
