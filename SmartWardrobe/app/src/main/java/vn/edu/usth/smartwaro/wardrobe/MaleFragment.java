package vn.edu.usth.smartwaro.wardrobe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;

import vn.edu.usth.smartwaro.R;

public class MaleFragment extends Fragment {

    private ImageView modelMale, topMale, botMale, footwareMale;
    private ImageButton buttontopTanktop, buttontopPolo, buttontopSw, buttontopBlazerMale, buttontopBreast,
            buttonbotJeanMale, buttonbotPant, buttonloafermale, buttonChangeSkinColor;
    private ScrollView primaryScrollView;
    private ScrollView[] overlayScrollViews;

    private int[] shirtImages = {
            R.drawable.top_blazer_male,
            R.drawable.top_breast,
            R.drawable.top_sweater,
            R.drawable.top_polo,
            R.drawable.top_tanktop_male,
    };

    private int[] pantsImages = {
            R.drawable.bot_jean_male,
            R.drawable.bot_pant,
    };

    private int[] footwareImages = {
            R.drawable.male_footware,
    };

    private int currentShirtIndex = 0;
    private int currentPantsIndex = 0;
    private int currentFootwareIndex = 0;
    private int currentModelSkinIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_male, container, false);

        // Initialize ImageViews
        modelMale = view.findViewById(R.id.model_male);
        topMale = view.findViewById(R.id.top_male);
        botMale = view.findViewById(R.id.bot_male);
        footwareMale = view.findViewById(R.id.male_footwear);

        // Initialize Buttons
        buttontopTanktop = view.findViewById(R.id.top_tanktop_male_icon);
        buttontopPolo = view.findViewById(R.id.top_polo_icon);
        buttontopSw = view.findViewById(R.id.top_sweater_male_icon);
        buttontopBlazerMale = view.findViewById(R.id.top_blazer_male_icon);
        buttontopBreast = view.findViewById(R.id.top_breast_icon);

        buttonbotJeanMale = view.findViewById(R.id.bot_jean_male_icon);
        buttonbotPant = view.findViewById(R.id.bot_pant_icon);
        buttonloafermale = view.findViewById(R.id.male_loafer_icon);

        buttonChangeSkinColor = view.findViewById(R.id.buttonChangeSkinColorMale);

        primaryScrollView = view.findViewById(R.id.male_type_scroll_view);

        overlayScrollViews = new ScrollView[]{
                view.findViewById(R.id.overlay_scroll_view_tops_male),
                view.findViewById(R.id.overlay_scroll_view_bot_male),
                view.findViewById(R.id.overlay_scroll_view_footware_male),
                view.findViewById(R.id.overlay_scroll_view_skin_tone_male)
        };

        // Handle click on category buttons
        view.findViewById(R.id.type_top_male).setOnClickListener(v -> toggleOverlay(0));
        view.findViewById(R.id.type_bot_male).setOnClickListener(v -> toggleOverlay(1));
        view.findViewById(R.id.type_footware_male).setOnClickListener(v -> toggleOverlay(2));
        view.findViewById(R.id.type_skin_male).setOnClickListener(v -> toggleOverlay(3));

        // Close overlays when touching outside
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

        // Shirt selection
        buttontopBlazerMale.setOnClickListener(v -> updateClothing(topMale, shirtImages, 0));
        buttontopBreast.setOnClickListener(v -> updateClothing(topMale, shirtImages, 1));
        buttontopSw.setOnClickListener(v -> updateClothing(topMale, shirtImages, 2));
        buttontopPolo.setOnClickListener(v -> updateClothing(topMale, shirtImages, 3));
        buttontopTanktop.setOnClickListener(v -> updateClothing(topMale, shirtImages, 4));

        // Pants selection
        buttonbotJeanMale.setOnClickListener(v -> updateClothing(botMale, pantsImages, 0));
        buttonbotPant.setOnClickListener(v -> updateClothing(botMale, pantsImages, 1));

        // Footwear selection
        buttonloafermale.setOnClickListener(v -> updateClothing(footwareMale, footwareImages, 0));

        // Skin tone change
        buttonChangeSkinColor.setOnClickListener(v -> {
            currentModelSkinIndex = (currentModelSkinIndex + 1) % 3;
            int[] skinImages = {
                    R.drawable.male,
                    R.drawable.male_yellow_skin,
                    R.drawable.male_dark_skin,
            };
            modelMale.setImageResource(skinImages[currentModelSkinIndex]);
        });

        return view;
    }

    private void updateClothing(ImageView view, int[] images, int index) {
        view.setImageResource(images[index]);
        view.setVisibility(View.VISIBLE);
    }

    private void toggleOverlay(int index) {
        primaryScrollView.setVisibility(View.GONE);
        for (int i = 0; i < overlayScrollViews.length; i++) {
            overlayScrollViews[i].setVisibility(i == index ? View.VISIBLE : View.GONE);
        }
    }
}