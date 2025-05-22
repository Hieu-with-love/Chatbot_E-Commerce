package hcmute.edu.vn.chatbot_ec.enums;

public enum SENDER {
    USER("user"),
    BOT("bot");

    private final String value;

    SENDER(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
