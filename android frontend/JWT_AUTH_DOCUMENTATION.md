# JWT Authentication in Chatbot E-Commerce

## Overview

This application implements secure JWT (JSON Web Token) authentication for API requests. Here's how it works:

### Key Components

1. **TokenManager**: Handles secure storage and retrieval of JWT tokens using SharedPreferences
2. **AuthInterceptor**: Automatically adds JWT token to all API requests
3. **NetworkUtils**: Handles network availability and API error responses
4. **AuthUtils**: Provides authentication utilities like logout and auth status checking
5. **BaseAuthenticatedActivity**: Base activity class for handling token validation and session expiry

### Authentication Flow

1. **Login Process**:
   - User enters credentials
   - App validates credentials with backend
   - Backend returns JWT token
   - App securely stores token with TokenManager
   - Retrofit instance is reset to ensure token is used

2. **API Requests**:
   - AuthInterceptor automatically adds Authorization header with JWT
   - All API requests include token without manual intervention

3. **Token Validation**:
   - App validates token on startup via SplashActivity
   - If token is invalid or expired, user is redirected to login

4. **Error Handling**:
   - 401 Unauthorized responses trigger automatic logout
   - Broadcast system ensures all activities are notified of logout events
   - NetworkUtils handles network connectivity issues

## Implementation Details

### Adding JWT to Requests

```java
public class AuthInterceptor implements Interceptor {
    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder();
        
        // Get token from TokenManager
        String token = TokenManager.getToken(context);

        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }

        Request request = builder.build();
        return chain.proceed(request);
    }
}
```

### Secure Token Storage

```java
public class TokenManager {
    private static final String PREF_NAME = "MyAppPrefs";
    private static final String KEY_JWT_TOKEN = "jwt_token";
    
    public static void saveToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_JWT_TOKEN, token).apply();
    }
    
    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_JWT_TOKEN, null);
    }
}
```

## Security Considerations

1. Token is stored in SharedPreferences with private mode
2. Network errors are properly handled to prevent data leakage
3. Unauthorized responses trigger automatic logout
4. Token validation occurs at app startup

## Future Improvements

1. Add token refresh mechanism
2. Implement token encryption before storage
3. Add biometric authentication for higher security
4. Implement token expiry detection and auto-logout
