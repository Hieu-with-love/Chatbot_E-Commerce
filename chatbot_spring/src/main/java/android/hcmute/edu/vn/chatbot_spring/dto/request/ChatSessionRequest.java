package android.hcmute.edu.vn.chatbot_spring.dto.request;

import lombok.Data;

@Data
public class ChatSessionRequest {
    private String sessionTitle;
    private int userId;
    private int sessionId;
}
