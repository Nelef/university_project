package com.anushka.livedatademo

import androidx.lifecycle.*
import com.anushka.livedatademo.model.User
import com.anushka.livedatademo.model.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityViewModel : ViewModel()  {

    private var usersRepository= UserRepository()

    var users = liveData(Dispatchers.IO) {
        val result = usersRepository.getUsers()
        emit(result)
    }


   // var users: MutableLiveData<List<User>> = MutableLiveData()

//    fun getUsers() {
//        viewModelScope.launch {
//            var result: List<User>? = null
//            withContext(Dispatchers.IO) {
//                result = usersRepository.getUsers()
//            }
//            users.value = result
//        }
//    }
}