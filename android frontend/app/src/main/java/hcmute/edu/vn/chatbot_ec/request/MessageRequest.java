package hcmute.edu.vn.chatbot_ec.request;

public class MessageRequest {
    private String content;
    private String sender;

    public MessageRequest() {}

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
