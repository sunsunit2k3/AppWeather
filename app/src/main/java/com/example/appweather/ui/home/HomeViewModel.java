package com.example.appweather.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appweather.interfaces.WeatherService;
import com.example.appweather.response.CurrentWeatherResponse;
import com.example.appweather.retrofit.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {
    private static final String UNITS = "metric";
    private final MutableLiveData<String> textTemperature;
    private final MutableLiveData<String> textDateTime;
    private final MutableLiveData<String> textNext5Days;
    private final MutableLiveData<String> textState;
    private final MutableLiveData<String> textNameCity;
    private final MutableLiveData<String> textHumidity;
    private final MutableLiveData<String> textWindSpeed;
    private final MutableLiveData<String> textFeelsLike;


    private final WeatherService weatherService;

    public HomeViewModel() {
        textTemperature = new MutableLiveData<>();
        textDateTime = new MutableLiveData<>();
        textNext5Days = new MutableLiveData<>();
        textState = new MutableLiveData<>();
        textNameCity = new MutableLiveData<>();
        textHumidity = new MutableLiveData<>();
        textWindSpeed = new MutableLiveData<>();
        textFeelsLike = new MutableLiveData<>();

        // Khởi tạo WeatherService
        weatherService = RetrofitClient.getInstance().create(WeatherService.class);

        // Dữ liệu mặc định
        textNext5Days.setValue("Next 5 days");
        fetchWeatherData("Hanoi"); // Gọi API cho thành phố mặc định
    }

    public void fetchWeatherData(String city) {
        weatherService.getCurrentWeather(city, "e5afb6abedc33f32a139cf17a8921af6", UNITS).enqueue(new Callback<CurrentWeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<CurrentWeatherResponse> call, @NonNull Response<CurrentWeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CurrentWeatherResponse data = response.body();
                    textNameCity.setValue(data.getName());
                    textState.setValue(data.getSys().getCountry());
                    textTemperature.setValue(String.format(Locale.getDefault(), "%.0f°C", data.getMain().getTemp()));
                    textHumidity.setValue(String.format(Locale.getDefault(), "%d%%", data.getMain().getHumidity()));
                    textWindSpeed.setValue(String.format(Locale.getDefault(), "%.1f m/s", data.getWind().getSpeed()));
                    textFeelsLike.setValue(String.format(Locale.getDefault(), "%.0f°C", data.getMain().getFeelsLike()));
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    textDateTime.setValue(sdf.format(new Date((long) (data.getDateTime() * 1000))));
                }else {
                    textTemperature.setValue("Error");
                    textNameCity.setValue("Error");
                    textHumidity.setValue("Error");
                    textWindSpeed.setValue("Error");
                    textFeelsLike.setValue("Error");
                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {
                textTemperature.setValue("Error");
                textNameCity.setValue("Error");
                textHumidity.setValue("Error");
                textWindSpeed.setValue("Error");
                textFeelsLike.setValue("Error");
            }
        });
    }

    public LiveData<String> getTextTemperature() {
        return textTemperature;
    }

    public LiveData<String> getTextDateTime() {
        return textDateTime;
    }

    public LiveData<String> getTextNameCity() {
        return textNameCity;
    }

    public LiveData<String> getTextState() {
        return textState;
    }

    public LiveData<String> getTextNext5Days() {
        return textNext5Days;
    }

    public LiveData<String> getTextHumidity() {
        return textHumidity;
    }

    public LiveData<String> getTextWindSpeed() {
        return textWindSpeed;
    }

    public LiveData<String> getTextFeelsLike() {
        return textFeelsLike;
    }
}