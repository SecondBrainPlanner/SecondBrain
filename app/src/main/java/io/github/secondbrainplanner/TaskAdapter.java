package io.github.secondbrainplanner;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private List<Object> itemList = new ArrayList<>();
    private SimpleDateFormat headerFormat = new SimpleDateFormat("dd MMMM • EEEE", Locale.getDefault());

    private TaskViewModel taskViewModel;
    private FragmentManager fragmentManager;
    private RecyclerView recyclerView;
    private onDateClickListener onDateClickListener;
    private Context context;
    private Handler handler;
    private Runnable updateRunnable;

    public TaskAdapter(Context context, TaskViewModel taskViewModel, FragmentManager fragmentManager, RecyclerView recyclerView, onDateClickListener onDateClickListener) {
        this.itemList = new ArrayList<>();
        this.taskViewModel = taskViewModel;
        this.fragmentManager = fragmentManager;
        this.recyclerView = recyclerView;
        this.onDateClickListener = onDateClickListener;
        this.context = context;

        handler = new Handler(Looper.getMainLooper());
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
                handler.postDelayed(this, 60000);
            }
        };
        handler.post(updateRunnable);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (isDate(itemList.get(position))) {
            long dateInMillis = (Long) itemList.get(position);
            Date date = new Date(dateInMillis);
            holder.dateTextView.setText(headerFormat.format(date));
            holder.dateTextView.setVisibility(View.VISIBLE);
            holder.nameTextView.setVisibility(View.GONE);
            holder.descriptionTextView.setVisibility(View.GONE);
            holder.completedCheckBoxView.setVisibility(View.GONE);
            holder.timeTextView.setVisibility(View.GONE);

            boolean hasTasks = false;
            for (int i = position + 1; i < itemList.size(); i++) {
                if (!isDate(itemList.get(i)) && taskViewModel.isSameDay(dateInMillis, ((Task) itemList.get(i)).getDue_date())) {    //später .get updated at nutzen?
                    hasTasks = true;
                    break;
                }
                if (isDate(itemList.get(i))) {
                    break;
                }
            }

            if (hasTasks) {
                holder.dateTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
            } else {
                holder.dateTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.grey));
            }

            holder.dateTextView.setOnClickListener(v -> onDateClickListener.onDateClick(dateInMillis));

        } else {
            Task task = (Task) itemList.get(position);
            holder.nameTextView.setText(task.getTitle());
            holder.descriptionTextView.setText(task.getDescription());
            holder.dateTextView.setVisibility(View.GONE);
            holder.nameTextView.setVisibility(View.VISIBLE);
            holder.descriptionTextView.setVisibility(View.VISIBLE);
            holder.completedCheckBoxView.setVisibility(View.VISIBLE);

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            holder.timeTextView.setText(timeFormat.format(new Date(task.getDue_date())));

            Calendar todayCalendar = Calendar.getInstance();
            todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
            todayCalendar.set(Calendar.MINUTE, 0);
            todayCalendar.set(Calendar.SECOND, 0);
            todayCalendar.set(Calendar.MILLISECOND, 0);
            long todayMillis = todayCalendar.getTimeInMillis();

            if (task.getDue_date() < todayMillis) {
                holder.nameTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
                holder.descriptionTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
            } else {
                holder.nameTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
                holder.descriptionTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
            }

            Calendar taskDueDateCalendar = Calendar.getInstance();
            taskDueDateCalendar.setTimeInMillis(task.getDue_date());

            if (taskDueDateCalendar.get(Calendar.HOUR_OF_DAY) == 0 &&
                    taskDueDateCalendar.get(Calendar.MINUTE) == 0 &&
                    taskDueDateCalendar.get(Calendar.SECOND) == 0 &&
                    taskDueDateCalendar.get(Calendar.MILLISECOND) == 0) {
                holder.timeTextView.setVisibility(View.GONE);
            } else {
                holder.timeTextView.setVisibility(View.VISIBLE);
            }

            if (task.getDue_date() < System.currentTimeMillis()) {
                holder.timeTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
            } else {
                holder.timeTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.teal_200));
            }

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(v.getContext(), context.getString(R.string.press_and_hold_for_2_seconds_to_delete) + task.getTitle(), Toast.LENGTH_SHORT).show();

                    final boolean[] wasPressed = {true};

                    new CountDownTimer(2000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            if (!wasPressed[0]) {
                                cancel();
                            }
                        }

                        @Override
                        public void onFinish() {
                            if (wasPressed[0]) {
                                if (taskViewModel != null && task != null) {
                                    Toast.makeText(v.getContext(), context.getString(R.string.task_deleted) + task.getTitle(), Toast.LENGTH_SHORT).show();
                                    taskViewModel.deleteTask(task);
                                } else {
                                    //Logge den Fehler im Android-Log, ansonsten stürzt die App ab.
                                    Log.e("TaskAdapter", "taskViewModel or task is null");
                                }
                            }
                        }
                    }.start();

                    holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                wasPressed[0] = false;
                            }
                            return false;
                        }
                    });

                    return true;
                }
            });

            holder.itemView.setOnClickListener(v -> {
                new EditTaskSheet(task).show(fragmentManager, "editTaskTag");
            });

            holder.completedCheckBoxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (compoundButton.isChecked()) {
                        String title = task.getTitle();
                        String description = task.getDescription();
                        long created_at = task.getCreated_at();
                        long due_date = task.getDue_date();
                        int completed = 1;
                        long completed_at = System.currentTimeMillis();
                        long updated_at = task.getUpdated_at();
                        Task completedTask = new Task(title, description, created_at, due_date, completed, completed_at, updated_at);
                        completedTask.setId(task.getId());
                        taskViewModel.completeTask(completedTask, task);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private boolean isDate(Object item) {
        return item instanceof Long;
    }

    public void setItems(List<Object> items) {
        this.itemList = items;
        notifyDataSetChanged();
    }

    public Object getItemAtPosition(int position) {
        if (position >= 0 && position < itemList.size()) {
            return itemList.get(position);
        }
        return null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView nameTextView;
        TextView descriptionTextView;
        TextView timeTextView;
        CheckBox completedCheckBoxView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            nameTextView = itemView.findViewById(R.id.textViewTaskName);
            descriptionTextView = itemView.findViewById(R.id.textViewTaskDescription);
            timeTextView = itemView.findViewById(R.id.textViewTime);
            completedCheckBoxView = itemView.findViewById(R.id.checkBoxCompleted);
        }
    }

    public interface onDateClickListener{
        void onDateClick(long dateInMillis);
    }
}
