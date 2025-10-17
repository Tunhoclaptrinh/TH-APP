package com.example.bai4.model;
import java.io.Serializable;
import java.util.UUID;

// Lớp Task cần implements Serializable để có thể truyền qua Intent
public class Task implements Serializable {
    private String id;
    private String title;
    private String description;
    private long dueDate; // Lưu dưới dạng timestamp (milliseconds)
    private boolean isCompleted;

    public Task(String title, String description, long dueDate) {
        this.id = UUID.randomUUID().toString(); // Tạo ID duy nhất cho mỗi task
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.isCompleted = false;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}