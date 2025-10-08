package com.example.to_do_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTasks;
    private TaskAdapter taskAdapter;
    private DatabaseHelper db;
    private List<Task> taskList;
    private FloatingActionButton btnAddTask;
    private TextView tvProgress;

    private static final int ADD_TASK_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        btnAddTask = findViewById(R.id.btnAddTask);
        tvProgress = findViewById(R.id.tvProgress);

        // Initialize Database
        db = new DatabaseHelper(this);

        // Load tasks
        loadTasks();

        // Add Task FAB click
        btnAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, com.example.to_do_app.AddEditTaskActivity.class);
//            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
//            startActivityForResult(intent, ADD_TASK_REQUEST);
            startActivity(intent);
        });
    }

    private void loadTasks() {
        taskList = db.getAllTasks();

        // Setup RecyclerView
        taskAdapter = new TaskAdapter(this, taskList, db);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTasks.setAdapter(taskAdapter);

        // Update progress summary
        updateProgress();
    }

    private void updateProgress() {
        int total = taskList.size();
        int completed = 0;
        for (Task task : taskList) {
            if (task.isCompleted()) completed++;
        }
        tvProgress.setText(completed + " of " + total + " tasks completed");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload tasks in case any were added/edited
        loadTasks();
    }
}
