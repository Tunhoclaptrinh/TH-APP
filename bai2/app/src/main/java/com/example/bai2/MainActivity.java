package com.example.bai2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText editTextCity;
    private Button buttonGetWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ các view từ file layout
        editTextCity = findViewById(R.id.editTextCity);
        buttonGetWeather = findViewById(R.id.buttonGetWeather);

        // Xử lý sự kiện khi người dùng nhấn nút
        buttonGetWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = editTextCity.getText().toString().trim();

                // Kiểm tra xem người dùng đã nhập thành phố chưa
                if (city.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập tên thành phố", Toast.LENGTH_SHORT).show();
                } else {
                    // Tạo Intent để chuyển sang màn hình WeatherActivity
                    Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                    // Đính kèm tên thành phố vào Intent
                    intent.putExtra("CITY_NAME", city);
                    startActivity(intent);
                }
            }
        });
    }

    // Tạo menu trên thanh action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    // Xử lý sự kiện khi một item trên menu được chọn
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_about) {
            showAboutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Hiển thị AlertDialog giới thiệu ứng dụng
    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Giới thiệu ứng dụng")
                .setMessage("MyWeather là một ứng dụng dự báo thời tiết đơn giản.\n\nPhiên bản: 1.0\nPhát triển bởi: Gemini")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }
}