package com.trevin.myplaylists.data

import com.trevin.myplaylists.data.database.PlaylistDatabase
import com.trevin.myplaylists.data.database.UserDao
import com.trevin.myplaylists.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepo private constructor(private val userDao: UserDao) {
    suspend fun register(fullName: String, userName: String, password: String): Long = withContext(
        Dispatchers.IO
    ) {
        userDao.insertUser(User(fullName = fullName, userName = userName, password = password))
    }

    suspend fun login(userName: String, password: String): User? = withContext(Dispatchers.IO) {
        userDao.authenticate(username = userName, password = password)
    }

    companion object {
        @Volatile
        private var INSTANCE: UserRepo? = null

        fun getInstance(database: PlaylistDatabase): UserRepo = INSTANCE ?: synchronized(this) {
            INSTANCE ?: UserRepo(database.userDao()).also { INSTANCE = it }
        }

    }

}