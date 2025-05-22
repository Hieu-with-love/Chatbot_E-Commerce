package hcmute.edu.vn.chatbot_ec.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.model.Message;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        
        holder.textViewMessage.setText(message.getContent());
        
        // Set timestamp if available
        if (message.getTimestamp() != null) {
            holder.textViewTimestamp.setText(timeFormat.format(message.getTimestamp()));
        } else {
            holder.textViewTimestamp.setVisibility(View.GONE);
        }

        // Style the message based on who sent it
        CardView cardView = (CardView) holder.itemView;
        if (message.isFromUser()) {
            // User message
            cardView.setCardBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.user_message_bg, null));
            holder.textViewMessage.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        } else {
            // Bot message
            cardView.setCardBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.bot_message_bg, null));
            holder.textViewMessage.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void updateMessages(List<Message> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;
        TextView textViewTimestamp;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewTimestamp = itemView.findViewById(R.id.textViewTimestamp);
        }
    }
}
