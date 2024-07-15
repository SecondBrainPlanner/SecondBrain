package io.github.secondbrainplanner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DATABASE = "secondbrain.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_COMMAND_DROP_TASKS = "DROP TABLE IF EXISTS tasks";

    private static final String SQL_COMMAND_CREATE_TASKS =
                    "CREATE TABLE \"tasks\" (" +
                    "\"id\" INTEGER NOT NULL UNIQUE, " +
                    "\"title\" TEXT NOT NULL, " +
                    "\"description\" TEXT, " +
                    "\"created_at\" INTEGER NOT NULL, " +
                    "\"due_date\" INTEGER NOT NULL, " +
                    "\"completed\" INTEGER NOT NULL, " +
                    "\"completed_at\" INTEGER NOT NULL, " +
                    "\"updated_at\" INTEGER NOT NULL, " +
                    "PRIMARY KEY(\"id\" AUTOINCREMENT)" +
                    ");";

    public DatabaseManager(Context context) {
        super(context, DATABASE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_COMMAND_CREATE_TASKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_COMMAND_DROP_TASKS);
        onCreate(db);
    }

}
