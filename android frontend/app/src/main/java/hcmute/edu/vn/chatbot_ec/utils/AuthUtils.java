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
public class AuthUtils {
      /**
     * Logs out the user by clearing the session and redirecting to login screen
     * @param context Current activity context
     * @param showToast Whether to show a toast message about logout
     */
    public static void logout(Context context, boolean showToast) {
        // Clear the session data
        SessionManager.clearSession(context);
        
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
     * @return true if the user has a valid session, false otherwise
     */
    public static boolean isAuthenticated(Context context) {
        return SessionManager.hasActiveSession(context);
    }
      /**
     * Check if user is currently authenticated with a valid non-expired session
     * @param context Application context
     * @return true if user has valid non-expired session, false otherwise
     */
    public static boolean isUserAuthenticated(Context context) {
        if (context == null) {
            return false;
        }
        
        return SessionManager.hasActiveSession(context) && !SessionManager.isTokenExpired(context);
    }
      /**
     * Get authenticated user's information from session
     * @param context Application context
     * @return UserInfo object with user details, or null if not authenticated
     */
    public static UserInfo getAuthenticatedUserInfo(Context context) {
        if (!isUserAuthenticated(context)) {
            return null;
        }
        
        // Get user info from SessionManager
        SessionManager.UserInfo sessionUserInfo = SessionManager.getUserInfo(context);
        if (sessionUserInfo == null) {
            return null;
        }
        
        // Convert SessionManager.UserInfo to AuthUtils.UserInfo for backward compatibility
        UserInfo userInfo = new UserInfo();
        userInfo.userId = ""; // userId not stored in session, could be extracted from JWT if needed
        userInfo.email = sessionUserInfo.email;
        userInfo.fullName = sessionUserInfo.fullName;
        userInfo.role = sessionUserInfo.role;
        userInfo.expiration = 0; // Use session expiration logic instead
        
        return userInfo;
    }
      /**
     * Check if token will expire soon (within 5 minutes)
     * @param context Application context
     * @return true if token expires within 5 minutes
     */
    public static boolean isTokenExpiringSoon(Context context) {
        long timeRemaining = SessionManager.getTokenTimeRemaining(context);
        return timeRemaining > 0 && timeRemaining < 300; // 5 minutes
    }
    
    /**
     * Validate token and handle expiration automatically
     * @param context Application context
     * @param forceLogoutOnExpiry if true, automatically logout if token is expired
     * @return true if token is valid, false otherwise
     */
    public static boolean validateAndHandleToken(Context context, boolean forceLogoutOnExpiry) {
        if (context == null) {
            return false;
        }        if (!isUserAuthenticated(context)) {
            if (forceLogoutOnExpiry && SessionManager.hasActiveSession(context)) {
                // Session exists but is invalid/expired
                logout(context, true);
            }
            return false;
        }
        
        if (isTokenExpiringSoon(context)) {
            // Show warning about upcoming expiration
            Toast.makeText(context, "Your session will expire soon. Please refresh or login again.", Toast.LENGTH_LONG).show();
        }
        
        return true;
    }
      /**
     * Enhanced logout with broadcast support for real-time updates
     * @param context Current context
     * @param showToast Whether to show logout message
     * @param reason Reason for logout (optional)
     */
    public static void logoutWithBroadcast(Context context, boolean showToast, String reason) {
        // Clear the session
        SessionManager.clearSession(context);
        
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
    }    public static long getTokenTimeRemaining(Context context) {
        if (!isUserAuthenticated(context)) {
            return -1;
        }
        
        return SessionManager.getTokenTimeRemaining(context);
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
