package hcmute.edu.vn.chatbot_ec.response;

import com.google.gson.annotations.SerializedName;

/**
 * Model class for authentication response that contains JWT token
 */
public class AuthResponse {
    @SerializedName("token")
    private String token;

    
    @SerializedName("tokenType")
    private String tokenType;
    
    @SerializedName("expiresIn")
    private long expiresIn;
    
    // Optional user data that might be included in the response
    @SerializedName("userId")
    private String userId;
    
    @SerializedName("email")
    private String email;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Returns the full authentication token with token type
     * Example: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     * 
     * @return Formatted authentication token
     */
    public String getAuthorizationHeader() {
        String type = tokenType != null ? tokenType : "Bearer";
        return type + " " + token;
    }
}
