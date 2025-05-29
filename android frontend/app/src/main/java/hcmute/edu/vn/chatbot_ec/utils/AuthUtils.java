package hcmute.edu.vn.chatbot_ec.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import hcmute.edu.vn.chatbot_ec.activity.Login;
import hcmute.edu.vn.chatbot_ec.network.ApiClient;

/**
 * Utility class for authentication-related operations
 * Contains helpers for logout and auth status checking
 */
public class AuthUtils {    /**
     * Logs out the user by clearing the token and redirecting to login screen
     * @param context Current activity context
     * @param showToast Whether to show a toast message about logout
     */
    public static void logout(Context context, boolean showToast) {
        // Clear the token data
        TokenManager.clearToken(context);
        
        // Reset the Retrofit instance to ensure subsequent requests don't use the token
        ApiClient.resetRetrofitInstance();
        
        if (showToast) {
            Toast.makeText(context, "You have been logged out", Toast.LENGTH_SHORT).show();
        }
        
        // Only navigate to Login if the context is an Activity
        if (context instanceof Activity) {
            // Create login intent with flags to clear back stack
            Intent loginIntent = new Intent(context, Login.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | 
                                Intent.FLAG_ACTIVITY_NEW_TASK | 
                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(loginIntent);
            ((Activity) context).finish();
        }
    }
    
    /**
     * Checks if the user is authenticated
     * @param context Application context
     * @return true if the user has a valid token, false otherwise
     */
    public static boolean isAuthenticated(Context context) {
        return TokenManager.hasToken(context);
    }
      /**
     * Check if user is currently authenticated with a valid token
     * @param context Application context
     * @return true if user has valid token, false otherwise
     */
    public static boolean isUserAuthenticated(Context context) {
        if (context == null) {
            return false;
        }
        
        return TokenManager.hasToken(context);
    }      /**
     * Get authenticated user's information from token
     * Note: Since we're only using TokenManager, user info needs to be retrieved from API
     * @param context Application context
     * @return UserInfo object with basic info, or null if not authenticated
     */
    public static UserInfo getAuthenticatedUserInfo(Context context) {
        if (!isUserAuthenticated(context)) {
            return null;
        }
        
        // With TokenManager only, we don't store user info locally
        // This method returns a minimal UserInfo object
        // For full user details, use the API to fetch current user info
        UserInfo userInfo = new UserInfo();
        userInfo.userId = ""; // Can be extracted from JWT if needed
        userInfo.email = ""; // Needs to be fetched from API
        userInfo.fullName = ""; // Needs to be fetched from API
        userInfo.role = ""; // Needs to be fetched from API
        userInfo.expiration = 0; // Can be extracted from JWT if needed
        
        return userInfo;
    }
      /**
     * Check if token will expire soon
     * Note: With TokenManager only, expiration checking requires JWT parsing
     * @param context Application context
     * @return false (simplified - no expiration checking without SessionManager)
     */
    public static boolean isTokenExpiringSoon(Context context) {
        // Simplified implementation - TokenManager doesn't track expiration
        // To implement properly, you'd need to parse the JWT token
        return false;
    }

    public static void logoutWithBroadcast(Context context, boolean showToast, String reason) {
        // Clear the token
        TokenManager.clearToken(context);
        
        // Reset the Retrofit instance
        ApiClient.resetRetrofitInstance();
        
        // Send broadcast to notify all components
        Intent logoutIntent = new Intent("ACTION_USER_LOGOUT");
        if (reason != null) {
            logoutIntent.putExtra("logout_reason", reason);
        }
        context.sendBroadcast(logoutIntent);
        
        if (showToast) {
            String message = reason != null ? reason : "You have been logged out";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
        
        // Navigate to login if context is an Activity
        if (context instanceof Activity) {
            Intent loginIntent = new Intent(context, Login.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | 
                                Intent.FLAG_ACTIVITY_NEW_TASK | 
                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(loginIntent);
            ((Activity) context).finish();
        }
    }
    
    /**
     * Handle unauthorized API responses (401 errors)
     * @param context Current context
     */
    public static void handleUnauthorizedResponse(Context context) {
        logoutWithBroadcast(context, true, "Session expired. Please login again.");
    }
    
    public static long getTokenTimeRemaining(Context context) {
        if (!isUserAuthenticated(context)) {
            return -1;
        }
        
        // With TokenManager only, we don't track token expiration
        // To implement properly, you'd need to parse the JWT token
        return 0;
    }
    
    /**
     * Inner class to hold user information extracted from JWT
     */
    public static class UserInfo {
        public String userId;
        public String email;
        public String fullName;
        public String role;
        public long expiration;
        
        @Override
        public String toString() {
            return "UserInfo{" +
                    "userId='" + userId + '\'' +
                    ", email='" + email + '\'' +
                    ", fullName='" + fullName + '\'' +
                    ", role='" + role + '\'' +
                    ", expiration=" + expiration +
                    '}';
        }
        
        /**
         * Check if user has admin role
         * @return true if user is admin
         */
        public boolean isAdmin() {
            return role != null && role.equalsIgnoreCase("ADMIN");
        }
        
        /**
         * Check if user has manager role
         * @return true if user is manager
         */
        public boolean isManager() {
            return role != null && role.equalsIgnoreCase("MANAGER");
        }
        
        /**
         * Check if user has VIP role
         * @return true if user is VIP
         */
        public boolean isVip() {
            return role != null && role.equalsIgnoreCase("VIP");
        }
        
        /**
         * Get user display name (full name if available, otherwise email or user ID)
         * @return display name for user
         */
        public String getDisplayName() {
            if (fullName != null && !fullName.trim().isEmpty()) {
                return fullName;
            } else if (email != null && !email.trim().isEmpty()) {
                return email;
            } else if (userId != null && !userId.trim().isEmpty()) {
                return "User " + userId;
            }
            return "Unknown User";
        }
    }
}
