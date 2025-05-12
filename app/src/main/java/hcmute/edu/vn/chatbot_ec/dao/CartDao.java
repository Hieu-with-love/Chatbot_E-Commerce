package hcmute.edu.vn.chatbot_ec.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import hcmute.edu.vn.chatbot_ec.entity.Cart;

@Dao
public interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Cart cart);

    @Update
    void update(Cart cart);

    @Delete
    void delete(Cart cart);

    @Query("SELECT * FROM carts WHERE userId = :userId")
    LiveData<Cart> getCartByUserId(int userId);

    @Query("DELETE FROM carts WHERE id = :cartId")
    void deleteCartById(int cartId);

    @Query("UPDATE carts SET totalPrice = :totalPrice WHERE id = :cartId")
    void updateCartTotal(int cartId, double totalPrice);
}
