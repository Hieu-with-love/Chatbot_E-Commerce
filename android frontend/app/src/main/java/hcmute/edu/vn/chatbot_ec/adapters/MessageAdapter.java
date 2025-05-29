package hcmute.edu.vn.chatbot_ec.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

        if (message.isFromUser()) {
            // Show user message layout, hide bot layout
            holder.userMessageLayout.setVisibility(View.VISIBLE);
            holder.botMessageLayout.setVisibility(View.GONE);
            
            // Always ensure user avatar is visible
            holder.userAvatar.setVisibility(View.VISIBLE);

            // Set user message content
            holder.userMessageText.setText(message.getContent());

            // Set timestamp if available
            if (message.getTimestamp() != null) {
                holder.userTimestamp.setText(timeFormat.format(message.getTimestamp()));
                holder.userTimestamp.setVisibility(View.VISIBLE);
            } else {
                holder.userTimestamp.setVisibility(View.GONE);
            }

        } else {
            // Show bot message layout, hide user layout
            holder.botMessageLayout.setVisibility(View.VISIBLE);
            holder.userMessageLayout.setVisibility(View.GONE);
            
            // Always ensure bot avatar is visible
            holder.botAvatar.setVisibility(View.VISIBLE);

            // Set bot message content
            holder.botMessageText.setText(message.getContent());

            // Set timestamp if available
            if (message.getTimestamp() != null) {
                holder.botTimestamp.setText(timeFormat.format(message.getTimestamp()));
                holder.botTimestamp.setVisibility(View.VISIBLE);
            } else {
                holder.botTimestamp.setVisibility(View.GONE);
            }
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
    
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        // User message components
        public LinearLayout userMessageLayout;
        public TextView userMessageText;
        public TextView userTimestamp;
        public View userAvatar;

        // Bot message components
        public LinearLayout botMessageLayout;
        public TextView botMessageText;
        public TextView botTimestamp;
        public View botAvatar;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize user message components
            userMessageLayout = itemView.findViewById(R.id.userMessageLayout);
            userMessageText = itemView.findViewById(R.id.userMessageText);
            userTimestamp = itemView.findViewById(R.id.userTimestamp);
            userAvatar = itemView.findViewById(R.id.userAvatar);

            // Initialize bot message components
            botMessageLayout = itemView.findViewById(R.id.botMessageLayout);
            botMessageText = itemView.findViewById(R.id.botMessageText);
            botTimestamp = itemView.findViewById(R.id.botTimestamp);
            botAvatar = itemView.findViewById(R.id.botAvatar);
        }
    }
}