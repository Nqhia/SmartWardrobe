package vn.edu.usth.smartwaro.weather;

import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import android.os.Handler;


import vn.edu.usth.smartwaro.R;

public class LocationFragment extends Fragment {
    private EditText edtSearch;
    private RecyclerView recyclerView;
    private ImageView btnClose;
    private LocationsAdapter locationsAdapter;
    private LocationViewModel locationViewModel;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        edtSearch = view.findViewById(R.id.edtSearch);
        recyclerView = view.findViewById(R.id.locationRecyclerView);
        btnClose = view.findViewById(R.id.imageClose);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        locationsAdapter = new LocationsAdapter(new LocationsAdapter.OnLocationClickListener() {
            @Override
            public void onLocationClicked(RemoteLocation remoteLocation) {
                String locationText = remoteLocation.getName() + ", " +
                        remoteLocation.getRegion() + ", " +
                        remoteLocation.getCountry();
                Bundle result = new Bundle();
                result.putString(WeatherFragment.KEY_LOCATION_TEXT, locationText);
                result.putDouble(WeatherFragment.KEY_LATITUDE, remoteLocation.getLat());
                result.putDouble(WeatherFragment.KEY_LONGITUDE, remoteLocation.getLon());
                FragmentManager fm = getParentFragmentManager();
                fm.setFragmentResult(WeatherFragment.REQUEST_KEY_MANUAL_LOCATION_SEARCH, result);
                fm.popBackStack();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(locationsAdapter);

        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        locationViewModel.getSearchResult().observe(getViewLifecycleOwner(), new Observer<List<RemoteLocation>>() {
            @Override
            public void onChanged(List<RemoteLocation> remoteLocations) {
                locationsAdapter.setData(remoteLocations);
            }
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String query = edtSearch.getText().toString();
                    if (!query.isEmpty()) {
                        locationViewModel.searchLocation(query);
                    }
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });
    }
}
