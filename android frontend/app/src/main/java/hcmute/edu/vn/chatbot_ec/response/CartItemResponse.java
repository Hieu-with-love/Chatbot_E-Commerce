package hcmute.edu.vn.chatbot_ec.response;

import java.math.BigDecimal;

public class CartItemResponse {
    private Integer id;
    private Integer productId;
    private String name;
    private String thumbnailUrl;
    private BigDecimal price;
    private int quantity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public CartItemResponse(Integer productId, Integer id, String name, String thumbnailUrl, BigDecimal price, int quantity) {
        this.productId = productId;
        this.id = id;
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
        this.price = price;
        this.quantity = quantity;
    }
}
