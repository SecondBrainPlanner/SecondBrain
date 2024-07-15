package io.github.secondbrainplanner;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class TaskViewModel extends ViewModel {
    private final MutableLiveData<List<Task>> _tasks = new MutableLiveData<>(new ArrayList<>());
    public final LiveData<List<Task>> tasks = _tasks;
    private TaskManager taskManager;

    public TaskViewModel() {}

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
        loadTasksFromDatabase();
    }

    private void loadTasksFromDatabase() {
        List<Task> taskList = taskManager.getAllTasks();
        _tasks.setValue(taskList);
    }

    public void addTask(Task task) {
        long id = taskManager.insertTask(
                task.getTitle(), task.getDescription(),
                task.getCreated_at(), task.getDue_date(),
                task.getCompleted(), task.getCompleted_at(),
                task.getUpdated_at()
        );
        task.setId((int) id);
        List<Task> currentList = new ArrayList<>(_tasks.getValue());
        currentList.add(task);
        _tasks.setValue(currentList);
    }
}

