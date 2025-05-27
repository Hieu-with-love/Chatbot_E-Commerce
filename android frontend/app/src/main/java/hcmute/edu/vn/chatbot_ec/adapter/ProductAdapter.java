package hcmute.edu.vn.chatbot_ec.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import java.util.List;
import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.response.ProductResponse;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_PRODUCT = 0;
    private static final int TYPE_LOADING = 1;
    private List<ProductResponse> products;
    private OnProductClickListener listener;
    private boolean isLoadingFooter = false;

    public interface OnProductClickListener {
        void onProductClick(ProductResponse product);
        void onAddToCartClick(ProductResponse product);
    }

    public ProductAdapter(List<ProductResponse> products, OnProductClickListener listener) {
        this.products = products;
        this.listener = listener;
    }



    @Override
    public int getItemViewType(int position) {
        return (position == products.size() && isLoadingFooter) ? TYPE_LOADING : TYPE_PRODUCT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_PRODUCT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.product_item, parent, false);
            return new ProductViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.loading_footer, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProductViewHolder) {
            ProductResponse product = products.get(position);
            ProductViewHolder productHolder = (ProductViewHolder) holder;
            productHolder.textName.setText(product.getName());
            productHolder.textPrice.setText(String.format("%s VNĐ", product.getPrice().toString()));
            productHolder.textColor.setText(product.getColor() != null ? "Màu: " + product.getColor() : "Màu: Không xác định");
            productHolder.textSize.setText(product.getSize() != null ? "Kích thước: " + product.getSize() : "Kích thước: Không xác định");

            Glide.with(holder.itemView.getContext())
                    .load(product.getThumbnailUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(productHolder.imageThumbnail);

            productHolder.itemView.setOnClickListener(v -> listener.onProductClick(product));
            productHolder.buttonAddToCart.setOnClickListener(v -> listener.onAddToCartClick(product));
        }
        // No binding needed for LoadingViewHolder
    }

    @Override
    public int getItemCount() {
        return products.size() + (isLoadingFooter ? 1 : 0);
    }

    public void setLoadingFooter(boolean loading) {
        isLoadingFooter = loading;
        notifyItemChanged(products.size());
    }
    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageThumbnail;
        TextView textName, textPrice, textColor, textSize;
        MaterialButton buttonAddToCart;

        ProductViewHolder(View itemView) {
            super(itemView);
            imageThumbnail = itemView.findViewById(R.id.image_thumbnail);
            textName = itemView.findViewById(R.id.text_product_name);
            textPrice = itemView.findViewById(R.id.text_price);
            textColor = itemView.findViewById(R.id.text_color);
            textSize = itemView.findViewById(R.id.text_size);
            buttonAddToCart = itemView.findViewById(R.id.button_add_to_cart);
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }
}