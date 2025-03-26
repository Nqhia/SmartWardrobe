package vn.edu.usth.smartwaro.wardrobe;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.network.FlaskNetwork;

public class MaleFragment extends BaseWardrobeFragment {

    private ImageView topMale, botMale, footwareMale;
    private ImageButton buttontopTanktop, buttontopPolo, buttontopSw, buttontopBlazerMale, buttontopBreast,
            buttonbotJeanMale, buttonbotPant, buttonloafermale, buttonChangeSkinColor;
    private Button buttonMyCloset;
    private ImageButton removeButton;
    private ScaleGestureDetector scaleGestureDetector;
    private FlaskNetwork flaskNetwork;
    private List<CustomClothingItem> activeClothingItems = new ArrayList<>();
    private CustomClothingItem selectedClothingItem = null;

    // Đồng bộ với drawable trong XML
    private int[] shirtImages = {
            R.drawable.top_blazer_male,    // top_blazer_male_icon
            R.drawable.top_breast,         // top_breast_icon
            R.drawable.top_sweater,        // top_sweater_male_icon
            R.drawable.top_polo,           // top_polo_icon
            R.drawable.top_tanktop_male    // top_tanktop_male_icon
    };
    private int[] pantsImages = {
            R.drawable.bot_jean_male,      // bot_jean_male_icon
            R.drawable.bot_pant            // bot_pant_icon
    };
    private int[] footwareImages = {
            R.drawable.male_footware       // male_loafer_icon (đổi nếu cần)
    };

    private int currentModelSkinIndex = 0;

    private class CustomClothingItem {
        ImageView imageView;
        String imageUrl;
        float scaleFactor = 1.0f;
        float xPosition = 0f, yPosition = 0f;
        private float lastTouchX, lastTouchY;
        private boolean isMoving = false;

        CustomClothingItem(String imageUrl, ViewGroup modelContainer) {
            this.imageUrl = imageUrl;
            this.imageView = new ImageView(requireContext());
            this.imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            modelContainer.addView(this.imageView);
            setupTouchListeners();
        }

        private void setupTouchListeners() {
            this.imageView.setOnTouchListener((v, event) -> {
                scaleGestureDetector.onTouchEvent(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        selectClothingItem(this);
                        lastTouchX = event.getRawX();
                        lastTouchY = event.getRawY();
                        isMoving = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!scaleGestureDetector.isInProgress() && isMoving && this == selectedClothingItem) {
                            float dx = event.getRawX() - lastTouchX;
                            float dy = event.getRawY() - lastTouchY;
                            v.setX(v.getX() + dx);
                            v.setY(v.getY() + dy);
                            xPosition = v.getX();
                            yPosition = v.getY();
                            lastTouchX = event.getRawX();
                            lastTouchY = event.getRawY();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        isMoving = false;
                        break;
                }
                return true;
            });
        }

        void loadImage() {
            Picasso.get().load(FlaskNetwork.BASE_URL + "/" + imageUrl).into(imageView);
            imageView.setScaleX(1.0f);
            imageView.setScaleY(1.0f);
            imageView.setX(0);
            imageView.setY(0);
            scaleFactor = 1.0f;
        }

        void setVisibility(int visibility) {
            imageView.setVisibility(visibility);
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (selectedClothingItem != null) {
                selectedClothingItem.scaleFactor *= detector.getScaleFactor();
                selectedClothingItem.scaleFactor = Math.max(0.5f, Math.min(selectedClothingItem.scaleFactor, 3.0f));
                selectedClothingItem.imageView.setScaleX(selectedClothingItem.scaleFactor);
                selectedClothingItem.imageView.setScaleY(selectedClothingItem.scaleFactor);
                return true;
            }
            return false;
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_male;
    }

    @Override
    protected int getModelViewId() {
        return R.id.model_male;
    }

