package com.example.librarymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class StaffDashboardActivity extends AppCompatActivity {
    Button btnManageMembers, btnManageBooks, btnViewRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);

        btnManageMembers = findViewById(R.id.btnManageMembers);
        btnManageBooks = findViewById(R.id.btnManageBooks);
        btnViewRequests = findViewById(R.id.btnViewRequests);

       btnManageMembers.setOnClickListener(v ->
                startActivity(new Intent(this, ManageMembersActivity.class))
        );
        btnManageBooks.setOnClickListener(v ->
                startActivity(new Intent(this, ManageBooksActivity.class))
        );
        btnViewRequests.setOnClickListener(v ->
                startActivity(new Intent(this, BookRequestsActivity.class))
        );
    }
}
