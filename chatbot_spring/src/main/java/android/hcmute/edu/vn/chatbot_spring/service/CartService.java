package android.hcmute.edu.vn.chatbot_spring.service;

import android.hcmute.edu.vn.chatbot_spring.dto.request.AddCartItemRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.request.UpdateCartItemRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.response.CartResponse;
import jakarta.transaction.Transactional;

public interface CartService {
    CartResponse getCartByUserId(Integer userId);

    CartResponse addItemToCart(Integer userId, AddCartItemRequest request);

    CartResponse updateCartItem(Integer userId, Integer itemId, UpdateCartItemRequest request);

    CartResponse removeItemFromCart(Integer userId, Integer itemId);

    void clearCart(Integer userId);
}
