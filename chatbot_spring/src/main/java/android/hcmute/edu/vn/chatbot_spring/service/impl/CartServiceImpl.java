package android.hcmute.edu.vn.chatbot_spring.service.impl;

import android.hcmute.edu.vn.chatbot_spring.dto.request.AddCartItemRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.request.UpdateCartItemRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.response.CartItemResponse;
import android.hcmute.edu.vn.chatbot_spring.dto.response.CartResponse;
import android.hcmute.edu.vn.chatbot_spring.exception.ResourceNotFoundException;
import android.hcmute.edu.vn.chatbot_spring.model.Cart;
import android.hcmute.edu.vn.chatbot_spring.model.CartItem;
import android.hcmute.edu.vn.chatbot_spring.model.Product;
import android.hcmute.edu.vn.chatbot_spring.model.User;
import android.hcmute.edu.vn.chatbot_spring.repository.CartItemRepository;
import android.hcmute.edu.vn.chatbot_spring.repository.CartRepository;
import android.hcmute.edu.vn.chatbot_spring.repository.ProductRepository;
import android.hcmute.edu.vn.chatbot_spring.repository.UserRepository;
import android.hcmute.edu.vn.chatbot_spring.service.CartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    private Cart getOrCreateCart(Integer userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
            cart = Cart.builder()
                    .user(user)
                    .cartItems(new ArrayList<>())
                    .build();
            cart = cartRepository.save(cart);
        }
        return cart;
    }

    @Override
    public CartResponse getCartByUserId(Integer userId) {
        Cart cart = getOrCreateCart(userId);
        return convertToCartResponse(cart);
    }

    @Transactional
    @Override
    public CartResponse addItemToCart(Integer userId, AddCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + request.getProductId()));

        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        CartItem existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(request.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Cập nhật số lượng nếu sản phẩm đã tồn tại
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            // Thêm sản phẩm mới vào giỏ hàng
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cart.getCartItems().add(cartItem);
            cartItemRepository.save(cartItem);
        }

        return convertToCartResponse(cart);
    }

    @Transactional
    @Override
    public CartResponse updateCartItem(Integer userId, Integer itemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + itemId));

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        return convertToCartResponse(cart);
    }

    @Transactional
    @Override
    public CartResponse removeItemFromCart(Integer userId, Integer itemId) {
        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + itemId));

        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        return convertToCartResponse(cart);
    }

    @Transactional
    @Override
    public void clearCart(Integer userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }


    private CartResponse convertToCartResponse(Cart cart) {
        CartResponse cartResponse = new CartResponse();
        cartResponse.setId(cart.getId());
        cartResponse.setUserId(cart.getUser().getId());
        cartResponse.setCartItems(cart.getCartItems().stream()
                .map(this::convertToCartItemResponse)
                .toList());
        return cartResponse;
    }

    private CartItemResponse convertToCartItemResponse(CartItem cartItem) {
        CartItemResponse cartItemResponse = new CartItemResponse();
        cartItemResponse.setId(cartItem.getId());
        cartItemResponse.setProductId(cartItem.getProduct().getId());
        cartItemResponse.setName(cartItem.getProduct().getName());
        cartItemResponse.setThumbnailUrl(cartItem.getProduct().getThumbnailUrl());
        cartItemResponse.setPrice(cartItem.getProduct().getPrice());
        cartItemResponse.setQuantity(cartItem.getQuantity());
        return cartItemResponse;
    }
}
