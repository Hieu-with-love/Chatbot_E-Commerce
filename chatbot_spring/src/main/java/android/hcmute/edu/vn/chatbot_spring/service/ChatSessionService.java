package android.hcmute.edu.vn.chatbot_spring.service;

import android.hcmute.edu.vn.chatbot_spring.dto.request.ChatSessionRequest;

public interface ChatSessionService {
    void createChatSession(ChatSessionRequest req);
    void updateChatSession(int id, ChatSessionRequest req);
    void deleteChatSession(int id);

}
