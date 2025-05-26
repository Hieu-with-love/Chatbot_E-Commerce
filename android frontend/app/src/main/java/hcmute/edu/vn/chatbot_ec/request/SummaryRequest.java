package hcmute.edu.vn.chatbot_ec.request;

import com.google.gson.annotations.SerializedName;

/**
 * Request model for sending a chat session summary
 */
public class SummaryRequest {
    
    @SerializedName("summary")
    private String summary;
    
    @SerializedName("userId")
    private Integer userId;

    public SummaryRequest() {}
    
    public SummaryRequest(String summary, Integer userId) {
        this.summary = summary;
        this.userId = userId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
