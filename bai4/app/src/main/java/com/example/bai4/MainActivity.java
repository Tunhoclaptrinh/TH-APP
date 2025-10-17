package com.example.bai4;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bai4.model.Task;
import com.example.bai4.model.TaskAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskListener {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList = new ArrayList<>();
    private List<Task> fullTaskList = new ArrayList<>(); // Danh sách đầy đủ trước khi lọc
    private FloatingActionButton fabAddTask;
    private static final String PREFS_NAME = "STaskPrefs";
    private static final String TASKS_KEY = "Tasks";
    private boolean isShowingCompleted = true; // Trạng thái bộ lọc

    private final ActivityResultLauncher<Intent> taskActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Task task = (Task) result.getData().getSerializableExtra("task");
                    int position = result.getData().getIntExtra("position", -1);

                    if (position == -1) { // Thêm mới
                        fullTaskList.add(task);
                    } else { // Cập nhật
                        fullTaskList.set(position, task);
                    }
                    saveAndRefreshTasks();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewTasks);
        fabAddTask = findViewById(R.id.fabAddTask);

        loadTasks();
        setupRecyclerView();

        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TaskActivity.class);
            taskActivityResultLauncher.launch(intent);
        });
    }

    private void setupRecyclerView() {
        taskAdapter = new TaskAdapter(new ArrayList<>(), this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);
        filterTasks();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_task) {
            fabAddTask.performClick();
            return true;
        } else if (id == R.id.action_delete_all) {
            new AlertDialog.Builder(this)
                    .setTitle("Xóa tất cả công việc")
                    .setMessage("Bạn có chắc chắn muốn xóa tất cả công việc không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        fullTaskList.clear();
                        saveAndRefreshTasks();
                        Toast.makeText(this, "Đã xóa tất cả công việc", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
            return true;
        } else if (id == R.id.action_filter) {
            isShowingCompleted = !isShowingCompleted;
            item.setTitle(isShowingCompleted ? "Ẩn công việc đã hoàn thành" : "Hiện công việc đã hoàn thành");
            filterTasks();
            return true;
        }
        else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadTasks() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String tasksJson = prefs.getString(TASKS_KEY, null);
        if (tasksJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Task>>() {}.getType();
            fullTaskList = gson.fromJson(tasksJson, type);
        }
    }

    private void saveTasks() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String tasksJson = gson.toJson(fullTaskList);
        editor.putString(TASKS_KEY, tasksJson);
        editor.apply();

        // Lên lịch thông báo cho các công việc
        for (Task task : fullTaskList) {
            if (!task.isCompleted() && task.getDueDate() > System.currentTimeMillis()) {
                setAlarm(task);
            } else {
                cancelAlarm(task);
            }
        }
    }

    private void saveAndRefreshTasks() {
        saveTasks();
        filterTasks();
    }

    private void filterTasks() {
        if (isShowingCompleted) {
            taskList = new ArrayList<>(fullTaskList);
        } else {
            // Chỉ hiển thị các công việc chưa hoàn thành
            taskList = fullTaskList.stream().filter(t -> !t.isCompleted()).collect(Collectors.toList());
        }
        // Sắp xếp: chưa hoàn thành lên trước, sau đó theo ngày hết hạn
        Collections.sort(taskList, (t1, t2) -> {
            if (t1.isCompleted() != t2.isCompleted()) {
                return t1.isCompleted() ? 1 : -1;
            }
            return Long.compare(t1.getDueDate(), t2.getDueDate());
        });
        taskAdapter.updateTasks(taskList);
    }

    @Override
    public void onTaskClick(int position) {
        Task task = taskList.get(position);
        int originalPosition = fullTaskList.indexOf(task);

        Intent intent = new Intent(this, TaskActivity.class);
        intent.putExtra("task", task);
        intent.putExtra("position", originalPosition);
        taskActivityResultLauncher.launch(intent);
    }

    @Override
    public void onTaskCheckChanged(int position, boolean isChecked) {
        Task task = taskList.get(position);
        task.setCompleted(isChecked);
        saveAndRefreshTasks();
    }

    private void setAlarm(Task task) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("task_title", task.getTitle());
        intent.putExtra("task_id", task.getId());

        // Sử dụng hashCode của ID để tạo requestCode duy nhất
        int requestCode = task.getId().hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            try {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, task.getDueDate(), pendingIntent);
            } catch (SecurityException e) {
                // Xử lý trường hợp không có quyền
                Toast.makeText(this, "Không có quyền đặt báo thức", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cancelAlarm(Task task) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        int requestCode = task.getId().hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
