package com.example.librarymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookVH> {

    // Interface for click handling
    public interface OnBookClickListener {
        void onBookClick(Book book);
    }

    private final List<Book> books;
    private final OnBookClickListener listener;

    // Updated constructor to accept click listener
    public BookAdapter(List<Book> books, OnBookClickListener listener) {
        this.books = books;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookVH holder, int position) {
        Book b = books.get(position);

        // Localisation-friendly placeholders
        holder.tvTitle.setText(b.title);
        holder.tvAuthor.setText(
                holder.itemView.getContext().getString(R.string.book_author, b.author)
        );
        holder.tvGenre.setText(
                holder.itemView.getContext().getString(R.string.book_genre, b.genre)
        );

        // Click behaviour for item
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onBookClick(b);
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    /**
     * Update the list and refresh
     */
    public void updateBooks(List<Book> updated) {
        books.clear();
        books.addAll(updated);
        notifyItemRangeChanged(0, books.size());
    }

    /**
     * ViewHolder for book items
     */
    public static class BookVH extends RecyclerView.ViewHolder {
        public final TextView tvTitle;
        public final TextView tvAuthor;
        public final TextView tvGenre;

        public BookVH(View v) {
            super(v);
            tvTitle  = v.findViewById(R.id.tvBookTitle);
            tvAuthor = v.findViewById(R.id.tvBookAuthor);
            tvGenre  = v.findViewById(R.id.tvBookGenre);
        }
    }
}
