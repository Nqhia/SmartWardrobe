package vn.edu.usth.smartwaro.mycloset;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.smartwaro.R;

public class ImageViewerFragment extends Fragment {
    private ViewPager2 viewPager;
    private ImageViewerAdapter adapter;
    private List<GalleryImage> images;
    private int startPosition;
    private TextView pageIndicator;

    public static ImageViewerFragment newInstance(ArrayList<GalleryImage> images, int startPosition) {
        ImageViewerFragment fragment = new ImageViewerFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("images", images);
        args.putInt("position", startPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery_image_detail, container, false);

        // Hide status bar and action bar
        hideSystemUI();

        viewPager = view.findViewById(R.id.view_pager);
        ImageButton closeButton = view.findViewById(R.id.btn_close);
        pageIndicator = view.findViewById(R.id.page_indicator);

        if (getArguments() != null) {
            images = getArguments().getParcelableArrayList("images");
            startPosition = getArguments().getInt("position");
        }

        setupViewPager();
        updatePageIndicator(startPosition);

        closeButton.setOnClickListener(v -> {
            showSystemUI();
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    private void setupViewPager() {
        adapter = new ImageViewerAdapter(requireContext(), images);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(startPosition, false);
        viewPager.setOffscreenPageLimit(1);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updatePageIndicator(position);
            }
        });
    }

    private void updatePageIndicator(int position) {
        if (images != null && !images.isEmpty()) {
            pageIndicator.setText(String.format("%d/%d", position + 1, images.size()));
        }
    }

    private void hideSystemUI() {
        if (getActivity() != null) {
            // Hide the action bar
            if (getActivity() instanceof AppCompatActivity) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            }

            // Hide the status bar and make the content fullscreen
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

    private void showSystemUI() {
        if (getActivity() != null) {
            // Show the action bar
            if (getActivity() instanceof AppCompatActivity) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            }

            // Show the status bar and restore normal UI
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        showSystemUI();
    }
}