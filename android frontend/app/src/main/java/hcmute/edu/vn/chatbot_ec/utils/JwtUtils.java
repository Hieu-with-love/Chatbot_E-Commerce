package hcmute.edu.vn.chatbot_ec.utils;

import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

/**
 * Utility class for JWT token validation and decoding
 * Provides methods to check token expiry and extract user information
 */
public class JwtUtils {
    private static final String TAG = "JwtUtils";
    
    /**
     * Checks if a JWT token is expired
     * @param token The JWT token to check
     * @return true if token is expired or invalid, false otherwise
     */
    public static boolean isTokenExpired(String token) {
        if (token == null || token.trim().isEmpty()) {
            Log.d(TAG, "Token is null or empty");
            return true;
        }
        
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                Log.e(TAG, "Invalid JWT token format");
                return true;
            }
            
            // Decode payload (second part)
            String payload = parts[1];
            // Add padding if needed for Base64 decoding
            while (payload.length() % 4 != 0) {
                payload += "=";
            }
            
            byte[] decodedBytes = Base64.decode(payload, Base64.URL_SAFE);
            String decodedPayload = new String(decodedBytes, StandardCharsets.UTF_8);
            
            JSONObject payloadJson = new JSONObject(decodedPayload);
            
            // Check if 'exp' field exists
            if (!payloadJson.has("exp")) {
                Log.w(TAG, "Token does not contain expiration field");
                return false; // If no exp field, consider token valid
            }
            
            long expirationTime = payloadJson.getLong("exp");
            long currentTime = System.currentTimeMillis() / 1000; // Convert to seconds
            
            boolean isExpired = currentTime >= expirationTime;
            Log.d(TAG, "Token expiration check - Current: " + currentTime + ", Exp: " + expirationTime + ", Expired: " + isExpired);
            
