package hcmute.edu.vn.chatbot_ec.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import hcmute.edu.vn.chatbot_ec.entity.User;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM users")
    LiveData<List<User>> getAllUsers();

    @Query("SELECT * FROM users WHERE id = :id")
    LiveData<User> getUserById(int id);

    @Query("SELECT * FROM users WHERE email = :email")
    LiveData<User> getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    LiveData<User> login(String email, String password);

    @Query("UPDATE users SET password = :newPassword WHERE id = :userId")
    void updatePassword(int userId, String newPassword);

    @Query("SELECT EXISTS(SELECT * FROM users WHERE email = :email)")
    boolean isEmailExists(String email);

    @Query("SELECT EXISTS(SELECT * FROM users WHERE phoneNumber = :phoneNumber)")
    boolean isPhoneNumberExists(String phoneNumber);
}
