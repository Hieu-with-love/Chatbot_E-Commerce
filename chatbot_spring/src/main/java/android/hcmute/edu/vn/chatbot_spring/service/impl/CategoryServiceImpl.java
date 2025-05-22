package android.hcmute.edu.vn.chatbot_spring.service.impl;

import android.hcmute.edu.vn.chatbot_spring.dto.request.CreateCategoryRequest;
import android.hcmute.edu.vn.chatbot_spring.model.Category;
import android.hcmute.edu.vn.chatbot_spring.repository.CategoryRepository;
import android.hcmute.edu.vn.chatbot_spring.service.CategoryService;
import android.hcmute.edu.vn.chatbot_spring.util.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ImageUtils imageUtils;

    @Override
    public List<Category> getAllCategories(int page, int size) {
        return List.of();
    }

    @Override
    public Category getCategoryById(int categoryId) {
        return null;
    }

    @Override
    public Category createCategory(CreateCategoryRequest req, MultipartFile fileImage) {

        if (categoryRepository.existsByCode(req.getCode())) {
            throw new RuntimeException("Category code already exists");
        }
        if (categoryRepository.existsByName(req.getName())) {
            throw new RuntimeException("Category name already exists");
        }

        String urlThumb = req.getThumbnailUrl();
        if (fileImage != null) {
            urlThumb = imageUtils.uploadImage(fileImage);
        }

        Category category = Category.builder()
                .name(req.getName())
                .code(req.getCode())
                .description(req.getDescription())
                .thumbnailUrl(urlThumb)
                .build();
        return categoryRepository.save(category);
    }

    @Override
    public void createAllCategories(List<CreateCategoryRequest> reqs) {
        List<Category> categories = reqs.stream()
                        .map(
                                req -> Category.builder()
                                        .name(req.getName())
                                        .code(req.getCode())
                                        .description(req.getDescription())
                                        .thumbnailUrl(req.getThumbnailUrl())
                                        .build()
                        ).toList();

        categoryRepository.saveAll(categories);
    }

    @Override
    public Category updateCategory(int categoryId, CreateCategoryRequest req, MultipartFile fileImage) {
        Category existingCate = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!existingCate.getCode().equals(req.getCode()) && categoryRepository.existsByCode(req.getCode())) {
            throw new RuntimeException("Category code already exists");
        }
        if (!existingCate.getName().equals(req.getName()) && categoryRepository.existsByName(req.getName())) {
            throw new RuntimeException("Category name already exists");
        }

        String urlThumb = req.getThumbnailUrl();
        if (fileImage != null) {
            urlThumb = imageUtils.uploadImage(fileImage);
        }

        existingCate.setName(req.getName());
        existingCate.setCode(req.getCode());
        existingCate.setDescription(req.getDescription());
        existingCate.setThumbnailUrl(urlThumb);

        return categoryRepository.save(existingCate);
    }

    @Override
    public void deleteCategory(int categoryId) {
        Category existingCate = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        categoryRepository.delete(existingCate);
    }
}
