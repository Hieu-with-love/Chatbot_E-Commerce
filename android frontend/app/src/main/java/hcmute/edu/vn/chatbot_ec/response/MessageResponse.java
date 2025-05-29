package hcmute.edu.vn.chatbot_ec.response;

import java.time.LocalDateTime;

public class MessageResponse {
    private Integer id;
    private String content;
    private String sender;
    private LocalDateTime sentTime;
    private String intentType;
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getSender() {
        return sender;
    }
    
    public void setSender(String sender) {
        this.sender = sender;
    }
    
    public LocalDateTime getSentTime() {
        return sentTime;
    }
    
    public void setSentTime(LocalDateTime sentTime) {
        this.sentTime = sentTime;
    }
    
    public String getIntentType() {
        return intentType;
    }
    
    public void setIntentType(String intentType) {
        this.intentType = intentType;
    }
}
