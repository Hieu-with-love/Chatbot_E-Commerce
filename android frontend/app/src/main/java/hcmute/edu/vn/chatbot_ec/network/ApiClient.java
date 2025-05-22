package hcmute.edu.vn.chatbot_ec.network;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import hcmute.edu.vn.chatbot_ec.utils.ChatGeminiUtils;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;
    private static final int TIMEOUT_SECONDS = 30;

    // Get base URL dynamically based on device type
    private static String getBaseUrl() {
        String baseUrl = ChatGeminiUtils.getBaseUrl();
        // Ensure the URL ends with a slash
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        Log.d("ApiClient", "Using base URL: " + baseUrl);
        return baseUrl;
    }

    /**
     * Creates and configures an OkHttpClient with appropriate timeouts
     * @return Configured OkHttpClient instance
     */
    private static OkHttpClient getHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();
    }

    public static AuthApiService getAuthApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .client(getHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(AuthApiService.class);
    }

    public static ProductApiService getProductApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .client(getHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ProductApiService.class);
    }

    public static ChatbotApiService getChatbotApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .client(getHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ChatbotApiService.class);
    }
    
    /**
     * Forces the API client to use the physical device URL and resets the Retrofit instance.
     * This is useful for testing connectivity issues.
     */
    public static void usePhysicalDeviceUrl() {
        // Force use of physical device URL
        ChatGeminiUtils.usePhysicalDeviceUrl();
        
        // Reset retrofit instance to rebuild with new URL
        retrofit = null;
        
        // Log the URL being used
        Log.d("ApiClient", "Forced use of physical device URL: " + ChatGeminiUtils.getBaseUrl());
    }
    
    /**
     * Resets the Retrofit instance, forcing it to be recreated with current settings
     * This is useful after changing network configurations or URLs
     */
    public static void resetRetrofitInstance() {
        retrofit = null;
        Log.d("ApiClient", "Retrofit instance reset");
    }
}
