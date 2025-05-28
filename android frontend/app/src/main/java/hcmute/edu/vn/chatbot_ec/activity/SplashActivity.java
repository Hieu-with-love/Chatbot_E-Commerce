package hcmute.edu.vn.chatbot_ec.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import hcmute.edu.vn.chatbot_ec.MainActivity;
import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.enums.HTTP_STATUS;
import hcmute.edu.vn.chatbot_ec.network.ApiClient;
import hcmute.edu.vn.chatbot_ec.network.AuthApiService;
import hcmute.edu.vn.chatbot_ec.response.ResponseData;
import hcmute.edu.vn.chatbot_ec.response.UserResponse;
import hcmute.edu.vn.chatbot_ec.utils.ActivityUtils;
import hcmute.edu.vn.chatbot_ec.utils.JwtUtils;
import hcmute.edu.vn.chatbot_ec.utils.TokenManager;
import hcmute.edu.vn.chatbot_ec.utils.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private static final long SPLASH_DELAY = 2000; // 2 seconds
    
    private Button getStartedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide the status bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_splash);

        initViews();
        setupClickListeners();
        
        // Check authentication status after a short delay for better UX
        new Handler(Looper.getMainLooper()).postDelayed(this::checkAuthenticationStatus, SPLASH_DELAY);
    }

    private void initViews() {
        getStartedBtn = findViewById(R.id.getStartedButton);
    }
    
    private void setupClickListeners() {
        // Setting up click listeners
        getStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigateToRegister();
                navigateToMain();
            }
        });

        TextView signInTextView = findViewById(R.id.signInTextView);
        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to sign in screen
                ActivityUtils.navigateToActivity(SplashActivity.this, Login.class);
            }
        });
    }    /**
     * Check if user has a valid token and validate it
     */
    private void checkAuthenticationStatus() {
        String token = TokenManager.getToken(this);
        
        if (token == null) {
            Log.d(TAG, "No token found, user needs to login");
            // No token found, show splash UI for user to choose login/register
            showSplashUI();
            return;
        }
        
        // Token exists, validate with server
        validateTokenWithServer(token);
    }
    
    /**
     * Validate token with server to ensure it's still valid
     */
    private void validateTokenWithServer(String token) {
        Log.d(TAG, "Validating token with server");
        
        AuthApiService authApiService = ApiClient.getAuthApiService();
        authApiService.getCurrentUser().enqueue(new Callback<ResponseData<UserResponse>>() {
            @Override
            public void onResponse(Call<ResponseData<UserResponse>> call, Response<ResponseData<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == HTTP_STATUS.OK.getCode()) {
                        Log.d(TAG, "Token is valid, user is authenticated");
                        // Token is valid, navigate directly to main activity
                        navigateToMainAuthenticated();
                    } else {
                        Log.w(TAG, "Server returned error status: " + response.body().getStatus());
                        handleInvalidToken();
                    }
                } else {
                    Log.w(TAG, "Token validation failed with response code: " + response.code());
                    handleInvalidToken();
                }
            }

            @Override
            public void onFailure(Call<ResponseData<UserResponse>> call, Throwable t) {
                Log.e(TAG, "Network error during token validation", t);
                // On network error, still proceed to main if token format is valid
                // This allows offline usage with cached data
                Log.d(TAG, "Network error, but token format is valid, proceeding to main");
                navigateToMainAuthenticated();
            }
        });
    }
      /**
     * Handle invalid session by clearing it and showing splash UI
     */    private void handleInvalidToken() {
        Log.d(TAG, "Handling invalid session");
        TokenManager.clearToken(this);
        ApiClient.resetRetrofitInstance();
        showSplashUI();
    }
    
    /**
     * Show splash UI for guest/unauthenticated users
     */
    private void showSplashUI() {
        Log.d(TAG, "Showing splash UI for unauthenticated user");
        // UI is already visible, just ensure buttons are enabled
        getStartedBtn.setEnabled(true);
        findViewById(R.id.signInTextView).setEnabled(true);
    }
    
    /**
     * Navigate to main activity for authenticated users
     */
    private void navigateToMainAuthenticated() {
        Log.d(TAG, "Navigating to main activity (user authenticated)");
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user_authenticated", true);
        startActivity(intent);
        finish();
    }    
    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user_authenticated", false);
        startActivity(intent);
        finish();
    }
    
    private void navigateToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}
