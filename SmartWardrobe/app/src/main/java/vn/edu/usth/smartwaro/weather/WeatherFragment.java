package vn.edu.usth.smartwaro.weather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.edu.usth.smartwaro.R;
import vn.edu.usth.smartwaro.network.WeatherApiService;
import vn.edu.usth.smartwaro.network.WeatherResponse;

public class WeatherFragment extends Fragment {
    private static final String BASE_URL = "https://api.weatherapi.com/v1/";
    private static final String API_KEY = "9db56aef06a0411e81d121533242812"; // Replace with your WeatherAPI key

    private TextView tempTextView, locationTextView, conditionTextView;
    private ImageView conditionIcon;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        tempTextView = view.findViewById(R.id.temp);
        locationTextView = view.findViewById(R.id.locationText);
        conditionTextView = view.findViewById(R.id.conditionText);

        fetchWeatherData();

        return view;
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
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();

                    String location = weather.getLocation().getName() + ", " + weather.getLocation().getCountry();
                    String condition = weather.getCurrent().getCondition().getText();
                    double temperature = weather.getCurrent().getTempC();

                    locationTextView.setText(location);
                    tempTextView.setText(String.format("%.1f °C", temperature));
                    conditionTextView.setText(condition);
                } else {
                    Toast.makeText(getContext(), "Failed to load weather data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
