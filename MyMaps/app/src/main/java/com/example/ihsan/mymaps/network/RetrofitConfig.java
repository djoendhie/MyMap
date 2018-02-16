

package com.example.ihsan.mymaps.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by FUADMASKA on 6/16/2017.
 */

public class RetrofitConfig {

    public static Retrofit getRetrofit(){
        return new Retrofit.Builder().baseUrl("https://maps.googleapis.com/maps/")
                .addConverterFactory(GsonConverterFactory.create()).build();

    }

    public static ApiService getInstanceRetrofit(){
        return RetrofitConfig.getRetrofit().create(ApiService.class);
    }

}
