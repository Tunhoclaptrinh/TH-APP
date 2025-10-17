package com.example.bai4.model;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bai4.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private final OnTaskListener onTaskListener;
    private final Context context;

    public TaskAdapter(List<Task> taskList, Context context, OnTaskListener onTaskListener) {
        this.taskList = taskList;
        this.context = context;
        this.onTaskListener = onTaskListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view, onTaskListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.textViewTitle.setText(task.getTitle());
        holder.checkBoxCompleted.setChecked(task.isCompleted());

        // Định dạng ngày hết hạn
        if (task.getDueDate() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            holder.textViewDueDate.setText(sdf.format(new Date(task.getDueDate())));
            holder.textViewDueDate.setVisibility(View.VISIBLE);
        } else {
            holder.textViewDueDate.setVisibility(View.GONE);
        }

        // Gạch ngang text nếu công việc đã hoàn thành
        updateTaskAppearance(holder, task.isCompleted());

        holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                onTaskListener.onTaskCheckChanged(holder.getAdapterPosition(), isChecked);
                updateTaskAppearance(holder, isChecked);
            }
        });
    }

    private void updateTaskAppearance(TaskViewHolder holder, boolean isCompleted) {
        if (isCompleted) {
            holder.textViewTitle.setPaintFlags(holder.textViewTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.itemView.setAlpha(0.5f);
        } else {
            holder.textViewTitle.setPaintFlags(holder.textViewTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.itemView.setAlpha(1.0f);
        }
    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void updateTasks(List<Task> newTasks) {
        this.taskList = newTasks;
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewTitle, textViewDueDate;
        CheckBox checkBoxCompleted;
        OnTaskListener onTaskListener;

        public TaskViewHolder(@NonNull View itemView, OnTaskListener onTaskListener) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTaskTitle);
            textViewDueDate = itemView.findViewById(R.id.textViewTaskDueDate);
            checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
            this.onTaskListener = onTaskListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onTaskListener.onTaskClick(getAdapterPosition());
        }
    }

    public interface OnTaskListener {
        void onTaskClick(int position);
        void onTaskCheckChanged(int position, boolean isChecked);
    }
}