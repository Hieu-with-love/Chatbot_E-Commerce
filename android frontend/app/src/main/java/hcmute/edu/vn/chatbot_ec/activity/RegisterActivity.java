package hcmute.edu.vn.chatbot_ec.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.enums.HTTP_STATUS;
import hcmute.edu.vn.chatbot_ec.model.User;
import hcmute.edu.vn.chatbot_ec.network.ApiClient;
import hcmute.edu.vn.chatbot_ec.network.AuthApiService;
import hcmute.edu.vn.chatbot_ec.request.RegisterRequest;
import hcmute.edu.vn.chatbot_ec.response.ResponseData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText fullNameEditText, emailEditText, phoneEditText, passwordEditText, confirmPasswordEditText;
    private TextInputLayout fullNameInputLayout, emailInputLayout, phoneInputLayout, passwordInputLayout, confirmPasswordInputLayout;
    private Button registerButton;
    private TextView loginTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        // Hide the status bar for a more immersive experience
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_register);
        
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
        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        
        // TextInputLayout for validation feedback
        fullNameInputLayout = findViewById(R.id.fullNameInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        phoneInputLayout = findViewById(R.id.phoneInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        confirmPasswordInputLayout = findViewById(R.id.confirmPasswordInputLayout);
        
        // Buttons and clickable text
        registerButton = findViewById(R.id.registerButton);
        loginTextView = findViewById(R.id.loginTextView);
    }
    
    private void setupClickListeners() {
        // Register button click listener
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    // Perform registration process
                    RegisterRequest registerRequest = new RegisterRequest();
                    registerRequest.setEmail(fullNameEditText.getText().toString());
                    registerRequest.setPhone(phoneEditText.getText().toString());
                    registerRequest.setFullName(fullNameEditText.getText().toString());
                    registerRequest.setPassword(passwordEditText.getText().toString());
                    registerRequest.setConfirmPassword(confirmPasswordEditText.getText().toString());

                    // Get api service
                    AuthApiService authApiService = ApiClient.getAuthApiService();

                    authApiService.register(registerRequest).enqueue(
                            new Callback<ResponseData>() {
                                @Override
                                public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                                    if (response.body()!=null && response.body().getStatus() == HTTP_STATUS.CREATED.getCode()){
                                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                        navigateToLogin();
                                    }else{
                                        Toast.makeText(RegisterActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseData> call, Throwable t) {
                                    if (t instanceof IOException) {
                                        // Lỗi kết nối mạng
                                        Toast.makeText(RegisterActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Lỗi khác (ví dụ lỗi JSON, lỗi code...)
                                        Log.e("API_ERROR", "Lỗi không xác định: " + t.getMessage());
                                    }
                                }
                            }
                    );

                    // This is where you'd implement your registration logic
                    // For now, just show a success message and navigate
                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    navigateToMain();
                }
            }
        });
        
        // Login text click listener (to navigate to login page)
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to login page (to be implemented later)
                // For now, just navigate to the main screen
                navigateToLogin();
            }
        });
    }

    private boolean validateForm() {
        boolean isValid = true;
        
        // Validate full name
        if (TextUtils.isEmpty(fullNameEditText.getText())) {
            fullNameInputLayout.setError("Please enter your full name");
            isValid = false;
        } else {
            fullNameInputLayout.setError(null);
        }
        
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

        // validate phone
        String phone = phoneEditText.getText().toString().trim();
        String phoneRegex = "^0[3|5|7|8|9]\\d{8}$";
        if (TextUtils.isEmpty(phone)) {
            phoneInputLayout.setError("Please enter your phone number");
            isValid = false;
        } else if (!phone.matches(phoneRegex)) {
            phoneInputLayout.setError("Please enter a valid phone number");
            isValid = false;
        } else {
            phoneInputLayout.setError(null);
        }

        // Validate password
        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordInputLayout.setError("Please enter a password");
            isValid = false;
        } else if (password.length() < 6) {
            passwordInputLayout.setError("Password must be at least 6 characters");
            isValid = false;
        } else {
            passwordInputLayout.setError(null);
        }
        
        // Validate password confirmation
        String confirmPassword = confirmPasswordEditText.getText().toString();
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordInputLayout.setError("Please confirm your password");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordInputLayout.setError("Passwords do not match");
            isValid = false;
        } else {
            confirmPasswordInputLayout.setError(null);
        }
        
        return isValid;
    }
    
    private void navigateToLogin() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
    private void navigateToMain() {
        Intent intent = new Intent(this, ProductListActivity.class);
        startActivity(intent);
        finish();
    }
}