package android.hcmute.edu.vn.chatbot_spring.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryResponse {
    private Integer id;
    private String name;
    private String description;
    private String code;
    private String thumbnailUrl;
}
