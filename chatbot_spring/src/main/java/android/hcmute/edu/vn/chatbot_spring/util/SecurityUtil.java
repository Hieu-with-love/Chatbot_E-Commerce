package android.hcmute.edu.vn.chatbot_spring.util;

import android.hcmute.edu.vn.chatbot_spring.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    private SecurityUtil() {
    }
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken || authentication == null) {
            throw new AccessDeniedException("You are not an anonymous user");
        }

        return (User) authentication.getPrincipal();
    }

    public static String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }
}
