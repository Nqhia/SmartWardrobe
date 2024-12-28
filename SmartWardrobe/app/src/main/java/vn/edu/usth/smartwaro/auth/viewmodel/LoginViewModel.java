package vn.edu.usth.smartwaro.auth.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import vn.edu.usth.smartwaro.auth.AuthCallback;
import vn.edu.usth.smartwaro.auth.model.UserModel;
import vn.edu.usth.smartwaro.auth.repository.AuthRepository;
import vn.edu.usth.smartwaro.utils.PreferenceManager;
import vn.edu.usth.smartwaro.auth.utils.Resource;

public class LoginViewModel extends AndroidViewModel {
    private final MutableLiveData<Resource<UserModel>> loginResult = new MutableLiveData<>();
    private final AuthRepository authRepository;
    private final PreferenceManager preferenceManager;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository();
        preferenceManager = new PreferenceManager(application);
    }

    public void login(String email, String password) {
        loginResult.setValue(Resource.loading());

        authRepository.signIn(email, password, new AuthCallback() {
            @Override
            public void onSuccess(UserModel user) {
                loginResult.setValue(Resource.success(user));
            }

            @Override
            public void onError(String error) {
                loginResult.setValue(Resource.error(error));
            }
        });
    }

    public LiveData<Resource<UserModel>> getLoginResult() {
        return loginResult;
    }
}
