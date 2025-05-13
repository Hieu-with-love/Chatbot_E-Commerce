package hcmute.edu.vn.chatbot_ec;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import hcmute.edu.vn.chatbot_ec.database.AppDatabase;
import hcmute.edu.vn.chatbot_ec.entity.Category;
import hcmute.edu.vn.chatbot_ec.entity.Product;

public class MainActivity extends AppCompatActivity {
    private TextView textViewProducts;
    private Button btnSaveProduct, btnShowProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initViews();

        btnSaveProduct.setOnClickListener(v -> {
            createProduct();
            Toast.makeText(this, "Product created", Toast.LENGTH_SHORT).show();
        });

        btnShowProducts.setOnClickListener(v -> {
            new Thread(() -> {
                List<Category> categories = AppDatabase.getDatabase(getApplicationContext())
                        .categoryDao()
                        .getAllCategoriesList();
                
                StringBuilder categoryData = new StringBuilder();
                for (Category category : categories) {
                    Log.d("Category", category.getId() + " - " + category.getName() + " - " + category.getCode());
                    categoryData.append("ID: ").append(category.getId())
                               .append(" | Name: ").append(category.getName())
                               .append(" | Code: ").append(category.getCode())
                               .append("\n\n");
                }
                
                runOnUiThread(() -> {
                    textViewProducts.setText(categoryData.toString());
                    Toast.makeText(MainActivity.this, "Found " + categories.size() + " categories", Toast.LENGTH_SHORT).show();
                });
            }).start();
        });

    }

    private void createProduct() {
        new Thread(() -> {
            Category c = new Category();
            c.setName("Áo sơ mi");
            c.setCode("asm01");

            long categoryId = AppDatabase.getDatabase(getApplicationContext())
                    .categoryDao()
                    .insert(c);
            
            Log.d("MainActivity", "Created category with ID: " + categoryId);
            
            // Add another category for testing
            Category c2 = new Category();
            c2.setName("Quần jeans");
            c2.setCode("qj01");
            
            long categoryId2 = AppDatabase.getDatabase(getApplicationContext())
                    .categoryDao()
                    .insert(c2);
                    
            Log.d("MainActivity", "Created category with ID: " + categoryId2);
        }).start();
    }

    private void initViews(){
        textViewProducts = findViewById(R.id.tv_products);
        btnSaveProduct = findViewById(R.id.btn_saveProduct);
        btnShowProducts = findViewById(R.id.btn_showProducts);
    }
}