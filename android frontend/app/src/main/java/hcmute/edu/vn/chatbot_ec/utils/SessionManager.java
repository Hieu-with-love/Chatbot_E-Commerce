package hcmute.edu.vn.chatbot_ec.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import hcmute.edu.vn.chatbot_ec.response.AuthResponse;

/**
 * SessionManager handles user session data including token and user information
 * Stores data received from backend login response directly without JWT decoding
 */
public class SessionManager {
    private static final String TAG = "SessionManager";
    private static final String PREF_NAME = "UserSession";
    
    // Keys for storing session data
    private static final String KEY_JWT_TOKEN = "jwt_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_AVATAR_URL = "avatar_url";
    private static final String KEY_IS_VERIFIED = "is_verified";
    private static final String KEY_ROLE = "role";
    private static final String KEY_TOKEN_TYPE = "token_type";
    private static final String KEY_EXPIRES_IN = "expires_in";
    private static final String KEY_LOGIN_TIME = "login_time";
    
    /**
     * Save user session data from AuthResponse
     * @param context Application context
     * @param authResponse Response from login API containing user information
     */
    public static void saveUserSession(Context context, AuthResponse authResponse) {
        if (context == null || authResponse == null) {
            Log.e(TAG, "Cannot save user session: context or authResponse is null");
            return;
        }
        
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        // Save token information
        editor.putString(KEY_JWT_TOKEN, authResponse.getToken());
        editor.putString(KEY_TOKEN_TYPE, authResponse.getTokenType());
        editor.putLong(KEY_EXPIRES_IN, authResponse.getExpiresIn());
        editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis());
        
        // Save user information
        editor.putString(KEY_EMAIL, authResponse.getEmail());
        editor.putString(KEY_FULL_NAME, authResponse.getFullName());
        editor.putString(KEY_AVATAR_URL, authResponse.getAvatarUrl());
        editor.putString(KEY_ROLE, authResponse.getRole());
        editor.putBoolean(KEY_IS_VERIFIED, authResponse.isVerified());
        
        editor.apply();
        
