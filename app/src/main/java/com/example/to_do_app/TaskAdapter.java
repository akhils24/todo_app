package com.example.to_do_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context;
    private List<Task> taskList;
    private DatabaseHelper db;

    public TaskAdapter(Context context, List<Task> taskList, DatabaseHelper db) {
        this.context = context;
        this.taskList = taskList;
        this.db = db;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        // Set data
        holder.tvTaskTitle.setText(task.getTitle());
        holder.tvDueDate.setText("Due: " + task.getDueDate());
        holder.checkBoxComplete.setChecked(task.isCompleted());

        // Strike-through if completed
        if (task.isCompleted()) {
            holder.tvTaskTitle.setPaintFlags(holder.tvTaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tvTaskTitle.setPaintFlags(holder.tvTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // Priority Badge
        switch (task.getPriority()) {
            case "High":
                holder.tvPriority.setBackgroundResource(R.drawable.bg_priority_high);
                holder.tvPriority.setText("High");
                break;
            case "Medium":
                holder.tvPriority.setBackgroundResource(R.drawable.bg_priority_medium);
                holder.tvPriority.setText("Medium");
                break;
            case "Low":
                holder.tvPriority.setBackgroundResource(R.drawable.bg_priority_low);
                holder.tvPriority.setText("Low");
                break;
        }

        // ✅ Checkbox click (mark complete/incomplete)
        holder.checkBoxComplete.setOnClickListener(v -> {
            task.setCompleted(holder.checkBoxComplete.isChecked());
            db.updateTask(task);
            notifyItemChanged(position);
        });

        // ✏️ Edit Button
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditTaskActivity.class);
            intent.putExtra("task_id", task.getId());
            context.startActivity(intent);
        });

        // ❌ Delete Button
        holder.btnDelete.setOnClickListener(v -> {
            db.deleteTask(task.getId());
            taskList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, taskList.size());
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskTitle, tvDueDate, tvPriority;
        CheckBox checkBoxComplete;
        ImageButton btnEdit, btnDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            checkBoxComplete = itemView.findViewById(R.id.checkBoxComplete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
