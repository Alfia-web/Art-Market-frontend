package com.example.artmarket.api;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

import java.util.List;
import com.example.artmarket.model.Image;
import com.example.artmarket.LoginRequest;

public interface ApiService {
    @GET("api/images")
    Call<List<Image>> getImages();

    @POST("api/users/login")
    Call<String> login(@Body LoginRequest request);

    @POST("api/users/register")
    Call<String> register(@Body LoginRequest request);

    @Multipart
    @POST("api/images/add")
    Call<Image> addImage(
            @Part MultipartBody.Part file,
            @Part("nameImage")RequestBody nameImage,
            @Part("author")RequestBody author,
            @Part("width")RequestBody width,
            @Part("height")RequestBody height,
            @Part("genre")RequestBody genres,
            @Part("userId")RequestBody userId);

    @GET("api/images/user/{userId}")
    Call<List<Image>> getImagesByUser(@Path("userId") Long userId);

    @DELETE("api/images/{id}")
    Call<Void> deleteImage(@Path("id") Long userId);

    @Multipart
    @PUT("api/images/{id}")
    Call<Image> updateImage(
            @Path("id") Long id, @Part MultipartBody.Part file,
            @Part("nameImage") RequestBody nameImage,
            @Part("author") RequestBody author,
            @Part("width") RequestBody width,
            @Part("height") RequestBody height,
            @Part("genre") RequestBody genres);
}

