package hcmute.edu.vn.chatbot_ec.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

import hcmute.edu.vn.chatbot_ec.MainActivity;
import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.enums.HTTP_STATUS;
import hcmute.edu.vn.chatbot_ec.network.ApiClient;
import hcmute.edu.vn.chatbot_ec.network.AuthApiService;
import hcmute.edu.vn.chatbot_ec.request.LoginRequest;
import hcmute.edu.vn.chatbot_ec.response.ResponseData;
import hcmute.edu.vn.chatbot_ec.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private TextInputLayout emailInputLayout, passwordInputLayout;
    private Button loginButton;
    private TextView registerTextView;
    private FrameLayout loginSuccessContainer;
    private ImageView loginSuccessCheckmark;
    private TextView welcomeMessageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        // Hide the status bar for a more immersive experience
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_login);
        
        // Initialize UI components
        initializeViews();
        
        // Set up window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Set up click listeners
        setupClickListeners();
    }
    
    private void initializeViews() {
        // TextInputEditText fields
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        
        // TextInputLayout for validation feedback
        emailInputLayout = findViewById(R.id.emailInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        
        // Buttons and clickable text
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);
        
        // Login success animation views
        loginSuccessContainer = findViewById(R.id.loginSuccessContainer);
        loginSuccessCheckmark = findViewById(R.id.loginSuccessCheckmark);
        welcomeMessageTextView = findViewById(R.id.welcomeMessageTextView);
    }
    
    private void setupClickListeners() {
        // Login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    // Check network availability before making the API call
                    if (!NetworkUtils.isNetworkAvailable(Login.this)) {
                        NetworkUtils.handleNetworkError(Login.this, new IOException("No network connection"));
                        return;
                    }

                    // Create login request
                    LoginRequest loginRequest = new LoginRequest();
                    loginRequest.setEmail(emailEditText.getText().toString().trim());
                    loginRequest.setPassword(passwordEditText.getText().toString());

                    // Get auth API service
                    AuthApiService authApiService = ApiClient.getAuthApiService();
                    
                    // Call login API
                    authApiService.login(loginRequest).enqueue(
                        new Callback<ResponseData>() {
                            @Override
                            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                                if (response.body() != null && response.body().getStatus() == HTTP_STATUS.OK.getCode()) {
                                    // Show success animation
                                    showLoginSuccessAnimation();
                                } else {
                                    Toast.makeText(Login.this, "Login failed! Invalid credentials.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseData> call, Throwable t) {
                                NetworkUtils.handleNetworkError(Login.this, t);
                            }
                        }
                    );
                }
            }
        });
        
        // Register text click listener
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToRegister();
            }
        });
    }

    private boolean validateForm() {
        boolean isValid = true;
        
        // Validate email
        String email = emailEditText.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            emailInputLayout.setError("Please enter your email");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.setError("Please enter a valid email address");
            isValid = false;
        } else {
            emailInputLayout.setError(null);
        }
        
        // Validate password
        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordInputLayout.setError("Please enter your password");
            isValid = false;
        } else {
            passwordInputLayout.setError(null);
        }
        
        return isValid;
    }
    
    private void showLoginSuccessAnimation() {
        // Show the success container
        loginSuccessContainer.setVisibility(View.VISIBLE);
        
        // Set initial state for animation
        loginSuccessCheckmark.setScaleX(0);
        loginSuccessCheckmark.setScaleY(0);
        loginSuccessCheckmark.setAlpha(0f);
        welcomeMessageTextView.setAlpha(0f);
        
        // Create and start checkmark animation
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(loginSuccessCheckmark, "scaleX", 0f, 1.2f, 1f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(loginSuccessCheckmark, "scaleY", 0f, 1.2f, 1f);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(loginSuccessCheckmark, "alpha", 0f, 1f);
        
        AnimatorSet checkmarkAnimSet = new AnimatorSet();
        checkmarkAnimSet.playTogether(scaleXAnimator, scaleYAnimator, alphaAnimator);
        checkmarkAnimSet.setDuration(500);
        checkmarkAnimSet.setInterpolator(new AccelerateDecelerateInterpolator());
        
        // Create welcome message animation
        ObjectAnimator welcomeAlphaAnimator = ObjectAnimator.ofFloat(welcomeMessageTextView, "alpha", 0f, 1f);
        welcomeAlphaAnimator.setDuration(500);
        welcomeAlphaAnimator.setStartDelay(300);
        
        // Play all animations together
        AnimatorSet fullAnimSet = new AnimatorSet();
        fullAnimSet.playSequentially(checkmarkAnimSet, welcomeAlphaAnimator);
        fullAnimSet.start();
        
        // Navigate to main screen after animation finishes
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToMain();
            }
        }, 2000);
    }
    
    private void navigateToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
    
    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}