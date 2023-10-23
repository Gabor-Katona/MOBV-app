package eu.mcomputing.mobv.zadanie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FeedViewModel : ViewModel() {
    private val _feed_items = MutableLiveData<List<MyItem>>()
    val feed_items: LiveData<List<MyItem>>
        get() = _feed_items

    fun updateItems() {
        viewModelScope.launch {
            val new_items = fetchRandomNumber()
            _feed_items.postValue(new_items)
        }
    }
}

suspend fun fetchRandomNumber(): List<MyItem> {
    delay(5000)
    val items = mutableListOf<MyItem>()
    for (i in 1..100) {

        items.add(MyItem(i, R.drawable.ic_action_map, "Text ${(0..100).random()}"))
    }
    return items
}