            return isExpired;
            
        } catch (Exception e) {
            Log.e(TAG, "Error checking token expiration", e);
            return true; // Consider invalid token as expired
        }
    }
    
    /**
     * Extracts user information from JWT token payload
     * @param token The JWT token to decode
     * @return JSONObject containing user information, or null if invalid
     */
    public static JSONObject getUserInfoFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            Log.d(TAG, "Token is null or empty");
            return null;
        }
        
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                Log.e(TAG, "Invalid JWT token format");
                return null;
            }
            
            // Decode payload (second part)
            String payload = parts[1];
            // Add padding if needed for Base64 decoding
            while (payload.length() % 4 != 0) {
                payload += "=";
            }
            
            byte[] decodedBytes = Base64.decode(payload, Base64.URL_SAFE);
            String decodedPayload = new String(decodedBytes, StandardCharsets.UTF_8);
            
            JSONObject payloadJson = new JSONObject(decodedPayload);
            Log.d(TAG, "Successfully decoded token payload");
            
            return payloadJson;
            
        } catch (Exception e) {
            Log.e(TAG, "Error decoding token payload", e);
            return null;
        }
    }
    
    /**
     * Extracts user ID from JWT token
     * @param token The JWT token to decode
     * @return User ID string, or null if not found
     */
    public static String getUserIdFromToken(String token) {
        JSONObject userInfo = getUserInfoFromToken(token);
        if (userInfo != null) {
            try {
                // Try different possible field names for user ID
                if (userInfo.has("userId")) {
                    return userInfo.getString("userId");
                } else if (userInfo.has("sub")) {
                    return userInfo.getString("sub");
                } else if (userInfo.has("id")) {
                    return userInfo.getString("id");
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error extracting user ID from token", e);
            }
        }
        return null;
    }
    
    /**
     * Extracts email from JWT token
     * @param token The JWT token to decode
     * @return Email string, or null if not found
     */
    public static String getEmailFromToken(String token) {
        JSONObject userInfo = getUserInfoFromToken(token);
        if (userInfo != null) {
            try {
                if (userInfo.has("email")) {
                    return userInfo.getString("email");
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error extracting email from token", e);
            }
        }
        return null;
    }
    
    /**
     * Extracts full name from JWT token
     * @param token The JWT token to decode
     * @return Full name string, or null if not found
     */
    public static String getFullNameFromToken(String token) {
        JSONObject userInfo = getUserInfoFromToken(token);
        if (userInfo != null) {
            try {
                // Try different possible field names for full name
                if (userInfo.has("fullName")) {
                    return userInfo.getString("fullName");
                } else if (userInfo.has("name")) {
                    return userInfo.getString("name");
                } else if (userInfo.has("displayName")) {
                    return userInfo.getString("displayName");
                } else if (userInfo.has("username")) {
                    return userInfo.getString("username");
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error extracting full name from token", e);
            }
        }
        return null;
    }
    
    /**
     * Extracts role from JWT token
     * @param token The JWT token to decode
     * @return Role string, or null if not found
     */
    public static String getRoleFromToken(String token) {
        JSONObject userInfo = getUserInfoFromToken(token);
        if (userInfo != null) {
            try {
                // Try different possible field names for role
                if (userInfo.has("role")) {
                    return userInfo.getString("role");
                } else if (userInfo.has("authorities")) {
                    return userInfo.getString("authorities");
                } else if (userInfo.has("scope")) {
                    return userInfo.getString("scope");
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error extracting role from token", e);
            }
        }
        return null;
    }
    
    /**
     * Extracts expiration timestamp from JWT token
     * @param token The JWT token to decode
     * @return Expiration timestamp in seconds, or -1 if not found
     */
    public static long getExpirationFromToken(String token) {
        JSONObject userInfo = getUserInfoFromToken(token);
        if (userInfo != null) {
            try {
                if (userInfo.has("exp")) {
                    return userInfo.getLong("exp");
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error extracting expiration from token", e);
            }
        }
        return -1;
    }
    
    /**
     * Validates JWT token format and structure
     * @param token The JWT token to validate
     * @return true if token format is valid, false otherwise
     */
    public static boolean isValidTokenFormat(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        String[] parts = token.split("\\.");
        return parts.length == 3;
    }
    
    /**
     * Helper method to create a sample JWT token for testing purposes
     * THIS IS FOR TESTING ONLY - DO NOT USE IN PRODUCTION
     * @param userId User ID
     * @param fullName User's full name
     * @param email User's email
     * @param role User's role
     * @return Base64 encoded JWT-like token for testing
     */
    public static String createTestToken(String userId, String fullName, String email, String role) {
        try {
            // Create header (standard JWT header)
            JSONObject header = new JSONObject();
            header.put("alg", "HS256");
            header.put("typ", "JWT");
            
            // Create payload with user information
            JSONObject payload = new JSONObject();
            payload.put("userId", userId);
            payload.put("sub", userId);
            payload.put("fullName", fullName);
            payload.put("email", email);
            payload.put("role", role);
            payload.put("iat", System.currentTimeMillis() / 1000); // issued at
            payload.put("exp", (System.currentTimeMillis() / 1000) + 86400); // expires in 24 hours
            
            // Encode parts
            String encodedHeader = Base64.encodeToString(
                header.toString().getBytes(StandardCharsets.UTF_8), 
                Base64.URL_SAFE | Base64.NO_WRAP
            );
            String encodedPayload = Base64.encodeToString(
                payload.toString().getBytes(StandardCharsets.UTF_8), 
                Base64.URL_SAFE | Base64.NO_WRAP
            );
            
            // Create fake signature (for testing only)
            String fakeSignature = Base64.encodeToString(
                "fake_signature_for_testing".getBytes(StandardCharsets.UTF_8), 
                Base64.URL_SAFE | Base64.NO_WRAP
            );
            
            return encodedHeader + "." + encodedPayload + "." + fakeSignature;
            
        } catch (JSONException e) {
            Log.e(TAG, "Error creating test token", e);
            return null;
        }
    }
}
