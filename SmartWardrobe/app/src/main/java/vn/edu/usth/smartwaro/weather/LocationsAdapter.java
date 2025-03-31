package vn.edu.usth.smartwaro.weather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.smartwaro.R;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.LocationViewHolder> {
    private List<RemoteLocation> locations = new ArrayList<>();
    private OnLocationClickListener listener;

    public interface OnLocationClickListener {
        void onLocationClicked(RemoteLocation remoteLocation);
    }

    public LocationsAdapter(OnLocationClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<RemoteLocation> data) {
        locations.clear();
        locations.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        RemoteLocation remoteLocation = locations.get(position);
        holder.bind(remoteLocation);
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView txtLocation;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLocation = itemView.findViewById(R.id.txtLocation);
        }

        public void bind(final RemoteLocation remoteLocation) {
            String locationStr = remoteLocation.getName() + ", " +
                    remoteLocation.getRegion() + ", " +
                    remoteLocation.getCountry();
            txtLocation.setText(locationStr);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onLocationClicked(remoteLocation);
                }
            });
        }
    }
}
