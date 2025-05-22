package android.hcmute.edu.vn.chatbot_spring.repository;

import android.hcmute.edu.vn.chatbot_spring.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}