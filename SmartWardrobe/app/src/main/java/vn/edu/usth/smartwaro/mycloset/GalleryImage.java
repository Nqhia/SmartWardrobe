package vn.edu.usth.smartwaro.mycloset;

import static vn.edu.usth.smartwaro.network.FlaskNetwork.BASE_URL;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class GalleryImage implements Parcelable {
    @SerializedName("filename")
    private  String filename;

    @SerializedName("originalFilename")
    private String originalFilename;

    @SerializedName("uploadDate")
    private String uploadDate;

    @SerializedName("url")
    private String url;

    @SerializedName("category")
    private String category;

    private boolean isSelected;
    private boolean isFavorite;

    // Default constructor (bắt buộc cho Gson)
    public GalleryImage() {
    }

    public GalleryImage(@NonNull String filename,
                        @NonNull String originalFilename,
                        @Nullable String uploadDate,
                        @NonNull String url,
                        @Nullable String category) {    // Added category parameter
        this.filename = filename;
        this.originalFilename = originalFilename;
        this.uploadDate = uploadDate != null ? uploadDate : "";
        this.url = url;
        this.category = category != null ? category : "";    // Default to empty string if null
        this.isSelected = false;
        this.isFavorite = false;
    }

    protected GalleryImage(Parcel in) {
        filename = in.readString();
        originalFilename = in.readString();
        uploadDate = in.readString();
        url = in.readString();
        category = in.readString();    // Read category from parcel
        isSelected = in.readByte() != 0;
        isFavorite = in.readByte() != 0;
    }

    public static final Creator<GalleryImage> CREATOR = new Creator<GalleryImage>() {
        @Override
        public GalleryImage createFromParcel(Parcel in) {
            return new GalleryImage(in);
        }

        @Override
        public GalleryImage[] newArray(int size) {
            return new GalleryImage[size];
        }
    };

    @NonNull
    public String getFilename() { return filename; }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @NonNull
    public String getOriginalFilename() { return originalFilename; }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    @NonNull
    public String getUploadDate() { return uploadDate; }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    @NonNull
    public String getUrl() { return url; }

    @NonNull
    public String getCategory() { return category; }    // Added getter for category

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public boolean isSelected() { return isSelected; }

    public void setSelected(boolean selected) { isSelected = selected; }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(filename);
        dest.writeString(originalFilename);
        dest.writeString(uploadDate);
        dest.writeString(url);
        dest.writeString(category);    // Write category to parcel
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeByte((byte) (isFavorite ? 1 : 0));
    }

    @Override
    public String toString() {
        return "GalleryImage{" +
                "filename='" + filename + '\'' +
                ", originalFilename='" + originalFilename + '\'' +
                ", uploadDate='" + uploadDate + '\'' +
                ", url='" + url + '\'' +
                ", category='" + category + '\'' +
                ", isSelected=" + isSelected + '\'' +
                ", isFavorite=" + isFavorite +// Added category to toString
                '}';
    }
}
