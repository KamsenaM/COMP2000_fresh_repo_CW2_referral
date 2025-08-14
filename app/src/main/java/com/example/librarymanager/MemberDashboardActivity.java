package com.example.librarymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MemberDashboardActivity extends AppCompatActivity {

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_dashboard);

        // Get the username passed from LoginActivity
        username = getIntent().getStringExtra("username");

        Button btnCatalogue = findViewById(R.id.btnCatalogue);
        Button btnProfile = findViewById(R.id.btnProfile);
        Button btnNotificationSettings = findViewById(R.id.btnNotificationSettings);

        // Pass username into CatalogueActivity so it knows who is requesting books
        btnCatalogue.setOnClickListener(v -> {
            Intent intent = new Intent(this, CatalogueActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        // Pass username into ProfileActivity so it can load and edit the member's details
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        // Pass username into NotificationSettingsActivity so prefs can be saved per user
        btnNotificationSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationSettingsActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });
    }
}
