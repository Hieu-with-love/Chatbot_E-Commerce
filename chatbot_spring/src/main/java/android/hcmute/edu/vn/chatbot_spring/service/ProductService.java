package android.hcmute.edu.vn.chatbot_spring.service;

import android.hcmute.edu.vn.chatbot_spring.dto.response.PageResponse;
import android.hcmute.edu.vn.chatbot_spring.dto.response.ProductResponse;

public interface ProductService {
    PageResponse<ProductResponse> getAllProducts(int page, int size, String sort, String direction);
}
