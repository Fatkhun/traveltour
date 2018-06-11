package com.fatkhun.travelia.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RatingService {
    // Get data from paket wisata endpoint.
    @GET("rating/index/")
    // Method to call paket wisata above.
    Call<String> getRateWisata(@Query("api_token") String api_token);
}
