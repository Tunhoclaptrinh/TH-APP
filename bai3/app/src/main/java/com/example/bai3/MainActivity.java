package com.example.bai3;


import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ZoomableImageView imageView;
    private GridView gridView;
    private TextView tvImageName, tvImageDesc;
    private Button btnPrevious, btnNext, btnAddImage;
    private AppCompatImageButton btnFavorite, btnPlayPause, btnTheme;
    private int currentPosition = 0;
    private boolean isPlaying = false;
    private boolean isDarkMode = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable slideshowRunnable;
    private Set<String> favoriteImages = new HashSet<>();
    private SharedPreferences sharedPreferences;
    private static final String FAVORITES_KEY = "favorite_images";
    private static final String THEME_KEY = "dark_mode";
    private static final String IMAGE_URIS_KEY = "image_uris";
    private static final long SLIDESHOW_INTERVAL = 3000;
    private static final int PICK_IMAGE_REQUEST = 1;
    private List<String> imageUriList = new ArrayList<>();
    private List<String> imageNameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("PhotoGalleryPrefs", MODE_PRIVATE);
        isDarkMode = sharedPreferences.getBoolean(THEME_KEY, false);

        applyTheme();
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        gridView = findViewById(R.id.gridView);
        tvImageName = findViewById(R.id.tvImageName);
        tvImageDesc = findViewById(R.id.tvImageDesc);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnAddImage = findViewById(R.id.btnAddImage);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnTheme = findViewById(R.id.btnTheme);

        loadFavorites();
        loadDefaultImages();
        loadSavedImages();

        ImageAdapter adapter = new ImageAdapter(this, imageUriList);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            currentPosition = position;
            updateImage();
        });

        btnPrevious.setOnClickListener(v -> {
            if (currentPosition > 0) {
                currentPosition--;
                updateImage();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentPosition < imageUriList.size() - 1) {
                currentPosition++;
                updateImage();
            }
        });

        btnAddImage.setOnClickListener(v -> openImagePicker());

        btnFavorite.setOnClickListener(v -> toggleFavorite());

        btnPlayPause.setOnClickListener(v -> toggleSlideshow());

        btnTheme.setOnClickListener(v -> toggleTheme());

        slideshowRunnable = new Runnable() {
            @Override
            public void run() {
                if (isPlaying) {
                    if (currentPosition < imageUriList.size() - 1) {
                        currentPosition++;
                    } else {
                        currentPosition = 0;
                    }
                    updateImage();
                    handler.postDelayed(this, SLIDESHOW_INTERVAL);
                }
            }
        };

        updateImage();
    }

    private void loadDefaultImages() {
        // Thêm ảnh mặc định từ drawable
        imageUriList.add("drawable://avatar");
        imageUriList.add("drawable://cover");
        imageUriList.add("drawable://frame");
        imageUriList.add("drawable://bg");
        imageUriList.add("drawable://ptit");

        imageNameList.add("Avatar");
        imageNameList.add("Cover");
        imageNameList.add("Frame");
        imageNameList.add("Background");
        imageNameList.add("PTIT Logo");
    }

    private void loadSavedImages() {
        Set<String> savedUris = sharedPreferences.getStringSet(IMAGE_URIS_KEY, new HashSet<>());
        imageUriList.addAll(savedUris);

        // Tạo tên cho các ảnh đã lưu
        for (String uri : savedUris) {
            imageNameList.add(getImageNameFromUri(Uri.parse(uri)));
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                imageUriList.add(selectedImageUri.toString());
                imageNameList.add(getImageNameFromUri(selectedImageUri));

                // Lưu vào SharedPreferences
                Set<String> uriSet = new HashSet<>(imageUriList);
                sharedPreferences.edit().putStringSet(IMAGE_URIS_KEY, uriSet).apply();

                // Cập nhật adapter
                ImageAdapter adapter = (ImageAdapter) gridView.getAdapter();
                adapter.notifyDataSetChanged();

                currentPosition = imageUriList.size() - 1;
                updateImage();
            }
        }
    }

    private String getImageNameFromUri(Uri uri) {
        String name = "Image";
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                if (nameIndex >= 0) {
                    name = cursor.getString(nameIndex);
                    // Xóa phần mở rộng
                    int lastDot = name.lastIndexOf('.');
                    if (lastDot > 0) {
                        name = name.substring(0, lastDot);
                    }
                }
                cursor.close();
            }
        }
        return name;
    }

    private void updateImage() {
        String uriString = imageUriList.get(currentPosition);

        if (uriString.startsWith("drawable://")) {
            String drawableName = uriString.replace("drawable://", "");
            int drawableId = getResources().getIdentifier(drawableName, "drawable", getPackageName());
            imageView.setImageResource(drawableId);
        } else {
            imageView.setImageURI(Uri.parse(uriString));
        }

        String name = imageNameList.get(currentPosition);
        tvImageName.setText(name + " (" + (currentPosition + 1) + "/" + imageUriList.size() + ")");

        // Hiển thị thông tin ảnh
        tvImageDesc.setText("Nhấn để phóng to ảnh");

        btnPrevious.setEnabled(currentPosition > 0);
        btnNext.setEnabled(currentPosition < imageUriList.size() - 1);

        updateFavoriteButton();
    }

    private void toggleFavorite() {
        String currentUri = imageUriList.get(currentPosition);
        if (favoriteImages.contains(currentUri)) {
            favoriteImages.remove(currentUri);
        } else {
            favoriteImages.add(currentUri);
        }
        saveFavorites();
        updateFavoriteButton();
    }

    private void updateFavoriteButton() {
        String currentUri = imageUriList.get(currentPosition);
        if (favoriteImages.contains(currentUri)) {
            btnFavorite.setColorFilter(Color.RED);
        } else {
            btnFavorite.setColorFilter(Color.GRAY);
        }
    }

    private void toggleSlideshow() {
        isPlaying = !isPlaying;
        if (isPlaying) {
            btnPlayPause.setColorFilter(Color.GREEN);
            handler.postDelayed(slideshowRunnable, SLIDESHOW_INTERVAL);
        } else {
            btnPlayPause.setColorFilter(Color.GRAY);
            handler.removeCallbacks(slideshowRunnable);
        }
    }

    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        sharedPreferences.edit().putBoolean(THEME_KEY, isDarkMode).apply();
        applyTheme();
        recreate();
    }

    private void applyTheme() {
        if (isDarkMode) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }
    }

    private void saveFavorites() {
        sharedPreferences.edit()
                .putStringSet(FAVORITES_KEY, favoriteImages)
                .apply();
    }

    private void loadFavorites() {
        favoriteImages = new HashSet<>(sharedPreferences.getStringSet(FAVORITES_KEY, new HashSet<>()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(slideshowRunnable);
    }
}