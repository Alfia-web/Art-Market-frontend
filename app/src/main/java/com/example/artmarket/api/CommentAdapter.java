package com.example.artmarket.api;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.artmarket.R;
import com.example.artmarket.model.Comment;

import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    public interface OnDeleteListener {
        void onDelete(Long commentId);
    }

    private final List<Comment> comments;
    private final long userId;
    private final OnDeleteListener deleteListener;

    private final DateTimeFormatter outputFormat =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private String formatDate(String iso) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(iso);
            return dateTime.format(outputFormat);
        } catch (Exception e) {
            return iso;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsername;
        TextView txtText;
        TextView txtData;

        public ViewHolder(View view) {
            super(view);
            txtText = view.findViewById(R.id.commentText);
            txtUsername = view.findViewById(R.id.commentUsername);
            txtData = view.findViewById(R.id.commentDate);
        }
    }

    public CommentAdapter(List<Comment> comments, long userId, OnDeleteListener deleteListener) {
        this.comments = comments;
        this.userId = userId;
        this.deleteListener = deleteListener;
    }

    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentAdapter.ViewHolder holder,int position) {
        Comment c = comments.get(position);
        holder.txtUsername.setText(c.getUsername());
        holder.txtText.setText(c.getText());
        holder.txtData.setText(formatDate(c.getAddAt()));

        if (c.getUserId() != null && c.getUserId() == userId) {
            holder.itemView.setBackgroundColor(0x22FF0000);
            holder.txtUsername.setTextColor(0xFFE53935);
        } else {
            holder.itemView.setBackgroundColor(0x00000000);
            holder.txtUsername.setTextColor(0xFF212121);
        }

        holder.itemView.setOnClickListener(view -> {
            if(c.getUserId()!=null && c.getUserId()==userId){
                deleteListener.onDelete(c.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
