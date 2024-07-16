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

    public void deleteTask(Task task) {
        taskManager.deleteTask(task.getId());
        loadItems();
    }
    
    private List<Object> generateDateList(List<Task> taskList) {
        List<Object> itemList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        int numberOfDaysToShow = 30;

        for (int i = 0; i < numberOfDaysToShow; i++) {
            long currentDate = calendar.getTimeInMillis();
            itemList.add(currentDate);

            for (Task task : taskList) {
                if (isSameDay(task.getDue_date(), currentDate)) {
                    itemList.add(task);
                }
            }

            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return itemList;
    }

    private boolean isSameDay(long timestamp1, long timestamp2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(timestamp1);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(timestamp2);

        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR);
    }
}
