package hcmute.edu.vn.chatbot_ec.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import hcmute.edu.vn.chatbot_ec.entity.Product;
import hcmute.edu.vn.chatbot_ec.repository.ProductRepository;

public class ProductViewModel extends AndroidViewModel {
    private final ProductRepository productRepository;
    private final LiveData<List<Product>> allProducts;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository(application);
        allProducts = productRepository.getAllProducts();
    }

    public LiveData<List<Product>> getAllProducts() {
        return allProducts;
    }

    public LiveData<Product> getProductById(int id) {
        return productRepository.getProductById(id);
    }

    public LiveData<List<Product>> getProductsByCategory(int categoryId) {
        return productRepository.getProductsByCategory(categoryId);
    }

    public LiveData<List<Product>> searchProducts(String query) {
        return productRepository.searchProducts(query);
    }

    public LiveData<List<Product>> getProductsSortedByPriceAsc() {
        return productRepository.getProductsSortedByPriceAsc();
    }

    public LiveData<List<Product>> getProductsSortedByPriceDesc() {
        return productRepository.getProductsSortedByPriceDesc();
    }

    public void insert(Product product) {
        productRepository.insert(product);
    }

    public void update(Product product) {
        productRepository.update(product);
    }

    public void delete(Product product) {
        productRepository.delete(product);
    }
}
