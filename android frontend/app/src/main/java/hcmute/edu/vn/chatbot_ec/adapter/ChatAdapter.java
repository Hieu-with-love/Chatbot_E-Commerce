package hcmute.edu.vn.chatbot_ec.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.model.Message;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Message> messageList;

    public ChatAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messageList.get(position);
        
        if (message.getSentBy() == Message.SENT_BY_USER) {
            // Show user message, hide bot message
            holder.userMessageContainer.setVisibility(View.VISIBLE);
            holder.botMessageContainer.setVisibility(View.GONE);
            
            // Set message content
            holder.userMessageTextView.setText(message.getContent());
            
            // Always ensure user avatar is visible
            holder.userAvatarView.setVisibility(View.VISIBLE);
        } else {
            // Show bot message, hide user message
            holder.userMessageContainer.setVisibility(View.GONE);
            holder.botMessageContainer.setVisibility(View.VISIBLE);
            
            // Set message content
            holder.botMessageTextView.setText(message.getContent());
            
            // Always ensure bot avatar is visible
            holder.botAvatarView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void addMessage(Message message) {
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }
    
    public void setMessages(List<Message> messages) {
        this.messageList.clear();
        this.messageList.addAll(messages);
        notifyDataSetChanged();
    }    
    
      public static class ChatViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout userMessageContainer;
        RelativeLayout botMessageContainer;
        TextView userMessageTextView;
        TextView botMessageTextView;
        TextView userLabelTextView;
        TextView botLabelTextView;
        ImageView userAvatarView;
        ImageView botAvatarView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userMessageContainer = itemView.findViewById(R.id.layout_user_message);
            botMessageContainer = itemView.findViewById(R.id.layout_bot_message);
            userMessageTextView = itemView.findViewById(R.id.textView_user_message);
            botMessageTextView = itemView.findViewById(R.id.textView_bot_message);
            userLabelTextView = itemView.findViewById(R.id.textView_user_label);
            botLabelTextView = itemView.findViewById(R.id.textView_bot_label);
            userAvatarView = itemView.findViewById(R.id.imageView_user_avatar);
            botAvatarView = itemView.findViewById(R.id.imageView_bot_avatar);
        }
    }
}