package hcmute.edu.vn.chatbot_ec.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import hcmute.edu.vn.chatbot_ec.MainActivity;
import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.adapter.ChatAdapter;
import hcmute.edu.vn.chatbot_ec.callback.IntentCallback;
import hcmute.edu.vn.chatbot_ec.callback.SummaryCallback;
import hcmute.edu.vn.chatbot_ec.config.BuildConfig;
import hcmute.edu.vn.chatbot_ec.enums.SENDER;
import hcmute.edu.vn.chatbot_ec.helper.GeminiHelper;
import hcmute.edu.vn.chatbot_ec.model.Message;
import hcmute.edu.vn.chatbot_ec.network.ApiClient;
import hcmute.edu.vn.chatbot_ec.network.ChatbotApiService;
import hcmute.edu.vn.chatbot_ec.network.UserApiService;
import hcmute.edu.vn.chatbot_ec.request.ChatSessionRequest;
import hcmute.edu.vn.chatbot_ec.request.ChatSessionStartRequest;
import hcmute.edu.vn.chatbot_ec.request.MessageRequest;
import hcmute.edu.vn.chatbot_ec.request.MessageSendRequest;
import hcmute.edu.vn.chatbot_ec.request.SummaryRequest;
import hcmute.edu.vn.chatbot_ec.response.ChatSessionResponse;
import hcmute.edu.vn.chatbot_ec.response.MessageResponse;
import hcmute.edu.vn.chatbot_ec.response.ResponseData;
import hcmute.edu.vn.chatbot_ec.response.UserResponse;
import hcmute.edu.vn.chatbot_ec.utils.ChatGeminiUtils;
import hcmute.edu.vn.chatbot_ec.utils.MyJsonUtils;
import hcmute.edu.vn.chatbot_ec.utils.TokenManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A dedicated activity for the chatbot interface.
 * All chat functionality is contained within this single activity.
 */
