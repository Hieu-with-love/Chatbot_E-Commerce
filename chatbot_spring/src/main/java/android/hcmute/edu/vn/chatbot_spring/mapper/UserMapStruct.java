package android.hcmute.edu.vn.chatbot_spring.mapper;

import android.hcmute.edu.vn.chatbot_spring.dto.request.RegisterRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.response.UserResponse;
import android.hcmute.edu.vn.chatbot_spring.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapStruct {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "chatSessions", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "cart", ignore = true)
    User toEntity(RegisterRequest request);
    
    @Mapping(target = "chatSessionId", expression = "java(getChatSessionId(user))")
    UserResponse toResponse(User user);
    
    @AfterMapping
    default void setChatSessionId(@MappingTarget UserResponse.UserResponseBuilder response, User user) {
        if (user.getChatSessions() != null && !user.getChatSessions().isEmpty()) {
            response.chatSessionId(user.getChatSessions().get(0).getId());
        }
    }
    
    default Integer getChatSessionId(User user) {
        if (user.getChatSessions() == null || user.getChatSessions().isEmpty()) {
            return null;
        }
        return user.getChatSessions().get(0).getId();
    }
}
