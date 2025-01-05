package vn.edu.usth.smartwaro.auth.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Map;

import vn.edu.usth.smartwaro.auth.AuthCallback;
import vn.edu.usth.smartwaro.auth.model.UserModel;
import vn.edu.usth.smartwaro.auth.repository.AuthRepository;
import vn.edu.usth.smartwaro.auth.utils.Resource;

public class UpdateProfileViewModel extends ViewModel {
    private final MutableLiveData<Resource<UserModel>> updateResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<UserModel>> profileData = new MutableLiveData<>();
    private final AuthRepository authRepository;

    public UpdateProfileViewModel() {
        authRepository = new AuthRepository();
    }

    public void loadUserProfile() {
        profileData.setValue(Resource.loading());
        authRepository.getCurrentUser(new AuthCallback() {
            @Override
            public void onSuccess(UserModel user) {
                profileData.setValue(Resource.success(user));
            }

            @Override
            public void onError(String error) {
                profileData.setValue(Resource.error(error));
            }
        });
    }

    public void updateProfile(String userId, Map<String, Object> updates) {
        updateResult.setValue(Resource.loading());
        authRepository.updateProfile(userId, updates, new AuthCallback() {
            @Override
            public void onSuccess(UserModel user) {
                updateResult.setValue(Resource.success(user));
            }

            @Override
            public void onError(String error) {
                updateResult.setValue(Resource.error(error));
            }
        });
    }

    public String getCurrentUserId() {
        return authRepository.getCurrentUserId();
    }

    public LiveData<Resource<UserModel>> getProfileData() {
        return profileData;
    }

    public LiveData<Resource<UserModel>> getUpdateResult() {
        return updateResult;
    }
}
