package vn.edu.usth.smartwaro.mycloset;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.network.FlaskNetwork;

public class FavoriteSetListFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoriteSetListAdapter adapter;
    private List<FavoriteSet> favoriteSets = new ArrayList<>();

    public static FavoriteSetListFragment newInstance() {
        return new FavoriteSetListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite_set_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerViewFavoriteSetList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FavoriteSetListAdapter(favoriteSets, set -> {
            FavoriteSetEditFragment editFragment = FavoriteSetEditFragment.newInstance(
                    set.getId(),
                    set.getSetName(),
                    new ArrayList<>(set.getShirtImages()),
                    new ArrayList<>(set.getPantsImages())
            );
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, editFragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(adapter);
        loadFavoriteSets();
    }

    private void loadFavoriteSets() {
        new FlaskNetwork().getFavoriteSets(new FlaskNetwork.OnFavoriteSetsLoadedListener() {
            @Override
            public void onSuccess(List<FavoriteSet> sets) {
                if(getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        favoriteSets.clear();
                        favoriteSets.addAll(sets);
                        adapter.notifyDataSetChanged();
                    });
                }
            }
            @Override
            public void onError(String message) {
                if(getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Error loading favorite sets: " + message, Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}