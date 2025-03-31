package vn.edu.usth.smartwaro.weather;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.usth.smartwaro.network.WeatherApiService;

public class LocationViewModel extends ViewModel {
    private MutableLiveData<List<RemoteLocation>> searchResult = new MutableLiveData<>();

    // Đường dẫn và API key của WeatherAPI
    private static final String BASE_URL = "https://api.weatherapi.com/v1/";
    private static final String API_KEY = "24a2f6bb281c4612912115620252203";

    public LiveData<List<RemoteLocation>> getSearchResult() {
        return searchResult;
    }

    public void searchLocation(final String query) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApiService apiService = retrofit.create(WeatherApiService.class);
        Call<List<RemoteLocation>> call = apiService.searchLocation(API_KEY, query);
        call.enqueue(new Callback<List<RemoteLocation>>() {
            @Override
            public void onResponse(Call<List<RemoteLocation>> call, Response<List<RemoteLocation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    searchResult.postValue(response.body());
                } else {
                    // Nếu API không trả về kết quả hợp lệ, có thể post giá trị rỗng hoặc thông báo lỗi
                    searchResult.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<RemoteLocation>> call, Throwable t) {
                // Xử lý lỗi (ví dụ log lỗi)
                searchResult.postValue(null);
            }
        });
    }
}
