package hcmute.edu.vn.chatbot_ec.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Represents a chat message in the application
 */
public class Message {
    public static final int SENT_BY_USER = 0;
    public static final int SENT_BY_BOT = 1;
    
    @SerializedName("id")
    private Integer id;
    
    @SerializedName("sessionId")
    private Integer sessionId;
    
    @SerializedName("content")
    private String content;
    
    @SerializedName("timestamp")
    private Date timestamp;
    
    @SerializedName("isFromUser")
    private boolean isFromUser;
    
    // Default constructor required for Retrofit
    public Message() {}
    
    public Message(String content, int sentBy) {
        this.content = content;
        this.isFromUser = (sentBy == SENT_BY_USER);
        this.timestamp = new Date();
    }
    
    public Message(Integer id, Integer sessionId, String content, Date timestamp, boolean isFromUser) {
        this.id = id;
        this.sessionId = sessionId;
        this.content = content;
        this.timestamp = timestamp;
        this.isFromUser = isFromUser;
    }
    
    // Create a new user message 
    public static Message createUserMessage(Integer sessionId, String content) {
        return new Message(null, sessionId, content, new Date(), true);
    }
    
    // Create a new bot message
    public static Message createBotMessage(Integer sessionId, String content) {
        return new Message(null, sessionId, content, new Date(), false);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isFromUser() {
        return isFromUser;
    }

    public void setFromUser(boolean fromUser) {
        isFromUser = fromUser;
    }
    
    // For backward compatibility
    public int getSentBy() {
        return isFromUser ? SENT_BY_USER : SENT_BY_BOT;
    }

    public void setSentBy(int sentBy) {
        this.isFromUser = (sentBy == SENT_BY_USER);
    }
}