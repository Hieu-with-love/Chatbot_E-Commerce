package android.hcmute.edu.vn.chatbot_spring.service.impl;

import android.hcmute.edu.vn.chatbot_spring.dto.request.CreateOrderRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.request.UpdateOrderStatusRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.response.CartItemResponse;
import android.hcmute.edu.vn.chatbot_spring.dto.response.CartResponse;
import android.hcmute.edu.vn.chatbot_spring.dto.response.OrderItemResponse;
import android.hcmute.edu.vn.chatbot_spring.dto.response.OrderResponse;
import android.hcmute.edu.vn.chatbot_spring.enums.OrderStatus;
import android.hcmute.edu.vn.chatbot_spring.exception.ConflictException;
import android.hcmute.edu.vn.chatbot_spring.exception.ResourceNotFoundException;
import android.hcmute.edu.vn.chatbot_spring.model.*;
import android.hcmute.edu.vn.chatbot_spring.repository.AddressRepository;
import android.hcmute.edu.vn.chatbot_spring.repository.OrderRepository;
import android.hcmute.edu.vn.chatbot_spring.repository.ProductRepository;
import android.hcmute.edu.vn.chatbot_spring.repository.UserRepository;
import android.hcmute.edu.vn.chatbot_spring.service.CartService;
import android.hcmute.edu.vn.chatbot_spring.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;

    @Transactional
    @Override
    public OrderResponse createOrder(Integer userId, CreateOrderRequest request) {
        // Lấy giỏ hàng của người dùng
        CartResponse cartResponse = cartService.getCartByUserId(userId);
        if (cartResponse.getCartItems().isEmpty()) {
            throw new ResourceNotFoundException("Cart is empty");
        }

        // Lấy thông tin người dùng
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ: " + request.getAddressId()));
        if (!address.getUser().getId().equals(userId)) {
            throw new ConflictException("Địa chỉ không thuộc về người dùng này");
        }

        // Tạo đơn hàng mới
        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .recipientName(address.getRecipientName())
                .shippingAddress(address.getFullAddress())
                .shippingPhone(address.getPhone())
                .totalPrice(BigDecimal.ZERO)
                .orderItems(new ArrayList<>())
                .build();

        BigDecimal totalPrice = BigDecimal.ZERO;

        // Chuyển các CartItem thành OrderItem
        for (CartItemResponse cartItem : cartResponse.getCartItems()) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + cartItem.getProductId()));
            if (product.getStock() < cartItem.getQuantity()) {
                throw new ConflictException("Không đủ tồn kho cho sản phẩm: " + product.getName());
            }
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(cartItem.getPrice())
                    .build();
            order.getOrderItems().add(orderItem);
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
            totalPrice = totalPrice.add(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        order.setTotalPrice(totalPrice);

        // Lưu đơn hàng
        order = orderRepository.save(order);

        // Xóa giỏ hàng sau khi tạo đơn hàng
        cartService.clearCart(userId);

        return convertToOrderResponse(order);
    }

    @Override
    public List<OrderResponse> getOrdersByUserId(Integer userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(this::convertToOrderResponse)
                .toList();
    }

    @Override
    public OrderResponse getOrderById(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        return convertToOrderResponse(order);
    }

    @Transactional
    @Override
    public OrderResponse updateOrderStatus(Integer orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        order.setStatus(request.getStatus());
        orderRepository.save(order);
        return convertToOrderResponse(order);
    }

    @Transactional
    @Override
    public void cancelOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            throw new ConflictException("Only PENDING orders can be canceled");
        }

        // Khôi phục tồn kho
        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = productRepository.findById(orderItem.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm: " + orderItem.getProduct().getId()));
            product.setStock(product.getStock() + orderItem.getQuantity());
            productRepository.save(product);
        }

        orderRepository.delete(order);
    }


    // Chuyển đổi Order entity thành OrderResponse
    private OrderResponse convertToOrderResponse(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(order.getId());
        orderResponse.setUserId(order.getUser().getId());
        orderResponse.setOrderDate(order.getOrderDate());
        orderResponse.setStatus(order.getStatus());
        orderResponse.setRecipientName(order.getRecipientName());
        orderResponse.setShippingAddress(order.getShippingAddress());
        orderResponse.setShippingPhone(order.getShippingPhone());
        orderResponse.setTotalPrice(order.getTotalPrice());
        orderResponse.setOrderItems(order.getOrderItems().stream()
                .map(this::convertToOrderItemResponse)
                .toList());
        return orderResponse;
    }

    // Chuyển đổi OrderItem entity thành OrderItemResponse
    private OrderItemResponse convertToOrderItemResponse(OrderItem orderItem) {
        OrderItemResponse orderItemResponse = new OrderItemResponse();
        orderItemResponse.setId(orderItem.getId());
        orderItemResponse.setProductId(orderItem.getProduct().getId());
        orderItemResponse.setProductName(orderItem.getProduct().getName());
        orderItemResponse.setThumbnailUrl(orderItem.getProduct().getThumbnailUrl());
        orderItemResponse.setSize(orderItem.getProduct().getSize());
        orderItemResponse.setColor(orderItem.getProduct().getColor());
        orderItemResponse.setPriceAtPurchase(orderItem.getPriceAtPurchase());
        orderItemResponse.setQuantity(orderItem.getQuantity());
        return orderItemResponse;
    }
}
