package hcmute.edu.vn.chatbot_ec.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.Date;
import java.util.List;

import hcmute.edu.vn.chatbot_ec.dao.OrderDao;
import hcmute.edu.vn.chatbot_ec.dao.OrderDetailsDao;
import hcmute.edu.vn.chatbot_ec.database.AppDatabase;
import hcmute.edu.vn.chatbot_ec.entity.Order;
import hcmute.edu.vn.chatbot_ec.entity.OrderDetails;

public class OrderRepository {
    private final OrderDao orderDao;
    private final OrderDetailsDao orderDetailsDao;

    public OrderRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        orderDao = db.orderDao();
        orderDetailsDao = db.orderDetailsDao();
    }

    // Order operations
    public LiveData<List<Order>> getAllOrders() {
        return orderDao.getAllOrders();
    }

    public LiveData<Order> getOrderById(int id) {
        return orderDao.getOrderById(id);
    }

    public LiveData<List<Order>> getOrdersByUserId(int userId) {
        return orderDao.getOrdersByUserId(userId);
    }

    public LiveData<List<Order>> getOrdersByDateRange(Date startDate, Date endDate) {
        return orderDao.getOrdersByDateRange(startDate, endDate);
    }

    public void insertOrder(Order order) {
        AppDatabase.databaseWriteExecutor.execute(() -> orderDao.insert(order));
    }

    public void updateOrder(Order order) {
        AppDatabase.databaseWriteExecutor.execute(() -> orderDao.update(order));
    }

    public void updateOrderStatus(int orderId, String status) {
        AppDatabase.databaseWriteExecutor.execute(() -> orderDao.updateOrderStatus(orderId, status));
    }

    public void deleteOrder(Order order) {
        AppDatabase.databaseWriteExecutor.execute(() -> orderDao.delete(order));
    }

    // Order details operations
    public LiveData<List<OrderDetails>> getOrderDetailsByOrderId(int orderId) {
        return orderDetailsDao.getOrderDetailsByOrderId(orderId);
    }

    public LiveData<OrderDetails> getOrderDetailById(int id) {
        return orderDetailsDao.getOrderDetailById(id);
    }

    public void insertOrderDetail(OrderDetails orderDetail) {
        AppDatabase.databaseWriteExecutor.execute(() -> orderDetailsDao.insert(orderDetail));
    }

    public void insertAllOrderDetails(List<OrderDetails> orderDetails) {
        AppDatabase.databaseWriteExecutor.execute(() -> orderDetailsDao.insertAll(orderDetails));
    }

    public void updateOrderDetail(OrderDetails orderDetail) {
        AppDatabase.databaseWriteExecutor.execute(() -> orderDetailsDao.update(orderDetail));
    }

    public void deleteOrderDetail(OrderDetails orderDetail) {
        AppDatabase.databaseWriteExecutor.execute(() -> orderDetailsDao.delete(orderDetail));
    }

    public void deleteOrderDetailsByOrderId(int orderId) {
        AppDatabase.databaseWriteExecutor.execute(() -> orderDetailsDao.deleteOrderDetailsByOrderId(orderId));
    }
}
