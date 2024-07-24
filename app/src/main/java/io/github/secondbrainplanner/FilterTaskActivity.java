package io.github.secondbrainplanner;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Filter;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.github.secondbrainplanner.databinding.ActivityFilterTaskBinding;

public class FilterTaskActivity extends AppCompatActivity {

    private ActivityFilterTaskBinding binding;
    private TaskViewModel taskViewModel;
    private FilterTaskAdapter filterTaskAdapter;
    private String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        Window window = getWindow();
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.light_grey));
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.darker_grey));

        binding = ActivityFilterTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TaskViewModelFactory factory = new TaskViewModelFactory(getApplication());
        taskViewModel = new ViewModelProvider(this, factory).get(TaskViewModel.class);

        filterTaskAdapter = new FilterTaskAdapter(getApplicationContext(), taskViewModel, getSupportFragmentManager(), binding.recyclerView);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(filterTaskAdapter);

        taskViewModel.items.observe(this, items -> filterTaskAdapter.setItems(items));

        binding.newTaskButton.setOnClickListener(view -> new NewTaskSheet(currentDate).show(getSupportFragmentManager(), "newTaskTag"));

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.filter), (view, insets) -> {
            Insets systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemInsets.left, systemInsets.top, systemInsets.right, systemInsets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

    }

    private String getDayOfMonth(Calendar calendar, int dayOfWeek) {
        Calendar cal = (Calendar) calendar.clone();
        cal.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        if (cal.get(Calendar.DAY_OF_WEEK) != dayOfWeek) {
            cal.add(Calendar.DAY_OF_MONTH, dayOfWeek - cal.get(Calendar.DAY_OF_WEEK));
            dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        }
        return String.valueOf(dayOfMonth);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem filterItem = menu.findItem(R.id.action_filter);
        if (filterItem != null && filterItem.getIcon() != null) {
            filterItem.getIcon().setColorFilter(ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_IN);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(FilterTaskActivity.this, SettingsActivity.class);
            FilterTaskActivity.this.startActivity(intent);
            return true;
        } else if (id == R.id.action_checklist) {
            Intent intent = new Intent(FilterTaskActivity.this, CompletedTaskActivity.class);
            FilterTaskActivity.this.startActivity(intent);
            return true;
        } else if (id == R.id.action_filter) {
            Intent intent = new Intent(FilterTaskActivity.this, MainActivity.class);
            FilterTaskActivity.this.startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
