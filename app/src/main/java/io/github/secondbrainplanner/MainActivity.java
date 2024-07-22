package io.github.secondbrainplanner;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
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

import io.github.secondbrainplanner.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements TaskAdapter.onDateClickListener {

    private ActivityMainBinding binding;
    private TaskViewModel taskViewModel;
    private TaskAdapter taskAdapter;
    private TextView textViewMon, textViewTue, textViewWed, textViewThu, textViewFri, textViewSat, textViewSun;
    private TextView textViewMonNum, textViewTueNum, textViewWedNum, textViewThuNum, textViewFriNum, textViewSatNum, textViewSunNum;
    private TextView monthAndYear;
    private String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        Window window = getWindow();
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.light_grey));
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.darker_grey));

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        monthAndYear = findViewById(R.id.monthAndYear);

        textViewMon = findViewById(R.id.textViewMon);
        textViewTue = findViewById(R.id.textViewTue);
        textViewWed = findViewById(R.id.textViewWed);
        textViewThu = findViewById(R.id.textViewThu);
        textViewFri = findViewById(R.id.textViewFri);
        textViewSat = findViewById(R.id.textViewSat);
        textViewSun = findViewById(R.id.textViewSun);

        textViewMonNum = findViewById(R.id.textViewMonNum);
        textViewTueNum = findViewById(R.id.textViewTueNum);
        textViewWedNum = findViewById(R.id.textViewWedNum);
        textViewThuNum = findViewById(R.id.textViewThuNum);
        textViewFriNum = findViewById(R.id.textViewFriNum);
        textViewSatNum = findViewById(R.id.textViewSatNum);
        textViewSunNum = findViewById(R.id.textViewSunNum);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TaskViewModelFactory factory = new TaskViewModelFactory(getApplication());
        taskViewModel = new ViewModelProvider(this, factory).get(TaskViewModel.class);

        taskAdapter = new TaskAdapter(taskViewModel, getSupportFragmentManager(), binding.recyclerView, this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(taskAdapter);

        taskViewModel.items.observe(this, items -> taskAdapter.setItems(items));

        binding.newTaskButton.setOnClickListener(view -> new NewTaskSheet(currentDate).show(getSupportFragmentManager(), "newTaskTag"));

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                updateMonthAndYear();
                updateDateNumbers();
                updateHighlightedWeekDay();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
            Insets systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemInsets.left, systemInsets.top, systemInsets.right, systemInsets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        SimpleDateFormat monthDateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        String currentMonthAndYear = monthDateFormat.format(Calendar.getInstance().getTime());
        monthAndYear.setText(currentMonthAndYear);

        resetWeekDayHighlights();
    }
    private void updateMonthAndYear() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
        if (layoutManager != null) {
            int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
            Object item = taskAdapter.getItemAtPosition(firstVisiblePosition);
            if (item instanceof Long) {
                long dateInMillis = (Long) item;
                Calendar calendar = CalendarUtils.getGermanCalendar();
                calendar.setTimeInMillis(dateInMillis);
                SimpleDateFormat monthAndYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                String monthAndYearString = monthAndYearFormat.format(calendar.getTime());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                currentDate = dateFormat.format(calendar.getTime());
                monthAndYear.setText(monthAndYearString);
            }
        }
    }

    private void updateHighlightedWeekDay() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
        if (layoutManager != null) {
            int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
            Object item = taskAdapter.getItemAtPosition(firstVisiblePosition);
            if (item instanceof Long) {
                long dateInMillis = (Long) item;
                Calendar calendar = CalendarUtils.getGermanCalendar();
                calendar.setTimeInMillis(dateInMillis);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                highlightWeekDay(dayOfWeek);
            }
        }
    }

    private void highlightWeekDay(int dayOfWeek) {
        resetWeekDayHighlights();
        int topBarHighlightingColor = ContextCompat.getColor(this, R.color.topBarHighlightingColor);
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                textViewMon.setBackgroundColor(topBarHighlightingColor);
                textViewMonNum.setBackgroundColor(topBarHighlightingColor);
                break;
            case Calendar.TUESDAY:
                textViewTue.setBackgroundColor(topBarHighlightingColor);
                textViewTueNum.setBackgroundColor(topBarHighlightingColor);
                break;
            case Calendar.WEDNESDAY:
                textViewWed.setBackgroundColor(topBarHighlightingColor);
                textViewWedNum.setBackgroundColor(topBarHighlightingColor);
                break;
            case Calendar.THURSDAY:
                textViewThu.setBackgroundColor(topBarHighlightingColor);
                textViewThuNum.setBackgroundColor(topBarHighlightingColor);
                break;
            case Calendar.FRIDAY:
                textViewFri.setBackgroundColor(topBarHighlightingColor);
                textViewFriNum.setBackgroundColor(topBarHighlightingColor);
                break;
            case Calendar.SATURDAY:
                textViewSat.setBackgroundColor(topBarHighlightingColor);
                textViewSatNum.setBackgroundColor(topBarHighlightingColor);
                break;
            case Calendar.SUNDAY:
                textViewSun.setBackgroundColor(topBarHighlightingColor);
                textViewSunNum.setBackgroundColor(topBarHighlightingColor);
                break;
        }
    }

    private void resetWeekDayHighlights() {
        textViewMon.setBackgroundColor(Color.TRANSPARENT);
        textViewTue.setBackgroundColor(Color.TRANSPARENT);
        textViewWed.setBackgroundColor(Color.TRANSPARENT);
        textViewThu.setBackgroundColor(Color.TRANSPARENT);
        textViewFri.setBackgroundColor(Color.TRANSPARENT);
        textViewSat.setBackgroundColor(Color.TRANSPARENT);
        textViewSun.setBackgroundColor(Color.TRANSPARENT);

        textViewMonNum.setBackgroundColor(Color.TRANSPARENT);
        textViewTueNum.setBackgroundColor(Color.TRANSPARENT);
        textViewWedNum.setBackgroundColor(Color.TRANSPARENT);
        textViewThuNum.setBackgroundColor(Color.TRANSPARENT);
        textViewFriNum.setBackgroundColor(Color.TRANSPARENT);
        textViewSatNum.setBackgroundColor(Color.TRANSPARENT);
        textViewSunNum.setBackgroundColor(Color.TRANSPARENT);
    }

    private void updateDateNumbers() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
        if (layoutManager != null) {
            int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
            Object item = taskAdapter.getItemAtPosition(firstVisiblePosition);
            if (item instanceof Long) {
                long dateInMillis = (Long) item;
                Calendar calendar = CalendarUtils.getGermanCalendar();
                calendar.setTimeInMillis(dateInMillis);

                textViewMonNum.setText(getDayOfMonth(calendar, Calendar.MONDAY));
                textViewTueNum.setText(getDayOfMonth(calendar, Calendar.TUESDAY));
                textViewWedNum.setText(getDayOfMonth(calendar, Calendar.WEDNESDAY));
                textViewThuNum.setText(getDayOfMonth(calendar, Calendar.THURSDAY));
                textViewFriNum.setText(getDayOfMonth(calendar, Calendar.FRIDAY));
                textViewSatNum.setText(getDayOfMonth(calendar, Calendar.SATURDAY));
                textViewSunNum.setText(getDayOfMonth(calendar, Calendar.SUNDAY));
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            MainActivity.this.startActivity(intent);
            return true;
        } else if (id == R.id.action_checklist) {
            Intent intent = new Intent(MainActivity.this, CompletedTaskActivity.class);
            MainActivity.this.startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateClick(long dateInMillis) {
        scrollToDate(dateInMillis);
    }

    private void scrollToDate(long dateInMillis) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
        if (layoutManager != null) {
            for (int i = 0; i < taskAdapter.getItemCount(); i++) {
                Object item = taskAdapter.getItemAtPosition(i);
                if (item instanceof Long && (Long) item == dateInMillis) {
                    layoutManager.scrollToPositionWithOffset(i, 0);
                    break;
                }
            }
        }
    }
}
