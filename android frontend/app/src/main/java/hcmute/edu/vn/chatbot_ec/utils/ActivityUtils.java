package hcmute.edu.vn.chatbot_ec.utils;

import android.content.Context;
import android.content.Intent;

public class ActivityUtils {
    public static void navigateToActivity(Context context, Class<?> targetActivity) {
        Intent intent = new Intent(context, targetActivity);
        context.startActivity(intent);

    }
}