        Log.d(TAG, "User session saved successfully for user: " + authResponse.getFullName());
    }
    
    /**
     * Get JWT token from session
     * @param context Application context
     * @return JWT token or null if not found
     */
    public static String getToken(Context context) {
        if (context == null) {
            Log.e(TAG, "Cannot retrieve token: context is null");
            return null;
        }
        
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_JWT_TOKEN, null);
    }
    
    /**
     * Get user's full name from session
     * @param context Application context
     * @return Full name or null if not found
     */
    public static String getFullName(Context context) {
        if (context == null) return null;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_FULL_NAME, null);
    }
    
    /**
     * Get user's email from session
     * @param context Application context
     * @return Email or null if not found
     */
    public static String getEmail(Context context) {
        if (context == null) return null;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_EMAIL, null);
    }
    
    /**
     * Get user's avatar URL from session
     * @param context Application context
     * @return Avatar URL or null if not found
     */
    public static String getAvatarUrl(Context context) {
        if (context == null) return null;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_AVATAR_URL, null);
    }
    
    /**
     * Get user's role from session
     * @param context Application context
     * @return Role or null if not found
     */
    public static String getRole(Context context) {
        if (context == null) return null;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_ROLE, null);
    }
    
    /**
     * Check if user is verified from session
     * @param context Application context
     * @return true if user is verified, false otherwise
     */
    public static boolean isVerified(Context context) {
        if (context == null) return false;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_VERIFIED, false);
    }
    
    /**
     * Get token type from session
     * @param context Application context
     * @return Token type or "Bearer" as default
     */
    public static String getTokenType(Context context) {
        if (context == null) return "Bearer";
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_TOKEN_TYPE, "Bearer");
    }
    
    /**
     * Get token expiration time from session
     * @param context Application context
     * @return Expiration time in seconds, or -1 if not found
     */
    public static long getExpiresIn(Context context) {
        if (context == null) return -1;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getLong(KEY_EXPIRES_IN, -1);
    }
    
    /**
     * Get login time from session
     * @param context Application context
     * @return Login time in milliseconds, or -1 if not found
     */
    public static long getLoginTime(Context context) {
        if (context == null) return -1;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getLong(KEY_LOGIN_TIME, -1);
    }
    
    /**
     * Check if user has an active session
     * @param context Application context
     * @return true if token exists, false otherwise
     */
    public static boolean hasActiveSession(Context context) {
        return getToken(context) != null;
    }
    
    /**
     * Check if token is expired based on stored expiration time
     * @param context Application context
     * @return true if token is expired, false otherwise
     */
    public static boolean isTokenExpired(Context context) {
        long expiresIn = getExpiresIn(context);
        long loginTime = getLoginTime(context);
        
        if (expiresIn <= 0 || loginTime <= 0) {
            return false; // If no expiration info, consider as not expired
        }
        
        long currentTime = System.currentTimeMillis();
        long expirationTime = loginTime + (expiresIn * 1000); // Convert seconds to milliseconds
        
        return currentTime >= expirationTime;
    }
    
    /**
     * Get time remaining until token expires
     * @param context Application context
     * @return Time remaining in seconds, or -1 if cannot determine
     */
    public static long getTokenTimeRemaining(Context context) {
        long expiresIn = getExpiresIn(context);
        long loginTime = getLoginTime(context);
        
        if (expiresIn <= 0 || loginTime <= 0) {
            return -1;
        }
        
        long currentTime = System.currentTimeMillis();
        long expirationTime = loginTime + (expiresIn * 1000);
        long timeRemaining = (expirationTime - currentTime) / 1000; // Convert to seconds
        
        return Math.max(0, timeRemaining);
    }
    
    /**
     * Clear all user session data (for logout)
     * @param context Application context
     */
    public static void clearSession(Context context) {
        if (context == null) {
            Log.e(TAG, "Cannot clear session: context is null");
            return;
        }
        
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        
        Log.d(TAG, "User session cleared successfully");
    }
    
    /**
     * Get user information as a UserInfo object
     * @param context Application context
     * @return UserInfo object with user details, or null if no session
     */
    public static UserInfo getUserInfo(Context context) {
        if (!hasActiveSession(context)) {
            return null;
        }
        
        UserInfo userInfo = new UserInfo();
        userInfo.fullName = getFullName(context);
        userInfo.email = getEmail(context);
        userInfo.avatarUrl = getAvatarUrl(context);
        userInfo.role = getRole(context);
        userInfo.isVerified = isVerified(context);
        userInfo.token = getToken(context);
        
        return userInfo;
    }
    
    /**
     * User information model class
     */
    public static class UserInfo {
        public String fullName;
        public String email;
        public String avatarUrl;
        public String role;
        public boolean isVerified;
        public String token;
        
        /**
         * Get display name for user
         * @return User's full name or email as fallback
         */
        public String getDisplayName() {
            if (fullName != null && !fullName.trim().isEmpty()) {
                return fullName;
            }
            return email != null ? email : "User";
        }
        
        /**
         * Check if user is admin
         * @return true if user role is ADMIN
         */
        public boolean isAdmin() {
            return "ADMIN".equalsIgnoreCase(role);
        }
        
        /**
         * Check if user is manager
         * @return true if user role is MANAGER
         */
        public boolean isManager() {
            return "MANAGER".equalsIgnoreCase(role);
        }
        
        /**
         * Check if user is VIP
         * @return true if user role is VIP
         */
        public boolean isVip() {
            return "VIP".equalsIgnoreCase(role);
        }
        
        /**
         * Check if user is regular user
         * @return true if user role is USER
         */
        public boolean isUser() {
            return "USER".equalsIgnoreCase(role);
        }
    }
}
