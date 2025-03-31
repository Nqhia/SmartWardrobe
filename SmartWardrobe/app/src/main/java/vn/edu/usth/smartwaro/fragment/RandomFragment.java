package vn.edu.usth.smartwaro.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.network.FlaskNetwork;

public class RandomFragment extends Fragment {
    private static final String TAG = "RandomFragment";
    private Button btnGenerateRandomOutfit;
    private ImageView imageViewTop, imageViewBottom;

    private FlaskNetwork flaskNetwork;

    public RandomFragment() {
        // Required empty public constructor
    }

    public static RandomFragment newInstance() {
        return new RandomFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flaskNetwork = new FlaskNetwork();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_random, container, false);

        // Initialize views
        btnGenerateRandomOutfit = view.findViewById(R.id.btnGenerateRandomOutfit);
        imageViewTop = view.findViewById(R.id.imageViewTop);
        imageViewBottom = view.findViewById(R.id.imageViewBottom);
//        tvTopCategory = view.findViewById(R.id.tvTopCategory);
//        tvBottomCategory = view.findViewById(R.id.tvBottomCategory);

        // Set up button click listener
        btnGenerateRandomOutfit.setOnClickListener(v -> generateRandomOutfit());

        return view;
    }

    private void generateRandomOutfit() {
        btnGenerateRandomOutfit.setEnabled(false);

        flaskNetwork.getRandomOutfit(new FlaskNetwork.OnRandomOutfitReceivedListener() {
            @Override
            public void onSuccess(FlaskNetwork.RandomOutfit outfit) {
                requireActivity().runOnUiThread(() -> {
                    try {
                        Log.d(TAG, "Top URL: " + outfit.top.url);
                        Log.d(TAG, "Bottom URL: " + outfit.bottom.url);

                        // Load top image using Glide
                        Glide.with(requireContext())
                                .load(outfit.top.url)
                                .placeholder(R.drawable.placeholder_image)
                                .error(R.drawable.placeholder_image)
                                .into(imageViewTop);

                        // Load bottom image using Glide
                        Glide.with(requireContext())
                                .load(outfit.bottom.url)
                                .placeholder(R.drawable.placeholder_image)
                                .error(R.drawable.placeholder_image)
                                .into(imageViewBottom);

                        // Re-enable button
                        btnGenerateRandomOutfit.setEnabled(true);
                    } catch (Exception e) {
                        Log.e(TAG, "Error displaying outfit", e);
                        Toast.makeText(requireContext(),
                                "Error displaying outfit: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        btnGenerateRandomOutfit.setEnabled(true);
                    }
                });
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(() -> {
                    Log.e(TAG, "Error generating outfit: " + message);
                    Toast.makeText(requireContext(),
                            "Error generating outfit: " + message,
                            Toast.LENGTH_SHORT).show();
                    btnGenerateRandomOutfit.setEnabled(true);
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        // Clear Glide images to prevent memory leaks
        Glide.with(requireContext()).clear(imageViewTop);
        Glide.with(requireContext()).clear(imageViewBottom);
        super.onDestroyView();
    }
}
