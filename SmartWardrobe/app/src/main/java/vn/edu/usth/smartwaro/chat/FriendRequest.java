package vn.edu.usth.smartwaro.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import vn.edu.usth.smartwaro.auth.model.UserModel;
import vn.edu.usth.smartwaro.databinding.ActivityUsersBinding;
import vn.edu.usth.smartwaro.utils.Constants;
import vn.edu.usth.smartwaro.utils.PreferenceManager;

public class FriendRequest extends AppCompatActivity {

    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        loadFriendRequests();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUser(query); // Tìm kiếm khi người dùng nhấn Enter
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false; // Không cần xử lý
            }
        });
    }

    private void loadFriendRequests() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);

        database.collection(Constants.KEY_COLLECTION_FRIEND_REQUESTS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, currentUserId)
                .whereEqualTo(Constants.KEY_STATUS, Constants.STATUS_PENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<vn.edu.usth.smartwaro.auth.model.FriendRequest> requests = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            vn.edu.usth.smartwaro.auth.model.FriendRequest request = snapshot.toObject(vn.edu.usth.smartwaro.auth.model.FriendRequest.class);
                            request.setId(snapshot.getId());
                            requests.add(request);
                        }
                        showFriendRequests(requests); // Hiển thị danh sách lời mời
                    }
                });
    }

    private void showFriendRequests(List<vn.edu.usth.smartwaro.auth.model.FriendRequest> requests) {
        FriendRequestAdapter adapter = new FriendRequestAdapter(requests, new FriendRequestListener() {
            @Override
            public void onAcceptClicked(vn.edu.usth.smartwaro.auth.model.FriendRequest request) {
                acceptFriendRequest(request);
            }

            @Override
            public void onRejectClicked(vn.edu.usth.smartwaro.auth.model.FriendRequest request) {
                rejectFriendRequest(request);
            }
        });
        binding.usersRecyclerView.setAdapter(adapter);
        binding.usersRecyclerView.setVisibility(View.VISIBLE);
    }

    private void acceptFriendRequest(vn.edu.usth.smartwaro.auth.model.FriendRequest request) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        // Thêm vào danh sách bạn bè
        HashMap<String, Object> friend1 = new HashMap<>();
        friend1.put("userId", request.getReceiverId());
        friend1.put("friendId", request.getSenderId());
        friend1.put(Constants.KEY_TIMESTAMP, new Date());

        HashMap<String, Object> friend2 = new HashMap<>();
        friend2.put("userId", request.getSenderId());
        friend2.put("friendId", request.getReceiverId());
        friend2.put(Constants.KEY_TIMESTAMP, new Date());

        database.collection(Constants.KEY_COLLECTION_FRIENDS)
                .add(friend1)
                .addOnSuccessListener(docRef -> {
                    database.collection(Constants.KEY_COLLECTION_FRIENDS)
                            .add(friend2)
                            .addOnSuccessListener(unused -> {
                                // Xóa lời mời kết bạn
                                database.collection(Constants.KEY_COLLECTION_FRIEND_REQUESTS)
                                        .document(request.getId())
                                        .delete()
                                        .addOnSuccessListener(unused2 -> {
                                            Toast.makeText(this, "Đã chấp nhận lời mời kết bạn!", Toast.LENGTH_SHORT).show();
                                            // Xóa khỏi giao diện
                                            FriendRequestAdapter adapter = (FriendRequestAdapter) binding.usersRecyclerView.getAdapter();
                                            if (adapter != null) {
                                                adapter.removeRequest(request);
                                            }
                                        });
                            });
                });
    }


    private void rejectFriendRequest(vn.edu.usth.smartwaro.auth.model.FriendRequest request) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection(Constants.KEY_COLLECTION_FRIEND_REQUESTS)
                .document(request.getId())
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Đã từ chối lời mời kết bạn!", Toast.LENGTH_SHORT).show();
                    // Xóa lời mời khỏi danh sách hiển thị
                    FriendRequestAdapter adapter = (FriendRequestAdapter) binding.usersRecyclerView.getAdapter();
                    if (adapter != null) {
                        adapter.removeRequest(request);
                    }
                });
    }


    private void searchUser(String email) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            UserModel user = snapshot.toObject(UserModel.class);
                            user.setId(snapshot.getId());
                            sendFriendRequest(user); // Gửi lời mời kết bạn
                        }
                    } else {
                        Toast.makeText(this, "Không tìm thấy người dùng!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendFriendRequest(UserModel user) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);

        if (currentUserId.equals(user.getId())) {
            Toast.makeText(this, "Bạn không thể kết bạn với chính mình!", Toast.LENGTH_SHORT).show();
            return; // Dừng lại, không tiếp tục gửi lời mời
        }
        // Kiểm tra xem đã là bạn bè chưa
        database.collection(Constants.KEY_COLLECTION_FRIENDS)
                .whereEqualTo("userId", currentUserId)
                .whereEqualTo("friendId", user.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        // Nếu đã là bạn bè
                        Toast.makeText(this, "Người này đã là bạn bè của bạn!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Nếu chưa là bạn bè, tiến hành gửi lời mời
                        proceedToSendFriendRequest(user);
                    }
                });
    }

    private void proceedToSendFriendRequest(UserModel user) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
        String currentUserName = preferenceManager.getString(Constants.KEY_NAME);
        String currentUserEmail = preferenceManager.getString(Constants.KEY_EMAIL);

        HashMap<String, Object> request = new HashMap<>();
        request.put(Constants.KEY_SENDER_ID, currentUserId);
        request.put(Constants.KEY_RECEIVER_ID, user.getId());
        request.put(Constants.KEY_STATUS, Constants.STATUS_PENDING);
        request.put(Constants.KEY_TIMESTAMP, new Date());
        request.put("senderName", currentUserName);
        request.put("senderEmail", currentUserEmail);

        database.collection(Constants.KEY_COLLECTION_FRIEND_REQUESTS)
                .add(request)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Lời mời kết bạn đã được gửi!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Không thể gửi lời mời kết bạn!", Toast.LENGTH_SHORT).show();
                });
    }


}
