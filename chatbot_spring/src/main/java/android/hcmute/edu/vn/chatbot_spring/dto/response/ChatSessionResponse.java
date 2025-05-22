package android.hcmute.edu.vn.chatbot_spring.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatSessionResponse {
    private Integer id;
    private String sessionTitle;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Integer userId;
    private String userName;
    private List<MessageResponse> messages;
}
