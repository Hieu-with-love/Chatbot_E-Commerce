package hcmute.edu.vn.chatbot_ec.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.adapter.ChatAdapter;
import hcmute.edu.vn.chatbot_ec.callback.IntentCallback;
import hcmute.edu.vn.chatbot_ec.config.BuildConfig;
import hcmute.edu.vn.chatbot_ec.helper.GeminiHelper;
import hcmute.edu.vn.chatbot_ec.model.Message;
import hcmute.edu.vn.chatbot_ec.utils.MyJsonUtils;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_gemini);

        initView();
        setupRecyclerView();

        btnGeneratePrompt.setOnClickListener(v -> {
            String requestFromUser = generateRequest();
            if (requestFromUser != null) {
                generateResponse(requestFromUser);
            }
        });
        
        if (btnCloseChat != null) {
            btnCloseChat.setOnClickListener(v -> closeChatRedirectToProductList());
        }
    }

    private void closeChatRedirectToProductList() {
        Intent intent = new Intent(ChatGeminiActivity.this, ProductListActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);  // Messages show from bottom
        
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);
    }

    private String generateRequest() {
        String userInput = edtPrompt.getText().toString();
        Log.d("GeminiAPI", "User Input: " + userInput);

        if (!userInput.isEmpty()) {
            // Add user message to the chat by user sender
            addMessageToChat(userInput, Message.SENT_BY_USER);
            
            // Clear input field
            edtPrompt.setText("");
            
            return userInput;
        }
        return null;
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
                (response, error) -> {
                    runOnUiThread(() -> {
                        if (error != null) {
                            Log.e("GeminiAPI", "Error generating JSON response", error);
                            Toast.makeText(this, "Lỗi giai đoạn 1: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            showLoading(false);
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
                    Toast.makeText(ChatGeminiActivity.this, "Lỗi xử lý intent JSON", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("App", "Error generating intent: " + errorMessage);
                runOnUiThread(() -> {
                    showLoading(false);
                    addMessageToChat("Xin lỗi, tôi không hiểu câu hỏi của bạn. Vui lòng thử lại.", Message.SENT_BY_BOT);
                });
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
            case "cancel_order":
                //handleCancelOrder(originalPrompt);
                break;
            case "complain":
                //handleComplaint(originalPrompt);
                break;
            default:
                //handleUnknownIntent(originalPrompt);
        }
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
            // Configure OkHttpClient with timeouts
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            // Prepare request body with proper content type
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(jsonResponse, JSON);

            String endpoint = getEndpointByIntent(intentType);

            // Build request with headers
            Request request = new Request.Builder()
                    .url(BuildConfig.BACKEND_URL + endpoint)
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
        String systemContext = getSystemContextForIntent(intentType);
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
                        showLoading(false);
                        if (error != null) {
                            handleGenerationError("tạo phản hồi cuối cùng", error);
                        } else if (finalResponse != null) {
                            addMessageToChat(finalResponse, Message.SENT_BY_BOT);
                            Log.d("GeminiAPI", "Final Response (" + intentType + "): " + finalResponse);
                        }
                    });
                    return kotlin.Unit.INSTANCE;
                }
        );
    }
    private String getSystemContextForIntent(String intentType) {
        switch (intentType) {
            case "search_product":
                return "Bạn là trợ lý bán hàng. Dựa trên kết quả tìm kiếm sản phẩm,";
            case "check_order":
                return "Bạn là trợ lý khách hàng. Dựa trên thông tin đơn hàng,";
            case "cancel_order":
                return "Bạn là trợ lý khách hàng. Dựa trên việc xử lý hủy đơn hàng,";
            default:
                return "Bạn là trợ lý thân thiện. Dựa trên thông tin từ hệ thống,";
        }
    }
    // Helper methods cho error handling
    private void handleGenerationError(String stage, Throwable error) {
        showLoading(false);
        Toast.makeText(this, "Lỗi " + stage + ": " + error.getMessage(), Toast.LENGTH_SHORT).show();
        Log.e("GeminiAPI", "Error at stage: " + stage, error);
        addMessageToChat("Xin lỗi, tôi đang gặp vấn đề xử lý. Vui lòng thử lại sau.", Message.SENT_BY_BOT);
    }
    private void handleServerError(int code, String response) {
        showLoading(false);
        String errorMessage = "Lỗi từ server: " + code;
        if (response != null) {
            errorMessage += " - " + response;
        }
        Toast.makeText(ChatGeminiActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        Log.e("SpringBackend", "Server error: " + code + ", " + response);
        addMessageToChat("Xin lỗi, tôi đang gặp vấn đề với server. Vui lòng thử lại sau.", Message.SENT_BY_BOT);
    }
    private void handleEmptyResponse() {
        showLoading(false);
        Toast.makeText(ChatGeminiActivity.this, "Không nhận được phản hồi từ server", Toast.LENGTH_SHORT).show();
        Log.e("SpringBackend", "Empty response from server");
        addMessageToChat("Xin lỗi, tôi không nhận được phản hồi từ server. Vui lòng thử lại sau.", Message.SENT_BY_BOT);
    }

    private String getEndpointByIntent(String intentType) {
        switch (intentType) {
            case "search_product":
                return "/api/v1/chatbot/process-product";
            case "check_order":
                return "/api/v1/chatbot/check-order";
            case "cancel_order":
                return "/api/v1/chatbot/cancel-order";
            default:
                return "/api/v1/chatbot/process-general";
        }
    }

    // Phương thức để tạo phản hồi cuối cùng từ Gemini
    private void generateFinalResponse(String originalPrompt, String backendData) {
        String finalPrompt = "Dựa trên câu hỏi gốc là: \"" + originalPrompt + "\" " +
                "và dữ liệu nhận được từ hệ thống là: " + backendData + ", " +
                "hãy tạo một câu trả lời hữu ích và thân thiện cho người dùng.";

        GeminiHelper.generateResponse(
                finalPrompt,
                BuildConfig.API_KEY,
                BuildConfig.MODEL_NAME,
                (finalResponse, error) -> {
                    runOnUiThread(() -> {
                        showLoading(false);
                        if (error != null) {
                            Toast.makeText(this, "Lỗi giai đoạn cuối: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("GeminiAPI", "Error generating final response", error);
                        } else if (finalResponse != null) {
                            // Hiển thị câu trả lời cuối cùng cho người dùng
                            addMessageToChat(finalResponse, Message.SENT_BY_BOT);
                            Log.d("GeminiAPI", "Final Response: " + finalResponse);
                        }
                    });
                    return kotlin.Unit.INSTANCE;
                }
        );
    }

    private void addMessageToChat(String content, int sentBy) {
        Message message = new Message(content, sentBy);
        chatAdapter.addMessage(message);
        
        // Scroll to the bottom to show the latest message
        recyclerView.smoothScrollToPosition(messageList.size() - 1);
    }

    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
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
