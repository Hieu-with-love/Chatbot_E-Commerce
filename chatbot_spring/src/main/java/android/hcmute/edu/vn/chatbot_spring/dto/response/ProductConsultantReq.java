package android.hcmute.edu.vn.chatbot_spring.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductConsultantReq {
    private String productName;
    private String size;
}
