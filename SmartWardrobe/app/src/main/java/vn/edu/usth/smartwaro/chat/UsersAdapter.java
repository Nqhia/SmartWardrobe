package vn.edu.usth.smartwaro.chat;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.edu.usth.smartwaro.auth.model.UserModel;
import vn.edu.usth.smartwaro.databinding.ItemContainerUserBinding;
import vn.edu.usth.smartwaro.R;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>{

    private final List<UserModel> users;
    private final UserListener userListener;

    public UsersAdapter(List<UserModel> users, UserListener userListener) {
        this.users = users;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),parent,false
        );
        return new UserViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{

        ItemContainerUserBinding binding;

        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding){
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        void setUserData(UserModel user){
            binding.textname.setText(user.getName());
            binding.textEmail.setText(user.getEmail());
            binding.imageProfile.setImageResource(R.drawable.ic_profile_default);
            binding.getRoot().setOnClickListener(v -> userListener.onUserClicked(user));
        }
    }

}
