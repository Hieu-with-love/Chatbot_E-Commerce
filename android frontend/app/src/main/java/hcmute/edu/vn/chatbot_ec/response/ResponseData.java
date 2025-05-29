package hcmute.edu.vn.chatbot_ec.response;

import com.google.gson.annotations.SerializedName;

/**
 * Generic response wrapper for all API responses
 * @param <T> Type of data contained in the response
 */
public class ResponseData<T> {
    @SerializedName("status")
    private int status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private T data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
