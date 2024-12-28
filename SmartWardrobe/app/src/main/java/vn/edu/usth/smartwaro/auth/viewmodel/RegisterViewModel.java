package vn.edu.usth.smartwaro.auth.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import vn.edu.usth.smartwaro.auth.AuthCallback;
import vn.edu.usth.smartwaro.auth.model.UserModel;
import vn.edu.usth.smartwaro.auth.repository.AuthRepository;
import vn.edu.usth.smartwaro.auth.utils.Resource;

public class RegisterViewModel extends ViewModel {
    private final MutableLiveData<Resource<UserModel>> registerResult = new MutableLiveData<>();
    private final AuthRepository authRepository;

    public RegisterViewModel() {
        authRepository = new AuthRepository();
    }

    public void register(String username, String email, String password) {
        registerResult.setValue(Resource.loading());

        authRepository.register(username, email, password, new AuthCallback() {
            @Override
            public void onSuccess(UserModel user) {
                registerResult.setValue(Resource.success(user));
            }

            @Override
            public void onError(String error) {
                registerResult.setValue(Resource.error(error));
            }
        });
    }

    public LiveData<Resource<UserModel>> getRegisterResult() {
        return registerResult;
    }
}
