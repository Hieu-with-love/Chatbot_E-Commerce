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
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.activity.ProductDetailActivity;
import hcmute.edu.vn.chatbot_ec.activity.Login;
import hcmute.edu.vn.chatbot_ec.adapter.ProductAdapter;

import hcmute.edu.vn.chatbot_ec.network.ApiClient;
import hcmute.edu.vn.chatbot_ec.network.UserApiService;
import hcmute.edu.vn.chatbot_ec.response.UserDetailResponse;
import hcmute.edu.vn.chatbot_ec.service.AuthenticationService;
import hcmute.edu.vn.chatbot_ec.utils.AuthUtils;
import hcmute.edu.vn.chatbot_ec.utils.SessionManager;
import hcmute.edu.vn.chatbot_ec.utils.JwtUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import hcmute.edu.vn.chatbot_ec.network.CartApiService;
import hcmute.edu.vn.chatbot_ec.network.ProductApiService;
import hcmute.edu.vn.chatbot_ec.request.AddCartItemRequest;
import hcmute.edu.vn.chatbot_ec.response.CartResponse;
import hcmute.edu.vn.chatbot_ec.response.PageResponse;
import hcmute.edu.vn.chatbot_ec.response.ProductImageResponse;
import hcmute.edu.vn.chatbot_ec.response.ProductResponse;
import hcmute.edu.vn.chatbot_ec.utils.JwtUtils;

public class HomeFragment extends Fragment {
    private RecyclerView rvProducts;
    private ProgressBar progressBar;
    private TextView tvEmptyState, tvGreeting, tvFullName;
    private ImageView imgCart, imgUserAvatar;
    private SearchView searchView;
    private ProductAdapter adapter;
    private List<ProductResponse> products = new ArrayList<>();
    private ProductApiService productApiService;
    private CartApiService cartApiService;
    private MaterialCardView headerCard;
    private Integer userId;
    private int currentPage = 1;
    private int pageSize = 10;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String currentQuery = "";
    private static final long DEBOUNCE_DELAY_MS = 300;
    private static final long SEARCH_DEBOUNCE_DELAY_MS = 500;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable loadMoreRunnable;
    private Runnable searchRunnable;
    private Button btnLogin;
    private boolean isUserAuthenticated = false;

    private static String TAG = "HomeFragment";
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

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        headerCard = view.findViewById(R.id.header_card);
        
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
        
        // Debug authentication state (remove in production)
        debugSessionInfo();
        debugJWTTokenInfo();
        
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
  }

    }private void loadUserProfile() {
        // Get user info directly from SessionManager first
        SessionManager.UserInfo userInfo = SessionManager.getUserInfo(getContext());
        
        if (userInfo != null && userInfo.fullName != null && !userInfo.fullName.trim().isEmpty()) {
            Log.d(TAG, "Displaying user info from SessionManager: " + userInfo.toString());
            displayUserInfoFromSession(userInfo.fullName, userInfo.role);
            return;
        }

        // Fallback: Try to get user info from JWT token
        String token = TokenManager.getToken(getContext());
        if (token != null && !JwtUtils.isTokenExpired(token)) {
            String fullNameFromJWT = JwtUtils.getFullNameFromToken(token);
            if (fullNameFromJWT != null && !fullNameFromJWT.trim().isEmpty()) {
                Log.d(TAG, "Displaying user info from JWT token");
                displayUserInfoFromJWT(token);
                return;
            }
        }

        // If no user info in session or JWT, user might not be logged in
        Log.w(TAG, "No user info found in session or JWT, switching to guest mode");
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
    }    /**
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
    
    /**
     * Display user information extracted from JWT token
     * @param token JWT token containing user information
     */
    private void displayUserInfoFromJWT(String token) {
        if (token == null || JwtUtils.isTokenExpired(token)) {
            Log.w(TAG, "Cannot display user info: token is null or expired");
            showGuestMode();
            return;
        }
        
        String fullName = JwtUtils.getFullNameFromToken(token);
        String role = JwtUtils.getRoleFromToken(token);
        String userId = JwtUtils.getUserIdFromToken(token);
        
        Log.d(TAG, "Displaying user info from JWT for: " + fullName + ", Role: " + role + ", ID: " + userId);
        
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
        tvFullName.setText(fullName != null ? fullName : "User");
        tvFullName.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.GONE);
        
        // Set userId for cart functionality
        if (userId != null) {
            try {
                this.userId = Integer.parseInt(userId);
            } catch (NumberFormatException e) {
                Log.w(TAG, "Cannot parse user ID from JWT: " + userId);
            }
        }
        
        // Use default avatar placeholder for JWT-based display
        // Can be enhanced later to load avatar from additional API call if needed
    }
    
    private void showGuestMode() {
        Log.d(TAG, "Showing guest mode UI");

        headerCard.setVisibility(View.GONE);
        btnLogin.setVisibility(View.VISIBLE);
    }
    private void setupProductList(View view) {


        // Initialize UI components
        rvProducts = view.findViewById(R.id.rv_products);
        progressBar = view.findViewById(R.id.progress_bar);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
        tvGreeting = view.findViewById(R.id.tv_greeting);
        tvFullName = view.findViewById(R.id.tv_full_name);
        imgCart = view.findViewById(R.id.img_cart);
        imgUserAvatar = view.findViewById(R.id.img_user_avatar);
        searchView = view.findViewById(R.id.search_view);
        productApiService = ApiClient.getProductApiService();
        cartApiService = ApiClient.getCartApiService();

        // Set up RecyclerView with GridLayoutManager

        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ProductAdapter(products, new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(ProductResponse product) {
                Intent intent = new Intent(getContext(), ProductDetailActivity.class);
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            }

            @Override
            public void onAddToCartClick(ProductResponse product) {
                if (userId == null) {
                    Toast.makeText(getContext(), R.string.login_required, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (product.getStock() == null || product.getStock() <= 0) {
                    Toast.makeText(getContext(), "Sản phẩm đã hết hàng", Toast.LENGTH_SHORT).show();
                    return;
                }
                addToCart(product.getId(), 1);
            }
        });
        rvProducts.setAdapter(adapter);

        // Handle cart icon click
        imgCart.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new CartFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Initialize runnables for debouncing
        loadMoreRunnable = () -> {
            if (!isLoading && !isLastPage) {
                fetchProducts(currentPage + 1, pageSize, "id", "asc", currentQuery);
            }
        };
        searchRunnable = () -> {
            if (isAdded() && getContext() != null) {
                currentPage = 1; // Reset pagination for new search
                isLastPage = false;
                fetchProducts(currentPage, pageSize, "id", "asc", currentQuery);
            }
        };

        // Handle pagination with debounce
        rvProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // Only load when scrolling down
                    GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !isLastPage && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 4) {
                        handler.removeCallbacks(loadMoreRunnable);
                        handler.postDelayed(loadMoreRunnable, DEBOUNCE_DELAY_MS);
                    }
                }
            }
        });

        // Handle search with debounce
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentQuery = query.trim();
                searchHandler.removeCallbacks(searchRunnable);
                searchHandler.post(searchRunnable); // Immediate search on submit
                return true;
            }            

