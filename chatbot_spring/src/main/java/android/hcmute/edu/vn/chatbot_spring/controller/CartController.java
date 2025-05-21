package android.hcmute.edu.vn.chatbot_spring.controller;

import android.hcmute.edu.vn.chatbot_spring.dto.request.AddCartItemRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.request.UpdateCartItemRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.response.CartResponse;
import android.hcmute.edu.vn.chatbot_spring.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable Integer userId) {
        CartResponse cartResponse = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cartResponse);
    }

    // Thêm sản phẩm vào giỏ hàng
    @PostMapping("/{userId}/items")
    public ResponseEntity<CartResponse> addItemToCart(@PathVariable Integer userId,
                                                      @RequestBody AddCartItemRequest request) {
        CartResponse cartResponse = cartService.addItemToCart(userId, request);
        return ResponseEntity.ok(cartResponse);
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    @PutMapping("/{userId}/items/{itemId}")
    public ResponseEntity<CartResponse> updateCartItem(@PathVariable Integer userId,
                                                       @PathVariable Integer itemId,
                                                       @RequestBody UpdateCartItemRequest request) {
        CartResponse cartResponse = cartService.updateCartItem(userId, itemId, request);
        return ResponseEntity.ok(cartResponse);
    }

    // Xóa sản phẩm khỏi giỏ hàng
    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<CartResponse> removeItemFromCart(@PathVariable Integer userId,
                                                           @PathVariable Integer itemId) {
        CartResponse cartResponse = cartService.removeItemFromCart(userId, itemId);
        return ResponseEntity.ok(cartResponse);
    }

    // Xóa toàn bộ giỏ hàng
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Integer userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
