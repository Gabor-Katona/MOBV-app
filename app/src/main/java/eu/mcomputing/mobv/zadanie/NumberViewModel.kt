package eu.mcomputing.mobv.zadanie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NumberViewModel : ViewModel() {
    private val _randomNumber = MutableLiveData<Int>()
    val randomNumber: LiveData<Int>
        get() = _randomNumber

    fun generateRandomNumber() {
        viewModelScope.launch {
            val number = fetchRandomNumber()
            _randomNumber.postValue(number)
        }
    }

    suspend fun fetchRandomNumber(): Int {
        delay(5000)
        return (0..10).random()
    }
}
