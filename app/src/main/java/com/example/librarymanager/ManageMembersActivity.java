package com.example.librarymanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
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

public class ManageMembersActivity extends AppCompatActivity implements MemberAdapter.OnMemberClickListener {

    private static final String TAG = "ManageMembersActivity";
    private final List<Member> memberList = new ArrayList<>();
    private MemberAdapter adapter;
    private RequestQueue queue;
    private static final String BASE_URL = "http://10.224.41.18/comp2000/library/members";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_members);

        RecyclerView rv = findViewById(R.id.rvMembers);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MemberAdapter(memberList, this);
        rv.setAdapter(adapter);

        findViewById(R.id.btnAddMember).setOnClickListener(v -> showAddEditDialog(null));

        queue = Volley.newRequestQueue(this);
        fetchMembers();
    }

    /** Fetch members from API and update adapter */
    private void fetchMembers() {
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, BASE_URL, null,
                response -> {
                    List<Member> refreshed = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject o = response.getJSONObject(i);
                            refreshed.add(new Member(
                                    o.getString("username"),
                                    o.getString("firstname"),
                                    o.getString("lastname"),
                                    o.getString("email"),
                                    o.getString("contact")
                            ));
                        }
                        adapter.updateMembers(refreshed); // Use adapter's update method
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing members", e);
                        Toast.makeText(this, "Error loading members", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching members", error);
                    Toast.makeText(this, "Error fetching members", Toast.LENGTH_SHORT).show();
                });
        req.setRetryPolicy(new DefaultRetryPolicy(5000, 2, 1));
        queue.add(req);
    }

    private void showAddEditDialog(Member existingMember) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_member, null);
        EditText etUsername = dialogView.findViewById(R.id.etUsername);
        EditText etFirstName = dialogView.findViewById(R.id.etFirstName);
        EditText etLastName = dialogView.findViewById(R.id.etLastName);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        EditText etContact = dialogView.findViewById(R.id.etContact);

        boolean isEdit = existingMember != null;
        if (isEdit) {
            etUsername.setText(existingMember.username);
            etUsername.setEnabled(false);
            etFirstName.setText(existingMember.firstname);
            etLastName.setText(existingMember.lastname);
            etEmail.setText(existingMember.email);
            etContact.setText(existingMember.contact);
        }

        new AlertDialog.Builder(this)
                .setTitle(isEdit ? "Edit Member" : "Add Member")
                .setView(dialogView)
                .setPositiveButton(isEdit ? "Update" : "Add", (dialog, which) -> {
                    String username = etUsername.getText().toString().trim();
                    String first = etFirstName.getText().toString().trim();
                    String last = etLastName.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String contact = etContact.getText().toString().trim();

                    if (TextUtils.isEmpty(username) || TextUtils.isEmpty(first) ||
                            TextUtils.isEmpty(last) || TextUtils.isEmpty(email) || TextUtils.isEmpty(contact)) {
                        Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (isEdit) {
                        updateMember(username, first, last, email, contact);
                    } else {
                        addMember(username, first, last, email, contact);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void addMember(String username, String first, String last, String email, String contact) {
        try {
            JSONObject body = new JSONObject();
            body.put("username", username);
            body.put("firstname", first);
            body.put("lastname", last);
            body.put("email", email);
            body.put("contact", contact);
            body.put("membership_end_date", "2025-12-31");

            JsonObjectRequest req = buildRequest(Request.Method.POST, BASE_URL, body,
                    response -> {
                        Toast.makeText(this, "Member added", Toast.LENGTH_SHORT).show();
                        fetchMembers();
                    },
                    error -> {
                        Log.e(TAG, "Error adding member", error);
                        Toast.makeText(this, "Failed to add member", Toast.LENGTH_SHORT).show();
                    });
            queue.add(req);
        } catch (Exception e) {
            Log.e(TAG, "JSON build error", e);
        }
    }

    private void updateMember(String username, String first, String last, String email, String contact) {
        try {
            JSONObject body = new JSONObject();
            body.put("firstname", first);
            body.put("lastname", last);
            body.put("email", email);
            body.put("contact", contact);
            body.put("membership_end_date", "2025-12-31");

            JsonObjectRequest req = buildRequest(Request.Method.PUT, BASE_URL + "/" + username, body,
                    resp -> {
                        Toast.makeText(this, "Member updated", Toast.LENGTH_SHORT).show();
                        fetchMembers();
                    },
                    err -> {
                        Log.e(TAG, "Update failed", err);
                        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                    });
            queue.add(req);
        } catch (Exception e) {
            Log.e(TAG, "JSON build error", e);
        }
    }

    private void deleteMember(Member member) {
        JsonObjectRequest req = buildRequest(Request.Method.DELETE, BASE_URL + "/" + member.username, null,
                resp -> {
                    int pos = memberList.indexOf(member);
                    if (pos != -1) {
                        memberList.remove(pos);
                        adapter.notifyItemRemoved(pos);
                    }
                    Toast.makeText(this, "Member deleted", Toast.LENGTH_SHORT).show();
                },
                err -> {
                    Log.e(TAG, "Delete failed", err);
                    Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
                });
        queue.add(req);
    }

    private JsonObjectRequest buildRequest(int method, String url, JSONObject body,
                                           com.android.volley.Response.Listener<JSONObject> ok,
                                           com.android.volley.Response.ErrorListener err) {
        JsonObjectRequest req = new JsonObjectRequest(method, url, body, ok, err);
        req.setRetryPolicy(new DefaultRetryPolicy(5000, 2, 1));
        return req;
    }

    @Override
    public void onEdit(Member member) {
        showAddEditDialog(member);
    }

    @Override
    public void onDelete(Member member) {
        new AlertDialog.Builder(this)
                .setMessage("Delete member " + member.username + "?")
                .setPositiveButton("Yes", (d, w) -> deleteMember(member))
                .setNegativeButton("No", null)
                .show();
    }
}
