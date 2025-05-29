package hcmute.edu.vn.chatbot_ec.utils;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.config.BuildConfig;
import hcmute.edu.vn.chatbot_ec.model.Message;
import retrofit2.Callback;

public class ChatGeminiUtils {
    // Manual override flags for testing purposes
    private static boolean manualOverrideEnabled = false;
    private static boolean forceUseEmulatorUrl = false;

    public static List<Pair<String, String>> getLastNCouples(List<Message> messages, int nCouples) {
        List<Pair<String, String>> result = new ArrayList<>();

        for (int i = messages.size() - 1; i >= 1; i--) {
            Message m1 = messages.get(i - 1);
            Message m2 = messages.get(i);

            if (m1.getSentBy() == Message.SENT_BY_USER && m2.getSentBy() == Message.SENT_BY_BOT) {
                result.add(0, new Pair<>(m1.getContent(), m2.getContent()));
                if (result.size() >= nCouples) break;
                i--; // bỏ qua cặp này
            }
        }

        return result;
    }

    public static String structuredSummary(Context context, String originalPrompt, String summary, int raw){
        String targetPrompt = originalPrompt;

        if (summary!=null){
            String promptTemplate = ChatGeminiUtils.readFileFromAssets(context, R.raw.summary_prompt);
            String targetSummary = String.format(promptTemplate, originalPrompt + " and required before  " + summary);
            Log.d("Summary not null", targetSummary);
        }

        String promptTemplateS = ChatGeminiUtils.readFileFromAssets(context, raw);
        String structuredPrompt = String.format(promptTemplateS, targetPrompt);

        return structuredPrompt;
    }

    public static String readFileFromAssets(Context context, int fileRaw) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream is = context.getResources().openRawResource(fileRaw);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return stringBuilder.toString();
    }
    
    public static String getSystemPromptForType(String responseType) {
        switch (responseType) {
            case "complaint":
                return "Bạn là trợ lý khách hàng thân thiện. Hãy xin lỗi và đưa ra lời khuyên phù hợp cho khiếu nại của khách hàng.";
            case "general":
                return "Bạn là trợ lý bán hàng thân thiện. Hãy trả lời câu hỏi một cách hữu ích.";
            default:
                return "Bạn là trợ lý thân thiện. Hãy trả lời câu hỏi một cách hữu ích.";
        }
    }

    public static String getSystemContextForIntent(String intentType) {
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

    public static String getEndpointByIntent(String intentType) {
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
    
    /**
     * Detects if the app is running on an emulator.
     * 
     * @return true if the app is running on an emulator, false otherwise
     */
    public static boolean isEmulator() {
        // If manual override is enabled, return the forced value
        if (manualOverrideEnabled) {
            Log.d("ChatGeminiUtils", "Using manual override. Forced emulator mode: " + forceUseEmulatorUrl);
            return forceUseEmulatorUrl;
        }
        
        // Actual emulator detection
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.FINGERPRINT.contains("sdk_")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT)
                || TextUtils.isEmpty(Build.HARDWARE);
    }
    
    /**
     * Gets the appropriate base URL based on whether the app is running on an emulator or a physical device.
     * 
     * @return The base URL for API calls
     */
    public static String getBaseUrl() {
        if (isEmulator()) {
            return BuildConfig.BACKEND_URL; // Emulator URL (typically uses 10.0.2.2)
        } else {
            return BuildConfig.PHYSICAL_DEVICE_URL; // Physical device URL
        }
    }
    
    /**
     * Enable manual override for URL selection.
     * This is useful for testing purposes.
     * 
     * @param useEmulatorUrl true to force using the emulator URL, false to force using the physical device URL
     */
    public static void enableManualOverride(boolean useEmulatorUrl) {
        manualOverrideEnabled = true;
        forceUseEmulatorUrl = useEmulatorUrl;
        Log.d("ChatGeminiUtils", "Manual override enabled. Using " + 
              (useEmulatorUrl ? "emulator URL: " + BuildConfig.BACKEND_URL : 
                               "physical device URL: " + BuildConfig.PHYSICAL_DEVICE_URL));
    }
    
    /**
     * Disable manual override and return to automatic URL selection.
     */
    public static void disableManualOverride() {
        manualOverrideEnabled = false;
        Log.d("ChatGeminiUtils", "Manual override disabled. Returning to automatic detection.");
    }
    
    /**
     * Check if manual override is currently enabled.
     * 
     * @return true if manual override is enabled, false otherwise
     */
    public static boolean isManualOverrideEnabled() {
        return manualOverrideEnabled;
    }
    
    /**
     * Explicitly switch to emulator URL mode.
     */
    public static void useEmulatorUrl() {
        enableManualOverride(true);
    }
    
    /**
     * Explicitly switch to physical device URL mode.
     */
    public static void usePhysicalDeviceUrl() {
        enableManualOverride(false);
    }
}
