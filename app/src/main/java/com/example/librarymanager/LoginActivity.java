package com.example.librarymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private LibraryDBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = LibraryDBHelper.getInstance(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        Button btnLoginStaff = findViewById(R.id.btnLoginStaff);
        Button btnLoginMember = findViewById(R.id.btnLoginMember);

        btnLoginStaff.setOnClickListener(v -> loginAttempt("staff"));
        btnLoginMember.setOnClickListener(v -> loginAttempt("member"));
    }

    private void loginAttempt(String expectedRole) {
        String user = etUsername.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!db.validateUser(user, pass)) {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        String role = db.getUserRole(user);
        if (!expectedRole.equalsIgnoreCase(role)) {
            Toast.makeText(this, "Access denied for this role", Toast.LENGTH_SHORT).show();
            return;
        }

        // Pass username across the app
        Intent intent = new Intent(this, role.equals("staff") ?
                StaffDashboardActivity.class : MemberDashboardActivity.class);
        intent.putExtra("username", user);
        startActivity(intent);
        finish();
    }
}
