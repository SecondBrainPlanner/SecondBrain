package io.github.secondbrainplanner;

public class Task {
    private int id;
    private String title;
    private String description;
    private long created_at;
    private long due_date;
    private int completed;
    private long completed_at;
    private long updated_at;

    public Task(String title, String description, long created_at, long due_date, int completed, long completed_at, long updated_at) {
        this.title = title;
        this.description = description;
        this.created_at = created_at;
        this.due_date = due_date;
        this.completed = completed;
        this.completed_at = completed_at;
        this.updated_at = updated_at;
    }

    public int getId() { return id; }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getCreated_at() { return created_at; }

    public long getDue_date() { return due_date; }

    public int getCompleted() { return completed; }

    public long getCompleted_at() { return completed_at; }

    public long getUpdated_at() { return updated_at; }

    public void setId(int id) { this.id = id; }

    public void setTitle(String title) { this.title = title; }

    public void setDescription(String description) { this.description = description; }

    public void setCreated_at(long created_at) { this.created_at = created_at; }

    public void setDue_date(long due_date) { this.due_date = due_date; }

    public void setCompleted(int completed) { this.completed = completed; }

    public void setCompleted_at(long completed_at) { this.completed_at = completed_at; }

    public void setUpdated_at(long updated_at) { this.updated_at = updated_at; }

}