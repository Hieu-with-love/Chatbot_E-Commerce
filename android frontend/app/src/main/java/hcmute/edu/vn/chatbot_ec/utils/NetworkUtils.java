package hcmute.edu.vn.chatbot_ec.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Intent;
import android.widget.Toast;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.activity.NetworkError;

public class NetworkUtils {
    
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    public static void handleNetworkError(Context context, Throwable throwable) {
        if (throwable instanceof java.net.UnknownHostException || 
            throwable instanceof java.net.SocketTimeoutException ||
            throwable instanceof java.io.IOException) {
            // Show network error screen
            Intent intent = new Intent(context, NetworkError.class);
            context.startActivity(intent);
        } else {
            // Show generic error message for other types of errors
            Toast.makeText(context, R.string.generic_error_message, Toast.LENGTH_SHORT).show();
        }
    }
} 