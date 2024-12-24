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


}