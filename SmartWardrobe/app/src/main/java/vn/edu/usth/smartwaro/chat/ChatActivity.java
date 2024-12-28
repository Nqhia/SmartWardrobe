package vn.edu.usth.smartwaro.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.databinding.ActivityChatBinding;
import vn.edu.usth.smartwaro.utils.Constants;
import vn.edu.usth.smartwaro.utils.PreferenceManager;

public class ChatActivity extends AppCompatActivity {

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
}