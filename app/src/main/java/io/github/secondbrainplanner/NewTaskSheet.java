package io.github.secondbrainplanner;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.github.secondbrainplanner.databinding.FragmentNewTaskSheetBinding;

public class NewTaskSheet extends BottomSheetDialogFragment {
    private FragmentNewTaskSheetBinding binding;
    private TaskViewModel taskViewModel;
    private String currentDate;

    public NewTaskSheet(String currentDate) {
        this.currentDate = currentDate;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity activity = (MainActivity) requireActivity();
        taskViewModel = new ViewModelProvider(activity).get(TaskViewModel.class);
        binding.newTaskAddButton.setOnClickListener(v -> saveAction());
        binding.newTaskDate.setOnClickListener(v -> showDatePicker());
        binding.newTaskDate.setFocusable(false);
        binding.newTaskDate.setClickable(true);
        binding.newTaskReminder.setOnClickListener(v -> showTimePicker());
        binding.newTaskReminder.setFocusable(false);
        binding.newTaskReminder.setClickable(true);

        if (currentDate != null) {
            binding.newTaskDate.setText(currentDate);
        } else {
            binding.newTaskDate.setText(convertToDate(System.currentTimeMillis()));
        }

        binding.newTaskName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    ((BottomSheetDialog) getDialog()).getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
        binding.newTaskDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    ((BottomSheetDialog) getDialog()).getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
        binding.newTaskDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    ((BottomSheetDialog) getDialog()).getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
        binding.newTaskReminder.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    ((BottomSheetDialog) getDialog()).getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
        binding.resetReminderButton.setOnClickListener(v -> {
            if (binding.newTaskReminder.getText().toString().isEmpty()) {
                Toast.makeText(v.getContext(), R.string.no_reminder_set, Toast.LENGTH_SHORT).show();
            } else {
                binding.newTaskReminder.setText("");
                Toast.makeText(v.getContext(), R.string.reminder_deleted, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewTaskSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                String selectedDate = selectedDay + "." + (selectedMonth + 1) + "." + selectedYear;
                binding.newTaskDate.setText(selectedDate);
            }
        },
                year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                        String selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                        binding.newTaskReminder.setText(selectedTime);
                    }
                },
                hour, minute, true
        );

        timePickerDialog.show();
    }

    private void saveAction() {
        String title = binding.newTaskName.getText().toString();
        String description = binding.newTaskDescription.getText().toString();
        String due_date_str = binding.newTaskDate.getText().toString();
        String reminder_str = binding.newTaskReminder.getText().toString();
        if (!title.isEmpty() && !due_date_str.isEmpty()) {
            long created_at = System.currentTimeMillis();
            long due_date = parseDate(due_date_str);
            if (!reminder_str.isEmpty()) {
                due_date = parseDate(due_date_str) + parseTimeToMillis(reminder_str);
            }
            int completed = 0;
            long completed_at = 0;
            long updated_at = created_at;
            Task task = new Task(title, description, created_at, due_date, completed, completed_at, updated_at);
            taskViewModel.addTask(task, !reminder_str.isEmpty());
            binding.newTaskName.setText("");
            binding.newTaskDescription.setText("");
            binding.newTaskDate.setText("");
            binding.newTaskReminder.setText("");
            dismiss();
        } else {
            Toast.makeText(getContext(), getString(R.string.name_or_date_is_empty), Toast.LENGTH_SHORT).show();
        }
    }

    private long parseDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        try {
            Date date = sdf.parse(dateStr);
            return date != null ? date.getTime() : System.currentTimeMillis();
        } catch (ParseException e) {
            e.printStackTrace();
            return System.currentTimeMillis();
        }
    }

    private long parseTimeToMillis(String timeStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = sdf.parse(timeStr);

            long millis = date.getTime();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            long millisFromStartOfDay = calendar.get(Calendar.HOUR_OF_DAY) * 3600000L + calendar.get(Calendar.MINUTE) * 60000L;
            if (millisFromStartOfDay == 0) {
                return 100;
            } else {
                return millisFromStartOfDay;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private String convertToDate(long date) {
        Instant instant = Instant.ofEpochMilli(date);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return dateTime.format(formatter);
    }

}

