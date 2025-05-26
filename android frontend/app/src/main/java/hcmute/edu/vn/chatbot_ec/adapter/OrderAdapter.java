package hcmute.edu.vn.chatbot_ec.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.response.OrderResponse;

public class OrderAdapter extends ListAdapter<OrderResponse, OrderAdapter.OrderViewHolder> {

    public OrderAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<OrderResponse> DIFF_CALLBACK = new DiffUtil.ItemCallback<OrderResponse>() {
        @Override
        public boolean areItemsTheSame(@NonNull OrderResponse oldItem, @NonNull OrderResponse newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull OrderResponse oldItem, @NonNull OrderResponse newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderResponse order = getItem(position);

        // Format order date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        holder.textOrderDate.setText(holder.itemView.getContext().getString(R.string.order_date_format, order.getOrderDate().format(formatter)));
        holder.textRecipientName.setText(holder.itemView.getContext().getString(R.string.recipient_name_format, order.getRecipientName()));
        holder.textShippingAddress.setText(holder.itemView.getContext().getString(R.string.shipping_address_format, order.getShippingAddress()));
        holder.textShippingPhone.setText(holder.itemView.getContext().getString(R.string.shipping_phone_format, order.getShippingPhone()));
        holder.textTotalPrice.setText(holder.itemView.getContext().getString(R.string.total_price_format, order.getTotalPrice().toString()));
        holder.itemView.setContentDescription(holder.itemView.getContext().getString(R.string.order_description, order.getId()));

        // Customize status color
        String status = order.getStatus();
        holder.textOrderStatus.setText(holder.itemView.getContext().getString(R.string.order_status_format, status));
        if (status.equals("DELIVERED")) {
            holder.textOrderStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.success));
        } else if (status.equals("PENDING")) {
            holder.textOrderStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.warning));
        } else if (status.equals("SHIPPING")) {
            holder.textOrderStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.info));
        } else if (status.equals("CANCELLED")) {
            holder.textOrderStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.error));
        } else {
            holder.textOrderStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_secondary));
        }

        // Setup nested RecyclerView for order items
        OrderItemAdapter itemAdapter = new OrderItemAdapter(order.getOrderItems());
        holder.recyclerViewItems.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.recyclerViewItems.setAdapter(itemAdapter);
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textOrderDate, textOrderStatus, textRecipientName, textShippingAddress, textShippingPhone, textTotalPrice;
        RecyclerView recyclerViewItems;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textOrderDate = itemView.findViewById(R.id.text_order_date);
            textOrderStatus = itemView.findViewById(R.id.text_order_status);
            textRecipientName = itemView.findViewById(R.id.text_recipient_name);
            textShippingAddress = itemView.findViewById(R.id.text_shipping_address);
            textShippingPhone = itemView.findViewById(R.id.text_shipping_phone);
            textTotalPrice = itemView.findViewById(R.id.text_total_price);
            recyclerViewItems = itemView.findViewById(R.id.recycler_view_order_items);
        }
    }
}