package vn.edu.usth.smartwaro.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.auth.model.UserModel;
import vn.edu.usth.smartwaro.databinding.ActivityChatBinding;
import vn.edu.usth.smartwaro.utils.Constants;
import vn.edu.usth.smartwaro.utils.PreferenceManager;

public class UsersActivity extends AppCompatActivity implements UserListener{

    private ActivityChatBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        loadUserDetails();
        setListeners();
        loadFriends();
    }

    private void setListeners(){
        binding.imageSignOut.setOnClickListener(v -> onBackPressed());
        binding.fabNewChat.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), FriendRequestActivity.class)));
    }

    private void loadUserDetails() {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        binding.imageProfile.setImageResource(R.drawable.ic_profile_default);
    }


    private void showErrorMessage(String s) {
        binding.textErrorMessage.setText("No user available");
        binding.textErrorMessage.setVisibility(View.VISIBLE);
        binding.usersRecyclerView.setVisibility(View.GONE);
    }

    private void loading(Boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onUserClicked(UserModel user) {
        if (user == null) return;

        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user); // Đảm bảo UserModel implements Serializable
        startActivity(intent);
        finish();
    }

    private void loadFriends() {
        loading(true); // Hiển thị ProgressBar
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);

        database.collection(Constants.KEY_COLLECTION_FRIENDS)
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false); // Ẩn ProgressBar
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<UserModel> friends = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            String friendId = documentSnapshot.getString("friendId");

                            // Tải thông tin chi tiết của bạn bè
                            database.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(friendId)
                                    .get()
                                    .addOnSuccessListener(document -> {
                                        UserModel user = new UserModel();
                                        user.setId(friendId);
                                        user.setName(document.getString(Constants.KEY_NAME));
                                        user.setEmail(document.getString(Constants.KEY_EMAIL));
                                        friends.add(user);

                                        if (friends.size() > 0) {
                                            showFriends(friends); // Hiển thị danh sách bạn bè
                                        } else {
                                            showErrorMessage("Không có bạn bè!");
                                        }
                                    });
                        }
                    } else {
                        showErrorMessage("Không thể tải danh sách bạn bè!");
                    }
                });
    }

    private void showFriends(List<UserModel> friends) {
        UsersAdapter usersAdapter = new UsersAdapter(friends, this);
        binding.usersRecyclerView.setAdapter(usersAdapter);
        binding.usersRecyclerView.setVisibility(View.VISIBLE);
        binding.textErrorMessage.setVisibility(View.GONE);
    }

}