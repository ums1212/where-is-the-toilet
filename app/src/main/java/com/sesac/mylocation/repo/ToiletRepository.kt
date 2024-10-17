package com.sesac.mylocation.repo

import android.location.Location
import com.sesac.mylocation.api.RetrofitManager
import com.sesac.mylocation.api.ToiletInfo
import com.sesac.mylocation.common.getDistance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ToiletRepository {

    fun getToiletList(currentLocation: Location): Flow<List<ToiletInfo>> = flow {
        val list = mutableListOf<ToiletInfo>()

        val result = RetrofitManager.ToiletService.toiletService.getToiletResult1()
        result.let {
            if (it.isSuccessful) {
                it.body()?.geoInfoPublicToiletWGS?.row?.forEach { toilet ->
                    if (getDistance(currentLocation, toilet)) {
                        list.add(toilet)
                    }
                }
            }
        }
        val result2 = RetrofitManager.ToiletService.toiletService.getToiletResult2()
        result2.let {
            if (it.isSuccessful) {
                it.body()?.geoInfoPublicToiletWGS?.row?.forEach { toilet ->
                    if (getDistance(currentLocation, toilet)) {
                        list.add(toilet)
                    }
                }
            }
        }
        val result3 = RetrofitManager.ToiletService.toiletService.getToiletResult3()
        result3.let {
            if (it.isSuccessful) {
                it.body()?.geoInfoPublicToiletWGS?.row?.forEach { toilet ->
                    if (getDistance(currentLocation, toilet)) {
                        list.add(toilet)
                    }
                }
            }
        }
        val result4 = RetrofitManager.ToiletService.toiletService.getToiletResult4()
        result4.let {
            if (it.isSuccessful) {
                it.body()?.geoInfoPublicToiletWGS?.row?.forEach { toilet ->
                    if (getDistance(currentLocation, toilet)) {
                        list.add(toilet)
                    }
                }
            }
        }
        val result5 = RetrofitManager.ToiletService.toiletService.getToiletResult5()
        result5.let {
            if (it.isSuccessful) {
                it.body()?.geoInfoPublicToiletWGS?.row?.forEach { toilet ->
                    if (getDistance(currentLocation, toilet)) {
                        list.add(toilet)
                    }
                }
            }
        }

        emit(list)
    }

}