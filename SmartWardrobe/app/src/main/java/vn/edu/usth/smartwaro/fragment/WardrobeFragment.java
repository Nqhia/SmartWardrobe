package vn.edu.usth.smartwaro.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.wardrobe.FemaleFragment;
import vn.edu.usth.smartwaro.wardrobe.MaleFragment;

public class WardrobeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wardrobe, container, false);

        ImageView maleModel = view.findViewById(R.id.male_model);
        ImageView femaleModel = view.findViewById(R.id.female_model);
        Button buttonMale = view.findViewById(R.id.button_male);
        Button buttonFemale = view.findViewById(R.id.button_female);

        animateViewsTogether(maleModel, femaleModel, buttonMale, buttonFemale);

        buttonMale.setOnClickListener(v -> openFragment(new MaleFragment()));
        buttonFemale.setOnClickListener(v -> openFragment(new FemaleFragment()));

        return view;
    }

    private void animateViewsTogether(ImageView maleModel, ImageView femaleModel, Button buttonMale, Button buttonFemale) {
        maleModel.setVisibility(View.VISIBLE);
        femaleModel.setVisibility(View.VISIBLE);
        buttonMale.setVisibility(View.VISIBLE);
        buttonFemale.setVisibility(View.VISIBLE);

        ObjectAnimator animMale = ObjectAnimator.ofFloat(maleModel, "translationX", -500f, 0f);

        ObjectAnimator animFemale = ObjectAnimator.ofFloat(femaleModel, "translationX", 500f, 0f);

        ObjectAnimator animButtonMale = ObjectAnimator.ofFloat(buttonMale, "translationY", -300f, 0f);

        ObjectAnimator animButtonFemale = ObjectAnimator.ofFloat(buttonFemale, "translationY", -300f, 0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animMale, animFemale, animButtonMale, animButtonFemale);
        animatorSet.setDuration(50);
        animatorSet.start();
    }

    private void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
