package hcmute.edu.vn.chatbot_ec.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import hcmute.edu.vn.chatbot_ec.entity.CartItem;

@Dao
public interface CartItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CartItem cartItem);

    @Update
    void update(CartItem cartItem);

    @Delete
    void delete(CartItem cartItem);

    @Query("SELECT * FROM cart_items WHERE cartId = :cartId")
    LiveData<List<CartItem>> getCartItemsByCartId(int cartId);

    @Query("SELECT * FROM cart_items WHERE id = :id")
    LiveData<CartItem> getCartItemById(int id);

    @Query("DELETE FROM cart_items WHERE cartId = :cartId")
    void deleteAllCartItems(int cartId);

    @Query("SELECT * FROM cart_items WHERE cartId = :cartId AND productId = :productId")
    LiveData<CartItem> getCartItemByProductId(int cartId, int productId);

    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :cartItemId")
    void updateCartItemQuantity(int cartItemId, int quantity);

    @Query("DELETE FROM cart_items WHERE id = :cartItemId")
    void deleteCartItemById(int cartItemId);
}
