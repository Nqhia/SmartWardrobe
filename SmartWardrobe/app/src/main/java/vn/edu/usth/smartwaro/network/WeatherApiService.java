package vn.edu.usth.smartwaro.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import vn.edu.usth.smartwaro.weather.RemoteLocation;

public interface WeatherApiService {
    @GET("current.json")
    Call<WeatherResponse> getCurrentWeather(
            @Query("key") String apiKey,
            @Query("q") String location
    );

    @GET("search.json")
    Call<List<RemoteLocation>> searchLocation(
            @Query("key") String apiKey,
            @Query("q") String query
    );
}
