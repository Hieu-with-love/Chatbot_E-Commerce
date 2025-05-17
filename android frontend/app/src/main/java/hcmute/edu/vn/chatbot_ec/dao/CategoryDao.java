package hcmute.edu.vn.chatbot_ec.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import hcmute.edu.vn.chatbot_ec.entity.Category;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Category category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Category> categories);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("DELETE FROM categories")
    void deleteAll();    @Query("SELECT * FROM categories ORDER BY name")
    LiveData<List<Category>> getAllCategories();
    
    @Query("SELECT * FROM categories ORDER BY name")
    List<Category> getAllCategoriesList();

    @Query("SELECT * FROM categories WHERE id = :id")
    LiveData<Category> getCategoryById(int id);

    @Query("SELECT * FROM categories WHERE code = :code")
    LiveData<Category> getCategoryByCode(String code);
}
