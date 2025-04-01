package vn.edu.usth.smartwaro.chat;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import vn.edu.usth.smartwaro.auth.model.UserModel;
import vn.edu.usth.smartwaro.databinding.FragmentShareUsersBinding;
import vn.edu.usth.smartwaro.utils.Constants;
import vn.edu.usth.smartwaro.utils.PreferenceManager;

public class ShareUsersFragment extends Fragment implements UserListener {
    private FragmentShareUsersBinding binding;
    private PreferenceManager preferenceManager;
    private Context safeContext; // Biến để lưu context an toàn

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        safeContext = context; // Lưu context khi fragment attached
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShareUsersBinding.inflate(inflater, container, false);

        requireActivity().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        init();
        setListeners();
        loadUsers();
        return binding.getRoot();
    }

    private void init() {
        preferenceManager = new PreferenceManager(requireContext());
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private void loadUsers() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);

        database.collection(Constants.KEY_COLLECTION_FRIENDS)
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<UserModel> users = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String friendId = document.getString("friendId");
                            database.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(friendId)
                                    .get()
                                    .addOnSuccessListener(userDoc -> {
                                        UserModel user = new UserModel();
                                        user.setId(friendId);
                                        user.setName(userDoc.getString(Constants.KEY_NAME));
                                        user.setEmail(userDoc.getString(Constants.KEY_EMAIL));
                                        users.add(user);
                                        if (users.size() > 0) {
                                            UsersAdapter usersAdapter = new UsersAdapter(users, this::onUserClicked);
                                            binding.usersRecyclerView.setAdapter(usersAdapter);
                                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                                        } else {
                                            showErrorMessage();
                                        }
                                    });
                        }
                    }
                });
    }

    @Override
    public void onUserClicked(UserModel user) {
        if (user == null) return;

        String imageString = getArguments() != null ? getArguments().getString("modelImage") : null;
        if (imageString == null) {
            showToast("No outfit to share");
            return;
        }

        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, user.getId());
        message.put(Constants.KEY_MESSAGE, "");
        message.put(Constants.KEY_IMAGE, imageString);
        message.put(Constants.KEY_TIMESTAMP, new Date());

        FirebaseFirestore.getInstance()
                .collection(Constants.KEY_COLLECTION_CHAT)
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    showToast("Outfit shared successfully!");
                    if (getActivity() != null) {
                        requireActivity().onBackPressed();
                    }
                })
                .addOnFailureListener(e -> showToast("Failed to share outfit: " + e.getMessage()));
    }

    // Phương thức helper để hiển thị Toast an toàn
    private void showToast(String message) {
        if (safeContext != null) {
            Toast.makeText(safeContext, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "Invalid User"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireActivity().getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        binding = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        safeContext = null; // Xóa tham chiếu khi fragment detach
    }
}