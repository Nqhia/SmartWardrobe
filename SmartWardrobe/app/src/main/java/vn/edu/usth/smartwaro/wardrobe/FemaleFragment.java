package vn.edu.usth.smartwaro.wardrobe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import vn.edu.usth.smartwaro.R;


public class FemaleFragment extends Fragment {

    private ImageView modelFemale, topBlazer, topCar, topTank, topSweater, topMock, botJean, botLegging, botSkirt, fullblazer, tanklegging,
            tankjean, tankskirt, mocklegging, mockjean, mockskirt, swlegging, swjean, swskirt, carlegging, carjean, carskirt,
            tubepurple, tubeblack, tubered, dresspurple, dressblack, dresswhite ;
    private ImageButton buttonmodelFemale, buttontopBlazer, buttontopCar, buttontopTank, buttontopSweater, buttontopMock,
            buttonbotJean, buttonbotLegging, buttonbotSkirt, buttonfullblazer, buttontanklegging, buttontankjean, buttontankskirt,
            buttonmocklegging, buttonmockjean, buttonmockskirt, buttonswlegging, buttonswjean, buttonswskirt,
            buttoncarlegging,buttoncarjean, buttoncarskirt,
            buttontubepurple, buttontubeblack, buttontubered, buttondresspurple, buttondressblack, buttondresswhite;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_female, container, false);

        // Tham chiếu các ImageView
        modelFemale = view.findViewById(R.id.model_female);
        topBlazer = view.findViewById(R.id.top_blazer);
        topCar = view.findViewById(R.id.top_cardigan);
        topTank = view.findViewById(R.id.top_tanktop);
        topSweater = view.findViewById(R.id.top_sweater_turtleneck);
        topMock = view.findViewById(R.id.top_tshirt_mockneck);
        botJean = view.findViewById(R.id.bot_jean);
        botLegging = view.findViewById(R.id.bot_legging);
        botSkirt = view.findViewById(R.id.bot_skirt);
        fullblazer = view.findViewById(R.id.full_blazer);
        tanklegging =view.findViewById(R.id.full_tanktop);
        tankjean = view.findViewById(R.id.tanktop_jeans);
        tankskirt =view.findViewById(R.id.tanktop_skirt);
        mocklegging =view.findViewById(R.id.tshirt_mockneck_legging);
        mockjean = view.findViewById(R.id.full_tshirt_mockneck);
        mockskirt =view.findViewById(R.id.tshirt_mockneck_skirt);
        swlegging =view.findViewById(R.id.sweater_legging);
        swjean = view.findViewById(R.id.sweater_jeans);
        swskirt =view.findViewById(R.id.sweater_skirt);
        mocklegging =view.findViewById(R.id.tshirt_mockneck_legging);
        mockjean = view.findViewById(R.id.full_tshirt_mockneck);
        mockskirt =view.findViewById(R.id.tshirt_mockneck_skirt);
        carlegging =view.findViewById(R.id.cardigan_legging);
        carjean = view.findViewById(R.id.cardigan_jeans);
        carskirt =view.findViewById(R.id.cardigan_skirt);
        tubepurple =view.findViewById(R.id.tube_purple);
        tubeblack = view.findViewById(R.id.dress_tube);
        tubered =view.findViewById(R.id.dress_red);
        dresspurple =view.findViewById(R.id.top_dress);
        dressblack = view.findViewById(R.id.dress_black);
        dresswhite =view.findViewById(R.id.dress_white);

        // Tham chiếu các nút
        buttonmodelFemale = view.findViewById(R.id.female_icon);
        buttontopBlazer = view.findViewById(R.id.top_blazer_icon);
        buttontopCar = view.findViewById(R.id.top_cardigan_icon);
        buttontopTank = view.findViewById(R.id.top_tanktop_icon);
        buttontopSweater = view.findViewById(R.id.top_sweater_icon);
        buttontopMock = view.findViewById(R.id.top_mockneck_icon);
        buttonbotJean = view.findViewById(R.id.bot_jeans_icon);
        buttonbotLegging = view.findViewById(R.id.bot_legging_icon);
        buttonbotSkirt = view.findViewById(R.id.bot_skirt_icon);
        buttonfullblazer = view.findViewById(R.id.full_blazer_icon);
        buttontanklegging = view.findViewById(R.id.tanktop_legging_icon);
        buttontankjean = view.findViewById(R.id.tanktop_jeans_icon);
        buttontankskirt = view.findViewById(R.id.tanktop_skirt_icon);
        buttonmocklegging = view.findViewById(R.id.mockneck_legging_icon);
        buttonmockjean = view.findViewById(R.id.mockneck_jeans_icon);
        buttonmockskirt = view.findViewById(R.id.mockneck_skirt_icon);
        buttonswlegging = view.findViewById(R.id.sweater_legging_icon);
        buttonswjean = view.findViewById(R.id.sweater_jeans_icon);
        buttonswskirt = view.findViewById(R.id.sweater_skirt_icon);
        buttoncarlegging = view.findViewById(R.id.cardigan_legging_icon);
        buttoncarjean = view.findViewById(R.id.cardigan_jeans_icon);
        buttoncarskirt = view.findViewById(R.id.cardigan_skirt_icon);
        buttontubepurple = view.findViewById(R.id.tube_purple_icon);
        buttontubeblack = view.findViewById(R.id.black_tube_icon);
        buttontubered = view.findViewById(R.id.tube_red_icon);
        buttondresspurple = view.findViewById(R.id.dress_purple_icon);
        buttondressblack = view.findViewById(R.id.black_dress_icon);
        buttondresswhite = view.findViewById(R.id.dress_white_icon);


        // Xử lý sự kiện nút bấm
        buttonmodelFemale.setOnClickListener(v -> showOutfit(modelFemale));
        buttontopBlazer.setOnClickListener(v -> showOutfit(topBlazer));
        buttontopCar.setOnClickListener(v -> showOutfit(topCar));
        buttontopTank.setOnClickListener(v -> showOutfit(topTank));
        buttontopSweater.setOnClickListener(v -> showOutfit(topSweater));
        buttontopMock.setOnClickListener(v -> showOutfit(topMock));
        buttonbotJean.setOnClickListener(v -> showOutfit(botJean));
        buttonbotLegging.setOnClickListener(v -> showOutfit(botLegging));
        buttonbotSkirt.setOnClickListener(v -> showOutfit(botSkirt));
        buttonfullblazer.setOnClickListener(v -> showOutfit(fullblazer));
        buttontanklegging.setOnClickListener(v -> showOutfit(tanklegging));
        buttontankjean.setOnClickListener(v -> showOutfit(tankjean));
        buttontankskirt.setOnClickListener(v -> showOutfit(tankskirt));

        buttonmocklegging.setOnClickListener(v -> showOutfit(mocklegging));
        buttonmockjean.setOnClickListener(v -> showOutfit(mockjean));
        buttonmockskirt.setOnClickListener(v -> showOutfit(mockskirt));

        buttonswlegging.setOnClickListener(v -> showOutfit(swlegging));
        buttonswjean.setOnClickListener(v -> showOutfit(swjean));
        buttonswskirt.setOnClickListener(v -> showOutfit(swskirt));

        buttoncarlegging.setOnClickListener(v -> showOutfit(carlegging));
        buttoncarjean.setOnClickListener(v -> showOutfit(carjean));
        buttoncarskirt.setOnClickListener(v -> showOutfit(carskirt));

        buttontubepurple.setOnClickListener(v -> showOutfit(tubepurple));
        buttontubeblack.setOnClickListener(v -> showOutfit(tubeblack));
        buttontubered.setOnClickListener(v -> showOutfit(tubered));

        buttondresspurple.setOnClickListener(v -> showOutfit(dresspurple));
        buttondressblack.setOnClickListener(v -> showOutfit(dressblack));
        buttondresswhite.setOnClickListener(v -> showOutfit(dresswhite));



        return view;
    }

    private void showOutfit(ImageView selectedOutfit) {
        // Đặt tất cả các ImageView về trạng thái "gone"
        modelFemale.setVisibility(View.VISIBLE);
        topBlazer.setVisibility(View.GONE);
        topCar.setVisibility(View.GONE);
        topTank.setVisibility(View.GONE);
        topSweater.setVisibility(View.GONE);
        topMock.setVisibility(View.GONE);
        botLegging.setVisibility(View.GONE);
        botJean.setVisibility(View.GONE);
        botSkirt.setVisibility(View.GONE);
        fullblazer.setVisibility(View.GONE);
        tanklegging.setVisibility(View.GONE);
        tankjean.setVisibility(View.GONE);
        tankskirt.setVisibility(View.GONE);
        mocklegging.setVisibility(View.GONE);
        mockjean.setVisibility(View.GONE);
        mockskirt.setVisibility(View.GONE);
        swskirt.setVisibility(View.GONE);
        swjean.setVisibility(View.GONE);
        swlegging.setVisibility(View.GONE);
        carskirt.setVisibility(View.GONE);
        carjean.setVisibility(View.GONE);
        carlegging.setVisibility(View.GONE);
        tubered.setVisibility(View.GONE);
        tubeblack.setVisibility(View.GONE);
        tubepurple.setVisibility(View.GONE);
        dresswhite.setVisibility(View.GONE);
        dressblack.setVisibility(View.GONE);
        dresspurple.setVisibility(View.GONE);



        // Hiển thị ImageView được chọn
        selectedOutfit.setVisibility(View.VISIBLE);
        //modelFemale.setVisibility(View.GONE); // Ẩn model cơ bản khi chọn outfit
    }
}
