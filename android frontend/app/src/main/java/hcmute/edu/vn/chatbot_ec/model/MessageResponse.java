package hcmute.edu.vn.chatbot_ec.model;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for a processed message
 */
public class MessageResponse {
    
    @SerializedName("messageId")
    private String messageId;
    
    @SerializedName("sessionId")
    private String sessionId;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("sender")
    private String sender;
    
    @SerializedName("timestamp")
    private String timestamp;
    
    public MessageResponse() {}

    public String getMessageId() {
        return messageId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
