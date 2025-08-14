package com.example.librarymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookRequestAdapter extends RecyclerView.Adapter<BookRequestAdapter.RequestVH> {

    public interface RequestActionListener {
        void onApprove(BookRequest request);
        void onDeny(BookRequest request);
    }

    private final List<BookRequest> requests;
    private final RequestActionListener listener;
    private final Context context;

    public BookRequestAdapter(Context context, List<BookRequest> requests, RequestActionListener listener) {
        this.context = context;
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_book_request, parent, false);
        return new RequestVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestVH holder, int position) {
        BookRequest req = requests.get(position);

        // Set book title
        holder.tvBookTitle.setText(req.bookTitle);

        // If using a static string resource for "Requested by", use getString()
        holder.tvRequester.setText(
                context.getString(R.string.requested_by_username, req.username)
        );

        holder.btnApprove.setOnClickListener(v -> {
            if (listener != null) listener.onApprove(req);
        });

        holder.btnDeny.setOnClickListener(v -> {
            if (listener != null) listener.onDeny(req);
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public static class RequestVH extends RecyclerView.ViewHolder {
        TextView tvBookTitle, tvRequester;
        Button btnApprove, btnDeny;

        public RequestVH(@NonNull View itemView) {
            super(itemView);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvRequester = itemView.findViewById(R.id.tvRequester);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnDeny = itemView.findViewById(R.id.btnDeny);
        }
    }
}
