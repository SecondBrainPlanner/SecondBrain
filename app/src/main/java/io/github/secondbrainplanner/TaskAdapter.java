package io.github.secondbrainplanner;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    public TaskAdapter(TaskViewModel taskViewModel, FragmentManager fragmentManager, RecyclerView recyclerView, onDateClickListener onDateClickListener) {
        this.itemList = new ArrayList<>();
        this.taskViewModel = taskViewModel;
        this.fragmentManager = fragmentManager;
        this.recyclerView = recyclerView;
        this.onDateClickListener = onDateClickListener;
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
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(v.getContext(), "Task wird in 2 Sekunden gelöscht: " + task.getTitle(), Toast.LENGTH_SHORT).show();

                    new CountDownTimer(2000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long secondsRemaining = millisUntilFinished / 1000;
                            Toast.makeText(v.getContext(), "Noch " + secondsRemaining + " Sekunden...", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFinish() {
                            if (taskViewModel != null && task != null) {
                                taskViewModel.deleteTask(task);
                            } else {
                                //Logge den Fehler im Android-Log, ansonsten stürzt die App ab.
                                Log.e("TaskAdapter", "taskViewModel or task is null");
                            }
                        }
                    }.start();

                    return true;
                }
            });
            holder.itemView.setOnClickListener(v -> {
                new EditTaskSheet(task).show(fragmentManager, "editTaskTag");
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
        CheckBox completedCheckBoxView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            nameTextView = itemView.findViewById(R.id.textViewTaskName);
            descriptionTextView = itemView.findViewById(R.id.textViewTaskDescription);
            completedCheckBoxView = itemView.findViewById(R.id.checkBoxCompleted);
        }
    }

    public interface onDateClickListener{
        void onDateClick(long dateInMillis);
    }
}
