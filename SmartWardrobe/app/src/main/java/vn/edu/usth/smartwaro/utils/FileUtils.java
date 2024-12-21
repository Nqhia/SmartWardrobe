package vn.edu.usth.smartwaro.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    private static final String TAG = "FileUtils";

    public static File getFileFromUri(Context context, Uri uri) {
        try {
            String fileName = getFileName(context, uri);
            File file = new File(context.getCacheDir(), fileName);

            try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
                 OutputStream outputStream = new FileOutputStream(file)) {

                if (inputStream == null) {
                    return null;
                }

                byte[] buffer = new byte[4 * 1024]; // 4kb buffer
                int read;

                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }

                outputStream.flush();
                return file;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error copying file: " + e.getMessage());
            return null;
        }
    }

    private static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return "upload_" + System.currentTimeMillis() + "_" + result;
    }

    public static void clearCache(Context context) {
        File cacheDir = context.getCacheDir();
        if (cacheDir != null && cacheDir.isDirectory()) {
            try {
                for (File child : cacheDir.listFiles()) {
                    if (child.getName().startsWith("upload_") ||
                            child.getName().startsWith("processed_")) {
                        child.delete();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error clearing cache: " + e.getMessage());
            }
        }
    }
}