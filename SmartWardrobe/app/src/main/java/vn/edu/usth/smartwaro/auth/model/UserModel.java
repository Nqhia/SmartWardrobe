package vn.edu.usth.smartwaro.auth.model;

import com.google.firebase.Timestamp;

import java.util.Date;


import com.google.firebase.Timestamp;
import java.io.Serializable;
import java.util.Date;

public class UserModel implements Serializable {
    private String id;
    private String name;
    private String email;
    public String image;
    private boolean isDefaultImage;
    private String avatarUrl;
    public String token;
    private Timestamp createdAt;
    private Timestamp lastLogin;

    public UserModel() {}

    public UserModel(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.image = image;
        this.createdAt = new Timestamp(new Date());
        this.lastLogin = new Timestamp(new Date());
    }

    // Getters and Setters với null check
    public String getId() {
        return id != null ? id : "";
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email != null ? email : "";
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getToken() {
        return token != null ? token : "";
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getImage() {
        return image != null ? image : "";
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAvatarUrl() {
        return avatarUrl != null ? avatarUrl : "";
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Timestamp getCreatedAt() {
        return createdAt != null ? createdAt : new Timestamp(new Date());
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getLastLogin() {
        return lastLogin != null ? lastLogin : new Timestamp(new Date());
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    // Override toString() để dễ debug
    @Override
    public String toString() {
        return "UserModel{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", token='" + getToken() + '\'' +
                '}';
    }

    // Override equals() và hashCode() nếu cần so sánh objects
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserModel userModel = (UserModel) o;
        return getId().equals(userModel.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    public void setDefaultImage(boolean isDefaultImage) { this.isDefaultImage = isDefaultImage; }

}
