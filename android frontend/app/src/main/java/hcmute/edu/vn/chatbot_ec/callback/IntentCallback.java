package hcmute.edu.vn.chatbot_ec.callback;

public interface IntentCallback {
    void onIntentGenerated(String intent);
    void onError(String errorMessage);
}
