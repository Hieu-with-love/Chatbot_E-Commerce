package android.hcmute.edu.vn.chatbot_spring.service.impl;

import android.hcmute.edu.vn.chatbot_spring.dto.request.CreateProductRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.request.ProductSearchRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.response.PageResponse;
import android.hcmute.edu.vn.chatbot_spring.dto.response.ProductImageResponse;
import android.hcmute.edu.vn.chatbot_spring.dto.response.ProductResponse;
import android.hcmute.edu.vn.chatbot_spring.model.Category;
import android.hcmute.edu.vn.chatbot_spring.model.Product;
import android.hcmute.edu.vn.chatbot_spring.model.ProductImage;
import android.hcmute.edu.vn.chatbot_spring.repository.CategoryRepository;
import android.hcmute.edu.vn.chatbot_spring.repository.ProductRepository;
import android.hcmute.edu.vn.chatbot_spring.service.ProductService;
import android.hcmute.edu.vn.chatbot_spring.util.ImageUtils;
import android.hcmute.edu.vn.chatbot_spring.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        String productName = request.getProductName() != null ? request.getProductName() : "";
        String categoryName = request.getCategoryName() != null ? request.getCategoryName() : "";
        String description = request.getDescription() != null ? request.getDescription() : "";
        BigDecimal price = request.getPrice() != null ? request.getPrice() : BigDecimal.ZERO;

        return productRepository.findByCriteria(productName, description, categoryName, price);
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
    @Override
    public PageResponse<ProductResponse> getAllProducts(int page, int size, String sort, String direction) {
        Pageable pageable = PaginationUtil.createPageable(page, size, sort, direction);

        Page<Product> productPage = productRepository.findAll(pageable);

        return PageResponse.<ProductResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(productPage.getTotalPages())
                .totalElements(productPage.getTotalElements())
                .content(productPage.getContent().stream().map(this::convert).toList())
                .build();
    }

    @Override
    public PageResponse<ProductResponse> searchProductsByKeyword(int page, int size, String sort, String direction, String keyword) {
        Pageable pageable = PaginationUtil.createPageable(page, size, sort, direction);

        Page<Product> productPage;
        if (keyword != null && !keyword.isEmpty()) {
            productPage = productRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        return PageResponse.<ProductResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(productPage.getTotalPages())
                .totalElements(productPage.getTotalElements())
                .content(productPage.getContent().stream().map(this::convert).toList())
                .build();
    }

    private ProductResponse convert(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .color(product.getColor())
                .size(product.getSize())
                .thumbnailUrl(product.getThumbnailUrl())
                .productImages(product.getProductImages().stream()
                        .map(this::convertProductImage)
                        .toList())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .build();
    }

    private ProductImageResponse convertProductImage(ProductImage productImage) {
        return ProductImageResponse.builder()
                .id(productImage.getId())
                .imageUrl(productImage.getImageUrl())
                .build();
    }
}
