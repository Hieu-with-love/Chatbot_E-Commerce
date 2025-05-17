package hcmute.edu.vn.chatbot_ec.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import hcmute.edu.vn.chatbot_ec.entity.OrderDetails;

@Dao
public interface OrderDetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(OrderDetails orderDetail);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<OrderDetails> orderDetails);

    @Update
    void update(OrderDetails orderDetail);

    @Delete
    void delete(OrderDetails orderDetail);

    @Query("SELECT * FROM order_details WHERE orderId = :orderId")
    LiveData<List<OrderDetails>> getOrderDetailsByOrderId(int orderId);

    @Query("SELECT * FROM order_details WHERE id = :id")
    LiveData<OrderDetails> getOrderDetailById(int id);

    @Query("DELETE FROM order_details WHERE orderId = :orderId")
    void deleteOrderDetailsByOrderId(int orderId);
}
