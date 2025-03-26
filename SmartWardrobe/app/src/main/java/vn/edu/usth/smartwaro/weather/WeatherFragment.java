package vn.edu.usth.smartwaro.weather;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import vn.edu.usth.smartwaro.fragment.RandomFragment;
import vn.edu.usth.smartwaro.network.FlaskNetwork;
import vn.edu.usth.smartwaro.network.WeatherApiService;
import vn.edu.usth.smartwaro.network.WeatherResponse;

public class WeatherFragment extends Fragment {
    private static final String TAG = "WeatherFragment";
    private static final String BASE_URL = "https://api.weatherapi.com/v1/";
    private static final String API_KEY = "9db56aef06a0411e81d121533242812";

    private TextView tempTextView;
    private TextView locationTextView;
    private TextView conditionTextView;
    private TextView recommendationTextView; // Add this to show the AI recommendation explanation
    private RecyclerView topClothingRecyclerView;
    private RecyclerView bottomClothingRecyclerView;
    private ClothingAdapter topAdapter;
    private ClothingAdapter bottomAdapter;
    private FlaskNetwork flaskNetwork;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private String userId;
    private Button randombutton;
    private TextView humidityTextView;
    private TextView windSpeedTextView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        randombutton=view.findViewById(R.id.random_frag);
        randombutton.setOnClickListener(v -> openFragment( new RandomFragment()));


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
        recommendationTextView = view.findViewById(R.id.recommendationText); // Make sure to add this in your layout
        topClothingRecyclerView = view.findViewById(R.id.topClothingRecyclerView);
        bottomClothingRecyclerView = view.findViewById(R.id.bottomClothingRecyclerView);
        humidityTextView = view.findViewById(R.id.humidityText);
        windSpeedTextView = view.findViewById(R.id.windSpeedText);
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
        Call<WeatherResponse> call = apiService.getCurrentWeather(API_KEY, "USA");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                mainHandler.post(() -> {
                    if (!isAdded()) return;

                    if (response.isSuccessful() && response.body() != null) {
                        updateWeatherUI(response.body());
                        if (userId != null) {
                            double temperature = response.body().getCurrent().getTempC();
                            int humidity = response.body().getCurrent().getHumidity();
                            double windSpeed = response.body().getCurrent().getWindKph();
                            fetchClothingRecommendations(temperature, humidity, windSpeed);

                            // Update recommendation text based on temperature
                            // updateRecommendationText(temperature);
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
        int humidity = weather.getCurrent().getHumidity();
        double windSpeed = weather.getCurrent().getWindKph();

        locationTextView.setText(location);
        tempTextView.setText(String.format("%.1f °C", temperature));
        conditionTextView.setText(condition);
        humidityTextView.setText("Humidity: " + humidity + "%");
        windSpeedTextView.setText(String.format("Wind Speed: %.1f km/h", windSpeed));
    }

    private void fetchClothingRecommendations(double temperature, int humidity, double windSpeed) {
        flaskNetwork.getRecommendedClothing(temperature, humidity, windSpeed, new FlaskNetwork.OnRecommendationReceivedListener() {
            @Override
            public void onSuccess(String topWear, String bottomWear) {
                mainHandler.post(() -> {
                    if (isAdded()) {
                        // Hiển thị thông tin đề xuất trên recommendationTextView
                        String recommendationText = topWear + " + " + bottomWear;
                        recommendationTextView.setText(recommendationText);

                        // Tìm và hiển thị ảnh áo
                        flaskNetwork.getUserImages(topWear, new FlaskNetwork.OnImagesLoadedListener() {
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
                                mainHandler.post(() -> showError("Lỗi tải ảnh áo: " + message));
                            }
                        });

                        // Tìm và hiển thị ảnh quần
                        flaskNetwork.getUserImages(bottomWear, new FlaskNetwork.OnImagesLoadedListener() {
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
                                mainHandler.post(() -> showError("Lỗi tải ảnh quần: " + message));
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(String message) {
                mainHandler.post(() -> showError(message));
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

    private void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();


    }
}