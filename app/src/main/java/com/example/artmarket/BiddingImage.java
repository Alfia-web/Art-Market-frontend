package com.example.artmarket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.artmarket.api.RetrofitClient;
import com.example.artmarket.model.Auction;
import com.example.artmarket.model.Comment;
import com.example.artmarket.model.Favorite;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.os.CountDownTimer;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BiddingImage extends AppCompatActivity {
    private final String[] genres = {
            "Без жанра", "Марина", "Пейзаж", "Анималистическая живопись",
            "Архитектурная живопись", "Батальный жанр", "Жанровая живопись",
            "Исторический жанр", "Натюрморт", "Портрет",
            "Религиозная живопись", "Модернизм", "Абстракционизм"
    };

    private final String[] genresValues = {
            "Без_жанра", "Марина", "Пейзаж", "Анималистический_живопись",
            "Архитектурная_живопись", "Батальный_жанр", "Жанровая_живопись",
            "Исторический_жанр", "Натюрморт", "Портрет",
            "Религиозная_живопись", "Модернизм", "Абстракционизм"
    };

    private ImageView imgPreview;
    private TextInputEditText txtName, txtAuthor, txtWidth, txtHeight, editRateAmount;
    private Spinner spinnerGenre;
    private ImageButton btnBack;
    private TextView txtCurrentBid, txtAuctionStatus, txtEndTime;
    private FloatingActionButton btnAddRateBid;
    private ImageButton btnComment;
    private ImageButton btnFave;
    private boolean hasWinner = false;

    private final DateTimeFormatter format =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private String formatDate(String iso) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(iso);
            return dateTime.format(format);
        } catch (Exception e) {
            return iso;
        }
    }
    private void disableEdit(TextInputEditText e) {
        e.setKeyListener(null);
        e.setFocusable(false);
        e.setClickable(false);
        e.setCursorVisible(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bidding_image);

        btnBack = findViewById(R.id.btnBack);
        imgPreview = findViewById(R.id.imagePreviewBid);
        txtName = findViewById(R.id.editNameImageBid);
        txtAuthor = findViewById(R.id.editAuthorBid);
        txtWidth = findViewById(R.id.editWidthBid);
        txtHeight = findViewById(R.id.editHeightBid);
        spinnerGenre = findViewById(R.id.spinnerGenresBid);
        txtCurrentBid = findViewById(R.id.txtCurrentRateBid);
        txtAuctionStatus = findViewById(R.id.txtAuctionStatusBid);
        txtEndTime = findViewById(R.id.txtEndTimeBid);
        btnAddRateBid = findViewById(R.id.btnAddRateBid);
        editRateAmount = findViewById(R.id.editRateAmountBid);
        btnBack.setOnClickListener(v -> finish());
        btnComment = findViewById(R.id.btnComments);
        btnFave = findViewById(R.id.btnFavorite);

        long imageId = getIntent().getLongExtra("id", -1);
        String pathImage = getIntent().getStringExtra("image_url");
        String name = getIntent().getStringExtra("name");
        String author = getIntent().getStringExtra("author");
        int width = getIntent().getIntExtra("width", 0);
        int height = getIntent().getIntExtra("height", 0);
        String genre = getIntent().getStringExtra("genre");

        SharedPreferences sp = getSharedPreferences("PC", MODE_PRIVATE);
        long currentUserId = Long.parseLong(sp.getString("TY", "-9"));

        txtName.setText(name != null ? name : "");
        txtAuthor.setText(author != null ? author : "");
        txtWidth.setText(String.valueOf(width));
        txtHeight.setText(String.valueOf(height));

        disableEdit(txtName);
        disableEdit(txtAuthor);
        disableEdit(txtWidth);
        disableEdit(txtHeight);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, genres);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(spinnerAdapter);
        if (genre == null) genre = "";
        for (int i = 0; i < genresValues.length; i++) {
            if (genresValues[i].equalsIgnoreCase(genre.trim())) {
                spinnerGenre.setSelection(i);
                break;
            }
        }
        spinnerGenre.setOnTouchListener((v, event) -> true);

        if (pathImage != null) {
            Glide.with(this)
                    .load("http://10.0.2.2:8080" + pathImage)
                    .into(imgPreview);
        }

        btnAddRateBid.setVisibility(View.GONE);
        editRateAmount.setVisibility(View.GONE);

        RetrofitClient.getInstance().checkFavorite(currentUserId, imageId)
                .enqueue(new Callback<Favorite>() {
                    @Override
                    public void onResponse(Call<Favorite> call, Response<Favorite> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            boolean isFave = response.body().getIsFavorite();
                            btnFave.setImageResource(
                                    isFave ? R.drawable.fave_heart_red
                                            : R.drawable.fave_heart_black);
                        }
                    }

                    @Override
                    public void onFailure(Call<Favorite> call, Throwable t) {
                    }
                });


        btnFave.setOnClickListener(v -> {
            RetrofitClient.getInstance().toggle(currentUserId, imageId)
                    .enqueue(new Callback<Favorite>() {
                        @Override
                        public void onResponse(Call<Favorite> call, Response<Favorite> response) {
                            Log.d("FAVE", "code=" + response.code());
                            try {
                                Log.d("FAVE", "body=" + response.body().getIsFavorite());
                                Log.d("FAVE", "error=" + response.errorBody().string());
                            } catch (Exception e) {
                            }

                            if (response.isSuccessful() && response.body() != null) {
                                boolean isFave = response.body().getIsFavorite();
                                Log.d("FAVE", "isFave=" + isFave);
                                btnFave.setImageResource(
                                        isFave ? R.drawable.fave_heart_red
                                                : R.drawable.fave_heart_black);
                            }
                        }

                        @Override
                        public void onFailure(Call<Favorite> call, Throwable t) {
                            Toast.makeText(BiddingImage.this, "Ошибка сети",
                                    Toast.LENGTH_SHORT).show();
                            ;
                        }
                    });
        });

        btnComment.setOnClickListener(v -> {
            Intent intent = new Intent(BiddingImage.this, CommentActivity.class);
            intent.putExtra("imageId", imageId);
            startActivity(intent);
        });

        RetrofitClient.getInstance().getAuction(imageId)
                .enqueue(new Callback<Auction>() {
                    @Override
                    public void onResponse(Call<Auction> call, Response<Auction> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            txtAuctionStatus.setText("Аукцион не найден");
                            return;
                        }

                        Auction auction = response.body();

                        txtCurrentBid.setText("Текущая ставка: " +
                                auction.getCurrentPrice() + " ₽");

                        switch (auction.getStatus()) {
                            case "PENDING":
                                txtAuctionStatus.setText("Торги начнутся " +
                                        (formatDate(auction.getStartTime()) != null ?
                                                formatDate(auction.getStartTime()) : "-"));
                                btnAddRateBid.setVisibility(View.GONE);
                                editRateAmount.setVisibility(View.GONE);
                                break;

                            case "ACTIVE":
                                txtAuctionStatus.setText("Торги идут до " +
                                formatDate(auction.getEndTime() != null ?
                                        formatDate(auction.getEndTime()) : "-"));

                                RetrofitClient.getInstance().getOwnerId(imageId)
                                        .enqueue(new Callback<Long>() {
                                            @Override
                                            public void onResponse(Call<Long> call, Response<Long> response) {
                                                if (response.isSuccessful() && response.body() != null) {
                                                    long ownerId = response.body();

                                                    if (currentUserId != ownerId) {
                                                        btnAddRateBid.setVisibility(View.VISIBLE);
                                                        editRateAmount.setVisibility(View.VISIBLE);
                                                        rateButton(auction);
                                                    } else {
                                                        btnAddRateBid.setVisibility(View.GONE);
                                                        editRateAmount.setVisibility(View.GONE);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Long> call, Throwable t) {}
                                        });
                                break;

                            case "FINISHED":
                                txtAuctionStatus.setText("Торги завершены " + (formatDate(auction.getEndTime()) != null ?
                                        formatDate(auction.getEndTime()) : "-"));
                                txtCurrentBid.setText("Продана за: " +
                                        auction.getCurrentPrice() + " ₽");
                                btnAddRateBid.setVisibility(View.GONE);
                                editRateAmount.setVisibility(View.GONE);

                                if(auction.getWinnerId() != null && auction.getWinnerId()==currentUserId){
                                    if(hasWinner==false) {
                                        new android.app.AlertDialog.Builder(BiddingImage.this)
                                                .setTitle("Вы победили!")
                                                .setMessage("Картина " + name + " ваша за "
                                                        + auction.getFinalPrice() + " ₽")
                                                .setPositiveButton("Принять", null)
                                                .show();
                                        hasWinner=true;
                                    }
                                }
                                break;
                        }
                    }
                    @Override
                    public void onFailure(Call<Auction> call, Throwable t) {
                        txtAuctionStatus.setText("Ошибка загрузки аукциона");
                    }
                });
    }

    private void rateButton(Auction auction) {
        btnAddRateBid.setOnClickListener(v -> {
            String amountStr = editRateAmount.getText().toString().trim();

            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Введите сумму", Toast.LENGTH_SHORT).show();
                return;
            }

            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr);
            } catch (Exception e) {
                Toast.makeText(this, "Неверная сумма", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences sp = getSharedPreferences("PC", MODE_PRIVATE);
            long userId = Long.parseLong(sp.getString("TY", "-1"));

            RetrofitClient.getInstance()
                    .addRate(auction.getId(), userId, amount.toPlainString())
                    .enqueue(new Callback<Auction>() {

                        @Override
                        public void onResponse(Call<Auction> call, Response<Auction> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                txtCurrentBid.setText("Текущая ставка: " +
                                        response.body().getCurrentPrice() + " ₽");

                                Toast.makeText(BiddingImage.this,
                                        "Ставка принята", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            try {
                                String error = response.errorBody() != null
                                        ? response.errorBody().string()
                                        : "Ошибка";

                                Log.d("RATE_ERROR", error);

                                if (error!=null){
                                    Toast.makeText(BiddingImage.this,
                                            error,
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(BiddingImage.this,
                                        "Ошибка обработки",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Auction> call, Throwable t) {
                            Toast.makeText(BiddingImage.this,
                                    "Ошибка сети",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}