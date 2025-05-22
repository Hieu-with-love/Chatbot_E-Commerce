package hcmute.edu.vn.chatbot_ec.service;

public interface ChatbotService {
    void extractSearchJsonThenSendToBackend(String originalPrompt);
    void sendToSpringBackend(String jsonResponse, String originalPrompt, String intentType);
}
