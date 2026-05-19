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

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.artmarket.api.RetrofitClient;
import com.example.artmarket.model.Image;

import com.example.artmarket.R;
import com.example.artmarket.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_sign_up, container, false);

        EditText email = view.findViewById(R.id.editEmail);
        EditText password = view.findViewById(R.id.editPassword);
        TextView button = view.findViewById(R.id.regBotton);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_actionbar);
        toolbar.setVisibility(View.GONE);

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

        email.addTextChangedListener(watcher);
        password.addTextChangedListener(watcher);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();
                User user = new User(emailText, passwordText);
                RetrofitClient.getInstance().register(user).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d("REGISTER", "code=" + response.code());
                        Log.d("REGISTER", "body=" + response.body());

                        if (response.isSuccessful()) {
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).replaceFragment(new regist());
                            }
                        } else {
                            String error = "";
                            if (response.errorBody() != null) {
                                try {
                                    error = response.errorBody().string();
                                } catch (Exception ignored) {
                                }
                            }

                            if (error.contains("Email уже существует")) {
                                Toast.makeText(getContext(),
                                        "Этот login уже зарегистрирован",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(),
                                        "Ошибка регистрации",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(getContext(), "Ошибка сети", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        button.setEnabled(false);
        button.setAlpha(0.5f);

        return view;
    }
}