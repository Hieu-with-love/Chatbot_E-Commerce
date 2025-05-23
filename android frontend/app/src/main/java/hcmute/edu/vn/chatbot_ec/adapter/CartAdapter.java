package hcmute.edu.vn.chatbot_ec.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import android.util.SparseBooleanArray;
import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.model.CartItem;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    public interface OnSelectionChangedListener {
        void onSelectionChanged(BigDecimal newTotal);
    }

    private List<CartItem> cartItems;
    private OnSelectionChangedListener listener;
    private SparseBooleanArray checkedMap = new SparseBooleanArray();

    public CartAdapter(List<CartItem> cartItems, OnSelectionChangedListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.tvProductId.setText(String.valueOf(cartItem.getProduct().getId()));
        holder.tvProductName.setText(cartItem.getProduct().getName());
        holder.tvProductPrice.setText(String.valueOf(cartItem.getProduct().getPrice()));
        holder.tvProductQuantity.setText(String.valueOf(cartItem.getQuantity()));

        // restore checkbox state
        holder.cbSelect.setOnCheckedChangeListener(null);
        boolean isChecked = checkedMap.get(position, false);
        holder.cbSelect.setChecked(isChecked);

        // handle checkbox changes
        holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked1) -> {
            checkedMap.put(holder.getAdapterPosition(), isChecked1);
            if (listener != null) {
                listener.onSelectionChanged(calculateTotal());
            }
        });

        holder.btnIncrease.setOnClickListener(v -> updateQty(holder, +1));
        holder.btnDecrease.setOnClickListener(v -> updateQty(holder, -1));

        // remove button
        holder.btnRemove.setOnClickListener(v -> {
            int index = holder.getAdapterPosition();
            cartItems.remove(index);
            checkedMap.delete(index);
            notifyItemRemoved(index);
            if (listener != null) {
                listener.onSelectionChanged(calculateTotal());
            }
        });
    }

    private void updateQty(ViewHolder holder, int delta) {
        int qty = Integer.parseInt(holder.tvProductQuantity.getText().toString()) + delta;
        if (qty < 1) qty = 1;
        holder.tvProductQuantity.setText(String.valueOf(qty));
        cartItems.get(holder.getAdapterPosition()).setQuantity(qty);
        if (holder.cbSelect.isChecked() && listener != null) listener.onSelectionChanged(calculateTotal());
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public List<CartItem> getSelectedItems() {
        List<CartItem> sel = new ArrayList<>();
        for (int i = 0; i < cartItems.size(); i++) {
            if (checkedMap.get(i, false)) sel.add(cartItems.get(i));
        }
        return sel;
    }
    // sum price * qty for checked items
    public BigDecimal calculateTotal() {
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < cartItems.size(); i++) {
            if (checkedMap.get(i, false)) {
                CartItem ci = cartItems.get(i);
                BigDecimal line = ci.getProduct()
                        .getPrice()
                        .multiply(BigDecimal.valueOf(ci.getQuantity()));
                sum = sum.add(line);
            }
        }
        return sum;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvProductQuantity, tvProductId;
        ImageButton btnIncrease, btnDecrease, btnRemove;
        CheckBox cbSelect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSelect       = itemView.findViewById(R.id.checkbox_select);
            tvProductId    = itemView.findViewById(R.id.cart_tv_product_id);
            tvProductName  = itemView.findViewById(R.id.cart_tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.cart_tv_product_price);
            tvProductQuantity = itemView.findViewById(R.id.cart_tv_quantity);
            btnIncrease    = itemView.findViewById(R.id.button_increase);
            btnDecrease    = itemView.findViewById(R.id.button_decrease);
            btnRemove      = itemView.findViewById(R.id.cart_button_remove);

            // quantity increase
//            btnIncrease.setOnClickListener(v -> {
//                int quantity = Integer.parseInt(tvProductQuantity.getText().toString());
//                quantity++;
//                tvProductQuantity.setText(String.valueOf(quantity));
//                if (listener != null && cbSelect.isChecked()) {
//                    listener.onSelectionChanged(calculateTotal());
//                }
//            });
//
//            // quantity decrease
//            btnDecrease.setOnClickListener(v -> {
//                int quantity = Integer.parseInt(tvProductQuantity.getText().toString());
//                if (quantity > 1) {
//                    quantity--;
//                    tvProductQuantity.setText(String.valueOf(quantity));
//                    if (listener != null && cbSelect.isChecked()) {
//                        listener.onSelectionChanged(calculateTotal());
//                    }
//                }
//            });
        }
    }
}
