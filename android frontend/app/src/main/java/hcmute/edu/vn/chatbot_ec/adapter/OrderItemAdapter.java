package hcmute.edu.vn.chatbot_ec.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.response.OrderItemResponse;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private List<OrderItemResponse> orderItems;

    public OrderItemAdapter(List<OrderItemResponse> orderItems) {
        this.orderItems = orderItems;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_item, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItemResponse item = orderItems.get(position);

        holder.textProductName.setText(item.getProductName());
        holder.textQuantity.setText(holder.itemView.getContext().getString(R.string.quantity_format, item.getQuantity().toString()));
        holder.textPrice.setText(holder.itemView.getContext().getString(R.string.price_format, item.getPriceAtPurchase().toString()));
        holder.textColor.setText(holder.itemView.getContext().getString(R.string.color_format, item.getColor()));
        holder.textSize.setText(holder.itemView.getContext().getString(R.string.size_format, item.getSize()));
        holder.imageThumbnail.setContentDescription(holder.itemView.getContext().getString(R.string.product_image_description, item.getProductName()));

        // Load thumbnail with Glide
        Glide.with(holder.itemView.getContext())
                .load(item.getThumbnailUrl())
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(holder.imageThumbnail);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageThumbnail;
        TextView textProductName, textQuantity, textPrice, textColor, textSize;

        OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageThumbnail = itemView.findViewById(R.id.image_thumbnail);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textQuantity = itemView.findViewById(R.id.text_quantity);
            textPrice = itemView.findViewById(R.id.text_price);
            textColor = itemView.findViewById(R.id.text_color);
            textSize = itemView.findViewById(R.id.text_size);
        }
    }
}