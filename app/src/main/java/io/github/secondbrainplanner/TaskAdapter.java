package io.github.secondbrainplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private List<Object> itemList = new ArrayList<>();
    private SimpleDateFormat headerFormat = new SimpleDateFormat("dd MMMM • EEEE", Locale.getDefault());

    public TaskAdapter() {
        this.itemList = new ArrayList<>();
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
        } else {
            Task task = (Task) itemList.get(position);
            holder.nameTextView.setText(task.getTitle());
            holder.descriptionTextView.setText(task.getDescription());
            holder.dateTextView.setVisibility(View.GONE);
            holder.nameTextView.setVisibility(View.VISIBLE);
            holder.descriptionTextView.setVisibility(View.VISIBLE);
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView nameTextView;
        TextView descriptionTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            nameTextView = itemView.findViewById(R.id.textViewTaskName);
            descriptionTextView = itemView.findViewById(R.id.textViewTaskDescription);
        }
    }
}
