package io.github.secondbrainplanner;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import io.github.secondbrainplanner.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private TaskViewModel taskViewModel;
    private TaskAdapter taskAdapter;
    private TextView textViewMon, textViewTue, textViewWed, textViewThu, textViewFri, textViewSat, textViewSun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        textViewMon = findViewById(R.id.textViewMon);
        textViewTue = findViewById(R.id.textViewTue);
        textViewWed = findViewById(R.id.textViewWed);
        textViewThu = findViewById(R.id.textViewThu);
        textViewFri = findViewById(R.id.textViewFri);
        textViewSat = findViewById(R.id.textViewSat);
        textViewSun = findViewById(R.id.textViewSun);

        TaskViewModelFactory factory = new TaskViewModelFactory(getApplication());
        taskViewModel = new ViewModelProvider(this, factory).get(TaskViewModel.class);

        taskAdapter = new TaskAdapter(taskViewModel, getSupportFragmentManager());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(taskAdapter);

        taskViewModel.items.observe(this, items -> taskAdapter.setItems(items));

        taskViewModel.items.observe(this, new Observer<List<Object>>() {
            @Override
            public void onChanged(List<Object> items) {
                taskAdapter.setItems(items);
            }
        });

        binding.newTaskButton.setOnClickListener(view -> {
            new NewTaskSheet().show(getSupportFragmentManager(), "newTaskTag");
        });

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                updateHighlightedWeekDay();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
