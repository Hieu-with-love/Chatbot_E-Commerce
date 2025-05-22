package hcmute.edu.vn.chatbot_ec.model;

import com.google.gson.annotations.SerializedName;

/**
 * Request model for starting a new chat session
 */
public class ChatSessionStartRequest {
    
    @SerializedName("userId")
    private Integer userId;
    
    @SerializedName("initialMessage")
    private String initialMessage;
    
    public ChatSessionStartRequest() {}
    
    public ChatSessionStartRequest(Integer userId, String initialMessage) {
        this.userId = userId;
        this.initialMessage = initialMessage;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getInitialMessage() {
        return initialMessage;
    }

    public void setInitialMessage(String initialMessage) {
        this.initialMessage = initialMessage;
    }
}
