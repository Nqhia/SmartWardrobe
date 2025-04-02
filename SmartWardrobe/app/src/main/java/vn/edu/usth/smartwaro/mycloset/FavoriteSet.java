package vn.edu.usth.smartwaro.mycloset;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FavoriteSet {
    @SerializedName("id")
    private String id;  // Thêm trường id

    @SerializedName("set_name")
    private String setName;

    @SerializedName("shirt_images")
    private List<GalleryImage> shirtImages;

    @SerializedName("pant_images")
    private List<GalleryImage> pantImages;

    private List<String> itemFilenames;  // Nếu cần

    public FavoriteSet() {
    }

    public FavoriteSet(String setName, List<String> itemFilenames) {
        this.setName = setName;
        this.itemFilenames = itemFilenames;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public List<GalleryImage> getShirtImages(){
        return shirtImages;
    }

    public List<GalleryImage> getPantsImages(){
        return pantImages;
    }

    public List<String> getItemFilenames() {
        return itemFilenames;
    }

    public void setItemFilenames(List<String> itemFilenames) {
        this.itemFilenames = itemFilenames;
    }
}
