package com.example.artmarket;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.artmarket.api.Adapter;
import com.example.artmarket.api.RetrofitClient;
import com.example.artmarket.model.Auction;
import com.example.artmarket.model.Image;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Search extends Fragment {
    private Adapter adapter;
    private List<Image> allImages = new ArrayList<>();
    private List<Image> filteredImages = new ArrayList<>();
    private String selectedStatus = null;
    private String currentQuery = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        EditText searchInput = view.findViewById(R.id.searchInput);
        RecyclerView recyclerView = view.findViewById(R.id.searchRecycler);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));

        Chip chipAll = view.findViewById(R.id.chipAll);
        Chip chipPending = view.findViewById(R.id.chipPending);
        Chip chipActive = view.findViewById(R.id.chipActive);
        Chip chipFinished = view.findViewById(R.id.chipFinished);
        chipAll.setChecked(true);

        adapter = new Adapter(filteredImages);
        recyclerView.setAdapter(adapter);

        RetrofitClient.getInstance().getImages()
                .enqueue(new Callback<List<Image>>() {
                    @Override
                    public void onResponse(Call<List<Image>> call, Response<List<Image>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            allImages = response.body();
                            applyFilter();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Image>> call, Throwable t) {
                        Toast.makeText(requireContext(),
                                "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                    }
                });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s,int start,int count,int after) {}
            @Override
            public void onTextChanged(CharSequence s,int start,int before,int count) {
                currentQuery = s.toString();
                applyFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        chipAll.setOnClickListener(v -> {
            selectedStatus = null;
            applyFilter();
        });

        chipPending.setOnClickListener(v -> {
            selectedStatus = "PENDING";
            applyFilter();
        });

        chipActive.setOnClickListener(v -> {
            selectedStatus = "ACTIVE";
            applyFilter();
        });

        chipFinished.setOnClickListener(v -> {
            selectedStatus = "FINISHED";
            applyFilter();
        });

        return view;
    }

    private void applyFilter() {
        filteredImages.clear();

        for (Image img : allImages) {
            boolean byName = img.getNameImage() != null && img.getNameImage().toLowerCase().contains(currentQuery.toLowerCase());

            if (currentQuery.isEmpty()) {
                byName = true;
            }

            if (!byName) {
                continue;
            }

            if (selectedStatus == null) {
                filteredImages.add(img);
                continue;
            }

            RetrofitClient.getInstance()
                    .getAuction(img.getId())
                    .enqueue(new Callback<Auction>() {
                        @Override
                        public void onResponse(Call<Auction> call, Response<Auction> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (selectedStatus.equals(response.body().getStatus())) {
                                    filteredImages.add(img);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Auction> call,Throwable t) {
                        }
                    });
        }
        adapter.notifyDataSetChanged();
    }
}