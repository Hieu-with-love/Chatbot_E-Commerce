package android.hcmute.edu.vn.chatbot_spring.repository;

import android.hcmute.edu.vn.chatbot_spring.model.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("SELECT p FROM Product p WHERE " +
            "(:productName IS NULL OR LOWER(p.nameUnsigned) LIKE LOWER(CONCAT('%', :productName, '%'))) AND " +
            "(:categoryName IS NULL OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :categoryName, '%'))) AND " +
            "(:price IS NULL OR p.price <= :price)")
    List<Product> findByCriteria(@Param("productName") String productName,
                                 @Param("categoryName") String categoryName,
                                 @Param("price") BigDecimal price);


    @Query("SELECT p FROM Product p WHERE " +
            "(:productName IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :productName, '%'))) AND " +
            "(:size IS NULL OR LOWER(p.size) LIKE LOWER(CONCAT('%', :size, '%')))")
    List<Product> findProductBySize(@Param("productName") String productName,
                                    @Param("size") String size);

    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}
