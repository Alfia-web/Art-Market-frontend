package com.example.artmarket;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.artmarket.api.Adapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.artmarket.api.RetrofitClient;
import com.example.artmarket.api.ApiService;
import com.example.artmarket.model.Image;
import com.example.artmarket.R;
import com.google.android.material.textfield.TextInputEditText;

public class BiddingImage extends AppCompatActivity {
    private final String[] genres = {
            "Без жанра", "Марина", "Пейзаж", "Анималистическая живопись",
            "Архитектурная живопись", "Батальный жанр", "Жанровая живопись",
            "Исторический жанр", "Натюрморт", "Портрет",
            "Религиозная живопись", "Модернизм", "Абстракционизм"
    };

    private final String[] genresValues = {
            "Без_жанра", "Марина", "Пейзаж", "Анималистическая_живопись",
            "Архитектурная_живопись", "Батальный_жанр", "Жанровая_живопись",
            "Исторический_жанр", "Натюрморт", "Портрет",
            "Религиозная_живопись", "Модернизм", "Абстракционизм"
    };
    ImageView imgPreview;
    private TextInputEditText txtName;
    private TextInputEditText txtAuthor;
    private TextInputEditText txtWidth;
    private TextInputEditText txtHeight;
    private Spinner spinnerGenre;
    ImageButton btnBack;

    private void disableEdit(TextInputEditText e) {
        e.setKeyListener(null);
        e.setFocusable(false);
        e.setFocusableInTouchMode(false);
        e.setClickable(false);
        e.setCursorVisible(false);
    }

    @Override
    protected void onCreate(Bundle savedInsanseState){
        super.onCreate(savedInsanseState);
        setContentView(R.layout.bidding_image);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v ->
                finish());

        imgPreview = findViewById(R.id.imagePreviewBid);
        txtName = findViewById(R.id.editNameImageBid);
        txtAuthor = findViewById(R.id.editAuthorBid);
        txtWidth = findViewById(R.id.editWidthBid);
        txtHeight = findViewById(R.id.editHeightBid);
        spinnerGenre = findViewById(R.id.spinnerGenresBid);

        //getIntent() - вернёт Intent, когда ты сделаешь intent.putExtra..
        //getStringExtra - достаём строку в которую раньше положили putExtra(key, value)
        //"image_url" - ключ
        String pathImage = getIntent().getStringExtra("image_url");
        String name = getIntent().getStringExtra("name");
        String author = getIntent().getStringExtra("author");
        int width = getIntent().getIntExtra("width", 0);
        int height = getIntent().getIntExtra("height",0);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, genres);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(adapter);
        String genre = getIntent().getStringExtra("genre");
        if (genre == null) genre = "";
        genre = genre.trim();

        txtName.setText(name);
        txtAuthor.setText(author);
        txtWidth.setText(String.valueOf(width));
        txtHeight.setText(String.valueOf(height));
        for (int i = 0; i < genresValues.length; i++) {
            if (genresValues[i].equalsIgnoreCase(genre)) {
                spinnerGenre.setSelection(i);
                break;
            }
        }

        disableEdit(txtName);
        disableEdit(txtAuthor);
        disableEdit(txtWidth);
        disableEdit(txtHeight);

        if (pathImage != null) {
            String url = "http://10.0.2.2:8080" + pathImage;

            Glide.with(this)
                    .load(url)
                    .into(imgPreview);
        }
    }
}