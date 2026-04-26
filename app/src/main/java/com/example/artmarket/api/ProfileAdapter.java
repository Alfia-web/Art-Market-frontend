package com.example.artmarket.api;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.artmarket.R;
import com.example.artmarket.model.Image;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onDeleteClick(Image image);
        void onEditClick(Image image);
    }

    private List<Image> images;
    private OnItemClickListener listener;

    public ProfileAdapter(List<Image> images, OnItemClickListener listener) {
        this.images = images;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ImageButton deleteButton;
        public ImageButton editButton;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.profileImageView);
            deleteButton = view.findViewById(R.id.btnDelete);
            editButton = view.findViewById(R.id.btnEdit);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Image img = images.get(position);

        String url = "http://10.0.2.2:8080" + img.getPathImage();

        Glide.with(holder.itemView.getContext()).load(url).into(holder.imageView);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDeleteClick(img);
                }
            }
        });

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onEditClick(img);
                }
            }
        });

        if (img.getWidth() > 0) {
            int screenWidth = holder.itemView.getResources().getDisplayMetrics().widthPixels / 2;
            int calculateHeight = (int) ((float) img.getHeight() / img.getWidth() * screenWidth);

            ViewGroup.LayoutParams params = holder.imageView.getLayoutParams();
            params.height = calculateHeight;
            holder.imageView.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }
}