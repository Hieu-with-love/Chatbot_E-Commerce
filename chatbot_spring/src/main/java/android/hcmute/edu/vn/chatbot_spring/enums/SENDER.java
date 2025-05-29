package android.hcmute.edu.vn.chatbot_spring.enums;

public enum SENDER {
    USER("user"),
    BOT("bot");

    public static SENDER fromValue(String value) {
        for (SENDER sender : SENDER.values()) {
            if (sender.getValue().equalsIgnoreCase(value)) {
                return sender;
            }
        }
        throw new IllegalArgumentException("Invalid sender value: " + value);
    }


    private final String value;

    SENDER(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
