package android.hcmute.edu.vn.chatbot_spring.service;

import android.hcmute.edu.vn.chatbot_spring.dto.request.CreateOrderRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.request.PurchaseProductRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.request.UpdateOrderStatusRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.response.OrderResponse;
import jakarta.transaction.Transactional;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(Integer userId, CreateOrderRequest request);

    List<OrderResponse> getOrdersByUserId(Integer userId);

    OrderResponse getOrderById(Integer orderId);

    OrderResponse updateOrderStatus(Integer orderId, UpdateOrderStatusRequest request);

    void cancelOrder(Integer orderId);

    OrderResponse purchaseProduct(Integer userId, PurchaseProductRequest request);
}
