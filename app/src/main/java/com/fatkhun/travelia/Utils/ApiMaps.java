package com.fatkhun.travelia.Utils;

import com.fatkhun.travelia.service.BaseApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiMaps {
    public static String BASE_URL = "https://maps.googleapis.com/maps/api/directions/";
    public static Retrofit setInit(){
        return new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static BaseApiService getInstance(){
        return setInit().create(BaseApiService.class);
    }
}
