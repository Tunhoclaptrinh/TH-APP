package com.example.bai2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherActivity extends AppCompatActivity {

    private TextView textViewCityName, textViewTemperature, textViewWeatherStatus, textViewHumidity;
    private ImageView imageViewWeatherIcon;

    // Em Xin API KEY CỦA  THẰNG BẠN
    private static final String API_KEY = "05597c4ed4ebf86a8a6d14280bf7ea7a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        textViewCityName = findViewById(R.id.textViewCityName);
        textViewTemperature = findViewById(R.id.textViewTemperature);
        textViewWeatherStatus = findViewById(R.id.textViewWeatherStatus);
        textViewHumidity = findViewById(R.id.textViewHumidity);
        imageViewWeatherIcon = findViewById(R.id.imageViewWeatherIcon);

        String cityName = getIntent().getStringExtra("CITY_NAME");
        if (cityName != null && !cityName.isEmpty()) {
            textViewCityName.setText(cityName);
            new GetWeatherTask().execute(cityName);
        } else {
            Toast.makeText(this, "Không có tên thành phố!", Toast.LENGTH_SHORT).show();
        }
    }

    // 🛰️ AsyncTask để gọi API trên luồng phụ
    private class GetWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String city = params[0];
            String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q="
                    + city + "&appid=" + API_KEY + "&units=metric&lang=vi";
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(WeatherActivity.this, "Không lấy được dữ liệu thời tiết!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject json = new JSONObject(result);
                JSONObject main = json.getJSONObject("main");
                JSONArray weatherArray = json.getJSONArray("weather");
                JSONObject weather = weatherArray.getJSONObject(0);

                double temp = main.getDouble("temp");
                int humidity = main.getInt("humidity");
                String description = weather.getString("description");

                // Cập nhật UI
                textViewTemperature.setText(String.format("%.1f°C", temp));
                textViewHumidity.setText(humidity + "%");
                textViewWeatherStatus.setText(description.substring(0, 1).toUpperCase() + description.substring(1));

                // Đổi icon dựa theo trạng thái
                String mainWeather = weather.getString("main").toLowerCase();
                if (mainWeather.contains("cloud")) {
                    imageViewWeatherIcon.setImageResource(R.drawable.ic_cloud);
                    imageViewWeatherIcon.setColorFilter(ContextCompat.getColor(WeatherActivity.this, R.color.cloud_color));
                } else if (mainWeather.contains("rain")) {
                    imageViewWeatherIcon.setImageResource(R.drawable.ic_rain);
                    imageViewWeatherIcon.setColorFilter(ContextCompat.getColor(WeatherActivity.this, R.color.rain_color));
                } else {
                    imageViewWeatherIcon.setImageResource(R.drawable.ic_sun);
                    imageViewWeatherIcon.setColorFilter(ContextCompat.getColor(WeatherActivity.this, R.color.sun_color));
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(WeatherActivity.this, "Lỗi xử lý dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
