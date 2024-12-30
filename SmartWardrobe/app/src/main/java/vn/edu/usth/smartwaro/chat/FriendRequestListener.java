package vn.edu.usth.smartwaro.chat;

import vn.edu.usth.smartwaro.auth.model.FriendRequest;

public interface FriendRequestListener {
    void onAcceptClicked(FriendRequest request);
    void onRejectClicked(FriendRequest request);
}
