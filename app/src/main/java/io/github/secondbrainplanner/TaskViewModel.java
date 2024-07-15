package io.github.secondbrainplanner;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class TaskViewModel extends ViewModel {
    private final MutableLiveData<List<Task>> _tasks = new MutableLiveData<>(new ArrayList<>());
    public final LiveData<List<Task>> tasks = _tasks;

    public void addTask(Task task) {
        List<Task> currentList = new ArrayList<>(_tasks.getValue());
        currentList.add(task);
        _tasks.setValue(currentList);
    }
}

