package hcmute.edu.vn.chatbot_ec.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.activity.Login;
import hcmute.edu.vn.chatbot_ec.adapter.OrderAdapter;
import hcmute.edu.vn.chatbot_ec.network.ApiClient;
import hcmute.edu.vn.chatbot_ec.network.OrderApiService;
import hcmute.edu.vn.chatbot_ec.network.UserApiService;
import hcmute.edu.vn.chatbot_ec.response.OrderResponse;
import hcmute.edu.vn.chatbot_ec.response.UserResponse;
import hcmute.edu.vn.chatbot_ec.service.AuthenticationService;
import hcmute.edu.vn.chatbot_ec.utils.AuthUtils;
import hcmute.edu.vn.chatbot_ec.utils.JwtUtils;
import hcmute.edu.vn.chatbot_ec.utils.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {
    private static final String TAG = "OrderActivity";
    
    private RecyclerView recyclerViewOrders;
    private MaterialTextView textEmptyOrders;
    private ProgressBar progressBar;
    private OrderAdapter orderAdapter;
    private List<OrderResponse> orders;
    private UserApiService userApiService;
    private OrderApiService orderApiService;
    private Integer userId;
    
    // Broadcast receiver for logout events
    private BroadcastReceiver logoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AuthenticationService.ACTION_USER_LOGOUT.equals(intent.getAction())) {
                Log.d(TAG, "Logout broadcast received");
                handleLogoutBroadcast();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Initialize views
        recyclerViewOrders = findViewById(R.id.recycler_view_orders);
        textEmptyOrders = findViewById(R.id.text_empty_orders);
        progressBar = findViewById(R.id.progress_bar);
        userApiService = ApiClient.getUserApiService();
        orderApiService = ApiClient.getOrderApiService();
        orders = new ArrayList<>();

        // Initialize RecyclerView
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter();
        recyclerViewOrders.setAdapter(orderAdapter);
        recyclerViewOrders.setContentDescription(getString(R.string.orders_list_description));        // Show loading state
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewOrders.setVisibility(View.GONE);
        textEmptyOrders.setVisibility(View.GONE);        // Initialize authentication and load orders
        initializeAuthentication();
    }    /**
     * Initialize authentication following JWT pattern from HomeFragment
     */
    private void initializeAuthentication() {
        // Check if user is authenticated
        if (!AuthUtils.isAuthenticated(this)) {
            Log.d(TAG, "No valid token found, redirecting to login");
            redirectToLogin();
            return;
        }
        
        Log.d(TAG, "Valid token found, extracting user ID");
        
        // Fallback: Fetch user ID from API
        fetchUserIdFromApi();
    }
    
    /**
     * Fallback method to fetch user ID from API if JWT extraction fails
     */
    private void fetchUserIdFromApi() {
        String token = TokenManager.getToken(this);
        userApiService.getMe(token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userId = response.body().getUserId();
                    Log.d(TAG, "User ID fetched from API: " + userId);
                    fetchOrders(userId);
                } else if (response.code() == 401) {
                    Log.w(TAG, "Unauthorized response - token may be expired");
                    AuthUtils.handleUnauthorizedResponse(OrderActivity.this);
                } else {
                    Log.w(TAG, "Failed to fetch user info: " + response.code());
                    showErrorState(getString(R.string.error_user_info));
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e(TAG, "Network error fetching user info", t);
                showErrorState(getString(R.string.connection_error_message, t.getMessage()));
            }
        });
    }
    
    /**
     * Redirect to login screen
     */
    private void redirectToLogin() {
        progressBar.setVisibility(View.GONE);
        textEmptyOrders.setVisibility(View.VISIBLE);
        textEmptyOrders.setText(R.string.please_login);
        Toast.makeText(this, R.string.please_login, Toast.LENGTH_SHORT).show();
        
        Intent loginIntent = new Intent(this, Login.class);
        startActivity(loginIntent);
        finish();
    }
    
    /**
     * Show error state with message
     */
    private void showErrorState(String message) {
        progressBar.setVisibility(View.GONE);
        textEmptyOrders.setVisibility(View.VISIBLE);
        textEmptyOrders.setText(message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void fetchOrders(Integer userId) {
        if (userId == null) return;
        
        orderApiService.getOrdersByUserId(userId).enqueue(new Callback<List<OrderResponse>>() {
            @Override
            public void onResponse(Call<List<OrderResponse>> call, Response<List<OrderResponse>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    orders.clear();
                    orders.addAll(response.body());
                    orderAdapter.submitList(orders);
                    updateOrderVisibility();
                } else if (response.code() == 401) {
                    Log.w(TAG, "Unauthorized response - token may be expired");
                    AuthUtils.handleUnauthorizedResponse(OrderActivity.this);
                } else {
                    showErrorState(getString(R.string.error_load_orders));
                }
            }

            @Override
            public void onFailure(Call<List<OrderResponse>> call, Throwable t) {
                Log.e(TAG, "Network error loading orders", t);
                showErrorState(getString(R.string.connection_error_message, t.getMessage()));
            }
        });
    }

    private void updateOrderVisibility() {
        if (orders.isEmpty()) {
            recyclerViewOrders.setVisibility(View.GONE);
            textEmptyOrders.setVisibility(View.VISIBLE);
            textEmptyOrders.setText(R.string.no_orders);
        } else {
            recyclerViewOrders.setVisibility(View.VISIBLE);
            textEmptyOrders.setVisibility(View.GONE);
        }
    }
    
    private void showNotLoggedInState() {
        recyclerViewOrders.setVisibility(View.GONE);
        textEmptyOrders.setVisibility(View.VISIBLE);
        textEmptyOrders.setText(R.string.please_login);
        orders.clear();
        orderAdapter.submitList(orders);
    }
      private void handleLogoutBroadcast() {
        Log.d(TAG, "Handling logout broadcast");
        progressBar.setVisibility(View.GONE);
        showNotLoggedInState();
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
    }    @Override
    protected void onStart() {
        super.onStart();
        // Register for logout broadcasts
        IntentFilter filter = new IntentFilter(AuthenticationService.ACTION_USER_LOGOUT);
        ContextCompat.registerReceiver(this, logoutReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        // Unregister broadcast receiver
        try {
            unregisterReceiver(logoutReceiver);
        } catch (IllegalArgumentException e) {
            // Receiver was not registered
            Log.d(TAG, "Receiver was not registered");
        }
    }
}