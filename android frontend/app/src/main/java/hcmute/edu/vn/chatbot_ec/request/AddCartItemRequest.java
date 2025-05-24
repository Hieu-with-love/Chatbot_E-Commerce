package hcmute.edu.vn.chatbot_ec.request;

public class AddCartItemRequest {
    private Integer productId;
    private Integer quantity;

    public Integer getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public AddCartItemRequest(Integer productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
