package hcmute.edu.vn.chatbot_ec.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.model.ProductImage;

public class ProductImageAdapter extends RecyclerView.Adapter<ProductImageAdapter.ViewHolder> {

    List<String> productImages;
    private final OnImageClickListener listener;

    public interface OnImageClickListener {
        void onImageClick(String url);
    }
    public ProductImageAdapter(List<String> productImages, OnImageClickListener listener) {
        this.productImages = productImages;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductImageAdapter.ViewHolder holder, int position) {
        String image = productImages.get(position);
        Glide.with(holder.itemView.getContext())
                .load(image)
                .into((ImageView) holder.itemView);

        holder.itemView.setOnClickListener(v -> listener.onImageClick(image));
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView = itemView.findViewById(R.id.img_gallery_thumb);
        }
    }
}
