package android.hcmute.edu.vn.chatbot_spring.service;

import android.hcmute.edu.vn.chatbot_spring.dto.request.ChatSessionStartRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.request.MessageSendRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.response.ChatSessionResponse;
import android.hcmute.edu.vn.chatbot_spring.dto.response.MessageResponse;
import android.hcmute.edu.vn.chatbot_spring.model.ChatSession;
import android.hcmute.edu.vn.chatbot_spring.model.Message;

import java.util.List;

public interface ChatbotService {
    ChatSessionResponse startChatSession(ChatSessionStartRequest request);

    MessageResponse processMessage(MessageSendRequest request);

    ChatSessionResponse getChatSessionById(Integer sessionId);

    List<ChatSessionResponse> getChatSessionsByUserId(Integer userId);

    ChatSessionResponse endChatSession(Integer sessionId);

    Message saveMessage(Message message);
}
