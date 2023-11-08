package eu.mcomputing.mobv.zadanie.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import eu.mcomputing.mobv.zadanie.MyItem
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.data.api.DataRepository
import eu.mcomputing.mobv.zadanie.data.db.entities.UserEntity
import eu.mcomputing.mobv.zadanie.utils.Evento
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FeedViewModel(private val repository: DataRepository) : ViewModel() {

    val feed_items: LiveData<List<UserEntity>?> =
        liveData {
            loading.postValue(true)
            repository.apiListGeofence()
            loading.postValue(false)
            emitSource(repository.getUsers())
        }

    val loading = MutableLiveData(false)

    private val _message = MutableLiveData<Evento<String>>()
    val message: LiveData<Evento<String>>
        get() = _message

    fun updateItems() {
        viewModelScope.launch {
            loading.postValue(true)
            _message.postValue(Evento(repository.apiListGeofence()))
            loading.postValue(false)
        }
    }
}

/*suspend fun fetchRandomNumber(): List<MyItem> {
    delay(5000)
    val items = mutableListOf<MyItem>()
    for (i in 1..100) {

        items.add(MyItem(i, R.drawable.ic_action_map, "Text ${(0..100).random()}"))
    }
    return items
}*/