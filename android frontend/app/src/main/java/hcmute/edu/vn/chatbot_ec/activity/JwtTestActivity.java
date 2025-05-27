package hcmute.edu.vn.chatbot_ec.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.utils.AuthUtils;
import hcmute.edu.vn.chatbot_ec.utils.JwtUtils;
import hcmute.edu.vn.chatbot_ec.utils.TokenManager;

/**
 * Demo activity to test JWT authentication features
 * Remove this in production
 */
public class JwtTestActivity extends AppCompatActivity {
    private static final String TAG = "JwtTestActivity";
    
    private TextView tvTokenStatus;
    private TextView tvUserInfo;
    private Button btnCreateTestToken;
    private Button btnClearToken;
    private Button btnRefreshInfo;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jwt_test);
        
        initViews();
        setupClickListeners();
        refreshTokenInfo();
    }
    
    private void initViews() {
        tvTokenStatus = findViewById(R.id.tv_token_status);
        tvUserInfo = findViewById(R.id.tv_user_info);
        btnCreateTestToken = findViewById(R.id.btn_create_test_token);
        btnClearToken = findViewById(R.id.btn_clear_token);
        btnRefreshInfo = findViewById(R.id.btn_refresh_info);
    }
    
    private void setupClickListeners() {
        btnCreateTestToken.setOnClickListener(v -> createTestToken());
        btnClearToken.setOnClickListener(v -> clearToken());
        btnRefreshInfo.setOnClickListener(v -> refreshTokenInfo());
    }
    
    private void createTestToken() {
        String testToken = JwtUtils.createTestToken(
            "123", 
            "Nguyễn Văn Test", 
            "test@example.com", 
            "ADMIN"
        );
        
        if (testToken != null) {
            TokenManager.saveToken(this, testToken);
            Log.d(TAG, "Test token created: " + testToken);
            Toast.makeText(this, "Test token created!", Toast.LENGTH_SHORT).show();
            refreshTokenInfo();
        } else {
            Toast.makeText(this, "Failed to create test token", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void clearToken() {
        TokenManager.clearToken(this);
        Toast.makeText(this, "Token cleared!", Toast.LENGTH_SHORT).show();
        refreshTokenInfo();
    }
    
    private void refreshTokenInfo() {
        StringBuilder statusText = new StringBuilder();
        StringBuilder userInfoText = new StringBuilder();
        
        // Check authentication status
        boolean isAuthenticated = AuthUtils.isUserAuthenticated(this);
        statusText.append("Authenticated: ").append(isAuthenticated).append("\n");
        
        String token = TokenManager.getToken(this);
        if (token != null) {
            statusText.append("Token exists: Yes\n");
            statusText.append("Token format valid: ").append(JwtUtils.isValidTokenFormat(token)).append("\n");
            statusText.append("Token expired: ").append(JwtUtils.isTokenExpired(token)).append("\n");
            
            long timeRemaining = AuthUtils.getTokenTimeRemaining(this);
            if (timeRemaining > 0) {
                statusText.append("Time remaining: ").append(timeRemaining).append(" seconds\n");
            }
            
            // Get user info
            AuthUtils.UserInfo userInfo = AuthUtils.getAuthenticatedUserInfo(this);
            if (userInfo != null) {
                userInfoText.append("User Info:\n");
                userInfoText.append("User ID: ").append(userInfo.userId).append("\n");
                userInfoText.append("Full Name: ").append(userInfo.fullName).append("\n");
                userInfoText.append("Email: ").append(userInfo.email).append("\n");
                userInfoText.append("Role: ").append(userInfo.role).append("\n");
                userInfoText.append("Display Name: ").append(userInfo.getDisplayName()).append("\n");
                userInfoText.append("Is Admin: ").append(userInfo.isAdmin()).append("\n");
                userInfoText.append("Is Manager: ").append(userInfo.isManager()).append("\n");
                userInfoText.append("Is VIP: ").append(userInfo.isVip()).append("\n");
                userInfoText.append("Expiration: ").append(userInfo.expiration).append("\n");
            } else {
                userInfoText.append("No user info available");
            }
        } else {
            statusText.append("Token exists: No\n");
            userInfoText.append("No user info available");
        }
        
        tvTokenStatus.setText(statusText.toString());
        tvUserInfo.setText(userInfoText.toString());
    }
}
