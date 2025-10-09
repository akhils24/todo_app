package com.example.to_do_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView tvProgress;
    private RecyclerView recyclerViewTasks;
    private TaskAdapter taskAdapter;
    private DatabaseHelper db;
    private List<Task> taskList;
    private FloatingActionButton btnAddTask;
    private Spinner spinnerFilter;

    private static final int ADD_TASK_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        btnAddTask = findViewById(R.id.btnAddTask);
        tvProgress = findViewById(R.id.tvProgress);
        progressBar = findViewById(R.id.progressBar);
        spinnerFilter = findViewById(R.id.spinnerFilter);

        // Initialize Database
        db = new DatabaseHelper(this);

        // Load tasks
        loadTasks();

        // Add Task FAB click
        btnAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            startActivityForResult(intent, ADD_TASK_REQUEST);
        });

        // Filter spinner listener
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String filter = parent.getItemAtPosition(position).toString();
                if (filter.equals("All")) {
                    taskList = db.getAllTasks();
                } else {
                    taskList = db.getTasksByPriority(filter);
                }
                taskAdapter.updateTasks(taskList);
                updateProgress();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadTasks() {
        taskList = db.getAllTasks();
        taskAdapter = new TaskAdapter(this, taskList, db);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTasks.setAdapter(taskAdapter);
        updateProgress();
    }

    public void updateProgress() {
        int totalTasks = db.getAllTasks().size();
        int completedTasks = db.getCompletedTaskCount();
        int progress = totalTasks == 0 ? 0 : (completedTasks * 100) / totalTasks;

        progressBar.setProgress(progress);
        String progressText = completedTasks + " of " + totalTasks + " tasks completed";
        tvProgress.setText(progressText);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }
}
