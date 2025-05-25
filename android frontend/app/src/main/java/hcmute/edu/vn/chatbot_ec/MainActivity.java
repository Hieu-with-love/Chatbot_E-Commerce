package hcmute.edu.vn.chatbot_ec;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import hcmute.edu.vn.chatbot_ec.activity.ChatGeminiActivity;
import hcmute.edu.vn.chatbot_ec.fragments.CartFragment;
import hcmute.edu.vn.chatbot_ec.fragments.ChatbotFragment;
import hcmute.edu.vn.chatbot_ec.fragments.HomeFragment;
import hcmute.edu.vn.chatbot_ec.fragments.UserFragment;
import hcmute.edu.vn.chatbot_ec.network.ApiClient;
import hcmute.edu.vn.chatbot_ec.network.AuthApiService;
import hcmute.edu.vn.chatbot_ec.request.RegisterRequest;
import hcmute.edu.vn.chatbot_ec.response.ResponseData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private TextView textViewProducts;
    private Button btnSaveProduct, btnShowProducts;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (id == R.id.nav_cart) {
                    selectedFragment = new CartFragment();
                } else if (id == R.id.nav_chat) {
                    Intent chatIntent = new Intent(MainActivity.this, ChatGeminiActivity.class);
                    startActivity(chatIntent);
                    return false; // Return false since we're starting an activity, not loading a fragment
                } else if (id == R.id.nav_user) {
                    selectedFragment = new UserFragment();
                }
                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            }
        });

        // Default to CartFragment if no saved state
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
            loadFragment(new HomeFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void register() {
        RegisterRequest request = new RegisterRequest(
                "abc@example.com",
                "Nguyen Van A",
                "0123456789",
                "mypassword",
                "USER"
        );

        AuthApiService authApiService = ApiClient.getAuthApiService();
        authApiService.register(request).enqueue(
                new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        Toast.makeText(MainActivity.this, "Register success: " + response.body().getData(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Register failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Register failed", t.getMessage());
                    }
                }
        );
    }
}