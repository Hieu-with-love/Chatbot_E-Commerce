package hcmute.edu.vn.chatbot_ec.adapter;

import android.content.Context;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.model.Product;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    private Context context;
    private List<Product> products = new ArrayList<>();
    private OnItemClickListener listener;

    public ProductListAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(hcmute.edu.vn.chatbot_ec.R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(products.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView tvId;
        private ImageView ivImage;
        private TextView tvName;
        private TextView tvCategory;
        private TextView tvPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId    = itemView.findViewById(R.id.product_id);
            ivImage = itemView.findViewById(R.id.product_image);
            tvName  = itemView.findViewById(R.id.product_name);
            tvCategory = itemView.findViewById(R.id.product_category);
            tvPrice = itemView.findViewById(R.id.product_price);
        }

        public void bind(final Product product, final OnItemClickListener listener) {
            // Nếu bạn muốn ẩn TextView lưu ID:
            tvId.setText(String.valueOf(product.getId()));
            tvId.setVisibility(View.GONE);

            // Gán dữ liệu lên các view
            tvName.setText(product.getName());
            tvCategory.setText(product.getCategoryName());
            tvPrice.setText(String.format("$%.2f", product.getPrice()));

            // Load ảnh (Glide / Picasso)


            // Bắt sự kiện click
            itemView.setOnClickListener(v -> listener.onItemClick(product));
        }
    }

}
