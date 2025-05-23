package android.hcmute.edu.vn.chatbot_spring.service.impl;

import android.hcmute.edu.vn.chatbot_spring.configuration.JwtProvider;
import android.hcmute.edu.vn.chatbot_spring.dto.request.ChatSessionRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.request.ChatSessionStartRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.request.MessageSendRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.response.ChatSessionResponse;
import android.hcmute.edu.vn.chatbot_spring.dto.response.MessageResponse;
import android.hcmute.edu.vn.chatbot_spring.enums.SENDER;
import android.hcmute.edu.vn.chatbot_spring.exception.ResourceNotFoundException;
import android.hcmute.edu.vn.chatbot_spring.model.ChatSession;
import android.hcmute.edu.vn.chatbot_spring.model.Message;
import android.hcmute.edu.vn.chatbot_spring.model.User;
import android.hcmute.edu.vn.chatbot_spring.repository.ChatSessionRepository;
import android.hcmute.edu.vn.chatbot_spring.repository.MessageRepository;
import android.hcmute.edu.vn.chatbot_spring.repository.UserRepository;
import android.hcmute.edu.vn.chatbot_spring.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

    private final ChatSessionRepository chatSessionRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    
    // Import the mapper
    private final android.hcmute.edu.vn.chatbot_spring.mapper.ChatSessionMapper chatSessionMapper = new android.hcmute.edu.vn.chatbot_spring.mapper.ChatSessionMapper();

    @Override
    @Transactional
    public ChatSessionResponse startChatSession(ChatSessionStartRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        String sessionTitle = request.getSessionTitle();
        if (sessionTitle == null || sessionTitle.trim().isEmpty()) {
            sessionTitle = "Chat Session " + LocalDateTime.now().toString();
        }

        ChatSession chatSession = ChatSession.builder()
                .sessionTitle(sessionTitle)
                .startedAt(LocalDateTime.now())
                .user(user)
                .messages(new ArrayList<>())
                .firstSession(true)
                .build();

        ChatSession savedSession = chatSessionRepository.save(chatSession);
        return convertToChatSessionResponse(savedSession);
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

        // Save user message
        Message userMessage = Message.builder()
                .content(request.getContent())
                .sender(SENDER.valueOf(request.getSender()))
                .sentTime(LocalDateTime.now())
                .chatSession(chatSession)
                .build();

        userMessage = messageRepository.save(userMessage);

        return convertToMessageResponse(userMessage);
    }

    @Override
    public ChatSessionResponse getChatSessionById(Integer sessionId) {
        ChatSession chatSession = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found with ID: " + sessionId));

        return convertToChatSessionResponse(chatSession);
    }

    @Override
    public List<ChatSessionResponse> getChatSessionsByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
                
        List<ChatSession> chatSessions = chatSessionRepository.findByUserId(userId);
        return chatSessions.stream()
                .map(this::convertToChatSessionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChatSessionResponse endChatSession(Integer sessionId) {
        ChatSession chatSession = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found with ID: " + sessionId));

        chatSession.setEndedAt(LocalDateTime.now());
        ChatSession savedSession = chatSessionRepository.save(chatSession);

        return convertToChatSessionResponse(savedSession);
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
        return ChatSessionResponse.builder()
                .userId(user.getId())
                .isFistSession(true)
                .build();
    }

    @Override
    public Optional<ChatSessionRequest> getChatSession(ChatSessionRequest request) {
        return chatSessionRepository.findBySessionIdAndUserId(request.getSessionId(), request.getUserId());
    }

    @Override
    public android.hcmute.edu.vn.chatbot_spring.dto.ChatSessionDto getChatSessionWithMessages(Integer sessionId, Integer userId) {
        // Find the chat session that belongs to the user
        ChatSession chatSession = chatSessionRepository.findSessionByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chat session not found with ID: " + sessionId + " for user: " + userId));
        
        // Fetch all messages for this session, ordered by sentTime ascending
        List<Message> messages = messageRepository.findBySessionIdAndSentTimeAsc(sessionId);
        
        // Use the mapper to convert to DTO
        return android.hcmute.edu.vn.chatbot_spring.mapper.ChatSessionMapper.toDto(chatSession, messages);
    }

    @Override
    public void updateChatSession(Integer id) {
        ChatSession chatSession = chatSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found with ID: " + id));

        chatSession.setFirstSession(false);
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
    }

    /**
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

    /**
     * Convert a ChatSession entity to a ChatSessionResponse DTO
     * @param session The chat session entity
     * @return A DTO representing the chat session
     */
    private ChatSessionResponse convertToChatSessionResponse(ChatSession session) {
        List<Message> messages = messageRepository.findBySessionIdAndSentTimeAsc(session.getId());

        return ChatSessionResponse.builder()
                .id(session.getId())
                .sessionTitle(session.getSessionTitle())
                .startedAt(session.getStartedAt())
                .endedAt(session.getEndedAt())
                .userId(session.getUser().getId())
                .userName(session.getUser().getFullName())
                .messages(messages.stream()
                        .map(this::convertToMessageResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Convert a Message entity to a MessageResponse DTO
     * @param message The message entity
     * @return A DTO representing the message
     */
    private MessageResponse convertToMessageResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .sender(message.getSender().getValue())
                .sentTime(message.getSentTime())
                .intentType(message.getIntentType())
                .build();
    }
}
