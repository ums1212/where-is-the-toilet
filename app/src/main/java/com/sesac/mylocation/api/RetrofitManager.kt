package com.sesac.mylocation.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitManager {
    object ToiletService{
        var toiletService: ToiletAPIService = Retrofit.Builder()
            .baseUrl(TOILET_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ToiletAPIService::class.java)
    }
}