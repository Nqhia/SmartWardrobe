package vn.edu.usth.smartwaro.auth;

import vn.edu.usth.smartwaro.auth.model.UserModel;

public interface AuthCallback {
    void onSuccess(UserModel user);
    void onError(String error);
}
