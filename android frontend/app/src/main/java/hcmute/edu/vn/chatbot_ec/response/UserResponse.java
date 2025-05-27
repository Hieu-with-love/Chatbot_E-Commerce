package hcmute.edu.vn.chatbot_ec.response;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("userId")
    private Integer userId;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("avatarUrl")
    private String avatarUrl;

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
