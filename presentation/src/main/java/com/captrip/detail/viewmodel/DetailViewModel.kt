package com.captrip.detail.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.captrip.domain.entities.TripItemDomain
import com.captrip.domain.usecases.DetailUseCase
import com.captrip.common.entities.Status
import com.captrip.detail.mapper.DetailHeaderMapper
import com.captrip.detail.mapper.DetailItemMapper
import com.captrip.detail.model.DetailHeaderItem
import com.captrip.detail.model.DetailItem
import kotlinx.coroutines.launch

class DetailViewModel(private val id: Int, private val useCase: DetailUseCase) : ViewModel() {

    private val _trip = MutableLiveData<DetailItem>()
    private val _header = MutableLiveData<DetailHeaderItem>()
    private val _state = MutableLiveData<Status>()

    val trip: LiveData<DetailItem>
        get() = _trip
    val header: LiveData<DetailHeaderItem>
        get() = _header
    val state: LiveData<Status>
        get() = _state

    fun fetchTrip() {
        if(_trip.value != null){
            return
        }

        viewModelScope.launch {
            try {
                _state.value = Status.LOADING
                val trip = useCase.getTrip(id)
                onFetchSuccess(trip)
            } catch (throwable: Throwable) {
                onFetchError(throwable)
            }
        }
    }

    private fun onFetchSuccess(detail: TripItemDomain) {
        _trip.value = DetailItemMapper()(detail)
        _header.value = DetailHeaderMapper()(detail)
        _state.value = Status.IDLE
    }

    private fun onFetchError(throwable: Throwable?) {
        Log.e("Error", throwable?.message)
        _state.value = Status.ERROR
    }
}