package hcmute.edu.vn.chatbot_ec.utils;

public class ChatGeminiUtils {
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
}
