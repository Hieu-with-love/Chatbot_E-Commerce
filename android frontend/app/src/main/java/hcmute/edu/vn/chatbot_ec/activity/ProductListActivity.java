package hcmute.edu.vn.chatbot_ec.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import hcmute.edu.vn.chatbot_ec.R;

/**
 * Activity displaying a list of products with an integrated chatbot assistant.
 * This activity now launches ChatGeminiActivity directly instead of using fragments.
 */
public class ProductListActivity extends AppCompatActivity {

    private FloatingActionButton fabChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_list);

        // Initialize views
        fabChat = findViewById(R.id.fab_chat);
        
        // Set click listeners
        fabChat.setOnClickListener(v -> launchChatActivity());
    }
    
    /**
     * Launch the dedicated ChatGeminiActivity
     */
    private void launchChatActivity() {
        Intent intent = new Intent(this, ChatGeminiActivity.class);
        startActivity(intent);
    }
}