package hcmute.edu.vn.chatbot_ec.adapter;

import android.content.Context;
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

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder> {
    private List<ProductImage> images;
    private Context context;

    public ImageSliderAdapter(Context ctx, List<ProductImage> imgs) {
        context = ctx;
        images = imgs;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.product_image_item, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull SliderViewHolder holder, int position) {
        Glide.with(context)
                .load(images.get(position).getUrl())
                .centerCrop()
                .into(holder.img);
    }

    @Override public int getItemCount() {
        return images.size();
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.rv_image_gallery);
        }
    }
}
