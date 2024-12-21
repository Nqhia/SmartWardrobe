package vn.edu.usth.smartwaro.mycloset;

import android.net.Uri;

public class ClothingItem {

    private String clothingName;  // Tên quần áo (Ví dụ: "Áo thun", "Quần jean", v.v.)
    private Uri imageUri;         // URI của ảnh (chụp từ camera)
    private int imageRes;         // Ảnh từ tài nguyên (drawable)

    // Constructor với URI (dành cho ảnh chụp từ camera)
    public ClothingItem(String clothingName, Uri imageUri) {
        this.clothingName = clothingName;
        this.imageUri = imageUri;
    }

    // Constructor với imageRes (dành cho ảnh từ tài nguyên drawable)
    public ClothingItem(String clothingName, int imageRes) {
        this.clothingName = clothingName;
        this.imageRes = imageRes;
    }

    // Getter và Setter cho clothingName
    public String getClothingName() {
        return clothingName;
    }

    public void setClothingName(String clothingName) {
        this.clothingName = clothingName;
    }

    // Getter và Setter cho imageUri
    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    // Getter và Setter cho imageRes
    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    // Kiểm tra xem có imageUri hay không (sử dụng cho ảnh đã chụp)
    public boolean hasImageUri() {
        return imageUri != null;
    }

    // Kiểm tra xem có imageRes hay không (sử dụng cho ảnh trong tài nguyên drawable)
    public boolean hasImageRes() {
        return imageRes != 0;
    }
}
