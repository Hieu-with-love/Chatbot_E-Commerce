package hcmute.edu.vn.chatbot_ec.request;

import com.google.gson.annotations.SerializedName;

/**
 * Request model for sending a message in an existing chat session
 */
public class MessageSendRequest {
    
    @SerializedName("sessionId")
    private Integer sessionId;
    
    @SerializedName("content")
    private String content;
    
    @SerializedName("userId")
    private Integer userId;

    @SerializedName("sender")
    private String sender;

    public MessageSendRequest() {}
    
    public MessageSendRequest(Integer sessionId, String content, Integer userId) {
        this.sessionId = sessionId;
        this.content = content;
        this.userId = userId;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getUserId(Integer userId) {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
