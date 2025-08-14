package com.example.librarymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationSettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "notification_prefs";
    private static final String KEY_BOOK_REQUEST_UPDATES = "book_request_updates";
    private static final String KEY_LIBRARY_ANNOUNCEMENTS = "library_announcements";

    private CheckBox cbBookRequests;
    private CheckBox cbLibraryAnnouncements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        cbBookRequests = findViewById(R.id.cbBookRequests);
        cbLibraryAnnouncements = findViewById(R.id.cbLibraryAnnouncements);
        Button btnSavePrefs = findViewById(R.id.btnSavePrefs);

        loadPreferences();

        btnSavePrefs.setOnClickListener(v -> {
            savePreferences();
            Toast.makeText(this, "Preferences saved", Toast.LENGTH_SHORT).show();
            finish(); // Optional: close screen after saving
        });
    }

    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        cbBookRequests.setChecked(prefs.getBoolean(KEY_BOOK_REQUEST_UPDATES, true));
        cbLibraryAnnouncements.setChecked(prefs.getBoolean(KEY_LIBRARY_ANNOUNCEMENTS, true));
    }

    private void savePreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_BOOK_REQUEST_UPDATES, cbBookRequests.isChecked());
        editor.putBoolean(KEY_LIBRARY_ANNOUNCEMENTS, cbLibraryAnnouncements.isChecked());
        editor.apply();
    }
}
