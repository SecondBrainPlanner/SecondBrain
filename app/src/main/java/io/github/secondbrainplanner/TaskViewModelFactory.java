package io.github.secondbrainplanner;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class TaskViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;

    public TaskViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TaskViewModel.class)) {
            TaskViewModel viewModel = new TaskViewModel();
            TaskManager taskManager = new TaskManager(application.getApplicationContext());
            viewModel.setTaskManager(taskManager);
            return (T) viewModel;
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
