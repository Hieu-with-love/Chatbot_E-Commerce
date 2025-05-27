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
import hcmute.edu.vn.chatbot_ec.activity.Login;
import hcmute.edu.vn.chatbot_ec.adapter.ProductAdapter;

import hcmute.edu.vn.chatbot_ec.network.ApiClient;
import hcmute.edu.vn.chatbot_ec.network.UserApiService;
import hcmute.edu.vn.chatbot_ec.response.UserDetailResponse;
import hcmute.edu.vn.chatbot_ec.service.AuthenticationService;
import hcmute.edu.vn.chatbot_ec.utils.AuthUtils;
import hcmute.edu.vn.chatbot_ec.utils.SessionManager;
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
    private static final long DEBOUNCE_DELAY_MS = 300; // Debounce delay for scrolling
    private static final long SEARCH_DEBOUNCE_DELAY_MS = 500; // Debounce delay for search
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

    private void loadUserProfile() {
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
                Bundle bundle = new Bundle();
                bundle.putInt("product_id", product.getId());
                bundle.putString("product_name", product.getName());
                bundle.putString("product_description", product.getDescription());
                bundle.putString("product_price", product.getPrice().toString());
                bundle.putString("product_thumbnail_url", product.getThumbnailUrl());
                bundle.putString("product_color", product.getColor());
                bundle.putString("product_size", product.getSize());
                bundle.putInt("product_category_id", product.getCategoryId());
                bundle.putInt("product_stock", product.getStock() != null ? product.getStock() : 0); // Thêm stock
                bundle.putInt("user_id", userId != null ? userId : -1);
                ArrayList<String> imageUrls = new ArrayList<>();
                if (product.getProductImages() != null) {
                    for (ProductImageResponse image : product.getProductImages()) {
                        imageUrls.add(image.getImageUrl());
                    }
                }
                bundle.putStringArrayList("product_images", imageUrls);

                ProductDetailFragment detailFragment = new ProductDetailFragment();
                detailFragment.setArguments(bundle);
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, detailFragment)
                        .addToBackStack(null)
                        .commit();
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
            }            @Override
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