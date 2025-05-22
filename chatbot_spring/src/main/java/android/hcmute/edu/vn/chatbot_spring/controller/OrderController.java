package android.hcmute.edu.vn.chatbot_spring.controller;

import android.hcmute.edu.vn.chatbot_spring.dto.request.CreateOrderRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.request.UpdateOrderStatusRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.response.OrderResponse;
import android.hcmute.edu.vn.chatbot_spring.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    // Tạo đơn hàng từ giỏ hàng
    @PostMapping("/{userId}")
    public ResponseEntity<OrderResponse> createOrder(@PathVariable Integer userId,
                                                    @RequestBody CreateOrderRequest request) {
        OrderResponse orderResponse = orderService.createOrder(userId, request);
        return ResponseEntity.ok(orderResponse);
    }

    // Lấy danh sách đơn hàng của người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(@PathVariable Integer userId) {
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    // Lấy chi tiết đơn hàng
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Integer orderId) {
        OrderResponse orderResponse = orderService.getOrderById(orderId);
        return ResponseEntity.ok(orderResponse);
    }

    // Cập nhật trạng thái đơn hàng
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Integer orderId,
                                                          @RequestBody UpdateOrderStatusRequest request) {
        OrderResponse orderResponse = orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(orderResponse);
    }

    // Hủy đơn hàng
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Integer orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}