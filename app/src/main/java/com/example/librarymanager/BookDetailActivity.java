package com.example.librarymanager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Displays details for a selected book and lets the member request it.
 */
public class BookDetailActivity extends AppCompatActivity {

    private static final String TAG = "BookDetailActivity";
    private static final String BASE_URL = "http://10.224.41.18/comp2000/library/books";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // Get data passed from previous screen
        String username = getIntent().getStringExtra("username");
        String bookTitle = getIntent().getStringExtra("book_title");
        String bookAuthor = getIntent().getStringExtra("book_author");
        String bookGenre = getIntent().getStringExtra("book_genre");

        // Validate mandatory extras
        if (username == null || username.trim().isEmpty() ||
                bookTitle == null || bookTitle.trim().isEmpty()) {
            Toast.makeText(this, R.string.error_missing_book_or_user, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Bind to views
        TextView tvTitle = findViewById(R.id.tvBookTitle);
        TextView tvAuthor = findViewById(R.id.tvBookAuthor);
        TextView tvGenre = findViewById(R.id.tvBookGenre);
        Button btnRequest = findViewById(R.id.btnRequestBook);

        // Populate UI
        tvTitle.setText(bookTitle);
        if (bookAuthor != null && !bookAuthor.trim().isEmpty()) {
            tvAuthor.setText(getString(R.string.book_author, bookAuthor));
        }
        if (bookGenre != null && !bookGenre.trim().isEmpty()) {
            tvGenre.setText(getString(R.string.book_genre, bookGenre));
        }

        // Handle book request click
        btnRequest.setOnClickListener(v -> issueBook(username, bookTitle));
    }

    /**
     * Sends a book request to the API for this member.
     */
    private void issueBook(String username, String title) {
        RequestQueue q = Volley.newRequestQueue(getApplicationContext());

        // Calculate today and return date (+7 days)
        String issueDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US)
                .format(Calendar.getInstance().getTime());
        Calendar returnCal = Calendar.getInstance();
        returnCal.add(Calendar.DAY_OF_YEAR, 7);
        String returnDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US)
                .format(returnCal.getTime());

        // Build request body
        JSONObject body = new JSONObject();
        try {
            body.put("username", username);
            body.put("book_title", title);
            body.put("issue_date", issueDate);
            body.put("return_date", returnDate);
        } catch (Exception e) {
            Log.e(TAG, "Error building JSON body", e);
            Toast.makeText(this, R.string.error_requesting_book, Toast.LENGTH_SHORT).show();
            return;
        }

        // Create POST request
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL,
                body,
                response -> {
                    Log.i(TAG, "Book request successful for " + title);
                    NotificationHelper.sendNotification(
                            this,
                            getString(R.string.request_sent),
                            getString(R.string.book_request_success, title),
                            "book_request"
                    );
                },
                error -> {
                    Log.e(TAG, "Network error: " + error.getMessage(), error);
                    String msg = (error.networkResponse != null && error.networkResponse.statusCode == 404) ?
                            getString(R.string.error_fetching_data) :
                            getString(R.string.error_network);
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Retry policy to prevent infinite hang
        req.setRetryPolicy(new DefaultRetryPolicy(
                5000, // 5 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        // Add to queue
        q.add(req);
    }
}
