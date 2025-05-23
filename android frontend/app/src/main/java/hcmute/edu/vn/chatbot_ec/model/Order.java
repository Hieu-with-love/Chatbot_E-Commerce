package hcmute.edu.vn.chatbot_ec.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private Integer id;
    private LocalDateTime orderDate;
    private String status;
    private String shippingAddress;
    private String shippingPhone;
    private BigDecimal totalPrice;
    private User user;
    private List<OrderDetails> orderItems = new ArrayList<>();
    private String paymentMethod;
}