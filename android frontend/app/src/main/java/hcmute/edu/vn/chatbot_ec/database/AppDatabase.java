package hcmute.edu.vn.chatbot_ec.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hcmute.edu.vn.chatbot_ec.dao.CartDao;
import hcmute.edu.vn.chatbot_ec.dao.CartItemDao;
import hcmute.edu.vn.chatbot_ec.dao.CategoryDao;
import hcmute.edu.vn.chatbot_ec.dao.OrderDao;
import hcmute.edu.vn.chatbot_ec.dao.OrderDetailsDao;
import hcmute.edu.vn.chatbot_ec.dao.ProductDao;
import hcmute.edu.vn.chatbot_ec.dao.UserDao;
import hcmute.edu.vn.chatbot_ec.entity.Cart;
import hcmute.edu.vn.chatbot_ec.entity.CartItem;
import hcmute.edu.vn.chatbot_ec.entity.Category;
import hcmute.edu.vn.chatbot_ec.entity.Order;
import hcmute.edu.vn.chatbot_ec.entity.OrderDetails;
import hcmute.edu.vn.chatbot_ec.entity.Product;
import hcmute.edu.vn.chatbot_ec.entity.User;
import hcmute.edu.vn.chatbot_ec.util.DateConverter;

@Database(entities = {
        User.class,
        Category.class,
        Product.class,
        Cart.class,
        CartItem.class,
        Order.class,
        OrderDetails.class
}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    // Define DAOs
    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();
    public abstract ProductDao productDao();
    public abstract CartDao cartDao();
    public abstract CartItemDao cartItemDao();
    public abstract OrderDao orderDao();
    public abstract OrderDetailsDao orderDetailsDao();

    // Singleton pattern
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "ecommerce_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Callback for database initialization
    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // If you want to populate the database when it's first created
            databaseWriteExecutor.execute(() -> {
                // Prepopulate with some data if needed
                // Example: Insert some default categories
            });
        }
    };
}
