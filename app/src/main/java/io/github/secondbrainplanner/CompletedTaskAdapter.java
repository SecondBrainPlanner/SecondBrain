package io.github.secondbrainplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CompletedTaskAdapter extends RecyclerView.Adapter<CompletedTaskAdapter.ViewHolder> {

    private List<Task> completedTasks = new ArrayList<>();
    private TaskViewModel taskViewModel;
    private FragmentManager fragmentManager;
    private RecyclerView recyclerView;
    private OnUncompleteTaskListener onUncompleteTaskListener;
    private Context context;

    public CompletedTaskAdapter(Context context, TaskViewModel taskViewModel, FragmentManager fragmentManager, RecyclerView recyclerView, OnUncompleteTaskListener onUncompleteTaskListener) {
        this.taskViewModel = taskViewModel;
        this.fragmentManager = fragmentManager;
        this.recyclerView = recyclerView;
        this.onUncompleteTaskListener = onUncompleteTaskListener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_completedtask, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = completedTasks.get(position);
        holder.nameTextView.setText(task.getTitle());
        holder.descriptionTextView.setText(task.getDescription());
        holder.completedDateTextView.setText(context.getString(R.string.completed_at) + convertToDate(task.getCompleted_at()));
        holder.completedCheckBoxView.setChecked(task.getCompleted() != 0);

        holder.completedCheckBoxView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                onUncompleteTaskListener.onUncompleteTask(task);
            }
        });

    }

    @Override
    public int getItemCount() {
        return completedTasks.size();
    }

    public void setItems(List<Task> tasks) {
        this.completedTasks = tasks;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView descriptionTextView;
        TextView completedDateTextView;
        CheckBox completedCheckBoxView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewTaskName);
            descriptionTextView = itemView.findViewById(R.id.textViewTaskDescription);
            completedDateTextView = itemView.findViewById(R.id.completedDateTextView);
            completedCheckBoxView = itemView.findViewById(R.id.checkBoxCompleted);
        }
    }

    public interface OnUncompleteTaskListener {
        void onUncompleteTask(Task task);
    }

    private String convertToDate(long date) {
        Instant instant = Instant.ofEpochMilli(date);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy | HH:mm");
        return dateTime.format(formatter);
    }

}
