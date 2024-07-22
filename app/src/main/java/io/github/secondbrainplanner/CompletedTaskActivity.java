package io.github.secondbrainplanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.secondbrainplanner.databinding.ActivityCompletedTasksBinding;

public class CompletedTaskActivity extends AppCompatActivity {

    private ActivityCompletedTasksBinding binding;
    private TaskViewModel taskViewModel;
    private CompletedTaskAdapter completedTaskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCompletedTasksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TaskViewModelFactory factory = new TaskViewModelFactory(getApplication());
        taskViewModel = new ViewModelProvider(this, factory).get(TaskViewModel.class);

        completedTaskAdapter = new CompletedTaskAdapter(taskViewModel, getSupportFragmentManager(), binding.recyclerViewCompletedTasks, task -> {
            String title = task.getTitle();
            String description = task.getDescription();
            long created_at = task.getCreated_at();
            long due_date = task.getDue_date();
            int completed = 0;
            long completed_at = 0;
            long updated_at = task.getUpdated_at();
            Task newTask = new Task(title, description, created_at, due_date, completed, completed_at, updated_at);
            newTask.setId(task.getId());
            taskViewModel.uncompleteTask(newTask);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        binding.recyclerViewCompletedTasks.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewCompletedTasks.setAdapter(completedTaskAdapter);

        taskViewModel.getCompletedTasks().observe(this, completedTasks -> completedTaskAdapter.setItems(completedTasks));
    }
}
