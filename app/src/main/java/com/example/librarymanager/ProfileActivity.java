package com.example.librarymanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private EditText etFirstName, etLastName, etEmail, etContact;
    private String username;
    private static final String BASE_URL = "http://10.224.41.18/comp2000/library/members";
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username = getIntent().getStringExtra("username");
        if (username == null || username.trim().isEmpty()) {
            Toast.makeText(this, "Missing profile data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        queue = Volley.newRequestQueue(this);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName  = findViewById(R.id.etLastName);
        etEmail     = findViewById(R.id.etEmail);
        etContact   = findViewById(R.id.etContact);
        Button btnSave = findViewById(R.id.btnSaveProfile);

        fetchProfileData();
        btnSave.setOnClickListener(v -> updateProfile());
    }

    private void fetchProfileData() {
        String url = BASE_URL + "/" + username;

        JsonObjectRequest getReq = buildRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        etFirstName.setText(response.optString("firstname", ""));
                        etLastName.setText(response.optString("lastname", ""));
                        etEmail.setText(response.optString("email", ""));
                        etContact.setText(response.optString("contact", ""));
                    } catch (Exception e) {
                        Log.e(TAG, "JSON parse error", e);
                        Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Profile fetch error", error);
                    Toast.makeText(this, "Failed to fetch profile. Check connection.", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(getReq);
    }

    private void updateProfile() {
        String first = etFirstName.getText().toString().trim();
        String last = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String contact = etContact.getText().toString().trim();

        if (TextUtils.isEmpty(first) || TextUtils.isEmpty(last) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(contact)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject body = new JSONObject();
            body.put("firstname", first);
            body.put("lastname", last);
            body.put("email", email);
            body.put("contact", contact);
            body.put("membership_end_date", "2025-12-31");

            String url = BASE_URL + "/" + username;

            JsonObjectRequest putReq = buildRequest(
                    Request.Method.PUT,
                    url,
                    body,
                    resp -> Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show(),
                    err -> {
                        Log.e(TAG, "Profile update error", err);
                        Toast.makeText(this, "Update failed. Check connection.", Toast.LENGTH_SHORT).show();
                    }
            );

            queue.add(putReq);

        } catch (Exception e) {
            Log.e(TAG, "Error preparing update JSON", e);
            Toast.makeText(this, "Something went wrong preparing the update", Toast.LENGTH_SHORT).show();
        }
    }

    /** Helper to build a request with retry policy */
    private JsonObjectRequest buildRequest(
            int method,
            String url,
            JSONObject body,
            com.android.volley.Response.Listener<JSONObject> onSuccess,
            com.android.volley.Response.ErrorListener onError) {

        JsonObjectRequest req = new JsonObjectRequest(method, url, body, onSuccess, onError);
        req.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        return req;
    }
}
