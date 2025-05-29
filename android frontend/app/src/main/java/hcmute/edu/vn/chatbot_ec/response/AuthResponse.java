package hcmute.edu.vn.chatbot_ec.response;

import com.google.gson.annotations.SerializedName;

/**
 * Model class for authentication response that contains JWT token and user information
 */
public class AuthResponse {
    @SerializedName("token")
    private String token;

    @SerializedName("tokenType")
    private String tokenType;
    
    @SerializedName("expiresIn")
    private long expiresIn;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("fullName")
    private String fullName;
    
    @SerializedName("avatarUrl")
    private String avatarUrl;
    
    @SerializedName("isVerified")
    private boolean isVerified;
    
    @SerializedName("role")
    private String role;

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

    public String getEmail() {
        return email;
    }    public void setEmail(String email) {
        this.email = email;
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

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
