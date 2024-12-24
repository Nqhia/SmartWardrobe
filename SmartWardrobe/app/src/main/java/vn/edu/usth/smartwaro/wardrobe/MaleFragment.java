package vn.edu.usth.smartwaro.wardrobe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;

import vn.edu.usth.smartwaro.R;

public class MaleFragment extends Fragment {

    private ImageView modelMale, topTanktop, topPolo, topSw, topBlazerMale, topBreast, botJeanMale, botPant;
    private ImageButton buttonmodelMale, buttontopTanktop, buttontopPolo, buttontopSw, buttontopBlazerMale, buttontopBreast,
            buttonbotJeanMale, buttonbotPant;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_male, container, false);

        // Tham chiếu các ImageView
        modelMale = view.findViewById(R.id.model_male);
        topTanktop = view.findViewById(R.id.top_tanktop_male);
        topPolo = view.findViewById(R.id.top_polo);
        topSw = view.findViewById(R.id.top_sweater);
        topBlazerMale = view.findViewById(R.id.top_blazer_male);
        topBreast = view.findViewById(R.id.top_breast);

        botJeanMale = view.findViewById(R.id.bot_jean_male);
        botPant = view.findViewById(R.id.bot_pant);


        // Tham chiếu các nút
        buttonmodelMale = view.findViewById(R.id.male_model_icon);

        buttontopTanktop = view.findViewById(R.id.top_tanktop_male_icon);
        buttontopPolo = view.findViewById(R.id.top_polo_icon);
        buttontopSw = view.findViewById(R.id.top_sweater_male_icon);
        buttontopBlazerMale = view.findViewById(R.id.top_blazer_male_icon);
        buttontopBreast = view.findViewById(R.id.top_breast_icon);

        buttonbotJeanMale = view.findViewById(R.id.bot_jean_male_icon);
        buttonbotPant = view.findViewById(R.id.bot_pant_icon);




        // Xử lý sự kiện nút bấm
        buttonmodelMale.setOnClickListener(v -> showOutfit(modelMale));
        buttontopTanktop.setOnClickListener(v -> showOutfit(topTanktop));
        buttontopPolo.setOnClickListener(v -> showOutfit(topPolo));
        buttontopSw.setOnClickListener(v -> showOutfit(topSw));
        buttontopBlazerMale.setOnClickListener(v -> showOutfit(topBlazerMale));
        buttontopBreast.setOnClickListener(v -> showOutfit(topBreast));

        buttonbotJeanMale.setOnClickListener(v -> showOutfit(botJeanMale));
        buttonbotPant.setOnClickListener(v -> showOutfit(botPant));



        return view;
    }

    private void showOutfit(ImageView selectedOutfit) {
        // Đặt tất cả các ImageView về trạng thái "gone"
        modelMale.setVisibility(View.VISIBLE);
        topTanktop.setVisibility(View.GONE);
        topBreast.setVisibility(View.GONE);
        topBlazerMale.setVisibility(View.GONE);
        topSw.setVisibility(View.GONE);
        topPolo.setVisibility(View.GONE);

        botJeanMale.setVisibility(View.GONE);
        botPant.setVisibility(View.GONE);



        // Hiển thị ImageView được chọn
        selectedOutfit.setVisibility(View.VISIBLE);
        //modelFemale.setVisibility(View.GONE);
    }
}

