package io.github.secondbrainplanner;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> settingsList;
    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.textViewTitle), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listView = findViewById(R.id.listViewSettings);
        settingsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, settingsList);
        listView.setAdapter(adapter);
        dbManager = new DatabaseManager(this);

        settingsList.add(getString(R.string.export_database));
        settingsList.add(getString(R.string.import_database));
        settingsList.add(getString(R.string.reset_app));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    startFilePickerForExport();
                } else if (position == 1) {
                    startFilePickerForImport();
                } else if (position == 2) {
                    showResetConfirmationDialog();
                }
            }
        });
    }

    private void startFilePickerForExport() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, "secondbrain_export.csv");

        createFileLauncher.launch(intent);
    }

    private void startFilePickerForImport() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        importFileLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> createFileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        exportDatabase(uri);
                    }
                }
            });

    private final ActivityResultLauncher<Intent> importFileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        importDatabase(uri);
                    }
                }
            });

    private void exportDatabase(Uri uri) {
        try {
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "w");
            if (pfd != null) {
                FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
                dbManager.exportDatabaseToCSV(fileOutputStream);
                Toast.makeText(this, getString(R.string.database_exported_to) + uri.getPath(), Toast.LENGTH_LONG).show();
                pfd.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.error_while_exporting_the_database, Toast.LENGTH_LONG).show();
            Log.e("SettingsActivity", "Error exporting database: " + e.getMessage());
        }
    }

    private void importDatabase(Uri uri) {
        try {
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "r");
            if (pfd != null) {
                dbManager.importDatabaseFromCSV(new FileInputStream(pfd.getFileDescriptor()));
                Toast.makeText(this, getString(R.string.database_imported_from) + uri.getPath(), Toast.LENGTH_LONG).show();
                pfd.close();
                restartApp();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.error_while_importing_the_database, Toast.LENGTH_LONG).show();
            Log.e("SettingsActivity", "Error importing database: " + e.getMessage());
        }
    }

    private void showResetConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.reset_app);
        builder.setMessage(R.string.do_you_really_want_to_reset_the_app);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetDatabase();
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void resetDatabase() {
        getApplicationContext().deleteDatabase("secondbrain.db");
        restartApp();
    }

    private void restartApp() {
        Intent mStartActivity = new Intent(getApplicationContext(), MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }



}