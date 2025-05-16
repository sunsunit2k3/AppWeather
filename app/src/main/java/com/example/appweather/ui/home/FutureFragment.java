package com.example.appweather.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appweather.R;
import com.example.appweather.adapters.FutureAdapter;
import com.example.appweather.databinding.FragmentFutureBinding;
import com.example.appweather.entities.FutureDomain;
import com.example.appweather.interfaces.WeatherService;
import com.example.appweather.response.ForecastResponse;
import com.example.appweather.retrofit.RetrofitClient;

import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FutureFragment extends Fragment {
    private FragmentFutureBinding binding;
    private ArrayList<FutureDomain> items;
    private FutureAdapter futureAdapter;
    private WeatherService weatherService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFutureBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Khởi tạo RecyclerView
        items = new ArrayList<>();
        futureAdapter = new FutureAdapter(items);
        RecyclerView recyclerViewFuture = binding.recyclerViewFuture;
        if (recyclerViewFuture != null) {
            recyclerViewFuture.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerViewFuture.setAdapter(futureAdapter);
        } else {
            Toast.makeText(getContext(), "RecyclerView not found", Toast.LENGTH_SHORT).show();
            return root;
        }

        // Khởi tạo WeatherService
        weatherService = RetrofitClient.getInstance().create(WeatherService.class);

        // Lấy tên thành phố từ arguments (nếu có) hoặc mặc định là "Hanoi"
        String city = getArguments() != null ? getArguments().getString("city", "Hanoi") : "Hanoi";
        fetchForecastData(city);

        return root;
    }
    private void setIntentExtras() {
        String city = editTextSearch.getText().toString();
        Intent intent = new Intent(HomeFragment.this, FutureFragment.class);
        intent.putExtra("name", city);
        intent.putExtra("state", textState.getText().toString());
        intent.putExtra("temperature", textTemperature.getText().toString());
        intent.putExtra("feelsLike", textFeelsLike.getText().toString());
        intent.putExtra("windSpeed", textWindSpeed.getText().toString());
        intent.putExtra("humidity", textPercentHumidity.getText().toString());
        intent.putExtra("imgIconWeather", icon);
        startActivity(intent);
    }
    private void fetchForecastData(String city) {
        weatherService.getForecast(city, "e5afb6abedc33f32a139cf17a8921af6", "metric").enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    items.clear();
                    List<ForecastResponse.HourlyForecast> forecasts = response.body().getList();
                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
                    Calendar calendar = Calendar.getInstance();
                    String currentDay = "";

                    // Nhóm dữ liệu theo ngày (lấy 5 ngày)
                    for (int i = 0; i < forecasts.size() && items.size() < 5; i++) {
                        ForecastResponse.HourlyForecast forecast = forecasts.get(i);
                        calendar.setTimeInMillis((long) (forecast.getDt() * 1000));
                        String day = sdf.format(calendar.getTime());

                        // Chỉ thêm khi ngày thay đổi
                        if (!day.equals(currentDay)) {
                            String status = forecast.getWeather().get(0).getMain();
                            String icon = forecast.getWeather().get(0).getIcon();
                            int highTemp = (int) forecast.getMain().getTempMax();
                            int lowTemp = (int) forecast.getMain().getTempMin();
                            items.add(new FutureDomain(day, icon, status, highTemp, lowTemp));
                            currentDay = day;
                        }
                    }
                    futureAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to load forecast: Invalid response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load forecast: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}