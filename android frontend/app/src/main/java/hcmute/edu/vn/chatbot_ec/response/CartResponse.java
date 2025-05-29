package hcmute.edu.vn.chatbot_ec.response;

import java.util.List;

public class CartResponse {
    private Integer id;
    private Integer userId;
    private List<CartItemResponse> cartItems;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<CartItemResponse> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItemResponse> cartItems) {
        this.cartItems = cartItems;
    }

    public CartResponse() {
    }

    public CartResponse(Integer id, Integer userId, List<CartItemResponse> cartItems) {
        this.id = id;
        this.userId = userId;
        this.cartItems = cartItems;
    }
}
