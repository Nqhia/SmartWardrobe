package vn.edu.usth.smartwaro.wardrobe;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import androidx.fragment.app.Fragment;
import java.io.ByteArrayOutputStream;
import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.chat.ShareUsersFragment;

public abstract class BaseWardrobeFragment extends Fragment {

    protected ImageView modelView;
    protected ScrollView primaryScrollView;
    protected ScrollView[] overlayScrollViews;
    protected Button buttonShowFriends;
    protected ViewGroup modelContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResourceId(), container, false);

        modelView = view.findViewById(getModelViewId());
        primaryScrollView = view.findViewById(R.id.primary_scroll_view);
        modelContainer = view.findViewById(R.id.model_container);
        buttonShowFriends = view.findViewById(R.id.button_show_friends);

        setupOverlayScrollViews(view);

        view.setOnTouchListener((v, event) -> {
            for (ScrollView scrollView : overlayScrollViews) {
                if (scrollView.getVisibility() == View.VISIBLE) {
                    scrollView.setVisibility(View.GONE);
                    primaryScrollView.setVisibility(View.VISIBLE);
                    return true;
                }
            }
            return false;
        });

        setListeners();
        setupClothingListeners(view);

        return view;
    }

    protected abstract int getLayoutResourceId();

    protected abstract int getModelViewId();

    protected abstract void setupOverlayScrollViews(View view);

    protected abstract void setupClothingListeners(View view);

    protected void toggleOverlay(int index) {
        primaryScrollView.setVisibility(View.GONE);
        for (int i = 0; i < overlayScrollViews.length; i++) {
            overlayScrollViews[i].setVisibility(i == index ? View.VISIBLE : View.GONE);
        }
    }

    protected void updateClothing(ImageView view, int[] images, int index) {
        view.setImageResource(images[index]);
        view.setVisibility(View.VISIBLE);
    }

    private void setListeners() {
        buttonShowFriends.setOnClickListener(v -> {
            modelContainer.setDrawingCacheEnabled(true);
            Bitmap modelImage = Bitmap.createBitmap(modelContainer.getDrawingCache());
            modelContainer.setDrawingCacheEnabled(false);

            String imageString = bitmapToString(modelImage);
            Bundle bundle = new Bundle();
            bundle.putString("modelImage", imageString);

            ShareUsersFragment shareUsersFragment = new ShareUsersFragment();
            shareUsersFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, shareUsersFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    protected String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}