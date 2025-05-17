package hcmute.edu.vn.chatbot_ec.model;

public class Message {
    public static final int SENT_BY_USER = 0;
    public static final int SENT_BY_BOT = 1;

    private String content;
    private int sentBy;

    public Message(String content, int sentBy) {
        this.content = content;
        this.sentBy = sentBy;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSentBy() {
        return sentBy;
    }

    public void setSentBy(int sentBy) {
        this.sentBy = sentBy;
    }
}