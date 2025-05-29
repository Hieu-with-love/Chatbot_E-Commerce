package android.hcmute.edu.vn.chatbot_spring.mapper;

import android.hcmute.edu.vn.chatbot_spring.dto.request.ChatSessionRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.request.ChatSessionStartRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.response.ChatSessionResponse;
import android.hcmute.edu.vn.chatbot_spring.dto.response.MessageResponse;
import android.hcmute.edu.vn.chatbot_spring.model.ChatSession;
import android.hcmute.edu.vn.chatbot_spring.model.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", 
        uses = {MessageMapStruct.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChatSessionMapStruct {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "messages", ignore = true)
    @Mapping(target = "user", source = "userId", qualifiedByName = "userIdToUser")
    ChatSession toEntity(ChatSessionStartRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "messages", ignore = true)
    @Mapping(target = "user", source = "userId", qualifiedByName = "userIdToUser")
    ChatSession toEntity(ChatSessionRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.fullName")
    @Mapping(target = "chatSessionId", source = "id")
    @Mapping(target = "messages", ignore = true)
    ChatSessionResponse toResponse(ChatSession chatSession);

    @Mapping(target = "userId", source = "chatSession.user.id")
    @Mapping(target = "userName", source = "chatSession.user.fullName")
    @Mapping(target = "chatSessionId", source = "chatSession.id")
    @Mapping(target = "messages", source = "messagesList")
    ChatSessionResponse toResponseWithMessages(ChatSession chatSession, List<android.hcmute.edu.vn.chatbot_spring.model.Message> messagesList);

    @Named("userIdToUser")
    default User userIdToUser(Integer userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }
}
