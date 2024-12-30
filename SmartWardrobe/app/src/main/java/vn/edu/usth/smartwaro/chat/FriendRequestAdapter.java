package vn.edu.usth.smartwaro.chat;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.edu.usth.smartwaro.auth.model.FriendRequest;
import vn.edu.usth.smartwaro.databinding.ItemContainerFriendRequestBinding;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder> {
    private final List<FriendRequest> friendRequests;
    private final FriendRequestListener listener;

    public FriendRequestAdapter(List<FriendRequest> friendRequests, FriendRequestListener listener) {
        this.friendRequests = friendRequests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerFriendRequestBinding binding = ItemContainerFriendRequestBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new FriendRequestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position) {
        holder.setFriendRequestData(friendRequests.get(position));
    }

    @Override
    public int getItemCount() {
        return friendRequests.size();
    }

    public void removeRequest(FriendRequest request) {
        int position = friendRequests.indexOf(request);
        if (position != -1) {
            friendRequests.remove(position);
            notifyItemRemoved(position); // Cập nhật giao diện sau khi xóa
        }
    }

    class FriendRequestViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerFriendRequestBinding binding;

        FriendRequestViewHolder(ItemContainerFriendRequestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setFriendRequestData(FriendRequest request) {
            binding.textSenderName.setText(request.getSenderId());
            binding.textSenderEmail.setText(request.getSenderEmail()); // Hiển thị email// Thay bằng tên người gửi nếu có
            binding.buttonAccept.setOnClickListener(v -> listener.onAcceptClicked(request));
            binding.buttonReject.setOnClickListener(v -> listener.onRejectClicked(request));
        }
    }
}
