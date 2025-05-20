package hcmute.edu.vn.chatbot_ec.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.utils.NetworkUtils;

public class NetworkError extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_network_error);
        
        // Set up window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up retry button click listener
        Button retryButton = findViewById(R.id.retryButton);
        retryButton.setOnClickListener(v -> checkNetworkAndRetry());
    }

    private void checkNetworkAndRetry() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            // Network is available, go back to previous activity
            finish();
        } else {
            // Network is still unavailable
            Toast.makeText(this, R.string.network_error_description, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check network status when activity resumes
        if (NetworkUtils.isNetworkAvailable(this)) {
            finish();
        }
    }
}