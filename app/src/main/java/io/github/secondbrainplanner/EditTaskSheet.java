package io.github.secondbrainplanner;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.lifecycle.ViewModelProvider;

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

import io.github.secondbrainplanner.databinding.FragmentEditTaskSheetBinding;

public class EditTaskSheet extends BottomSheetDialogFragment {
    private FragmentEditTaskSheetBinding binding;
    private TaskViewModel taskViewModel;
    private Task edittask;

    public EditTaskSheet(Task edittask) {
        this.edittask = edittask;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity activity = (MainActivity) requireActivity();
        taskViewModel = new ViewModelProvider(activity).get(TaskViewModel.class);
        binding.editTaskAddButton.setOnClickListener(v -> saveAction());
        binding.editTaskDate.setOnClickListener(v -> showDatePicker(edittask.getDue_date()));
        binding.editTaskDate.setFocusable(false);
        binding.editTaskDate.setClickable(true);
        binding.editTaskName.setText(edittask.getTitle());
        binding.editTaskDescription.setText(edittask.getDescription());
        binding.editTaskDate.setText(convertToDate(edittask.getDue_date()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditTaskSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void showDatePicker(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                String selectedDate = selectedDay + "." + (selectedMonth + 1) + "." + selectedYear;
                binding.editTaskDate.setText(selectedDate);
            }
        },
                year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void saveAction() {
        String title = binding.editTaskName.getText().toString();
        String description = binding.editTaskDescription.getText().toString();
        String due_date_str = binding.editTaskDate.getText().toString();
        if (!title.isEmpty() && !description.isEmpty() && !due_date_str.isEmpty()) {
            long created_at = edittask.getCreated_at();
            long due_date = parseDate(due_date_str);
            int completed = 0;
            long completed_at = 0;
            long updated_at = System.currentTimeMillis();
            Task task = new Task(title, description, created_at, due_date, completed, completed_at, updated_at);
            task.setId(edittask.getId());
            taskViewModel.editTask(task, edittask);
            binding.editTaskName.setText("");
            binding.editTaskDescription.setText("");
            binding.editTaskDate.setText("");
            dismiss();
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

    private String convertToDate(long date) {
        Instant instant = Instant.ofEpochMilli(date);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return dateTime.format(formatter);
    }
}

