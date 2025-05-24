package hcmute.edu.vn.chatbot_ec.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.network.ApiClient;
import hcmute.edu.vn.chatbot_ec.network.CartApiService;
import hcmute.edu.vn.chatbot_ec.request.AddCartItemRequest;
import hcmute.edu.vn.chatbot_ec.response.CartItemResponse;
import hcmute.edu.vn.chatbot_ec.response.CartResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartItemResponse> cartItems;
    private Integer userId;
    private CartApiService apiService;
    private OnCartUpdatedListener onCartUpdatedListener;
    private final Fragment fragment; // Store Fragment for Glide

    public CartAdapter(List<CartItemResponse> cartItems, Integer userId, Fragment fragment) {
        this.cartItems = cartItems;
        this.userId = userId;
        this.fragment = fragment;
        this.apiService = ApiClient.getCartApiService();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItemResponse item = cartItems.get(position);
        holder.textProductName.setText(item.getName());
        holder.textPrice.setText(String.format("%s VNĐ", item.getPrice().toString()));
        holder.textQuantity.setText(String.valueOf(item.getQuantity()));

        // Load image with Glide, checking Fragment attachment
        if (fragment.isAdded() && fragment.getActivity() != null) {
            Glide.with(fragment)
                    .load(item.getThumbnailUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(holder.imageThumbnail);
        }

        // Handle increase quantity button
        holder.buttonIncrease.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            updateItemQuantity(item.getId(), item.getProductId(), newQuantity, position);
        });

        // Handle decrease quantity button
        holder.buttonDecrease.setOnClickListener(v -> {
            int newQuantity = Math.max(1, item.getQuantity() - 1); // Prevent quantity < 1
            updateItemQuantity(item.getId(), item.getProductId(), newQuantity, position);
        });

        // Handle remove button
        holder.buttonRemove.setOnClickListener(v -> {
            removeItem(item.getId(), position);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    private void updateItemQuantity(int itemId, int productId, int quantity, int position) {
        if (userId == null) {
            Toast.makeText(fragment.getContext(), "Vui lòng đăng nhập để cập nhật giỏ hàng", Toast.LENGTH_SHORT).show();
            return;
        }
        AddCartItemRequest request = new AddCartItemRequest(productId, quantity);
        apiService.updateItemInCart(userId, itemId, request).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (onCartUpdatedListener != null) {
                        onCartUpdatedListener.onCartUpdated(response.body().getCartItems());
                    }
                } else {
                    if (fragment.isAdded() && fragment.getContext() != null) {
                        Toast.makeText(fragment.getContext(), "Cập nhật số lượng thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                if (fragment.isAdded() && fragment.getContext() != null) {
                    Toast.makeText(fragment.getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void removeItem(int itemId, int position) {
        if (userId == null) {
            Toast.makeText(fragment.getContext(), "Vui lòng đăng nhập để xóa sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }
        apiService.removeItemFromCart(userId, itemId).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (onCartUpdatedListener != null) {
                        onCartUpdatedListener.onCartUpdated(response.body().getCartItems());
                    }
                } else {
                    if (fragment.isAdded() && fragment.getContext() != null) {
                        Toast.makeText(fragment.getContext(), "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                if (fragment.isAdded() && fragment.getContext() != null) {
                    Toast.makeText(fragment.getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Interface to notify cart updates
    public interface OnCartUpdatedListener {
        void onCartUpdated(List<CartItemResponse> updatedCartItems);
    }

    public void setOnCartUpdatedListener(OnCartUpdatedListener listener) {
        this.onCartUpdatedListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageThumbnail;
        TextView textProductName;
        TextView textPrice;
        TextView textQuantity;
        MaterialButton buttonIncrease;
        MaterialButton buttonDecrease;
        ImageButton buttonRemove;

        public ViewHolder(View itemView) {
            super(itemView);
            imageThumbnail = itemView.findViewById(R.id.image_thumbnail);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textPrice = itemView.findViewById(R.id.text_price);
            textQuantity = itemView.findViewById(R.id.text_quantity);
            buttonIncrease = itemView.findViewById(R.id.button_increase);
            buttonDecrease = itemView.findViewById(R.id.button_decrease);
            buttonRemove = itemView.findViewById(R.id.button_remove);
        }
    }
}