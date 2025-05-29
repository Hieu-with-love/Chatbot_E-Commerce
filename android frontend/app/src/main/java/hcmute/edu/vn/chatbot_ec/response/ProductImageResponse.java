package hcmute.edu.vn.chatbot_ec.response;

import com.google.gson.annotations.SerializedName;

public class ProductImageResponse {
    @SerializedName("id")
    private Integer id;

    @SerializedName("imageUrl")
    private String imageUrl;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}