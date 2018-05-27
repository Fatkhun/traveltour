package com.fatkhun.travelia.service;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TourWisataService {
    // Get data from paket wisata endpoint.
    @GET("/paket/index")

    // Method to call paket wisata above.
    Call<String> getTourWisata();
}
