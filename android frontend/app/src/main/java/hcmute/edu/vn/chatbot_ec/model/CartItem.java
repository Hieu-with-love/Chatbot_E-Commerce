package hcmute.edu.vn.chatbot_ec.model;

public class CartItem {
    private int id;
    private Product product;
    private int quantity;

    public CartItem(int i, Product product, int quantity) {
        this.id = i;
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
