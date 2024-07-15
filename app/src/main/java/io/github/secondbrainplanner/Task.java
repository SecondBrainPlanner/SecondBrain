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

    public Task(int id, String title, String description, int created_at, int due_date, int completed, int completed_at, int updated_at) {
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

}