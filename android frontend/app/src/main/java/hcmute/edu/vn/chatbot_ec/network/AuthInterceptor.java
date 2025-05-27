package hcmute.edu.vn.chatbot_ec.network;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import hcmute.edu.vn.chatbot_ec.utils.SessionManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Interceptor for adding Authorization headers to outgoing requests
 * Automatically adds JWT token from SessionManager to all API requests
 */
public class AuthInterceptor implements Interceptor {
    private static final String TAG = "AuthInterceptor";
    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder();
        
        // Get token from SessionManager
        String token = SessionManager.getToken(context);
        String tokenType = SessionManager.getTokenType(context);

        if (token != null) {
            Log.d(TAG, "Adding Authorization header to request: " + original.url());
            builder.header("Authorization", tokenType + " " + token);
        } else {
            Log.d(TAG, "No JWT token available for request: " + original.url());
        }

        // Add common headers if needed
        builder.header("Accept", "application/json");
        
        Request request = builder.build();
        return chain.proceed(request);
    }
}