package io.github.secondbrainplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private SimpleDateFormat headerFormat = new SimpleDateFormat("dd MMMM • EEEE", Locale.getDefault());

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.nameTextView.setText(task.getTitle());
        holder.descriptionTextView.setText(task.getDescription());
        
        if (position == 0 || !isSameDay(taskList.get(position - 1).getDate(), task.getDate())) {
            holder.dateTextView.setVisibility(View.VISIBLE);
            holder.dateTextView.setText(headerFormat.format(task.getDate()));
        } else {
            holder.dateTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void setTasks(List<Task> tasks) {
        this.taskList = tasks;
        notifyDataSetChanged();
    }

    private boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return fmt.format(date1).equals(fmt.format(date2));
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView nameTextView;
        TextView descriptionTextView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            nameTextView = itemView.findViewById(R.id.textViewTaskName);
            descriptionTextView = itemView.findViewById(R.id.textViewTaskDescription);
        }
    }
}
