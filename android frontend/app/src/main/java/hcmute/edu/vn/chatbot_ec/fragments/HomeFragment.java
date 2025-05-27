package hcmute.edu.vn.chatbot_ec.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.activity.Login;
import hcmute.edu.vn.chatbot_ec.adapter.ProductAdapter;
import hcmute.edu.vn.chatbot_ec.model.Product;
import hcmute.edu.vn.chatbot_ec.model.ProductImage;
import hcmute.edu.vn.chatbot_ec.network.ApiClient;
import hcmute.edu.vn.chatbot_ec.network.UserApiService;
import hcmute.edu.vn.chatbot_ec.response.UserDetailResponse;
import hcmute.edu.vn.chatbot_ec.service.AuthenticationService;
import hcmute.edu.vn.chatbot_ec.utils.AuthUtils;
import hcmute.edu.vn.chatbot_ec.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    
    RecyclerView rvProducts;
    private ImageView imgUserAvatar;
    private TextView tvGreeting;
    private TextView tvFullName;
    private Button btnLogin;
    private boolean isUserAuthenticated = false;
    
    // Broadcast receiver for logout events
    private BroadcastReceiver logoutReceiver = new BroadcastReceiver() {        @Override
        public void onReceive(Context context, Intent intent) {
            if (AuthenticationService.ACTION_USER_LOGOUT.equals(intent.getAction())) {
                Log.d(TAG, "Logout broadcast received");
                handleLogoutBroadcast();
            }
        }
    };

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        initViews(view);
        
        // Get authentication state from arguments
        if (getArguments() != null) {
            isUserAuthenticated = getArguments().getBoolean("user_authenticated", false);
        }
        
        setupTopBar();
        setupProductList(view);
        
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getContext() != null) {
            IntentFilter filter = new IntentFilter(AuthenticationService.ACTION_USER_LOGOUT);
            ContextCompat.registerReceiver(getContext(), logoutReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
        }
    }
    
    @Override
    public void onStop() {
        super.onStop();
        // Unregister broadcast receiver
        if (getContext() != null) {
            try {
                getContext().unregisterReceiver(logoutReceiver);
            } catch (IllegalArgumentException e) {
                // Receiver was not registered
                Log.d(TAG, "Receiver was not registered");
            }
        }
    }
    
    private void initViews(View view) {
        rvProducts = view.findViewById(R.id.rv_products);
        imgUserAvatar = view.findViewById(R.id.img_user_avatar);
        tvGreeting = view.findViewById(R.id.tv_greeting);
        tvFullName = view.findViewById(R.id.tv_full_name);
        btnLogin = view.findViewById(R.id.btn_login);
        
        // Setup login button click listener
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
        });
    }

    private void setupTopBar() {
        if (getContext() == null) {
            return;
        }
        
        // Validate token and handle expiration
        if (!AuthUtils.validateAndHandleToken(getContext(), false)) {
            Log.d(TAG, "No valid token found, showing guest mode");
            showGuestMode();
            return;
        }
        
        Log.d(TAG, "Valid token found, loading user profile");
        loadUserProfile();
        
        // Check if token is expiring soon and show warning
        if (AuthUtils.isTokenExpiringSoon(getContext())) {
            Log.w(TAG, "Token expiring soon - user should refresh session");
        }
    }    private void loadUserProfile() {
        // Get user info directly from SessionManager
        SessionManager.UserInfo userInfo = SessionManager.getUserInfo(getContext());
        
        if (userInfo != null && userInfo.fullName != null && !userInfo.fullName.trim().isEmpty()) {
            Log.d(TAG, "Displaying user info from SessionManager: " + userInfo.toString());
            displayUserInfoFromSession(userInfo.fullName, userInfo.role);
            return;
        }

        // If no user info in session, user might not be logged in
        Log.w(TAG, "No user info found in session, switching to guest mode");
        showGuestMode();
    }

    private void fetchUserDetails(Integer userId) {
        if (userId == null || getContext() == null) {
            showGuestMode();
            return;
        }

        UserApiService userApiService = ApiClient.getUserApiService();
        userApiService.getUserById(userId).enqueue(new Callback<UserDetailResponse>() {
            @Override
            public void onResponse(Call<UserDetailResponse> call, Response<UserDetailResponse> response) {
                if (getActivity() == null) return; // Fragment might be detached
                
                if (response.isSuccessful() && response.body() != null) {
                    UserDetailResponse userDetail = response.body();
                    displayUserInfo(userDetail);
                } else if (response.code() == 401) {
                    // Handle unauthorized response
                    Log.w(TAG, "Unauthorized response - token may be expired");
                    AuthUtils.handleUnauthorizedResponse(getContext());                } else {
                    Log.w(TAG, "Failed to load user details: " + response.code());
                    // If we have SessionManager data, fallback to displaying that
                    SessionManager.UserInfo userInfo = SessionManager.getUserInfo(getContext());
                    if (userInfo != null && userInfo.fullName != null) {
                        displayUserInfoFromSession(userInfo.fullName, userInfo.role);
                    } else {
                        showGuestMode();
                    }
                }
            }            @Override
            public void onFailure(Call<UserDetailResponse> call, Throwable t) {
                Log.e(TAG, "Network error loading user details", t);
                if (getActivity() != null) {
                    // Fallback to SessionManager data if available
                    SessionManager.UserInfo userInfo = SessionManager.getUserInfo(getContext());
                    if (userInfo != null && userInfo.fullName != null) {
                        displayUserInfoFromSession(userInfo.fullName, userInfo.role);
                    } else {
                        showGuestMode();
                    }
                }
            }
        });
    }


    private void displayUserInfo(UserDetailResponse user) {
        Log.d(TAG, "Displaying user info for: " + user.getFullName());
        
        // Show user avatar and name, hide login button
        imgUserAvatar.setVisibility(View.VISIBLE);
        tvGreeting.setText("Chào bạn,");
        tvFullName.setText(user.getFullName());
        tvFullName.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.GONE);
        
        // TODO: Load user avatar from URL if available
        // For now, keep the default placeholder
    }      /**
     * Display user information from SessionManager
     * @param fullName User's full name from SessionManager
     * @param role User's role from SessionManager (optional)
     */
    private void displayUserInfoFromSession(String fullName, String role) {
        Log.d(TAG, "Displaying user info from SessionManager for: " + fullName + ", Role: " + role);
        
        // Show user avatar and name, hide login button
        imgUserAvatar.setVisibility(View.VISIBLE);
        
        // Customize greeting based on role
        String greeting = "Chào bạn,";
        if (role != null && !role.trim().isEmpty()) {
            if (role.equalsIgnoreCase("ADMIN")) {
                greeting = "Chào Admin,";
            } else if (role.equalsIgnoreCase("MANAGER")) {
                greeting = "Chào Quản lý,";
            } else if (role.equalsIgnoreCase("VIP")) {
                greeting = "Chào thành viên VIP,";
            }
        }
        
        tvGreeting.setText(greeting);
        tvFullName.setText(fullName);
        tvFullName.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.GONE);
        
        // Use default avatar placeholder for session-based display
        // Can be enhanced later to load avatar from additional API call if needed
    }
    
    private void showGuestMode() {
        Log.d(TAG, "Showing guest mode UI");
        
        // Hide user avatar and name, show login button
        imgUserAvatar.setVisibility(View.VISIBLE); // Keep avatar placeholder
        tvGreeting.setText("Chào khách,");
        tvFullName.setVisibility(View.GONE);
        btnLogin.setVisibility(View.VISIBLE);
    }
    private void setupProductList(View view) {
        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));

        //Test data
        List<Product> products = new ArrayList<>();
        products.add(new Product(1, "Wireless Headphones", "Audio", new BigDecimal("99.99"), null, "Clothes"));
        products.add(new Product(2, "Smart Watch", "Wearables", new BigDecimal("129.99"), null, "General"));
        products.add(new Product(3, "Bluetooth Speaker", "Audio", new BigDecimal("49.99"), null, "Electronics"));
        products.add(new Product(4, "Running Shoes", "Footwear", new BigDecimal("89.99"), null, "Clothes"));
        products.add(new Product(5, "Laptop Backpack", "Accessories", new BigDecimal("59.99"), null, "General"));
        products.add(new Product(6, "Fitness Tracker", "Wearables", new BigDecimal("79.99"), null, "General"));
        products.add(new Product(7, "LED Desk Lamp", "Lighting", new BigDecimal("39.99"), null, "Home"));
        products.add(new Product(8, "Gaming Mouse", "Peripherals", new BigDecimal("29.99"), null, "Electronics"));
        products.add(new Product(9, "Noise Cancelling Earbuds", "Audio", new BigDecimal("109.99"), null, "Electronics"));
        products.add(new Product(10, "Cotton Hoodie", "Apparel", new BigDecimal("45.99"), null, "Clothes"));

        ProductAdapter adapter = new ProductAdapter(products, product -> {
            Bundle bundle = new Bundle();
            bundle.putString("product_name", product.getName());
            bundle.putString("product_category", product.getCategoryName());
            bundle.putString("product_price", product.getPrice().toString());
            bundle.putString("product_description", product.getDescription());
            bundle.putString("product_thumbnail_url", product.getThumbnailUrl());

            // Convert List<ProductImage> to List<String>
            ArrayList<String> imageUrls = new ArrayList<>();
            for (ProductImage image : product.getProductImages()) {
                imageUrls.add(image.getUrl());
            }

            bundle.putStringArrayList("product_images", imageUrls);
            bundle.putString("product_id", String.valueOf(product.getId()));


            ProductDetailFragment detailFragment = new ProductDetailFragment();
            detailFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null) // Để có thể quay lại
                    .commit();
        });
        rvProducts.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh authentication state when fragment becomes visible
        // This handles cases where user logs in/out from other activities
        setupTopBar();
    }

    /**
     * Public method to refresh the authentication state
     * Can be called from parent activity when authentication state changes
     */
    public void refreshAuthenticationState() {
        setupTopBar();
    }    /**
     * Debug method to log SessionManager information
     * Useful for development and debugging
     */
    private void debugSessionInfo() {
        if (getContext() == null) {
            Log.d(TAG, "Session Debug: Context is null");
            return;
        }
        
        Log.d(TAG, "Session Debug: Has active session: " + SessionManager.hasActiveSession(getContext()));
        Log.d(TAG, "Session Debug: Is token expired: " + SessionManager.isTokenExpired(getContext()));
        
        SessionManager.UserInfo userInfo = SessionManager.getUserInfo(getContext());
        if (userInfo != null) {
            Log.d(TAG, "Session Debug: Email: " + userInfo.email);
            Log.d(TAG, "Session Debug: Full Name: " + userInfo.fullName);
            Log.d(TAG, "Session Debug: Role: " + userInfo.role);
        }
        
        long timeLeft = SessionManager.getTokenTimeRemaining(getContext());
        Log.d(TAG, "Session Debug: Time left (seconds): " + timeLeft);
    }
      /**
     * Enhanced logout method with broadcast support
     * Clears the session and switches to guest mode
     */
    public void logout() {
        if (getContext() != null) {
            AuthUtils.logoutWithBroadcast(getContext(), false, null); // Don't show toast or navigate
            Log.d(TAG, "User logged out, session cleared");
            showGuestMode();
        }
    }
    
    /**
     * Handle logout from broadcast receiver
     */
    public void handleLogoutBroadcast() {
        Log.d(TAG, "Logout broadcast received, switching to guest mode");
        showGuestMode();
    }
    
    /**
     * Method to check if user is currently authenticated
     * @return true if user has valid token, false otherwise
     */
    public boolean isUserAuthenticated() {
        return AuthUtils.isUserAuthenticated(getContext());
    }
}