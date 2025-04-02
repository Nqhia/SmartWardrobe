package vn.edu.usth.smartwaro.mycloset;

import android.net.Uri;

public class ClothingItem {
    private String clothingName;
    private Uri imageUri;
    private int imageRes;
    private String category;

    public ClothingItem(String clothingName, Uri imageUri, String category) {
        this.clothingName = clothingName;
        this.imageUri = imageUri;
        this.category = category;
    }

    public ClothingItem(String clothingName, Uri imageUri) {
        this(clothingName, imageUri, "uncategorized");
    }

    public String getClothingName() {
        return clothingName;
    }

    public void setClothingName(String clothingName) {
        this.clothingName = clothingName;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
