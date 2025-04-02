package vn.edu.usth.smartwaro.wardrobe;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
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
import androidx.fragment.app.FragmentTransaction;
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

    private int[] shirtImages = {
            R.drawable.top_blazer_male,
            R.drawable.top_breast,
            R.drawable.top_sweater,
            R.drawable.top_polo,
            R.drawable.top_tanktop_male
    };
    private int[] pantsImages = {
            R.drawable.bot_jean_male,
            R.drawable.bot_pant
    };
    private int[] footwareImages = {
            R.drawable.male_footware
    };

    private final String[] baseProductLinks = {
            "https://www2.hm.com/en_us/productpage.1236723003.html",
            "https://www2.hm.com/en_us/productpage.1250094001.html",
            "https://www2.hm.com/en_us/productpage.1232261002.html",
            "https://www2.hm.com/en_us/productpage.0956343001.html",
            "https://www2.hm.com/en_us/productpage.1272904001.html",
            "https://www2.hm.com/en_us/productpage.1096385014.html",
            "https://www2.hm.com/en_us/productpage.1253395001.html",
            "https://www2.hm.com/en_us/productpage.1250662001.html"
    };
    private final String affiliateCode = "?affiliate_id=my_swaro";

    private final int[] buyButtonIds = {
            R.id.buy_button_blazer_male,
            R.id.buy_button_breast,
            R.id.buy_button_sweater_male,
            R.id.buy_button_polo,
            R.id.buy_button_tanktop,
            R.id.buy_button_jean_male,
            R.id.buy_button_pant,
            R.id.buy_button_loafer_male
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

        hideAllBuyButtons(view);

        buttontopBlazerMale.setOnClickListener(v -> handleClothingClick(topMale, shirtImages, 0));
        buttontopBreast.setOnClickListener(v -> handleClothingClick(topMale, shirtImages, 1));
        buttontopSw.setOnClickListener(v -> handleClothingClick(topMale, shirtImages, 2));
        buttontopPolo.setOnClickListener(v -> handleClothingClick(topMale, shirtImages, 3));
        buttontopTanktop.setOnClickListener(v -> handleClothingClick(topMale, shirtImages, 4));

        buttonbotJeanMale.setOnClickListener(v -> handleClothingClick(botMale, pantsImages, 0));
        buttonbotPant.setOnClickListener(v -> handleClothingClick(botMale, pantsImages, 1));

        buttonloafermale.setOnClickListener(v -> handleClothingClick(footwareMale, footwareImages, 0));

        setupBuyButtons(view);

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

    private void handleClothingClick(ImageView view, int[] images, int index) {
        updateClothingSafely(view, images, index);
        hideAllCustomClothing();
        hideAllBuyButtons(requireView());
        int affiliateIndex = getAffiliateIndex(view, index);
        ImageButton buyButton = requireView().findViewById(buyButtonIds[affiliateIndex]);
        if (buyButton != null) {
            buyButton.setVisibility(View.VISIBLE);
        }
    }

    private void setupBuyButtons(View view) {
        for (int i = 0; i < buyButtonIds.length; i++) {
            int index = i;
            ImageButton buyButton = view.findViewById(buyButtonIds[index]);
            if (buyButton != null) {
                buyButton.setOnClickListener(v -> openAffiliateFragment(index));
            }
        }
    }

    private void hideAllBuyButtons(View view) {
        for (int buyButtonId : buyButtonIds) {
            ImageButton buyButton = view.findViewById(buyButtonId);
            if (buyButton != null) {
                buyButton.setVisibility(View.GONE);
            }
        }
    }

    private int getAffiliateIndex(ImageView view, int index) {
        if (view == topMale) return index; // Top: 0-4
        if (view == botMale) return 5 + index; // Bot: 5-6
        if (view == footwareMale) return 7; // Footware: 7
        return -1;
    }

    private void openAffiliateFragment(int index) {
        if (index >= 0 && index < baseProductLinks.length) {
            String fullUrl = baseProductLinks[index] + affiliateCode;
            AffiliateFragment fragment = new AffiliateFragment();
            Bundle bundle = new Bundle();
            bundle.putString("affiliate_url", fullUrl);
            bundle.putInt("product_image", getProductImage(index));
            fragment.setArguments(bundle);

            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private int getProductImage(int index) {
        if (index < 5) return shirtImages[index];
        else if (index < 7) return pantsImages[index - 5];
        else return footwareImages[0];
    }

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