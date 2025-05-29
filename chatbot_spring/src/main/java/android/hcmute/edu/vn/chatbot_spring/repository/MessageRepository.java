package android.hcmute.edu.vn.chatbot_spring.repository;

import android.hcmute.edu.vn.chatbot_spring.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query("SELECT m FROM Message m WHERE m.chatSession.id = :sessionId ORDER BY m.sentTime ASC")
    List<Message> findBySessionIdAndSentTimeAsc(@Param("sessionId") Integer sessionId);

}
