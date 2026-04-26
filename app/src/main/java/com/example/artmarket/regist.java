package com.example.artmarket;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.artmarket.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class regist extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_regist, container, false);

        EditText email = view.findViewById(R.id.editEmail);
        EditText password = view.findViewById(R.id.editPassword);
        View button = view.findViewById(R.id.regBotton);
        View signuptext = view.findViewById(R.id.signuptext);


        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean enabled = email.getText().length() > 0 && password.getText().length() > 0;
                button.setEnabled(enabled);
                button.setAlpha(enabled ? 1.0f : 0.5f);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        email.addTextChangedListener(watcher);
        password.addTextChangedListener(watcher);

        button.setEnabled(false);
        button.setAlpha(0.5f);

        signuptext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Переход на регистрацию", Toast.LENGTH_SHORT).show();

                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();

                Log.d("LOGIN", "Отправляем email=" + emailText + " password=" + passwordText);
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).replaceFragment(new SignUpActivity());
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();

                button.setEnabled(false);

                LoginRequest request = new LoginRequest(emailText, passwordText);

                RetrofitClient.getInstance().login(request).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        button.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null) {
                            String body = response.body();

                            String userId = body.replace("OK", "")
                                    .replace("ОК", "").trim();

                            SharedPreferences sp = requireActivity()
                                    .getSharedPreferences("PC", Context.MODE_PRIVATE);
                            sp.edit().putString("TY", userId).apply();

                            MainActivity activity = (MainActivity) getActivity();
                                activity.showMainMenu();
                                activity.replaceFragment(new Home());
                        } else {
                            Toast.makeText(getContext(), "Ошибка входа", Toast.LENGTH_SHORT).show();
                        }
                        Log.d("LOGIN", "code=" + response.code());
                        Log.d("LOGIN", "error=" + response.errorBody());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        button.setEnabled(true);
                        Toast.makeText(getContext(), "Ошибка сети", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return view;
    }
}