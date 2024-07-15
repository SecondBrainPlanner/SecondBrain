package io.github.secondbrainplanner;

public class Task {
    private int id;
    private String title;
    private String description;
    private int created_at;
    private int due_date;
    private int completed;
    private int completed_at;
    private int updated_at;

    public Task(String title, String description, int created_at, int due_date, int completed, int completed_at, int updated_at) {
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

    public int getCreated_at() { return created_at; }

    public int getDue_date() { return due_date; }

    public int getCompleted() { return completed; }

    public int getCompleted_at() { return completed_at; }

    public int getUpdated_at() { return updated_at; }

    public void setId(int id) { this.id = id; }

    public void setTitle(String title) { this.title = title; }

    public void setDescription(String description) { this.description = description; }

    public void setCreated_at(int created_at) { this.created_at = created_at; }

    public void setDue_date(int due_date) { this.due_date = due_date; }

    public void setCompleted(int completed) { this.completed = completed; }

    public void setCompleted_at(int completed_at) { this.completed_at = completed_at; }

    public void setUpdated_at(int updated_at) { this.updated_at = updated_at; }

}