package android.hcmute.edu.vn.chatbot_spring.service.impl;

import android.hcmute.edu.vn.chatbot_spring.configuration.JwtProvider;
import android.hcmute.edu.vn.chatbot_spring.dto.request.ChatSessionRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.request.ChatSessionStartRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.request.MessageSendRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.response.ChatSessionResponse;
import android.hcmute.edu.vn.chatbot_spring.dto.response.MessageResponse;
import android.hcmute.edu.vn.chatbot_spring.enums.SENDER;
import android.hcmute.edu.vn.chatbot_spring.exception.ResourceNotFoundException;
import android.hcmute.edu.vn.chatbot_spring.mapper.ChatSessionMapStruct;
import android.hcmute.edu.vn.chatbot_spring.mapper.MessageMapStruct;
import android.hcmute.edu.vn.chatbot_spring.model.ChatSession;
import android.hcmute.edu.vn.chatbot_spring.model.Message;
import android.hcmute.edu.vn.chatbot_spring.model.User;
import android.hcmute.edu.vn.chatbot_spring.repository.ChatSessionRepository;
import android.hcmute.edu.vn.chatbot_spring.repository.MessageRepository;
import android.hcmute.edu.vn.chatbot_spring.repository.UserRepository;
import android.hcmute.edu.vn.chatbot_spring.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {
    private final ChatSessionRepository chatSessionRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    
    // MapStruct mappers
    private final ChatSessionMapStruct chatSessionMapper;
    private final MessageMapStruct messageMapper;
    
    @Override
    @Transactional
    public ChatSessionResponse startChatSession(ChatSessionStartRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        String sessionTitle = request.getSessionTitle();
        if (sessionTitle == null || sessionTitle.trim().isEmpty()) {
            sessionTitle = "Chat Session " + LocalDateTime.now().toString() + "of " + user.getFullName();
        }

        // Convert request to entity using mapper
        ChatSession chatSession = chatSessionMapper.toEntity(request);
        chatSession.setSessionTitle(sessionTitle);
        chatSession.setStartedAt(LocalDateTime.now());
        chatSession.setUser(user);
        chatSession.setMessages(new ArrayList<>());
        chatSession.setHasSession(false);

        ChatSession savedSession = chatSessionRepository.save(chatSession);
        return chatSessionMapper.toResponse(savedSession);
    }
    
    @Override
    @Transactional
    public MessageResponse processMessage(MessageSendRequest request) {
        // Find the chat session
        ChatSession chatSession = chatSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found with ID: " + request.getSessionId()));

        // Check if the user is the owner of the session
        if (!chatSession.getUser().getId().equals(request.getUserId())) {
            throw new ResourceNotFoundException("User does not own this chat session");
        }

        // Convert request to entity and save user message
        Message userMessage = messageMapper.toEntity(request);
        userMessage.setChatSession(chatSession); // Ensure proper session assignment
        userMessage = messageRepository.save(userMessage);        return messageMapper.toResponse(userMessage);
    }
    
    @Override
    public ChatSessionResponse getChatSessionById(Integer sessionId) {
        ChatSession chatSession = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found with ID: " + sessionId));
        
        List<Message> messages = messageRepository.findBySessionIdAndSentTimeAsc(sessionId);
        return chatSessionMapper.toResponseWithMessages(chatSession, messages);
    }

    @Override
    public List<ChatSessionResponse> getChatSessionsByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
                
        List<ChatSession> chatSessions = chatSessionRepository.findByUserId(userId);
        return chatSessions.stream()
                .map(session -> {
                    List<Message> messages = messageRepository.findBySessionIdAndSentTimeAsc(session.getId());
                    return chatSessionMapper.toResponseWithMessages(session, messages);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChatSessionResponse endChatSession(Integer sessionId) {
        ChatSession chatSession = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found with ID: " + sessionId));

        chatSession.setEndedAt(LocalDateTime.now());
        ChatSession savedSession = chatSessionRepository.save(chatSession);        return chatSessionMapper.toResponse(savedSession);
    }

    @Override
    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }
    
    @Override
    public ChatSessionResponse existsAnyChatSessionByToken(String token) {
        String email = jwtProvider.getEmailFromJwtToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        log.info("Checking if user {} has any chat sessions", user.getId());
        boolean exists = chatSessionRepository.existsAnyChatSessionByUserId(user.getId());

        if (exists && !user.getChatSessions().isEmpty()) {
            return ChatSessionResponse.builder()
                    .userId(user.getId())
                    .hasSession(true)
                    .chatSessionId(user.getChatSessions().get(0).getId())
                    .build();
        }

        return ChatSessionResponse.builder()
                .userId(user.getId())
                .hasSession(exists)
                .build();
    }

    @Override
    public Optional<ChatSessionRequest> getChatSession(ChatSessionRequest request) {        return chatSessionRepository.findBySessionIdAndUserId(request.getSessionId(), request.getUserId());
    }
    
    @Override
    public ChatSessionResponse getChatSessionWithMessages(Integer sessionId, Integer userId) {
        // Find the chat session that belongs to the user
        ChatSession chatSession = chatSessionRepository.findSessionByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chat session not found with ID: " + sessionId + " for user: " + userId));
        
        // Fetch all messages for this session, ordered by sentTime ascending
        List<Message> messages = messageRepository.findBySessionIdAndSentTimeAsc(sessionId);
        
        // Use the MapStruct mapper to convert to DTO
        return chatSessionMapper.toResponseWithMessages(chatSession, messages);
    }

    @Override
    public void updateChatSession(Integer id) {
        ChatSession chatSession = chatSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found with ID: " + id));

        chatSession.setHasSession(true);
        chatSessionRepository.save(chatSession);
    }

    /**
     * Mock method to generate a bot response
     * In a real application, this would call an AI service like Gemini
     * @param userMessage The user message
     * @return A bot response message
     */
    private Message generateBotResponse(Message userMessage) {
        String response = "I received your message: " + userMessage.getContent() + 
                          "\nThis is a mock response. In a real app, I would use an AI like Gemini to generate a meaningful response.";
        String intentType = detectIntent(userMessage.getContent());

        return Message.builder()
                .content(response)
                .sender(SENDER.BOT)
                .sentTime(LocalDateTime.now())
                .chatSession(userMessage.getChatSession())
                .intentType(intentType)
                .build();
    }    /**
     * Simple intent detection method
     * In a real application, this would be more sophisticated
     * @param content The message content
     * @return The detected intent type or null
     */
    private String detectIntent(String content) {
        String lowerContent = content.toLowerCase();
        if (lowerContent.contains("product") || lowerContent.contains("buy") || lowerContent.contains("purchase")) {
            return "PRODUCT_SEARCH";
        } else if (lowerContent.contains("order") || lowerContent.contains("delivery") || lowerContent.contains("shipping")) {
            return "ORDER_INQUIRY";
        } else if (lowerContent.contains("help") || lowerContent.contains("support")) {
            return "SUPPORT_REQUEST";
        }
        return null; // No specific intent detected
    }
}
