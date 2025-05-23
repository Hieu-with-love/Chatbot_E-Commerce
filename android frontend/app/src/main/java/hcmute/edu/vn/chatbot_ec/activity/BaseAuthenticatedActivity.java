package hcmute.edu.vn.chatbot_ec.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import hcmute.edu.vn.chatbot_ec.utils.AuthUtils;

/**
 * Base activity that all authenticated activities should extend
 * Handles token validation, session expiry, and network connectivity
 */
public abstract class BaseAuthenticatedActivity extends AppCompatActivity {
    private static final String TAG = "BaseAuthActivity";
    
    // Action for logout broadcast
    public static final String ACTION_LOGOUT = "hcmute.edu.vn.chatbot_ec.ACTION_LOGOUT";
    
    // Broadcast receiver for logout events
    private BroadcastReceiver logoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_LOGOUT.equals(intent.getAction())) {
                Log.d(TAG, "Logout broadcast received");
                finish();
            }
        }
    };
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if user is authenticated
        if (!AuthUtils.isAuthenticated(this)) {
            Log.d(TAG, "User is not authenticated, redirecting to login");
            AuthUtils.logout(this, false);
            finish();
            return;
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        // Register for logout broadcasts
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(logoutReceiver, new IntentFilter(ACTION_LOGOUT));
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        // Unregister receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(logoutReceiver);
    }
    
    /**
     * Sends a logout broadcast to all activities
     * This is useful when a 401 Unauthorized response is received
     */
    protected void broadcastLogout() {
        Log.d(TAG, "Broadcasting logout to all activities");
        Intent intent = new Intent(ACTION_LOGOUT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        
        // Logout the user
        AuthUtils.logout(this, true);
    }
    
    /**
     * Handle unauthorized errors (401)
     * This should be called by activities when they receive 401 errors
     */
    protected void handleUnauthorized() {
        Log.d(TAG, "Handling unauthorized error");
        broadcastLogout();
    }
}
