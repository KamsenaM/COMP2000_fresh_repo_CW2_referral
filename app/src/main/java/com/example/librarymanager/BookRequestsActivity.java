package com.example.librarymanager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class BookRequestsActivity extends AppCompatActivity implements BookRequestAdapter.RequestActionListener {

    private static final String TAG = "BookRequestsActivity";
    private final List<BookRequest> requestList = new ArrayList<>();
    private BookRequestAdapter adapter;
    private final String BASE_URL = "http://10.224.41.18/comp2000/library/books";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_requests);

        RecyclerView rv = findViewById(R.id.rvBookRequests);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BookRequestAdapter(this, requestList, this);
        rv.setAdapter(adapter);

        fetchBookRequests();
    }

    private void fetchBookRequests() {
        RequestQueue queue = Volley.newRequestQueue(this);

        @SuppressLint("NotifyDataSetChanged") JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, BASE_URL, null,
                response -> {
                    requestList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.has("id") ? obj.getInt("id") : i;
                            String username = obj.optString("username", "");
                            String bookTitle = obj.optString("book_title", "");
                            String issueDate = obj.optString("issue_date", "");
                            String returnDate = obj.optString("return_date", "");
                            requestList.add(new BookRequest(id, username, bookTitle, issueDate, returnDate));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing request list", e);
                        Toast.makeText(this, "Error loading requests", Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    Log.e(TAG, "Network error: " + error.getMessage(), error);
                    Toast.makeText(this, "Failed to load requests. Check connection.", Toast.LENGTH_SHORT).show();
                });

        request.setRetryPolicy(new DefaultRetryPolicy(5000, 2, 1));
        queue.add(request);
    }

    @Override
    public void onApprove(BookRequest request) {
        NotificationHelper.sendNotification(
                this,
                "Book Approved",
                request.bookTitle + " approved for " + request.username,
                "book_request"
        );
        removeRequestFromList(request);
    }

    @Override
    public void onDeny(BookRequest request) {
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject body = new JSONObject();
        try {
            body.put("username", request.username);
            body.put("book_title", request.bookTitle);
        } catch (Exception e) {
            Log.e(TAG, "Error building JSON for deny", e);
            Toast.makeText(this, "Error preparing request", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest deleteReq = new JsonObjectRequest(Request.Method.DELETE, BASE_URL, body,
                response -> {
                    Toast.makeText(this, "Request denied", Toast.LENGTH_SHORT).show();
                    NotificationHelper.sendNotification(
                            this,
                            "Book Denied",
                            request.bookTitle + " request denied for " + request.username,
                            "book_request"
                    );
                    removeRequestFromList(request);
                },
                error -> {
                    Log.e(TAG, "Error denying request: " + error.getMessage(), error);
                    Toast.makeText(this, "Failed to deny request", Toast.LENGTH_SHORT).show();
                });

        deleteReq.setRetryPolicy(new DefaultRetryPolicy(5000, 2, 1));
        queue.add(deleteReq);
    }

    private void removeRequestFromList(BookRequest request) {
        int pos = requestList.indexOf(request);
        if (pos != -1) {
            requestList.remove(pos);
            adapter.notifyItemRemoved(pos);
        }
    }

}
