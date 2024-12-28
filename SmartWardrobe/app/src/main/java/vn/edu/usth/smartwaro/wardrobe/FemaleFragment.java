package vn.edu.usth.smartwaro.wardrobe;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;

import vn.edu.usth.smartwaro.R;


public class FemaleFragment extends Fragment {

    private ImageView modelFemale, topFemale, botFemale, tubedress, footwareFemale;
    private ImageButton buttonmodelFemale, buttontopBlazer, buttontopCar, buttontopTank, buttontopSweater, buttontopMock,
            buttonbotJean, buttonbotLegging, buttonbotSkirt,
            buttontubepurple, buttontubeblack, buttontubered, buttonloafer, buttonheels, buttonChangeSkinColor;
    private ScrollView primaryScrollView;
    private ScrollView[] overlayScrollViews;

    private int[] shirtImages = {
            R.drawable.top_blazer,
            R.drawable.top_cardigan,
            R.drawable.top_tanktop,
            R.drawable.top_sweater_turtleneck,
            R.drawable.top_tshirt_mockneck,

    };

    private int[] pantsImages = {
            R.drawable.bot_jean,
            R.drawable.bot_legging,
            R.drawable.bot_skirt
    };

    private int[] dressImages = {
            R.drawable.tube_purple,
            R.drawable.dress_tube,
            R.drawable.dress_red

    };

    private int[] footwareImages = {
            R.drawable.loafer,
            R.drawable.heels
    };


