package com.sesac.mylocation.api

import com.google.gson.annotations.SerializedName

data class ToiletAPIInfo(
    @SerializedName("GeoInfoPublicToiletWGS")
    val geoInfoPublicToiletWGS: GeoInfoPublicToiletWGS
){
    data class GeoInfoPublicToiletWGS(
        @SerializedName("list_total_count")
        val listTotalCount: Int,
        @SerializedName("RESULT")
        val result: APIResult,
        @SerializedName("row")
        val row: List<ToiletInfo>,
    )

    data class APIResult(
        @SerializedName("CODE")
        val code: String,
        @SerializedName("MESSAGE")
        val message: String,
    )
}
