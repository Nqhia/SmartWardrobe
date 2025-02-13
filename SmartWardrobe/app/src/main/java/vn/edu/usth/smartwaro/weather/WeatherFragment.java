package vn.edu.usth.smartwaro.weather;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.network.FlaskNetwork;
import vn.edu.usth.smartwaro.network.WeatherApiService;
import vn.edu.usth.smartwaro.network.WeatherResponse;

public class WeatherFragment extends Fragment {
    private static final String TAG = "WeatherFragment";
    private static final String BASE_URL = "https://api.weatherapi.com/v1/";
    private static final String API_KEY = "9db56aef06a0411e81d121533242812";
    private static final double COLD_TEMP_THRESHOLD = 20.0;

    private TextView tempTextView;
    private TextView locationTextView;
    private TextView conditionTextView;
    private RecyclerView topClothingRecyclerView;
    private RecyclerView bottomClothingRecyclerView;
    private ClothingAdapter topAdapter;
    private ClothingAdapter bottomAdapter;
    private FlaskNetwork flaskNetwork;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private String userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        initializeViews(view);
        initializeFlaskNetwork();
        setupRecyclerViews();
        fetchWeatherData();
        return view;
    }

    private void initializeViews(View view) {
        tempTextView = view.findViewById(R.id.temp);
        locationTextView = view.findViewById(R.id.locationText);
        conditionTextView = view.findViewById(R.id.conditionText);
        topClothingRecyclerView = view.findViewById(R.id.topClothingRecyclerView);
        bottomClothingRecyclerView = view.findViewById(R.id.bottomClothingRecyclerView);
    }

    private void initializeFlaskNetwork() {
        flaskNetwork = new FlaskNetwork();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            showError("Please log in to view recommendations");
        }
    }

    private void setupRecyclerViews() {
        // Top clothing RecyclerView
        LinearLayoutManager topLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        topClothingRecyclerView.setLayoutManager(topLayoutManager);
        topAdapter = new ClothingAdapter();
        topClothingRecyclerView.setAdapter(topAdapter);

        // Bottom clothing RecyclerView
        LinearLayoutManager bottomLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        bottomClothingRecyclerView.setLayoutManager(bottomLayoutManager);
        bottomAdapter = new ClothingAdapter();
        bottomClothingRecyclerView.setAdapter(bottomAdapter);
    }

    private void fetchWeatherData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApiService apiService = retrofit.create(WeatherApiService.class);
        Call<WeatherResponse> call = apiService.getCurrentWeather(API_KEY, "Hanoi");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                mainHandler.post(() -> {
                    if (!isAdded()) return;

                    if (response.isSuccessful() && response.body() != null) {
                        updateWeatherUI(response.body());
                        if (userId != null) {
                            fetchClothingRecommendations(response.body().getCurrent().getTempC());
                        }
                    } else {
                        showError("Failed to load weather data");
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                mainHandler.post(() -> {
                    if (isAdded()) {
                        showError("Error: " + t.getMessage());
                    }
                });
            }
        });
    }

    private void updateWeatherUI(WeatherResponse weather) {
        if (!isAdded()) return;

        String location = String.format("%s, %s",
                weather.getLocation().getName(),
                weather.getLocation().getCountry());
        String condition = weather.getCurrent().getCondition().getText();
        double temperature = weather.getCurrent().getTempC();

        locationTextView.setText(location);
        tempTextView.setText(String.format("%.1f °C", temperature));
        conditionTextView.setText(condition);
    }

    private void fetchClothingRecommendations(double temperature) {
        String topCategory = temperature < COLD_TEMP_THRESHOLD ?
                FlaskNetwork.CATEGORY_LONG_SLEEVES : FlaskNetwork.CATEGORY_SHORT_SLEEVES;
        String bottomCategory = temperature < COLD_TEMP_THRESHOLD ?
                FlaskNetwork.CATEGORY_LONG_LEGGINGS : FlaskNetwork.CATEGORY_SHORT_LEGGINGS;

        // Fetch top wear
        flaskNetwork.getUserImages(topCategory, new FlaskNetwork.OnImagesLoadedListener() {
            @Override
            public void onSuccess(String[] imageUrls) {
                mainHandler.post(() -> {
                    if (isAdded()) {
                        topAdapter.updateItems(imageUrls);
                    }
                });
            }

            @Override
            public void onError(String message) {
                mainHandler.post(() -> {
                    if (isAdded()) {
                        showError("Error loading top wear: " + message);
                    }
                });
            }
        });

        // Fetch bottom wear
        flaskNetwork.getUserImages(bottomCategory, new FlaskNetwork.OnImagesLoadedListener() {
            @Override
            public void onSuccess(String[] imageUrls) {
                mainHandler.post(() -> {
                    if (isAdded()) {
                        bottomAdapter.updateItems(imageUrls);
                    }
                });
            }

            @Override
            public void onError(String message) {
                mainHandler.post(() -> {
                    if (isAdded()) {
                        showError("Error loading bottom wear: " + message);
                    }
                });
            }
        });
    }

    private void showError(String message) {
        if (getContext() != null && isAdded()) {
            mainHandler.post(() -> {
                if (isAdded()) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}