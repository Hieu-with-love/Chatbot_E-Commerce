package android.hcmute.edu.vn.chatbot_spring.mapper;

import android.hcmute.edu.vn.chatbot_spring.dto.request.MessageSendRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.response.MessageResponse;
import android.hcmute.edu.vn.chatbot_spring.enums.SENDER;
import android.hcmute.edu.vn.chatbot_spring.model.ChatSession;
import android.hcmute.edu.vn.chatbot_spring.model.Message;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageMapStruct {    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sender", expression = "java(mapSender(request.getSender()))")
    @Mapping(target = "sentTime", expression = "java(mapCurrentTime())")
    @Mapping(target = "chatSession", source = "sessionId", qualifiedByName = "sessionIdToChatSession")
    @Mapping(target = "intentType", ignore = true)
    Message toEntity(MessageSendRequest request);

    @Mapping(target = "sender", expression = "java(message.getSender().getValue())")
    MessageResponse toResponse(Message message);
    
    List<MessageResponse> toResponseList(List<Message> messages);

    @Named("sessionIdToChatSession")
    default ChatSession sessionIdToChatSession(Integer sessionId) {
        if (sessionId == null) {
            return null;
        }
        ChatSession chatSession = new ChatSession();
        chatSession.setId(sessionId);
        return chatSession;
    }
    
    default SENDER mapSender(String sender) {
        if (sender == null) {
            return null;
        }
        return SENDER.fromValue(sender);
    }
    
    default LocalDateTime mapCurrentTime() {
        return LocalDateTime.now();
    }
}
