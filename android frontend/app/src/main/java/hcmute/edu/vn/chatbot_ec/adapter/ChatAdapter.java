package hcmute.edu.vn.chatbot_ec.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
            holder.userMessageTextView.setVisibility(View.VISIBLE);
            holder.botMessageTextView.setVisibility(View.GONE);
            holder.userMessageTextView.setText(message.getContent());
        } else {
            holder.userMessageTextView.setVisibility(View.GONE);
            holder.botMessageTextView.setVisibility(View.VISIBLE);
            holder.botMessageTextView.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }    public void addMessage(Message message) {
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }
    
    public void setMessages(List<Message> messages) {
        this.messageList.clear();
        this.messageList.addAll(messages);
        notifyDataSetChanged();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView userMessageTextView;
        TextView botMessageTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userMessageTextView = itemView.findViewById(R.id.textView_user_message);
            botMessageTextView = itemView.findViewById(R.id.textView_bot_message);
        }
    }
}