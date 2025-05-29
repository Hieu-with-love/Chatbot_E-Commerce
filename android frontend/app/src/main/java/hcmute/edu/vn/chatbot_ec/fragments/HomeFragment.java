package hcmute.edu.vn.chatbot_ec.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.activity.Login; // Assuming LoginActivity exists
import hcmute.edu.vn.chatbot_ec.activity.ProductDetailActivity;
import hcmute.edu.vn.chatbot_ec.adapter.ProductAdapter;
import hcmute.edu.vn.chatbot_ec.network.ApiClient;
import hcmute.edu.vn.chatbot_ec.network.CartApiService;
import hcmute.edu.vn.chatbot_ec.network.ProductApiService;
import hcmute.edu.vn.chatbot_ec.network.UserApiService;
import hcmute.edu.vn.chatbot_ec.request.AddCartItemRequest;
import hcmute.edu.vn.chatbot_ec.response.CartResponse;
import hcmute.edu.vn.chatbot_ec.response.PageResponse;
import hcmute.edu.vn.chatbot_ec.response.ProductResponse;
import hcmute.edu.vn.chatbot_ec.response.UserResponse;
import hcmute.edu.vn.chatbot_ec.utils.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private RecyclerView rvProducts;
    private ProgressBar progressBar;
    private TextView tvEmptyState, tvGreeting, tvFullName;
    private ImageView imgCart, imgUserAvatar;
    private Button btnLogin; // Added login button
    private SearchView searchView;
    private ProductAdapter adapter;
    private List<ProductResponse> products = new ArrayList<>();
    private ProductApiService productApiService;
    private CartApiService cartApiService;
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

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize UI components
        rvProducts = view.findViewById(R.id.rv_products);
        progressBar = view.findViewById(R.id.progress_bar);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
        tvGreeting = view.findViewById(R.id.tv_greeting);
        tvFullName = view.findViewById(R.id.tv_full_name);
        imgCart = view.findViewById(R.id.img_cart);
        btnLogin = view.findViewById(R.id.btn_login);
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

        // Handle login button click
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Login.class);
            startActivity(intent);
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
                searchHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY_MS);
                return true;
            }
        });

        // Handle search clear
        searchView.setOnCloseListener(() -> {
            currentQuery = "";
            currentPage = 1;
            isLastPage = false;
            fetchProducts(currentPage, pageSize, "id", "asc", "");
            return false;
        });

        // Fetch user info for greeting and avatar
        String token = TokenManager.getToken(getContext());
        if (token != null) {
            // Show cart icon, hide login button
            imgCart.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);

            UserApiService userApiService = ApiClient.getUserApiService();
            userApiService.getMe(token).enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (isAdded() && getContext() != null) {
                        if (response.isSuccessful() && response.body() != null) {
                            userId = response.body().getUserId();
                            String fullName = response.body().getFullName();
                            tvFullName.setText(fullName != null ? fullName : getString(R.string.guest));

                            String avatarUrl = response.body().getAvatarUrl();
                            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                Glide.with(HomeFragment.this)
                                        .load(avatarUrl)
                                        .placeholder(R.drawable.ic_user_placeholder)
                                        .error(R.drawable.ic_user_placeholder)
                                        .circleCrop()
                                        .into(imgUserAvatar);
                            } else {
                                imgUserAvatar.setImageResource(R.drawable.ic_user_placeholder);
                            }
                        } else {
                            tvFullName.setText(R.string.guest);
                            imgUserAvatar.setImageResource(R.drawable.ic_user_placeholder);
                            Toast.makeText(getContext(), "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                        }
                        fetchProducts(currentPage, pageSize, "id", "asc", "");
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    if (isAdded() && getContext() != null) {
                        tvFullName.setText(R.string.guest);
                        imgUserAvatar.setImageResource(R.drawable.ic_user_placeholder);
                        Toast.makeText(getContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        fetchProducts(currentPage, pageSize, "id", "asc", "");
                    }
                }
            });
        } else {
            // Show login button, hide cart icon
            imgCart.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
            tvFullName.setText(R.string.guest);
            imgUserAvatar.setImageResource(R.drawable.ic_user_placeholder);
            fetchProducts(currentPage, pageSize, "id", "asc", "");
        }

        return view;
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