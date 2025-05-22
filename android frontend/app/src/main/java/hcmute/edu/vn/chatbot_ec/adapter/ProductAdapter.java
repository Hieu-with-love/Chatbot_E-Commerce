package hcmute.edu.vn.chatbot_ec.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.model.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductAdapter(List<Product> products, OnProductClickListener listener) {
        this.productList = products;
        this.listener = listener;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productId, productName, productCategory, productPrice;
        ImageView productImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productId = itemView.findViewById(R.id.product_id);
            productName = itemView.findViewById(R.id.product_name);
            productCategory = itemView.findViewById(R.id.product_category);
            productPrice = itemView.findViewById(R.id.product_price);
            productImage = itemView.findViewById(R.id.product_image);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onProductClick(productList.get(position));
                }
            });
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productId.setText(String.valueOf(product.getId()));
        holder.productName.setText(product.getName());
        holder.productCategory.setText(product.getCategoryName());

//        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
//        holder.productPrice.setText(format.format(product.getPrice()));


        String price = "$" + String.valueOf(product.getPrice());
        holder.productPrice.setText(price);
//        holder.productImage.setImageDrawable(product.getProductImages().get(0));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
