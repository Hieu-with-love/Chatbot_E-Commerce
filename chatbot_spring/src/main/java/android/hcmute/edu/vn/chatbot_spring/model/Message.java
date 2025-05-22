package android.hcmute.edu.vn.chatbot_spring.model;

import android.hcmute.edu.vn.chatbot_spring.enums.SENDER;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String content;

    @Enumerated(EnumType.STRING)
    private SENDER sender;

    private LocalDateTime sentTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_session_id", referencedColumnName = "id")
    private ChatSession chatSession;

    // optional, save time but storage cost
    @Column(name = "intent_type")
    private String intentType;


}
