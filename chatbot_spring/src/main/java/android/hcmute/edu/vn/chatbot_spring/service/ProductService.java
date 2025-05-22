package android.hcmute.edu.vn.chatbot_spring.service;

import android.hcmute.edu.vn.chatbot_spring.dto.request.CreateProductRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.request.ProductSearchRequest;
import android.hcmute.edu.vn.chatbot_spring.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    Page<Product> getAllProducts(int page, int size, String sortBy, String sortDir);
    Product createProduct(CreateProductRequest req, MultipartFile imageFile);
    void createAllProducts(List<CreateProductRequest> reqs);
    List<Product> searchProducts(ProductSearchRequest request);
    Product updateProduct(int id, CreateProductRequest req, MultipartFile imageFile);
    void deleteProduct(int id);
}
