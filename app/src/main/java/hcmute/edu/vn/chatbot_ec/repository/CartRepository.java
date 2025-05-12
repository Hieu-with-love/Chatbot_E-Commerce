package hcmute.edu.vn.chatbot_ec.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import hcmute.edu.vn.chatbot_ec.dao.CartDao;
import hcmute.edu.vn.chatbot_ec.dao.CartItemDao;
import hcmute.edu.vn.chatbot_ec.database.AppDatabase;
import hcmute.edu.vn.chatbot_ec.entity.Cart;
import hcmute.edu.vn.chatbot_ec.entity.CartItem;

public class CartRepository {
    private final CartDao cartDao;
    private final CartItemDao cartItemDao;

    public CartRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        cartDao = db.cartDao();
        cartItemDao = db.cartItemDao();
    }

    // Cart operations
    public LiveData<Cart> getCartByUserId(int userId) {
        return cartDao.getCartByUserId(userId);
    }

    public void insertCart(Cart cart) {
        AppDatabase.databaseWriteExecutor.execute(() -> cartDao.insert(cart));
    }

    public void updateCart(Cart cart) {
        AppDatabase.databaseWriteExecutor.execute(() -> cartDao.update(cart));
    }

    public void deleteCart(Cart cart) {
        AppDatabase.databaseWriteExecutor.execute(() -> cartDao.delete(cart));
    }

    public void deleteCartById(int cartId) {
        AppDatabase.databaseWriteExecutor.execute(() -> cartDao.deleteCartById(cartId));
    }

    public void updateCartTotal(int cartId, double totalPrice) {
        AppDatabase.databaseWriteExecutor.execute(() -> cartDao.updateCartTotal(cartId, totalPrice));
    }

    // CartItem operations
    public LiveData<List<CartItem>> getCartItemsByCartId(int cartId) {
        return cartItemDao.getCartItemsByCartId(cartId);
    }

    public LiveData<CartItem> getCartItemById(int id) {
        return cartItemDao.getCartItemById(id);
    }

    public LiveData<CartItem> getCartItemByProductId(int cartId, int productId) {
        return cartItemDao.getCartItemByProductId(cartId, productId);
    }

    public void insertCartItem(CartItem cartItem) {
        AppDatabase.databaseWriteExecutor.execute(() -> cartItemDao.insert(cartItem));
    }

    public void updateCartItem(CartItem cartItem) {
        AppDatabase.databaseWriteExecutor.execute(() -> cartItemDao.update(cartItem));
    }

    public void deleteCartItem(CartItem cartItem) {
        AppDatabase.databaseWriteExecutor.execute(() -> cartItemDao.delete(cartItem));
    }

    public void deleteAllCartItems(int cartId) {
        AppDatabase.databaseWriteExecutor.execute(() -> cartItemDao.deleteAllCartItems(cartId));
    }

    public void updateCartItemQuantity(int cartItemId, int quantity) {
        AppDatabase.databaseWriteExecutor.execute(() -> cartItemDao.updateCartItemQuantity(cartItemId, quantity));
    }

    public void deleteCartItemById(int cartItemId) {
        AppDatabase.databaseWriteExecutor.execute(() -> cartItemDao.deleteCartItemById(cartItemId));
    }
}
