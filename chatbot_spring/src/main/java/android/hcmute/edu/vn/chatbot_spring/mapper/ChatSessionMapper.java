package android.hcmute.edu.vn.chatbot_spring.mapper;

import android.hcmute.edu.vn.chatbot_spring.dto.ChatSessionDto;
import android.hcmute.edu.vn.chatbot_spring.dto.MessageDto;
import android.hcmute.edu.vn.chatbot_spring.model.ChatSession;
import android.hcmute.edu.vn.chatbot_spring.model.Message;

import java.util.List;
import java.util.stream.Collectors;

public class ChatSessionMapper {

    /**
     * Convert a ChatSession entity to a ChatSessionDto
     * @param session The chat session entity
     * @param messages List of messages associated with this session
     * @return ChatSessionDto object
     */
    public static ChatSessionDto toDto(ChatSession session, List<Message> messages) {
        if (session == null) {
            return null;
        }

        return ChatSessionDto.builder()
                .id(session.getId())
                .sessionTitle(session.getSessionTitle())
                .startedAt(session.getStartedAt())
                .endedAt(session.getEndedAt())
                .userId(session.getUser().getId())
                .userName(session.getUser().getFullName())
                .messages(messages.stream().map(ChatSessionMapper::toMessageDto).collect(Collectors.toList()))
                .build();
    }

    /**
     * Convert a Message entity to a MessageDto
     * @param message The message entity
     * @return MessageDto object
     */
    public static MessageDto toMessageDto(Message message) {
        if (message == null) {
            return null;
        }

        return MessageDto.builder()
                .id(message.getId())
                .content(message.getContent())
                .sender(message.getSender().getValue()) // Assuming SENDER enum has getValue() method
                .sentTime(message.getSentTime())
                .intentType(message.getIntentType())
                .build();
    }
}
