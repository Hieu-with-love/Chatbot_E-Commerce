package hcmute.edu.vn.chatbot_ec.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.activity.NetworkError;
import hcmute.edu.vn.chatbot_ec.network.ApiClient;
import retrofit2.HttpException;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    
    /**
     * Interface for network availability callback
     */
    public interface NetworkCallback {
        void onNetworkAvailable();
        void onNetworkUnavailable();
    }
    
    /**
     * Checks if the device has any active network connection
     * @param context Application context
     * @return true if network is available, false otherwise
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;
        
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }
    
    /**
     * Performs a network check and executes appropriate callback
     * @param context Application context
     * @param callback Callback to handle network status
     */
    public static void checkNetworkConnection(Context context, NetworkCallback callback) {
        if (isNetworkAvailable(context)) {
            callback.onNetworkAvailable();
        } else {
            callback.onNetworkUnavailable();
            showNetworkErrorScreen(context);
        }
    }

    /**
     * Shows network error screen
     * @param context Application context
     */
    public static void showNetworkErrorScreen(Context context) {
        Intent intent = new Intent(context, NetworkError.class);
        context.startActivity(intent);
    }
    
    /**
     * Handles API errors based on their type
     * Includes special handling for authentication errors
     * @param context Application context
     * @param throwable The error that occurred
     */
    public static void handleNetworkError(Context context, Throwable throwable) {
        if (throwable instanceof java.net.UnknownHostException || 
            throwable instanceof java.net.SocketTimeoutException) {
            // Network connectivity issues
            showNetworkErrorScreen(context);
        } else if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            int statusCode = httpException.code();
            
            if (statusCode == 401) {
                // Unauthorized - token might be expired or invalid
                Log.e(TAG, "Authentication error: " + httpException.message());
                Toast.makeText(context, "Session expired. Please login again.", Toast.LENGTH_LONG).show();
                
                // Clear token and redirect to login
                AuthUtils.logout(context, false);
            } else if (statusCode >= 500) {
                // Server error
                Toast.makeText(context, "Server error. Please try again later.", Toast.LENGTH_LONG).show();
            } else {
                // Other HTTP errors
                Toast.makeText(context, 
                    "Error " + statusCode + ": " + httpException.message(), 
                    Toast.LENGTH_SHORT).show();
            }
        } else if (throwable instanceof IOException) {
            // I/O errors
            showNetworkErrorScreen(context);
        } else {
            // Generic error message for other types of errors
            Log.e(TAG, "API Error: ", throwable);
            Toast.makeText(context, R.string.generic_error_message, Toast.LENGTH_SHORT).show();
        }
    }
} 