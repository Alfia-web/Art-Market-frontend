package com.example.artmarket.api;
import com.example.artmarket.BiddingImage;
import com.example.artmarket.R;
import com.bumptech.glide.request.target.Target;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import com.bumptech.glide.request.target.Target;
import com.example.artmarket.databinding.BiddingImageBinding;
import com.example.artmarket.model.Image;
import com.bumptech.glide.Glide;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private List<Image> images;

    public Adapter(List<Image> images) {
        this.images = images;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Image img = images.get(position);
        String pathImage = img.getPathImage();

        if (pathImage != null && pathImage.startsWith("/images/")) {
            String url = "http://10.0.2.2:8080" + pathImage;

            Glide.with(holder.itemView.getContext()).load(url)
                    .into(holder.imageView);
        }  else {
            holder.imageView.setImageDrawable(null);
        }

        holder.itemView.setOnClickListener(v->{
            Context context = v.getContext();

            //без указания компонента, описываем действие, которое должно выполниться
            //context  - откуда
            //BiddingImage - куда
            Intent intent = new Intent(context, BiddingImage.class);

            intent.putExtra("image_url", pathImage);
            intent.putExtra("id", img.getId());
            intent.putExtra("name", img.getAuthor());
            intent.putExtra("author",img.getAuthor());
            intent.putExtra("width", img.getWidth());
            intent.putExtra("height", img.getHeight());
            intent.putExtra("genre", img.getGenres());

            context.startActivity(intent);
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

    public void setData(List<Image> newImages){
        this.images = newImages;
        notifyDataSetChanged();
    }
}