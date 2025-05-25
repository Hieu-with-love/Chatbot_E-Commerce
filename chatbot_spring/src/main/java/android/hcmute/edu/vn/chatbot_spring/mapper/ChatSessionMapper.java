package android.hcmute.edu.vn.chatbot_spring.mapper;

import android.hcmute.edu.vn.chatbot_spring.dto.response.ChatSessionResponse;
import android.hcmute.edu.vn.chatbot_spring.dto.response.MessageResponse;
import android.hcmute.edu.vn.chatbot_spring.model.ChatSession;
import android.hcmute.edu.vn.chatbot_spring.model.Message;

import java.util.List;
import java.util.stream.Collectors;

public class ChatSessionMapper {    /**
     * Convert a ChatSession entity to a ChatSessionResponse
     * @param session The chat session entity
     * @param messages List of messages associated with this session
     * @return ChatSessionResponse object
     */
    public static ChatSessionResponse toResponse(ChatSession session, List<Message> messages) {
        if (session == null) {
            return null;
        }

        return ChatSessionResponse.builder()
                .id(session.getId())
                .sessionTitle(session.getSessionTitle())
                .startedAt(session.getStartedAt())
                .endedAt(session.getEndedAt())
                .userId(session.getUser().getId())
                .userName(session.getUser().getFullName())
                .messages(messages.stream()
                        .map(ChatSessionMapper::toMessageResponse)
                        .collect(Collectors.toList()))
                .chatSessionId(session.getId())
                .build();
    }    /**
     * Convert a Message entity to a MessageResponse
     * @param message The message entity
     * @return MessageResponse object
     */    public static MessageResponse toMessageResponse(Message message) {
        if (message == null) {
            return null;
        }

        return MessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .sender(message.getSender().getValue())
                .sentTime(message.getSentTime())
                .intentType(message.getIntentType())
                .build();
    }
    
    /**
     * Convert a Message entity to be included in a ChatSessionResponse
     * @param message The message entity
     * @return MessageResponse object
     * 
     * @deprecated Use MessageMapStruct.toResponse instead
     */
    @Deprecated
    private static MessageResponse convertToChatSessionResponse(Message message) {
        return toMessageResponse(message);
    }
}
