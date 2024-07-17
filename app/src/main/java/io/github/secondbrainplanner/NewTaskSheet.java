package io.github.secondbrainplanner;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.github.secondbrainplanner.databinding.FragmentNewTaskSheetBinding;

public class NewTaskSheet extends BottomSheetDialogFragment {
    private FragmentNewTaskSheetBinding binding;
    private TaskViewModel taskViewModel;

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

    private void saveAction() {
        String title = binding.newTaskName.getText().toString();
        String description = binding.newTaskDescription.getText().toString();
        String due_date_str = binding.newTaskDate.getText().toString();
        if (!title.isEmpty() && !description.isEmpty() && !due_date_str.isEmpty()) {
            long created_at = System.currentTimeMillis();
            long due_date = parseDate(due_date_str);
            int completed = 0;
            long completed_at = 0;
            long updated_at = created_at;
            Task task = new Task(title, description, created_at, due_date, completed, completed_at, updated_at);
            taskViewModel.addTask(task);
            binding.newTaskName.setText("");
            binding.newTaskDescription.setText("");
            binding.newTaskDate.setText("");
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
}

