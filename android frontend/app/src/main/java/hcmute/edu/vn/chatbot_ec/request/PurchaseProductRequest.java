package hcmute.edu.vn.chatbot_ec.request;

public class PurchaseProductRequest {
    private Integer productId;
    private Integer quantity;
    private Integer addressId;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public PurchaseProductRequest() {
    }
}
