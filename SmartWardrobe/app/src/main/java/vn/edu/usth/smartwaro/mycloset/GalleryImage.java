package vn.edu.usth.smartwaro.mycloset;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GalleryImage {
    private final String filename;
    private final String originalFilename;
    private final String uploadDate;
    private final String url;

    public GalleryImage(@NonNull String filename,
                        @NonNull String originalFilename,
                        @Nullable String uploadDate,
                        @NonNull String url) {
        this.filename = filename != null ? filename : "";
        this.originalFilename = originalFilename != null ? originalFilename : "";
        this.uploadDate = uploadDate != null ? uploadDate : "";
        this.url = url != null ? url : "";
    }

    @NonNull
    public String getFilename() { return filename; }

    @NonNull
    public String getOriginalFilename() { return originalFilename; }

    @NonNull
    public String getUploadDate() { return uploadDate; }

    @NonNull
    public String getUrl() { return url; }

    @Override
    public String toString() {
        return "GalleryImage{" +
                "filename='" + filename + '\'' +
                ", originalFilename='" + originalFilename + '\'' +
                ", uploadDate='" + uploadDate + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}