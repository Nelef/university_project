package com.anushka.viewmodelscopedemo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anushka.viewmodelscopedemo.model.User
import com.anushka.viewmodelscopedemo.model.UserRepository
import kotlinx.coroutines.*


class MainActivityViewModel : ViewModel() {
    private var userRepository = UserRepository()
    var users : MutableLiveData<List<User>> = MutableLiveData()

 fun getUserData(){
     viewModelScope.launch {
         var result : List<User>? = null
         withContext(Dispatchers.IO){
             result = userRepository.getUsers()
         }
         users.value = result
     }
 }

}