    @Override
    protected void setupOverlayScrollViews(View view) {
        overlayScrollViews = new ScrollView[]{
                view.findViewById(R.id.overlay_scroll_view_tops_male),
                view.findViewById(R.id.overlay_scroll_view_bot_male),
                view.findViewById(R.id.overlay_scroll_view_footware_male),
                view.findViewById(R.id.overlay_scroll_view_skin_tone_male)
        };
        view.findViewById(R.id.type_top_male).setOnClickListener(v -> toggleOverlay(0));
        view.findViewById(R.id.type_bot_male).setOnClickListener(v -> toggleOverlay(1));
        view.findViewById(R.id.type_footware_male).setOnClickListener(v -> toggleOverlay(2));
        view.findViewById(R.id.type_skin_male).setOnClickListener(v -> toggleOverlay(3));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setupClothingListeners(View view) {
        flaskNetwork = new FlaskNetwork();

        topMale = view.findViewById(R.id.top_male);
        botMale = view.findViewById(R.id.bot_male);
        footwareMale = view.findViewById(R.id.male_footwear);
        removeButton = view.findViewById(R.id.remove_button);

        buttontopBlazerMale = view.findViewById(R.id.top_blazer_male_icon);
        buttontopBreast = view.findViewById(R.id.top_breast_icon);
        buttontopSw = view.findViewById(R.id.top_sweater_male_icon);
        buttontopPolo = view.findViewById(R.id.top_polo_icon);
        buttontopTanktop = view.findViewById(R.id.top_tanktop_male_icon);
        buttonbotJeanMale = view.findViewById(R.id.bot_jean_male_icon);
        buttonbotPant = view.findViewById(R.id.bot_pant_icon);
        buttonloafermale = view.findViewById(R.id.male_loafer_icon);
        buttonChangeSkinColor = view.findViewById(R.id.buttonChangeSkinColorMale);
        buttonMyCloset = view.findViewById(R.id.button_my_closet);

        scaleGestureDetector = new ScaleGestureDetector(requireContext(), new ScaleListener());

        // Clothing listeners
        buttontopBlazerMale.setOnClickListener(v -> {
            updateClothingSafely(topMale, shirtImages, 0);
            hideAllCustomClothing();
        });
        buttontopBreast.setOnClickListener(v -> {
            updateClothingSafely(topMale, shirtImages, 1);
            hideAllCustomClothing();
        });
        buttontopSw.setOnClickListener(v -> {
            updateClothingSafely(topMale, shirtImages, 2);
            hideAllCustomClothing();
        });
        buttontopPolo.setOnClickListener(v -> {
            updateClothingSafely(topMale, shirtImages, 3);
            hideAllCustomClothing();
        });
        buttontopTanktop.setOnClickListener(v -> {
            updateClothingSafely(topMale, shirtImages, 4);
            hideAllCustomClothing();
        });

        buttonbotJeanMale.setOnClickListener(v -> {
            updateClothingSafely(botMale, pantsImages, 0);
            hideAllCustomClothing();
        });
        buttonbotPant.setOnClickListener(v -> {
            updateClothingSafely(botMale, pantsImages, 1);
            hideAllCustomClothing();
        });

        buttonloafermale.setOnClickListener(v -> updateClothingSafely(footwareMale, footwareImages, 0));

        buttonChangeSkinColor.setOnClickListener(v -> {
            currentModelSkinIndex = (currentModelSkinIndex + 1) % 3;
            int[] skinImages = {R.drawable.male, R.drawable.male_yellow_skin, R.drawable.male_dark_skin};
            try {
                modelView.setImageResource(skinImages[currentModelSkinIndex]);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error changing skin tone", Toast.LENGTH_SHORT).show();
            }
        });

        buttonMyCloset.setOnClickListener(v -> showMyClosetDialog());

        removeButton.setOnClickListener(v -> {
            if (selectedClothingItem != null) {
                activeClothingItems.remove(selectedClothingItem);
                modelContainer.removeView(selectedClothingItem.imageView);
                selectClothingItem(null);
            }
        });
    }

    // Phương thức an toàn để cập nhật quần áo, tránh crash
    private void updateClothingSafely(ImageView view, int[] images, int index) {
        try {
            if (index >= 0 && index < images.length) {
                view.setImageResource(images[index]);
                view.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(requireContext(), "Invalid clothing index", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error loading clothing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void selectClothingItem(CustomClothingItem item) {
        selectedClothingItem = item;
        removeButton.setVisibility(item != null ? View.VISIBLE : View.GONE);
    }

    private void hideAllCustomClothing() {
        for (CustomClothingItem item : activeClothingItems) {
            item.setVisibility(View.GONE);
        }
        activeClothingItems.clear();
        selectClothingItem(null);
    }

    private void showMyClosetDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_my_closet);
        dialog.setTitle("My Closet");

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.9);
        params.height = (int) (requireContext().getResources().getDisplayMetrics().heightPixels * 0.8);
        dialog.getWindow().setAttributes(params);

        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerViewCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<FlaskNetwork.Category> categoryList = new ArrayList<>();
        CategoryAdapter categoryAdapter = new CategoryAdapter(categoryList, (category, imageUrl) -> {
            CustomClothingItem clothingItem = new CustomClothingItem(imageUrl, modelContainer);
            activeClothingItems.add(clothingItem);
            clothingItem.loadImage();
            clothingItem.setVisibility(View.VISIBLE);

            switch (category.toLowerCase()) {
                case "top": case "long sleeves": case "short sleeves":
                    topMale.setVisibility(View.GONE);
                    break;
                case "bottom": case "long leggings": case "short leggings":
                    botMale.setVisibility(View.GONE);
                    break;
            }
            dialog.dismiss();
        });
        recyclerView.setAdapter(categoryAdapter);

        flaskNetwork.getAllUserClothes(new FlaskNetwork.OnAllClothesLoadedListener() {
            @Override
            public void onSuccess(List<FlaskNetwork.Category> categories) {
                requireActivity().runOnUiThread(() -> {
                    List<FlaskNetwork.Category> filteredCategories = new ArrayList<>();
                    for (FlaskNetwork.Category category : categories) {
                        if (category.images != null && category.images.length > 0) {
                            filteredCategories.add(category);
                        }
                    }
                    categoryList.clear();
                    categoryList.addAll(filteredCategories);
                    categoryAdapter.notifyDataSetChanged();
                    if (filteredCategories.isEmpty()) {
                        Toast.makeText(getContext(), "No clothes found in your closet", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "Error loading closet: " + message, Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        Button closeButton = dialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}