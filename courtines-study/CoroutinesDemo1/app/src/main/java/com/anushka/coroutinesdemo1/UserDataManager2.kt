package com.anushka.coroutinesdemo1

import kotlinx.coroutines.*

class UserDataManager2 {
    var count = 0
    lateinit var deferred: Deferred<Int>
    suspend fun getTotalUserCount(): Int {
        coroutineScope { //대문자 C가 아닌 이 소문자 c로 시작하는 코루틴스코프는 suspending function이기 때문에 child scope를 가질 수 있다. (2개 이상의 코루틴을 갖고 있다면 이것을 사용해야 원하는 바를 얻을 수 있을거다.)
            launch(Dispatchers.IO) {
                delay(1000)
                count = 50
            }
            deferred = async(Dispatchers.IO) {
                delay(1000)
                return@async 70
            }
        }

        return count + deferred.await() // 50 + 70
    }

}