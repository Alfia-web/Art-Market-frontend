package com.example.artmarket.api;
import com.example.artmarket.BiddingImage;
import com.example.artmarket.R;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import com.example.artmarket.model.Auction;
import com.example.artmarket.model.Image;
import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private List<Image> images;
    public Adapter(List<Image> images) {
        this.images = images;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView txtStatus;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            txtStatus = view.findViewById(R.id.txtStatus);
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

        holder.txtStatus.setVisibility(View.VISIBLE);
        RetrofitClient.getInstance().getAuction(img.getId()).enqueue(new Callback<Auction>() {
            @Override
            public void onResponse(Call<Auction> call, Response<Auction> response) {
                if(!response.isSuccessful() || response.body() == null){
                    holder.txtStatus.setVisibility(View.GONE);
                    return;
                }

                String status = response.body().getStatus();
                switch (status) {
                    case "PENDING":
                        holder.txtStatus.setText("Скоро");
                        holder.txtStatus.setBackgroundResource(R.drawable.badge_pending);
                        break;
                    case "ACTIVE":
                        holder.txtStatus.setText("Торги идут");
                        holder.txtStatus.setBackgroundResource(R.drawable.badge_active);
                        break;
                    case "FINISHED":
                        holder.txtStatus.setText("Завершено");
                        holder.txtStatus.setBackgroundResource(R.drawable.badge_finished);
                        break;
                    default:
                        holder.txtStatus.setVisibility(View.GONE);
                        break;
                }
            }
            @Override
            public void onFailure(Call<Auction> call, Throwable t) {
                holder.txtStatus.setVisibility(View.GONE);
            }
        });

        holder.itemView.setOnClickListener(v->{
            Context context = v.getContext();

            Intent intent = new Intent(context, BiddingImage.class);

            intent.putExtra("image_url", pathImage);
            intent.putExtra("id", img.getId());
            intent.putExtra("name", img.getNameImage());
            intent.putExtra("author",img.getAuthor());
            intent.putExtra("width", img.getWidth());
            intent.putExtra("height", img.getHeight());
            intent.putExtra("genre", img.getGenres());

            context.startActivity(intent);
        });

        if (img.getWidth() > 0) {
            ViewGroup.LayoutParams params = holder.imageView.getLayoutParams();
            if (img.getWidth() > 0 && img.getHeight() > 0) {
                int screenWidth = holder.itemView.getResources().getDisplayMetrics().widthPixels / 2;
                int calculateHeight = (int) ((float) img.getHeight() / img.getWidth() * screenWidth);
                params.height = calculateHeight;
            } else {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            holder.imageView.setLayoutParams(params);

            Glide.with(holder.itemView.getContext())
                    .load("http://10.0.2.2:8080" + pathImage)
                    .override(params.width, params.height)
                    .centerCrop()
                    .into(holder.imageView);
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