@Override
            public boolean onQueryTextChange(String newText) {
                currentQuery = newText.trim();
                searchHandler.removeCallbacks(searchRunnable);
                
                // If search is cleared, reset pagination and fetch all products
                if (currentQuery.isEmpty()) {
                    currentPage = 1;
                    isLastPage = false;
                    searchHandler.post(() -> {
                        if (isAdded() && getContext() != null) {
                            fetchProducts(currentPage, pageSize, "id", "asc", "");
                        }
                    });
                } else {
                    searchHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY_MS);
                }                return true;
            }});        // Get userId from JWT token for cart operations
        SessionManager.UserInfo userInfo = SessionManager.getUserInfo(getContext());
        if (userInfo != null && userInfo.token != null) {
            String userIdStr = JwtUtils.getUserIdFromToken(userInfo.token);
            if (userIdStr != null && !userIdStr.trim().isEmpty()) {
                try {
                    userId = Integer.parseInt(userIdStr);
                } catch (NumberFormatException e) {
                    Log.w(TAG, "Could not parse userId from token: " + userIdStr, e);
                    userId = null;
                }
            }
        }
        
        // Start loading products
        fetchProducts(currentPage, pageSize, "id", "asc", "");
                searchHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY_MS);
                return true;
            }        });
        rvProducts.setAdapter(adapter);
        
        // Fetch user information for cart functionality
        loadUserInfoForCart();
        
        // Load initial products
        fetchProducts(currentPage, pageSize, "id", "asc", "");
    }

    /**
     * Load user information specifically for cart functionality and user ID
     * This works alongside the top bar authentication but focuses on getting userId
     */
    private void loadUserInfoForCart() {
        // Try to get user ID from JWT token first
        String token = TokenManager.getToken(getContext());
        if (token != null && !JwtUtils.isTokenExpired(token)) {
            String userIdFromToken = JwtUtils.getUserIdFromToken(token);
            if (userIdFromToken != null) {
                try {
                    userId = Integer.parseInt(userIdFromToken);
                    Log.d(TAG, "User ID loaded from JWT: " + userId);
                    return;
                } catch (NumberFormatException e) {
                    Log.w(TAG, "Cannot parse user ID from JWT: " + userIdFromToken);
                }
            }
        }
        
        // Fallback: Fetch user info from API if JWT doesn't have user ID or is invalid
        if (token != null) {
            UserApiService userApiService = ApiClient.getUserApiService();
            userApiService.getMe(token).enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (isAdded() && getContext() != null) {
                        if (response.isSuccessful() && response.body() != null) {
                            userId = response.body().getUserId();
                            Log.d(TAG, "User ID loaded from API: " + userId);
                            
                            // Update avatar if not already set by top bar logic
                            String avatarUrl = response.body().getAvatarUrl();
                            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                Glide.with(HomeFragment.this)
                                        .load(avatarUrl)
                                        .placeholder(R.drawable.ic_user_placeholder)
                                        .error(R.drawable.ic_user_placeholder)
                                        .circleCrop()
                                        .into(imgUserAvatar);
                            }
                        } else if (response.code() == 401) {
                            Log.w(TAG, "Token expired while fetching user info");
                            AuthUtils.handleUnauthorizedResponse(getContext());
                        } else {
                            Log.w(TAG, "Failed to load user info from API: " + response.code());
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    if (isAdded() && getContext() != null) {
                        Log.e(TAG, "Network error while fetching user info", t);
                    }
                }
            });
        } else {
            Log.d(TAG, "No token available for user info fetching");
        }
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
     * Debug method to log JWT token information
     * Useful for development and debugging JWT integration
     */
    private void debugJWTTokenInfo() {
        String token = TokenManager.getToken(getContext());
        if (token == null) {
            Log.d(TAG, "JWT Debug: No token found");
            return;
        }
        
        Log.d(TAG, "JWT Debug: Token exists: true");
        Log.d(TAG, "JWT Debug: Token expired: " + JwtUtils.isTokenExpired(token));
        Log.d(TAG, "JWT Debug: Valid format: " + JwtUtils.isValidTokenFormat(token));
        
        if (!JwtUtils.isTokenExpired(token)) {
            Log.d(TAG, "JWT Debug: User ID: " + JwtUtils.getUserIdFromToken(token));
            Log.d(TAG, "JWT Debug: Email: " + JwtUtils.getEmailFromToken(token));
            Log.d(TAG, "JWT Debug: Full Name: " + JwtUtils.getFullNameFromToken(token));
            Log.d(TAG, "JWT Debug: Role: " + JwtUtils.getRoleFromToken(token));
            Log.d(TAG, "JWT Debug: Expiration: " + JwtUtils.getExpirationFromToken(token));
        }
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
    
    /**
     * Public method to enable/disable debug logging
     * Useful for development and testing
     */
    public void enableDebugMode(boolean enable) {
        if (enable) {
            debugSessionInfo();
            debugJWTTokenInfo();
        }
    }

    private void fetchProducts(int page, int size, String sort, String direction, String query) {
        if (isLoading) return;
        isLoading = true;
        if (page == 1) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            adapter.setLoadingFooter(true);
        }
        tvEmptyState.setVisibility(View.GONE);

        Call<PageResponse<ProductResponse>> call;
        if (query.isEmpty()) {
            call = productApiService.getAllProducts(page, size, sort, direction);
        } else {
            call = productApiService.searchProducts(query, page, size, sort, direction, query);
        }

        call.enqueue(new Callback<PageResponse<ProductResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<ProductResponse>> call, Response<PageResponse<ProductResponse>> response) {
                if (!isAdded() || getContext() == null) return;

                isLoading = false;
                progressBar.setVisibility(View.GONE);
                adapter.setLoadingFooter(false);

                if (response.isSuccessful() && response.body() != null) {
                    PageResponse<ProductResponse> pageResponse = response.body();
                    if (page == 1) {
                        products.clear();
                    }
                    products.addAll(pageResponse.getContent());
                    adapter.notifyDataSetChanged();
                    currentPage = pageResponse.getCurrentPage();
                    isLastPage = currentPage >= pageResponse.getTotalPages();

                    tvEmptyState.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(getContext(), R.string.fetch_products_failed, Toast.LENGTH_SHORT).show();
                    tvEmptyState.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
                    showRetryDialog(page, size, sort, direction, query);
                }
            }

            @Override
            public void onFailure(Call<PageResponse<ProductResponse>> call, Throwable t) {
                if (!isAdded() || getContext() == null) return;

                isLoading = false;
                progressBar.setVisibility(View.GONE);
                adapter.setLoadingFooter(false);
                Toast.makeText(getContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                tvEmptyState.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
                showRetryDialog(page, size, sort, direction, query);
            }
        });
    }

    private void showRetryDialog(int page, int size, String sort, String direction, String query) {
        new AlertDialog.Builder(requireContext())
                .setMessage(R.string.network_error_retry)
                .setPositiveButton(R.string.retry, (dialog, which) -> fetchProducts(page, size, sort, direction, query))
                .setNegativeButton(R.string.button_cancel, null)
                .show();
    }

    private void addToCart(int productId, int quantity) {
        if (userId == null) return;
        AddCartItemRequest request = new AddCartItemRequest(productId, quantity);
        cartApiService.addItemToCart(userId, request).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (isAdded() && getContext() != null) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(getContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Không thể thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up handlers to prevent memory leaks
        handler.removeCallbacks(loadMoreRunnable);
        searchHandler.removeCallbacks(searchRunnable);
    }
}