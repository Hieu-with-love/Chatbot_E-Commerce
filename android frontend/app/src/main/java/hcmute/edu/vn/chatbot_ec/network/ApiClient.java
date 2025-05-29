package hcmute.edu.vn.chatbot_ec.network;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hcmute.edu.vn.chatbot_ec.utils.ChatGeminiUtils;
import hcmute.edu.vn.chatbot_ec.utils.DateTimeAdapter;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient extends Application {
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
    }    private static Context applicationContext;
    
    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
    }
    
    /**
     * Creates and configures an OkHttpClient with appropriate timeouts
     * @return Configured OkHttpClient instance
     */
    private static OkHttpClient getHttpClient() {
        if (applicationContext == null) {
            Log.e("ApiClient", "Application context is null. Make sure ApiClient is properly initialized.");
            // Create a client without the auth interceptor if context is not available
            return new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .build();
        }
        
        return new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(applicationContext))
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();
    }    /**
     * Creates a Gson instance configured with custom type adapters
     * @return Configured Gson instance
     */
    private static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new DateTimeAdapter())
                .create();
    }
    
    public static AuthApiService getAuthApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .client(getHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(createGson()))
                    .build();
        }
        return retrofit.create(AuthApiService.class);
    }

    public static UserApiService getUserApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .client(getHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(createGson()))
                    .build();
        }
        return retrofit.create(UserApiService.class);
    }

    public static ProductApiService getProductApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .client(getHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(createGson()))
                    .build();
        }
        return retrofit.create(ProductApiService.class);
    }

    public static ChatbotApiService getChatbotApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .client(getHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(createGson()))
                    .build();
        }
        return retrofit.create(ChatbotApiService.class);
    }

    public static CartApiService getCartApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(CartApiService.class);
    }

    public static OrderApiService getOrderApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .client(getHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(createGson()))
                    .build();
        }
        return retrofit.create(OrderApiService.class);
    }

    public static AddressApiService getAddressApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .client(getHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(createGson()))
                    .build();
        }
        return retrofit.create(AddressApiService.class);
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
