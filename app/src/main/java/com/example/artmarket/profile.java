package com.example.artmarket;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.artmarket.api.RetrofitClient;
import com.example.artmarket.model.Image;
import com.example.artmarket.R;
import com.example.artmarket.api.ProfileAdapter;

import org.json.JSONException;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class profile extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerProfile);

        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        SharedPreferences sp = requireContext().getSharedPreferences("PC", Context.MODE_PRIVATE);
        long userId = Long.parseLong(sp.getString("TY", "-1"));

        RetrofitClient.getInstance().getImagesByUser(userId).enqueue(new retrofit2.Callback<List<Image>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Image>> call, retrofit2.Response<List<Image>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProfileAdapter adapter = new ProfileAdapter(response.body(), new ProfileAdapter.OnItemClickListener() {
                        @Override
                        public void onDeleteClick(Image image) {
                            RetrofitClient.getInstance().deleteImage(image.getId()).enqueue(new retrofit2.Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call,Response<Void> resp) {
                                    Toast.makeText(requireContext(), "Удалено", Toast.LENGTH_SHORT).show();
                                    requireActivity().getSupportFragmentManager()
                                            .beginTransaction().replace(R.id.frame_layout, new profile()).commit();
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(requireContext(), "Ошибка удаления", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onEditClick(Image image) {
                            requireActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.frame_layout, plusPicture.newInstanceForEdit(image)).commit();
                        }
                    });
                    recyclerView.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(retrofit2.Call<List<Image>> call,Throwable t) {
                Toast.makeText(requireContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
