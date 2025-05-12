package hcmute.edu.vn.chatbot_ec.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import hcmute.edu.vn.chatbot_ec.dao.CategoryDao;
import hcmute.edu.vn.chatbot_ec.database.AppDatabase;
import hcmute.edu.vn.chatbot_ec.entity.Category;

public class CategoryRepository {
    private final CategoryDao categoryDao;
    private final LiveData<List<Category>> allCategories;

    public CategoryRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        categoryDao = db.categoryDao();
        allCategories = categoryDao.getAllCategories();
    }

    // Room executes all queries on a separate thread
    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public LiveData<Category> getCategoryById(int id) {
        return categoryDao.getCategoryById(id);
    }

    public LiveData<Category> getCategoryByCode(String code) {
        return categoryDao.getCategoryByCode(code);
    }

    // You must call these on a non-UI thread or your app will throw an exception
    // Room ensures that you're not doing any long-running operations on the main thread
    public void insert(Category category) {
        AppDatabase.databaseWriteExecutor.execute(() -> categoryDao.insert(category));
    }

    public void update(Category category) {
        AppDatabase.databaseWriteExecutor.execute(() -> categoryDao.update(category));
    }

    public void delete(Category category) {
        AppDatabase.databaseWriteExecutor.execute(() -> categoryDao.delete(category));
    }

    public void insertAll(List<Category> categories) {
        AppDatabase.databaseWriteExecutor.execute(() -> categoryDao.insertAll(categories));
    }

    public void deleteAll() {
        AppDatabase.databaseWriteExecutor.execute(() -> categoryDao.deleteAll());
    }
}
