package hcmute.edu.vn.chatbot_ec.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.response.AddressResponse;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private List<AddressResponse> addresses;

    public AddressAdapter(List<AddressResponse> addresses) {
        this.addresses = addresses;
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

        holder.textRecipientName.setText(address.getRecipientName());
        holder.textFullAddress.setText(address.getFullAddress());
        holder.textPhone.setText(address.getPhone());
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView textRecipientName, textFullAddress, textPhone;

        AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            textRecipientName = itemView.findViewById(R.id.text_recipient_name);
            textFullAddress = itemView.findViewById(R.id.text_full_address);
            textPhone = itemView.findViewById(R.id.text_phone);
        }
    }
}