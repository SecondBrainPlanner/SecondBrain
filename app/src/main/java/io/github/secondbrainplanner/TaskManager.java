package io.github.secondbrainplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    private DatabaseManager dbManager;

    public TaskManager(Context context) {
        dbManager = new DatabaseManager(context);
    }

    public long insertTask(String title, String description, long created_at, long due_date, int completed, long completed_at, long updated_at) {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("description", description);
        contentValues.put("created_at", created_at);
        contentValues.put("due_date", due_date);
        contentValues.put("completed", completed);
        contentValues.put("completed_at", completed_at);
        contentValues.put("updated_at", updated_at);
        long id = db.insert("tasks", null, contentValues);
        db.close();
        return id;
    }

    public void updateTask(int id, String title, String description, long created_at, long due_date, int completed, long completed_at, long updated_at) {
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

    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = dbManager.getReadableDatabase();
        Cursor cursor = db.query("tasks", null, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                long created_at = cursor.getLong(cursor.getColumnIndexOrThrow("created_at"));
                long due_date = cursor.getLong(cursor.getColumnIndexOrThrow("due_date"));
                int completed = cursor.getInt(cursor.getColumnIndexOrThrow("completed"));
                long completed_at = cursor.getLong(cursor.getColumnIndexOrThrow("completed_at"));
                long updated_at = cursor.getLong(cursor.getColumnIndexOrThrow("updated_at"));

                Task task = new Task(title, description, created_at, due_date, completed, completed_at, updated_at);
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return taskList;
    }


}
