package android.hcmute.edu.vn.chatbot_spring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_chat_sessions")
public class ChatSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "session_title")
    private String sessionTitle;

    @Column(name = "started_at")
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "has_session")
    private Boolean hasSession;

    @Lob
    private String summary;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;    // One session has many messages

    @OneToMany(mappedBy = "chatSession", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

    public void addMessage(Message message) {
        if (message != null) {
            messages.add(message);
            message.setChatSession(this);
        }
    }

    public void removeMessage(Message message) {
        if (message != null) {
            messages.remove(message);
            message.setChatSession(null);
        }
    }

}
