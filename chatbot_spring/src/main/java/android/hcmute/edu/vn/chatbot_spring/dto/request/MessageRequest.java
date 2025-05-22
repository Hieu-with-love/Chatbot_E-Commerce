package android.hcmute.edu.vn.chatbot_spring.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageRequest {
    private String content;
    private String sender;

}
