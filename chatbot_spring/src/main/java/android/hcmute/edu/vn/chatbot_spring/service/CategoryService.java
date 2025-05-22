package android.hcmute.edu.vn.chatbot_spring.service;

import android.hcmute.edu.vn.chatbot_spring.dto.request.CreateCategoryRequest;
import android.hcmute.edu.vn.chatbot_spring.model.Category;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories(int page, int size);
    Category getCategoryById(int categoryId);
    Category createCategory(CreateCategoryRequest req, MultipartFile fileImage);
    void createAllCategories(List<CreateCategoryRequest> reqs);
    Category updateCategory(int categoryId, CreateCategoryRequest req, MultipartFile fileImage);
    void deleteCategory(int categoryId);
}
