package android.hcmute.edu.vn.chatbot_spring.repository;

import android.hcmute.edu.vn.chatbot_spring.model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Integer> {    @Query("SELECT cs FROM ChatSession cs WHERE cs.user.id = :userId")
    List<ChatSession> findByUserId(@Param("userId") Integer userId);

}
