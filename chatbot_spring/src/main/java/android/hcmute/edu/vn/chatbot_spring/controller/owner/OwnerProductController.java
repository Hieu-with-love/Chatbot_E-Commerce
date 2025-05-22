package android.hcmute.edu.vn.chatbot_spring.controller.owner;

import android.hcmute.edu.vn.chatbot_spring.dto.request.CreateProductRequest;
import android.hcmute.edu.vn.chatbot_spring.service.ProductService;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("${api.v1}/owner/products")
@RequiredArgsConstructor
public class OwnerProductController {
    private final ProductService productService;
    private final Faker faker = new Faker();

    @GetMapping
    public ResponseEntity<?> getPagingProducts() {
        return null;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProduct() {
        return null;
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable("productId") Integer productId) {
        return null;
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable("productId") Integer productId) {
        return null;
    }

    @PostMapping("/create-seed")
    public ResponseEntity<?> seedData(@RequestParam(defaultValue = "200") int count) {
        // Implement the logic to seed data here
        List<CreateProductRequest> productsReq = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            CreateProductRequest createProductRequest = CreateProductRequest.builder()
                    .name(faker.commerce().productName())
                    .description(faker.lorem().sentence(10))
                    .price(new BigDecimal(faker.commerce().price(10.0, 1000.0)))
                    .categoryId(faker.number().numberBetween(1, 49))
                    .thumbnailUrl("https://example.com/image.jpg") // Replace with actual image URL
                    .build();
            productsReq.add(createProductRequest);
            // Call the createProduct method to create a new product
        }

        productService.createAllProducts(productsReq);
        return ResponseEntity.ok("Seed data successfully\n" + productsReq);
    }

}
