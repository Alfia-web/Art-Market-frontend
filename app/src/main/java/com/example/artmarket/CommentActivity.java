package com.example.artmarket;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artmarket.api.CommentAdapter;
import com.example.artmarket.api.RetrofitClient;
import com.example.artmarket.model.Comment;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> comments = new ArrayList<>();
    private long userId;
    private long imageId;
    private String username;
    TextInputEditText editComment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        imageId = getIntent().getLongExtra("imageId", -9);

        SharedPreferences sp = getSharedPreferences("PC", MODE_PRIVATE);
        userId = Long.parseLong(sp.getString("TY", "-1"));

        username = sp.getString("USERNAME", "Аноним");

        editComment = findViewById(R.id.editComment);
        recyclerView = findViewById(R.id.recyclerComments);
        ImageButton btnSend = findViewById(R.id.sendComment);
        ImageButton btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        commentAdapter = new CommentAdapter(comments, userId, commentId -> {
            new AlertDialog.Builder(this)
                    .setTitle("Удалить комментарий?")
                    .setPositiveButton("Да", (dialog, which) -> {
                        RetrofitClient.getInstance()
                                .deleteComment(commentId, userId)
                                .enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call,
                                                           Response<Void> response) {
                                        if (response.isSuccessful())
                                            loadComments();
                                    }
                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {}
                                });
                    })
                    .setNegativeButton("Нет", null)
                    .show();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(commentAdapter);

        loadComments();

        btnSend.setOnClickListener(v -> {
            String text = editComment.getText().toString();
            if (text.isEmpty())
                return;

            RetrofitClient.getInstance().addComment(imageId, userId, username, text)
                    .enqueue(new Callback<Comment>() {
                        @Override
                        public void onResponse(Call<Comment> call, Response<Comment> response) {
                            Log.d("COM", "code=" + response.code());
                            if (response.isSuccessful()) {
                                editComment.setText("");
                                loadComments();
                            } else {
                                Log.d("COM", "code"+response.code());
                                Log.d("COM", "body"+response.code());
                                Toast.makeText(CommentActivity.this, "Ошибка отправки",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Comment> call, Throwable t) {
                            Toast.makeText(CommentActivity.this,
                                    "Ошибка сети", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
    private void loadComments(){
        RetrofitClient.getInstance().getComments(imageId)
                .enqueue(new Callback<List<Comment>>() {
                    @Override
                    public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                        if(response.isSuccessful() && response.body()!=null){
                            comments.clear();
                            comments.addAll(response.body());
                            commentAdapter.notifyDataSetChanged();
                            if(!comments.isEmpty()){
                                recyclerView.smoothScrollToPosition(0);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Comment>> call, Throwable t) {
                    }
                });
    }
}
