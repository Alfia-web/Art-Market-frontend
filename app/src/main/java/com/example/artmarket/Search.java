package com.example.artmarket;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.artmarket.api.Adapter;
import com.example.artmarket.api.RetrofitClient;
import com.example.artmarket.model.Image;

import java.util.ArrayList;
import java.util.List;

public class Search extends Fragment {
    private Adapter adapter;
    private List<Image> allImages = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        EditText searchInput = view.findViewById(R.id.searchInput);
        RecyclerView recyclerView = view.findViewById(R.id.searchRecycler);

        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new Adapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        RetrofitClient.getInstance().getImages().enqueue(new retrofit2.Callback<List<Image>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Image>> call, retrofit2.Response<List<Image>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allImages = response.body();
                    adapter.setData(allImages);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<Image>> call, Throwable t) {
                Toast.makeText(requireContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show();
            }
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterImages(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void filterImages(String query) {
        if (query.isEmpty()) {
            adapter.setData(allImages);
            return;
        }

        List<Image> filtered = new ArrayList<>();
        for (Image img : allImages) {
            if (img.getNameImage() != null && img.getNameImage().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(img);
            }
        }
        adapter.setData(filtered);
    }
}
