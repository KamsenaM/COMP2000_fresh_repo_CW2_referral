package com.example.librarymanager;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CatalogueActivity extends AppCompatActivity {

    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue);

        currentUser = getIntent().getStringExtra("username");

        RecyclerView rv = findViewById(R.id.rvCatalogue);
        rv.setLayoutManager(new LinearLayoutManager(this));

        BookAdapter adapter = new BookAdapter(
                LibraryDBHelper.getInstance(this).getAllBooks(),
                selectedBook -> {
                    Intent intent = new Intent(this, BookDetailActivity.class);
                    intent.putExtra("username", currentUser);
                    intent.putExtra("book_title", selectedBook.title);
                    intent.putExtra("book_author", selectedBook.author);
                    intent.putExtra("book_genre", selectedBook.genre);
                    startActivity(intent);
                }
        );

        rv.setAdapter(adapter);
    }
}
