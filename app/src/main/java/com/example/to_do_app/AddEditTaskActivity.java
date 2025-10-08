package com.example.to_do_app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class AddEditTaskActivity extends AppCompatActivity {

    private TextInputEditText etTaskTitle, etTaskDescription;
    private TextView tvDueDate;
    private Spinner spinnerPriority;
    private Button btnSaveTask;

    private DatabaseHelper db;
    private Task task; // current task for editing
    private int taskId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        // Initialize views
        etTaskTitle = findViewById(R.id.etTaskTitle);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        tvDueDate = findViewById(R.id.tvDueDate);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        btnSaveTask = findViewById(R.id.btnSaveTask);

        db = new DatabaseHelper(this);

        // Check if we are editing an existing task
        if (getIntent() != null && getIntent().hasExtra("task_id")) {
            taskId = getIntent().getIntExtra("task_id", -1);
            loadTask(taskId);
        }

        // Date picker
        tvDueDate.setOnClickListener(v -> showDatePicker());

        // Save button
        btnSaveTask.setOnClickListener(v -> saveTask());
    }

    private void loadTask(int id) {
        for (Task t : db.getAllTasks()) {
            if (t.getId() == id) {
                task = t;
                break;
            }
        }

        if (task != null) {
            etTaskTitle.setText(task.getTitle());
            etTaskDescription.setText(task.getDescription());
            tvDueDate.setText(task.getDueDate());

            switch (task.getPriority()) {
                case "High":
                    spinnerPriority.setSelection(0);
                    break;
                case "Medium":
                    spinnerPriority.setSelection(1);
                    break;
                case "Low":
                    spinnerPriority.setSelection(2);
                    break;
            }
        }
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year, month, day;

        // If user already selected a date
        if (!tvDueDate.getText().toString().equals("Select date")) {
            String[] parts = tvDueDate.getText().toString().split("-");
            year = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]) - 1; // Month is 0-based
            day = Integer.parseInt(parts[2]);
        } else {
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }

        DatePickerDialog picker = new DatePickerDialog(this,
                (view, y, m, d) -> tvDueDate.setText(String.format("%04d-%02d-%02d", y, m + 1, d)),
                year, month, day);
        picker.show();
    }

    private void saveTask() {
        String title = etTaskTitle.getText().toString().trim();
        String description = etTaskDescription.getText().toString().trim();
        String dueDate = tvDueDate.getText().toString();
        String priority = spinnerPriority.getSelectedItem().toString();

        if (title.isEmpty()) {
            etTaskTitle.setError("Title required");
            etTaskTitle.requestFocus();
            return;
        }

        if (dueDate.equals("Select date")) {
            Toast.makeText(this, "Please select a due date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (taskId == -1) {
            // Add new task
            Task newTask = new Task();
            newTask.setTitle(title);
            newTask.setDescription(description);
            newTask.setDueDate(dueDate);
            newTask.setPriority(priority);
            newTask.setCompleted(false);
            db.addTask(newTask);
            Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();
        } else {
            // Update existing task
            task.setTitle(title);
            task.setDescription(description);
            task.setDueDate(dueDate);
            task.setPriority(priority);
            db.updateTask(task);
            Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();
        }

        finish(); // return to MainActivity
    }
}
