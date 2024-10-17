package com.sesac.mylocation.ui.vm

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sesac.mylocation.api.ToiletInfo
import com.sesac.mylocation.repo.ToiletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TMapViewModel: ViewModel() {

    private val repository = ToiletRepository()

    private val _toiletList = MutableStateFlow(listOf<ToiletInfo>())
    val toiletList: StateFlow<List<ToiletInfo>> = _toiletList

    fun getToiletList(currentLocation: Location){
        viewModelScope.launch {
            repository.getToiletList(currentLocation).collectLatest {
                _toiletList.emit(it)
            }
        }
    }

}