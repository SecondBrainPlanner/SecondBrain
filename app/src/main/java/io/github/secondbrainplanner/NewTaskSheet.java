package io.github.secondbrainplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import io.github.secondbrainplanner.databinding.FragmentNewTaskSheetBinding;

public class NewTaskSheet extends BottomSheetDialogFragment {
    private FragmentNewTaskSheetBinding binding;
    private TaskViewModel taskViewModel;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity activity = (MainActivity) requireActivity();
        taskViewModel = new ViewModelProvider(activity).get(TaskViewModel.class);
        binding.newTaskAddButton.setOnClickListener(v -> saveAction());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewTaskSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void saveAction() {
        String title = binding.newTaskName.getText().toString();
        String description = binding.newTaskDescription.getText().toString();
        if (!title.isEmpty() && !description.isEmpty()) {
            long created_at = System.currentTimeMillis();
            long due_date = System.currentTimeMillis(); //Muss noch geändert werden
            int completed = 0;
            long completed_at = 0;
            long updated_at = created_at;
            Task task = new Task(title, description, created_at, due_date, completed, completed_at, updated_at);
            taskViewModel.addTask(task);
            binding.newTaskName.setText("");
            binding.newTaskDescription.setText("");
            dismiss();
        }
    }
}

