package android.hcmute.edu.vn.chatbot_spring.service.impl;

import android.hcmute.edu.vn.chatbot_spring.dto.response.PageResponse;
import android.hcmute.edu.vn.chatbot_spring.dto.response.ProductImageResponse;
import android.hcmute.edu.vn.chatbot_spring.dto.response.ProductResponse;
import android.hcmute.edu.vn.chatbot_spring.model.Product;
import android.hcmute.edu.vn.chatbot_spring.model.ProductImage;
import android.hcmute.edu.vn.chatbot_spring.repository.ProductRepository;
import android.hcmute.edu.vn.chatbot_spring.service.ProductService;
import android.hcmute.edu.vn.chatbot_spring.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public PageResponse<ProductResponse> getAllProducts(int page, int size, String sort, String direction) {
        log.info("Fetching all branches with pagination: page={}, size={}, sort={}, direction={}", page, size, sort, direction);
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

    private ProductResponse convert(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
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
