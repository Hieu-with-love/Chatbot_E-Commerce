package hcmute.edu.vn.chatbot_ec.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import hcmute.edu.vn.chatbot_ec.dao.ProductDao;
import hcmute.edu.vn.chatbot_ec.database.AppDatabase;
import hcmute.edu.vn.chatbot_ec.entity.Product;

public class ProductRepository {
    private final ProductDao productDao;
    private final LiveData<List<Product>> allProducts;

    public ProductRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        productDao = db.productDao();
        allProducts = productDao.getAllProducts();
    }

    public LiveData<List<Product>> getAllProducts() {
        return allProducts;
    }

    public LiveData<Product> getProductById(int id) {
        return productDao.getProductById(id);
    }

    public LiveData<List<Product>> getProductsByCategory(int categoryId) {
        return productDao.getProductsByCategory(categoryId);
    }

    public LiveData<List<Product>> searchProducts(String query) {
        return productDao.searchProducts(query);
    }

    public LiveData<List<Product>> getProductsSortedByPriceAsc() {
        return productDao.getProductsSortedByPriceAsc();
    }

    public LiveData<List<Product>> getProductsSortedByPriceDesc() {
        return productDao.getProductsSortedByPriceDesc();
    }

    public void insert(Product product) {
        AppDatabase.databaseWriteExecutor.execute(() -> productDao.insert(product));
    }

    public void update(Product product) {
        AppDatabase.databaseWriteExecutor.execute(() -> productDao.update(product));
    }

    public void delete(Product product) {
        AppDatabase.databaseWriteExecutor.execute(() -> productDao.delete(product));
    }

    public void insertAll(List<Product> products) {
        AppDatabase.databaseWriteExecutor.execute(() -> productDao.insertAll(products));
    }

    public void deleteAll() {
        AppDatabase.databaseWriteExecutor.execute(() -> productDao.deleteAll());
    }
}
