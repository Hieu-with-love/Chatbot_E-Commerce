package android.hcmute.edu.vn.chatbot_spring.service.impl;

import android.hcmute.edu.vn.chatbot_spring.dto.request.CreateProductRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.request.ProductSearchRequest;
import android.hcmute.edu.vn.chatbot_spring.model.Category;
import android.hcmute.edu.vn.chatbot_spring.model.Product;
import android.hcmute.edu.vn.chatbot_spring.repository.CategoryRepository;
import android.hcmute.edu.vn.chatbot_spring.repository.ProductRepository;
import android.hcmute.edu.vn.chatbot_spring.service.ProductService;
import android.hcmute.edu.vn.chatbot_spring.util.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ImageUtils imageUtils;

    @Override
    public Page<Product> getAllProducts(int page, int size, String sortBy, String sortDir) {
        return null;
    }

    @Override
    public Product createProduct(CreateProductRequest req, MultipartFile imageFile) {
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new BadCredentialsException("Category not found"));
        String urlThumb = req.getThumbnailUrl();
        if (imageFile!=null){
            // use saveImage method from ImageUtils
            urlThumb = imageUtils.uploadImage(imageFile);

        }
        Product product = Product.builder()
                .name(req.getName())
                .description(req.getDescription())
                .price(req.getPrice())
                .thumbnailUrl(urlThumb)
                .category(category)
                .build();
        return productRepository.save(product);
    }

    @Override
    public void createAllProducts(List<CreateProductRequest> reqs) {
        List<Product> products = reqs.stream()
                .map(req -> Product.builder()
                        .name(req.getName())
                        .description(req.getDescription())
                        .price(req.getPrice())
                        .thumbnailUrl(req.getThumbnailUrl())
                        .category(categoryRepository.findById(req.getCategoryId())
                                .orElseThrow(() -> new BadCredentialsException("Category not found")))
                        .build())
                .toList();
        productRepository.saveAll(products);
    }

    @Override
    public List<Product> searchProducts(ProductSearchRequest request) {
        String name = request.getName() != null ? request.getName() : "";
        String categoryName = request.getCategoryName() != null ? request.getCategoryName() : "";
        BigDecimal price = request.getPrice() != null ? request.getPrice() : BigDecimal.ZERO;

        return productRepository.findByCriteria(name, categoryName, price);
    }

    @Override
    public Product updateProduct(int id, CreateProductRequest req, MultipartFile imageFile) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new BadCredentialsException("Product not found witd id: "));
        String urlThumb = req.getThumbnailUrl();

        existingProduct.setName(req.getName());
        existingProduct.setDescription(req.getDescription());
        existingProduct.setPrice(req.getPrice());

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new BadCredentialsException("Category not found"));
        category = !category.getId().equals(existingProduct.getCategory().getId()) ?
                category : existingProduct.getCategory();
        existingProduct.setCategory(category);

        if (imageFile!=null){
            urlThumb = imageUtils.uploadImage(imageFile);
        }
        existingProduct.setThumbnailUrl(urlThumb);

        return existingProduct;
    }

    @Override
    public void deleteProduct(int id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new BadCredentialsException("Product not found witd id: "));
        productRepository.delete(existingProduct);
    }
}
