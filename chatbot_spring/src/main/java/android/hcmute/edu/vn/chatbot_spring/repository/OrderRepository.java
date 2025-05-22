package android.hcmute.edu.vn.chatbot_spring.repository;

import android.hcmute.edu.vn.chatbot_spring.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserId(Integer userId);
}