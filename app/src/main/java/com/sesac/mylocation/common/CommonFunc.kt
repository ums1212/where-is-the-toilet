package com.sesac.mylocation.common

import android.location.Location
import com.sesac.mylocation.api.ToiletInfo

fun getDistance(currentLocation: Location, toilet: ToiletInfo): Boolean {
    val toiletLocation = Location("toiletLocation")
    toiletLocation.latitude = toilet.lat
    toiletLocation.longitude = toilet.lng
    return currentLocation.distanceTo(toiletLocation)<=500
}