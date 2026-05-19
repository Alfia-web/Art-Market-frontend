package com.example.artmarket;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.artmarket.api.Adapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.artmarket.api.RetrofitClient;
import com.example.artmarket.api.ApiService;
import com.example.artmarket.model.Image;
import com.example.artmarket.R;

public class Home extends Fragment {
    private RecyclerView recyclerView;

    public Home() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        List<Image> imageList = new ArrayList<>();
        Adapter adapter = new Adapter(imageList);
        recyclerView.setAdapter(adapter);

        RetrofitClient.getInstance().getImages().enqueue(new Callback<List<Image>>() {
            @Override
            public void onResponse(Call<List<Image>> call, Response<List<Image>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setData(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<Image>> call, Throwable t) {
                Log.e("HOME", "Ошибка: " + t.getMessage());
            }
        });

        return view;
    }
}