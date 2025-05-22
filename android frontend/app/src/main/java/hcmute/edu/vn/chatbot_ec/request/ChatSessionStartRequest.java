package hcmute.edu.vn.chatbot_ec.request;

import com.google.gson.annotations.SerializedName;

public class ChatSessionStartRequest {
    @SerializedName("userId")
    private Integer userId;
    
    @SerializedName("sessionTitle")
    private String sessionTitle;
    
    public ChatSessionStartRequest() {
    }
    
    public ChatSessionStartRequest(Integer userId, String sessionTitle) {
        this.userId = userId;
        this.sessionTitle = sessionTitle;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getSessionTitle() {
        return sessionTitle;
    }
    
    public void setSessionTitle(String sessionTitle) {
        this.sessionTitle = sessionTitle;
    }
}
