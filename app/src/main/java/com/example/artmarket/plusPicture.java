package com.example.artmarket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.artmarket.api.RetrofitClient;
import com.example.artmarket.model.Image;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class plusPicture extends Fragment {
    private Uri selectedImageUri = null;
    private ImageView imagePreview;
    private Image editingImage = null;
    private TextInputEditText editWidth;
    private TextInputEditText editHeight;

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

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    public plusPicture() {}

    public static plusPicture newInstanceForEdit(Image image) {
        plusPicture fragment = new plusPicture();
        Bundle args = new Bundle();

        args.putLong("edit_id", image.getId());
        args.putString("edit_name", image.getNameImage());
        args.putString("edit_author", image.getAuthor());
        args.putString("edit_width", String.valueOf(image.getWidth()));
        args.putString("edit_height", String.valueOf(image.getHeight()));
        args.putString("edit_genre", image.getGenres());
        args.putString("edit_path", image.getPathImage());

        fragment.setArguments(args);
        return fragment;
    }

    private byte[] readBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int nRead;

        while ((nRead = is.read(data)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    private void submitImage(Uri uri, String name, String author, String widthStr,
                             String heightStr, String genre) {
        try  {
            byte[] bytes;
            if (uri != null) {
                InputStream is = requireContext().getContentResolver().openInputStream(uri);
                bytes = readBytes(is);
            } else {
                bytes = new byte[0];
            }

            MultipartBody.Part filePart = MultipartBody.Part.createFormData
                    ("file", "upload.jpg", RequestBody.create(MediaType.parse("image/*"), bytes));

            RequestBody rbName = RequestBody.create(MediaType.parse("text/plain"), name);
            RequestBody rbAuthor = RequestBody.create(MediaType.parse("text/plain"), author);
            RequestBody rbWidth = RequestBody.create(MediaType.parse("text/plain"), widthStr);
            RequestBody rbHeight = RequestBody.create(MediaType.parse("text/plain"), heightStr);
            RequestBody rbGenre = RequestBody.create(MediaType.parse("text/plain"), genre);

            SharedPreferences sp =
                    requireContext().getSharedPreferences("PC", Context.MODE_PRIVATE);
            String userId = sp.getString("TY", "-1");

            RequestBody rbUserId =
                    RequestBody.create(MediaType.parse("text/plain"), userId);

            RetrofitClient.getInstance().addImage(filePart, rbName, rbAuthor,
                            rbWidth, rbHeight, rbGenre, rbUserId)
                    .enqueue(new retrofit2.Callback<Image>() {
                        @Override
                        public void onResponse(retrofit2.Call<Image> call,
                                               retrofit2.Response<Image> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(requireContext(), "Добавлено!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), "Ошибка: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<Image> call, Throwable t) {
                            Toast.makeText(requireContext(), "Ошибка сети", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (IOException e) {
            Toast.makeText(requireContext(), "Ошибка файла", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateImage(Long id, Uri uri, String name, String author,
                             String widthStr, String heightStr, String genre) {
        try {
            byte[] bytes;
            if (uri != null) {
                InputStream is = requireContext().getContentResolver().openInputStream(uri);
                bytes = readBytes(is);
            } else {
                bytes = new byte[0];
            }

            RequestBody fileBody =
                    RequestBody.create(MediaType.parse("image/*"), bytes);

            MultipartBody.Part filePart = MultipartBody.Part.createFormData
                    ("file", "upload.jpg", RequestBody.create(MediaType.parse("image/*"), bytes));

            RequestBody rbName = RequestBody.create(MediaType.parse("text/plain"), name);
            RequestBody rbAuthor = RequestBody.create(MediaType.parse("text/plain"), author);
            RequestBody rbWidth = RequestBody.create(MediaType.parse("text/plain"), widthStr);
            RequestBody rbHeight = RequestBody.create(MediaType.parse("text/plain"), heightStr);
            RequestBody rbGenre = RequestBody.create(MediaType.parse("text/plain"), genre);

            RetrofitClient.getInstance()
                    .updateImage(id, filePart, rbName, rbAuthor, rbWidth, rbHeight, rbGenre)
                    .enqueue(new retrofit2.Callback<Image>() {
                        @Override
                        public void onResponse(retrofit2.Call<Image> call,
                                               retrofit2.Response<Image> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(requireContext(), "Изменено!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), "Ошибка: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<Image> call, Throwable t) {
                            if (!isAdded()) return;
                            Toast.makeText(requireContext(), "Ошибка сети", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Ошибка файла", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        imagePreview.setImageURI(selectedImageUri);

                        try(InputStream is = requireContext().getContentResolver().
                                openInputStream(selectedImageUri)) {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            BitmapFactory.decodeStream(is, null, options);

                            if (editWidth != null) editWidth.setText(String.valueOf(options.outWidth));
                            if (editHeight != null) editHeight.setText(String.valueOf(options.outHeight));
                        }
                        catch(IOException e){
                            Toast.makeText(requireContext(), "Ошибка файла", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pluspicture, container, false);

        imagePreview = view.findViewById(R.id.imagePreview);

        TextInputEditText editName = view.findViewById(R.id.editNameImage);
        TextInputEditText editAuthor = view.findViewById(R.id.editAuthor);
        Spinner spinnerGenres = view.findViewById(R.id.spinnerGenres);
        editWidth = view.findViewById(R.id.editWidth);
        editHeight = view.findViewById(R.id.editHeight);
        View btnPickImage = view.findViewById(R.id.btnPickImage);
        Button btnSubmit = view.findViewById(R.id.btnSubmit);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, genres);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenres.setAdapter(adapter);

        Bundle args = getArguments();

        if (args != null && args.containsKey("edit_id")) {
            btnSubmit.setText("Изменить картину");
            editingImage = new Image();
            editingImage.setId(args.getLong("edit_id"));
            editName.setText(args.getString("edit_name"));
            editAuthor.setText(args.getString("edit_author"));
            editWidth.setText(args.getString("edit_width"));
            editHeight.setText(args.getString("edit_height"));
            String genre = args.getString("edit_genre", "");

            for (int i = 0; i < genresValues.length; i++) {
                if (genresValues[i].equals(genre)) {
                    spinnerGenres.setSelection(i);
                    break;
                }
            }

            String path = args.getString("edit_path", "");

            Glide.with(requireContext())
                    .load("http://10.0.2.2:8080" + path)
                    .into(imagePreview);
        }

        btnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(intent);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = editName.getText() != null ? editName.getText().toString().trim() : "";
                String author = editAuthor.getText() != null ? editAuthor.getText().toString().trim() : "";
                String heightStr = editHeight.getText() != null ? editHeight.getText().toString().trim() : "";
                String widthStr = editWidth.getText() != null ? editWidth.getText().toString().trim() : "";

                String genre = genresValues[spinnerGenres.getSelectedItemPosition()];

                if (name.isEmpty() || author.isEmpty() || heightStr.isEmpty() || widthStr.isEmpty()) {
                    Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (editingImage == null && selectedImageUri == null) {
                    Toast.makeText(requireContext(), "Выберите фото", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (editingImage != null) {
                    updateImage(editingImage.getId(), selectedImageUri, name, author,
                            widthStr, heightStr, genre);
                } else {
                    submitImage(selectedImageUri, name, author, widthStr, heightStr,
                            genre);
                }
            }
        });

        return view;
    }


}