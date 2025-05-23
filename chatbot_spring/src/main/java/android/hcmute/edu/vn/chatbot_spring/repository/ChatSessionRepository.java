package android.hcmute.edu.vn.chatbot_spring.repository;

import android.hcmute.edu.vn.chatbot_spring.dto.request.ChatSessionRequest;
import android.hcmute.edu.vn.chatbot_spring.model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Integer> {
    @Query("SELECT cs FROM ChatSession cs WHERE cs.user.id = :userId")
    List<ChatSession> findByUserId(@Param("userId") Integer userId);

    @Query("SELECT CASE WHEN COUNT(cs) > 0 THEN true ELSE false END FROM ChatSession cs WHERE cs.user.id = :userId")
    boolean existsAnyChatSessionByUserId(@Param("userId") Integer userId);

    // Trả về Optional để kiểm tra null an toàn hơn
    @Query("SELECT cs FROM ChatSession cs WHERE cs.id = :sessionId AND cs.user.id = :userId")
    Optional<ChatSessionRequest> findBySessionIdAndUserId(Integer sessionId, Integer userId);
    
    /**
     * Find a chat session by id and user id
     * Returns the ChatSession entity if it exists and belongs to the specified user
     */
    @Query("SELECT cs FROM ChatSession cs WHERE cs.id = :sessionId AND cs.user.id = :userId")
    Optional<ChatSession> findSessionByIdAndUserId(@Param("sessionId") Integer sessionId, @Param("userId") Integer userId);
}