    private int currentShirtIndex = 0;
    private int currentPantsIndex = 0;
    private int currentDressIndex = 0;
    private int currentFootwareIndex = 0;
    private int currentModelSkinIndex = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_female, container, false);

        modelFemale = view.findViewById(R.id.model_female);
        topFemale = view.findViewById(R.id.top_female);
        botFemale = view.findViewById(R.id.bot_female);
        tubedress = view.findViewById(R.id.tube_dress);
        footwareFemale = view.findViewById(R.id.female_footwear);

        //buttonmodelFemale = view.findViewById(R.id.female_icon);
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

        primaryScrollView = view.findViewById(R.id.primary_scroll_view);


        overlayScrollViews = new ScrollView[]{
                view.findViewById(R.id.overlay_scroll_view_tops),
                view.findViewById(R.id.overlay_scroll_view_bot),
                view.findViewById(R.id.overlay_scroll_view_skirt),
                view.findViewById(R.id.overlay_scroll_view_dress),
                view.findViewById(R.id.overlay_scroll_view_footware),
                view.findViewById(R.id.overlay_scroll_view_skin_tone)
        };

        // Handle click on category buttons
        view.findViewById(R.id.type_top_female).setOnClickListener(v -> toggleOverlay(0));
        view.findViewById(R.id.type_bot_female).setOnClickListener(v -> toggleOverlay(1));
        view.findViewById(R.id.type_skirt_female).setOnClickListener(v -> toggleOverlay(2));
        view.findViewById(R.id.type_dress_female).setOnClickListener(v -> toggleOverlay(3));
        view.findViewById(R.id.type_footware_female).setOnClickListener(v -> toggleOverlay(4));
        view.findViewById(R.id.type_skin_female).setOnClickListener(v -> toggleOverlay(5));

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

        modelFemale.setImageResource(R.drawable.female);


        //Áo
        buttontopBlazer.setOnClickListener(v -> {
            currentShirtIndex = 0;
            topFemale.setImageResource(shirtImages[currentShirtIndex]);
            topFemale.setVisibility(View.VISIBLE);
            tubedress.setVisibility(View.GONE);

        });
        buttontopCar.setOnClickListener(v -> {
            currentShirtIndex = 1;
            topFemale.setImageResource(shirtImages[currentShirtIndex]);
            topFemale.setVisibility(View.VISIBLE);
            tubedress.setVisibility(View.GONE);

        });
        buttontopTank.setOnClickListener(v -> {
            currentShirtIndex = 2;
            topFemale.setImageResource(shirtImages[currentShirtIndex]);
            topFemale.setVisibility(View.VISIBLE);
            tubedress.setVisibility(View.GONE);

        });
        buttontopSweater.setOnClickListener(v -> {
            currentShirtIndex = 3;
            topFemale.setImageResource(shirtImages[currentShirtIndex]);
            topFemale.setVisibility(View.VISIBLE);
            tubedress.setVisibility(View.GONE);

        });
        buttontopMock.setOnClickListener(v -> {
            currentShirtIndex = 4;
            topFemale.setImageResource(shirtImages[currentShirtIndex]);
            topFemale.setVisibility(View.VISIBLE);
            tubedress.setVisibility(View.GONE);

        });

        //Quần
        buttonbotJean.setOnClickListener(v -> {
            currentPantsIndex = 0;
            botFemale.setImageResource(pantsImages[currentPantsIndex]);
            botFemale.setVisibility(View.VISIBLE);
            tubedress.setVisibility(View.GONE);

        });
        buttonbotLegging.setOnClickListener(v -> {
            currentPantsIndex = 1;
            botFemale.setImageResource(pantsImages[currentPantsIndex]);
            botFemale.setVisibility(View.VISIBLE);
            tubedress.setVisibility(View.GONE);

        });
        buttonbotSkirt.setOnClickListener(v -> {
            currentPantsIndex = 2;
            botFemale.setImageResource(pantsImages[currentPantsIndex]);
            botFemale.setVisibility(View.VISIBLE);
            tubedress.setVisibility(View.GONE);
        });

        //Váy
        buttontubepurple.setOnClickListener(v -> {
            currentDressIndex = 0;
            tubedress.setImageResource(dressImages[currentDressIndex]);
            tubedress.setVisibility(View.VISIBLE);
            topFemale.setVisibility(View.GONE);
            botFemale.setVisibility(View.GONE);
        });
        buttontubeblack.setOnClickListener(v -> {
            currentDressIndex = 1;
            tubedress.setImageResource(dressImages[currentDressIndex]);
            tubedress.setVisibility(View.VISIBLE);
            topFemale.setVisibility(View.GONE);
            botFemale.setVisibility(View.GONE);
        });
        buttontubered.setOnClickListener(v -> {
            currentDressIndex = 2;
            tubedress.setImageResource(dressImages[currentDressIndex]);
            tubedress.setVisibility(View.VISIBLE);
            topFemale.setVisibility(View.GONE);
            botFemale.setVisibility(View.GONE);
        });

        //Giày
        buttonloafer.setOnClickListener(v -> {
            currentFootwareIndex = 0;
            footwareFemale.setImageResource(footwareImages[currentFootwareIndex]);
            footwareFemale.setVisibility(View.VISIBLE);
        });

        buttonheels.setOnClickListener(v -> {
            currentFootwareIndex = 1;
            footwareFemale.setImageResource(footwareImages[currentFootwareIndex]);
            footwareFemale.setVisibility(View.VISIBLE);
        });

        buttonChangeSkinColor.setOnClickListener(v -> {
            currentModelSkinIndex = (currentModelSkinIndex + 1) % 7;
            switch (currentModelSkinIndex) {
                case 0:
                    modelFemale.setImageResource(R.drawable.female);
                    break;
                case 1:
                    modelFemale.setImageResource(R.drawable.female_white_skin);
                    break;
                case 2:
                    modelFemale.setImageResource(R.drawable.female_nude_skin);
                    break;
                case 3:
                    modelFemale.setImageResource(R.drawable.female_yellow_skin);
                    break;
                case 4:
                    modelFemale.setImageResource(R.drawable.female_brown_skin);
                    break;
                case 5:
                    modelFemale.setImageResource(R.drawable.female_dark_skin);
                    break;
                case 6:
                    modelFemale.setImageResource(R.drawable.female_black_skin);
                    break;
            }
        });

        return view;
    }


    private void toggleOverlay(int index) {
        primaryScrollView.setVisibility(View.GONE);
        for (int i = 0; i < overlayScrollViews.length; i++) {
            overlayScrollViews[i].setVisibility(i == index ? View.VISIBLE : View.GONE);
        }
    }

}