package com.example.librarymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberVH> {

    public interface OnMemberClickListener {
        void onEdit(Member member);
        void onDelete(Member member);
    }

    private final List<Member> members;
    private final OnMemberClickListener listener;

    public MemberAdapter(List<Member> members, OnMemberClickListener listener) {
        this.members = members;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MemberVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member, parent, false);
        return new MemberVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberVH holder, int position) {
        Member m = members.get(position);
        holder.tvName.setText(holder.itemView.getContext()
                .getString(R.string.member_full_name, m.firstname, m.lastname));
        holder.tvEmail.setText(m.email);

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onEdit(m);
            return true;
        });
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(m);
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public void updateMembers(List<Member> updated) {
        members.clear();
        members.addAll(updated);
        notifyItemRangeChanged(0, members.size());
    }

    public static class MemberVH extends RecyclerView.ViewHolder {
        final TextView tvName, tvEmail;
        public MemberVH(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvName);
            tvEmail = v.findViewById(R.id.tvEmail);
        }
    }
}
