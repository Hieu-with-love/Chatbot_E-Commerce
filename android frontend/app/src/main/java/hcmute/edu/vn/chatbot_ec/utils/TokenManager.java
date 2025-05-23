package hcmute.edu.vn.chatbot_ec.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Utility class to manage JWT tokens
 * Handles secure storage, retrieval, and validation of authentication tokens
 */
public class TokenManager {
    private static final String TAG = "TokenManager";
    private static final String PREF_NAME = "MyAppPrefs";
    private static final String KEY_JWT_TOKEN = "jwt_token";
    
    /**
     * Save JWT token to SharedPreferences
     * @param context Application context
     * @param token JWT token received from authentication
     */
    public static void saveToken(Context context, String token) {
        if (context == null) {
            Log.e(TAG, "Cannot save token: context is null");
            return;
        }
        
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_JWT_TOKEN, token);
        editor.apply();
        
        Log.d(TAG, "JWT token saved successfully");
    }
    
    /**
     * Retrieve JWT token from SharedPreferences
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
     * Remove JWT token from SharedPreferences (for logout)
     * @param context Application context
     */
    public static void clearToken(Context context) {
        if (context == null) {
            Log.e(TAG, "Cannot clear token: context is null");
            return;
        }
        
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_JWT_TOKEN);
        editor.apply();
        
        Log.d(TAG, "JWT token cleared successfully");
    }
    
    /**
     * Check if user is logged in (has a token)
     * @param context Application context
     * @return true if token exists, false otherwise
     */
    public static boolean hasToken(Context context) {
        return getToken(context) != null;
    }
}
