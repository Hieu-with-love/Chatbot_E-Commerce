package hcmute.edu.vn.chatbot_ec.request;

import com.google.gson.annotations.SerializedName;

public class ChatSessionRequest {
    @SerializedName("sessionTitle")
    private String sessionTitle;
    
    @SerializedName("userId")
    private int userId;

    @SerializedName("sessionId")
    private int sessionId;
    
    public ChatSessionRequest() {
    }
    
    public ChatSessionRequest(String sessionTitle, int userId) {
        this.sessionTitle = sessionTitle;
        this.userId = userId;
    }
    
    public String getSessionTitle() {
        return sessionTitle;
    }
    
    public void setSessionTitle(String sessionTitle) {
        this.sessionTitle = sessionTitle;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }
}
