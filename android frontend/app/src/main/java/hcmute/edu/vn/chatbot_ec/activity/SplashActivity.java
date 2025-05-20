package hcmute.edu.vn.chatbot_ec.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.utils.ActivityUtils;

public class SplashActivity extends AppCompatActivity {
    private Button getStartedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide the status bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_splash);

        initViews();

        // Setting up click listeners        Button getStartedButton = findViewById(R.id.getStartedButton);
        getStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToRegister();
            }
        });

        TextView signInTextView = findViewById(R.id.signInTextView);
        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to sign in screen (implement later)
                ActivityUtils.navigateToActivity(SplashActivity.this, Login.class);
            }
        });

        // Auto navigate after delay
        // new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
        //     @Override
        //     public void run() {
        //         navigateToMain();
        //     }
        // }, 3000); // 3 seconds delay
    }

    private void initViews() {
        getStartedBtn = findViewById(R.id.getStartedButton);
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, ProductListActivity.class);
        startActivity(intent);
        finish();
    }
    
    private void navigateToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}
