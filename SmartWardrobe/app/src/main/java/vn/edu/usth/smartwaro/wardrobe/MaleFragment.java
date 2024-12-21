package vn.edu.usth.smartwaro.wardrobe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import vn.edu.usth.smartwaro.R;

public class MaleFragment extends Fragment {

    private ImageView modelMale, topTanktop, topPolo, topSw, topBlazerMale, topBreast, botJeanMale, botPant,
            tankjeanmale, tankpant,
            polojeanmale, polopant,
            swjeanmale, swpant,
            blazermale, breastmale;
    private ImageButton buttonmodelMale, buttontopTanktop, buttontopPolo, buttontopSw, buttontopBlazerMale, buttontopBreast,
    buttonbotJeanMale, buttonbotPant,
    buttontankjeanmale, buttontankpant,
    buttonpolojeanmale, buttonpolopant,
    buttonswjeanmale, buttonswpant,
    buttonblazermale, buttonbreastmale;
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

        tankjeanmale = view.findViewById(R.id.tank_jean);
        tankpant = view.findViewById(R.id.tank_pant);

        polojeanmale =view.findViewById(R.id.polo_jean);
        polopant = view.findViewById(R.id.polo_pant);

        swjeanmale =view.findViewById(R.id.sweater_jean_male);
        swpant =view.findViewById(R.id.sweater_pant);

        blazermale = view.findViewById(R.id.male_blazer);
        breastmale =view.findViewById(R.id.male_breast);

        // Tham chiếu các nút
        buttonmodelMale = view.findViewById(R.id.male_model_icon);

        buttontopTanktop = view.findViewById(R.id.top_tanktop_male_icon);
        buttontopPolo = view.findViewById(R.id.top_polo_icon);
        buttontopSw = view.findViewById(R.id.top_sweater_male_icon);
        buttontopBlazerMale = view.findViewById(R.id.top_blazer_male_icon);
        buttontopBreast = view.findViewById(R.id.top_breast_icon);

        buttonbotJeanMale = view.findViewById(R.id.bot_jean_male_icon);
        buttonbotPant = view.findViewById(R.id.bot_pant_icon);

        buttontankjeanmale = view.findViewById(R.id.tank_jean_male_icon);
        buttontankpant = view.findViewById(R.id.tank_pant_icon);

        buttonpolojeanmale =view.findViewById(R.id.polo_jean_icon);
        buttonpolopant = view.findViewById(R.id.polo_pant_icon);

        buttonswjeanmale =view.findViewById(R.id.sweater_jean_male_icon);
        buttonswpant =view.findViewById(R.id.sweater_pant_icon);

        buttonblazermale = view.findViewById(R.id.male_blazer_icon);
        buttonbreastmale =view.findViewById(R.id.male_breast_icon);


        // Xử lý sự kiện nút bấm
        buttonmodelMale.setOnClickListener(v -> showOutfit(modelMale));
        buttontopTanktop.setOnClickListener(v -> showOutfit(topTanktop));
        buttontopPolo.setOnClickListener(v -> showOutfit(topPolo));
        buttontopSw.setOnClickListener(v -> showOutfit(topSw));
        buttontopBlazerMale.setOnClickListener(v -> showOutfit(topBlazerMale));
        buttontopBreast.setOnClickListener(v -> showOutfit(topBreast));

        buttonbotJeanMale.setOnClickListener(v -> showOutfit(botJeanMale));
        buttonbotPant.setOnClickListener(v -> showOutfit(botPant));

        buttontankjeanmale.setOnClickListener(v -> showOutfit(tankjeanmale));
        buttontankpant.setOnClickListener(v -> showOutfit(tankpant));

        buttonpolojeanmale.setOnClickListener(v -> showOutfit(polojeanmale));
        buttonpolopant.setOnClickListener(v -> showOutfit(polopant));

        buttonswjeanmale.setOnClickListener(v -> showOutfit(swjeanmale));
        buttonswpant.setOnClickListener(v -> showOutfit(swpant));

        buttonblazermale.setOnClickListener(v -> showOutfit(blazermale));
        buttonbreastmale.setOnClickListener(v -> showOutfit(breastmale));

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

        tankjeanmale.setVisibility(View.GONE);
        tankpant.setVisibility(View.GONE);
        polojeanmale.setVisibility(View.GONE);
        polopant.setVisibility(View.GONE);
        swjeanmale.setVisibility(View.GONE);
        swpant.setVisibility(View.GONE);
        blazermale.setVisibility(View.GONE);
        breastmale.setVisibility(View.GONE);


        // Hiển thị ImageView được chọn
        selectedOutfit.setVisibility(View.VISIBLE);
        //modelFemale.setVisibility(View.GONE);
    }
}

