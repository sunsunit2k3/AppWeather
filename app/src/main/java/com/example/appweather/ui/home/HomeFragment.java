package com.example.appweather.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appweather.R;
import com.example.appweather.UpdateUI;
import com.example.appweather.adapters.HourlyAdapter;
import com.example.appweather.databinding.FragmentHomeBinding;
import com.example.appweather.entities.Hourly;
import com.example.appweather.interfaces.WeatherService;
import com.example.appweather.response.CurrentWeatherResponse;
import com.example.appweather.response.ForecastResponse;
import com.example.appweather.retrofit.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private static final String UNITS = "metric";
    private  static  final String API_KEY="e5afb6abedc33f32a139cf17a8921af6";
    private FragmentHomeBinding binding;
    private ArrayList<Hourly> items;
    private HourlyAdapter hourlyAdapter;
    private RecyclerView recyclerViewHourly;
    private TextView textNameCity, textNext5Days, textDateTime, textState, textTemperature;
    private TextView textPercentHumidity, textWindSpeed, textFeelsLike;
    private ImageView imgIconWeather, imgSearch;
    private EditText editTextSearch;
    private WeatherService weatherService;
    private HomeViewModel homeViewModel;

    @SuppressLint("DiscouragedApi")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Khởi tạo các view
        textTemperature = binding.textTemperature;
        textDateTime = binding.textDateTime;
        textNext5Days = binding.textNext5Days;
        textState = binding.textState;
        textNameCity = binding.textNameCity;
        imgIconWeather = binding.imgIconWeather;
        imgSearch = binding.imgSearch;
        editTextSearch = binding.editTextSearch;
        recyclerViewHourly = binding.recyclerViewHourly;

        // Khởi tạo ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Quan sát dữ liệu từ ViewModel
        homeViewModel.getTextTemperature().observe(getViewLifecycleOwner(), textTemperature::setText);
        homeViewModel.getTextDateTime().observe(getViewLifecycleOwner(), textDateTime::setText);
        homeViewModel.getTextState().observe(getViewLifecycleOwner(), textState::setText);
        homeViewModel.getTextNext5Days().observe(getViewLifecycleOwner(), textNext5Days::setText);
        homeViewModel.getTextNameCity().observe(getViewLifecycleOwner(), textNameCity::setText);

        // Khởi tạo WeatherService
        weatherService = RetrofitClient.getInstance().create(WeatherService.class);

        // Khởi tạo RecyclerView
        items = new ArrayList<>();
        hourlyAdapter = new HourlyAdapter(items);
        recyclerViewHourly.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewHourly.setAdapter(hourlyAdapter);

        // Gọi API dự báo để điền RecyclerView
        fetchForecastData("Hanoi");

        // Xử lý tìm kiếm
        imgSearch.setOnClickListener(v -> {
            String city = editTextSearch.getText().toString().trim();
            if (!city.isEmpty()) {
                homeViewModel.fetchWeatherData(city);
                fetchForecastData(city);
            } else {
                Toast.makeText(getContext(), "Please enter a city name", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý nhấn vào "Next 5 days"
        textNext5Days.setOnClickListener(v -> {
            try {
                Bundle bundle = new Bundle();
                String city = textNameCity != null && textNameCity.getText() != null ? textNameCity.getText().toString() : "Hanoi";
                bundle.putString("city", city);
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_navigation_home_to_futureFragment, bundle);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Navigation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("HomeFragment", "Navigation error", e);
            }
        });

        // Cập nhật icon thời tiết
        weatherService.getCurrentWeather("Hanoi", API_KEY, UNITS).enqueue(new Callback<CurrentWeatherResponse>() {
            @Override
            public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String icon = response.body().getWeather()[0].getIcon();
                    int iconResId = UpdateUI.getIconID(icon);
                    imgIconWeather.setImageResource(iconResId);
                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {
                imgIconWeather.setImageResource(R.drawable.clear_night); // Icon mặc định
            }
        });

        return root;
    }

    private void fetchForecastData(String city) {
        weatherService.getForecast(city, API_KEY, UNITS).enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    items.clear();
                    List<ForecastResponse.HourlyForecast> forecasts = response.body().getList();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    for (ForecastResponse.HourlyForecast forecast : forecasts) {
                        String hour = sdf.format(new Date((long) (forecast.getDt() * 1000)));
                        int temp = (int) forecast.getMain().getTemp();
                        String icon = forecast.getWeather().get(0).getIcon();
                        items.add(new Hourly(hour, temp, icon));
                    }
                    hourlyAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load forecast", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}