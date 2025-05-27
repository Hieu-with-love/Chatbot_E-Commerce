package android.hcmute.edu.vn.chatbot_spring.service;

import android.hcmute.edu.vn.chatbot_spring.dto.request.CreateProductRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.request.ProductSearchRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.response.PageResponse;
import android.hcmute.edu.vn.chatbot_spring.dto.response.ProductResponse;
import android.hcmute.edu.vn.chatbot_spring.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    Product createProduct(CreateProductRequest req, MultipartFile imageFile);
    void createAllProducts(List<CreateProductRequest> reqs);
    List<Product> searchProducts(ProductSearchRequest request);
    Product updateProduct(int id, CreateProductRequest req, MultipartFile imageFile);
    PageResponse<ProductResponse> getAllProducts(int page, int size, String sort, String direction);
    void deleteProduct(int id);

    PageResponse<ProductResponse> searchProductsByKeyword(int page, int size, String sort, String direction, String keyword);
}
