package hcmute.edu.vn.chatbot_ec.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.adapter.CartAdapter;
import hcmute.edu.vn.chatbot_ec.network.ApiClient;
import hcmute.edu.vn.chatbot_ec.network.CartApiService;
import hcmute.edu.vn.chatbot_ec.network.UserApiService;
import hcmute.edu.vn.chatbot_ec.response.CartItemResponse;
import hcmute.edu.vn.chatbot_ec.response.CartResponse;
import hcmute.edu.vn.chatbot_ec.response.UserResponse;
import hcmute.edu.vn.chatbot_ec.utils.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView textTotalPrice;
    private TextView textEmptyCart;
    private Button buttonCheckout;
    private Button buttonClearCart;
    private CartAdapter cartAdapter;
    private List<CartItemResponse> cartItems;
    private CartApiService cartApiService;
    private UserApiService userApiService;
    private Integer userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_cart);
        textTotalPrice = view.findViewById(R.id.text_total_price);
        textEmptyCart = view.findViewById(R.id.text_empty_cart);
        buttonCheckout = view.findViewById(R.id.button_checkout);
        buttonClearCart = view.findViewById(R.id.button_clear_cart);

        cartApiService = ApiClient.getCartApiService();
        userApiService = ApiClient.getUserApiService();
        cartItems = new ArrayList<>();

        // Initialize RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(cartItems, null, this); // Pass Fragment to adapter
        recyclerView.setAdapter(cartAdapter);

        // Set listener for cart updates
        cartAdapter.setOnCartUpdatedListener(updatedCartItems -> {
            if (isAdded()) { // Check if Fragment is attached
                cartItems.clear();
                cartItems.addAll(updatedCartItems);
                cartAdapter.notifyDataSetChanged();
                updateTotalPrice();
                updateCartVisibility();
            }
        });

        // Fetch userId from API
        String token = TokenManager.getToken(getContext());
        if (token == null) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập để xem giỏ hàng", Toast.LENGTH_SHORT).show();
            updateCartVisibility();
            return view;
        }

        userApiService.getMe(token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (isAdded() && getContext() != null) { // Check if Fragment is attached
                    if (response.isSuccessful() && response.body() != null) {
                        userId = response.body().getUserId();
                        cartAdapter = new CartAdapter(cartItems, userId, CartFragment.this); // Reinitialize adapter with userId
                        recyclerView.setAdapter(cartAdapter);
                        cartAdapter.setOnCartUpdatedListener(updatedCartItems -> {
                            if (isAdded()) {
                                cartItems.clear();
                                cartItems.addAll(updatedCartItems);
                                cartAdapter.notifyDataSetChanged();
                                updateTotalPrice();
                                updateCartVisibility();
                            }
                        });
                        fetchCart();
                    } else {
                        Toast.makeText(getContext(), "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                        updateCartVisibility();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    updateCartVisibility();
                }
            }
        });

        // Handle checkout button
        buttonCheckout.setOnClickListener(v -> {
            if (userId == null) {
                Toast.makeText(getContext(), "Vui lòng đăng nhập để thanh toán", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cartItems.isEmpty()) {
                Toast.makeText(getContext(), "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getContext(), "Thanh toán đang được xử lý", Toast.LENGTH_SHORT).show();
        });

        // Handle clear cart button
        buttonClearCart.setOnClickListener(v -> {
            if (userId == null) {
                Toast.makeText(getContext(), "Vui lòng đăng nhập để xóa giỏ hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            clearCart();
        });

        return view;
    }

    private void fetchCart() {
        if (userId == null || !isAdded()) return;
        cartApiService.getCart(userId).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (isAdded() && getContext() != null) {
                    if (response.isSuccessful() && response.body() != null) {
                        cartItems.clear();
                        cartItems.addAll(response.body().getCartItems());
                        cartAdapter.notifyDataSetChanged();
                        updateTotalPrice();
                        updateCartVisibility();
                    } else {
                        Toast.makeText(getContext(), "Không thể tải giỏ hàng", Toast.LENGTH_SHORT).show();
                        updateCartVisibility();
                    }
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    updateCartVisibility();
                }
            }
        });
    }

    private void clearCart() {
        if (userId == null || !isAdded()) return;
        cartApiService.clearCart(userId).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (isAdded() && getContext() != null) {
                    if (response.isSuccessful() && response.body() != null) {
                        cartItems.clear();
                        cartItems.addAll(response.body().getCartItems());
                        cartAdapter.notifyDataSetChanged();
                        updateTotalPrice();
                        updateCartVisibility();
                        Toast.makeText(getContext(), "Đã xóa toàn bộ giỏ hàng", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Không thể xóa giỏ hàng", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItemResponse item : cartItems) {
            if (item.getPrice() != null && item.getQuantity() > 0) {
                total = total.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
            }
        }
        textTotalPrice.setText(String.format("%s VNĐ", total.toString()));
    }

    private void updateCartVisibility() {
        boolean isCartEmpty = cartItems.isEmpty();
        textEmptyCart.setVisibility(isCartEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isCartEmpty ? View.GONE : View.VISIBLE);
        textTotalPrice.setVisibility(isCartEmpty ? View.GONE : View.VISIBLE);
        buttonCheckout.setVisibility(isCartEmpty ? View.GONE : View.VISIBLE);
        buttonClearCart.setVisibility(isCartEmpty ? View.GONE : View.VISIBLE);
    }
}