package com.anushka.coroutinesdemo1

import kotlinx.coroutines.*

class UserDataManager {
    suspend fun getTotalUserCount(): Int {
        var count = 0
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000)
            count = 50
        }
        val deferred = CoroutineScope(Dispatchers.IO).async {
            delay(1000)
            return@async 70
        }
        return count + deferred.await()
    }

}