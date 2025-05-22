package hcmute.edu.vn.chatbot_ec.response;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.List;

import hcmute.edu.vn.chatbot_ec.model.MessageResponse;

public class ChatSessionResponse {
    @SerializedName("id")
    private Integer id;
    
    @SerializedName("sessionTitle")
    private String sessionTitle;
    
    @SerializedName("startedAt")
    private String startedAt; // Using String for LocalDateTime compatibility
    
    @SerializedName("endedAt")
    private String endedAt; // Using String for LocalDateTime compatibility
    
    @SerializedName("userId")
    private Integer userId;
    
    @SerializedName("userName")
    private String userName;
    
    @SerializedName("messages")
    private List<MessageResponse> messages;
    
    public ChatSessionResponse() {
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getSessionTitle() {
        return sessionTitle;
    }
    
    public void setSessionTitle(String sessionTitle) {
        this.sessionTitle = sessionTitle;
    }
    
    public String getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }
    
    public String getEndedAt() {
        return endedAt;
    }
    
    public void setEndedAt(String endedAt) {
        this.endedAt = endedAt;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public List<MessageResponse> getMessages() {
        return messages;
    }
    
    public void setMessages(List<MessageResponse> messages) {
        this.messages = messages;
    }
}
