package com.fatkhun.travelia.Utils;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class NetworkUtils {
    /**
     * Fetch data from internet.
     *
     * @param url     The URL to fetch data from.
     * @param service The class where the data should be get.
     * @return Object
     */
    public static Object fetchUrl(String url, Class service) {

        // Build http client.
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

        // Build retrofit object with the given url.
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder().baseUrl(url)

                // Add adapter call factory to response
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

                // Add converter factory with scalars converter to convert the data
                // get from given url to plain String.
                .addConverterFactory(ScalarsConverterFactory.create());

        // Instantiate retrofit object from the builder.
        Retrofit retrofit = retrofitBuilder.client(okHttpClientBuilder.build()).build();

        // Return object.
        return retrofit.create(service);
    }
}
