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
     * Logs out the user by clearing the token and redirecting to login screen
     * @param context Current activity context
     * @param showToast Whether to show a toast message about logout
     */
    public static void logout(Context context, boolean showToast) {
        // Clear the token
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
}
