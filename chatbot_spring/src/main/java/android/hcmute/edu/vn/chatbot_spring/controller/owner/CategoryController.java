package android.hcmute.edu.vn.chatbot_spring.controller.owner;

import android.hcmute.edu.vn.chatbot_spring.dto.request.CreateCategoryRequest;
import android.hcmute.edu.vn.chatbot_spring.model.Category;
import android.hcmute.edu.vn.chatbot_spring.service.CategoryService;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("${api.v1}/owner/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final Faker faker = new Faker();

    @GetMapping
    public ResponseEntity<?> getPagingCategories() {
        return null;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createCategory() {
        return null;
    }

    @PutMapping("/update/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable("categoryId") Integer categoryId) {
        return null;
    }

    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable("categoryId") Integer categoryId) {
        return null;
    }

    @PostMapping("/create-seed")
    public ResponseEntity<?> seedData(@RequestParam(defaultValue = "50") int count) {
        List<CreateCategoryRequest> categoriesReq = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            CreateCategoryRequest categoryRequest = CreateCategoryRequest.builder()
                    .name(faker.commerce().department())
                    .code(faker.commerce().department().replaceAll(" ", "_").toLowerCase())
                    .description(faker.lorem().sentence(2))
                    .thumbnailUrl("https://example.com/image.jpg") // Replace with actual image URL
                    .build();
            categoriesReq.add(categoryRequest);
            // Call the createCategory method to create a new category
        }
        categoryService.createAllCategories(categoriesReq);
        return ResponseEntity.ok("Seed data successfully\n" + categoriesReq);
    }

}
