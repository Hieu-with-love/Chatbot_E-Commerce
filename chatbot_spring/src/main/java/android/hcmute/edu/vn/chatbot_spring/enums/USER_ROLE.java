package android.hcmute.edu.vn.chatbot_spring.enums;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum USER_ROLE {
    USER("USER"),
    ADMIN("ADMIN"),
    OWNER("OWNER");

    private String role;

    USER_ROLE(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
