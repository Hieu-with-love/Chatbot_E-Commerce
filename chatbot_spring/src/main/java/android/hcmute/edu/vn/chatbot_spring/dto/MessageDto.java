package android.hcmute.edu.vn.chatbot_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private Integer id;
    private String content;
    private String sender;
    private LocalDateTime sentTime;
    private String intentType;
}
