package hcmute.edu.vn.chatbot_ec.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.model.CartItem;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartItem> cartItems;

    public CartAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {

        CartItem cartItem = cartItems.get(position);
        holder.tvProductId.setText(String.valueOf(cartItem.getProduct().getId()));
        holder.tvProductName.setText(cartItem.getProduct().getName());
        holder.tvProductPrice.setText(String.valueOf(cartItem.getProduct().getPrice()));
        holder.tvProductQuantity.setText(String.valueOf(cartItem.getQuantity()));

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvProductQuantity, tvProductId;
        ImageButton btnIncrease, btnDecrease, btnRemove;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvProductId = itemView.findViewById(R.id.cart_tv_product_id);
            tvProductName = itemView.findViewById(R.id.cart_tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.cart_tv_product_price);
            tvProductQuantity = itemView.findViewById(R.id.cart_tv_quantity);
            btnIncrease = itemView.findViewById(R.id.button_increase);
            btnDecrease = itemView.findViewById(R.id.button_decrease);
            btnRemove = itemView.findViewById(R.id.cart_button_remove);

            btnIncrease.setOnClickListener(v -> {
                int quantity = Integer.parseInt(tvProductQuantity.getText().toString());
                quantity++;
                tvProductQuantity.setText(String.valueOf(quantity));
            });

            btnDecrease.setOnClickListener(v -> {
                int quantity = Integer.parseInt(tvProductQuantity.getText().toString());
                if (quantity > 1) {
                    quantity--;
                    tvProductQuantity.setText(String.valueOf(quantity));
                }
            });

        }
    }
}
