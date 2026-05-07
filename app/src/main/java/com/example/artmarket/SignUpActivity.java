package com.example.artmarket;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.artmarket.api.RetrofitClient;
import com.example.artmarket.model.Image;

import com.example.artmarket.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_sign_up, container, false);

        EditText email = view.findViewById(R.id.editEmail);
        EditText password = view.findViewById(R.id.editPassword);
        ConstraintLayout button = view.findViewById(R.id.regBotton);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean enabled = email.getText().length() > 0 &&
                        (password.getText().length() > 0 && password.getText().length() >= 6);

                button.setEnabled(enabled);
                button.setAlpha(enabled ? 1f : 0.5f);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();

                LoginRequest request = new LoginRequest(emailText, passwordText);
                RetrofitClient.getInstance().register(request).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d("REGISTER", "code=" + response.code());
                        Log.d("REGISTER", "body=" + response.body());
                        try {
                            Log.d("REGISTER", "error=" + response.errorBody().string());
                        } catch (Exception e) {}

                        if (response.isSuccessful()) {
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).replaceFragment(new regist());
                            }
                        } else {
                            Toast.makeText(getContext(), "Ошибка " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(getContext(), "Ошибка сети", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        email.addTextChangedListener(watcher);
        password.addTextChangedListener(watcher);

        button.setEnabled(false);
        button.setAlpha(0.5f);

        return view;
    }
}