public class ChatGeminiActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText edtPrompt;
    private ImageButton btnGeneratePrompt;
    private ImageView btnCloseChat;
    private ProgressBar progressBar;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private boolean welcomeMessageShown = false;
    private ChatbotApiService chatbotApiService = ApiClient.getChatbotApiService();
    private UserApiService userApiService = ApiClient.getUserApiService();

    private List<Pair<String, String>> recentPairs = new ArrayList<>();
    private String currentUserMessage = null;
    
    // Flag to prevent duplicate responses for the same user message
    private boolean isProcessingResponse = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_gemini);

        initView();
        setupRecyclerView();
        startNewChatSession();
        
        // Display which URL mode is being used
        boolean isEmulator = ChatGeminiUtils.isEmulator();
        String baseUrl = ChatGeminiUtils.getBaseUrl();
        Toast.makeText(this, "Running on " + (isEmulator ? "emulator" : "physical device") + 
                "\nUsing URL: " + baseUrl, Toast.LENGTH_LONG).show();
        Log.d("SpringBackend", "Device detection: " + (isEmulator ? "Emulator" : "Physical device") + 
                " - Using URL: " + baseUrl);        
          btnGeneratePrompt.setOnClickListener(v -> {
            String userInput = edtPrompt.getText().toString().trim();
            if (userInput.isEmpty()) {
                return; // Don't process empty messages
            }
            
            // Prevent duplicate processing
            if (isProcessingResponse) {
                Toast.makeText(this, "Đang xử lý tin nhắn trước đó, vui lòng đợi...", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Hiển thị ngay nội dung người dùng lên giao diện
            addMessageToChat(userInput, Message.SENT_BY_USER);
            edtPrompt.setText(""); // Xoá ô nhập

            // Set processing flag and show loading
            isProcessingResponse = true;
            showLoading(true);

            // Gửi nội dung người dùng đi (generateRequest already calls generateResponse internally)
            generateRequest(userInput);
        });
        
        if (btnCloseChat != null) {
            btnCloseChat.setOnClickListener(v -> closeChatRedirectToMain());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_url) {
            toggleUrlMode();
            return true;
        }
//        else if (item.getItemId() == R.id.action_generate_summary) {
//            generateAndSaveChatSummary();
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleUrlMode() {
        boolean isCurrentlyEmulator = ChatGeminiUtils.isEmulator();
        
        // Toggle to the opposite mode
        if (ChatGeminiUtils.isManualOverrideEnabled()) {
            // If manual override already enabled, toggle the setting
            ChatGeminiUtils.enableManualOverride(!isCurrentlyEmulator);
        } else {
            // Otherwise, enable manual override with opposite of current detection
            ChatGeminiUtils.enableManualOverride(!isCurrentlyEmulator);
        }
        
        // Display the new setting
        String newMode = ChatGeminiUtils.isEmulator() ? "Emulator" : "Physical Device";
        String baseUrl = ChatGeminiUtils.getBaseUrl();
        
        Toast.makeText(this, "URL mode changed to: " + newMode + 
                "\nURL: " + baseUrl, Toast.LENGTH_LONG).show();
        
        Log.d("SpringBackend", "URL mode manually toggled to: " + newMode + 
                " - Using URL: " + baseUrl);
    }

    private void closeChatRedirectToMain() {
        // Get the current user information to save the chat summary
        String token = TokenManager.getToken(this);
        userApiService.getMe(token).enqueue(new retrofit2.Callback<UserResponse>() {
            @Override
            public void onResponse(retrofit2.Call<UserResponse> call, retrofit2.Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Integer sessionId = response.body().getChatSessionId();
                    Integer userId = response.body().getUserId();
                    
                    // Save the chat summary before closing
                    saveChatSummary(sessionId, userId);
                }
                
                // Proceed with closing the activity and redirecting
                Intent intent = new Intent(ChatGeminiActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            
            @Override
            public void onFailure(retrofit2.Call<UserResponse> call, Throwable t) {
                Log.e("ChatSession", "Failed to get user information for summary", t);
                
                // Still redirect even if we couldn't save the summary
                Intent intent = new Intent(ChatGeminiActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);  // Messages show from bottom
        
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);
    }

    private void generateRequest(String userInput) {
        Log.d("GeminiAPI", "User Input: " + userInput);

        if (!userInput.isEmpty()) {
            // Store the current user message for pairing with bot response
            currentUserMessage = userInput;
            
            // Add user message to database
            String token = TokenManager.getToken(this);
            userApiService.getMe(token).enqueue(new retrofit2.Callback<UserResponse>() {
                @Override
                public void onResponse(retrofit2.Call<UserResponse> call, retrofit2.Response<UserResponse> response) {
                    MessageSendRequest req = new MessageSendRequest();
                    req.setContent(userInput);
                    req.setSender(SENDER.USER.getValue());
                    assert response.body() != null;
                    req.setSessionId(response.body().getChatSessionId());
                    req.setUserId(response.body().getUserId());

                    chatbotApiService.processMessage(req).enqueue(new retrofit2.Callback<ResponseData<MessageResponse>>() {
                        @Override
                        public void onResponse(retrofit2.Call<ResponseData<MessageResponse>> call, retrofit2.Response<ResponseData<MessageResponse>> response) {
                            Log.d("GeminiAPI", "Response from Gemini: " + response.body());
                            welcomeMessageShown = true;
                            // Generate response using the new intent-based approach
                            generateResponse(userInput);
                        }                        @Override
                        public void onFailure(retrofit2.Call<ResponseData<MessageResponse>> call, Throwable t) {
                            Log.e("GeminiAPI", "Error in processing message", t);
                            runOnUiThread(() -> {
                                resetProcessingState();
                                addMessageToChat("Xin lỗi, có lỗi xảy ra khi xử lý tin nhắn. Vui lòng thử lại.", Message.SENT_BY_BOT);
                            });
                        }                    });
                }
                @Override
                public void onFailure(retrofit2.Call<UserResponse> call, Throwable t) {
                    Log.e("ChatSession", "Failed to get user info for message processing", t);
                    runOnUiThread(() -> {
                        resetProcessingState();
                        addMessageToChat("Xin lỗi, có lỗi xảy ra khi xử lý tin nhắn. Vui lòng thử lại.", Message.SENT_BY_BOT);
                    });
                }
            });
        }
    }

    private void generateUserIntent(String originalPrompt, IntentCallback callback) {
        InputStream inputStream = getResources().openRawResource(R.raw.intent_prompt);
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        String promptTemplate = scanner.hasNext() ? scanner.next() : "";
        String structuredPrompt = String.format(promptTemplate, originalPrompt);

        GeminiHelper.generateResponse(
                structuredPrompt,
                BuildConfig.API_KEY,
                BuildConfig.MODEL_NAME,
                (response, error) -> {                    runOnUiThread(() -> {
                        if (error != null) {
                            Log.e("GeminiAPI", "Error generating JSON response", error);
                            Toast.makeText(this, "Lỗi giai đoạn 1: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            resetProcessingState();
                            callback.onError(error.getMessage());
                        } else if (response != null) {
                            Log.d("GeminiAPI", "JSON Response from Gemini: " + response);
                            callback.onIntentGenerated(response.toString());
                        }
                    });
                    return kotlin.Unit.INSTANCE;
                });
    }

    private void generateResponse(String originalPrompt) {
        generateUserIntent(originalPrompt, new IntentCallback() {
            @Override
            public void onIntentGenerated(String intent) {
                Log.d("App", "Intent generated: " + intent);

                try {
                    // Làm sạch phần markdown ```json ... ```
                    String cleanedJson = MyJsonUtils.removeMarkdownJson(intent);

                    JSONObject jsonObject = new JSONObject(cleanedJson);
                    String intentType = jsonObject.getString("intent");

                    // Gọi tiếp xử lý dựa trên intent
                    handleIntentBasedResponse(intentType.trim(), originalPrompt);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("App", "Error parsing intent JSON", e);
                    resetProcessingState();
                    addMessageToChat("Xin lỗi, tôi không hiểu câu hỏi của bạn. Vui lòng thử lại.", Message.SENT_BY_BOT);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("App", "Error generating intent: " + errorMessage);
                resetProcessingState();
                addMessageToChat("Xin lỗi, tôi không hiểu câu hỏi của bạn. Vui lòng thử lại.", Message.SENT_BY_BOT);
            }
        });
    }

    private void handleIntentBasedResponse(String userIntent, String originalPrompt) {
        Log.d("App", "Handling intent: " + userIntent);

        switch (userIntent) {
            case "search_product":
                handleSearchProduct(originalPrompt);
                break;
            case "check_order":
                //handleCheckOrder(originalPrompt);
                break;
            case "greeting":
                handleGreeting(originalPrompt);
                break;
            default:
                handleUnknownIntent(originalPrompt);
        }
    }    
  
  private void handleGreeting(String originalPrompt) {
        String promptTemplate = ChatGeminiUtils.readFileFromAssets(this,R.raw.greeting_prompt);
        assert promptTemplate != null;
        String structuredPrompt = promptTemplate.replace("{{user_input}}", originalPrompt);

        GeminiHelper.generateResponse(
                structuredPrompt,
                BuildConfig.API_KEY,
                BuildConfig.MODEL_NAME,
                (res, error) -> {
                    runOnUiThread(() -> {
                        if (error != null) {
                            Toast.makeText(this, "Lỗi giai đoạn 1: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("GeminiAPI", "Error generating response", error);
                            resetProcessingState();
                        } else if (res != null) {
                            Log.d("GeminiAPI", "Response from Gemini: " + res);
                            addMessageToChat(res, Message.SENT_BY_BOT);
                            
                            // Track the user-bot pair for summary generation
                            if (currentUserMessage != null) {
                                String token = TokenManager.getToken(this);
                                userApiService.getMe(token).enqueue(new retrofit2.Callback<UserResponse>() {
                                    @Override
                                    public void onResponse(retrofit2.Call<UserResponse> call, retrofit2.Response<UserResponse> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            int sessionId = response.body().getChatSessionId();
                                            onBotResponseReceived(currentUserMessage, res, sessionId);
                                            currentUserMessage = null; // Reset after use
                                        }
                                        resetProcessingState(); // Reset processing state after successful completion
                                    }
                                    
                                    @Override
                                    public void onFailure(retrofit2.Call<UserResponse> call, Throwable t) {
                                        Log.e("ChatSession", "Failed to get session info for tracking", t);
                                        resetProcessingState(); // Reset processing state even on failure
                                    }
                                });
                            } else {
                                resetProcessingState(); // Reset if no current user message
                            }
                        }
                    });
                    return kotlin.Unit.INSTANCE;
                }
        );
    }

    private void handleUnknownIntent(String originalPrompt) {
        // If don't realize prompt. just use gemini model return
        String promptTemplate = ChatGeminiUtils.readFileFromAssets(this,R.raw.unknown_prompt);
        String structuredPrompt = promptTemplate.replace("{{user_input}}", originalPrompt);

        GeminiHelper.generateResponse(
                structuredPrompt,
                BuildConfig.API_KEY,
                BuildConfig.MODEL_NAME,
                (res, error) -> {
                    runOnUiThread(() -> {
                        if (error != null) {
                            Toast.makeText(this, "Lỗi giai đoạn 1: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("GeminiAPI", "Error generating response", error);
                            resetProcessingState();
                        } else if (res != null) {
                            Log.d("GeminiAPI", "Response from Gemini: " + res);
                            addMessageToChat(res, Message.SENT_BY_BOT);
                            
                            // Track the user-bot pair for summary generation
                            if (currentUserMessage != null) {
                                String token = TokenManager.getToken(this);
                                userApiService.getMe(token).enqueue(new retrofit2.Callback<UserResponse>() {
                                    @Override
                                    public void onResponse(retrofit2.Call<UserResponse> call, retrofit2.Response<UserResponse> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            int sessionId = response.body().getChatSessionId();
                                            onBotResponseReceived(currentUserMessage, res, sessionId);
                                            currentUserMessage = null; // Reset after use
                                        }
                                        resetProcessingState(); // Reset processing state after successful completion
                                    }
                                    
                                    @Override
                                    public void onFailure(retrofit2.Call<UserResponse> call, Throwable t) {
                                        Log.e("ChatSession", "Failed to get session info for tracking", t);
                                        resetProcessingState(); // Reset processing state even on failure
                                    }
                                });
                            } else {
                                resetProcessingState(); // Reset if no current user message
                            }
                        }
                    });
                    return kotlin.Unit.INSTANCE;
                }
        );
    }

    private void handleSearchProduct(String originalPrompt) {
        showLoading(true);
        extractSearchJsonThenSendToBackend(originalPrompt);
    }

    private void extractSearchJsonThenSendToBackend(String originalPrompt) {
        InputStream inputStream = getResources().openRawResource(R.raw.search_product_prompt);
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        String promptTemplate = scanner.hasNext() ? scanner.next() : "";
        String structuredPrompt = String.format(promptTemplate, originalPrompt);

        GeminiHelper.generateResponse(
                structuredPrompt,
                BuildConfig.API_KEY,
                BuildConfig.MODEL_NAME,
                (jsonResponse, error) -> {
                    runOnUiThread(() -> {
                        if (error != null) {
                            Toast.makeText(this, "Lỗi giai đoạn 1: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("GeminiAPI", "Error generating JSON response", error);
                            showLoading(false);
                        } else if (jsonResponse != null) {
                            Log.d("GeminiAPI", "JSON Response from Gemini: " + jsonResponse);
                            String cleanedJson = MyJsonUtils.removeMarkdownJson(jsonResponse);
                            sendToSpringBackend(cleanedJson, originalPrompt, "search_product");
                        }
                    });
                    return kotlin.Unit.INSTANCE; // Explicitly return Unit to fix the lambda compatibility issue
                }
        );
    }

    private void sendToSpringBackend(String jsonResponse, String originalPrompt, String intentType) {
        try {
            Log.d("SpringBackend", "Sending request to backend with intent: " + intentType);
            
            // Configure OkHttpClient with timeouts
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            // Prepare request body with proper content type
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(jsonResponse, JSON);            
            String endpoint = ChatGeminiUtils.getEndpointByIntent(intentType);
            String baseUrl = ChatGeminiUtils.getBaseUrl();
            
            Log.d("SpringBackend", "Using " + (ChatGeminiUtils.isEmulator() ? "emulator" : "physical device") + " URL: " + baseUrl);
            
            // Build request with headers
            Request request = new Request.Builder()
                    .url(baseUrl + endpoint)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();

            Log.d("SpringBackend", "Request URL: " + request.url());

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> {
                        showLoading(false);
                        Toast.makeText(ChatGeminiActivity.this, "Lỗi kết nối backend: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("SpringBackend", "Error connecting to backend", e);
                        // Add user-friendly error message to chat
                        addMessageToChat("Xin lỗi, tôi đang gặp vấn đề kết nối. Vui lòng thử lại sau.", Message.SENT_BY_BOT);
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        handleBackendResponse(response, originalPrompt, intentType);
                }
            });
        } catch (Exception e) {
            runOnUiThread(() -> {
                showLoading(false);
                Toast.makeText(this, "Lỗi xử lý: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("SpringBackend", "Processing error", e);
                addMessageToChat("Xin lỗi, đã xảy ra lỗi không mong muốn. Vui lòng thử lại sau.", Message.SENT_BY_BOT);
            });
        }
    }

    private void handleBackendResponse(Response response, String originalPrompt, String intentType) {
        String springResponse = null;
        try {
            if (response.body() != null) {
                springResponse = response.body().string();
            }
        } catch (IOException e) {
            Log.e("SpringBackend", "Error reading response body", e);
        }

        final String finalResponse = springResponse;
        runOnUiThread(() -> {
            if (!response.isSuccessful()) {
                handleServerError(response.code(), finalResponse);
                return;
            }

            if (finalResponse == null) {
                handleEmptyResponse();
                return;
            }

            Log.d("SpringBackend", "Response from backend (" + intentType + "): " + finalResponse);
            generateFinalResponse(originalPrompt, finalResponse, intentType);
        });
    }    
  
  // Cập nhật method generateFinalResponse để handle các intent khác nhau
    private void generateFinalResponse(String originalPrompt, String backendData, String intentType) {
        String systemContext = ChatGeminiUtils.getSystemContextForIntent(intentType);
        String finalPrompt = systemContext +
                " Câu hỏi gốc: \"" + originalPrompt + "\" " +
                "Dữ liệu từ hệ thống: " + backendData +
                " Hãy tạo câu trả lời tự nhiên và hữu ích.";

        GeminiHelper.generateResponse(
                finalPrompt,
                BuildConfig.API_KEY,
                BuildConfig.MODEL_NAME,
                (finalResponse, error) -> {
                    runOnUiThread(() -> {
                        if (error != null) {
                            handleGenerationError("tạo phản hồi cuối cùng", error);
                        } else if (finalResponse != null) {
                            addMessageToChat(finalResponse, Message.SENT_BY_BOT);
                            Log.d("GeminiAPI", "Final Response (" + intentType + "): " + finalResponse);
                            
                            // Add bot message to database
                            String token = TokenManager.getToken(this);
                            userApiService.getMe(token).enqueue(new retrofit2.Callback<UserResponse>() {
                                @Override
                                public void onResponse(retrofit2.Call<UserResponse> call, retrofit2.Response<UserResponse> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        MessageSendRequest req = new MessageSendRequest();
                                        req.setContent(finalResponse);
                                        req.setSender(SENDER.BOT.getValue());
                                        req.setSessionId(response.body().getChatSessionId());
                                        req.setUserId(response.body().getUserId());

                                        chatbotApiService.processMessage(req).enqueue(new retrofit2.Callback<ResponseData<MessageResponse>>() {
                                            @Override
                                            public void onResponse(retrofit2.Call<ResponseData<MessageResponse>> call, retrofit2.Response<ResponseData<MessageResponse>> response) {
                                                Log.d("GeminiAPI", "Bot message saved with ID: " + 
                                                    (response.isSuccessful() && response.body() != null && response.body().getData() != null ? 
                                                    response.body().getData().getId() : "unknown"));
                                            }

                                            @Override
                                            public void onFailure(retrofit2.Call<ResponseData<MessageResponse>> call, Throwable t) {
                                                Log.e("GeminiAPI", "Error saving bot message", t);
                                            }
                                        });
                                        
                                        // Track the user-bot pair for summary generation
                                        if (currentUserMessage != null) {
                                            int sessionId = response.body().getChatSessionId();
                                            onBotResponseReceived(currentUserMessage, finalResponse, sessionId);
                                            currentUserMessage = null; // Reset after use
                                        }
                                        resetProcessingState(); // Reset processing state after successful completion
                                    }
                                }
                                
                                @Override
                                public void onFailure(retrofit2.Call<UserResponse> call, Throwable t) {
                                    Log.e("ChatSession", "Failed to get session info for final response", t);
                                    resetProcessingState(); // Reset processing state even on failure
                                }
                            });
                        }
                    });
                    return kotlin.Unit.INSTANCE;
                }
        );
    }

    // Helper methods cho error handling
    private void handleGenerationError(String stage, Throwable error) {
        resetProcessingState();
        Toast.makeText(this, "Lỗi " + stage + ": " + error.getMessage(), Toast.LENGTH_SHORT).show();
        Log.e("GeminiAPI", "Error at stage: " + stage, error);
        addMessageToChat("Xin lỗi, tôi đang gặp vấn đề xử lý. Vui lòng thử lại sau.", Message.SENT_BY_BOT);
    }
    
    private void handleServerError(int code, String response) {
        resetProcessingState();
        String errorMessage = "Lỗi từ server: " + code;
        if (response != null) {
            errorMessage += " - " + response;
        }
        Toast.makeText(ChatGeminiActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        Log.e("SpringBackend", "Server error: " + code + ", " + response);
        addMessageToChat("Xin lỗi, tôi đang gặp vấn đề với server. Vui lòng thử lại sau.", Message.SENT_BY_BOT);
    }
    
    private void handleEmptyResponse() {
        resetProcessingState();
        Toast.makeText(ChatGeminiActivity.this, "Không nhận được phản hồi từ server", Toast.LENGTH_SHORT).show();
        Log.e("SpringBackend", "Empty response from server");
        addMessageToChat("Xin lỗi, tôi không nhận được phản hồi từ server. Vui lòng thử lại sau.", Message.SENT_BY_BOT);
    }

    
    /**
     * Resets the processing state to allow new messages to be processed
     */
    private void resetProcessingState() {
        isProcessingResponse = false;
        showLoading(false);
    }
  
  private void addMessageToChat(String content, int sentBy) {

        Message message = new Message(content, sentBy);
        chatAdapter.addMessage(message);
        
        // Scroll to the bottom to show the latest message
        recyclerView.smoothScrollToPosition(messageList.size() - 1);
    }

    /**
     * Tracks user-bot message pairs and generates summary when 5 pairs are collected
     */
    public void onBotResponseReceived(String userMessage, String botMessage, int sessionId) {
        recentPairs.add(new Pair<>(userMessage, botMessage));

        if (recentPairs.size() == 5) {
            StringBuilder promptBuilder = new StringBuilder();
            for (Pair<String, String> pair : recentPairs) {
                promptBuilder.append("User: ").append(pair.first).append("\n");
                promptBuilder.append("Bot: ").append(pair.second).append("\n");
            }
            String summaryPrompt = promptBuilder.toString();

            // Call Gemini API with this prompt to generate a summary
            requestSummaryFromGemini(summaryPrompt, summary -> {
                // Save to session
                updateSummaryChatSession(sessionId, summary);
            });

            recentPairs.clear();
        }
    }

    /**
     * Requests a summary from Gemini API based on conversation pairs
     */
    private void requestSummaryFromGemini(String conversationPrompt, SummaryCallback callback) {
        String summarySystemPrompt = "You are a helpful assistant that creates concise summaries of conversations. " +
                "Please summarize the following conversation between a user and a chatbot in 2-3 sentences, " +
                "focusing on the main topics discussed and any important outcomes:\n\n" + conversationPrompt;

        GeminiHelper.generateResponse(
                summarySystemPrompt,
                BuildConfig.API_KEY,
                BuildConfig.MODEL_NAME,
                (response, error) -> {
                    runOnUiThread(() -> {
                        if (error != null) {
                            Log.e("GeminiAPI", "Error generating summary", error);
                            callback.onSummaryGenerated("Failed to generate summary");
                        } else if (response != null) {
                            Log.d("GeminiAPI", "Summary generated: " + response);
                            callback.onSummaryGenerated(response);
                        }
                    });
                    return kotlin.Unit.INSTANCE;
                }
        );
    }

    /**
     * Updates chat session with generated summary
     */
    private void updateSummaryChatSession(int sessionId, String summary) {
        String token = TokenManager.getToken(this);
        userApiService.getMe(token).enqueue(new retrofit2.Callback<UserResponse>() {
            @Override
            public void onResponse(retrofit2.Call<UserResponse> call, retrofit2.Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Integer userId = response.body().getUserId();
                    SummaryRequest summaryRequest = new SummaryRequest(summary, userId);
                    
                    chatbotApiService.updateSummaryChatSession(sessionId, summaryRequest)
                        .enqueue(new retrofit2.Callback<ResponseData<ChatSessionResponse>>() {
                            @Override
                            public void onResponse(retrofit2.Call<ResponseData<ChatSessionResponse>> call, 
                                                  retrofit2.Response<ResponseData<ChatSessionResponse>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Log.d("ChatSession", "Chat summary saved successfully for session " + sessionId);
                                    Toast.makeText(ChatGeminiActivity.this, 
                                        "Summary generated and saved", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e("ChatSession", "Failed to save chat summary: " + 
                                        (response.errorBody() != null ? response.errorBody().toString() : "Unknown error"));
                                }
                            }
                            
                            @Override
                            public void onFailure(retrofit2.Call<ResponseData<ChatSessionResponse>> call, Throwable t) {
                                Log.e("ChatSession", "Error saving chat summary", t);
                            }
                        });
                }
            }
            
            @Override
            public void onFailure(retrofit2.Call<UserResponse> call, Throwable t) {
                Log.e("ChatSession", "Failed to get user information for summary", t);
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void startNewChatSession() {
        String token = TokenManager.getToken(this);

        chatbotApiService.checkExistsChatSession(token).enqueue(new retrofit2.Callback<ResponseData<ChatSessionResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseData<ChatSessionResponse>> call, retrofit2.Response<ResponseData<ChatSessionResponse>> response) {
                assert response.body()!=null;

                if (response.body().getData().isHasSession()){
                    openChatSession(response.body().getData().getChatSessionId(), response.body().getData().getUserId());
                }
                else {
                    createNewChatSession(response.body().getData().getUserId());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseData<ChatSessionResponse>> call, Throwable t) {
                Log.d("ChatSession", "Error checking session: " + t.getMessage());
            }
        });
    }

    private void openChatSession(int sessionId, int userId) {
        // Use the new direct endpoint to get session with messages
        chatbotApiService.getChatSessionWithMessages(sessionId, userId).enqueue(new retrofit2.Callback<ResponseData<ChatSessionResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseData<ChatSessionResponse>> call, retrofit2.Response<ResponseData<ChatSessionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseData<ChatSessionResponse> responseData = response.body();
                    if (responseData.getData() != null && responseData.getData().getMessages() != null) {
                        // Clear existing messages
                        messageList.clear();
                        
                        // Add all messages from the chat session
                        List<MessageResponse> messages = responseData.getData().getMessages();
                        for (MessageResponse msg : messages) {
                            // Determine if message is from bot or user
                            int sentBy = msg.getSender().equals(SENDER.BOT.getValue()) ?
                                Message.SENT_BY_BOT : Message.SENT_BY_USER;
                            
                            // Add message to the list
                            messageList.add(new Message(msg.getContent(), sentBy));
                        }
                        
                        // Update the adapter
                        chatAdapter.notifyDataSetChanged();
                        
                        // Scroll to the bottom to show latest message
                        if (!messageList.isEmpty()) {
                            recyclerView.smoothScrollToPosition(messageList.size() - 1);
                            // Set welcome message shown if there are already messages
                            welcomeMessageShown = true;
                        } else {
                            // If no messages, show welcome message
                            showWelcomeMessage();
                        }
                        
                        Log.d("ChatSession", "Loaded " + messageList.size() + " messages from session " + sessionId);
                    } else {
                        Log.d("ChatSession", "No messages found in session or session data is null");
                        showWelcomeMessage();
                    }
                } else {
                    Log.e("ChatSession", "Failed to load chat session: " + response.code());
                    Toast.makeText(ChatGeminiActivity.this, 
                        "Failed to load chat session. Error: " + response.code(), 
                        Toast.LENGTH_SHORT).show();
                    showWelcomeMessage();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseData<ChatSessionResponse>> call, Throwable t) {
                Log.e("ChatSession", "Error loading chat session", t);
                Toast.makeText(ChatGeminiActivity.this, 
                    "Connection error: " + t.getMessage(), 
                    Toast.LENGTH_SHORT).show();
                showWelcomeMessage();
            }
        });
    }

    private void createNewChatSession(int userId) {
        ChatSessionStartRequest req = new ChatSessionStartRequest(userId, "New Chat Session");

        chatbotApiService.startChatSession(req).enqueue(new retrofit2.Callback<ResponseData<ChatSessionResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseData<ChatSessionResponse>> call, retrofit2.Response<ResponseData<ChatSessionResponse>> response) {
                messageList.clear();
                showWelcomeMessage();
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ChatSession", "New chat session created with ID: " + 
                        (response.body().getData() != null ? response.body().getData().getChatSessionId() : "unknown"));
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseData<ChatSessionResponse>> call, Throwable t) {
                Log.e("ChatSession", "Failed to create new chat session", t);
                Toast.makeText(ChatGeminiActivity.this, 
                    "Failed to create chat session: " + t.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }    /**
     * Creates a summary of the current chat session and sends it to the backend
     * This method is kept for backward compatibility but now uses a simple message
     */

    private void saveChatSummary(Integer sessionId, Integer userId) {
        // Create a simple summary message since we now handle summaries automatically
        String simpleSummary = "Chat session completed with " + messageList.size() + " messages exchanged.";
        
        SummaryRequest summaryRequest = new SummaryRequest(simpleSummary, userId);
        
        chatbotApiService.updateSummaryChatSession(sessionId, summaryRequest)
            .enqueue(new retrofit2.Callback<ResponseData<ChatSessionResponse>>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseData<ChatSessionResponse>> call, 
                                      retrofit2.Response<ResponseData<ChatSessionResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("ChatSession", "Chat summary saved successfully for session " + sessionId);
                        Toast.makeText(ChatGeminiActivity.this, 
                            getString(R.string.summary_generated), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("ChatSession", "Failed to save chat summary: " + 
                            (response.errorBody() != null ? response.errorBody().toString() : "Unknown error"));
                        Toast.makeText(ChatGeminiActivity.this, 
                            getString(R.string.summary_failed), Toast.LENGTH_SHORT).show();
                    }
                }
                
                @Override
                public void onFailure(retrofit2.Call<ResponseData<ChatSessionResponse>> call, Throwable t) {
                    Log.e("ChatSession", "Error saving chat summary", t);
                    Toast.makeText(ChatGeminiActivity.this, 
                        getString(R.string.summary_failed) + ": " + t.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            });
    }
    /**
     * Manually trigger the generation and saving of a chat summary.
     * This can be connected to a menu option or button for testing.
     */
    private void generateAndSaveChatSummary() {
        String token = TokenManager.getToken(this);
        userApiService.getMe(token).enqueue(new retrofit2.Callback<UserResponse>() {
            @Override
            public void onResponse(retrofit2.Call<UserResponse> call, retrofit2.Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Integer sessionId = response.body().getChatSessionId();
                    Integer userId = response.body().getUserId();
                      saveChatSummary(sessionId, userId);
                } else {
                    Toast.makeText(ChatGeminiActivity.this, "Could not get user info", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(retrofit2.Call<UserResponse> call, Throwable t) {
                Log.e("ChatSession", "Failed to get user information for summary", t);
                Toast.makeText(ChatGeminiActivity.this, 
                    "Failed to generate summary: " + t.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showWelcomeMessage() {
        if (!welcomeMessageShown) {
            String welcomeMessage = "Hello! I'm your shopping assistant! How can I assist you today?";

            String token = TokenManager.getToken(this);
            userApiService.getMe(token).enqueue(new retrofit2.Callback<UserResponse>() {
                @Override
                public void onResponse(retrofit2.Call<UserResponse> call, retrofit2.Response<UserResponse> response) {
                    MessageSendRequest req = new MessageSendRequest();
                    req.setContent(welcomeMessage);
                    req.setSender(SENDER.BOT.getValue());
                    assert response.body() != null;
                    req.setSessionId(response.body().getChatSessionId());
                    req.setUserId(response.body().getUserId());

                    chatbotApiService.processMessage(req).enqueue(new retrofit2.Callback<ResponseData<MessageResponse>>() {
                        @Override
                        public void onResponse(retrofit2.Call<ResponseData<MessageResponse>> call, retrofit2.Response<ResponseData<MessageResponse>> response) {
                            addMessageToChat(welcomeMessage, Message.SENT_BY_BOT);
                            welcomeMessageShown = true;
                        }

                        @Override
                        public void onFailure(retrofit2.Call<ResponseData<MessageResponse>> call, Throwable t) {
                            Log.e("GeminiAPI", "Error sending welcome message", t);
                        }
                    });
                }
                @Override
                public void onFailure(retrofit2.Call<UserResponse> call, Throwable t) {
                    Log.e("ChatSession", "Failed to get user for welcome message", t);
                    // Still show welcome message locally even if we can't save it
                    addMessageToChat(welcomeMessage, Message.SENT_BY_BOT);
                    welcomeMessageShown = true;
                }
            });
        }
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerView_chat);
        edtPrompt = findViewById(R.id.editText_message);
        btnGeneratePrompt = findViewById(R.id.button_send);
        btnCloseChat = findViewById(R.id.btn_close_chat);
        progressBar = findViewById(R.id.progress_bar_chat);
    }
}