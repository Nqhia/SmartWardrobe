package vn.edu.usth.smartwaro.chat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.databinding.ItemContainerReceivedMessageBinding;
import vn.edu.usth.smartwaro.databinding.ItemContainerSentMessageBinding;
import vn.edu.usth.smartwaro.chat.ChatMessage;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMessage> chatMessages;
    private final String senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(List<ChatMessage> chatMessages, String senderId) {
        this.chatMessages = chatMessages;

        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT){
            return new SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,false
                    )
            );
        }else {
            return new ReceivedMessageViewHolder(
                    ItemContainerReceivedMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false)
            );
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);

        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            SentMessageViewHolder sentHolder = (SentMessageViewHolder) holder;
            if (chatMessage.image != null) {
                sentHolder.binding.textMessage.setVisibility(View.GONE);
                sentHolder.binding.imageMessage.setVisibility(View.VISIBLE);

                byte[] bytes = Base64.decode(chatMessage.image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                // Sử dụng Glide để tối ưu hiển thị ảnh
                Glide.with(sentHolder.itemView.getContext())
                        .load(bitmap)
                        .override(1080, 1920) // Kích thước tối đa
                        .fitCenter()
                        .into(sentHolder.binding.imageMessage);
            } else {
                sentHolder.binding.textMessage.setVisibility(View.VISIBLE);
                sentHolder.binding.imageMessage.setVisibility(View.GONE);
                sentHolder.binding.textMessage.setText(chatMessage.message);
            }
            sentHolder.binding.textDateTime.setText(chatMessage.dateTime);
        } else {
            ReceivedMessageViewHolder receivedHolder = (ReceivedMessageViewHolder) holder;
            if (chatMessage.image != null) {
                receivedHolder.binding.textMessage.setVisibility(View.GONE);
                receivedHolder.binding.imageMessage.setVisibility(View.VISIBLE);

                byte[] bytes = Base64.decode(chatMessage.image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                Glide.with(receivedHolder.itemView.getContext())
                        .load(bitmap)
                        .override(1080, 1920)
                        .fitCenter()
                        .into(receivedHolder.binding.imageMessage);
            } else {
                receivedHolder.binding.textMessage.setVisibility(View.VISIBLE);
                receivedHolder.binding.imageMessage.setVisibility(View.GONE);
                receivedHolder.binding.textMessage.setText(chatMessage.message);
            }
            receivedHolder.binding.textDateTime.setText(chatMessage.dateTime);
        }
    }



    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).senderId.equals(senderId)){
            return VIEW_TYPE_SENT;
        }else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding){
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        void setData(ChatMessage chatMessage){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerReceivedMessageBinding binding;

        ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding){
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        void setData(ChatMessage chatMessage){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
            binding.imageProfile.setImageResource(R.drawable.ic_profile_default);

        }
    }
}
