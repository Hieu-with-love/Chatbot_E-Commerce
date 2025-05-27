package hcmute.edu.vn.chatbot_ec;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import hcmute.edu.vn.chatbot_ec.utils.AuthUtils;
import hcmute.edu.vn.chatbot_ec.service.AuthenticationService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    
    private TextView textViewProducts;
    private Button btnSaveProduct, btnShowProducts;
    private BottomNavigationView bottomNavigationView;
    private boolean isUserAuthenticated = false;
    
    // Broadcast receiver for authentication state changes
    private BroadcastReceiver authStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("ACTION_USER_LOGOUT".equals(intent.getAction())) {
                handleLogoutBroadcast();
            } else if ("ACTION_USER_LOGIN".equals(intent.getAction())) {
                handleLoginBroadcast();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Check current authentication state using enhanced AuthUtils
        checkAuthenticationState();
        Log.d(TAG, "User authenticated: " + isUserAuthenticated);

        // Start authentication service for background token monitoring
        AuthenticationService.startService(this);

        bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {                Fragment selectedFragment = null;
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    selectedFragment = createHomeFragmentWithAuthState();
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

        // Default to HomeFragment if no saved state, passing authentication state
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
            loadFragment(createHomeFragmentWithAuthState());
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
    
    /**
     * Create HomeFragment with authentication state
     */
    private Fragment createHomeFragmentWithAuthState() {
        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("user_authenticated", isUserAuthenticated);
        homeFragment.setArguments(bundle);
        return homeFragment;
    }
      @Override
    protected void onStart() {
        super.onStart();
        // Register for authentication state broadcasts
        IntentFilter filter = new IntentFilter();
        filter.addAction("ACTION_USER_LOGOUT");
        filter.addAction("ACTION_USER_LOGIN");
        ContextCompat.registerReceiver(this, authStateReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
        
        // Recheck authentication state when activity starts
        checkAuthenticationState();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        // Unregister broadcast receiver
        try {
            unregisterReceiver(authStateReceiver);
        } catch (IllegalArgumentException e) {
            // Receiver was not registered
        }
    }
    
    /**
     * Check and update authentication state using enhanced AuthUtils
     */
    private void checkAuthenticationState() {
        boolean wasAuthenticated = isUserAuthenticated;
        isUserAuthenticated = AuthUtils.validateAndHandleToken(this, false);
        
        Log.d(TAG, "Authentication state check - was: " + wasAuthenticated + ", now: " + isUserAuthenticated);
        
        // If authentication state changed, refresh current fragment
        if (wasAuthenticated != isUserAuthenticated) {
            refreshCurrentFragment();
        }
    }
    
    /**
     * Handle logout broadcast by updating authentication state
     */
    private void handleLogoutBroadcast() {
        Log.d(TAG, "Received logout broadcast");
        isUserAuthenticated = false;
        refreshCurrentFragment();
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Handle login broadcast by updating authentication state
     */
    private void handleLoginBroadcast() {
        Log.d(TAG, "Received login broadcast");
        checkAuthenticationState();
        refreshCurrentFragment();
        Toast.makeText(this, "Đã đăng nhập", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Refresh the current fragment with updated authentication state
     */
    private void refreshCurrentFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment != null) {
            // Get the currently selected item in bottom navigation
            int selectedItemId = bottomNavigationView.getSelectedItemId();
            
            // Recreate and load the appropriate fragment
            Fragment newFragment = null;
            if (selectedItemId == R.id.nav_home) {
                newFragment = createHomeFragmentWithAuthState();
            } else if (selectedItemId == R.id.nav_cart) {
                newFragment = new CartFragment();
            } else if (selectedItemId == R.id.nav_user) {
                newFragment = new UserFragment();
            }
            
            if (newFragment != null) {
                loadFragment(newFragment);
            }
        }
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