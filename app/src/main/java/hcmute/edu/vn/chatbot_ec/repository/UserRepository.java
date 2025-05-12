package hcmute.edu.vn.chatbot_ec.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import hcmute.edu.vn.chatbot_ec.dao.UserDao;
import hcmute.edu.vn.chatbot_ec.database.AppDatabase;
import hcmute.edu.vn.chatbot_ec.entity.User;

public class UserRepository {
    private final UserDao userDao;
    private final LiveData<List<User>> allUsers;

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
        allUsers = userDao.getAllUsers();
    }

    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    public LiveData<User> getUserById(int id) {
        return userDao.getUserById(id);
    }

    public LiveData<User> getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    public LiveData<User> login(String email, String password) {
        return userDao.login(email, password);
    }

    public void insert(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.insert(user));
    }

    public void update(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.update(user));
    }

    public void delete(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.delete(user));
    }

    public void updatePassword(int userId, String newPassword) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.updatePassword(userId, newPassword));
    }

    public boolean isEmailExists(String email) {
        // This should ideally be done asynchronously, but for simplicity
        // Note: Room doesn't allow database access on the main thread
        // You may want to refactor this to use LiveData or CompletableFuture
        try {
            return AppDatabase.databaseWriteExecutor.submit(() -> userDao.isEmailExists(email)).get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isPhoneNumberExists(String phoneNumber) {
        try {
            return AppDatabase.databaseWriteExecutor.submit(() -> userDao.isPhoneNumberExists(phoneNumber)).get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
