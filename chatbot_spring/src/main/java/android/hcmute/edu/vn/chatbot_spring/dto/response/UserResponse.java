package android.hcmute.edu.vn.chatbot_spring.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserResponse {
    @JsonProperty("chat_session_id")
    private String chatSessionId;
    private List<MessageResponse> messages;
}
