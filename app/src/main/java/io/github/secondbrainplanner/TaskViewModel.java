package io.github.secondbrainplanner;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaskViewModel extends ViewModel {
    private final MutableLiveData<List<Object>> _items = new MutableLiveData<>(new ArrayList<>());
    public final LiveData<List<Object>> items = _items;
    private TaskManager taskManager;

    public TaskViewModel() {}

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
        loadItems();
    }

    private void loadItems() {
        List<Task> taskList = taskManager.getAllTasks();
        List<Object> itemList = generateDateList(taskList);
        _items.setValue(itemList);
    }

    public void addTask(Task task) {
        long id = taskManager.insertTask(
                task.getTitle(), task.getDescription(),
                task.getCreated_at(), task.getDue_date(),
                task.getCompleted(), task.getCompleted_at(),
                task.getUpdated_at()
        );
        task.setId((int) id);
        loadItems();
    }
}