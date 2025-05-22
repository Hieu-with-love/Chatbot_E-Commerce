package hcmute.edu.vn.chatbot_ec.utils;

public class MyJsonUtils {
    public static String removeMarkdownJson(String json) {
        return json
                .replaceAll("(?i)```json", "")  // Xóa phần mở đầu ```json (không phân biệt hoa thường)
                .replaceAll("```", "")          // Xóa phần kết thúc ```
                .trim();
    }
}
