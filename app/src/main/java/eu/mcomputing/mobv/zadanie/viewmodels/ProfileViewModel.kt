package eu.mcomputing.mobv.zadanie.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.mcomputing.mobv.zadanie.data.api.DataRepository
import eu.mcomputing.mobv.zadanie.data.db.entities.GeofenceEntity
import eu.mcomputing.mobv.zadanie.data.model.User
import kotlinx.coroutines.launch

class ProfileViewModel(private val dataRepository: DataRepository) : ViewModel() {
    private val _profileResult = MutableLiveData<String>()
    val profileResult: LiveData<String>
        get() = _profileResult

    private val _userResult = MutableLiveData<User?>()
    val userResult: LiveData<User?>
        get() = _userResult

    //val sharingLocation = MutableLiveData<Boolean?>(null)

    val startHour = MutableLiveData<Int>()
    val startMinute = MutableLiveData<Int>()
    val startTime = MutableLiveData<String>("od:")

    val endHour = MutableLiveData<Int>()
    val endMinute = MutableLiveData<Int>()
    val endTime = MutableLiveData<String>("do:")

    fun loadUser(uid: String) {
        viewModelScope.launch {
            val result = dataRepository.apiGetUser(uid)

            _profileResult.postValue(result.first ?: "")
            _userResult.postValue(result.second)
        }
    }

    fun updateUserPhoto(photo: String) {
        if(_userResult.value != null ) {
            var user = User(
                _userResult.value!!.username,
                _userResult.value!!.email,
                _userResult.value!!.id,
                _userResult.value!!.access,
                _userResult.value!!.refresh,
                photo
            )
            _userResult.postValue(user)
        }
    }


    fun updateGeofence(lat: Double, lon: Double, radius: Double) {
        viewModelScope.launch {
            dataRepository.insertGeofence(GeofenceEntity(lat, lon, radius))
        }
    }

    fun removeGeofence() {
        viewModelScope.launch {
            dataRepository.removeGeofence()
        }
    }
}