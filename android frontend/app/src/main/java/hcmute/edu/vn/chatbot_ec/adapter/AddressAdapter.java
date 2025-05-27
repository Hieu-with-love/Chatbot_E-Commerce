package hcmute.edu.vn.chatbot_ec.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.response.AddressResponse;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private List<AddressResponse> addresses;
    private OnAddressActionListener listener;

    public interface OnAddressActionListener {
        void onEdit(AddressResponse address);
        void onDelete(AddressResponse address);
        void onSelect(AddressResponse address);
    }

    public AddressAdapter(List<AddressResponse> addresses, OnAddressActionListener listener) {
        this.addresses = addresses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        AddressResponse address = addresses.get(position);
        holder.textRecipientName.setText(address.getRecipientName() != null ? address.getRecipientName() : "Không xác định");
        holder.textFullAddress.setText(address.getFullAddress() != null ? address.getFullAddress() : "Không xác định");
        holder.textPhone.setText(address.getPhone() != null ? "SĐT: " + address.getPhone() : "Không xác định");

        holder.buttonEdit.setOnClickListener(v -> listener.onEdit(address));
        holder.buttonDelete.setOnClickListener(v -> listener.onDelete(address));
        holder.itemView.setOnClickListener(v -> listener.onSelect(address));
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public void updateAddresses(List<AddressResponse> newAddresses) {
        this.addresses = newAddresses;
        notifyDataSetChanged();
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView textRecipientName, textFullAddress, textPhone;
        MaterialButton buttonEdit, buttonDelete;

        AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            textRecipientName = itemView.findViewById(R.id.text_recipient_name);
            textFullAddress = itemView.findViewById(R.id.text_full_address);
            textPhone = itemView.findViewById(R.id.text_phone);
            buttonEdit = itemView.findViewById(R.id.button_edit);
            buttonDelete = itemView.findViewById(R.id.button_delete);
        }
    }
}