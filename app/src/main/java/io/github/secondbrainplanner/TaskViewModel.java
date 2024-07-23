package io.github.secondbrainplanner;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
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
    private final Application application;
    private int numberOfDaysToShow = 365;

    public TaskViewModel(@NonNull Application application) {
        this.application = application;
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
        loadTasksFromDatabase();
    }

    private void loadTasksFromDatabase() {
        List<Task> taskList = taskManager.getAllTasks();
        updateDateRangeIfNeeded(taskList);
        _items.setValue(generateDateList(taskList));
    }

    public void addTask(Task task, boolean reminder) {
        long id = taskManager.insertTask(
                task.getTitle(), task.getDescription(),
                task.getCreated_at(), task.getDue_date(),
                task.getCompleted(), task.getCompleted_at(),
                task.getUpdated_at()
        );
        task.setId((int) id);

        List<Task> currentTaskList = extractTasks(_items.getValue());
        currentTaskList.add(task);

        updateDateRangeIfNeeded(currentTaskList);
        _items.setValue(generateDateList(currentTaskList));

        if (reminder) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(task.getDue_date());
            AlarmHelper.setAlarm(application.getApplicationContext(), calendar, task.getTitle(), task.getDescription(), task.getId());
        }
    }

    public void deleteTask(Task task) {
        taskManager.deleteTask(task.getId());
  
        List<Task> currentTaskList = extractTasks(_items.getValue());
        currentTaskList.remove(task);

        updateDateRangeIfNeeded(currentTaskList);
        _items.setValue(generateDateList(currentTaskList));

        AlarmHelper.cancelAlarm(application.getApplicationContext(), task.getId());
    }

    public void editTask(Task task, Task oldtask, boolean reminder) {
        taskManager.updateTask(
                task.getId(), task.getTitle(), task.getDescription(),
                task.getCreated_at(), task.getDue_date(),
                task.getCompleted(), task.getCompleted_at(),
                task.getUpdated_at()
        );

        List<Task> currentTaskList = extractTasks(_items.getValue());
        currentTaskList.add(task);
        currentTaskList.remove(oldtask);

        updateDateRangeIfNeeded(currentTaskList);
        _items.setValue(generateDateList(currentTaskList));

        AlarmHelper.cancelAlarm(application.getApplicationContext(), oldtask.getId());
        if (reminder) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(task.getDue_date());
            AlarmHelper.setAlarm(application.getApplicationContext(), calendar, task.getTitle(), task.getDescription(), task.getId());
        }
    }

    public void completeTask(Task task, Task oldtask) {
        taskManager.updateTask(
                task.getId(), task.getTitle(), task.getDescription(),
                task.getCreated_at(), task.getDue_date(),
                task.getCompleted(), task.getCompleted_at(),
                task.getUpdated_at()
        );

        List<Task> currentTaskList = extractTasks(_items.getValue());
        currentTaskList.remove(oldtask);

        updateDateRangeIfNeeded(currentTaskList);
        _items.setValue(generateDateList(currentTaskList));

        AlarmHelper.cancelAlarm(application.getApplicationContext(), oldtask.getId());
    }

    public void uncompleteTask(Task task) {
        taskManager.updateTask(
                task.getId(), task.getTitle(), task.getDescription(),
                task.getCreated_at(), task.getDue_date(),
                task.getCompleted(), task.getCompleted_at(),
                task.getUpdated_at()
        );

        List<Task> currentTaskList = extractTasks(_items.getValue());
        currentTaskList.add(task);

        updateDateRangeIfNeeded(currentTaskList);
        _items.setValue(generateDateList(currentTaskList));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(task.getDue_date());
        AlarmHelper.setAlarm(application.getApplicationContext(), calendar, task.getTitle(), task.getDescription(), task.getId());
    }
    private void updateDateRangeIfNeeded(List<Task> taskList) {
        long minDate = System.currentTimeMillis();
        long maxDate = minDate + 365L * 24 * 60 * 60 * 1000; // 365 tage beim start

        for (Task task : taskList) {
            if (task.getDue_date() < minDate) {
                minDate = task.getDue_date();
            }
            if (task.getDue_date() > maxDate) {
                maxDate = task.getDue_date();
            }
        }

        numberOfDaysToShow = (int) (((maxDate - minDate) / (24 * 60 * 60 * 1000)) + 2) + 40; // extra Timestamps nach aufgabe
    }

    private List<Object> generateDateList(List<Task> taskList) {
        List<Object> itemList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(2024,1,1); // max oldest task date is 1.1.2024

        Calendar todayCalendar = Calendar.getInstance();    // set calender to start 0:00
        todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
        todayCalendar.set(Calendar.MINUTE, 0);
        todayCalendar.set(Calendar.SECOND, 0);
        todayCalendar.set(Calendar.MILLISECOND, 0);
        long todayMillis = todayCalendar.getTimeInMillis();

        for (int i = 0; i < numberOfDaysToShow; i++) {
            long currentDate = calendar.getTimeInMillis();

            if (currentDate >= todayMillis) {       // no timestamp for past days, only if time >= 0:00 today
                itemList.add(currentDate);
            }

            for (Task task : taskList) {
                if (isSameDay(task.getDue_date(), currentDate)) {
                    itemList.add(task);
                }
            }

            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return itemList;
    }

    public boolean isSameDay(long timestamp1, long timestamp2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(timestamp1);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(timestamp2);

        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR);
    }

    private List<Task> extractTasks(List<Object> items) {
        List<Task> taskList = new ArrayList<>();
        for (Object item : items) {
            if (item instanceof Task) {
                taskList.add((Task) item);
            }
        }
        return taskList;
    }

    public LiveData<List<Task>> getCompletedTasks() {
        return new MutableLiveData<>(taskManager.getAllCompletedTasks());
    }

}
