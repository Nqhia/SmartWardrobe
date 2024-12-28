package vn.edu.usth.smartwaro.wardrobe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;

import vn.edu.usth.smartwaro.R;

public class MaleFragment extends Fragment {

    private ImageView modelMale, topMale,botMale, footwareMale;
    private ImageButton buttonmodelMale, buttontopTanktop, buttontopPolo, buttontopSw, buttontopBlazerMale, buttontopBreast,
            buttonbotJeanMale, buttonbotPant, buttonloafermale, buttonChangeSkinColor;
    private ScrollView MaleTypeScrollView;

    private int[] shirtImages = {
            R.drawable.top_blazer_male,
            R.drawable.top_breast,
            R.drawable.top_sweater,
            R.drawable.top_polo,
            R.drawable.top_tanktop_male,

    };

    private int[] pantsImages = {
            R.drawable.bot_jean_male,
            R.drawable.bot_pant
    };


    private int[] footwareImages = {
            R.drawable.male_footware
    };


    private int currentShirtIndex = 0;
    private int currentPantsIndex = 0;
    private int currentFootwareIndex = 0;
    private int currentModelSkinIndex = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_male, container, false);

        // Tham chiếu các ImageView
        modelMale = view.findViewById(R.id.model_male);
        topMale = view.findViewById(R.id.top_male);
        botMale = view.findViewById(R.id.bot_male);
        footwareMale = view.findViewById(R.id.male_footwear);

        // Tham chiếu các nút
        buttonmodelMale = view.findViewById(R.id.male_model_icon);

        buttontopTanktop = view.findViewById(R.id.top_tanktop_male_icon);
        buttontopPolo = view.findViewById(R.id.top_polo_icon);
        buttontopSw = view.findViewById(R.id.top_sweater_male_icon);
        buttontopBlazerMale = view.findViewById(R.id.top_blazer_male_icon);
        buttontopBreast = view.findViewById(R.id.top_breast_icon);

        buttonbotJeanMale = view.findViewById(R.id.bot_jean_male_icon);
        buttonbotPant = view.findViewById(R.id.bot_pant_icon);
        buttonloafermale = view.findViewById(R.id.male_loafer_icon);

        buttonChangeSkinColor = view.findViewById(R.id.buttonChangeSkinColorMale);


        MaleTypeScrollView = view.findViewById(R.id.male_type_scroll_view);

        int[] overlayIds = {
                R.id.overlay_scroll_view_tops_male,
                R.id.overlay_scroll_view_bot_male,
                R.id.overlay_scroll_view_footware_male,
                R.id.overlay_scroll_view_skin_tone_male
        };

        for (int overlayId : overlayIds) {
            ScrollView overlayScrollView = view.findViewById(overlayId);
            Button closeOverlayButton = overlayScrollView.findViewById(R.id.back_male_type);

            closeOverlayButton.setOnClickListener(v -> {
                overlayScrollView.setVisibility(View.GONE);
                MaleTypeScrollView.setVisibility(View.VISIBLE);
            });
        }

        view.findViewById(R.id.type_top_male).setOnClickListener(v -> toggleOverlay(view, R.id.overlay_scroll_view_tops_male));
        view.findViewById(R.id.type_bot_male).setOnClickListener(v -> toggleOverlay(view, R.id.overlay_scroll_view_bot_male));
        view.findViewById(R.id.type_footware_male).setOnClickListener(v -> toggleOverlay(view, R.id.overlay_scroll_view_footware_male));
        view.findViewById(R.id.type_skin_male).setOnClickListener(v -> toggleOverlay(view, R.id.overlay_scroll_view_skin_tone_male));


        //Áo
        buttontopBlazerMale.setOnClickListener(v -> {
            currentShirtIndex = 0;
            topMale.setImageResource(shirtImages[currentShirtIndex]);
            topMale.setVisibility(View.VISIBLE);
        });
        buttontopBreast.setOnClickListener(v -> {
            currentShirtIndex = 1;
            topMale.setImageResource(shirtImages[currentShirtIndex]);
            topMale.setVisibility(View.VISIBLE);
        });
        buttontopSw.setOnClickListener(v -> {
            currentShirtIndex = 2;
            topMale.setImageResource(shirtImages[currentShirtIndex]);
            topMale.setVisibility(View.VISIBLE);
        });
        buttontopPolo.setOnClickListener(v -> {
            currentShirtIndex = 3;
            topMale.setImageResource(shirtImages[currentShirtIndex]);
            topMale.setVisibility(View.VISIBLE);
        });
        buttontopTanktop.setOnClickListener(v -> {
            currentShirtIndex = 4;
            topMale.setImageResource(shirtImages[currentShirtIndex]);
            topMale.setVisibility(View.VISIBLE);
        });

        //Quần
        buttonbotJeanMale.setOnClickListener(v -> {
            currentPantsIndex = 0;
            botMale.setImageResource(pantsImages[currentPantsIndex]);
            botMale.setVisibility(View.VISIBLE);
        });
        buttonbotPant.setOnClickListener(v -> {
            currentPantsIndex = 1;
            botMale.setImageResource(pantsImages[currentPantsIndex]);
            botMale.setVisibility(View.VISIBLE);
        });

        //Giày
        buttonloafermale.setOnClickListener(v -> {
            currentFootwareIndex = 0;
            footwareMale.setImageResource(footwareImages[currentFootwareIndex]);
            footwareMale.setVisibility(View.VISIBLE);
        });


        buttonChangeSkinColor.setOnClickListener(v -> {
            currentModelSkinIndex = (currentModelSkinIndex + 1) % 3;
            switch (currentModelSkinIndex) {
                case 0:
                    modelMale.setImageResource(R.drawable.male);
                    break;
                case 1:
                    modelMale.setImageResource(R.drawable.male_yellow_skin);
                    break;
                case 2:
                    modelMale.setImageResource(R.drawable.male_dark_skin);
                    break;

            }
        });

        return view;
    }

    private void toggleOverlay(View view, int overlayId) {
        MaleTypeScrollView.setVisibility(View.GONE);
        for (int id : new int[]{
                R.id.overlay_scroll_view_tops_male,
                R.id.overlay_scroll_view_bot_male,
                R.id.overlay_scroll_view_footware_male,
                R.id.overlay_scroll_view_skin_tone_male}) {
            view.findViewById(id).setVisibility(id == overlayId ? View.VISIBLE : View.GONE);
        }
    }
}

