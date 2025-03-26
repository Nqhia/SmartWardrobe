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

public class FemaleFragment extends BaseWardrobeFragment {

    private ImageView topFemale, botFemale, tubedress, footwareFemale;
    private ImageButton buttontopBlazer, buttontopCar, buttontopTank, buttontopSweater, buttontopMock,
            buttonbotJean, buttonbotLegging, buttonbotSkirt,
            buttontubepurple, buttontubeblack, buttontubered, buttonloafer, buttonheels, buttonChangeSkinColor;
    private Button buttonMyCloset;
    private ImageButton removeButton;
    private ScaleGestureDetector scaleGestureDetector;
    private FlaskNetwork flaskNetwork;
    private List<CustomClothingItem> activeClothingItems = new ArrayList<>();
    private CustomClothingItem selectedClothingItem = null;

    private int[] shirtImages = {
            R.drawable.top_blazer, R.drawable.top_cardigan, R.drawable.top_tanktop,
            R.drawable.top_sweater_turtleneck, R.drawable.top_tshirt_mockneck
    };
    private int[] pantsImages = {R.drawable.bot_jean, R.drawable.bot_legging, R.drawable.bot_skirt};
    private int[] dressImages = {R.drawable.tube_purple, R.drawable.dress_tube, R.drawable.dress_red};
    private int[] footwareImages = {R.drawable.loafer, R.drawable.heels};

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
        return R.layout.fragment_female;
    }

    @Override
    protected int getModelViewId() {
        return R.id.model_female;
    }

    @Override
    protected void setupOverlayScrollViews(View view) {
        overlayScrollViews = new ScrollView[]{
                view.findViewById(R.id.overlay_scroll_view_tops),
                view.findViewById(R.id.overlay_scroll_view_bot),
                view.findViewById(R.id.overlay_scroll_view_skirt),
                view.findViewById(R.id.overlay_scroll_view_dress),
                view.findViewById(R.id.overlay_scroll_view_footware),
                view.findViewById(R.id.overlay_scroll_view_skin_tone)
        };
        view.findViewById(R.id.type_top_female).setOnClickListener(v -> toggleOverlay(0));
        view.findViewById(R.id.type_bot_female).setOnClickListener(v -> toggleOverlay(1));
        view.findViewById(R.id.type_skirt_female).setOnClickListener(v -> toggleOverlay(2));
        view.findViewById(R.id.type_dress_female).setOnClickListener(v -> toggleOverlay(3));
        view.findViewById(R.id.type_footware_female).setOnClickListener(v -> toggleOverlay(4));
        view.findViewById(R.id.type_skin_female).setOnClickListener(v -> toggleOverlay(5));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setupClothingListeners(View view) {
        flaskNetwork = new FlaskNetwork();

        topFemale = view.findViewById(R.id.top_female);
        botFemale = view.findViewById(R.id.bot_female);
        tubedress = view.findViewById(R.id.tube_dress);
        footwareFemale = view.findViewById(R.id.female_footwear);
        removeButton = view.findViewById(R.id.remove_button);

        buttontopBlazer = view.findViewById(R.id.top_blazer_icon);
        buttontopCar = view.findViewById(R.id.top_cardigan_icon);
        buttontopTank = view.findViewById(R.id.top_tanktop_icon);
        buttontopSweater = view.findViewById(R.id.top_sweater_icon);
        buttontopMock = view.findViewById(R.id.top_mockneck_icon);
        buttonbotJean = view.findViewById(R.id.bot_jeans_icon);
        buttonbotLegging = view.findViewById(R.id.bot_legging_icon);
        buttonbotSkirt = view.findViewById(R.id.bot_skirt_icon);
        buttontubepurple = view.findViewById(R.id.tube_purple_icon);
        buttontubeblack = view.findViewById(R.id.black_tube_icon);
        buttontubered = view.findViewById(R.id.tube_red_icon);
        buttonloafer = view.findViewById(R.id.female_loafer_icon);
        buttonheels = view.findViewById(R.id.female_heels_icon);
        buttonChangeSkinColor = view.findViewById(R.id.buttonChangeSkinColor);
        buttonMyCloset = view.findViewById(R.id.button_my_closet);

        scaleGestureDetector = new ScaleGestureDetector(requireContext(), new ScaleListener());

        // Clothing listeners
        buttontopBlazer.setOnClickListener(v -> { updateClothing(topFemale, shirtImages, 0); tubedress.setVisibility(View.GONE); hideAllCustomClothing(); });
        buttontopCar.setOnClickListener(v -> { updateClothing(topFemale, shirtImages, 1); tubedress.setVisibility(View.GONE); hideAllCustomClothing(); });
        buttontopTank.setOnClickListener(v -> { updateClothing(topFemale, shirtImages, 2); tubedress.setVisibility(View.GONE); hideAllCustomClothing(); });
        buttontopSweater.setOnClickListener(v -> { updateClothing(topFemale, shirtImages, 3); tubedress.setVisibility(View.GONE); hideAllCustomClothing(); });
        buttontopMock.setOnClickListener(v -> { updateClothing(topFemale, shirtImages, 4); tubedress.setVisibility(View.GONE); hideAllCustomClothing(); });

        buttonbotJean.setOnClickListener(v -> { updateClothing(botFemale, pantsImages, 0); tubedress.setVisibility(View.GONE); hideAllCustomClothing(); });
        buttonbotLegging.setOnClickListener(v -> { updateClothing(botFemale, pantsImages, 1); tubedress.setVisibility(View.GONE); hideAllCustomClothing(); });
        buttonbotSkirt.setOnClickListener(v -> { updateClothing(botFemale, pantsImages, 2); tubedress.setVisibility(View.GONE); hideAllCustomClothing(); });

        buttontubepurple.setOnClickListener(v -> { updateClothing(tubedress, dressImages, 0); topFemale.setVisibility(View.GONE); botFemale.setVisibility(View.GONE); hideAllCustomClothing(); });
        buttontubeblack.setOnClickListener(v -> { updateClothing(tubedress, dressImages, 1); topFemale.setVisibility(View.GONE); botFemale.setVisibility(View.GONE); hideAllCustomClothing(); });
        buttontubered.setOnClickListener(v -> { updateClothing(tubedress, dressImages, 2); topFemale.setVisibility(View.GONE); botFemale.setVisibility(View.GONE); hideAllCustomClothing(); });

        buttonloafer.setOnClickListener(v -> updateClothing(footwareFemale, footwareImages, 0));
        buttonheels.setOnClickListener(v -> updateClothing(footwareFemale, footwareImages, 1));

        buttonChangeSkinColor.setOnClickListener(v -> {
            currentModelSkinIndex = (currentModelSkinIndex + 1) % 7;
            int[] skinImages = {R.drawable.female, R.drawable.female_white_skin, R.drawable.female_nude_skin,
                    R.drawable.female_yellow_skin, R.drawable.female_brown_skin, R.drawable.female_dark_skin, R.drawable.female_black_skin};
            modelView.setImageResource(skinImages[currentModelSkinIndex]);
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
                    topFemale.setVisibility(View.GONE);
                    tubedress.setVisibility(View.GONE);
                    break;
                case "bottom": case "long leggings": case "short leggings":
                    botFemale.setVisibility(View.GONE);
                    tubedress.setVisibility(View.GONE);
                    break;
                case "dress":
                    topFemale.setVisibility(View.GONE);
                    botFemale.setVisibility(View.GONE);
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