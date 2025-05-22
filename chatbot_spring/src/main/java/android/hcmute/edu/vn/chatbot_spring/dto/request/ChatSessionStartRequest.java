package android.hcmute.edu.vn.chatbot_spring.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatSessionStartRequest {
    private Integer userId;
    private String sessionTitle;
}
