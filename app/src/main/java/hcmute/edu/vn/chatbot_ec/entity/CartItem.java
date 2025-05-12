package hcmute.edu.vn.chatbot_ec.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "cart_items",
    foreignKeys = {
        @ForeignKey(
            entity = Cart.class,
            parentColumns = "id",
            childColumns = "cartId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = Product.class,
            parentColumns = "id",
            childColumns = "productId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index("cartId"),
        @Index("productId")
    }
)
public class CartItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int productId;
    private int quantity;
    private int cartId;
    
    public CartItem() {
    }
    
    public CartItem(int productId, int quantity, int cartId) {
        this.productId = productId;
        this.quantity = quantity;
        this.cartId = cartId;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getProductId() {
        return productId;
    }
    
    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public int getCartId() {
        return cartId;
    }
    
    public void setCartId(int cartId) {
        this.cartId = cartId;
    }
}
