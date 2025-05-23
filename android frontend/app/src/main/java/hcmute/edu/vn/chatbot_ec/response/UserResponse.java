package hcmute.edu.vn.chatbot_ec.response;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("userId")
    private Integer userId;

    @SerializedName("chatSessionId")
    private Integer chatSessionId;

    public UserResponse(){}

    public Integer getChatSessionId() {
        return chatSessionId;
    }

    public void setChatSessionId(Integer chatSessionId) {
        this.chatSessionId = chatSessionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
