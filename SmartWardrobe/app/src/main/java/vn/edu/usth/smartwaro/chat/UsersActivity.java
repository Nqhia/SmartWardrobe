package vn.edu.usth.smartwaro.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.smartwaro.databinding.ActivityUsersBinding;
import vn.edu.usth.smartwaro.auth.model.UserModel;
import vn.edu.usth.smartwaro.utils.Constants;
import vn.edu.usth.smartwaro.utils.PreferenceManager;

public class UsersActivity extends AppCompatActivity implements UserListener {

    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        getUsers();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void getUsers() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
        Log.d("UsersActivity", "Current User ID: " + currentUserId);
        if (currentUserId == null) {
            loading(false);
            showErrorMessage();
            return;
        }

        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<UserModel> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            // Kiểm tra ID document có null không
                            String documentId = queryDocumentSnapshot.getId();
                            if (documentId == null || documentId.equals(currentUserId)) {
                                continue;
                            }

                            try {
                                UserModel user = new UserModel();
                                // Kiểm tra và set các giá trị, tránh null
                                user.setName(queryDocumentSnapshot.getString(Constants.KEY_NAME));
                                user.setEmail(queryDocumentSnapshot.getString(Constants.KEY_EMAIL));
                                user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                                user.setId(documentId);
                                users.add(user);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (users.size() > 0) {
                            UsersAdapter usersAdapter = new UsersAdapter(users, this);
                            binding.usersRecyclerView.setAdapter(usersAdapter);
                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                            binding.textErrorMessage.setVisibility(View.GONE);
                        } else {
                            showErrorMessage();
                        }
                    } else {
                        showErrorMessage();
                    }
                })
                .addOnFailureListener(e -> {
                    loading(false);
                    showErrorMessage();
                    e.printStackTrace();
                });
    }

    private void showErrorMessage() {
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

        Intent intent = new Intent(getApplicationContext(), Chat_1Activity.class);
        // Sửa cách truyền user object
        intent.putExtra(Constants.KEY_USER, user); // Đảm bảo UserModel implements Serializable
        startActivity(intent);
        finish();
    }
}
