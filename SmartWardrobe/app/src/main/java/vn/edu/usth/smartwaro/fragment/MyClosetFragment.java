package vn.edu.usth.smartwaro.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.mycloset.ClosetAdapter;
import vn.edu.usth.smartwaro.mycloset.ClothingItem;
import vn.edu.usth.smartwaro.mycloset.ClosetViewModel;

public class MyClosetFragment extends Fragment {

    private RecyclerView rvUpperBody, rvLowerBody, rvFootwear;
    private ClosetAdapter upperBodyAdapter, lowerBodyAdapter, footwearAdapter;
    private ClosetViewModel closetViewModel;

    public MyClosetFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_closet, container, false);

        closetViewModel = new ViewModelProvider(requireActivity()).get(ClosetViewModel.class);

        rvUpperBody = view.findViewById(R.id.rv_upper_body);
        rvLowerBody = view.findViewById(R.id.rv_lower_body);
        rvFootwear = view.findViewById(R.id.rv_footwear);

        rvUpperBody.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvLowerBody.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFootwear.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        upperBodyAdapter = new ClosetAdapter(new ArrayList<>(), true, requireActivity(), "upperBody");
        lowerBodyAdapter = new ClosetAdapter(new ArrayList<>(), true, requireActivity(), "lowerBody");
        footwearAdapter = new ClosetAdapter(new ArrayList<>(), true, requireActivity(), "footwear");

        rvUpperBody.setAdapter(upperBodyAdapter);
        rvLowerBody.setAdapter(lowerBodyAdapter);
        rvFootwear.setAdapter(footwearAdapter);

        closetViewModel.getUpperBodyItems().observe(getViewLifecycleOwner(), items -> {
            upperBodyAdapter.updateData(items);  // Sử dụng updateData ở đây
        });

        closetViewModel.getLowerBodyItems().observe(getViewLifecycleOwner(), items -> {
            lowerBodyAdapter.updateData(items);
        });

        closetViewModel.getFootwearItems().observe(getViewLifecycleOwner(), items -> {
            footwearAdapter.updateData(items);
        });

        return view;
    }

    public void addImageToRecyclerView(ClothingItem clothingItem, String category) {
        switch (category) {
            case "upperBody":
                closetViewModel.addUpperBodyItem(clothingItem); // Thêm vào danh sách quần áo trên
                break;
            case "lowerBody":
                closetViewModel.addLowerBodyItem(clothingItem);
                break;
            case "footwear":
                closetViewModel.addFootwearItem(clothingItem);
                break;
            default:
                throw new IllegalArgumentException("Invalid category");
        }
    }
}
