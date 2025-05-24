package hcmute.edu.vn.chatbot_ec.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.fragments.CartFragment;
import hcmute.edu.vn.chatbot_ec.fragments.ChatbotFragment;
import hcmute.edu.vn.chatbot_ec.fragments.HomeFragment;
import hcmute.edu.vn.chatbot_ec.fragments.UserFragment;

public class CartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Thiết lập Toolbar
        setSupportActionBar(findViewById(R.id.toolbar));

        // Thiết lập Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment(); // Tạo Fragment Home
            } else if (itemId == R.id.nav_cart) {
                selectedFragment = new CartFragment();
            } else if (itemId == R.id.nav_chat) {
                selectedFragment = new ChatbotFragment(); // Tạo Fragment Chat
            } else if (itemId == R.id.nav_user) {
                selectedFragment = new UserFragment(); // Tạo Fragment User
            }

            if (selectedFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, selectedFragment);
                transaction.commit();
            }
            return true;
        });

        // Mặc định hiển thị CartFragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_cart);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new CartFragment());
            transaction.commit();
        }
    }
}