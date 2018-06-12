package com.fatkhun.travelia.service;


import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BaseApiService {

    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> loginRequest(@Field("email") String email,
                                    @Field("password") String password);


    @FormUrlEncoded
    @POST("register")
    Call<ResponseBody> registerRequest(@Field("username") String username,
                                       @Field("email") String email,
                                       @Field("password") String password);

    @FormUrlEncoded
    @POST("rating/store/")
    Call<ResponseBody> reviewRequest( @Field("api_token") String api_token,
                                      @Field("nama_wisata") String nama_wisata,
                                      @Field("nama") String nama,
                                      @Field("rating") float rating,
                                      @Field("review") String review);
    @GET("rating/destroy/{id}/")
    Call<ResponseBody> deteleRate(@Path("id") int id, @Query("api_token") String api_token);
}
