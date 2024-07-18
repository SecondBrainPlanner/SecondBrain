package io.github.secondbrainplanner;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

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

    public void exportDatabaseToCSV(OutputStream outputStream) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM tasks";
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            OutputStreamWriter fileWriter = new OutputStreamWriter(outputStream);

            fileWriter.write("id,title,description,created_at,due_date,completed,completed_at,updated_at\n");

            int rowCount = 0;
            while (cursor.moveToNext()) {
                rowCount++;
                fileWriter.write(cursor.getString(0) + ",");
                fileWriter.write(cursor.getString(1) + ",");
                fileWriter.write(cursor.getString(2) + ",");
                fileWriter.write(cursor.getString(3) + ",");
                fileWriter.write(cursor.getString(4) + ",");
                fileWriter.write(cursor.getString(5) + ",");
                fileWriter.write(cursor.getString(6) + ",");
                fileWriter.write(cursor.getString(7) + "\n");
            }

            fileWriter.flush();
            fileWriter.close();
            cursor.close();

            Log.d("DatabaseManager", "Exported " + rowCount + " rows to CSV");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("DatabaseManager", "Error writing to OutputStream: " + e.getMessage());
        }
    }

    public void importDatabaseFromCSV(InputStream inputStream) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                String[] columns = line.split(",");
                if (columns.length != 8) {
                    Log.e("DatabaseManager", "Ungültige Zeile: " + line);
                    continue;
                }
                String sql = "INSERT OR REPLACE INTO tasks (id, title, description, created_at, due_date, completed, completed_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                db.execSQL(sql, columns);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DatabaseManager", "Fehler beim Importieren der CSV-Datei: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

}
