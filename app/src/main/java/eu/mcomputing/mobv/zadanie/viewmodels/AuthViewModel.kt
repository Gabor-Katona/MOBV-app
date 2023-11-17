package eu.mcomputing.mobv.zadanie.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.mcomputing.mobv.zadanie.data.api.DataRepository
import eu.mcomputing.mobv.zadanie.data.model.User
import kotlinx.coroutines.launch

class AuthViewModel(private val dataRepository: DataRepository) : ViewModel() {
    private val _registrationResult = MutableLiveData<String>()

    val registrationResult: LiveData<String>
        get() = _registrationResult

    private val _loginResult = MutableLiveData<String>()
    val loginResult: LiveData<String>
        get() = _loginResult

    private val _userResult = MutableLiveData<User?>()
    val userResult: LiveData<User?>
        get() = _userResult

    val username = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val repeat_password = MutableLiveData<String>()

    fun registerUser() {
        viewModelScope.launch {
            val result = dataRepository.apiRegisterUser(
                username.value ?: "",
                email.value ?: "",
                password.value ?: ""
            )

            _registrationResult.postValue(result.first ?: "")
            _userResult.postValue(result.second)
        }
    }

    fun loginUser() {
        viewModelScope.launch {
            val result = dataRepository.apiLoginUser(username.value ?: "", password.value ?: "")

            //Log.d("TAG2", result.second.toString())
            _loginResult.postValue(result.first ?: "")
            _userResult.postValue(result.second)
        }
    }

    fun clearUserResult() {
        _userResult.value = null
        username.postValue("")
        email.postValue("")
        password.postValue("")
    }
}