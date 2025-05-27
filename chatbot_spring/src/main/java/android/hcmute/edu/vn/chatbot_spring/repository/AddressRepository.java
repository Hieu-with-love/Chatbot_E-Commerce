package android.hcmute.edu.vn.chatbot_spring.repository;

import android.hcmute.edu.vn.chatbot_spring.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findByUserId(Integer userId);
}