package hcmute.edu.vn.chatbot_ec.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import hcmute.edu.vn.chatbot_ec.entity.Cart;
import hcmute.edu.vn.chatbot_ec.entity.CartItem;
import hcmute.edu.vn.chatbot_ec.repository.CartRepository;

public class CartViewModel extends AndroidViewModel {
    private final CartRepository cartRepository;

    public CartViewModel(@NonNull Application application) {
        super(application);
        cartRepository = new CartRepository(application);
    }

    // Cart operations
    public LiveData<Cart> getCartByUserId(int userId) {
        return cartRepository.getCartByUserId(userId);
    }

    public void insertCart(Cart cart) {
        cartRepository.insertCart(cart);
    }

    public void updateCart(Cart cart) {
        cartRepository.updateCart(cart);
    }

    public void deleteCart(Cart cart) {
        cartRepository.deleteCart(cart);
    }

    public void updateCartTotal(int cartId, double totalPrice) {
        cartRepository.updateCartTotal(cartId, totalPrice);
    }

    // CartItem operations
    public LiveData<List<CartItem>> getCartItemsByCartId(int cartId) {
        return cartRepository.getCartItemsByCartId(cartId);
    }

    public LiveData<CartItem> getCartItemById(int id) {
        return cartRepository.getCartItemById(id);
    }

    public LiveData<CartItem> getCartItemByProductId(int cartId, int productId) {
        return cartRepository.getCartItemByProductId(cartId, productId);
    }

    public void addToCart(CartItem cartItem) {
        cartRepository.insertCartItem(cartItem);
    }

    public void updateCartItem(CartItem cartItem) {
        cartRepository.updateCartItem(cartItem);
    }

    public void removeFromCart(CartItem cartItem) {
        cartRepository.deleteCartItem(cartItem);
    }

    public void clearCart(int cartId) {
        cartRepository.deleteAllCartItems(cartId);
    }

    public void updateQuantity(int cartItemId, int quantity) {
        cartRepository.updateCartItemQuantity(cartItemId, quantity);
    }

    public void deleteCartItemById(int cartItemId) {
        cartRepository.deleteCartItemById(cartItemId);
    }
}
