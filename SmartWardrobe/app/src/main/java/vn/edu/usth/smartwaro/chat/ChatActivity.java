package vn.edu.usth.smartwaro.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.auth.model.UserModel;
import vn.edu.usth.smartwaro.databinding.ActivityChatBinding;
import vn.edu.usth.smartwaro.utils.Constants;
import vn.edu.usth.smartwaro.utils.PreferenceManager;

public class ChatActivity extends AppCompatActivity implements UserListener{

    private ActivityChatBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        loadUserDetails();
//        getToken();
        setListeners();
        getUsers();
    }

    private void setListeners(){
        binding.imageSignOut.setOnClickListener(v -> onBackPressed());
        binding.fabNewChat.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), UsersActivity.class)));
    }

    private void loadUserDetails() {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        binding.imageProfile.setImageResource(R.drawable.ic_profile_default);
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
    }

    private  void updateToken(String token){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection((Constants.KEY_COLLECTION_USERS)).document(preferenceManager.getString(Constants.KEY_USER_ID));

//        documentReference.update(Constants.KEY_FCM_TOKEN,token)
//                .addOnFailureListener(e -> showToast("Unable to update token"));
    }

//    private void getToken(){
//        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
//    }

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