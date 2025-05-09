package hcmute.edu.vn.chatbot_ec;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.chatbot_ec.adapter.ChatAdapter;
import hcmute.edu.vn.chatbot_ec.config.BuildConfig;
import hcmute.edu.vn.chatbot_ec.helper.GeminiHelper;
import hcmute.edu.vn.chatbot_ec.model.Message;

public class ChatGemini extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText edtPrompt;
    private ImageButton btnGenerate;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_gemini);

        initView();
        setupRecyclerView();

        btnGenerate.setOnClickListener(v -> generateResponse());
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);  // Messages show from bottom
        
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);
    }

    private void generateResponse() {
        String userInput = edtPrompt.getText().toString();
        Log.d("GeminiAPI", "User Input: " + userInput);

        if (!userInput.isEmpty()) {
            // Add user message to the chat
            addMessageToChat(userInput, Message.SENT_BY_USER);
            
            // Clear input field
            edtPrompt.setText("");
            
            // Get response from Gemini
            generateText(userInput);
        }
    }

    private void addMessageToChat(String content, int sentBy) {
        Message message = new Message(content, sentBy);
        chatAdapter.addMessage(message);
        
        // Scroll to the bottom to show the latest message
        recyclerView.smoothScrollToPosition(messageList.size() - 1);
    }

    private void generateText(String prompt) {
        GeminiHelper.generateResponse(
                prompt,
                BuildConfig.API_KEY,
                BuildConfig.MODEL_NAME,
                (response, error) -> {
                    runOnUiThread(() -> {
                        if (error != null) {
                            Toast.makeText(ChatGemini.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("GeminiAPI", "Error generating response", error);
                        } else if (response != null) {
                            addMessageToChat(response, Message.SENT_BY_BOT);
                            Log.d("GeminiAPI", "Response: " + response);
                        }
                    });
                    return kotlin.Unit.INSTANCE; // Explicitly return Unit to fix the lambda compatibility issue
                }
        );
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerview_chat);
        edtPrompt = findViewById(R.id.edt_message);
        btnGenerate = findViewById(R.id.btn_message);
    }
}