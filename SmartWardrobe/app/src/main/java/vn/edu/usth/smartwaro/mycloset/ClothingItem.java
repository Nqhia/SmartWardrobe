package vn.edu.usth.smartwaro.mycloset;

import android.net.Uri;

public class ClothingItem {
    private String clothingName;  // Name of clothing item
    private Uri imageUri;         // URI of the image (from camera)
    private int imageRes;         // Image from resources (drawable)
    private String category;      // Category of clothing item

    // Constructor with URI and category
    public ClothingItem(String clothingName, Uri imageUri, String category) {
        this.clothingName = clothingName;
        this.imageUri = imageUri;
        this.category = category;
    }

    // Constructor with URI (backward compatibility)
    public ClothingItem(String clothingName, Uri imageUri) {
        this(clothingName, imageUri, "uncategorized");
    }

    // Getters and setters
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
