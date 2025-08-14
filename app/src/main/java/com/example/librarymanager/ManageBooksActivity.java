package com.example.librarymanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ManageBooksActivity extends AppCompatActivity {

    private static final String TAG = "ManageBooksActivity";
    private BookAdapter adapter;
    private LibraryDBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_books);

        RecyclerView rv = findViewById(R.id.rvBooks);
        Button btnAddBook = findViewById(R.id.btnAddBook);

        db = LibraryDBHelper.getInstance(this);

        try {
            List<Book> books = db.getAllBooks();
            adapter = new BookAdapter(books, null); // no click listener needed for manage mode
        } catch (Exception e) {
            Log.e(TAG, "Error loading books from DB", e);
            adapter = new BookAdapter(new java.util.ArrayList<>(), null);
            Toast.makeText(this, "Error loading books", Toast.LENGTH_SHORT).show();
        }

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        btnAddBook.setOnClickListener(v -> showAddBookDialog());
    }

    private void showAddBookDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_book, null);
        EditText etTitle = dialogView.findViewById(R.id.etBookTitle);
        EditText etAuthor = dialogView.findViewById(R.id.etBookAuthor);
        EditText etGenre = dialogView.findViewById(R.id.etBookGenre);

        new AlertDialog.Builder(this)
                .setTitle(R.string.add_new_book)
                .setView(dialogView)
                .setPositiveButton(R.string.add, (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String author = etAuthor.getText().toString().trim();
                    String genre = etGenre.getText().toString().trim();

                    // Validate input fields
                    if (TextUtils.isEmpty(title) || TextUtils.isEmpty(author) || TextUtils.isEmpty(genre)) {
                        Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        db.addBook(title, author, genre);
                        adapter.updateBooks(db.getAllBooks());
                        Toast.makeText(this, "Book added", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error adding book", e);
                        Toast.makeText(this, "Failed to add book", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
