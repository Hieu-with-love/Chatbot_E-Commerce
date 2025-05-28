package hcmute.edu.vn.chatbot_ec.model;

import hcmute.edu.vn.chatbot_ec.enums.SENDER;

public class ChatMessage {
    private SENDER sender;

    private String content;

    public ChatMessage(SENDER sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    public SENDER getSender() {
        return sender;
    }

    public void setSender(SENDER sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
