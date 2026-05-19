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
import retrofit2.http.Query;

import java.math.BigDecimal;
import java.util.List;

import com.example.artmarket.model.Auction;
import com.example.artmarket.model.Comment;
import com.example.artmarket.model.Favorite;
import com.example.artmarket.model.Image;
import com.example.artmarket.model.User;

public interface ApiService {
    @GET("api/images")
    Call<List<Image>> getImages();

    @POST("api/users/register")
    Call<String> register(@Body User user);

    @POST("api/users/login")
    Call<String> login(@Body User user);

    @Multipart
    @POST("api/images/add")
    Call<Image> addImage(
            @Part MultipartBody.Part file,
            @Part("nameImage")RequestBody nameImage,
            @Part("author")RequestBody author,
            @Part("width")RequestBody width,
            @Part("height")RequestBody height,
            @Part("genre")RequestBody genres,
            @Part("userId")RequestBody userId,
            @Part("startPrice")RequestBody startPrice,
            @Part("startTime")RequestBody startTime,
            @Part("endTime") RequestBody endTime);

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
            @Part("genre") RequestBody genres,
            @Part("startPrice")RequestBody startPrice,
            @Part("startTime")RequestBody startTime,
            @Part("endTime") RequestBody endTime);

    @GET("api/auctions/image/{imageId}")
    Call<Auction> getAuction(@Path("imageId") Long imageId);

    @POST("api/auctions/rate")
    Call<Auction> addRate(@Query("auctionId") Long auctionId, @Query("userId") Long userId,
                                   @Query("amount") String amount);

    @POST("api/favorites/toggle")
    Call<Favorite> toggle(@Query("userId") Long userId, @Query("imageId") Long imageId);

    @GET("api/favorites/check")
    Call<Favorite> checkFavorite(@Query("userId") Long userId, @Query("imageId") Long imageId);

    @GET("api/favorites/user/{userId}")
    Call<List<Image>> getFavorites(@Path("userId") Long userId);

    @GET("api/comments/image/{imageId}")
    Call<List<Comment>> getComments(@Path("imageId") Long imageId);

    @POST("api/comments")
    Call<Comment> addComment(@Query("imageId") Long imageId, @Query("userId") Long userId,
                             @Query("username") String username, @Query("text") String text);

    @DELETE("api/comments/{commentId}")
    Call<Void> deleteComment(@Path("commentId") Long commentId, @Query("userId") Long userId);

    @GET("api/images/owner/{imageId}")
    Call<Long> getOwnerId(@Path("imageId") Long imageId);

    @GET("api/users/{userId}/balance")
    Call<BigDecimal> getBalance(@Path("userId") Long userId);

    @POST("api/users/{userId}/addMoney")
    Call<BigDecimal> addMoney(@Path("userId") Long userId, @Query("amount") String amount);
}

