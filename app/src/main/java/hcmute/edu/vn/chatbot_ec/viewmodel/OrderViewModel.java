package hcmute.edu.vn.chatbot_ec.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Date;
import java.util.List;

import hcmute.edu.vn.chatbot_ec.entity.Order;
import hcmute.edu.vn.chatbot_ec.entity.OrderDetails;
import hcmute.edu.vn.chatbot_ec.repository.OrderRepository;

public class OrderViewModel extends AndroidViewModel {
    private final OrderRepository orderRepository;

    public OrderViewModel(@NonNull Application application) {
        super(application);
        orderRepository = new OrderRepository(application);
    }

    // Order operations
    public LiveData<List<Order>> getAllOrders() {
        return orderRepository.getAllOrders();
    }

    public LiveData<Order> getOrderById(int id) {
        return orderRepository.getOrderById(id);
    }

    public LiveData<List<Order>> getOrdersByUserId(int userId) {
        return orderRepository.getOrdersByUserId(userId);
    }

    public LiveData<List<Order>> getOrdersByDateRange(Date startDate, Date endDate) {
        return orderRepository.getOrdersByDateRange(startDate, endDate);
    }

    public void createOrder(Order order) {
        orderRepository.insertOrder(order);
    }

    public void updateOrder(Order order) {
        orderRepository.updateOrder(order);
    }

    public void updateOrderStatus(int orderId, String status) {
        orderRepository.updateOrderStatus(orderId, status);
    }

    public void cancelOrder(Order order) {
        orderRepository.deleteOrder(order);
    }

    // Order details operations
    public LiveData<List<OrderDetails>> getOrderDetailsByOrderId(int orderId) {
        return orderRepository.getOrderDetailsByOrderId(orderId);
    }

    public void addOrderDetail(OrderDetails orderDetail) {
        orderRepository.insertOrderDetail(orderDetail);
    }

    public void addAllOrderDetails(List<OrderDetails> orderDetails) {
        orderRepository.insertAllOrderDetails(orderDetails);
    }

    public void updateOrderDetail(OrderDetails orderDetail) {
        orderRepository.updateOrderDetail(orderDetail);
    }

    public void removeOrderDetail(OrderDetails orderDetail) {
        orderRepository.deleteOrderDetail(orderDetail);
    }

    public void clearOrderDetails(int orderId) {
        orderRepository.deleteOrderDetailsByOrderId(orderId);
    }
}
