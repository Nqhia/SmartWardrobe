package vn.edu.usth.smartwaro.mycloset;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GalleryImage implements Parcelable {
    private final String filename;
    private final String originalFilename;
    private final String uploadDate;
    private final String url;
    private final String category;    // Added category field
    private boolean isSelected;

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
    }

    protected GalleryImage(Parcel in) {
        filename = in.readString();
        originalFilename = in.readString();
        uploadDate = in.readString();
        url = in.readString();
        category = in.readString();    // Read category from parcel
        isSelected = in.readByte() != 0;
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

    @NonNull
    public String getOriginalFilename() { return originalFilename; }

    @NonNull
    public String getUploadDate() { return uploadDate; }

    @NonNull
    public String getUrl() { return url; }

    @NonNull
    public String getCategory() { return category; }    // Added getter for category

    public boolean isSelected() { return isSelected; }

    public void setSelected(boolean selected) { isSelected = selected; }

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
    }

    @Override
    public String toString() {
        return "GalleryImage{" +
                "filename='" + filename + '\'' +
                ", originalFilename='" + originalFilename + '\'' +
                ", uploadDate='" + uploadDate + '\'' +
                ", url='" + url + '\'' +
                ", category='" + category + '\'' +    // Added category to toString
                '}';
    }
}
