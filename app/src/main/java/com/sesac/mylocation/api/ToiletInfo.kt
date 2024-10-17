package com.sesac.mylocation.api

import com.google.gson.annotations.SerializedName

data class ToiletInfo(
    @SerializedName("OBJECTID")
    val objectID: String,
    @SerializedName("LAT")
    val lat: Double,
    @SerializedName("LNG")
    val lng: Double
)
