package com.example.bai4;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bai4.model.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TaskActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription;
    private Button buttonSave, buttonSelectDate;
    private Calendar calendar = Calendar.getInstance();
    private Task currentTask;
    private int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        editTextTitle = findViewById(R.id.editTextTaskTitle);
        editTextDescription = findViewById(R.id.editTextTaskDescription);
        buttonSave = findViewById(R.id.buttonSaveTask);
        buttonSelectDate = findViewById(R.id.buttonSelectDate);

        Intent intent = getIntent();
        if (intent.hasExtra("task")) {
            currentTask = (Task) intent.getSerializableExtra("task");
            position = intent.getIntExtra("position", -1);
            editTextTitle.setText(currentTask.getTitle());
            editTextDescription.setText(currentTask.getDescription());
            calendar.setTimeInMillis(currentTask.getDueDate());
            setTitle("Sửa công việc");
        } else {
            setTitle("Thêm công việc mới");
        }

        updateDateButton();

        buttonSelectDate.setOnClickListener(v -> showDatePickerDialog());
        buttonSave.setOnClickListener(v -> saveTask());
    }

    private void showDatePickerDialog() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateButton();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateButton() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        buttonSelectDate.setText(sdf.format(calendar.getTime()));
    }

    private void saveTask() {
        String title = editTextTitle.getText().toString().trim();
        if (title.isEmpty()) {
            editTextTitle.setError("Tiêu đề không được để trống");
            return;
        }
        String description = editTextDescription.getText().toString().trim();
        long dueDate = calendar.getTimeInMillis();

        Task task;
        if (currentTask == null) {
            task = new Task(title, description, dueDate);
        } else {
            currentTask.setTitle(title);
            currentTask.setDescription(description);
            currentTask.setDueDate(dueDate);
            task = currentTask;
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("task", task);
        resultIntent.putExtra("position", position);
        setResult(RESULT_OK, resultIntent);

        Toast.makeText(this, "Đã lưu công việc", Toast.LENGTH_SHORT).show();
        finish();
    }
}