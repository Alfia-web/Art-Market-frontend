package com.example.artmarket;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.artmarket.api.Adapter;
import com.example.artmarket.api.RetrofitClient;
import com.example.artmarket.model.Auction;
import com.example.artmarket.model.Favorite;
import com.example.artmarket.model.Image;
import com.example.artmarket.R;
import com.example.artmarket.api.ProfileAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class profile extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        RecyclerView recyclerProfile = view.findViewById(R.id.recyclerProfile);
        RecyclerView recyclerFavorite = view.findViewById(R.id.recyclerFavotire);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        LinearLayout layoutBalance = view.findViewById(R.id.layoutBalance);
        TextView txtBalance = view.findViewById(R.id.txtBalance);
        TextInputEditText editMoney = view.findViewById(R.id.addMoney);
        Button btnAddMoney = view.findViewById(R.id.btnMoreMoney);

        recyclerProfile.setVisibility(View.VISIBLE);

        StaggeredGridLayoutManager layoutManager1 =
                new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerProfile.setLayoutManager(layoutManager1);

        StaggeredGridLayoutManager layoutManager2 =
                new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerFavorite.setLayoutManager(layoutManager2);

        SharedPreferences sp = requireContext().getSharedPreferences("PC", Context.MODE_PRIVATE);
        long userId = Long.parseLong(sp.getString("TY", "-1"));

        RetrofitClient.getInstance().getImagesByUser(userId).enqueue(new retrofit2.Callback<List<Image>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Image>> call, retrofit2.Response<List<Image>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProfileAdapter adapter = new ProfileAdapter(response.body(), new ProfileAdapter.OnItemClickListener() {
                        @Override
                        public void onDeleteClick(Image image) {
                            RetrofitClient.getInstance().getAuction(image.getId())
                                    .enqueue(new Callback<Auction>() {
                                        @Override
                                        public void onResponse(Call<Auction> call, Response<Auction> response) {
                                            if (response.isSuccessful() && response.body() != null) {
                                                Auction auction = response.body();

                                                if ("ACTIVE".equals(auction.getStatus())) {
                                                    Toast.makeText(requireContext(),
                                                            "Нельзя удалить, картина участвует в торгах",
                                                            Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                if("FINISHED".equals(auction.getStatus())){
                                                    Toast.makeText(requireContext(),
                                                            "Нельзя удалить, картина участвовала в торгах",
                                                            Toast.LENGTH_SHORT).show();
                                                    return;
                                                }

                                                RetrofitClient.getInstance().deleteImage(image.getId())
                                                        .enqueue(new Callback<Void>() {
                                                            @Override
                                                            public void onResponse(Call<Void> call, Response<Void> resp) {
                                                                Toast.makeText(requireContext(), "Удалено", Toast.LENGTH_SHORT).show();

                                                                requireActivity().getSupportFragmentManager()
                                                                        .beginTransaction()
                                                                        .replace(R.id.frame_layout, new profile())
                                                                        .commit();
                                                            }

                                                            @Override
                                                            public void onFailure(Call<Void> call, Throwable t) {
                                                                Toast.makeText(requireContext(), "Ошибка удаления", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Auction> call, Throwable t) {
                                            Toast.makeText(requireContext(),
                                                    "Ошибка проверки аукциона",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        @Override
                        public void onEditClick(Image image) {
                            RetrofitClient.getInstance().getAuction(image.getId()).enqueue(new Callback<Auction>() {
                                @Override
                                public void onResponse(Call<Auction> call, Response<Auction> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        String status = response.body().getStatus();


                                        if ("ACTIVE".equals(status)) {
                                            Toast.makeText(requireContext(),
                                                    "Нельзя редактировать, картина участвует в торгах",
                                                    Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        if ("FINISHED".equals(status)) {
                                            Toast.makeText(requireContext(),
                                                    "Нельзя удалить, картина участвовала в торгах",
                                                    Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        requireActivity().getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.frame_layout, plusPicture.newInstanceForEdit(image)).commit();
                                    }

                                }

                                @Override
                                public void onFailure(Call<Auction> call, Throwable t) {
                                    Toast.makeText(requireContext(),
                                            "Ошибка проверки аукциона", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    recyclerProfile.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(retrofit2.Call<List<Image>> call,Throwable t) {
                Toast.makeText(requireContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show();
            }
        });

        RetrofitClient.getInstance().getFavorites(userId).enqueue(new Callback<List<Image>>() {
            @Override
            public void onResponse(Call<List<Image>> call, Response<List<Image>> response) {
                Log.d("FAV", "code=" + response.code());
                Log.d("FAV", "body=" + response.body());
                if(response.isSuccessful() && response.body()!=null){
                    Adapter adapter = new Adapter(response.body());
                    recyclerFavorite.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Image>> call, Throwable t) {
                Toast.makeText(requireContext(),
                        "Ошибка загрузки избранного", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddMoney.setOnClickListener(v -> {
            String amountStr = editMoney.getText() != null ? editMoney.getText().toString().trim() : "";
            if (amountStr.isEmpty()){
                Toast.makeText(requireContext(),
                        "Введите сумму", Toast.LENGTH_SHORT).show();
                return;
            }

            RetrofitClient.getInstance().addMoney(userId, amountStr)
                    .enqueue(new Callback<BigDecimal>() {
                        @Override
                        public void onResponse(Call<BigDecimal> call, Response<BigDecimal> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                txtBalance.setText(response.body().toPlainString() + " ₽");
                                editMoney.setText("");
                                Toast.makeText(requireContext(),
                                        "Счёт пополнен!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<BigDecimal> call, Throwable t) {
                            Toast.makeText(requireContext(), "Ошибка сети", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                recyclerProfile.setVisibility(View.GONE);
                recyclerFavorite.setVisibility(View.GONE);
                layoutBalance.setVisibility(View.GONE);

                switch (tab.getPosition()) {
                    case 0:
                        recyclerProfile.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        recyclerFavorite.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        layoutBalance.setVisibility(View.VISIBLE);
                        RetrofitClient.getInstance().getBalance(userId).enqueue(new Callback<BigDecimal>() {
                            @Override
                            public void onResponse(Call<BigDecimal> call, Response<BigDecimal> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    txtBalance.setText(response.body().toPlainString() + " ₽");
                                }
                            }

                            @Override
                            public void onFailure(Call<BigDecimal> call, Throwable t) {
                            }
                        });
                        break;
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }
}
