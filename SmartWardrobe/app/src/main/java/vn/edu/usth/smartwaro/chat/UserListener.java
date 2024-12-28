package vn.edu.usth.smartwaro.chat;

import vn.edu.usth.smartwaro.auth.model.UserModel;

public interface UserListener {
    void onUserClicked(UserModel user);
}
