package com.sesac.mylocation.api

import retrofit2.Response
import retrofit2.http.GET

interface ToiletAPIService {

    @GET(TOILET_API_URL1)
    suspend fun getToiletResult1(): Response<ToiletAPIInfo>

    @GET(TOILET_API_URL2)
    suspend fun getToiletResult2(): Response<ToiletAPIInfo>

    @GET(TOILET_API_URL3)
    suspend fun getToiletResult3(): Response<ToiletAPIInfo>

    @GET(TOILET_API_URL4)
    suspend fun getToiletResult4(): Response<ToiletAPIInfo>

    @GET(TOILET_API_URL5)
    suspend fun getToiletResult5(): Response<ToiletAPIInfo>

}