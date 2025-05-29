package android.hcmute.edu.vn.chatbot_spring.service;

import android.hcmute.edu.vn.chatbot_spring.dto.request.ChatSessionRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.request.ChatSessionStartRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.request.MessageSendRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.request.SummaryRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.response.ChatSessionResponse;
import android.hcmute.edu.vn.chatbot_spring.dto.response.MessageResponse;
import android.hcmute.edu.vn.chatbot_spring.model.Message;

import java.util.List;
import java.util.Optional;

public interface ChatbotService {
    ChatSessionResponse startChatSession(ChatSessionStartRequest request);

    MessageResponse processMessage(MessageSendRequest request);

    ChatSessionResponse getChatSessionResponseById(Integer sessionId);

    List<ChatSessionResponse> getChatSessionsByUserId(Integer userId);

    ChatSessionResponse endChatSession(Integer sessionId);

    Message saveMessage(Message message);
    
    ChatSessionResponse existsAnyChatSessionByToken(String token);
    
    Optional<ChatSessionRequest> getChatSession(ChatSessionRequest request);
    
    /**
     * Get a chat session with all its messages by sessionId and userId
     * @param sessionId The session ID
     * @param userId The user ID
     * @return ChatSessionDto containing the session and all messages
     */
    ChatSessionResponse getChatSessionWithMessages(Integer sessionId, Integer userId);

    void updateChatSession(Integer id);
    ChatSessionResponse updateSummaryChatSession(Integer sessionId, SummaryRequest request);
}
