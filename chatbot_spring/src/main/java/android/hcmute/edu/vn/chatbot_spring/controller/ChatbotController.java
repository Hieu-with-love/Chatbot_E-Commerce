package android.hcmute.edu.vn.chatbot_spring.controller;

import android.hcmute.edu.vn.chatbot_spring.dto.request.ProductSearchRequest;
import android.hcmute.edu.vn.chatbot_spring.model.Product;
import android.hcmute.edu.vn.chatbot_spring.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.v1}/chatbot")
@RequiredArgsConstructor
public class ChatbotController {
    private final ProductService productService;

    @PostMapping("/process-product")
    public ResponseEntity<?> searchProducts(@RequestBody ProductSearchRequest req) {
        try{
            List<Product> products = productService.searchProducts(req);
            return ResponseEntity.ok(products);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
