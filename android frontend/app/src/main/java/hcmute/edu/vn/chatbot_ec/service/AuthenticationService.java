package hcmute.edu.vn.chatbot_ec.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import hcmute.edu.vn.chatbot_ec.utils.AuthUtils;
import hcmute.edu.vn.chatbot_ec.utils.SessionManager;

/**
 * Background service for managing authentication state and token lifecycle
 */
public class AuthenticationService extends Service {
    private static final String TAG = "AuthenticationService";
    private static final long TOKEN_CHECK_INTERVAL_MINUTES = 5;
    
    private ScheduledExecutorService scheduler;
    private boolean isRunning = false;
    
    // Authentication broadcast actions
    public static final String ACTION_USER_LOGOUT = "ACTION_USER_LOGOUT";
    public static final String ACTION_USER_LOGIN = "ACTION_USER_LOGIN";
    public static final String ACTION_TOKEN_EXPIRED = "hcmute.edu.vn.chatbot_ec.TOKEN_EXPIRED";
    public static final String ACTION_TOKEN_REFRESHED = "hcmute.edu.vn.chatbot_ec.TOKEN_REFRESHED";
    public static final String ACTION_AUTHENTICATION_STATE_CHANGED = "hcmute.edu.vn.chatbot_ec.AUTH_STATE_CHANGED";
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "AuthenticationService created");
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "AuthenticationService started");
        
        if (!isRunning) {
            startTokenMonitoring();
            isRunning = true;
        }
        
        // Service should restart if killed by the system
        return START_STICKY;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "AuthenticationService destroyed");
        
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        isRunning = false;
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    /**
     * Start periodic token monitoring
     */
    private void startTokenMonitoring() {
        scheduler.scheduleWithFixedDelay(this::checkTokenStatus, 
                0, TOKEN_CHECK_INTERVAL_MINUTES, TimeUnit.MINUTES);
    }
      /**
     * Check token status and handle expiration/refresh
     */
    private void checkTokenStatus() {
        try {
            Context context = getApplicationContext();
            String token = SessionManager.getToken(context);
            
            if (token == null) {
                Log.d(TAG, "No session found");
                broadcastAuthenticationStateChanged(false);
                return;
            }
            
            // Check if token is expiring soon
            if (AuthUtils.isTokenExpiringSoon(context)) {
                Log.d(TAG, "Token is expiring soon");
                handleTokenExpiringSoon();
            }
            
            // Validate current token
            if (!AuthUtils.validateAndHandleToken(context, false)) {
                Log.d(TAG, "Token validation failed");
                broadcastAuthenticationStateChanged(false);
            } else {
                Log.d(TAG, "Token is valid");
                broadcastAuthenticationStateChanged(true);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error checking token status", e);
        }
    }
    
    /**
     * Handle token that is expiring soon
     */
    private void handleTokenExpiringSoon() {
        // Broadcast that token is expiring
        Intent intent = new Intent(ACTION_TOKEN_EXPIRED);
        intent.putExtra("message", "Token sắp hết hạn");
        sendBroadcast(intent);
        
        // TODO: Implement token refresh mechanism here
        // For now, we'll just log and let the validation handle it
        Log.w(TAG, "Token is expiring soon - refresh mechanism not implemented");
    }
    
    /**
     * Broadcast authentication state change
     */
    private void broadcastAuthenticationStateChanged(boolean isAuthenticated) {
        Intent intent = new Intent(ACTION_AUTHENTICATION_STATE_CHANGED);
        intent.putExtra("isAuthenticated", isAuthenticated);
        sendBroadcast(intent);
        
        Log.d(TAG, "Broadcasted authentication state: " + isAuthenticated);
    }
    
    /**
     * Start the authentication service
     */
    public static void startService(Context context) {
        Intent serviceIntent = new Intent(context, AuthenticationService.class);
        context.startService(serviceIntent);
    }
    
    /**
     * Stop the authentication service
     */
    public static void stopService(Context context) {
        Intent serviceIntent = new Intent(context, AuthenticationService.class);
        context.stopService(serviceIntent);
    }
}
