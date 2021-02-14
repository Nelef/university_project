package com.anushka.viewmodelscopedemo

import kotlinx.coroutines.delay

class UserRepository {
    suspend fun getUsers(): List<User> {
        delay(8000)
        val users = listOf<User>(
            User(1, "a"),
            User(2, "b"),
            User(3, "c"),
            User(4, "d")
        )
        return users
    }
}