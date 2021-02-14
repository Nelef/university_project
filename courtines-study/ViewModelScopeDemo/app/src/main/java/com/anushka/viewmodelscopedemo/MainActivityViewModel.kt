package com.anushka.viewmodelscopedemo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivityViewModel : ViewModel() {

    //private val myJob = Job() //뷰모델 스코프를 사용하면 이 주석친것들이 필요없어짐
//private val myScope = CoroutineScope(Dispatchers.IO + myJob)
    private var userRepository = UserRepository()
    var users: MutableLiveData<List<User>> = MutableLiveData()

    fun getUserData() {
        viewModelScope.launch {
            var result: List<User>? = null
            withContext(Dispatchers.IO) {
                result = userRepository.getUsers()
            }
            users.value = result
        }
//     myScope.launch {
//         //write some code
//     }


    }

//    override fun onCleared() {
//        super.onCleared()
//        myJob.cancel()
//    }


}