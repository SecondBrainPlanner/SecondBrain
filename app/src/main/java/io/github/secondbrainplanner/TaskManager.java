package io.github.secondbrainplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class TaskManager {

    private DatabaseManager dbManager;

    public TaskManager(Context context) {
        dbManager = new DatabaseManager(context);
    }

    public void insertTask(String title, String description, int created_at, int due_date, int completed, int completed_at, int updated_at) {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("description", description);
        contentValues.put("created_at", created_at);
        contentValues.put("due_date", due_date);
        contentValues.put("completed", completed);
        contentValues.put("completed_at", completed_at);
        contentValues.put("updated_at", updated_at);
        db.insert("tasks", null, contentValues);
        db.close();
    }

    public void updateTask(int id, String title, String description, int created_at, int due_date, int completed, int completed_at, int updated_at) {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("description", description);
        contentValues.put("created_at", created_at);
        contentValues.put("due_date", due_date);
        contentValues.put("completed", completed);
        contentValues.put("completed_at", completed_at);
        contentValues.put("updated_at", updated_at);
        String select = "id = ?";
        String[] selectArgs = { String.valueOf(id) };
        db.update("tasks", contentValues, select, selectArgs);
        db.close();
    }

    public void deleteTask(int id) {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        String select = "id = ?";
        String[] selectArgs = { String.valueOf(id) };
        db.delete("tasks", select, selectArgs);
        db.close();
    }

}
