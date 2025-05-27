package hcmute.edu.vn.chatbot_ec.request;

import java.io.Serializable;

public class AddressRequest implements Serializable {
    private String recipientName;

    private String fullAddress;

    private String phone;

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public AddressRequest() {
    }
}
